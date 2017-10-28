/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.moderation.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.craftercms.social.domain.social.SocialUgc;
import org.craftercms.social.moderation.ModerationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Uses File to get Blacklist word
 * 
 * @author cortiz
 * 
 */
public class BlackListModeration implements ModerationFilter {

	private List<Pattern> registerPatterns;
	private List<String> registerWords;
	private Logger log = LoggerFactory.getLogger(BlackListModeration.class);

	public BlackListModeration(String blackListFile) throws SAXException,
			IOException, ParserConfigurationException, XPathException {
		loadBlackList(blackListFile);
		registerPatterns = new ArrayList<Pattern>();
		registerWords = new ArrayList<String>();
	}

	@Override
	public boolean needModeration(SocialUgc ugc) {
		boolean needsModeration = false;
		if (testRegex(ugc.getBody())) {
			needsModeration = true;
		} else if (testWord(ugc.getBody())) {
			needsModeration = true;
		}
		return needsModeration;
	}

	private boolean testRegex(String textContent) {
		boolean matchRegex = false;
		for (Pattern pattern : registerPatterns) {
			Matcher matcher = pattern.matcher(textContent);
			if (matcher.find()) {
				log.debug("{} matches blacklist word {1}", textContent,
						pattern.toString());
				matchRegex = true;
				break;
			}
		}
		return matchRegex;
	}

	private boolean testWord(String textContent) {
		boolean matchWord = false;
		for (String word : registerWords) {
			if (textContent.toLowerCase().contains(word)) {
				log.debug("{} matches blacklist word {1}", textContent, word);
				matchWord = true;
				break;
			}
		}
		return matchWord;
	}

	@Override
	public String getName() {
		return "BlackList Moderation";
	}

	private void loadBlackList(String blackListFile) throws SAXException,
			IOException, ParserConfigurationException, XPathException {
		InputStream xmlFile=null;
		if(blackListFile.startsWith("classpath:")){
			xmlFile=BlackListModeration.class.getResourceAsStream(blackListFile.split("classpath:")[1]);
		}else{
			xmlFile=new FileInputStream(blackListFile); 
		}
		log.debug("Loading File {} as blacklist file", blackListFile);
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);
		Document doc = dbf.newDocumentBuilder().parse(xmlFile);
		addRegexRules(doc);
		addWordRules(doc);
		doc=null;
		xmlFile.close();
	}

	private void addWordRules(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//word");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null) {
			if (result instanceof NodeList) {
				NodeList nodes = (NodeList) result;
				log.debug("Found {} blacklist words", nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					if (nodes.item(i).getNodeType() == Node.TEXT_NODE) {
						String word = nodes.item(i).getTextContent()
								.toLowerCase();
						registerWords.add(word);
						log.debug("Added {} as a blacklist word", word);
					}
				}
			}
		}
		xpath = null;
		expr = null;
		result = null;
	}

	private void addRegexRules(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//pattern");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		if (result != null) {
			if (result instanceof NodeList) {
				NodeList nodes = (NodeList) result;
				log.debug("Found {} blacklist patterns", nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					if (nodes.item(i).getNodeType() == Node.TEXT_NODE) {
						addRegexRule(nodes.item(i).getTextContent());
					}
				}
			}
		}
		xpath = null;
		expr = null;
		result = null;
	}

	private void addRegexRule(String rule) {
		try {
			registerPatterns.add(Pattern.compile(rule));
			log.debug("Added {} as a blacklist rule");
		} catch (PatternSyntaxException e) {
			log.error("Unable to register {} as a blacklist pattern", rule);
		}
	}

}
