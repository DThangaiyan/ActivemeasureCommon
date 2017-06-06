#!/bin/sh
ROOT=/home/rkothari
TOMCAT_LIB=$ROOT/apache-tomcat-6.0.33/lib
LIB_REPO=$ROOT/workspace/lib

echo "Using TOMCAT_LIB" $TOMCAT_LIB
echo "Using LIB REPOSITORY" $LIB_REPO
echo "Changing directory to" $TOMCAT_LIB

cd $TOMCAT_LIB

for i in _D_U_M_M_Y_ $*
do
if [[ $i = _D_U_M_M_Y_ ]]; then
   continue
fi
ln -s $LIB_REPO/$i $i 2>output.out
echo "Creating softlink" $LIB_REPO/$i
done
