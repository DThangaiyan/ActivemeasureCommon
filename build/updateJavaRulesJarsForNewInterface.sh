#!/bin/ksh
#
# Update jar files with latest java/class files for:
#        srl2java/src/net/ahm/careengine/JavaRuleExecutor.java
#
# $Workspace/dev/blaze/srl2java/cev2javarulesrc.java
# $Workspace/dev/blaze/srl2java/dist/cev2javarules.jar
# $Workspace/deploy/ce_server/lib/cev2javarules.jar
#
# We ASSUME that the JavaRuleExecutor.java file has been updated and committed.

#set -x              # Uncomment this to see a command-trace

# This is called from ant, which is started in: $Workspace/buld
BaseDir="`pwd`/.."   # We start from $Workspace/build

# Directories in which we can find the files from last release
Deploy="${BaseDir}/deploy/ce_server/lib"
Srl2java="${BaseDir}/dev/blaze/srl2java"

# The package in which the source file exists 
Package="net/ahm/careengine"

# Construct the java and class file names
BaseFile="JavaRuleExecutor"
JavaFile="${BaseFile}.java"
ClassFile="${BaseFile}.class"

# The names of the jar files to be upgraded
ClassJarName="cev2javarules.jar"
SrcJarName="cev2javarulesrc.jar"

# Absolute path to the files to be updated
DeployJar="${Deploy}/${ClassJarName}"
SrcJar="${Srl2java}/${SrcJarName}"

# A scratch directory
Tmp="${BaseDir}/tmpdir.$$"

# Update and compile the Java rules executor file
cd ${Srl2java}/src/${Package}
svn update ${JavaFile}
echo "Compiling ${JavaFile}"
cd ${Srl2java}
ant compile-java-rules

# Grab the latest jars
cd ${Deploy}
svn update ${ClassJarName}

cd ${Srl2java}
svn update ${SrcJarName}

cd ${Srl2java}/dist
svn update ${ClassJarName}

# Create a temp dir
mkdir ${Tmp}
cd ${Tmp}
mkdir -p ${Package}

# Copy the updated java source and newly compiled class file to a tmp location
cp ${Srl2java}/src/${Package}/${JavaFile}            ./${Package}/${JavaFile}
cp ${Srl2java}/build/classes/${Package}/${ClassFile} ./${Package}/${ClassFile}
cp ${DeployJar}                                      .
cp ${SrcJar}                                         .

# Update the jar files
jar uf ${ClassJarName} ${Package}/${ClassFile}
jar uf ${SrcJarName}   ${Package}/${JavaFile}

# Copy the jar files back from whence they came
cp ${SrcJarName}       ${SrcJar}
cp ${ClassJarName}     ${DeployJar}
cp ${ClassJarName}     ${Srl2java}/dist/${ClassJarName}

# No longer need the tmp dir 
cd ${BaseDir}
rm -rf ${Tmp}

# Commit the three updates jar files
cd ${Srl2java}
svn commit -m "Put new JavaRuleExecutor implementation into jar" ${SrcJarName}
cd ${Srl2java}/dist
svn commit -m "Put new JavaRuleExecutor implementation into jar" ${ClassJarName}
cd ${Deploy}
svn commit -m "Put new JavaRuleExecutor implementation into jar" ${ClassJarName}

exit 0

# Eof.