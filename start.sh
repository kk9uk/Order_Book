#!/usr/bin/env bash

set -eEuo pipefail
trap 'echo "[ERROR]: Line $LINENO of $0"' ERR

cd ./app
mvn clean package

cd ..
docker-compose up --build --detach
