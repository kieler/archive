#!/bin/sh

if [ "$#" -ne 1 ]
then
  echo "Usage ./increaseJavaVersion.sh [new java version]"
  set -e
  exit 0
fi

NEW_VERSION=$1

cd $(pwd)

MANIFESTS=$(find . -name 'MANIFEST.MF')

for f in $MANIFESTS 
do
  sed -i -r "s/(Bundle-RequiredExecutionEnvironment: ).*/\1$NEW_VERSION/g" $f
  echo $f
done


CLASSPATHS=$(find . -name ".classpath")

for f in $CLASSPATHS
do
  sed -i -r "s/(.*StandardVMType\/).*(\"\/>).*/\1$NEW_VERSION\2/g" $f
  echo $f
done


