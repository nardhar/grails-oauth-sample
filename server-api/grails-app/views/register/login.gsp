<!doctype html>
<html>
<head>
    <title><g:message code="login.message" default="Please Login" /></title>
    <meta name="layout" content="main">
</head>
<body>
    <g:form class="form-signin" method="post" action="login">
        <input type="hidden" name="${_csrf?.parameterName}" value="${_csrf?.token}"/>
        <label for="inputUsername" class="sr-only"><g:message code="user.username.label" /></label>
        <input type="text" id="inputUsername" name="username" value="${request.getSession().getAttribute("SPRING_SECURITY_LAST_USERNAME")}"
               class="form-control" placeholder="${message(code: 'user.username.label')}" required="" autofocus="">
        <label for="inputPassword" class="sr-only"><g:message code="user.password.label" /></label>
        <input type="password" id="inputPassword" name="password"
               class="form-control" placeholder="${message(code: 'user.password.label')}" required="">
        <g:if test="${params.containsKey('error')}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <g:message code="user.invalidCredentials.label" />
            </div>
        </g:if>
        <div class="checkbox">
            <label for="rememberMe">
                <input id="rememberMe" name="remember-me" type="checkbox">
                <g:message code="user.rememberMe.label" />
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit"><g:message code="user.login.button" /></button>
    </g:form>
</body>
</html>
