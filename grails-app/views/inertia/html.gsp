<!DOCTYPE html>
<html lang="en" class="h-full bg-slate-100">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/>
    <link rel="icon" type="image/svg+xml" href="${request.contextPath}/static/favicon.svg">
    <g:if env="development">
        <script type="module" src="http://localhost:3000/@vite/client"></script>
        <script type="module" src="http://localhost:3000/src/main/javascript/main.js"></script>
    </g:if>
    <g:else>
        <script type="module" src="/static/dist/${inertiaManifest['src/main/javascript/main.js']['file']}"></script>
        <g:each in="${inertiaManifest['src/main/javascript/main.js']['css']}" var="inertiaCss">
            <link rel="stylesheet" href="/static/dist/${inertiaCss}">
        </g:each>
    </g:else>
    <inertia:head/>
</head>
<body class="font-sans leading-none text-slate-700 antialiased">
<inertia:app/>
</body>
</html>