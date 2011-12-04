// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closure;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.deps.SortedDependencies.CircularDependencyException;
import com.google.javascript.jscomp.deps.SortedDependencies.MissingProvideException;

/**
 * Recompiles your JS code using the Closure compiler on each page refresh.
 * plovr is an alternative project which does the same and is more mature, but
 * requires starting a separate server.
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

  private final List<File> inputFiles;
  private final List<File> externFiles;
  private final List<String> entryPoints;
  private final CompilerOptions compilerOptions;

  public ClosureDevelopmentServlet(
      List<File> inputFiles,
      List<File> externFiles,
      List<String> entryPoints,
      CompilerOptions compilerOptions) {
    this.inputFiles = inputFiles;
    this.externFiles = externFiles;
    this.entryPoints = entryPoints;
    this.compilerOptions = compilerOptions;
  }
  
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/javascript;charset=UTF-8");
    OutputStream out = response.getOutputStream(); 
    try {
      compile(response.getOutputStream());
    } catch (CircularDependencyException e) {
      throw new ServletException(e);
    } catch (MissingProvideException e) {
      throw new ServletException(e);
    }
    out.close();
  }

  private void compile(OutputStream outputStream)
      throws IOException, CircularDependencyException, MissingProvideException {
    PrintStream printStream = new PrintStream(outputStream);
    Compiler compiler = new Compiler(System.err);

    List<JSSourceFile> inputs = filesToJsSourceFiles(inputFiles);
    List<JSSourceFile> externs = CommandLineRunner.getDefaultExterns(); 
    externs.addAll(filesToJsSourceFiles(externFiles));

    compilerOptions.setManageClosureDependencies(entryPoints);
    
    Result result = compiler.compile(externs, inputs, compilerOptions);

    if (result.success) {
      printStream.println(compiler.toSource());
    } else {
      for (JSError error : result.errors) {
        printStream.println(error.toString());
      }
      for (JSError warning : result.warnings) {
        printStream.println(warning.toString());
      }
    }
  }

  private List<JSSourceFile> filesToJsSourceFiles(List<File> inputFiles) {
    List<JSSourceFile> inputs = Lists.newArrayList();
    for (File file : inputFiles) {
      inputs.add(JSSourceFile.fromFile(file));       
    }
    return inputs;
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
