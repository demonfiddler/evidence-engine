<#ftl outputFormat="CSV">
<#if renderTable><#assign cols=columns><#else><#assign cols=allColumns></#if>
<#list cols as col>"${col.header}"<#sep>,</#sep></#list>
<#list page.content as record><#list cols as col>"${col.render(record, true)}"<#sep>,</#sep></#list>
</#list>