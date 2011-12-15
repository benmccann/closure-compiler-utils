Closure Compiler Utils
======================

Utilities for calling the [Google Closure Compiler](http://code.google.com/p/closure-compiler/) to optimize and detect errors in JavaScript code.

Main classes
------------
**ClosureDevelopmentServlet:** dynamically recompiles your JS on each page load  
**JsDepsGenerator:** calls out to depswriter.py to generate a deps.js file  

Downloading
-----------
The compiled version is [available in the Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Ccom.benmccann).

License
-------
Licensed under the [Version 2.0 Apache Software License](http://www.apache.org/licenses/LICENSE-2.0.txt).

Alternatives
------------
**[Plovr](http://plovr.com/):** also dynamically recompiles your JS on each page load.  However, it requires a new server to be brought up.  This makes it a bit more difficult to use for Java projects since you must run two servers, but has the advantage that it can be used in any non-Java web project.  It also facilitates the process of splitting JavaScript code into modules, which this project does not attempt to address.  
See other projects on the [Closure Related Projects wiki](http://code.google.com/p/closure-compiler/wiki/RelatedProjects)  
