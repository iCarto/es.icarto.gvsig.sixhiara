#!/bin/bash

#$1 = PATH_TO/160414_SIG_SIXHIARA/
SHAPE_FOLDER="${1}/Dados/Capas/SHP/"
DBF_FOLDER="${1}/Dados/Tablas_embebida/"

if [ ! -d $SHAPE_FOLDER ] ; then
    echo "El directorio $SHAPE_FOLDER debe existir"
    exit
fi

if [ ! -d $DBF_FOLDER ] ; then
    echo "El directorio $DBF_FOLDER debe existir"
    exit
fi

TODAY=`date +%Y%m%d`
CBASE=./cbase.sql.$TODAY
ACUIFEROS=./acuiferos.sql.$TODAY
FONTES=./fontes.sql.$TODAY
BARRAGENS=./barragens.sql.$TODAY
ESTACOES=./estacoes.sql.$TODAY

echo 'BEGIN;' > $CBASE
echo 'BEGIN;' > $ACUIFEROS
echo 'BEGIN;' > $FONTES
echo 'BEGIN;' > $BARRAGENS
echo 'BEGIN;' > $ESTACOES

ALL_TABLES=()

FIRST_RIO='false'
for shp in `find $SHAPE_FOLDER -iname '*.shp'` ; do

    TABLE=`basename ${shp%.shp} | tr '[:upper:]' '[:lower:]'`
    SCHEMA_BASE=cbase
    OUTPUT=$CBASE

    # bug 1186
    if [[ ${FIRST_RIO} == 'false' ]] && [[ ${TABLE} == 'rios' ]] ; then
	FIRST_RIO='true'
	continue
    fi

    if [[ ${TABLE} == 'acuiferos' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$ACUIFEROS
    fi

    if [[ ${TABLE} == 'fontes' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$FONTES
    fi

    if [[ ${TABLE} == 'barragem' ]] ; then
	TABLE=barragens
	SCHEMA_BASE=inventario
	OUTPUT=$BARRAGENS
    fi

    if [[ ${TABLE} == 'estacoes' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$ESTACOES
    fi

    if [[ ${TABLE} == 'zimbabwe' ]] || [[ ${TABLE} == 'zambia' ]] || [[ ${TABLE} == 'swaziland' ]] || [[ ${TABLE} == 'south_africa' ]] || [[ ${TABLE} == 'rdcongo' ]] || [[ ${TABLE} == 'namibia' ]] || [[ ${TABLE} == 'madagascar' ]] || [[ ${TABLE} == 'lesotho' ]] || [[ ${TABLE} == 'congo' ]] || [[ ${TABLE} == 'botswana' ]] || [[ ${TABLE} == 'angola' ]] || [[ ${TABLE} == 'reserva_do_niassa' ]] || [[ ${TABLE} == 'biodiversidad_region' ]] || [[ ${TABLE} == 'piezo_v0' ]] || [[ ${TABLE} == 'estacoes_hidrometricas' ]] || [[ ${TABLE} == 'estaçoes_hidrometricas' ]] || [[ ${TABLE} == 'estacoes_pluviometricas' ]] || [[ ${TABLE} == 'estaçoes_pluviometricas' ]] || [[ ${TABLE} == 'cotas' ]] || [[ ${TABLE} == 'direcao_fluxo' ]] || [[ ${TABLE} == 'dir_fluxo' ]] ; then
	continue
    fi

    if [[ ${TABLE} == 'reserva_zona_tampão' ]]; then
	TABLE='reserva_zona_tampao'
    fi

    if [[ ${TABLE} == 'estaçoes_evaporacion' ]]; then
	TABLE='estacoes_evaporacion'
    fi

    if [[ ${TABLE} == 'areas_exploraçao_mineira' ]]; then
	TABLE='areas_exploracao_mineira'
    fi

    if [[ ${TABLE} == 'areas_exploraçao_petroleo_gas' ]]; then
	TABLE='areas_exploracao_petroleo_gas'
    fi

    ALL_TABLES+=(${TABLE})

    shp2pgsql -s 32737 -g geom -a -W ISO8859-1 $shp ${SCHEMA_BASE}.${TABLE} \
    | sed 's/BEGIN;//' \
    | sed 's/COMMIT;//' \
    | sed 's/^SET.*//' >> $OUTPUT
done

echo 'COMMIT;' >> $CBASE
echo 'COMMIT;' >> $ACUIFEROS
echo 'COMMIT;' >> $FONTES
echo 'COMMIT;' >> $BARRAGENS
echo 'COMMIT;' >> $ESTACOES

# bug #1180
echo 'UPDATE inventario.fontes SET estado_fon = estado;' >> $FONTES
echo 'UPDATE inventario.fontes SET estado = NULL;' >> $FONTES

IFS=$'\n' ALL_TABLES=($(sort -r <<<"${ALL_TABLES[*]}"))
unset IFS

# TODO. Preservar nombres originales como nombre de capa. Espacios, mayúsculas, ...

MAP="./_map.sql.${TODAY}"
echo "BEGIN;" > $MAP
total=${#ALL_TABLES[*]}
#
for (( i=0; i<=$(( $total -1 )); i++ ))
do
    LAYERNAME=${ALL_TABLES[$i]}
    TABLENAME=${ALL_TABLES[$i]}
    SCHEMA='cbase'
    if [[ ${LAYERNAME} == 'acuiferos' ]] || [[ ${LAYERNAME} == 'fontes' ]] || [[ ${LAYERNAME} == 'barragens' ]] ||[[ ${LAYERNAME} == 'estacoes' ]] ; then
	SCHEMA='inventario'
    fi
    echo "INSERT INTO elle._map (mapa, nombre_capa, nombre_tabla, posicion, visible, max_escala, min_escala, grupo, schema, localizador) VALUES ('TODAS', '${LAYERNAME}', '${TABLENAME}', $i, true, NULL, NULL, NULL, '$SCHEMA', NULL);" >> $MAP
done

echo "INSERT INTO elle._map_style (nombre_capa, nombre_estilo, type, definicion, label) SELECT nombre_capa, 'TODAS', type, definicion, label FROM elle._map_style WHERE nombre_estilo='SIXHIARA';" >> $MAP
echo "COMMIT;" >> $MAP

OUTPUT=./inventario_alfanumerico.sql.$TODAY

echo 'BEGIN;' > $OUTPUT
for shp in `find $DBF_FOLDER -iname '*.dbf'` ; do
    TABLE=`basename ${shp%.dbf}`
    SCHEMA=inventario
    shp2pgsql -a -n -W ISO8859-1 $shp ${SCHEMA}.${TABLE} \
    | sed 's/BEGIN;//' \
    | sed 's/COMMIT;//' \
    | sed 's/^SET.*//' >> $OUTPUT
done
echo 'COMMIT;' >> $OUTPUT

# bug #1184
mv $OUTPUT $OUTPUT.BORRAR
grep -v 'MU-31' $OUTPUT.BORRAR > $OUTPUT
