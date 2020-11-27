#!/bin/bash

# set -e: stops the script on error
# set -u: stops the script on unset variables
# set -o pipefail:  fail the whole pipeline on first error
set -euo pipefail

# Los plugins org.hortonmachine.gvsig.base y org.hortonmachine.gvsig.geopaparazzi deben
# estar dentro. Descargarlos a trav�s del administrador de complementos y meterlos
WINDOWS_GVSIG_APP=/var/tmp/gvSIG-desktop-2.5.1-3046-final-win-x86_64

VERSION=$(date +%y%m%d)_SIRHAN_Inventario
VERSION_SUL=$(date +%y%m%d)_SIRHAS_Inventario

echo "Compilando es.icarto.gvsig.sixhiara. Recuerda que si hay cambios en otros proyectos también hay que compilarlos"
bash compile.sh

while IFS='' read -r line || [[ -n "${line}" ]]; do
    if [[ ${line} == gvsig.product.folder.path* ]]; then
        gvsig_product_folder_path=$(echo "${line}" | cut -d '=' -f2)
    fi
done < ~/.gvsig-devel.properties

EXT="${gvsig_product_folder_path}/gvSIG/extensiones/"

rm -rf "/tmp/${VERSION}"
rm -rf "/tmp/${VERSION_SUL}"

cp -r "${WINDOWS_GVSIG_APP}" "/tmp/${VERSION}"

# org.gvsig.geoprocess.app.algorithm org.gvsig.geoprocess.app.mainplugin org.gvsig.geoprocess.app.sextante
extensions_to_remove=(org.gvsig.animation3d.app org.gvsig.annotation.app.mainplugin org.gvsig.annotation.app.mainplugin org.gvsig.attributeeditor.app.mainplugin org.gvsig.catalog.extension org.gvsig.derivedgeometries.app.mainplugin org.gvsig.dgn.app.mainplugin org.gvsig.dwg.app.mainplugin org.gvsig.dxf.app.mainplugin org.gvsig.educa.portableview.app.viewer org.gvsig.gazetteer.extension org.gvsig.googlemaps.app.streetview org.gvsig.h2spatial.app.mainplugin org.gvsig.hyperlink.app.extension org.gvsig.legend.filteredheatmap.app.mainplugin org.gvsig.legend.heatmap.app.mainplugin org.gvsig.legend.heatmapcomparison.app.mainplugin org.gvsig.lidar.app.mainplugin org.gvsig.oracle.app.mainplugin org.gvsig.raster.georeferencing.app.georeferencingclient org.gvsig.raster.netcdf.app.netcdfclient org.gvsig.raster.wcs.app.wcsclient org.gvsig.raster.wms.app.wmsclient org.gvsig.raster.wmts.app.wmtsclient org.gvsig.selectiontools.app.mainplugin org.gvsig.symbology.app.importsymbols org.gvsig.symbology.app.symbolinstaller org.gvsig.timesupport.app.animation org.gvsig.timesupport.app.viewfilter org.gvsig.wfs.app.mainplugin)

rm -rf /tmp/"${VERSION}"/install/*
rm -rf "/tmp/${VERSION}/preferences/gvSIG/plugins/org.gvsig.app.mainplugin/Symbols/"

for item in ${extensions_to_remove[*]}; do
    full_path="/tmp/${VERSION}/gvSIG/extensiones/${item}"

    if [ -d "${full_path}" ]; then
        rm -rf "${full_path}"
        echo "borrado ${item}"
    else
        echo "El plugin no existe: ${item}"
    fi
done

cp -R "${EXT}/es.icarto.gvsig.commons" "/tmp/${VERSION}/gvSIG/extensiones/"
cp -R "${EXT}/es.icarto.gvsig.copyfeatures" "/tmp/${VERSION}/gvSIG/extensiones/"
cp -R "${EXT}/es.icarto.gvsig.navtable" "/tmp/${VERSION}/gvSIG/extensiones/"
cp -R "${EXT}/es.icarto.gvsig.navtableforms" "/tmp/${VERSION}/gvSIG/extensiones/"
cp -R "${EXT}/es.icarto.gvsig.sixhiara" "/tmp/${VERSION}/gvSIG/extensiones/"
cp -R "${EXT}/es.udc.cartolab.gvsig.elle" "/tmp/${VERSION}/gvSIG/extensiones/"
cp -R "${EXT}/es.udc.cartolab.gvsig.users" "/tmp/${VERSION}/gvSIG/extensiones/"

cp -R portable/common/preferences "/tmp/${VERSION}/"
cp -R portable/common/gvSIG "/tmp/${VERSION}/"
cp -R portable/common/i18n/ "/tmp/${VERSION}/"

BASE_PATCHES_PATH=/tmp/${VERSION}/
bash apply_patches.sh "${BASE_PATCHES_PATH}"

cp -R "/tmp/${VERSION}" "/tmp/${VERSION_SUL}"

cp -R portable/norte/* "/tmp/${VERSION}/"
mv "/tmp/${VERSION}/gvsig-desktop.exe" "/tmp/${VERSION}/SIRHAN_Inventario.exe"
# mv /tmp/${VERSION}/gvsig-desktop.cmd /tmp/${VERSION}/SIRHAN_Inventario.cmd
# mv /tmp/${VERSION}/gvsig-desktop.vbs /tmp/${VERSION}/SIRHAN_Inventario.vbs

cp -R portable/sul/* "/tmp/${VERSION_SUL}/"
mv "/tmp/${VERSION_SUL}/gvsig-desktop.exe" "/tmp/${VERSION_SUL}/SIRHAS_Inventario.exe"
# mv /tmp/${VERSION_SUL}/SIRHAN_Inventario.cmd /tmp/${VERSION_SUL}/SIRHAS_Inventario.cmd
# mv /tmp/${VERSION_SUL}/SIRHAN_Inventario.vbs /tmp/${VERSION_SUL}/SIRHAS_Inventario.vbs

cd /tmp
zip -r9 "${VERSION}.zip" "${VERSION}"
zip -r9 "${VERSION_SUL}.zip" "${VERSION_SUL}"
