<html>
<head>
  <title>Metrics</title>
  <#include "freemaker.css">
  
</head>
<body>
  
  
  <h1>${pojo.name}</h1>
  <hr>
  <p>${pojo.description}
  <hr>
  <h2>Dimensions</h2>:
  <ul>
    <#list dimensions as dim>
      <li><b>${dim.name}</b>: ${dim.description}</li>
    </#list>
  </ul>
  <#include "footer.html">
</body>
</html>