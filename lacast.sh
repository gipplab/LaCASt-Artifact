#!/bin/sh

LACAST_LIBS="./libs"
LACAST_BIN="./bin"
LACAST_CLASSPATH=

for JAR in "${LACAST_LIBS}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

for JAR in "${LACAST_BIN}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

eval java -cp "$LACAST_CLASSPATH:./latex-to-cas-translator.jar" gov.nist.drmf.interpreter.cas.SemanticToCASInterpreter "$@"
