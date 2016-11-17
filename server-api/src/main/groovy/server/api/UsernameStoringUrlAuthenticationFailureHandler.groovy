package server.api

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

class UsernameStoringUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException
    {
        request.getSession(true).setAttribute("SPRING_SECURITY_LAST_USERNAME", request.getParameter("username"))
        super.onAuthenticationFailure(request, response, exception)
    }

}