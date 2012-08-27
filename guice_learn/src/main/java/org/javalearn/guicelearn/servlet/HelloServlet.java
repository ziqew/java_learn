package org.javalearn.guicelearn.servlet;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.javalearn.guicelearn.web.HelloService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午9:07
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class HelloServlet extends HttpServlet {

    @Inject
    private Injector inj;
    //private final Provider<Integer> userIdProvider;


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        inj.getInstance(HelloService.class).execute();
    }
}
