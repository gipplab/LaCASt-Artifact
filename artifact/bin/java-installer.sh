#!/bin/bash

set -e

if [ "$EUID" -ne 0 ]
then
  echo "This script requires root. Run with sudo java-installer.sh"
  exit
fi

mkdir -p /opt/java

tar xfvz openjdk-11+28_linux-x64_bin.tar.gz --directory /opt/java/

update-alternatives --install /usr/bin/java java /opt/java/jdk-11/bin/java 100
update-alternatives --set java /opt/java/jdk-11/bin/java

echo '
JAVA_HOME="/opt/java/jdk-11"
PATH=$PATH:$JAVA_HOME/bin
export JAVA_HOME
export JRE_HOME
export PATH
' &>> ~/.profile

source ~/.profile
