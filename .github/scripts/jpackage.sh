
#
# Stream-Pi - Free & Open-Source Modular Cross-Platform Programmable Macro Pad
# Copyright (C) 2019-2021  Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#

echo $REQ_MODULES
echo $VERSION

$JPACKAGE_HOME/bin/jpackage \
--module-path $JAVAFX_JMODS/:target/lib/ \
--add-modules $REQ_MODULES \
--input target/lib \
--main-jar client-$VERSION.jar \
--main-class $MAIN_CLASS \
--description "Stream-Pi Client" \
--vendor "Debayan Sutradhar (rnayabed), Samuel Quiñones (SamuelQuinones)" \
--verbose \
--copyright "Copyright 2019-21 Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)" \
--dest $INSTALL_DIR \
--name 'Stream-Pi Client' \
--java-options '-Djavafx.verbose=true -Dprism.verbose=true -Dprism.lcdtext=false -Dprism.dirtyopts=false -Dprism.forceGPU=true' \
--arguments "Stream-Pi.startupRunnerFileName='Stream-Pi Client' Stream-Pi.xMode=true Stream-Pi.appendPathBeforeRunnerFileToOvercomeJPackageLimitation=true" \
"$@"
