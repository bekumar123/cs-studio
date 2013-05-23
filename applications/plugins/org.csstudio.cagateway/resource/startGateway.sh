#!/bin/sh

cd ./current

echo "Starting DOOCS Gateway"
nohup ./cagateway > cagateway.out 2> cagateway.err < /dev/null &
