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
package org.craftercms.social;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import org.craftercms.profile.api.ProfileClient;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Tenant;
import org.craftercms.profile.exceptions.AppAuthenticationFailedException;
import org.craftercms.profile.exceptions.UserAuthenticationFailedException;
import org.craftercms.profile.impl.ProfileRestClientImpl;



import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.UGC.ModerationStatus;
import org.craftercms.social.util.CrafterSocialConstants;
import org.craftercms.social.util.UGCHttpClient;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class UCGRestServicesTest extends TestCase {
	
	private String scheme = "http";
	private int port;
	private String host = "localhost";
	private String appPath = "/crafter-social";
	
	private String profileUsername;
	private String profilePassword;
	private String profileAppPassword;
	private String profileAppUsername;
	private String target;
	private String postContent;
	private String attachmentFile;
	private String[] attachmenList;
	private UGCHttpClient ugcClient;
	private final TypeReference<List<UGC>> UGC_LIST_TYPE = new TypeReference<List<UGC>>() { };
	private final TypeReference<List<UGCAudit>> UGC_AUDIT_LIST_TYPE = new TypeReference<List<UGCAudit>>() { };
	
	ProfileRestClientImpl profileRestClient = new ProfileRestClientImpl();
	private HttpClient httpclient = new DefaultHttpClient();
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private Properties properties;
	private String tenantName;
	private String appToken;
	
	@Override
    protected void setUp() throws Exception {
		super.setUp();
		properties = new Properties();
        
        properties.load(UCGRestServicesTest.class.getClassLoader().getResourceAsStream("social-server.properties"));
        this.appPath = (String)properties.get(CrafterSocialConstants.APP_PATH);
        this.host = (String)properties.get(CrafterSocialConstants.HOST);
        this.port = Integer.parseInt((String)properties.get(CrafterSocialConstants.PORT));
        this.profileAppPassword = (String)properties.get(CrafterSocialConstants.PROFILE_APP_PASSWORD);
        this.profileAppUsername = (String)properties.get(CrafterSocialConstants.PROFILE_APP_USERNAME);
        this.profilePassword = (String)properties.get(CrafterSocialConstants.PROFILE_PASSWORD);
        this.profileUsername = (String)properties.get(CrafterSocialConstants.PROFILE_USERNAME);
        this.tenantName = (String)properties.get(CrafterSocialConstants.TENANT_NAME);
        this.ugcClient = new UGCHttpClient(scheme,port,host,appPath);
        String attachments = (String)properties.get(CrafterSocialConstants.ATTACHMENT_FILE);
        this.attachmenList = attachments.split(";");
        
        this.target = (String)properties.get(CrafterSocialConstants.TARGET);
        this.postContent = (String)properties.get(CrafterSocialConstants.POST_CONTENT);
        profileRestClient.setPort(port);
    }
	
//	public void testAddPost() throws Exception {
//		
//		HttpEntity entity = null;
//		try {
//
//			String ticket = initProfileSecurity();
//			
//			HttpResponse response = ugcClient.addPost(ticket, this.target,null, postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				UGC o = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				System.out.println(o.getTextContent());
//				assertEquals(this.target, o.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//		
// 
//		} finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
//	
//	public void testAddPostWithAttachments() throws Exception {
//		
//		HttpEntity entity = null;
//		try {
//
//			String ticket = initProfileSecurity();
//			
//			//HttpResponse response = ugcClient.addPost(ticket, this.target,null, postContent,new String[]{this.attachmentFile});
//			HttpResponse response = ugcClient.addPost(ticket, this.target,null, postContent,this.attachmenList);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				UGC o = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				System.out.println(o.getTextContent());
//				assertEquals(this.target, o.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//		
// 
//		} finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
//	
//	public void testLike() throws Exception {
//		
//		HttpEntity entity = null;
//		try {
//
//			String ticket = initProfileSecurity();
//			UGC ugc = null;
//			
//			HttpResponse response = this.ugcClient.addPost(ticket, this.target, null, postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			assertNotNull(ugc);
//			response = this.ugcClient.like(ticket, ugc.getId().toString());
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				assertTrue(ugc.getLikeCount()==1);
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			assertNotNull(ugc);
//			response = this.ugcClient.getAudit(ticket, ugc.getId().toString());
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				ObjectMapper mapper = new ObjectMapper();
//				mapper.getDeserializationConfig().addMixInAnnotations(UGCAuditList.class, MixIn.class);
//				UGCAuditList al = mapper.readValue(entity.getContent(), UGCAuditList.class);
//				assertTrue(al.getUGCAuditList().size()>0);
//				assertTrue(al.getUGCAuditList().get(0).getUgcId().toString().equals(ugc.getId().toString()));
//				assertEquals(this.target, ugc.getTargetId());
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//		}
//		finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
//	
//	public void testDislike() throws Exception {
//		
//		HttpEntity entity = null;
//		try {
//
//			String ticket = initProfileSecurity();
//			UGC ugc = null;
//			HttpResponse response = this.ugcClient.addPost(ticket, this.target, null, postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			assertNotNull(ugc);
//			response = this.ugcClient.dislike(ticket, ugc.getId().toString());
//			
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				assertTrue(ugc.getOffenceCount()==1);
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			response = this.ugcClient.getAudit(ticket, ugc.getId().toString());
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				ObjectMapper mapper = new ObjectMapper();
//				mapper.getDeserializationConfig().addMixInAnnotations(UGCAuditList.class, MixIn.class);
//				UGCAuditList al = mapper.readValue(entity.getContent(), UGCAuditList.class);
//				assertTrue(al.getUGCAuditList().size()>0);
//				assertTrue(al.getUGCAuditList().get(0).getUgcId().toString().equals(ugc.getId().toString()));
//				assertEquals(this.target, ugc.getTargetId());
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//		}
//		finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
//	
//	public void testFlag() throws Exception {
//		
//		HttpEntity entity = null;
//		try {
//
//			String ticket = initProfileSecurity();
//			UGC ugc = null;
//			HttpResponse response = this.ugcClient.addPost(ticket, this.target, null, postContent,null);
//			
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				System.out.println(ugc.getTextContent());
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			assertNotNull(ugc);
//			response = this.ugcClient.flag(ticket, ugc.getId().toString(), "Testing reasons");
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				assertTrue(ugc.getFlagCount()==1);//TODO there's a bug to fix
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			response = this.ugcClient.getAudit(ticket, ugc.getId().toString());
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				ObjectMapper mapper = new ObjectMapper();
//				mapper.getDeserializationConfig().addMixInAnnotations(UGCAuditList.class, MixIn.class);
//				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				UGCAuditList al = mapper.readValue(entity.getContent(), UGCAuditList.class);
//				assertTrue(al.getUGCAuditList().size()>0);
//				assertTrue(al.getUGCAuditList().get(0).getUgcId().toString().equals(ugc.getId().toString()));
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//		}
//		finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
	
	
//	public void testGetTarget() throws UserAuthenticationFailedException {
//		HttpEntity entity = null;
//		
//		try {
//
//			String ticket = initProfileSecurity();
//			UGC ugc = null;
//			HttpResponse response = this.ugcClient.getTarget(ticket, target);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				ObjectMapper mapper = new ObjectMapper();
//
//				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				UGCTargetResponse r = mapper.readValue(entity.getContent(), UGCTargetResponse.class);
//				assertTrue(r.getHierarchyList().getList().size()>0);
//
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			
//			
//			
//		} catch (AppAuthenticationFailedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
	
//	public void testModerationStatus() throws UserAuthenticationFailedException {
//		
//		HttpEntity entity = null;
//		
//		try {
//			
//			String ticket = initProfileSecurity();
//			UGC ugc = null;
//			HttpResponse response = this.ugcClient.addPost(ticket, this.target, null, postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				System.out.println(ugc.getTextContent());
//				assertEquals(this.target, ugc.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			assertNotNull(ugc);
//			response = this.ugcClient.updateModerationStatus(ticket, ugc.getId().toString(), "PENDING");
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201 || response.getStatusLine().getStatusCode() ==204) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				ugc = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				
//				assertEquals(ModerationStatus.PENDING, ugc.getModerationStatus());
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			assertNotNull(ugc);
//			response = this.ugcClient.getModerationStatus(ticket, "UNMODERATED");
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201 || response.getStatusLine().getStatusCode() ==204) {
//				
//				ObjectMapper mapper = new ObjectMapper();
//				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				UGCListResponse r = mapper.readValue(entity.getContent(), UGCListResponse.class);
//				assertTrue(r.getUGCList().size() >0);
//				
//
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			
//			response = this.ugcClient.getModerationStatusTarget(ticket, "pending",this.target);
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201 || response.getStatusLine().getStatusCode() ==204) {
//				
//				ObjectMapper mapper = new ObjectMapper();
//				mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				UGCListResponse r = mapper.readValue(entity.getContent(), UGCListResponse.class);
//				assertTrue(r.getUGCList().size() >0);
//				
//
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			
//		} catch (AppAuthenticationFailedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//	}
	
//	public void testThread() throws Exception {
//		
//		HttpEntity entity = null;
//		try {
//
//			String ticket = initProfileSecurity();
//			UGC parent = null, grandParent =null, child = null,thread = null;
//			
//			HttpResponse response = ugcClient.addPost(ticket, this.target, null, postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				grandParent = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				
//				assertEquals(this.target, grandParent.getTargetId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			
//			response = ugcClient.addPost(ticket, null, grandParent.getId().toString(), postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				parent = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				
//				assertEquals(grandParent.getId(), parent.getParentId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			
//			response = ugcClient.addPost(ticket, null, parent.getId().toString(), postContent,null);
//
//			entity = response.getEntity();
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
//				
//				child = (UGC) objectMapper.readValue(entity.getContent(), UGC.class);
//				
//				assertEquals(parent.getId(), child.getParentId());
//				
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
//			response = ugcClient.getThread(ticket, child.getId().toString());
//			if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() ==201) {
//				ObjectMapper mapper1 = new ObjectMapper();
//				mapper1.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//				UgcResponse u = mapper1.readValue(response.getEntity().getContent(), UgcResponse.class);
//				assertTrue(u.getUGC().getId().toString().equals(grandParent.getId().toString()));
//
//			} else {
//				assertEquals(200, response.getStatusLine().getStatusCode());
//			}
// 
//		} finally {
//			try {
//				EntityUtils.consume(entity);
//			} catch (IOException e) {
//				System.out.println("Could not consume entity");
//			}
//		}
//		
//	}
	
	private void initAppToken() throws AppAuthenticationFailedException {
		if (appToken==null)
			appToken = profileRestClient.getAppToken(this.profileAppUsername, this.profileAppPassword);
	}
	
	private String initProfileSecurity() throws AppAuthenticationFailedException, UserAuthenticationFailedException {
		initAppToken();
		Tenant tenant = profileRestClient.getTenantByName(appToken, tenantName);
		String ticket = profileRestClient.getTicket(this.appToken, this.profileUsername, this.profilePassword, tenant.getId());
		Profile p = profileRestClient.getProfileByUsername(appToken, this.profileUsername, tenant.getId());
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p, null, null));
		return ticket;
	}
	
	public static void main(String[] args) throws Exception {
		
			ProfileClient profileRestClient = new ProfileRestClientImpl();
			
			String token = profileRestClient.getAppToken("craftersocial", "craftersocial");
			Tenant tenant = profileRestClient.getTenantByName(token, "testing");
			String ticket = profileRestClient.getTicket(token, "admin", "admin",tenant.getId());
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("ticket", ticket));
			qparams.add(new BasicNameValuePair("target", "testing"));
			
			qparams.add(new BasicNameValuePair("textContent", "Content"));
			
			
			URI uri = URIUtils.createURI("http", "localhost", 8080, "crafter-social/api/1/ugc/" + "create.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
						
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(uri);
			File file = new File("/Users/alvarogonzalez/development/projects/crafter-social/rest/src/test/resources/test.txt");
			MultipartEntity me = new MultipartEntity();
			 
	        //The usual form parameters can be added this way
	        //me.addPart("fileDescription", new StringBody(fileDescription != null ? fileDescription : "")) ;
	        //multiPartEntity.addPart("fileName", new StringBody(fileName != null ? fileName : file.getName())) ;
	
	        /*Need to construct a FileBody with the file that needs to be attached and specify the mime type of the file. Add the fileBody to the request as an another part.
	        This part will be considered as file part and the rest of them as usual form-data parts*/
	        FileBody fileBody = new FileBody(file, "application/octect-stream") ;
	        //me.addPart("ticket", new StringBody(token)) ;
	        //me.addPart("target", new StringBody("my-test")) ;
	        me.addPart("attachments", fileBody) ;
	
	        httppost.setEntity(me) ;
	        httpclient.execute(httppost);
		
	}
	
	

	
	static class Test{
		private ArrayList<MyClass> ugcAuditList;

		
		public ArrayList<MyClass> getUGCAuditList() {
			return ugcAuditList;
		}

		@JsonProperty("UGCAuditList")
		public void setUGCAuditList(ArrayList<MyClass> test) {
			this.ugcAuditList = test;
		}
	}
	
	static class MyClass{
		private String id;
		private String name;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	@JsonAutoDetect(fieldVisibility = Visibility.NONE,setterVisibility=Visibility.ANY)
	 abstract class MixIn  {

	    @JsonProperty("UGCAuditList")
	    public abstract void setUGCAuditList(ArrayList<UGCAudit> ugcAuditList);

	    

	 }
	

}
