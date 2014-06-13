/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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

package org.craftercms.social.documentation.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.configuration.JacksonSwaggerSupport;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiKey;
import com.wordnik.swagger.model.AuthorizationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration for REST API Documentation.
 *
 * @author Dejan Brkic
 */
@Configuration
public class RestApiDocumentationSwaggerConfig {

    public static final List<String> DEFAULT_INCLUDE_PATTERNS = Arrays.asList(new String[] {"/api/3/.*"});
    public static final String SWAGGER_GROUP = "comments";

    private
    @Value("${documentation.services.hostUrl}")
    String hostUrl;

    /**
     * Autowire the bundled swagger config
     */
    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;
    //@Autowired
    // private ModelProvider modelProvider;

    /**
     * Adds the jackson scala module to the MappingJackson2HttpMessageConverter registered with spring
     * Swagger core models are scala so we need to be able to convert to JSON
     * Also registers some custom serializers needed to transform swagger models to swagger-ui required json format
     */
    @Bean
    public JacksonSwaggerSupport jacksonScalaSupport() {
        JacksonSwaggerSupport jacksonScalaSupport = new JacksonSwaggerSupport();
        //Set to false to disable
        //jacksonScalaSupport. setRegisterScalaModule(true);
        return jacksonScalaSupport;
    }

    /**
     * Object mapper.
     *
     * @return the configured object mapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        //This is the opportunity to override object mapper behavior
        return new ObjectMapper();
    }


    /**
     * Global swagger settings
     */
    @Bean
    public SwaggerGlobalSettings swaggerGlobalSettings() {
        SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
        swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages());

        // This is where we add types to ignore (or use the default provided types)
        swaggerGlobalSettings.setIgnorableParameterTypes(springSwaggerConfig.defaultIgnorableParameterTypes());
        // This is where we add type substitutions (or use the default provided alternates)
        swaggerGlobalSettings.setAlternateTypeProvider(springSwaggerConfig.defaultAlternateTypeProvider());
        return swaggerGlobalSettings;
    }


    /**
     * API Info as it appears on the swagger-ui page
     */
    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Crafter Studio Spring MVC swagger 1.2 api",
            "Crafter Studio api based on the swagger 1.2 spec", "http://en.wikipedia.org/wiki/Terms_of_service",
            "somecontact@somewhere.com", "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0.html");
        return apiInfo;
    }

    /**
     * Configure a SwaggerApiResourceListing for each swagger instance within your app. e.g. 1. private  2. external
     * apis
     * Required to be a spring bean as spring will call the postConstruct method to bootstrap swagger scanning.
     *
     * @return
     */
    @Bean
    public SwaggerApiResourceListing swaggerApiResourceListing() {
        //The group name is important and should match the group set on ApiListingReferenceScanner
        //Note that swaggerCache() is by DefaultSwaggerController to serve the swagger json
        SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(springSwaggerConfig
            .swaggerCache(), SWAGGER_GROUP);

        //Set the required swagger settings
        swaggerApiResourceListing.setSwaggerGlobalSettings(swaggerGlobalSettings());

        //Use a custom path provider or springSwaggerConfig.defaultSwaggerPathProvider()
        swaggerApiResourceListing.setSwaggerPathProvider(new RelativeSwaggerPathProvider());

        //Supply the API Info as it should appear on swagger-ui web page
        swaggerApiResourceListing.setApiInfo(apiInfo());

        // Set the model provider, uses the default autowired model provider.
        swaggerApiResourceListing.setModelProvider(springSwaggerConfig.defaultModelProvider());

        //Global authorization - see the swagger documentation
        swaggerApiResourceListing.setAuthorizationTypes(authorizationTypes());

        //Sets up an auth context - i.e. which controller request paths to apply global auth to
        //swaggerApiResourceListing.setAuthorizationContext(authorizationContext());

        //Every SwaggerApiResourceListing needs an ApiListingReferenceScanner to scan the spring request mappings
        swaggerApiResourceListing.setApiListingReferenceScanner(apiListingReferenceScanner());
        return swaggerApiResourceListing;
    }

    @Bean
    /**
     * The ApiListingReferenceScanner does most of the work.
     * Scans the appropriate spring RequestMappingHandlerMappings
     * Applies the correct absolute paths to the generated swagger resources
     */
    public ApiListingReferenceScanner apiListingReferenceScanner() {
        ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner();

        //Picks up all of the registered spring RequestMappingHandlerMappings for scanning
        apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig
            .swaggerRequestMappingHandlerMappings());

        //Excludes any controllers with the supplied annotations
        apiListingReferenceScanner.setExcludeAnnotations(springSwaggerConfig.defaultExcludeAnnotations());

        //How to group request mappings to ApiResource's typically by spring controller classes or @Api.value()
        apiListingReferenceScanner.setResourceGroupingStrategy(springSwaggerConfig.defaultResourceGroupingStrategy());

        //Path provider used to generate the appropriate uri's
        apiListingReferenceScanner.setSwaggerPathProvider(new RelativeSwaggerPathProvider());

        //Must match the swagger group set on the SwaggerApiResourceListing
        apiListingReferenceScanner.setSwaggerGroup(SWAGGER_GROUP);

        //Only include paths that match the supplied regular expressions
        apiListingReferenceScanner.setIncludePatterns(DEFAULT_INCLUDE_PATTERNS);

        return apiListingReferenceScanner;
    }

    /**
     * Example of a custom path provider
     */
    @Bean
    public DocumentationPathProvider documentationPathProvider() {
        DocumentationPathProvider documentationPathProvider = new DocumentationPathProvider();
        documentationPathProvider.setDefaultSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
        documentationPathProvider.setHostUrl(hostUrl);
        return documentationPathProvider;
    }


    // Relative path will be addressed in next swagger-springmvc release (seems it is already addresses in 0.8
    // .2-SNAPSHOT

    /* Relative path example
    @Bean
    public SwaggerApiResourceListing swaggerApiResourceListing() {
        SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(springSwaggerConfig
            .swaggerCache(), SWAGGER_GROUP);
        swaggerApiResourceListing.setSwaggerGlobalSettings(swaggerGlobalSettings());
        swaggerApiResourceListing.setSwaggerPathProvider(documentationRelativePathProvider());
        swaggerApiResourceListing.setApiListingReferenceScanner(relativeApiListingReferenceScanner());
        return swaggerApiResourceListing;
    }

    @Bean
    public ApiListingReferenceScanner relativeApiListingReferenceScanner() {
        ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner();
        apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig
        .swaggerRequestMappingHandlerMappings());
        apiListingReferenceScanner.setExcludeAnnotations(springSwaggerConfig.defaultExcludeAnnotations());
        apiListingReferenceScanner.setResourceGroupingStrategy(springSwaggerConfig.defaultResourceGroupingStrategy());
        apiListingReferenceScanner.setSwaggerPathProvider(documentationRelativePathProvider());
        apiListingReferenceScanner.setSwaggerGroup(SWAGGER_GROUP);
        apiListingReferenceScanner.setIncludePatterns(DEFAULT_INCLUDE_PATTERNS);
        return apiListingReferenceScanner;
    }
      */
    private List<AuthorizationType> authorizationTypes() {
        ArrayList<AuthorizationType> authorizationTypes = new ArrayList<AuthorizationType>();
        authorizationTypes.add(new ApiKey("x-auth-token", "header"));
        return authorizationTypes;
    }

}
