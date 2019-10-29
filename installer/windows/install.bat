@ECHO OFF
echo StreamPi Client Installer
echo Alpha 0.0.5
echo Enter Screen Width (No. Of Pixels) : 
set /P width=
echo Enter Screen Height (No. Of Pixels) : 
set /P height=
echo Install fonts here
echo Where do you wish to install the client?
echo Note: copy and paste the root directory and the installer will create a new folder there called "StreamPi"
set /P direc=
mkdir "%direc%/StreamPi"
break>config
echo %width%::%height%::black::192.168.0.31::1024::test1::1::1::100::10:: >> config
robocopy %~dp0. "%direc%\StreamPi" /e /is /xf install.bat
pause

