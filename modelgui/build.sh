#!/bin/bash
# -----------------------------------------------------------------------------
#
#
#
# build.sh - Unix Build Script for ModelGUI
#
# $Id$
# -----------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
   
ANT_HOME=./modelgui-src/lib/ant
ANT_LAUNCHER=ant-launcher-1.7.jar
ANT=ant-1.7.jar

CP=$ANT_HOME

if [ "$JAVA_HOME" = "" ]; then
    echo $JAVA_HOME
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi


# ----- Execute The Requested Build -------------------------------------------

TARGET=$1;
if [ $# != 0 ]; then
  shift 1
fi

# $JAVA_HOME/bin/java -version

"$JAVA_HOME"/bin/java -classpath "$CP" -jar $ANT_HOME/$ANT_LAUNCHER -emacs -Dant.home=$ANT_HOME $TARGET -Dargs="$*"