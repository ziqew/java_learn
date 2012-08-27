package org.javalearn.guicelearn.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.javalearn.guicelearn.module.MyServletModule;
import org.javalearn.guicelearn.module.ServiceModule;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午9:05
 * To change this template use File | Settings | File Templates.
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new MyServletModule());
    }
}
