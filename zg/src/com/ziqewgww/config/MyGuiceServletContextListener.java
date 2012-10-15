package com.ziqewgww.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class MyGuiceServletContextListener extends GuiceServletContextListener {

	  @Override protected Injector getInjector() {
	    return Guice.createInjector(
	        new MyServletModule(),
	        new BusinessLogicModule());
	  }
	}