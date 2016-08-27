#!/bin/bash

TODAY=`date +%Y%m%d`


PSQL="psql -X -q -v ON_ERROR_STOP=1 --pset pager=off"

PGOPTIONS='--client-min-messages=warning' $PSQL -h localhost -U postgres -d sixhiara -f cbase.sql.$TODAY
PGOPTIONS='--client-min-messages=warning' $PSQL -h localhost -U postgres -d sixhiara -f acuiferos.sql.$TODAY
PGOPTIONS='--client-min-messages=warning' $PSQL -h localhost -U postgres -d sixhiara -f fontes.sql.$TODAY
PGOPTIONS='--client-min-messages=warning' $PSQL -h localhost -U postgres -d sixhiara -f barragens.sql.$TODAY
PGOPTIONS='--client-min-messages=warning' $PSQL -h localhost -U postgres -d sixhiara -f estacoes.sql.$TODAY
PGOPTIONS='--client-min-messages=warning' $PSQL -h localhost -U postgres -d sixhiara -f inventario_alfanumerico.sql.$TODAY
