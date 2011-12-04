// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closure;

import java.io.File;

import com.google.common.base.Strings;

/**
 * @author Ben McCann (benmccann.com)
 */
public class ClosureFileUtils {

  public static File getClosureLibraryDirectoryFromEnvironmentVariable(String envVariableName) {
    String path = System.getenv("CLOSURE_LIBRARY");
    if (Strings.isNullOrEmpty(path)) {
      throw new IllegalArgumentException("The environment variable " + envVariableName
          + " must be defined to point at the Closure Library.");
    }
    File file = new File(path);
    if (!file.exists() || !file.isDirectory()) {
      throw new IllegalArgumentException(envVariableName + " does not point to a valid directory.");
    }
    return file;
  }
  
  /**
   * @param closureLibrary the Closure Library directory
   * @return the closure/goog/base.js file that comes with the Closure Library
   */
  public static File getClosureBaseDotJsFile(File closureLibrary) {
    return getFileRelativeToGoogDirectory(closureLibrary, "goog" + File.separator + "base.js");
  }

  /**
   * @param closureLibrary the Closure Library directory
   * @return the closure/goog/deps.js file that comes with the Closure Library
   */
  public static File getClosureDepsFile(File closureLibrary) {
    return getFileRelativeToGoogDirectory(closureLibrary, "goog" + File.separator + "deps.js");
  }

  /**
   * @param closureLibrary the Closure Library directory
   * @return the closure/bin/build/depswriter.py file that comes with the Closure Library
   */
  public static File getDepsWriterDotPy(File closureLibrary) {
    return getFileRelativeToGoogDirectory(closureLibrary,
        "bin" + File.separator + "build" + File.separator + "depswriter.py");
  }

  /**
   * @param closureLibrary the Closure Library directory
   * @param path the file path relative to the closure-library/closure directory
   * @return the requested file
   */
  private static File getFileRelativeToGoogDirectory(File closureLibrary, String path) {
    File baseDotJs = new File(new StringBuilder(closureLibrary.getAbsolutePath())
        .append(File.separator).append("closure")
        .append(File.separator).append(path).toString());
    if (!baseDotJs.exists()) {
      throw new IllegalArgumentException("Could not find closure-library/closure"
          + File.separator + path + " in given directory.");
    }
    return baseDotJs;
  }
  
}
