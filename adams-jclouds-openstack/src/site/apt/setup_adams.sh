#!/bin/bash
#
# initializes an ADAMS environment on a cloud instance

cd ~

# java
echo
echo "Java..."
if [ -d "/opt/jdk" ]
then
  echo "...deleting old Java"
  cd /opt
  sudo rm -Rf jdk
  cd ~
fi
echo "...downloading Java"
wget https://adams.cms.waikato.ac.nz/download/jdk/jdk-8u92-linux-x64.tar.gz
echo "...decompressing Java"
tar -xzf jdk-8u92-linux-x64.tar.gz
echo "...installing Java"
sudo mv jdk1.8.0_92 /opt/jdk
echo "...cleaning up Java"
rm -f jdk-*.tar.gz

# adams
echo "adams..."
if [ -d "/opt/adams" ]
then
  echo "...deleting old ADAMS"
  cd /opt
  sudo rm -Rf adams
  cd ~
fi
echo "...downloading ADAMS"
wget https://adams.cms.waikato.ac.nz/snapshots/adams/adams-addons-snapshot-bin.zip
echo "...decompressing ADAMS"
unzip -q adams-addons-snapshot-bin.zip
echo "...installing ADAMS"
sudo mv adams-addons-all-* /opt/adams
echo "...cleaning up ADAMS"
rm -f adams-addons-*.zip

# directories
echo "creating directories..."
if [ -d "/opt/data" ]
then
  echo "...deleting old dirs"
  cd /opt
  sudo rm -Rf data
  cd ~
fi
echo "...creating dirs"
sudo mkdir -p /opt/data/incoming
sudo mkdir -p /opt/data/processing
sudo mkdir -p /opt/data/processed
sudo mkdir -p /opt/data/failed
sudo chmod -R 777 /opt/data

# systemd
if [ -f "/etc/init.d/adams" ]
then
  echo "...deleting old systemd script"
  sudo systemctl disable adams
  sudo rm -f /etc/init.d/adams
fi
echo "...downloading systemd script"
wget https://adams.cms.waikato.ac.nz/download/cloud/systemd_adams
echo "...installing systemd script"
sudo mv systemd_adams /etc/init.d/adams
sudo chmod a+x /etc/init.d/adams
sudo systemctl enable adams

