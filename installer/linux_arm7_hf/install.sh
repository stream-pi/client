#!/usr/bin/env bash
echo "StreamPi Client Installer"
echo "Alpha"
echo -n "Enter Screen Width (No. Of Pixels) : "
read screen_width
echo -n "Enter Screen Height (No. Of Pixels) : "
read screen_height
echo "Installing Fonts ..."
sudo cp Roboto-Regular.ttf /usr/share/fonts/truetype/
fc-cache -v -f
echo "... Done!"
echo "Copying Files ..."
mkdir /home/pi/StreamPi/
sudo cp -r actions /home/pi/StreamPi/
sudo cp -r animatefx /home/pi/StreamPi/
sudo cp -r assets /home/pi/StreamPi/
sudo cp -r com /home/pi/StreamPi/
sudo cp -r css /home/pi/StreamPi/
sudo cp -r jdk /home/pi/StreamPi/
sudo cp -r net /home/pi/StreamPi/
sudo cp -r StreamPiClient /home/pi/StreamPi/
sudo cp start_streampi /home/pi/StreamPi/
cd /home/pi/StreamPi/
sudo chmod +x jdk/bin/java
echo "${screen_width}::${screen_height}::black::192.168.0.102::69::test1::1::1::100::10:::" >> config
sudo chmod +x start_streampi
echo "... Done!"
echo "Setting Up Essentials to enable Hardware Accelartion ..."
cd /opt/vc/lib
ln -s libbrcmEGL.so libEGL.so
ln -s libbrcmGLESv2.so libGLESv2.so
echo "... Done!"
echo "Set Run at startup/boot ? [y,n]"
read input
if [[ $input == "Y" || $input == "y" ]]; then
    echo "Setting up run at boot..."
    sed -i -e '$i cd /home/pi/StreamPi/\nsudo jdk/bin/java -Dcom.sun.javafx.isEmbedded=true -Dcom.sun.javafx.touch=true -Dcom.sun.javafx.virtualKeyboard=javafx StreamPiClient.Main\n' /etc/rc.local
    echo "... Done!"
    echo "Installation done. Reboot to see changes :)"
else
    echo "Skipping run at boot ..."
    echo "Installation done. run 'sudo /home/pi/StreamPi/start_streampi' to start the program (Since run at bootup is disabled)"
fi
read -n 1 -s -r
