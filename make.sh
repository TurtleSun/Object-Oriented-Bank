#!/bin/bash

javac -d bin -cp lib/opencsv-5.9.jar:lib/commons-lang3-3.14.0.jar $(find . -name "*.java")

if [ $? -eq 0 ]; then
    cd bin
    java -cp .:../lib/opencsv-5.9.jar:../lib/commons-lang3-3.14.0.jar src.Main  
else
    echo "Compilation failed."
fi