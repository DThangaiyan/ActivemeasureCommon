#This script can be used to cut the branch using maven
#You need to update the -D informations to suit your need, at least before we change this script to be dynamic approach
mvn release:branch -DbranchName=trunk_cdm -Dbranchbase=svn://192.168.4.33/careengine/dev/branches -DupdateBranchVersions=true -DupdateWorkingCopyVersions=false -DautoVersionSubmodules=true -DdryRun=true -DtagBase=svn://192.168.4.33/careengine/dev/tags -Dtag=CEv4.0-TRUNK-2012-07-10
