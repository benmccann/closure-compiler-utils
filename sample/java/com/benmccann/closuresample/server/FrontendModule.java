// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closuresample.server;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import com.google.inject.struts2.Struts2GuicePluginModule;

/**
 * @author Ben McCann (benmccann.com)
 */
public class FrontendModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new Struts2GuicePluginModule());
    install(new ServletModule());
  }
  
}
