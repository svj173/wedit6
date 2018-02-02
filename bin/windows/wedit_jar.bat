REM Start WEdit. Use classpath inside JAR
SET MODULE_HOME=c:/Programm/My/wedit
SET JAVA_HOME=C:/Programm/Java/JDK/1.6.0
%JAVA_HOME%/bin/java -Dmodule.home=%MODULE_HOME% -Dfile.encoding=UTF-8 -jar %MODULE_HOME%/lib/wedit.jar
