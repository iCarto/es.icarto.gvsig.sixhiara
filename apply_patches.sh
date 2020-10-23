#!/bin/bash

set -e

BASE_PATCHES_PATH="${1}"
if [ -z "${BASE_PATCHES_PATH}" ]; then
    echo "ERROR. Introduzca el path base donde aplicar los cambios"
    echo "Probablmente SVN REPO/target/product"
    exit 64
fi

### Hacked libs. Remove when fixed upstream

# https://redmine.gvsig.net/redmine/issues/5512
cp portable/common/patches/org.gvsig.fmap.dal.impl-2.0.304.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.app.mainplugin/lib/org.gvsig.fmap.dal.impl-2.0.304.jar

# ?
cp portable/common/patches/org.gvsig.datalocator.app.mainplugin-2.0.304.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.datalocator.app.mainplugin/lib/org.gvsig.datalocator.app.mainplugin-2.0.304.jar

