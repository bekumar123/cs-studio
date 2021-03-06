@echo off

set JAVA_CMD=java.exe
set LAUNCHER=org.eclipse.equinox.launcher_1.1.1.R36x_v20101122_1400.jar
set MAINCLASS=org.eclipse.equinox.launcher.Main
set ECLIPSEAPPLIC=org.csstudio.ams.remotetool.AmsRemoteToolApplication

set LOCAL_USERNAME=applic
set LOCAL_HOSTNAME=krykams

:abfrage
cls
echo.
echo Welche AMS-Anwendung soll beendet werden?
echo.
echo     [1] AmsDistributor
echo     [2] AmsMessageMinder
echo     [3] AmsDepartmentDecision
echo     [4] AmsEMailConnector
echo     [5] AmsSmsConnector
echo     [6] AmsJmsConnector
echo     [7] AmsVoicemailConnector
echo.
set /P AUSWAHL=Geben Sie die Nummer ein: 

if "%AUSWAHL%" == "" goto abfrage
if NOT "%AUSWAHL%" == "1" goto app2
set APPLICATIONNAME=ams-distributor
goto stopIt

:app2
if NOT "%AUSWAHL%" == "2" goto app3
set APPLICATIONNAME=ams-message-minder
goto stopIt

:app3
if NOT "%AUSWAHL%" == "3" goto app4
set APPLICATIONNAME=ams-department-decision
goto stopIt

:app4
if NOT "%AUSWAHL%" == "4" goto app5
set APPLICATIONNAME=ams-mail-connector
goto stopIt

:app5
if NOT "%AUSWAHL%" == "5" goto app6
set APPLICATIONNAME=ams-sms-connector
goto stopIt

:app6
if NOT "%AUSWAHL%" == "6" goto app7
set APPLICATIONNAME=ams-jms-connector
goto stopIt

:app7
if NOT "%AUSWAHL%" == "7" goto abfrage
set APPLICATIONNAME=ams-voicemail-connector

:stopIt
echo.
echo Try to stop %APPLICATIONNAME%
echo.
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname %APPLICATIONNAME% -username %LOCAL_USERNAME% -pw admin4AMS
if "%ERRORLEVEL%" == "0" goto ok
if "%ERRORLEVEL%" == "1" goto error1
if "%ERRORLEVEL%" == "2" goto error2
if "%ERRORLEVEL%" == "3" goto error3
if "%ERRORLEVEL%" == "4" goto error4
if "%ERRORLEVEL%" == "5" goto error5

color cf
echo.
echo Nicht definierter Fehler.
echo.
pause
goto continue

:ok
echo.
echo %APPLICATIONNAME% stopped
goto end

:error1
color cf
echo.
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto end

:error2
color cf
echo.
echo FEHLER: Anwendung ams-jms-connector nicht gefunden oder nicht aktiv
goto end

:error3
color cf
echo.
echo FEHLER: Administrationspasswort fuer ams-jms-connector ist nicht gueltig
goto end

:error4
color cf
echo.
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto end

:error5
color cf
echo.
echo FEHLER: Unbekannte Ursache

:end
set LOCAL_USERNAME=
set LOCAL_HOSTNAME=
set JAVA_CMD=
set LAUNCHER=
set MAINCLASS=
set ECLIPSEAPPLIC=
set APPLICATIONNAME=

echo.
pause
