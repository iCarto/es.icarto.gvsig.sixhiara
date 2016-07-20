#!/bin/bash

psql -h localhost -U postgres -d sixhiara -f cbase.sql
psql -h localhost -U postgres -d sixhiara -f inventario_geom.sql
psql -h localhost -U postgres -d sixhiara -f inventario_alfanumerico.sql
