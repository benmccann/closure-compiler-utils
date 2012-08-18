Closure Compiler Utils
======================

Utilities for calling the [Google Closure Compiler](http://code.google.com/p/closure-compiler/) to optimize and detect errors in JavaScript code.

Note
------------
I am currently not actively supporting this library in favor of the [Play Framework Plovr Plugin](https://github.com/benmccann/play-plovr-plugin).

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
