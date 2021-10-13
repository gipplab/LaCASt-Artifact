#!/bin/bash

if [[ -z "${LD_LIBRARY_PATH}" ]]; then
    echo "ERROR: You did not set LD_LIBRARY_PATH. But this is necessary for both CAS: Maple and Mathematica"
    exit
fi

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
    -mm | --mathematica)
      CAS="Mathematica"
      ARG="--mathematica"
      shift
      ;;
    -ma | --maple)
      CAS="Maple"
      ARG="--maple"
      if [[ -z "${MAPLE}" ]]; then
      	echo "ERROR: In order to use Maple, you must set the 'MAPLE' environment variable!"
      	exit;
      fi
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

LACAST_LIBS="./libs"
LACAST_CLASSPATH=

for JAR in "${LACAST_LIBS}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

eval java -Xmx15g -Xss100M -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.evaluation.core.symbolic.SymbolicEvaluator "${ARG}"
