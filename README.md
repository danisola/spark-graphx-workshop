# GraphX Workshop

## Installing Spark Locally

1. Clone this repository `git clone `
1. cd into the local repository
1. Run [setup.sh](setup.sh) script, which will install Spark in `./bin/`

## Background & Theory

Check the presentation [workshop.pdf](workshop.pdf).

## Hands On

To start the Spark shell, run the following command from the local repository:

```bash
$SPARK_HOME/bin/spark-shell --master local[*]
```

Then follow the commands found in [graph-processing.scala](graph-processing.scala)
