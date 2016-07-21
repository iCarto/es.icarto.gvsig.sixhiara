#!/bin/bash

TODAY=`date +%Y%m%d`

psql -h localhost -U postgres -d sixhiara -f cbase.sql.$TODAY
psql -h localhost -U postgres -d sixhiara -f acuiferos.sql.$TODAY
psql -h localhost -U postgres -d sixhiara -f fontes.sql.$TODAY
psql -h localhost -U postgres -d sixhiara -f barragens.sql.$TODAY
psql -h localhost -U postgres -d sixhiara -f estacoes.sql.$TODAY
psql -h localhost -U postgres -d sixhiara -f inventario_alfanumerico.sql.$TODAY
