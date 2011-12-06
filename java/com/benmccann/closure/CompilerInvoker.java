// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closure;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;

/**
 * Invokes the Closure compiler.
 *
 * @author Ben McCann (benmccann.com)
 */
public class CompilerInvoker {

  private final List<File> inputFiles;
  private final List<File> externFiles;
  private final List<String> entryPoints;
  private final CompilerOptions compilerOptions;

  public CompilerInvoker(
      List<File> inputFiles,
      List<File> externFiles,
      List<String> entryPoints,
      CompilerOptions compilerOptions) {
    this.inputFiles = inputFiles;
    this.externFiles = externFiles;
    this.entryPoints = entryPoints;
    this.compilerOptions = compilerOptions;
  }

  public void compile(OutputStream outputStream)
      throws IOException, JavascriptCompilationException {
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
        printError(printStream, "ERROR",  error);
      }
      for (JSError warning : result.warnings) {
        printError(printStream, "WARNING", warning);
      }
      printStream.println();
      printStream.println(result.errors.length + " error(s), " + result.warnings.length + " warning(s)");
      throw new JavascriptCompilationException("Error during JavaScript compilation");
    }
  }

  private void printError(PrintStream printStream, String level, JSError error) {
    printStream.println((!Strings.isNullOrEmpty(error.sourceName) ? error.sourceName : "(unknown source)")
        + ":" + (error.lineNumber != -1 ? String.valueOf(error.lineNumber) : "(unknown line)")
        + ": " + level + " - " + error.description);
  }

  private List<JSSourceFile> filesToJsSourceFiles(List<File> inputFiles) {
    List<JSSourceFile> inputs = Lists.newArrayList();
    for (File file : inputFiles) {
      inputs.add(JSSourceFile.fromFile(file));       
    }
    return inputs;
  }

  public static class JavascriptCompilationException extends Exception {
    private static final long serialVersionUID = 1L;

    public JavascriptCompilationException(String message) {
      super(message);
    }
  }

}
