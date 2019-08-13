#!/usr/bin/env bash

echo "STARTING WORKERS..."
sbt 'runMain excercise.WorkersApp' &> workers-app.log &

sleep 5s

echo "STARTING MASTER..."
sbt 'runMain excercise.MasterApp'
