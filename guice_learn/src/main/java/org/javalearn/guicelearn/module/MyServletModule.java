package org.javalearn.guicelearn.module;

import com.google.inject.servlet.ServletModule;
import org.javalearn.guicelearn.servlet.AccountServlet;
import org.javalearn.guicelearn.servlet.AjaxServlet;
import org.javalearn.guicelearn.servlet.HelloServlet;
import org.javalearn.guicelearn.servlet.SecutiryFilter;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午10:19
 * To change this template use File | Settings | File Templates.
 */
public class MyServletModule extends ServletModule{

    @Override
    protected void configureServlets() {
        filter("/*").through(SecutiryFilter.class);
        serve("/hello").with(HelloServlet.class);
        serve("/account").with(AccountServlet.class);
        serveRegex("(.)*ajax(.)*").with(AjaxServlet.class);
    }
}
