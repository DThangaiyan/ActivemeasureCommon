#!/bin/ksh
#
# This must be run as follows:
#
# svn co svn://192.168.4.33/careengine/trunk_or_branch/deploy/releasemgmt
# ./releasemgmt/ce-installer.ksh [trunk-or-branch/xxx]

LOG="${HOME}/ce-installer.log"
rm -f "${LOG}"

function log {
  msg="`date +'%Y%m%d-%H%M%S'`: ${1}"
  echo "${msg}"
  echo "${msg}" >>${LOG}
}

ME=`whoami`
IP=`hostname -i`
TMP=/tmp/xxx.$$
ScriptDir="${0%/*}"
if [[ "${ScriptDir}" = "." ]]; then
   ScriptDir=`pwd`
fi

log "Launched script in directory: ${ScriptDir}"
log "Running as [${ME}] on host [${IP}]"

# Figure out the branch which must be installed

# See if we're being run from: blast.ksh
Tmp1="_ARGUMENT_"
Tmp2="_ARGU" # Split into 2 pieces to keep sed from replacing it
Tmp3="MENT_" # Split into 2 pieces to keep sed from replacing it
if [[ "${Tmp1}" != "${Tmp2}${Tmp3}" ]]; then
   # We are being run via: blast.ksh: the value of Branch is in Tmp1
   Branch="${Tmp1}"
else
   cd ${ScriptDir}
   Branch=`svn  info | \
           grep URL  | \
           sed  -e 's/URL: svn:\/\/192.168.4.33\/careengine\///' \
                -e 's/\/deploy\/.*$//'`
fi
log "Branch: [${Branch}]"

if [[ -z ${Branch} ]]; then
   log "Error: unable to determine the branch that is to be installed - unable to proceed!"
   exit 1
fi

# We work relative to the home dir of the current user
cd ${HOME}

# Set up base portion of URL for all apps to be installed
BaseUrl="svn://192.168.4.33/careengine/${Branch}"
log "BaseURL = [${BaseUrl}'"

# Switch to the new version of: lib

# Do lib (always)
URL="${BaseUrl}/lib/"
if [[ ! -d lib ]]; then
   log "Installing 'lib': ${URL}..."
   mkdir lib
   cd lib
   log "svn co ${URL}"
   (svn co ${URL} .) >/dev/null 2>&1
   log "Installation of 'lib' finished"
   cd ..
else
   log "Switching 'lib' to: ${URL}"
   cd lib
   if [[ `svn switch -q ${URL} . 2>&1 | grep -c "does not exist"` -gt 0 ]]; then
      log "URL: [${URL}] does not exist"
      exit 1
   fi
   log "Updating 'lib'..."
   log "svn update"
   svn update   2>&1 | grep -v "At revision"
   log 'svn update *'
   svn update * 2>&1 | grep -v "At revision"
   log "Update of 'lib' finished"
   cd ..
fi

# Do CE: careengine / dasb
URL="${BaseUrl}/deploy/ce_server/"
# See if it should be deployed, and if so, as which (careengine or dasb)
log "Checking to see if 'careengine/dasb' should be deployed..."
mkdir -p ${TMP}
cd ${TMP}
svn co ${URL} . 2>&1 >/dev/null
doIt="N"
if [[ `grep ${ME}@${IP} bin/node.config | grep -v "^#" | wc -l | awk '{printf("%s\n",$1);}'` -eq 1 ]]; then
   doIt="Y"
   confDir=`grep ${ME}@${IP} bin/node.config | grep -v "^#" | awk 'BEGIN { FS="="; }{ printf("%s\n",$2); }'`
   DIR=careengine
   if ( [[ -f bin/${confDir}/ods-server.properties ]] && [[ ! -f bin/${confDir}/cev2.properties ]] ); then
      DIR=dasb
   fi
fi
cd ${HOME}
rm -rf ${TMP} >/dev/null 2>&1

if [[ "${doIt}" = "Y" ]]; then
   if [[ ! -d ${DIR} ]]; then
      log "Installing '${DIR}'..."
      mkdir ${DIR}
      cd ${DIR}
      log "svn co ${URL}"
      (svn co ${URL} .) >/dev/null 2>&1
      log "Finished installing '${DIR}'"
      cd ..
   else
      log "Switching '${DIR}' to: ${URL}"
      cd ${DIR}
      if [[ `svn switch -q ${URL} . 2>&1 | grep -c "does not exist"` -gt 0 ]]; then
         log "URL: [${URL}] does not exist"
         exit 1
      fi
      log "Updating '${DIR}'..."
      log "svn update"
      svn update   2>&1 | grep -v "At revision"
      log 'svn update *'
      svn update * 2>&1 | grep -v "At revision"
      log "Update of '${DIR}' finished"
      cd ..
   fi
else
   log "Not installing 'careengine/dasb' for ${ME}@${IP} because ${ME} is not configured to run it on ${IP}"
fi

# Do CE_light
URL="${BaseUrl}/deploy/ce_light/"
# See if we SHOULD install it
log "Checking to see if 'ce_light' should be deployed..."
mkdir -p ${TMP}
cd ${TMP}
svn co ${URL} . 2>&1 >/dev/null
doIt="N"
if [[ `grep ${ME}@${IP} bin/node.config | grep -v "^#" | wc -l | awk '{ printf("%s\n",$1);}'` -eq 1 ]]; then
   doIt="Y"
fi
rm -rf ${TMP}
cd ${HOME}

if [[ "${doIt}" = "Y" ]]; then
   if [[ ! -d ce_light ]]; then
      log "Installing 'ce_light'..."
      mkdir ce_light
      cd ce_light
      log "svn co ${URL}"
      (svn co ${URL} .) >/dev/null 2>&1
      log "Installation of 'ce_light' finished"
      cd ..
   else
      log "Switching 'ce_light' to: ${URL}..."
      cd ce_light
      if [[ `svn switch -q ${URL} . 2>&1 | grep -c "does not exist"` -gt 0 ]]; then
         echo "URL: [${URL}] does not exist"
         exit 1
      fi
      log "Updating 'ce_light'..."
      log "svn update"
      svn update   2>&1 | grep -v "At revision"
      log 'svn update *'
      svn update * 2>&1 | grep -v "At revision"
      log "Update of 'ce_light' finished"
      cd ..
   fi
else
   log "Not installing 'ce_light' for ${ME}@${IP} because ${ME} is not configured to run it on ${IP}"
fi

log "Log in: ${LOG}"
log "Done."
 
exit 0

# Eof.
