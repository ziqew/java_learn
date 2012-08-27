package org.javalearn.guicelearn.web;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午10:05
 * To change this template use File | Settings | File Templates.
 */
@RequestScoped
public class HelloServiceImpl implements HelloService{
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Inject
    public HelloServiceImpl(HttpServletRequest request, HttpServletResponse response) {
        super();
        this.request = request;
        this.response = response;
    }

    public void execute() throws IOException {
        response.getWriter().append("Hello "+request.getParameter("name"));
    }
}
