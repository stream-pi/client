
echo $REQ_MODULES
echo $VERSION

$JPACKAGE_HOME/bin/jpackage \
--module-path $JAVAFX_JMODS/:target/lib/ \
--add-modules $REQ_MODULES \
--input target/lib \
--main-jar client-$VERSION.jar \
--main-class $MAIN_CLASS \
--description "Stream-Pi Client" \
--vendor "Stream-Pi" \
--verbose \
--copyright "Copyright 2019-21 Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)" \
--dest $INSTALL_DIR \
--name 'Stream-Pi Client' \
--java-options '-Dprism.verbose=true -Djavafx.verbose=true -Dprism.dirtyopts=false' \
--arguments "-DStream-Pi.startupRunnerFileName='Stream-Pi Client'" \
"$@"
