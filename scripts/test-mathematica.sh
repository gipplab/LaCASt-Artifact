#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

export LD_LIBRARY_PATH="${lacast_cas_Mathematica_native_library_path}"

eval java -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.mathematica.MathematicaConnectorSimpleTester
