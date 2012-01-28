// Copyright 2012 Benjamin McCann. All Rights Reserved.

package com.benmccann.closuresample.server;

import java.net.URL;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import com.benmccann.closure.ClosureDevelopmentServlet;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;


/**
 * @author benmccann.com
 */
public class SampleServer {

  protected static final int PORT = 8080;

  protected final GuiceListener guiceListener;
  protected final ClosureDevelopmentServlet closureDebugServlet;

  @Inject
  public SampleServer(
      GuiceListener guiceListener,
      ClosureDevelopmentServlet closureDebugServlet) {
    this.guiceListener = guiceListener;
    this.closureDebugServlet = closureDebugServlet;
  }
  
  protected Server createNewServer() {
    Server server = new Server();

    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setPort(PORT);
    server.addConnector(connector);

    WebAppContext webApp = new WebAppContext(getBaseUrl(), "/");
    webApp.addEventListener(guiceListener);

    webApp.setServletHandler(createServletHandler());
    webApp.setErrorHandler(createErrorHandler());
    server.setHandler(webApp);

    return server;
  }

  protected ErrorHandler createErrorHandler() {
    ErrorPageErrorHandler errorHandler = new ErrorPageErrorHandler();
    errorHandler.addErrorPage(500, "/error.html");
    return errorHandler;
  }
  
  protected ServletHandler createServletHandler() {
    ServletHandler servletHandler = new ServletHandler();

    FilterHolder guiceFilterHolder = createGuiceFilterHolder();
    servletHandler.addFilter(guiceFilterHolder, createFilterMapping("/*", guiceFilterHolder));

    FilterHolder strutsFilterHolder = createStrutsFilterHolder();
    servletHandler.addFilter(strutsFilterHolder, createFilterMapping("/*", strutsFilterHolder));    

    ServletHolder closureServletHolder = new ServletHolder(closureDebugServlet);
    servletHandler.addServletWithMapping(closureServletHolder, "/js/compiled.js");
    
    return servletHandler;
  }

  protected FilterHolder createGuiceFilterHolder() {
    FilterHolder filterHolder = new FilterHolder(GuiceFilter.class);
    filterHolder.setName("guice");
    return filterHolder;
  }
  
  protected FilterHolder createStrutsFilterHolder() {
    FilterHolder filterHolder = new FilterHolder(StrutsPrepareAndExecuteFilter.class);
    filterHolder.setName("struts2");
    return filterHolder;
  }

  protected FilterMapping createFilterMapping(String pathSpec, FilterHolder filterHolder) {
    FilterMapping filterMapping = new FilterMapping();
    filterMapping.setPathSpec(pathSpec);
    filterMapping.setFilterName(filterHolder.getName());
    return filterMapping;
  }
  
  public void run() throws Exception {
    Server server = createNewServer();
    server.start();
    server.join();
  }

  public static String getBaseUrl() {
    URL webInfUrl = SampleServer.class.getClassLoader().getResource("WEB-INF");
    String webInfUrlString = webInfUrl.toExternalForm();
    return webInfUrlString.substring(0, webInfUrlString.lastIndexOf('/') + 1);
  }

  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(
        new FrontendModule(),
        new DynamicCompilationModule());
    SampleServer server = injector.getInstance(SampleServer.class);
    server.run();
  }

}
