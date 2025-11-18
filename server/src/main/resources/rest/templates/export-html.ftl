<#ftl outputFormat="HTML">
<!DOCTYPE html>
<html>
<head>
  <title>Evidence Engine</title>
  <meta charset="UTF-8">
  <meta name="keywords" content="${recordKind}">
  <meta name="description" content="Database export">
  <meta name="author" content="Campaign Resources&mdash;Evidence Engine">
  <style>
    @page {
      size: ${paper} ${orientation};
      margin: 20mm 10mm 20mm 10mm;
    }
    body {
      font-family: "DejaVu Sans", "Liberation Sans", "Liberation Fallback", "Arial", "Helvetica";
      font-size: ${fontSize?c}pt
    }
    footer {
      padding-top: 32px;
    }
    hr {
      height: 1px;
      background-color: LightGrey;
      border: none;
    }
    table, th, td {
      border: 1px solid black;
      border-collapse: collapse;
      border-color: LightGrey;
    }
    thead {
      background-color: #f9fafb;
    }
    th, td {
      padding-left: 8px;
      padding-right: 8px;
    }
    footer > span {
      float: left;
      width: 33%;
    }
    .text-left {
      text-align: left;
    }
    .text-center {
      text-align: center;
    }
    .text-right {
      text-align: right;
    }
    .flex {
      display: flex;
    }
    .flex-row{
      flex-direction: row;
    }
    .flex-wrap {
      flex-wrap: wrap;
    }
    .gap-4 {
      gap: 4px;
    }
    .single {
      float:left;
      width:${singleFloatWidth?c}%;
    }
    .full {
      float:left;
      width:96%;
    }
  </style>
</head>
<body>
  <header>
<#if contentType == "text/html">
    <p class="text-center">${recordKind?capFirst} Export</p>
</#if>
    <h1>${recordKind?capFirst}</h1>
    Generated for user &apos;${user}&apos; on ${timestamp}.<br>
    Click <a href="${dataUrl}" target="_blank">here</a> to regenerate this document.<br>
    Click <a href="${webUrl}" target="_blank">here</a> to view this list online in the Evidence Engine.<br>
<#if debug>
    Paper: ${paper} ${orientation} ${fontSize?c} pt.
</#if>
<#if filter??>
    <h2>Filter</h2>
  <#if recordLabel??>
    Record: ${recordLabel}<br>
  <#else>
    <#if masterTopicLabel??>
    Master topic: ${masterTopicLabel}<br>
    Include sub-topics: ${utils.renderBoolean(filter.recursive, true)}<br>
    </#if>
    <#if masterRecordLabel??>
    Master record: ${masterRecordLabel}<br>
    </#if>
    <#if statusLabel??>
    Status: ${statusLabel}<br>
    </#if>
    <#if filter.text??>
    Text: ${filter.text}<br>
    Advanced search: ${utils.renderBoolean(filter.advancedSearch, true)}<br>
    </#if>
    <#if targetKindLabel??>
    Target kind: ${targetKindLabel}<br>
    </#if>
    <#if targetLabel??>
    Target record: ${targetLabel}<br>
    </#if>
    <#if parentLabel??>
    Parent comment: ${parentLabel}<br>
    </#if>
    <#if userLabel??>
    User: ${userLabel}<br>
    </#if>
    <#if from??>
    From: ${from}<br>
    </#if>
    <#if to??>
    To: ${to}<br>
    </#if>
    <#if entityKindLabel??>
    Record kind: ${entityKindLabel}<br>
    </#if>
    <#if entityLabel??>
    Record: ${entityLabel}<br>
    </#if>
    <#if transactionKindLabel??>
    Transaction kind: ${transactionKindLabel}<br>
    </#if>
  </#if>
</#if>
<#if pageSort?? && pageSort.sort?? && pageSort.sort.orders?hasContent>
    <h2>Sorting</h2>
    Sorted on <#list pageSort.sort.orders as order>${order.property} ${(order.direction!"asc")?lowerCase}<#sep>, </#sep></#list>
</#if>
  </header>
  <h2>Results</h2>
  <p>Page ${page.number + 1} of ${page.totalPages} (page size: ${page.size}), showing ${page.numberOfElements} of ${page.totalElements} records.</p>
<#if renderTable>
  <h3>Table</h3>
  <table>
    <thead>
      <tr>
  <#list columns as column>
        <th>${column.header}</th>
  </#list>
      <tr>
    </thead>
    <tbody>
  <#list page.content as record>
    <tr>
    <#list columns as column>
      <td <#if column.type.name() == "NUMBER" || column.type.name() == "ID">class="text-right"</#if>>${column.render(record, false)?noEsc}</td>
    </#list>
    </tr>
  </#list>
    </tbody>
  </table>
</#if>
<#if renderDetails>
  <#if renderTable>
  <br>
  <hr>
  </#if>
  <h3>Details</h3>
  <#list page.content as record>
    <#list allColumns as column>
    <div class="<#if column.span.name() == "SINGLE">single<#else>full</#if>"><b>${column.header}:</b>&nbsp;${column.render(record, false)?noEsc}</div>
    </#list>
    <hr class="full">
  </#list>
</#if>
<#if contentType == "text/html">
  <footer>
    <span class="text-left">${timestamp}</span><span class="text-center">From&nbsp;<a href="https://ee.campaign-resources.org" target="_blank">Evidence Engine</a></span>
  </footer>
</#if>
</body>
</html>