#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

if [[ -z "${lacast_cas_Maple_native_library_path}" || -z "${lacast_cas_Maple_install_path}" ]]; then
	echo "ERROR: You want to use Maple for symbolic evaluation but did not specify valid paths in lacast.config.yaml"
	exit 1;
elif [[ ! -d "${lacast_cas_Maple_native_library_path}" ]]; then
	echo "ERROR: The specified library path of Maple in lacast.config.yaml does not exist (${lacast_cas_Maple_native_library_path})"
	exit 1;
elif [[ ! -d "${lacast_cas_Maple_install_path}" ]]; then
	echo "ERROR: The specified install path of Maple in lacast.config.yaml does not exist (${lacast_cas_Maple_install_path})"
	exit 1;
elif [[ ! -f "${lacast_libs_path}/Maple.jar" || ! -f "${lacast_libs_path}/externalcall.jar" ]]; then
	echo "Unable to find necessary Maple.jar and externalcall.jar in LaCASt's libs folder."
	echo "Copy it from your Maple install path. You most likely find them in:"
	echo "  ${lacast_cas_Mapla_install_path}/Java"
	echo ""
	exit 1;
else
	export LD_LIBRARY_PATH="${lacast_cas_Maple_native_library_path}"
	export MAPLE="${lacast_cas_Maple_install_path}"
fi

eval java -cp "$LACAST_CLASSPATH" -Xss50M gov.nist.drmf.interpreter.maple.MapleConnectorSimpleTester
