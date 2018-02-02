REM Start WEdit. USE classpath from directory (for simple add new jar)
SET MODULE_HOME=c:/Programm/My/wedit
SET JAVA_HOME=C:/Programm/Java/jdk1.5.0_01
SET SP=%CLASSPATH%;%MODULE_HOME%/lib/wedit.jar;%MODULE_HOME%/lib/svjUtil.jar;%MODULE_HOME%/lib/log4j.jar;%MODULE_HOME%/lib/itext-2.0.6.jar
%JAVA_HOME%/bin/java -Dmodule.home=%MODULE_HOME% -Dfile.encoding=UTF-8 -classpath %SP% svj.wedit.WEdit
