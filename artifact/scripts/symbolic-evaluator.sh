#!/usr/bin/env bash

# Setup everything, load config file and jars from libs folder
source ./scripts/parser.sh

eval $(parse_yaml lacast.config.yaml)
eval $(load_jars)

CONFIGFILEBASE="config/symbolic_tests-base.properties"
CONFIGFILE="config/symbolic_tests.properties"
SETFILES="config/together-lines.txt"

OUTPUTPATH="./dlmf/results-generated"

# Manual in case the user does not specify anything
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

      # In case of Mathematica, LD_LIBRARY_PATH must be set. So be sure the values are available
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
      shift
      ;;
    -ma | --ma | --maple)
      CAS="Maple"
      ARG="--maple"

      # In case of Maple, the LD_LIBRARY_PATH and MAPLE must be set. Make sure both are properly setup via lacast.config.yaml
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
      shift
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done

# If the CAS was not specified, stop here. The user must decide to use a CAS
if [[ -z "$CAS" ]]; then
  usage
  exit 1
fi

# create output path just in case it does not exist already
mkdir -p "${OUTPUTPATH}/${CAS}Symbolic"

# Setup delimiters to read config/together-lines.txt
NEWLINE=$'\n'
RESSTR="Results:$NEWLINE";

IFS=': '
while read line; do
  echo "Removing config file symbolic_tests.properties."
  rm -f $CONFIGFILE;

  echo "Generating new config file symbolic_tests.properties."
  read -ra ADDR <<< $line;

  echo "# This file was auto-generated by 'symbolic-evaluator.sh'. Update it if you want to use 'lacast-eval-symbolic.sh'" >> $CONFIGFILE;
  echo "" >> $CONFIGFILE
  cat $CONFIGFILEBASE >> $CONFIGFILE;
  
  # Specify outputs and ranges
  printf "\n" >> $CONFIGFILE;
  echo "output=${OUTPUTPATH}/${CAS}Symbolic/${ADDR[0]}-symbolic.txt" >> $CONFIGFILE;
  echo "missing_macro_output=${OUTPUTPATH}/${CAS}Symbolic/${ADDR[0]}-missing.txt" >> $CONFIGFILE;
  echo "subset_tests=${ADDR[1]}" >> $CONFIGFILE;

  echo "Done creating configuration file for ${ADDR[0]}"
  echo "Start processing..."
  java -Xmx8g -Xss200M -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.evaluation.core.symbolic.SymbolicEvaluator "${ARG}"
  # Store the result code of the process, in case something went wrong, we can easily see it in the summary afterward
  RESULTCODE=$?;
  echo "Done ${ADDR[0]}"
  RESSTR="${RESSTR}${ADDR[0]}: $RESULTCODE $NEWLINE"
done < $SETFILES

# Print little summary
echo "The following lists the exit codes of the performed symbolic evaluations."
echo "If an exit code was different to 0, something went wrong and the output was not generated for that chapter!"
echo ""
echo "$RESSTR"
exit 0
