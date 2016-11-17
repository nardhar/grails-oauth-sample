<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="app.title" default="OAuth 2.0 App" /></title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>
    Show current apps or info about user profile
    <g:form url="/logout">
        <input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button class="btn btn-link" type="submit"><g:message code="user.logout.button" /></button>
    </g:form>
</body>
</html>
