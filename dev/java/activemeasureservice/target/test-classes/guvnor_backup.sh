#NOTE:The original file resides under svn://192.168.4.33/careengine/dev/trunk/dev/java/activemeasureservice/src/test/resources.Make sure to push updates to file. 
#Currently this script is on the following location with a cron job running on 192.168.4.33
#ce2admin@192.168.4.33:/home/ce2admin/apache-tomcat-6.0.35/bin
#in crontab, it is configured do execute at 23:45 every weekdays.

#!/bin/sh

WORKDIR=${HOME}/guvnor

SVNREPOURL=svn://192.168.4.33/careengine/dev/trunk/dev/java/activemeasureservice/src/test/resources

#Uncomment if you want to use specific svn user/password
SVNCREDENTIALS='--username ce2admin --password webwethods'


GUVNORBACKUPURL=http://192.168.4.112:8090/guvnor-5.5.0.Final-jboss-as-7.0/org.drools.guvnor.Guvnor/backup
GUVNORCREDENTIALS='--http-user=test --http-password=test'

PAYLOAD_ZIP=repository_export.zip
PAYLOAD_XML=repository_export.xml
#Following variable controls whether zip or xml thats get stored in svn. For now it is defaulted to zip
PAYLOAD=${PAYLOAD_ZIP}

#Cleanup post EXIT
trap 'cd $WORKDIR; rm -rf repository' EXIT

mkdir -p $WORKDIR
cd $WORKDIR

svn ${SVNCREDENTIALS} --non-interactive --no-auth-cache co ${SVNREPOURL} repository > /dev/null 2>&1

if [ $? -ne 0 ]
then
    echo SVN checkout failure
    exit 1
fi

cd repository
FIRSTTIME=YES
if [ -e ${PAYLOAD} ]
then
    FIRSTTIME=NO
    rm -f ${PAYLOAD} > /dev/null 2>&1
fi

wget ${GUVNORCREDENTIALS} -O ${PAYLOAD_ZIP} ${GUVNORBACKUPURL} > /dev/null 2>&1

if [ $? -eq 0 ]
then
    unzip -o ${PAYLOAD_ZIP}
    if [ $? -eq 0 ]
    then
	if [ ${FIRSTTIME} = "YES" ]
	then
	    svn ${SVNCREDENTIALS} --non-interactive --no-auth-cache add ${PAYLOAD}
	fi
	svn ${SVNCREDENTIALS} --non-interactive --no-auth-cache -m 'cron backup' commit ${PAYLOAD}
	if [ $? -eq 0 ]
	then
	    echo success. checked-in ${PAYLOAD} into svn at ${SVNREPOURL}
	    exit 0
	else
	    echo svn commit failure
	fi
    else
	echo unzip failure
    fi
else
    echo wget failure
fi

exit 1
