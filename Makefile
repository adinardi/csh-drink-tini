TINICODE = ../tini1.02g/
TINICONVLINE = java -classpath ../tini1.02g/bin/tini.jar BuildDependency -d ../tini1.02g/bin/tini.db -add OneWireContainer05 -x ../tini1.02g/bin/owapi_dep.txt -p ../tini1.02g/bin/owapi_dependencies_TINI.jar 

compile:
	javac -target 1.1 -source 1.3 -bootclasspath ../tini1.02g/bin/tiniclasses.jar -classpath ../tini1.02g/bin/owapi_dependencies_TINI.jar *.java

lightshow: compile
	java -classpath tini1.02g/bin/tini.jar BuildDependency -f OneWireCommands.class -f Tester.class -f OneWireLightShow.class -d tini1.02g/bin/tini.db -o Tester.tini -add OneWireContainer05 -x tini1.02g/bin/owapi_dep.txt -p tini1.02g/bin/owapi_dependencies_TINI.jar

commlinktest: compile
	$(TINICONVLINE) -f OneWireCommands.class -f CommLink.class -f IncomingLink.class -f OutgoingLink.class -o CommLink.tini 
clean: 
	rm *.class
