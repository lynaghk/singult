#!/bin/bash
set -e

COFFEE_IN=src/coffee/
PKG_OUT=pkg/closure-js/libs/singult #dir that'll be sucked into JAR
JS_TMP=dev_public/js_test
JS_OUT=target/singult.min.js
CLOSURE_LIBRARY=vendor/closure/closure/goog
CLOSURE_COMPILER=vendor/closure-compiler.jar

#Clean 
rm -rf $JS_TMP
mkdir -p $JS_TMP
mkdir -p target/

#Get Closure compiler, if it doesn't exist.
if [ ! -f $CLOSURE_COMPILER ]; then
echo "Fetching Google Closure compiler..."
    mkdir -p vendor
    cd vendor
    #This -4 forces cURL to use IP4. Without it, cURL gets confused...
    curl -4 -O http://closure-compiler.googlecode.com/files/compiler-latest.zip
    unzip -q compiler-latest.zip
    mv compiler.jar closure-compiler.jar
    rm -f COPYING README compiler-latest.zip
    cd ../
    echo "Closure compiler retrieved successfully."
fi

#Compile CoffeeScript
coffee                            \
    --compile                     \
    --bare                        \
    --output $JS_TMP $COFFEE_IN

#Run Closure Compiler
java -jar vendor/closure-compiler.jar \
    --js_output_file $JS_OUT \
    --compilation_level ADVANCED_OPTIMIZATIONS \
    --manage_closure_dependencies true \
    --js $CLOSURE_LIBRARY/base.js \
    --js $(find $CLOSURE_LIBRARY/string -name '*.js') \
    --js $(find $JS_TMP -name '*.js')
