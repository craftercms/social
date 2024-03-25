package org.craftercms.social.client.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.craftercms.social.client.filters.support.ProfileNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProfileClient {

    public static final String BASE_URL_REST_API = "/api/1";
    public static final String BASE_URL_PROFILE = BASE_URL_REST_API + "/profile";
    public static final String BASE_URL_AUTHENTICATION = BASE_URL_REST_API + "/authentication";
    public static final String URL_AUTH_GET_TICKET = "ticket";
    public static final String URL_PROFILE_GET_BY_USERNAME = "/by_username";
    public static final String URL_PROFILE_CREATE = "/create";
    public static final String URL_AUTH_CREATE_TICKET = "/ticket/create";

    public static final String PARAM_TENANT_NAME = "tenantName";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_ACCESS_TOKEN_ID = "accessTokenId";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_ENABLED = "enabled";
    public static final String PARAM_ATTRIBUTES = "attributes";
    public static final String PARAM_ROLE = "role";
    public static final String PARAM_PROFILE_ID = "profileId";
    public static final String URL_PROFILE_UPDATE_ATTRIBUTES = "/attributes/update";

    public static final String PARAM_PASSWORD = "password";
    public static final String USER_USERNAME = "USER_USERNAME";
    public static final String USER_PROFILE_ID = "id";


    protected HttpClient client;
    protected String profileUri;
    protected String profileAccessToken;
    protected HttpHost host;
    protected String tenantName;
    protected String profileDeploymentName;
    protected String socialContextIdParam;

    protected ObjectMapper mapper;


    public void init() throws URISyntaxException {
        client = HttpClientBuilder.create().build();
        URI uri = new URI(profileUri);
        host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        mapper= new ObjectMapper();
    }


    public Map<String, Object> getProfile(String username) throws Exception {

        List<NameValuePair> getParams= new ArrayList<NameValuePair>();
        getParams.add(new BasicNameValuePair(PARAM_TENANT_NAME, tenantName));
        getParams.add(new BasicNameValuePair(PARAM_USERNAME, username));
        getParams.add(new BasicNameValuePair (PARAM_ACCESS_TOKEN_ID,profileAccessToken));
        final URI uri = new URIBuilder(BASE_URL_PROFILE + URL_PROFILE_GET_BY_USERNAME).addParameters(getParams)
            .build();
        HttpGet httpGet = new HttpGet(profileDeploymentName+uri);
        setHeaders(httpGet);
        HttpResponse response=client.execute(host,httpGet);
        Map<String,Object> profile = null;
        try {
            if (!(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK)) {
                throw new Exception("Unable to connect get profile");
            }else if (response.getStatusLine().getStatusCode()==HttpServletResponse.SC_NOT_FOUND || response
                .getEntity().getContentLength()==0){
                throw new ProfileNotFound();
            }
            if (response.getEntity().getContentType().getValue().contains(ContentType.APPLICATION_JSON.getMimeType())) {
                final ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
                response.getEntity().writeTo(responseOutput);
                 profile = mapper.readValue(new ByteArrayInputStream(responseOutput.toByteArray()), HashMap.class);
            } else {
                throw new Exception("Response content is not a JSON");
            }
        }finally {
            response.getEntity().getContent().close();
        }
        return profile;
    }

    public Map<String,Object> createProfile(final HttpServletRequest request, final Map<String, Object> userInformation)
        throws
    Exception {
        if(userInformation==null){
            return null;
        }
        String profileUserName=(String)userInformation.get(USER_USERNAME);
        if(StringUtils.isBlank(profileUserName) || StringUtils.containsWhitespace(profileUserName)){
            throw new IllegalArgumentException("Username can not be empty or null or have whitespaces");
        }

        List<NameValuePair> createParams= new ArrayList<NameValuePair>();
        HttpPost post = new HttpPost(profileDeploymentName+BASE_URL_PROFILE + URL_PROFILE_CREATE);
        createParams.add(new BasicNameValuePair(PARAM_TENANT_NAME,tenantName));
        createParams.add(new BasicNameValuePair(PARAM_USERNAME,profileUserName ));
        createParams.add(new BasicNameValuePair(PARAM_PASSWORD, UUID.randomUUID().toString()));
        createParams.add(new BasicNameValuePair(PARAM_EMAIL, (String)userInformation.get(PARAM_EMAIL) ));
        createParams.add(new BasicNameValuePair(PARAM_ENABLED, "true"));
        createParams.add(new BasicNameValuePair(PARAM_ROLE, (String)userInformation.get(PARAM_ROLE)));
        createParams.add(new BasicNameValuePair(PARAM_ACCESS_TOKEN_ID,profileAccessToken));
        createParams.add(new BasicNameValuePair(PARAM_ATTRIBUTES, writeAttributes((Map<String,Object>)userInformation
            .get(PARAM_ATTRIBUTES))));
        HttpEntity entity = new StringEntity(URLEncodedUtils.format(createParams,"UTF-8"),ContentType
            .APPLICATION_FORM_URLENCODED.withCharset("UTF-8"));
        post.setEntity(entity);
        //setHeaders(post);
        final HttpResponse response = client.execute(host, post);
        post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.withCharset("UTF-8").toString());
        Map newProfile = null;
        try {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                newProfile = mapper.readValue(response.getEntity().getContent(), HashMap.class);
            } else if(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST){
                Map<String,Object>error=mapper.readValue(response.getEntity().getContent(),HashMap.class);
                if(!error.isEmpty()){
                    if(error.containsKey("errorCode") && error.get("errorCode").equals("PROFILE_EXISTS")){
                        newProfile = getProfile(profileUserName);
                    }else{
                        throw new Exception("Unable to create profile");
                    }
                }else{
                    throw new Exception("Unable to create profile");
                }
            }else{
                throw new Exception("Unable to create profile");
            }
        }finally {
            response.getEntity().getContent().close();
        }
        return newProfile;
    }


    public Map<String, Object> auth(final String profileId) throws Exception {
        List<NameValuePair> createParams= new ArrayList<NameValuePair>();
        createParams.add(new BasicNameValuePair(PARAM_ACCESS_TOKEN_ID,profileAccessToken));
        createParams.add(new BasicNameValuePair(PARAM_PROFILE_ID,profileId));
        HttpPost post = new HttpPost(profileDeploymentName+BASE_URL_AUTHENTICATION + URL_AUTH_CREATE_TICKET);
        HttpEntity entity = new StringEntity(URLEncodedUtils.format(createParams,"UTF-8"),ContentType
            .APPLICATION_FORM_URLENCODED.withCharset("UTF-8"));
        post.setEntity(entity);
        //setHeaders(post);
        final HttpResponse response = client.execute(host, post);
        Map<String,Object> ticket;
        try {
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                throw new Exception("Unable to auth profile");
            }
            ticket= mapper.readValue(response.getEntity().getContent(),HashMap.class);
        }finally {
            response.getEntity().getContent().close();
        }
        return ticket;
    }

    private String writeAttributes(final Map<String,Object> attributes) throws JsonProcessingException {
        return mapper.writeValueAsString(attributes);
    }


    private void setHeaders(final HttpRequestBase requestBase) {
        requestBase.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        requestBase.setHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8");
        requestBase.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        requestBase.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
    }

    public void setProfileUri(final String profileUri) {
        this.profileUri = profileUri;
    }

    public void setProfileAccessToken(final String profileAccessToken) {
        this.profileAccessToken = profileAccessToken;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setProfileDeploymentName(final String profileDeploymentName) {
        this.profileDeploymentName = profileDeploymentName;
    }

    public void setSocialContextIdParam(final String socialContextIdParam) {
        this.socialContextIdParam = socialContextIdParam;
    }


    public boolean isTicketValid(final String authCookieValue) throws URISyntaxException, IOException {
        List<NameValuePair> getParams= new ArrayList<NameValuePair>();
        getParams.add(new BasicNameValuePair (PARAM_ACCESS_TOKEN_ID,profileAccessToken));
        final URI uri = new URIBuilder(BASE_URL_AUTHENTICATION+"/"+URL_AUTH_GET_TICKET+"/"+authCookieValue)
            .addParameters(getParams).build();
        HttpGet httpGet = new HttpGet(profileDeploymentName+uri);
        setHeaders(httpGet);
        HttpResponse response=client.execute(host,httpGet);
        HttpEntity entity = response.getEntity();
        boolean result=false;
            if(response.getStatusLine().getStatusCode()!=HttpServletResponse.SC_OK){
                result=false;
            }else {
                ByteArrayOutputStream outputStream=null;
                String stringContent="";
                try {
                    outputStream = new ByteArrayOutputStream();
                    entity.writeTo(outputStream);
                    stringContent = new String(outputStream.toByteArray(), Charset.forName("UTF-8"));
                }catch (Throwable ex){
                    ex.printStackTrace();

                }finally {
                    if(outputStream!=null){
                        outputStream.close();
                    }
                    entity.getContent().close();
                }
                if(StringUtils.isNotBlank(stringContent)) {
                    final HashMap ticket = mapper.readValue(stringContent, HashMap.class);
                    result = !ticket.isEmpty();
                }else{
                    result=false;
                }
            }

        return result;
    }

    public Map<String,Object> updateAttributes(final String profileId, final Map<String, Object> attributesToUpdate) throws IOException {
        if(attributesToUpdate==null){
            return null;
        }
        Map<String,Object>profile=null;
        URI uri=null;
        try {
            uri = new URIBuilder(profileDeploymentName+BASE_URL_PROFILE +"/"+profileId
                +URL_PROFILE_UPDATE_ATTRIBUTES).addParameter(PARAM_ACCESS_TOKEN_ID,profileAccessToken)
                .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<NameValuePair> createParams= new ArrayList<NameValuePair>();
        HttpPost post = new HttpPost(uri);
        HttpEntity entity = new StringEntity(mapper.writeValueAsString(attributesToUpdate),ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        HttpResponse response = client.execute(host,post);
        try{
            if(response.getStatusLine().getStatusCode()==HttpServletResponse.SC_OK){
                profile=mapper.readValue(response.getEntity().getContent(),HashMap.class);
            }
        }finally {
            response.getEntity().getContent().close();
        }
        return profile;
    }
}
