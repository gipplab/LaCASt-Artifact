#!/bin/sh

LACAST_BIN="./bin"
LACAST_LIBS="./libs"
LACAST_CLASSPATH=

for JAR in "${LACAST_LIBS}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

for JAR in "${LACAST_BIN}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

# set environment vars
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:/opt/maple2020/bin.X86_64_LINUX"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:/opt/Wolfram/SystemFiles/Links/JLink/SystemFiles/Libraries/Linux-x86-64/"
export MAPLE="/opt/maple2020"

eval java -Xmx15g -Xss100M -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.evaluation.core.numeric.NumericalEvaluator "$@"
