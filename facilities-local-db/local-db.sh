#!/usr/bin/env bash

cd $(readlink -f $(dirname $0))

ID=facilities-cdw-db

# Destroy old container
echo "Stopping existing database container"
docker stop $ID
echo "Removing existing database container"
docker rm $ID

# SQL Server Docker Image (You don't have to install anything!!!)
docker pull mcr.microsoft.com/mssql/server:2017-latest

echo "Creating new database container"
docker run \
  --name "$ID" \
  -e 'ACCEPT_EULA=Y' \
  -e "SA_PASSWORD=<YourStrong!Passw0rd>" \
  -p 1533:1433 \
  -d mcr.microsoft.com/mssql/server:2017-latest

#
# Wait for the sqlservr processes
#
sleep 5

mvn clean install -Ppopulaterator -P'!standard' -Dexec.cleanupDaemonThreads=false
