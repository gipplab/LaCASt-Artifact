#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

export LD_LIBRARY_PATH="${lacast_cas_Maple_native_library_path}"
export MAPLE="${lacast_cas_Maple_install_path}"

eval java -cp "$LACAST_CLASSPATH" -Xss50M gov.nist.drmf.interpreter.maple.MapleConnectorSimpleTester
