#!/bin/sh
javac -target 1.1 -source 1.3 -bootclasspath tini1.02g/bin/tiniclasses.jar -classpath tini1.02g/bin/owapi_dependencies_TINI.jar *.java
#javac -target 1.1 -source 1.3 -bootclasspath tini1.02g/bin/tiniclasses.jar -classpath tini1.02g/bin/owapi_dependencies_TINI.jar Tester.java 

java -classpath tini1.02g/bin/tini.jar BuildDependency -f OneWireCommands.class -f Tester.class -f OneWireLightShow.class -d tini1.02g/bin/tini.db -o Tester.tini -add OneWireContainer05 -x tini1.02g/bin/owapi_dep.txt -p tini1.02g/bin/owapi_dependencies_TINI.jar
    
