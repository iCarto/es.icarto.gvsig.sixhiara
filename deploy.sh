#!/bin/bash

VERSION=gvSIG_SIXHIARA_`date +%Y%m%d`
EXT=/home/fpuga/development/gvsig-desktop2.2.0-2312/org.gvsig.desktop-2.0.100/target/product/gvSIG/extensiones

unzip /var/tmp/gvsig-desktop-2.2.0-2313-final-win-x86.zip -d /tmp
mv /tmp/gvsig-desktop-2.2.0-2313-final-win-x86/ /tmp/${VERSION}

rm -rf /tmp/${VERSION}/install/*
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.annotation.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.catalog.extension/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.gazetteer.extension/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.dyschromatopsia.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.publish.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.netcdf.app.netcdfclient/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.seismic.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.view3d.app/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.mapsheets.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.selectiontools.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.scripting.app.extension/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.symbology.app.symbolinstaller/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.symbology.app.importsymbols/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.attributeeditor.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.daltransform.app.mainplugin/

# rm -rf /tmp/${VERSION}/gvSIG/extensiones/

cp -R ${EXT}/es.icarto.gvsig.commons /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.copyfeatures /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.navtable /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.navtableforms /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.sixhiara /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.udc.cartolab.gvsig.elle /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.udc.cartolab.gvsig.users /tmp/${VERSION}/gvSIG/extensiones/

cp -R portable/home /tmp/${VERSION}/
cp -R portable/gvSIG /tmp/${VERSION}/

cd /tmp
zip -r ${VERSION} /tmp/${VERSION}



