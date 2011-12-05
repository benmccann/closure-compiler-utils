// Copyright 2011 Benjamin McCann. All Rights Reserved.

package com.benmccann.closure;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.ErrorManager;
import com.google.javascript.jscomp.LoggerErrorManager;
import com.google.javascript.jscomp.deps.DependencyInfo;
import com.google.javascript.jscomp.deps.DepsFileParser;

/**
 * @author Ben McCann (benmccann.com)
 */
public class ClosureCompilerInputGenerator {

  private static final Logger LOG =
      Logger.getLogger(ClosureCompilerInputGenerator.class.getName());
  
  private static final long serialVersionUID = 1L;

  private final File closureBaseFile;
  private final List<File> depsFiles;
  private final List<String> entryPoints;

  /**
   * @param closureBaseFile Closure's base.js
   * @param depsFiles Closure's deps.js and any deps.js from your own project
   * @param entryPoints namespace for the entry point to your JS
   */
  public ClosureCompilerInputGenerator(
      File closureBaseFile,
      List<File> depsFiles,
      List<String> entryPoints) {
    this.closureBaseFile = closureBaseFile;
    this.depsFiles = depsFiles;
    this.entryPoints = entryPoints;
  }

  public List<File> getInputFiles() throws IOException {
    Map<String, DependencyInfo> depMap = getDependencyMap();
    
    // Two unique requires can both be associated with the same file so this
    // must be a set to inorder to dedupe. E.g. goog.fx.Transition and
    // goog.fx.Transition.EventType
    List<File> files = Lists.newArrayList();
    files.add(closureBaseFile);
    files.addAll(depsFiles);

    // base.js needs to be the very first input file added to the args.
    Set<File> userFiles = Sets.newHashSet();
    Set<String> requires = Sets.newHashSet();
    Queue<String> unprocessedRequires = new LinkedList<String>();
    unprocessedRequires.addAll(entryPoints);
    while (!unprocessedRequires.isEmpty()) {
      String clazz = unprocessedRequires.remove();
      DependencyInfo depInfo = depMap.get(clazz);
      requires.add(clazz);
      for (String require : depInfo.getRequires()) {
        if (!requires.contains(require)) {
          unprocessedRequires.add(require);
        }
      }
    }

    File closureBaseDirectory = closureBaseFile.getParentFile();
    for (String require : requires) {
      userFiles.add(new File(closureBaseDirectory.getAbsolutePath()
          + "/" + depMap.get(require).getPathRelativeToClosureBase()));
    }

    files.addAll(userFiles);
    
    return files;
  }

  private Map<String, DependencyInfo> getDependencyMap() throws IOException {    
    ErrorManager errorManager = new LoggerErrorManager(LOG);
    DepsFileParser parser = new DepsFileParser(errorManager);
    Map<String, DependencyInfo> map = Maps.newHashMap();
    for (File depsFile : depsFiles) {
      addToMap(map, parser.parseFile(depsFile.getAbsolutePath()));
    }
    return map;
  }
  
  /**
   * Helper function for {@link getDependencyMap}.
   */
  private void addToMap(Map<String, DependencyInfo> map, List<DependencyInfo> infos) {
    for (DependencyInfo info : infos) {
      for (String provide : info.getProvides()) {
        if (map.containsKey(provide)) {
          throw new RuntimeException("Duplicate provide: " + provide);
        }
        map.put(provide, info);        
      }
    }
  }

  public static void turnOnAllErrorChecking(CompilerOptions options) {
    options.setWarningLevel(DiagnosticGroups.ACCESS_CONTROLS, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.AMBIGUOUS_FUNCTION_DECL, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CHECK_PROVIDES, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CHECK_REGEXP, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CHECK_USELESS_CODE, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CONST, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.CONSTANT_PROPERTY, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.DEBUGGER_STATEMENT_PRESENT, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.DEPRECATED, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.DUPLICATE_VARS, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.ES5_STRICT, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.EXTERNS_VALIDATION, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.FILEOVERVIEW_JSDOC, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.INTERNET_EXPLORER_CHECKS, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.INVALID_CASTS, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.MISSING_PROPERTIES, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.NON_STANDARD_JSDOC, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.STRICT_MODULE_DEP_CHECK, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.TWEAKS, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.TYPE_INVALIDATION, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.UNDEFINED_VARIABLES, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.UNKNOWN_DEFINES, CheckLevel.ERROR);
    options.setWarningLevel(DiagnosticGroups.VISIBILITY, CheckLevel.ERROR);    
    
    // These options are set the old way until the Closure team migrates them to Diagnostic Groups
    options.aggressiveVarCheck = CheckLevel.ERROR;
    options.checkCaja = true;
    options.checkControlStructures = true;
    options.checkDuplicateMessages = true;
    options.checkFunctions = CheckLevel.ERROR;
    options.checkGlobalNamesLevel = CheckLevel.ERROR;
    options.checkMethods = CheckLevel.ERROR;
    options.checkMissingReturn = CheckLevel.ERROR;
    options.checkRequires = CheckLevel.ERROR;
    options.checkSuspiciousCode = true;
    options.checkSymbols = true;
    options.checkTypedPropertyCalls = true;
    options.checkUnreachableCode = CheckLevel.ERROR;

    // TODO: turn to error when Closure library is fixed
    // http://code.google.com/p/closure-library/issues/detail?id=400
    options.reportMissingOverride = CheckLevel.WARNING;
  }

}
