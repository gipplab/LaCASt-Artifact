#!/bin/bash

source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

usage() {
    echo "You must specify the CAS you want to translate to.
The following arguments are possible"
    echo ""
    echo "./lacast-eval-symbolic.sh"
    printf "\t--mathematica\tFor translations to Mathematica\n"
    printf "\t-mm\t\tAlternative for --mathematica\n"
    printf "\t--maple\t\tFor translations to Maple\n"
    printf "\t-ma\t\tAlternative for --maple\n\n"
    echo "To change the test setup, update config/symbolic_tests.properties."
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
      else
        export LD_LIBRARY_PATH="${lacast_cas_Mathematica_native_library_path}"
      fi
      mkdir -p "./dlmf/results-generated/MathematicaSymbolic"
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
      else
        export LD_LIBRARY_PATH="${lacast_cas_Maple_native_library_path}"
        export MAPLE="${lacast_cas_Maple_install_path}"
      fi
      mkdir -p "./dlmf/results-generated/MapleSymbolic"
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

eval java -Xmx15g -Xss100M -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.evaluation.core.symbolic.SymbolicEvaluator "${ARG}"
