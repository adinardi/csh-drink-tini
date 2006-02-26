TINICODE = ../tini1.02g/
TINICONVLINE = java -classpath ../tini1.02g/bin/tini.jar BuildDependency -d ../tini1.02g/bin/tini.db -add OneWireContainer05 -add OneWireContainer28 -add OneWireMonitor -x ../tini1.02g/bin/owapi_dep.txt -p ../tini1.02g/bin/owapi_dependencies_TINI.jar 


default: commlinktest

compile:
	javac -target 1.1 -source 1.3 -bootclasspath ../tini1.02g/bin/tiniclasses.jar -classpath ../tini1.02g/bin/owapi_dependencies_TINI.jar *.java

lightshow: compile
	java -classpath tini1.02g/bin/tini.jar BuildDependency -f OneWireCommands.class -f Tester.class -f OneWireLightShow.class -d tini1.02g/bin/tini.db -o Tester.tini -add OneWireContainer05 -x tini1.02g/bin/owapi_dep.txt -p tini1.02g/bin/owapi_dependencies_TINI.jar

commlinktest: compile
	$(TINICONVLINE) -f CollectGarbage.class -f OneWireCommands.class -f OneWireLights.class -f SlotMonitorListener.class -f CommLink.class -f IncomingLink.class -f OutgoingLink.class -f ConfigMgr.class -f Starter.class -f TempWorker.class -m Starter -o Starter.tini 
clean: 
	rm *.class *.tini

javadoc:
	javadoc -private -d docs -verbose *
	cp -R docs/ /users/u12/adinardi/public_html/littledrink
