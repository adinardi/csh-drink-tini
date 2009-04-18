PATH=/usr/bin:/opt/sun-jdk-1.5.0.06/bin/

#TINICODE = ../tini1.02g/
#TINICONVLINE = java -classpath ../tini1.02g/bin/tini.jar BuildDependency -d ../tini1.02g/bin/tini.db -add OneWireContainer05 -add OneWireContainer28 -add OneWireMonitor -x ../tini1.02g/bin/owapi_dep.txt -p ../tini1.02g/bin/owapi_dependencies_TINI.jar 

# BDM & SNACK
TINICODE = ../tini1.02h/
TINICONVLINE = java -classpath ../tini1.02h/bin/tini.jar BuildDependency -d ../tini1.02h/bin/tini.db -add OneWireContainer05 -add OneWireContainer28 -add OneWireMonitor -x ../tini1.02h/bin/owapi_dep.txt -p ../tini1.02h/bin/owapi_dependencies_TINI.jar 

# LDM
#TINICODE = ../tini1.17/
#TINICONVLINE = java -classpath ../tini1.17/bin/tini.jar BuildDependency -d ../tini1.17/bin/tini.db -add OneWireContainer05 -add OneWireContainer28 -add OneWireMonitor -x ../tini1.17/bin/owapi_dep.txt -p ../tini1.17/bin/owapi_dependencies_TINI.jar 


default: commlinktest

compile:
	javac -target 1.1 -source 1.3 -bootclasspath $(TINICODE)bin/tiniclasses.jar -classpath $(TINICODE)bin/owapi_dependencies_TINI.jar *.java

lightshow: compile
	$(TINICONVLINE) -f CollectGarbage.class -f OneWireCommands.class -f OneWireLights.class -f SlotMonitorListener.class -f CommLink.class -f IncomingLink.class -f OutgoingLink.class -f ConfigMgr.class -f Tester.class -f OneWireLightShow.class -m Tester -o Tester.tini 
	
switcher: compile
	$(TINICONVLINE) -f SwitchChanger.class -m SwitchChanger -o SwitchChanger.tini

commlinktest: compile
	$(TINICONVLINE) -f CollectGarbage.class -f OneWireCommands.class -f OneWireLights.class -f SlotMonitorListener.class -f CommLink.class -f IncomingLink.class -f OutgoingLink.class -f ConfigMgr.class -f Starter.class -f TempWorker.class -m Starter -o Starter.tini 
clean: 
	rm *.class *.tini

javadoc:
	javadoc -private -d docs -verbose *
	#rm -R /users/u12/adinardi/public_html/littledrink/*
	cp -Rv docs /users/u12/adinardi/public_html/littledrink
