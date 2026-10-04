@echo off
rem Vanilla Plus server: syncs the pack with packwiz, keeps NeoForge on the
rem pack's version, then runs the server. Restarts after a stop or crash, and
rem every restart pulls the latest pack. Close the window during the countdown to quit.
setlocal
cd /d "%~dp0"

if not defined PACK_URL set "PACK_URL=https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml"
if not defined MEMORY set "MEMORY=6G"
set "BOOTSTRAP_URL=https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar"

findstr /b /c:"eula=true" eula.txt >nul 2>&1 || (
  echo eula=false> eula.txt
  echo Read https://aka.ms/MinecraftEULA, then set eula=true in eula.txt and run this again.
  pause
  exit /b 1
)

if not exist server.properties copy server.properties.defaults server.properties >nul
if not exist packwiz-installer-bootstrap.jar curl -fsSL -o packwiz-installer-bootstrap.jar "%BOOTSTRAP_URL%" || exit /b 1

:loop
rem NeoForge version comes from the pack, so a loader bump updates the server too
set "NEOFORGE="
for /f "tokens=2 delims==" %%v in ('curl -fsSL "%PACK_URL%" ^| findstr /b /c:"neoforge = "') do set "NEOFORGE=%%~v"
if not defined NEOFORGE (
  echo Couldn't read the NeoForge version from %PACK_URL%
  pause
  exit /b 1
)
set "NEOFORGE=%NEOFORGE: =%"
set "NEOFORGE=%NEOFORGE:"=%"
if not exist "libraries\net\neoforged\neoforge\%NEOFORGE%\win_args.txt" (
  echo Installing NeoForge %NEOFORGE%...
  curl -fsSL -o neoforge-installer.jar "https://maven.neoforged.net/releases/net/neoforged/neoforge/%NEOFORGE%/neoforge-%NEOFORGE%-installer.jar" || exit /b 1
  java -jar neoforge-installer.jar --installServer || exit /b 1
  del /q neoforge-installer.jar neoforge-installer.jar.log run.sh run.bat 2>nul
)

java -jar packwiz-installer-bootstrap.jar -g -s server "%PACK_URL%"

java -Xms%MEMORY% -Xmx%MEMORY% @libraries/net/neoforged/neoforge/%NEOFORGE%/win_args.txt nogui %*

echo Server stopped. Restarting in 10 seconds (close the window to quit)...
timeout /t 10 /nobreak >nul
goto loop
