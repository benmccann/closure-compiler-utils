// Copyright 2012 Benjamin McCann. All Rights Reserved.

package com.benmccann.closuresample.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author Ben McCann (benmccann.com)
 */
public class GuiceListener extends GuiceServletContextListener {

  private final Injector injector;
  
  @Inject
  public GuiceListener(Injector injector) {
    this.injector = injector;
  }

  @Override
  public Injector getInjector() {
    return injector;
  }

}
