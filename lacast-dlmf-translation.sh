#!/bin/bash

LACAST_LIBS="./libs"
LACAST_CLASSPATH=
CAS=
ARG=
IN="./dlmf/dlmf-formulae.txt"
OUT=
START=1
END=10000

usage() {
    echo "You must specify at least the CAS you want to translate to. The following arguments are possible"
    echo ""
    echo "./lacast-dlmf-translation.sh"
    printf "\t--mathematica\tFor translations to Mathematica\n"
    printf "\t-mm\t\tAlternative for --mathematica\n"
    printf "\t--maple\t\tFor translations to Maple\n"
    printf "\t-ma\t\tAlternative for --maple\n"
    printf "\t-i #1, --in #1\tThe path to the dlmf file (default: ./dlmf/dlmf-formulae.txt)\n"
    printf "\t-o #1, --out #1\tThe output path (default: ./dlmf/results-generated/<CAS>Translations/translations.txt)\n"
    printf "\t--startLine #1\tSets the first line to translate starting from 1 (inclusive lower boundary)\n"
    printf "\t--endLine #1\tSets the last line to translate (exclusive upper boundary)\n"
    printf "\t--min #1\tAlternative for --startLine #1\n"
    printf "\t--max #1\tAlternative for --endLine #1\n"
    echo ""
}

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
      shift
      ;;
    -i | --in)
      IN="$2"
      shift # past argument
      shift # past value
      ;;
    -o | --out)
      OUT="$2"
      shift # past argument
      shift # past value
      ;;
    --start | --startLine | --min)
      START="$2"
      shift # past argument
      shift # past value
      ;;
    --end | --endLine | --max)
      END="$2"
      shift # past argument
      shift # past value
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done

if [ -z "$CAS" ]
then
  usage
  exit 1
fi

if [ -z "$OUT" ]
then
  OUT="./dlmf/results-generated/${CAS}Translations/translations.txt"
fi

for JAR in "${LACAST_LIBS}"/*.jar
do
    LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
done

eval java -cp "$LACAST_CLASSPATH" gov.nist.drmf.interpreter.evaluation.core.SampleTranslator --in "${IN}" --out "${OUT}" --startLine "${START}" --endLine "${END}" "${ARG}"
