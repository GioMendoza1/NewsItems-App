[#assign title = content.title!"Hello Magnolia :-)"]

<!DOCTYPE html>
<html>
   <head>
      <title>${title}</title>
      <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Raleway:200" type="text/css">
      <link rel="stylesheet" href="${ctx.contextPath}/.resources/hello-magnolia/webresources/css/style.css">
      <link rel="stylesheet" href="${ctx.contextPath}/.resources/hello-magnolia/webresources/css/style2.css">
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.9.0/css/bulma.min.css">
      <script defer src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script>
      <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
      <script src="https://unpkg.com/vue"></script>
      [@cms.page /]
   </head>
   <body>
      <div class="container">
         <header>
            <h1>${title}</h1>
            [#if content.introText?has_content]<p>${content.introText}</p>[/#if]
         </header>
          <div class="main">
          [@cms.area name="main"/]
          </div>
      </div>
   </body>
</html>