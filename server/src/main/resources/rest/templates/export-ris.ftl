<#ftl outputFormat="plainText" stripWhitespace=true>
<#list page.content as record><#t>
TY  - ${record.kind}
DB  - Evidence Engine
DP  - Campaign Resources
SF  - publications
ID  - ${record.id?c}
<#if record.title??>TI  - ${record.title}</#if>
<#if record.authors??><#t>
  <#list utils.split(record.authors) as author><#t>
AU  - ${author}
  </#list><#t>
</#if><#t>
<#if record.journal??><#t>
  <#if record.journal.title??>JF  - ${record.journal.title}</#if><#lt>
  <#if record.journal.abbreviation??>JA  - ${record.journal.abbreviation}</#if><#lt>
  <#if record.journal.publisher?? && record.journal.publisher.location??>PP  - ${record.journal.publisher.location}</#if><#lt>
</#if><#t>
<#if record.kind == "BOOK"><#t>
  <#if record.isbn??>SN  - ${record.isbn}</#if><#lt>
<#else><#t>
  <#if record.journal?? && record.journal.issn??>SN  - ${record.journal.issn}</#if><#lt>
</#if><#t>
<#if record.doi??>DI  - ${record.doi}</#if><#lt>
<#if record.date??>DA  - ${utils.renderRisDate(record.date)}</#if><#lt>
<#if record.year??>YR  - ${record.year?c}</#if><#lt>
<#if record.pmid??>PMID  - ${record.pmid}</#if><#lt>
<#if record.abstract??>AB  - ${record.abstract}</#if><#lt>
<#if record.notes??>PA  - ${record.notes}</#if><#lt>
<#if record.url??>UR  - ${record.url}</#if><#lt>
<#if record.accessed??>DA  - ${record.accessed}</#if><#lt>
ER  -
</#list><#t>