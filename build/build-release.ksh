#!/bin/ksh

export JAVA_HOME="/usr/java/jdk1.6.0_27"
export BUILD_HOME="`pwd`"
export ANT_HOME="${BUILD_HOME}/tools/ant-1.7.1"
export TARGET="release"
export CEroot="`pwd`/.."

echo "Generating all jars..."

for d in build \
		 dev/java/cecontracts \
         dev/java/ceoutbound \
         dev/java/cev2blaze \
         dev/java/cev2blaze/test \
         dev/java/cev2dataaccess \
         dev/java/cev2light \
         dev/java/cev2light/test \
         dev/java/cev2util \
         dev/java/common \
         dev/java/dasbserver \
         dev/java/dasbserver/test;
do
    echo "  doing: ../${d}/build.xml...${TARGET}"
    cd ../${d} >/dev/null 2>&1
    ${ANT_HOME}/bin/ant -Dcareengine.root="${CEroot}" -f build.xml ${TARGET}
    cd - >/dev/null 2>&1
done

echo "Done."
