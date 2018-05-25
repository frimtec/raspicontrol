#!/bin/bash

JVM_OPTIONS=""
if [[ "$1" == "-debug" ]]; then
    echo "Remote debugging enabled."
    JVM_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
fi
java ${JVM_OPTIONS} -jar raspicontrol-demo-*.jar