package org.medcare.views.filter;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.medcare.views.UserBean;

import java.io.IOException;

@WebFilter("/app/*") // Redundant with web.xml but good practice
public class LoginFilter implements Filter {

    @Inject
    private UserBean userBean;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // If UserBean is null or user is not logged in, redirect to login page.
        if (userBean == null || !userBean.isLoggedIn()) {
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
        } else {
            // User is logged in, allow access to the requested page.
            chain.doFilter(request, response);
        }
    }

    // Other filter methods (init, destroy) can be left empty
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}