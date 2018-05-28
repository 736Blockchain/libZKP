#!/bin/bash
set -e

scriptdir=$(cd $(dirname $0); pwd -P)
cd $scriptdir
echo "当前目录"
pwd

(cd $scriptdir/;
mvn exec:java -Dexec.mainClass=tutorial.test -Dexec.cleanupDaemonThreads=false)
echo
