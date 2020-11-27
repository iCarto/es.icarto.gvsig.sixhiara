#!/bin/bash

# set -e: stops the script on error
# set -u: stops the script on unset variables
# set -o pipefail:  fail the whole pipeline on first error
set -euo pipefail

this_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" > /dev/null && pwd)"

function gvsigmvn() {
    JAVA_HOME=~/bin/development/jdk1.8.0_102 mvn -Danimal.sniffer.skip=true -Dsource.skip=true -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -DskipTests -Dgvsig.skip.downloadPluginTemplates=true install
}

GVSIG2_PLUGINS_FOLDER=~/development/gvsig2-plugins/

cd "${GVSIG2_PLUGINS_FOLDER}/es.icarto.gvsig.commons"
gvsigmvn
cd "${GVSIG2_PLUGINS_FOLDER}/extDBConnection"
gvsigmvn
cd "${GVSIG2_PLUGINS_FOLDER}/navtable"
gvsigmvn
cd "${GVSIG2_PLUGINS_FOLDER}/navtableforms"
gvsigmvn
cd "${GVSIG2_PLUGINS_FOLDER}/extCopyFeatures"
gvsigmvn
cd "${GVSIG2_PLUGINS_FOLDER}/extELLE"
gvsigmvn
cd "${this_dir}"
gvsigmvn
