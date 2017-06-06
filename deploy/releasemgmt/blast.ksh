#!/bin/ksh
#
# Run the script in arg2 on all the user/machines specified in the file in arg1
# with the optional parameter arg3.
#
# Usage: blast.ksh FileOfUserHostNames ScriptToRunOnEachMachine Arg
#
# Format of FileOfUserHostNames: each line contains: login@1.2.3.4 (IP)
# Comments start with "#" in column one
#
# The second argument can be any shell script. It only needs to exist locally.
# If it's not on the path, then the path must be specified as part of the 
# argument.
#
# The "Arg" parameter is a single string-argument containing any data that the
# script in: ${2} will require. Note, since the body of the script in: ${2} will
# be passed to the remote bash via stdin-redirect, there is no way to pass it
# arguments. As such, it is expected that the script will contain a string
# placeholder: _ARGUMENT_ (all caps), which will be stream edited to contain
# the contents of ${3} (Arg) before the body of the script is passed to the
# remote bash. Your script should then do whatever it needs to do with that
# string. 
#
# Sample usage: 
#
# svn co svn://192.168.4.33/careengine/xxx/deploy/releasemgmt/ .
# ./blast.ksh LoginIPfile ./ce-installer.ksh trunk
# ./blast.ksh LoginIPfile ./ce-installer.ksh "branches/production"
#
###############################################################################

# Initialize logging of all commands
LOG="`pwd`/output.log"
rm -f ${LOG}

echo "Output of remote commands will be stored in: ${LOG}."

# Utility function to date/timestamp each log entry
# arg-1: the string to be logged

function log {
  echo "`date +'%Y%m%d-%H:%M:%S'`: ${1}" | tee -a ${LOG}
}

# Check script arguments

if [[ -z ${3} ]]; then
   log "Usage: FileOfUserHostNames ScriptToRunOnEachMachine trunk-or-branch/xxx"
   exit 1
fi

if [[ ! -f ${1} ]]; then
   log "File of user@host names [${1}] doesn't exist"
   exit 1
fi

if [[ ! -f ${2} ]]; then
   log "Script to run on each machine [${2}] doesn't exist"
   exit 1
fi

Branch=`echo "${3}" | sed -e 'sz/z\\\\/zg'`

TmpScript=/tmp/tmp.$$
# See if we need to pass a string arg to the script to be run remotely
if [[ ! -z ${3} ]]; then
   cat ${2} | sed -e "s/_ARGUMENT_/${Branch}/g" >${TmpScript}
else
   cp ${2} ${TmpScript}
fi
 
# For every line in the file of login/IP's...
while read loginIP; do
  if [[ "${loginIP}" = "" ]]; then
     continue; # ignore blank lines
  fi

  firstChar=`echo "${loginIP}" | cut -c1`
  if [[ "${firstChar}" = "#" ]]; then
     continue; # ignore comment lines
  fi

  log ""
  log "Processing: ${loginIP}"
  
  # Run the script in ${2} as [login] on [IP] and capture the output
  (ssh ${loginIP} 'bash -s' <${TmpScript}) 2>&1 >> ${LOG}

done < ${1}

rm -f ${TmpScript}

log "Finished."

echo "Output is in ${LOG}."

exit 0

# Eof.
