#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

usage() {
    echo "You must specify the CAS you want to translate to.
The following arguments are possible"
    echo ""
    echo "./lacast-eval-numeric.sh"
    printf "\t--mathematica\tFor translations to Mathematica\n"
    printf "\t-mm\t\tAlternative for --mathematica\n"
    printf "\t--maple\t\tFor translations to Maple\n"
    printf "\t-ma\t\tAlternative for --maple\n\n"
    echo "To change the test setup, update config/numerical_tests.properties."
}

CAS=
ARG=

while [[ $# -gt 0 ]]; do
  key="$1"
  case $key in
    -mm | --mm | --mathematica)
      CAS="Mathematica"
      ARG="--mathematica"
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
      mkdir -p "./dlmf/results-generated/MathematicaNumeric"
      shift
      ;;
    -ma | --ma | --maple)
      CAS="Maple"
      ARG="--maple"
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
      mkdir -p "./dlmf/results-generated/MapleNumeric"
      shift
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$CAS" ]]; then
  usage
  exit 1
fi

eval java -Xmx15g -Xss100M -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.evaluation.core.numeric.NumericalEvaluator "${ARG}"
