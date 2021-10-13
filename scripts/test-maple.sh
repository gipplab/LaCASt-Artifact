#!/bin/bash

LACAST_LIBS="./libs"
LACAST_CLASSPATH=

for JAR in "${LACAST_LIBS}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

eval java -cp "$LACAST_CLASSPATH" -Xss50M gov.nist.drmf.interpreter.maple.MapleConnectorSimpleTester
