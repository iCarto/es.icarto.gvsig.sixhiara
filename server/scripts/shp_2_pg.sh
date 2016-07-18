#!/bin/bash

OUTPUT=./inventario.sql

USER=inventario
SCHEMA=inventario

echo 'BEGIN;' > $OUTPUT
for shp in `find -iname '*.shp'` ; do
    TABLE=`basename ${shp%.shp}`
    shp2pgsql -s 32737 -g geom -I -p -W ISO8859-1 $shp ${SCHEMA}.${TABLE} \
    | sed 's/(gid serial,/(\ngid SERIAL PRIMARY KEY,/' \
    | sed 's/ALTER TABLE .* ADD PRIMARY.*//' \
    | sed 's/BEGIN;//' \
    | sed 's/COMMIT;//' \
    | sed 's/^SET.*//' >> $OUTPUT

    echo "ALTER TABLE ${SCHEMA}.${TABLE} OWNER TO $USER" >> $OUTPUT

    # | sed 's/);/,\ngeom geometry(MultiPolygon, 32637)\n);/' \
done
echo 'COMMIT;' >> $OUTPUT


CBASE=./cbase.sql
DATA=./data.sql
echo 'BEGIN;' > $CBASE
echo 'BEGIN;' > $DATA
for shp in `find -iname '*.shp'` ; do
    TABLE=`basename ${shp%.shp}`
    SCHEMA_BASE=cbase
    OUTPUT=$CBASE
    if [[ ${TABLE} == 'Acuiferos' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$DATA
    fi
    if [[ ${TABLE} == 'Fontes' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$DATA
    fi
    if [[ ${TABLE} == 'Barragem' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$DATA
    fi
    if [[ ${TABLE} == 'Estacoes' ]] ; then
	SCHEMA_BASE=inventario
	OUTPUT=$DATA
    fi
    shp2pgsql -s 32737 -g geom -a -W ISO8859-1 $shp ${SCHEMA_BASE}.${TABLE} \
    | sed 's/BEGIN;//' \
    | sed 's/COMMIT;//' \
    | sed 's/^SET.*//' >> $OUTPUT
done
echo 'COMMIT;' >> $CBASE
echo 'COMMIT;' >> $DATA
