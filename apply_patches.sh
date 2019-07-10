#!/bin/bash

set -e

BASE_PATCHES_PATH="${1}"
if [ -z "${BASE_PATCHES_PATH}" ]; then
    echo "ERROR. Introduzca el path base donde aplicar los cambios"
    echo "Probablmente SVN REPO/target/product"
    exit 64
fi

### Hacked libs. Remove when fixed upstream
cp portable/common/patches/org.gvsig.fmap.dal.impl-2.0.157.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.app.mainplugin/lib/org.gvsig.fmap.dal.impl-2.0.157.jar
rm -f ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.gdal.app.ogr.mainplugin/lib/org.gvsig.gdal.prov.ogr-1.0.30.jar
cp portable/common/patches/org.gvsig.gdal.prov.ogr-1.0.32.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.gdal.app.ogr.mainplugin/lib/org.gvsig.gdal.prov.ogr-1.0.32.jar
cp portable/common/patches/org.gvsig.datalocator.app.mainplugin-2.0.157.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.datalocator.app.mainplugin/lib/org.gvsig.datalocator.app.mainplugin-2.0.157.jar
cp portable/common/patches/org.gvsig.fmap.dal.db.jdbc-2.0.157.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.app.mainplugin/lib/
rm -f ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.app.mainplugin/lib/postgresql-9.1-901.jdbc3.jar
rm -f ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.postgresql.app.mainplugin/lib/postgresql-9.1-901.jdbc3.jar
cp portable/common/patches/postgresql-42.2.6.jar ${BASE_PATCHES_PATH}/gvSIG/extensiones/org.gvsig.postgresql.app.mainplugin/lib/postgresql-42.2.6.jar
