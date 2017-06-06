@echo off
setlocal

if "%JAVA_HOME%" == "" goto javahome_error
if not exist "%JAVA_HOME%\bin" goto javahome_error
goto havejavahome

:javahome_error
echo invalid JAVA_HOME: %JAVA_HOME%
goto last

:havejavahome

set BUILD_HOME=%~dp0
set ANT_HOME=%BUILD_HOME%\tools\ant-1.7.1

pushd %BUILD_HOME%

if "%1" == "-p" goto useprofile

goto noprofile

:useprofile
call %ANT_HOME%\bin\ant -f build.xml -Dprofile.config="%2" %3 %4 %5 %6 %7 %8 %9 %10
goto end

:noprofile
call %ANT_HOME%\bin\ant -f build.xml -Dprofile.config="%COMPUTERNAME%" %1 %2 %3 %4 %5 %6 %7 %8
goto end

:end
popd
endlocal

:last
