package org.javalearn.guicelearn.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.javalearn.guicelearn.service.AccountService;
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
 * Time: 上午10:26
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class AccountServlet extends HttpServlet {

    private AccountService accountService;

    @Inject
    public AccountServlet(AccountService accountService){
        this.accountService=accountService;

    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        resp.getWriter().append(accountService.getName());
    }
}
