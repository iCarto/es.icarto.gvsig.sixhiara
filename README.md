# SIXHIARA

gvSIG extension for [SIRA:Inventario](https://icarto.es/proyecto-sixhiara/)


# Desarrollo

Configurar el entorno como se indica en https://gitlab.com/icarto/ikdb/-/blob/master/desktop/gvsig2/gvsig2_configurar_entorno.md

Despu�s continuar con estas instrucciones:

```shell
cd ..

mkdir gvsig2-plugins && cd gvsig2-plugins

git clone git@gitlab.com:icarto/es.icarto.gvsig.commons.git && cd es.icarto.gvsig.commons && git checkout gvsig2
git clone git@github.com:cartolab/extCopyFeatures.git && cd extCopyFeatures && git checkout gvsig2
git clone git@gitlab.com:icarto/extDBConnection.git && cd extDBConnection && git checkout gvsig2
git clone git@gitlab.com:icarto/extELLE.git && cd extELLE && git checkout gvsig2
git clone git@github.com:navtable/navtable.git && cd navtable && git checkout gvsig2
git clone git@github.com:navtable/navtableforms.git && cd navtableforms && git checkout gvsig2

git clone https://github.com/TheHortonMachine/hydrologis4gvsig

cd ..
cd sixhiara

git clone git@gitlab.com:icarto/sixhiara.git && cd sixhiara && git checkout gvsig2
```



## Aplicar parches

En la versi�n 2.3.1-2501 hay varios parches reportados. Pero es necesario aplicarlos al workspace antes de poder trabajar. En `es.icarto.gvsig.sixhiara/portable/common/patches` est�n los jar compilados. Lo m�s f�cil es sobreescribir los jar originales con estos ya compilados (y reaplicar cada vez que haya un cambio)

```
bash apply_patches SVN_PATH/target/product
```

En `es.icarto.gvsig.sixhiara/portable/common/patches/modified_files` est�n los ficheros java modificados, habr�a que sobreescribir los originales. Ver fichero de `notas.txt` en ese directorio. No se han generado diffs.

En nuevas versiones estos parches n oson necesarios pero es seguro usar apply_patches porqu� simplemente ignora los cambios.