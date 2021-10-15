#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

eval java -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.cas.SemanticToCASInterpreter "$@"
