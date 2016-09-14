#!/bin/sh

# SPARK_VERSION=spark-1.6.2-bin-hadoop2.6
SPARK_VERSION=spark-2.0.0-bin-hadoop2.7
SPARK_URL=https://d3kbcqa49mib13.cloudfront.net/$SPARK_VERSION.tgz # Apache Spark official Cloudfront distribution
INSTALL_DIR=`pwd`/bin
SPARK_HOME=$INSTALL_DIR/$SPARK_VERSION

# Downloading and extracting Spark
mkdir -p $INSTALL_DIR
curl $SPARK_URL --output $INSTALL_DIR/$SPARK_VERSION.tgz
tar -zxf $INSTALL_DIR/$SPARK_VERSION.tgz -C $INSTALL_DIR
conf_file=$SPARK_HOME/conf/spark-defaults.conf
echo "spark.executor.memory 2g" >> $conf_file
