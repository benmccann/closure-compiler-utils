// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closuresample.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.benmccann.closure.ClosureCompilerInputGenerator;
import com.benmccann.closure.ClosureDevelopmentServlet;
import com.benmccann.closure.ClosureFileUtils;
import com.benmccann.closure.JsDepsGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.javascript.jscomp.CompilerOptions;


/**
 * @author Ben McCann (benmccann.com)
 */
public class DynamicCompilationModule extends AbstractModule {

  private static final File CLOSURE_LIBRARY
      = ClosureFileUtils.getClosureLibraryDirectoryFromEnvironmentVariable("CLOSURE_LIBRARY");

  private static final File JS_DIR = getJsDir();

  @Override
  protected void configure() {
    // This deps file needs to be regenerated when the provides/requires are changed
    // We've set it here to be regenerated with each server start
    // You could also generate it on each page load or according to other strategies as preferred
    File depsFile = new File(JS_DIR, "deps.js");
    new JsDepsGenerator(CLOSURE_LIBRARY).regenerateDepsJsFile(JS_DIR, depsFile);
  }
 
  @Provides
  ClosureDevelopmentServlet provideClosureDevelopmentServlet() throws IOException {
    File closureBasePath = ClosureFileUtils.getClosureBaseDotJsFile(CLOSURE_LIBRARY);

    List<File> depsPaths = ImmutableList.of(
        ClosureFileUtils.getClosureDepsFile(CLOSURE_LIBRARY),
        new File(JS_DIR, "deps.js"));

    List<String> entryPoints = ImmutableList.of("bjm.PageManager");

    ClosureCompilerInputGenerator argsGenerator = new ClosureCompilerInputGenerator(
        closureBasePath, depsPaths, entryPoints);
    List<File> inputFiles = argsGenerator.getInputFiles();
    List<File> externFiles = Lists.newArrayList();

    CompilerOptions compilerOptions = new CompilerOptions();
    compilerOptions.closurePass = true;
    compilerOptions.generateExports = true;
    compilerOptions.prettyPrint = true;

    ClosureCompilerInputGenerator.turnOnAllErrorChecking(compilerOptions);

    return new ClosureDevelopmentServlet(inputFiles, externFiles, entryPoints, compilerOptions);
  }

  private static File getJsDir() {
    try {
      return new File(new URL(SampleServer.getBaseUrl() + "../js").getFile());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
