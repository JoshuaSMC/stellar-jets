#!/bin/bash
# Carga las variables del .env y arranca el backend
set -a
source "$(dirname "$0")/.env"
set +a

mvn spring-boot:run
