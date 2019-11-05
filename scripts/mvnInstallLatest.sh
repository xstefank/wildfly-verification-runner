#!/bin/sh

REPOSITORY=$1
BRANCH=$2
WORKSPACE=$3

git clone --branch $BRANCH $REPOSITORY $WORKSPACE

cd $WORKSPACE

VERSION=$(git describe --tags --always --dirty=-dirty)
echo $VERSION > version.txt

mvn versions:set -DnewVersion=$VERSION

mvn clean install




