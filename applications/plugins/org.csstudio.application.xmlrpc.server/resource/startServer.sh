#!/bin/sh

cd ./current

echo "Starting XMLRPC MySQL Server"
nohup ./xmlrpc-server > server.out 2> server.err < /dev/null &
