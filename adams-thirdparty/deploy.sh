#!/bin/bash
#
# Imports knir 3rd party libraries into Nexus
#
# Author: FracPete (fracpete at waikato dot ac dot nz)
# Version: $Revision$

HOST=https://adams.cms.waikato.ac.nz
REPO=adams-thirdparty
REPO_URL=$HOST/nexus/content/repositories/$REPO

LIB_DIR=./

GROUP=TauP
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=TauP-no-sac-timeseries \
  -Dversion=1.1.7 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/TauP-no-sac-timeseries-1.1.7.jar \
  -DgeneratePom.description="Seismic Analysis Code (SAC) is a general purpose interactive program designed for the study of sequential signals, especially timeseries data. http://www.iris.edu/dms/nodes/dmc/software/downloads/sac/" \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL

#GROUP=???
#mvn deploy:deploy-file -DgroupId=$GROUP \
#  -DartifactId=??? \
#  -Dversion=??? \
#  -Dpackaging=jar \
#  -Dfile=$LIB_DIR/???.jar \
#  -Dsources=$LIB_DIR/???-sources.jar \
#  -DgeneratePom.description="???" \
#  -DrepositoryId=$REPO \
#  -Durl=$REPO_URL

