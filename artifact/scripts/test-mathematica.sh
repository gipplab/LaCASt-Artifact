#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

if [[ -z "${lacast_cas_Mathematica_native_library_path}" ]]; then
	echo "ERROR: You want to use Mathematica for symbolic evaluation but did not specify a valid path in lacast.config.yaml"
	exit 1;
elif [[ ! -d "${lacast_cas_Mathematica_native_library_path}" ]]; then
	echo "ERROR: The specified library path in lacast.config.yaml for Mathematica does not exist (${lacast_cas_Mathematica_native_library_path})"
	exit 1;
elif [[ ! -f "${lacast_libs_path}/JLink.jar" ]]; then
	echo "Unable to find necessary JLink.jar in LaCASt's libs folder."
	echo "Copy it from your Mathematica install path. You probably find it under "
	echo "  ${lacast_cas_Mathematica_install_path}/SystemFiles/Links/JLink/JLink.jar"
	echo ""
	exit 1;
else
	export LD_LIBRARY_PATH="${lacast_cas_Mathematica_native_library_path}"
fi

eval java -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.mathematica.MathematicaConnectorSimpleTester
