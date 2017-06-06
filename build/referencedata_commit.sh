cd ../dev/java/eventprocessingunittestframework/src/main/resources/          
svn status > ~/ruleLogs/logs/referencedatarefreshsvn.log
svn status | grep '^?' | sed 's/^?\ *//' | sed 's/\ /\\ /g' | xargs svn add >> ~/ruleLogs/logs/referencedatarefreshsvn.log
svn status | grep '^!' | sed 's/^!\ *//' | sed 's/\ /\\ /g' | xargs svn remove >> ~/ruleLogs/logs/referencedatarefreshsvn.log
svn commit --non-interactive --no-auth-cache --username ce2admin --password webwethods -m "Jenkins automated Checkin" >> ~/ruleLogs/logs/referencedatarefreshsvn.log
svn status >> ~/ruleLogs/logs/referencedatarefreshsvn.log
