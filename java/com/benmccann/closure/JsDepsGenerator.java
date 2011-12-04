// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Ben McCann (benmccann.com)
 */
public class JsDepsGenerator {

  private final File closureLibraryDirectory;
  private final File baseDotJs;

  /**
   * @param closureLibraryDirectory the Closure library directory
   */
  public JsDepsGenerator(File closureLibraryDirectory) {
    this.closureLibraryDirectory = closureLibraryDirectory;
    this.baseDotJs = ClosureFileUtils.getClosureBaseDotJsFile(closureLibraryDirectory);
  }
  
  private static List<String> getPathList(File file) {
    List<String> list = Lists.newArrayList();
    try {
      file = file.getCanonicalFile().getParentFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    while (file != null) {
      list.add(0, file.getName());
      file = file.getParentFile();
    }
    return list;
  }

  private String getPathRelativeToBaseDotJs(File file) {
    List<String> basePath = getPathList(baseDotJs);
    List<String> filePath = getPathList(file);

    // find the common path
    int i = 0;
    for (; i < basePath.size() && i < filePath.size() && basePath.get(i).equals(filePath.get(i)); i++) {;
    }

    int j = i;

    // walk up the tree until you get to the common part
    StringBuilder relativePath = new StringBuilder();
    for (; i < basePath.size(); i++) {
      relativePath.append("..").append(File.separator);
    }

    // walk down to the second file
    for (; j < filePath.size(); j++) {
      relativePath.append(filePath.get(j)).append(File.separator);
    }

    return relativePath.append(file.getName()).toString();
  }

  public void regenerateDepsJsFile(File jsRoot, File outputFile) {
    try {
      outputFile.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String rootWithPrefix = jsRoot.getAbsolutePath() + " " + getPathRelativeToBaseDotJs(jsRoot);
    try {
      File depsWriter = ClosureFileUtils.getDepsWriterDotPy(closureLibraryDirectory);
      Process process = Runtime.getRuntime().exec(new String[] {
          depsWriter.getAbsolutePath(), "--root_with_prefix", rootWithPrefix });
      pipe(process.getErrorStream(), System.err);
      pipe(process.getInputStream(), new FileOutputStream(outputFile));
      if (process.waitFor() != 0) {
        throw new IllegalStateException("depswriter.py terminated abnormally");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static void pipe(final InputStream src, final OutputStream dest) {
    new Thread(new Runnable() {
      public void run() {
        try {
          byte[] buffer = new byte[1024];
          for (int n = 0; n != -1; n = src.read(buffer)) {
            dest.write(buffer, 0, n);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }
  
}
