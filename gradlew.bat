@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz /?
if "%~1"=="/?" goto mainHelp
if "%~1"=="-?" goto mainHelp
if "%~1"=="--help" goto mainHelp
if "%~1"=="-h" goto mainHelp

@rem Check for -Dsystem.property=value command-line argument
set JAVA_OPTS_CMD_LINE_ARGS=
:scanArgs
if /I "%~1" == "-D" (
    set JAVA_OPTS_CMD_LINE_ARGS=%JAVA_OPTS_CMD_LINE_ARGS% %1%2
    shift
    shift
    goto scanArgs
)
if /I "%~1" == "--daemon" (
    set USE_GRADLE_DAEMON=true
    shift
    goto scanArgs
)
if /I "%~1" == "--no-daemon" (
    set USE_GRADLE_DAEMON=false
    shift
    goto scanArgs
)
if /I "%~1" == "-d" (
    set DEBUG_GRADLE_SCRIPT=true
    shift
    goto scanArgs
)
if /I "%~1" == "--debug-jvm" (
    set DEBUG_GRADLE_APPLAUNCHER=true
    shift
    goto scanArgs
)


@rem Escape application args
set APP_ARGS=
:append_args
if "%~1"=="" goto args_done
set APP_ARGS=%APP_ARGS% %1
shift
goto append_args
:args_done

@rem Collect all arguments for the java command, following the shell quoting and substitution rules
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %JAVA_OPTS_CMD_LINE_ARGS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %APP_ARGS%

:fail
if "%OS%" == "Windows_NT" endlocal
exit /b 1

:mainHelp
echo.
echo To see Gradle help run: %APP_BASE_NAME% help
echo.
goto end

:end
if "%OS%" == "Windows_NT" endlocal
exit /b 0
