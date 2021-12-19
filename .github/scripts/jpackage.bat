::
:: Stream-Pi - Free & Open-Source Modular Cross-Platform Programmable Macro Pad
:: Copyright (C) 2019-2021  Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
::
:: This program is free software: you can redistribute it and/or modify
:: it under the terms of the GNU General Public License as published by
:: the Free Software Foundation, either version 3 of the License, or
:: (at your option) any later version.
:: This program is distributed in the hope that it will be useful,
:: but WITHOUT ANY WARRANTY; without even the implied warranty of
:: MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
:: GNU General Public License for more details.
::

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
--java-options "-Djavafx.verbose=true -Dprism.verbose=true -Dprism.lcdtext=false -Dprism.dirtyopts=false" ^
--arguments "Stream-Pi.startupRunnerFileName='Stream-Pi Client.exe'" ^
--main-class %MAIN_CLASS% ^
--icon assets/windows-icon.ico ^
--dest %INSTALL_DIR% ^
--win-dir-chooser ^
--win-menu ^
--win-menu-group "Stream-Pi" ^
--vendor "Debayan Sutradhar (rnayabed), Samuel Quiñones (SamuelQuinones)"

echo Done now renaming ..
cd %INSTALL_DIR%
echo run dir
dir
ren *.msi stream-pi-client-windows-%ARCH%-%VERSION%-installer.msi
dir