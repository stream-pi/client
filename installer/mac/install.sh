#!/bin/bash
echo "StreamPi Client Installer"
echo "Alpha 0.0.5"
echo -n "Enter Screen Width (No. of Pixels) : "
read screen_width
echo -n "Enter Screen Height (No. Of Pixels) : "
read screen_height
echo "Installing Fonts..."
echo "...Done!"
echo "Copying Files..."
mkdir ~/Applications/StreamPi/
sudo cp -r actions ~/Applications/StreamPi/
sudo cp -r animatefx ~/Applications/StreamPi/
sudo cp -r assets ~/Applications/StreamPi/
sudo cp -r com ~/Applications/StreamPi/
sudo cp -r css ~/Applications/StreamPi/
sudo cp -r jdk ~/Applications/StreamPi/
sudo cp -r net ~/Applications/StreamPi/
sudo cp -r StreamPiClient ~/Applications/StreamPi/
sudo cp start_streampi ~/Applications/StreamPi/
cd ~/Applications/StreamPi/
sudo chmod +x jdk/bin/java
echo "${screen_width}::${screen_height}::black::192.168.0.102::69::test1::1::0::100::10:::" >> config
sudo chmod +x start_streampi
echo "...Done!"
echo "Installation done!  Run the app by doing 'sudo ~/Applications/StreamPi/start_streampi' to start the app"
