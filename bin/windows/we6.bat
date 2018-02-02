REM Start WEdit. Use classpath inside JAR
REM SET MODULE_HOME=c:\Users\svj\Programm\wedit6
SET MODULE_HOME=%cd ..%
java -Dmodule.home=%MODULE_HOME% -Dfile.encoding=UTF-8 -jar %MODULE_HOME%\lib\wedit.jar
