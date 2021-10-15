#!/bin/bash

function parse_yaml {
   local prefix=$2
   local s='[[:space:]]*' w='[a-zA-Z0-9_\.]*' fs=$(echo @|tr @ '\034')
   sed -ne "s|^\($s\):|\1|" \
        -e "s|^\($s\)\($w\)$s:$s[\"']\(.*\)[\"']$s\$|\1$fs\2$fs\3|p" \
        -e "s|^\($s\)\($w\)$s:$s\(.*\)$s\$|\1$fs\2$fs\3|p" $1 |
   awk -F$fs '{
      indent = length($1)/2;
      vname[indent] = $2;
      for (i in vname) {if (i > indent) {delete vname[i]}}
      if (length($3) > 0) {
         vn=""; for (i=0; i<indent; i++) {vn=(vn)(vname[i])("_")}
         tmp=sprintf("%s%s%s", "'$prefix'",vn, $2);
         gsub(/\./, "_", tmp);
         printf("%s=\"%s\"\n", tmp, $3);
      }
   }'
}

function load_jars {
   LACAST_LIBS="${lacast_libs_path}"
   LACAST_CLASSPATH=

   for JAR in "${LACAST_LIBS}"/*.jar
   do
      LACAST_CLASSPATH="${LACAST_CLASSPATH}:${JAR}"
   done

   printf "LACAST_LIBS=%s\n" ${LACAST_LIBS}
   printf "LACAST_CLASSPATH=%s" ${LACAST_CLASSPATH}
}

#parse_yaml "$@"
