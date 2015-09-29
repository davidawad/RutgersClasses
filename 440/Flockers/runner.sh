#!/bin/bash

cd skeleton

ls

echo "compiling new version of java file and running example $1"

rm *.class

javac Flocker.java
javac Simulation.java

if [ $? -eq 0 ]; then
    echo OK
else
    echo FAIL
    exit 2
fi


if [ -z "$1" ]
    then
    java Simulation ../examples/ff1.xml

fi

java Simulation ../examples/$1
