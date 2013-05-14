package org.craftercms.blog.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.craftercms.blog.services.CrafterSocialClient;
import org.codehaus.jackson.map.DeserializationConfig;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrafterSocialRestClientImpl implements CrafterSocialClient {
	
	private static final Logger log = LoggerFactory.getLogger(CrafterSocialRestClientImpl.class);
	
	HttpClient client = new DefaultHttpClient();
	
	private int port = 8080;
	private String scheme = "http";
	private String host = "localhost";
	private String socialAppPath = "/crafter-social";
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean hasRootCreatePermissions(String tenant, String ticket) {
		boolean hasCreatPermissions = false;
		HttpEntity entity = null;

		if (log.isDebugEnabled()) {
			log.debug("Getting root create permission: " + tenant);
		}

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("ticket", ticket));
		qparams.add(new BasicNameValuePair("tenant",tenant));

		try {
			URI uri = URIUtils.createURI(scheme, host, port, socialAppPath
					+ "/api/2/permission/create.json",
					URLEncodedUtils.format(qparams, HTTP.UTF_8), null);
			HttpGet httpget = new HttpGet(uri);

			HttpResponse response = client.execute(
					httpget);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				hasCreatPermissions = (Boolean) objectMapper.readValue(entity.getContent(),
						Boolean.class);
				
				
//				Object test = (String) objectMapper.readValue(entity.getContent(),
//						Object.class);
				
				/*objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, false);
				Message m = (Message) objectMapper.readValue(entity.getContent(),
						Message.class);
				hasCreatPermissions = m.isBoolean();
				System.out.println("****** " +hasCreatPermissions);*/
				
				
//				BufferedReader br = new BufferedReader(
//                        new InputStreamReader((response.getEntity().getContent())));
//				String output;
//				System.out.println("Output from Server .... \n");
//				while ((output = br.readLine()) != null) {
//					//System.out.println("****** " +output);
//					if (output.endsWith("true}")) {
//						hasCreatPermissions = true;
//						break;
//					}
//				}
 
			}
		} catch (URISyntaxException e) {
			log.error(e.getMessage(),e);
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				log.error("Could not consume entity", e);
			}
		}
		
		return hasCreatPermissions;
	}
	
	class Message implements Serializable {
	  private Map<String, String> dataset = new HashMap<String, String>();
	  
	  Message() {
		  
	  }

	  public boolean isBoolean() {
	    return Boolean.valueOf(dataset.get("boolean"));
	  }

	  @JsonProperty("boolean")
	  public void setBoolean(boolean success) {
	    dataset.put("boolean", String.valueOf(success));
	  }
	}

}
