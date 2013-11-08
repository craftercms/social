<#ftl strip_whitespace=true>

<#import "spring.ftl" as spring />

<#--Iterates through a list only if it's not null and have more than one item-->
<#assign emptyList = []>
<#macro safeList theList=emptyList >
    <#if (theList?? && (theList?size > 0))>
        <#list theList as key>
            <#nested key, key_has_next, key_index/>
        </#list>
    </#if>
</#macro>



<#--Adds the servlet mapping to an url-->
<#macro servletUrl relativeUrl>${springMacroRequestContext.getContextUrl("/web" + relativeUrl)}</#macro>

<#-- optional todo: why this aren't working, I configured in crafter.properties as in the crafter xml-->
<#--${requestContext.requestUri}-->
<#--${requestContext.contextPath}-->
<#--${requestContext.contextUrl("")}-->

<#--Prints a variable value, only if it's not null or blank-->
<#macro $ var="">
    <#if var?? && var != "">
        ${var}
    </#if>
</#macro>


<#--Formats a date, only if it's not null-->
<#macro date var format="MM dd, yyyy">
    <#if var??>
        ${var?string(format)}
    </#if>
</#macro>


<#macro formInput path id="" attributes="" fieldType="text" default="">
    <#-- Start of Modified spring.bind -->
    <#if htmlEscape??>
        <#assign status = springMacroRequestContext.getBindStatus(path, htmlEscape) in spring>
    <#else>
        <#assign status = springMacroRequestContext.getBindStatus(path) in spring>
    </#if>
    <#if spring.status.value?? && spring.status.value?is_boolean>
        <#assign stringStatusValue = spring.status.value?string in spring>
    <#else>
        <#assign stringStatusValue = spring.status.value!default in spring>
    </#if>
    <#-- End of Modified spring.bind -->
    <#if id == "">
        <#assign id=spring.status.expression>
    </#if>
    <input type="${fieldType}" id="${id}" name="${spring.status.expression}"
           value="<#if fieldType!="password">${spring.stringStatusValue}</#if>" ${attributes}<@spring.closeTag/>
</#macro>

<#macro formTextarea path id="" attributes="">
    <@spring.bind path/>
    <#if id == "">
        <#assign id=spring.status.expression>
    </#if>
    <textarea id="${id}" name="${spring.status.expression}" ${attributes}>${spring.stringStatusValue}</textarea>
</#macro>

<#macro formSingleSelect path options valueProp="" textProp="" id="" attributes="">
    <@spring.bind path/>
    <#if id == "">
        <#assign id=spring.status.expression>
    </#if>
    <select id="${id}" name="${spring.status.expression}" ${attributes}>
        <#if options?is_hash>
            <#list options?keys as value>
            <option value="${value?html}"<@spring.checkSelected value/>>${options[value]?html}</option>
            </#list>
        <#else>
            <#list options as option>
            <#assign value = getOptionValue(option, valueProp)>
            <#assign text = getOptionText(option, textProp)>
            <option value="${value?html}"<@spring.checkSelected value/>>${text?html}</option>
            </#list>
        </#if>
    </select>
</#macro>

<#macro formMultiSelect path options valueProp="" textProp="" id="" attributes="">
    <@spring.bind path/>
    <#if id == "">
        <#assign id=spring.status.expression>
    </#if>
    <select multiple="multiple" id="${id}" name="${spring.status.expression}" ${attributes}>
        <#if options?is_hash>
            <#list options?keys as value>
            <#assign isSelected = spring.contains(spring.status.value![""], value)>
            <option value="${value?html}"<#if isSelected> selected="selected"</#if>>${options[value]?html}</option>
            </#list>
        <#else>
            <#list options as option>
            <#assign value = getOptionValue(option, valueProp)>
            <#assign text = getOptionText(option, textProp)>
            <#assign isSelected = spring.contains(spring.status.value![""], value)>
            <option value="${value?html}"<#if isSelected> selected="selected"</#if>>${text?html}</option>
            </#list>
        </#if>
    </select>
</#macro>

<#macro formCheckbox path id="" attributes="">
	<@spring.bind path />
    <#if id == "">
        <#assign id=spring.status.expression>
    </#if>
    <#assign isSelected = spring.status.value?? && spring.status.value?string=="true">
	<input type="hidden" name="_${spring.status.expression}" value="on"/>
	<input type="checkbox" id="${id}" name="${spring.status.expression}"<#if isSelected> checked="checked"</#if> ${attributes}/>
</#macro>

<#macro showErrors classOrStyle="" errorClassOrStyle="" lastErrorClassOrStyle=errorClassOrStyle>
    <#if spring.status.errorMessages?size != 0>
    <ul <@renderClassOrStyle classOrStyle/>>
        <#list spring.status.errorMessages as error>
            <#if error_has_next>
                <li <@renderClassOrStyle errorClassOrStyle/>>${error?html}</li>
            <#else>
                <li <@renderClassOrStyle lastErrorClassOrStyle/>>${error?html}</li>
            </#if>
        </#list>
    </ul>
    </#if>
</#macro>

<#macro renderClassOrStyle classOrStyle>
    <#if classOrStyle != "">
        <#if classOrStyle?index_of(":") == -1>class="${classOrStyle}"<#else>style="${classOrStyle}"</#if>
    </#if>
</#macro>

<#function getOptionValue option valueProp>
    <#if valueProp != "">
        <#return option[valueProp]?string>
    <#else>
        <#return option?string>
    </#if>
</#function>

<#function getOptionText option textProp>
    <#if textProp != "">
        <#return option[textProp]?string>
    <#else>
        <#return option?string>
    </#if>
</#function>
