:: Licensed to the Apache Software Foundation (ASF) under one
:: or more contributor license agreements.  See the NOTICE file
:: distributed with this work for additional information
:: regarding copyright ownership.  The ASF licenses this file
:: to you under the Apache License, Version 2.0 (the
:: "License"); you may not use this file except in compliance
:: with the License.  You may obtain a copy of the License at
::
::   http://www.apache.org/licenses/LICENSE-2.0
::
:: Unless required by applicable law or agreed to in writing,
:: software distributed under the License is distributed on an
:: "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
:: KIND, either express or implied.  See the License for the
:: specific language governing permissions and limitations
:: under the License.

:: Windows launcher script for Gremlin Server

@ECHO OFF
SETLOCAL EnableDelayedExpansion
SET work=%CD%

IF [%work:~-3%]==[bin] CD ..

IF NOT DEFINED hugegraph_HOME (
    SET hugegraph_HOME=%CD%
)

:: location of the hugegraph lib directory
SET hugegraph_LIB=%hugegraph_HOME%\lib

:: location of the hugegraph extensions directory
IF NOT DEFINED hugegraph_EXT (
    SET hugegraph_EXT=%hugegraph_HOME%\ext
)

:: Set default message threshold for Log4j Gremlin's console appender
IF NOT DEFINED GREMLIN_LOG_LEVEL (
    SET GREMLIN_LOG_LEVEL=WARN
)

:: Hadoop winutils.exe needs to be available because hadoop-gremlin is installed and active by default
IF NOT DEFINED HADOOP_HOME (
    SET hugegraph_WINUTILS=%hugegraph_HOME%\bin\winutils.exe
    IF EXIST !hugegraph_WINUTILS! (
        SET HADOOP_HOME=%hugegraph_HOME%
    ) ELSE (
        ECHO HADOOP_HOME is not set.
        ECHO Download http://public-repo-1.hortonworks.com/hdp-win-alpha/winutils.exe
        ECHO Place it under !hugegraph_WINUTILS!
        PAUSE
        GOTO :eof
    )
)

:: set HADOOP_GREMLIN_LIBS by default to the hugegraph lib
IF NOT DEFINED HADOOP_GREMLIN_LIBS (
    SET HADOOP_GREMLIN_LIBS=%hugegraph_LIB%
)

CD %hugegraph_LIB%

FOR /F "tokens=*" %%G IN ('dir /b "hugegraph-*.jar"') DO SET hugegraph_JARS=!hugegraph_JARS!;%hugegraph_LIB%\%%G

FOR /F "tokens=*" %%G IN ('dir /b "jamm-*.jar"') DO SET JAMM_JAR=%hugegraph_LIB%\%%G

FOR /F "tokens=*" %%G IN ('dir /b "slf4j-log4j12-*.jar"') DO SET SLF4J_LOG4J_JAR=%hugegraph_LIB%\%%G

CD %hugegraph_EXT%

FOR /D /r %%i in (*) do (
    SET EXTDIR_JARS=!EXTDIR_JARS!;%%i\*
)

CD %hugegraph_HOME%

:: put slf4j-log4j12 and hugegraph jars first because of conflict with logback
SET CP=%CLASSPATH%;%SLF4J_LOG4J_JAR%;%hugegraph_JARS%;%hugegraph_LIB%\*;%EXTDIR_JARS%

:: to debug plugin :install include -Divy.message.logger.level=4 -Dgroovy.grape.report.downloads=true
:: to debug log4j include -Dlog4j.debug=true
IF NOT DEFINED JAVA_OPTIONS (
 SET JAVA_OPTIONS=-Xms32m -Xmx512m ^
 -Dhugegraph.logdir=%hugegraph_HOME%\log ^
 -Dtinkerpop.ext=%hugegraph_EXT% ^
 -Dlogback.configurationFile=conf\logback.xml ^
 -Dlog4j.configuration=file:/%hugegraph_HOME%\conf\gremlin-server\log4j-server.properties ^
 -Dlog4j.debug=true ^
 -Dgremlin.log4j.level=%GREMLIN_LOG_LEVEL% ^
 -javaagent:%JAMM_JAR%
)


:: Launch the application

IF "%1" == "-i" ( 
    GOTO install
) else (
    GOTO server
)

:: Start the Gremlin Server

:server

IF "%1" == "" (
  SET GREMLIN_SERVER_YAML=%hugegraph_HOME%\conf\gremlin-server\gremlin-server.yaml
) ELSE (
  SET GREMLIN_SERVER_YAML=%1
)

java %JAVA_OPTIONS% %JAVA_ARGS% -cp %CP% org.apache.tinkerpop.gremlin.server.GremlinServer %GREMLIN_SERVER_YAML%

GOTO finally

:: Install a plugin

:install

SET GRP_ART_VER=
SHIFT

:loop1
IF "%1"=="" GOTO after_loop
SET GRP_ART_VER=%GRP_ART_VER% %1
SHIFT
GOTO loop1

:after_loop

java %JAVA_OPTIONS% %JAVA_ARGS% -cp %CP% org.apache.tinkerpop.gremlin.server.util.GremlinServerInstall %GRP_ART_VER%

GOTO finally

:finally

ENDLOCAL