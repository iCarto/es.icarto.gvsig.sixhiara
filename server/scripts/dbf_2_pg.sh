#!/bin/bash

OUTPUT=./inventario.sql

USER=inventario
SCHEMA=inventario

echo 'BEGIN;' > $OUTPUT
for shp in `find -iname '*.dbf'` ; do
    TABLE=`basename ${shp%.dbf}`
    shp2pgsql -p -n -W ISO8859-1 $shp ${SCHEMA}.${TABLE} \
    | sed 's/(gid serial,/(\ngid SERIAL PRIMARY KEY,/' \
    | sed 's/ALTER TABLE .* ADD PRIMARY.*//' \
    | sed 's/BEGIN;//' \
    | sed 's/COMMIT;//' \
    | sed 's/^SET.*//' >> $OUTPUT

    echo "ALTER TABLE ${SCHEMA}.${TABLE} OWNER TO $USER" >> $OUTPUT

    # | sed 's/);/,\ngeom geometry(MultiPolygon, 32637)\n);/' \
done
echo 'COMMIT;' >> $OUTPUT


OUTPUT=./data.sql
echo 'BEGIN;' > $OUTPUT
for shp in `find -iname '*.dbf'` ; do
    TABLE=`basename ${shp%.dbf}`
    shp2pgsql -a -n -W ISO8859-1 $shp ${SCHEMA}.${TABLE} \
    | sed 's/BEGIN;//' \
    | sed 's/COMMIT;//' \
    | sed 's/^SET.*//' >> $OUTPUT
done
echo 'COMMIT;' >> $OUTPUT
