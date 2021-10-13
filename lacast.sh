#!/bin/sh

LACAST_LIBS="./libs"
LACAST_CLASSPATH=

for JAR in "${LACAST_LIBS}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

eval java -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.cas.SemanticToCASInterpreter "$@"
