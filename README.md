# Extract method signatures

## Requirements

*  Java >= 17
*  Maven

## Input data to convert

`data/credentials-methods.json`

## Prerequisite

Download all jar dependencies listed in `data/credentials-methods.json` into the `maven-local-repository` directory using:
```bash
$ ./script/download-jars.sh
```

## Build
```bash
$ mvn clean package
```

## Execute
```bash
$ mvn exec:java
```

## Result

In the `target` directory:
* `credentials-methods.json` list of well formatted and validated method signatures
* `credentials-methods-formatted-input.json` input json formatted to have one signature per line
* `all-jars-methods.json` signatures of all methods in all jars of `maven-local-repository` directory
