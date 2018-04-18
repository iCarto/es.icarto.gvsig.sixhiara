#!/bin/bash



VERSION=`date +%g%m%d`_SIRHAN_Inventario

while IFS='' read -r line || [[ -n "$line" ]]; do
    if [[ $line == gvsig.product.folder.path* ]]; then
	gvsig_product_folder_path=$(echo "$line" | cut -d '=' -f2)
    fi
done < ~/.gvsig-devel.properties

EXT="${gvsig_product_folder_path}/gvSIG/extensiones/"


# unzip /var/tmp/gvsig-desktop-2.2.0-2313-final-win-x86.zip -d /tmp
cp -r /var/tmp/gvSIG-desktop-2.3.1-2501-final-win-x86_64/ /tmp/${VERSION}

rm -rf /tmp/${VERSION}/install/*
rm -rf /tmp/${VERSION}/gvSIG/extensiones/CSVWizard
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.animation3d.app
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.annotation.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.busquedacatastral.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.catalog.extension/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.chart.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.chart.app.layoutplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.chart.app.legendplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.derivedgeometries.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.dgn.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.dwg.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.dxf.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.dyschromatopsia.app.mainplugin/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.gazetteer.extension/
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.geoprocess.app.sextante
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.geoprocess.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.geoprocess.app.algorithm
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.hyperlink.app.extension
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.lidar.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.lrs.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.r.app.mainplugin
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
rm -rf /tmp/${VERSION}/gvSIG/extensiones/AutodeteccionDeAlturas
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.customize.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.wcs.app.wcsclient
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.timesupport.app.animation
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.timesupport.app.viewfilter
rm -rf /tmp/${VERSION}/gvSIG/extensiones/ScriptingComposerTools
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.wfs.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.wmts.app.wmtsclient
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.wms.app.wmsclient
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.postgis.app.postgisrasterclient
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.georeferencing.app.georeferencingclient
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.googlemaps.app.streetview
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.scripting.app.mainplugin
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.principalcomponents.app.principalcomponentsclient
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.roimask.app.client
rm -rf /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.raster.tasseledcap.app.tasseledcapclient

rm -rf /tmp/${VERSION}/home/gvSIG/plugins/org.gvsig.app.mainplugin/Symbols/

cp -R ${EXT}/es.icarto.gvsig.commons /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.copyfeatures /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.navtable /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.navtableforms /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.icarto.gvsig.sixhiara /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.udc.cartolab.gvsig.elle /tmp/${VERSION}/gvSIG/extensiones/
cp -R ${EXT}/es.udc.cartolab.gvsig.users /tmp/${VERSION}/gvSIG/extensiones/

cp -R portable/home /tmp/${VERSION}/
cp -R portable/gvSIG /tmp/${VERSION}/

cp -R portable/i18n/ /tmp/${VERSION}/

# mv /tmp/${VERSION}/gvsig-desktop.exe /tmp/${VERSION}/SIRHAN_Inventario.exe
mv /tmp/${VERSION}/gvsig-desktop.cmd /tmp/${VERSION}/SIRHAN_Inventario.cmd
mv /tmp/${VERSION}/gvsig-desktop.vbs /tmp/${VERSION}/SIRHAN_Inventario.vbs

### Hacked libs. Remove when fixed upstream
cp portable/patches/org.gvsig.fmap.dal.impl-2.0.157.jar /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.app.mainplugin/lib/org.gvsig.fmap.dal.impl-2.0.157.jar
rm /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.gdal.app.ogr.mainplugin/lib/org.gvsig.gdal.prov.ogr-1.0.30.jar
cp portable/patches/org.gvsig.gdal.prov.ogr-1.0.32.jar /tmp/${VERSION}/gvSIG/extensiones/org.gvsig.gdal.app.ogr.mainplugin/lib/org.gvsig.gdal.prov.ogr-1.0.32.jar

# cd /tmp
# zip -r ${VERSION} /tmp/${VERSION}



