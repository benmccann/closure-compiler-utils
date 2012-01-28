// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closure;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benmccann.closure.CompilerInvoker.JavascriptCompilationException;
import com.google.javascript.jscomp.CompilerOptions;

/**
 * Recompiles your JS code using the Closure compiler on each page refresh.
 *
 * To invoke, request a JS file and create a servlet mapping for that file.
 * For example:
 *
 * <script src="/js/compiled.js"></script>
 *
 * <servlet-mapping>
 *   <servlet-name>closure-development-servlet</servlet-name>
 *   <url-pattern>/js/compiled.js</url-pattern>
 * </servlet-mapping>
 *
 * @author Ben McCann (benmccann.com)
 */
public class ClosureDevelopmentServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private final CompilerInvoker compileInvoker;

  public ClosureDevelopmentServlet(
      List<File> inputFiles,
      List<File> externFiles,
      List<String> entryPoints,
      CompilerOptions compilerOptions) {
    this.compileInvoker = new CompilerInvoker(
        inputFiles, externFiles, entryPoints, compilerOptions);
  }
  
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/javascript;charset=UTF-8");
    OutputStream out = response.getOutputStream(); 
    try {
      compileInvoker.compile(response.getOutputStream());
    } catch (JavascriptCompilationException e) {
      // the compilation errors will have been printed to the output stream
    }
    out.close();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Closure development Servlet";
  }

}
