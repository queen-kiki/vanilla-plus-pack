@echo off
rem Pre-launch for the local dev instance: refresh the index of this working
rem tree, then install it into the instance. No push needed.
cd /d "%~dp0" || exit /b 1
"%USERPROFILE%\bin\packwiz.exe" refresh || exit /b 1
cd /d "%INST_MC_DIR%" || exit /b 1
"%INST_JAVA%" -jar packwiz-installer-bootstrap.jar --bootstrap-no-update "%~dp0pack.toml"
