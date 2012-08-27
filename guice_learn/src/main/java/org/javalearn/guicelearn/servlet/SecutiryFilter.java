package org.javalearn.guicelearn.servlet;

import com.google.inject.Singleton;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午9:09
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SecutiryFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request,response);
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
