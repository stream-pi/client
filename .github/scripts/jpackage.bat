set /p REQ_MODULES=<req_modules.txt
set /p VERSION=<version.txt

%JPACKAGE_HOME%\bin\jpackage ^
--module-path %JAVAFX_JMODS%;target/lib/ ^
--add-modules %REQ_MODULES% ^
--name "Stream-Pi Client" ^
--description "Stream-Pi Client" ^
--copyright "Copyright 2019-21 Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)" ^
--input target/lib ^
--main-jar client-%VERSION%.jar ^
--type msi ^
--java-options "-Dprism.verbose=true -Djavafx.verbose=true -Dprism.dirtyopts=false -DStream-Pi.startupRunnerFileName='Stream-Pi Client.exe'" ^
--main-class %MAIN_CLASS% ^
--icon assets/windows-icon.ico ^
--dest %INSTALL_DIR% ^
--win-dir-chooser ^
--win-menu ^
--win-menu-group "Stream-Pi" ^
--vendor "Stream-Pi"

echo Done now renaming ..
cd %INSTALL_DIR%
echo run dir
dir
ren *.msi stream-pi-client-windows-%ARCH%-%VERSION%-installer.msi
dir