#!/bin/bash

cd skeleton

ls

echo "compiling new version of java file"

javac Simulaton.java


if [ -z "$1" ]
    then
    java Simulation ../examples/ff1.xml

fi

java Simulation ../examples/$1
