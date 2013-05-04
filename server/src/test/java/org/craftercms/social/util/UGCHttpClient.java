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
package org.craftercms.social.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;


public class UGCHttpClient {
	
	private String scheme = "http";
	private int port;
	private String host = "localhost";
	private String appPath = "/crafter-social";
	
	public UGCHttpClient(String scheme, int port, String host, String appPath) {
		this.scheme = scheme;
		this.port = port;
		this.host = host;
		this.appPath = appPath;
	}
	
	public HttpResponse addPost(String ticket, String target, String parentId, String textContent, String[] fileAttachments) throws URISyntaxException, ClientProtocolException, IOException {
		HttpPost httppost = null;
		if (fileAttachments==null) {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("ticket", ticket));
			if (target!=null)
				qparams.add(new BasicNameValuePair("target", target));
			else if (parentId!=null) {
				qparams.add(new BasicNameValuePair("parentId", parentId));
			}
			qparams.add(new BasicNameValuePair("textContent", textContent));
			
			
			URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/" + "create.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			httppost = new HttpPost(uri);
		} else {
			//if (fileAttachments!=null) {
				httppost = manageAttachments(fileAttachments,ticket,target,textContent);
			//}
		}

		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httppost);
		
	}
	
	private HttpPost manageAttachments(String[] attachments,String ticket,String target, String textContent) throws UnsupportedEncodingException, URISyntaxException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/" + "create.json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpPost httppost = new HttpPost(uri);
		
		if (attachments.length > 0) {
			
			MultipartEntity me = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			File file;
			FileBody fileBody;
	        for(String f:attachments) {
	        	file = new File(f);
	        	fileBody = new FileBody(file, "application/octect-stream") ;
	        	me.addPart("attachments", fileBody) ;
	        }
			//File file = new File(attachments[0]);
	        //FileBody fileBody = new FileBody(file, "application/octect-stream") ;
	        //me.addPart("attachments", fileBody) ;
	        me.addPart("target", new StringBody(target)) ;
	        me.addPart("textContent", new StringBody(textContent)) ;
	        
	
	        httppost.setEntity(me) ;
		}
		return httppost;
	}
	
	public HttpResponse like(String ticket, String ugcId) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/like/"+ugcId + ".json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpPost httppost = new HttpPost(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httppost);
	}
	
	public HttpResponse dislike(String ticket, String ugcId) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/dislike/"+ugcId + ".json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpPost httppost = new HttpPost(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httppost);
	}
	
	public HttpResponse flag(String ticket, String ugcId, String reason) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		qparams.add(new BasicNameValuePair("reason", reason));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/flag/"+ugcId + ".json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpPost httppost = new HttpPost(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httppost);
	}
	
	public HttpResponse getAudit(String ticket, String ugcId) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/audit/"+ ugcId + ".json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpGet httpget = new HttpGet(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httpget);
	}

	public HttpResponse updateModerationStatus(String ticket, String ugcId, String moderationStatus) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/moderation/" + ugcId +"/status.json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(uri);
		StringEntity bodyEntity = new StringEntity(moderationStatus.toUpperCase(), "text/xml",HTTP.DEFAULT_CONTENT_CHARSET);
		httppost.setEntity(bodyEntity);
		
		return httpclient.execute(httppost);
		
	}
	
	public HttpResponse getTarget(String ticket, String target) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		qparams.add(new BasicNameValuePair("target", target));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/target.json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpGet httpget = new HttpGet(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httpget);
	}
	
	public HttpResponse getModerationStatus(String ticket, String moderationStatus) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/moderation/" + moderationStatus + ".json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpGet httpget = new HttpGet(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httpget);
	}
	
	public HttpResponse getModerationStatusTarget(String ticket, String moderationStatus, String target) throws URISyntaxException, ClientProtocolException, IOException {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		qparams.add(new BasicNameValuePair("target", target));
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/moderation/" + moderationStatus + "/target.json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpGet httpget = new HttpGet(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httpget);
	}
	
	public HttpResponse getThread(String ticket, String ugcId) throws Exception {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		
		URI uri = URIUtils.createURI(scheme, host, port, appPath + "/api/2/ugc/thread/" + ugcId + ".json",
				URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
		HttpGet httpget = new HttpGet(uri);
		HttpClient httpclient = new DefaultHttpClient();
		return httpclient.execute(httpget);
	}
	
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	
	public static void main(String[] args) {
		UGCHttpClient client = new UGCHttpClient("http",8080,"localhost","crafter-social");
	}

}
