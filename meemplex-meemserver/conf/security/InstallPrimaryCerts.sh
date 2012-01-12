#!/bin/sh
 
export CLASSPATH=/home/smart/Maji/lib/bcprov.jar:identity.jar
export JAVA_HOME=/home/smart/Maji/jre
 
clear
if [ -f identity.jar ]
then
   $JAVA_HOME/bin/java -cp $CLASSPATH -Dorg.openmaji.implementation.server.servertype=primary org.openmaji.implementation.server.security.auth.AuthorisationTool
else
   echo "Cannot locate identity.jar"
   echo
   echo "This must be downloaded from https://secure.majitek.com and placed in this directory before running this script"
fi
