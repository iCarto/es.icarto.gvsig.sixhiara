#!/bin/bash

dropdb -h localhost -U postgres sixhiara
dropuser -h localhost -U postgres sixhiara_owner
dropuser -h localhost -U postgres inventario_read
dropuser -h localhost -U postgres inventario_write
createdb -h localhost -U postgres -T template1 sixhiara
