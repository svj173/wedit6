#!/bin/bash
# Start WEdit. Use classpath inside JAR. Delete all '0D' !!!
# For Desktop icon only
export MODULE_HOME="/home/svj/programm/my/wedit6"
exec java -Dmodule.home=$MODULE_HOME -Dfile.encoding=UTF-8 -Dlog4j.configurationFile=$MODULE_HOME/conf/log4j2.xml -jar $MODULE_HOME/lib/wedit.jar &
#echo "OK"
