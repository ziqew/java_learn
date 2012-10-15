package com.ziqewgww.config;

import com.google.inject.servlet.ServletModule;
import com.ziqewgww.web.ServServlet;

public class MyServletModule extends ServletModule {
	  @Override protected void configureServlets() {
		    serve("/serv").with(ServServlet.class);
		  }
		}