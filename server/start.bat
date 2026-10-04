@echo off
rem Vanilla Plus server: syncs the pack with packwiz, keeps NeoForge on the
rem pack's version, then runs the server. Restarts after a stop or crash, and
rem every restart pulls the latest pack. Close the window during the countdown to quit.
setlocal
cd /d "%~dp0"

if not defined PACK_URL set "PACK_URL=https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml"
if not defined MEMORY set "MEMORY=6G"

rem Pinned and hash-checked, so a compromised or replaced release can't slip in.
rem packwiz-installer's own updater is turned off below (--bootstrap-no-update).
set "BOOTSTRAP_URL=https://github.com/packwiz/packwiz-installer-bootstrap/releases/download/v0.0.3/packwiz-installer-bootstrap.jar"
set "BOOTSTRAP_SHA256=a8fbb24dc604278e97f4688e82d3d91a318b98efc08d5dbfcbcbcab6443d116c"
set "INSTALLER_URL=https://github.com/packwiz/packwiz-installer/releases/download/v0.5.14/packwiz-installer.jar"
set "INSTALLER_SHA256=c9f646908d340d84773948a9a7d98bc1dae250d35e1016dc6e2b8459760b5598"

findstr /b /c:"eula=true" eula.txt >nul 2>&1 || (
  echo eula=false> eula.txt
  echo Read https://aka.ms/MinecraftEULA, then set eula=true in eula.txt and run this again.
  pause
  exit /b 1
)

if not exist server.properties copy server.properties.defaults server.properties >nul

:loop
call :fetch "%BOOTSTRAP_URL%" %BOOTSTRAP_SHA256% packwiz-installer-bootstrap.jar || goto fail
call :fetch "%INSTALLER_URL%" %INSTALLER_SHA256% packwiz-installer.jar || goto fail

rem NeoForge version comes from the pack, so a loader bump updates the server too.
rem Its installer hash sits next to the pack in server/neoforge.sha256.
set "NEOFORGE="
for /f "tokens=2 delims==" %%v in ('curl -fsSL "%PACK_URL%" ^| findstr /b /c:"neoforge = "') do set "NEOFORGE=%%~v"
if not defined NEOFORGE (
  echo Couldn't read the NeoForge version from %PACK_URL%
  goto fail
)
set "NEOFORGE=%NEOFORGE: =%"
set "NEOFORGE=%NEOFORGE:"=%"
if not exist "libraries\net\neoforged\neoforge\%NEOFORGE%\win_args.txt" (
  call :install_neoforge || goto fail
)

java -jar packwiz-installer-bootstrap.jar -g --bootstrap-no-update -s server "%PACK_URL%"

java -Xms%MEMORY% -Xmx%MEMORY% -XX:+UseZGC -XX:+IgnoreUnrecognizedVMOptions -XX:+ZGenerational @libraries/net/neoforged/neoforge/%NEOFORGE%/win_args.txt nogui %*

echo Server stopped. Restarting in 10 seconds (close the window to quit)...
"%SystemRoot%\System32\timeout.exe" /t 10 /nobreak >nul
goto loop

:fail
pause
exit /b 1

:install_neoforge
set "NEOFORGE_JAR=neoforge-%NEOFORGE%-installer.jar"
set "NEOFORGE_SHA256="
for /f "tokens=1,2" %%a in ('curl -fsSL "%PACK_URL:/pack.toml=%/server/neoforge.sha256"') do if "%%b"=="%NEOFORGE_JAR%" set "NEOFORGE_SHA256=%%a"
if not defined NEOFORGE_SHA256 (
  echo No hash for %NEOFORGE_JAR% in server/neoforge.sha256, refusing to install it.
  exit /b 1
)
echo Installing NeoForge %NEOFORGE%...
call :fetch "https://maven.neoforged.net/releases/net/neoforged/neoforge/%NEOFORGE%/%NEOFORGE_JAR%" %NEOFORGE_SHA256% neoforge-installer.jar || exit /b 1
java -jar neoforge-installer.jar --installServer || exit /b 1
del /q neoforge-installer.jar neoforge-installer.jar.log run.sh run.bat 2>nul
exit /b 0

rem Downloads %1 to %3 unless %3 already has SHA-256 %2. Refuses a file that doesn't match.
:fetch
if not exist "%~3" goto fetch_download
call :sha256 "%~3"
if /i "%HASH%"=="%~2" exit /b 0
:fetch_download
curl -fsSL -o "%~3.part" "%~1" || exit /b 1
call :sha256 "%~3.part"
if /i not "%HASH%"=="%~2" (
  del /q "%~3.part"
  echo Hash mismatch for %~1, refusing to use it.
  exit /b 1
)
move /y "%~3.part" "%~3" >nul
exit /b 0

:sha256
set "HASH="
for /f "skip=1 delims=" %%h in ('certutil -hashfile "%~1" SHA256') do if not defined HASH set "HASH=%%h"
set "HASH=%HASH: =%"
exit /b 0
