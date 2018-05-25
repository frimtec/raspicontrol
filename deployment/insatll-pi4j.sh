#!/bin/bash
######################################
# THE Pi4J PROJECT - INSTALL SCRIPT
######################################
#
#  curl -s get.pi4j.com/install | sudo bash
#

# download and install the Pi4J GPG public key
echo ====================================================
echo INSTALLING Pi4J GPG PUBLIC KEY
echo ====================================================
curl http://get.pi4j.com/pi4j.gpg | apt-key add -

# download and install the pi4j apt repository list
echo ====================================================
echo ADDING Pi4J APT REPOSITORY
echo ====================================================
sudo wget http://get.pi4j.com/pi4j.list -O /etc/apt/sources.list.d/pi4j.list

# update the apt package list
echo ====================================================
echo UPDATING APT REPOSITORIES
echo ====================================================
apt-get update -o Dir::Etc::sourcelist="sources.list.d/pi4j.list"  -o Dir::Etc::sourceparts="-" -o APT::Get::List-Cleanup="0"

# download and install the Pi4J project
echo ====================================================
echo INSTALLING Pi4J
echo ====================================================
apt-get install pi4j

echo ====================================================
echo Pi4J INSTALLATION COMPLETE
echo ====================================================
echo
echo The Pi4J JAR files are located at:
echo   /opt/pi4j/lib
echo
echo Example Java programs are located at:
echo   /opt/pi4j/examples
echo
echo You can compile the examples using this script:
echo   sudo /opt/pi4j/examples/build
echo
echo Please see http://www.pi4j.com for more information.
echo
