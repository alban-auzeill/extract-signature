#!/usr/bin/env bash

set -euo pipefail

PROJECT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && cd .. && pwd )
CREDENTIALS_METHODS_JSON="${PROJECT_DIR}/data/credentials-methods.json"

if [ ! -e "${PROJECT_DIR}/maven-local-repository" ]; then
  mkdir "${PROJECT_DIR}/maven-local-repository"
fi

export SETTINGS_PARTH="${PROJECT_DIR}/maven-local-repository/settings.xml"

cat <<- EOF > "${SETTINGS_PARTH}"
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository>${PROJECT_DIR}/maven-local-repository</localRepository>
</settings>
EOF

export DEPENDENCY_GET="org.apache.maven.plugins:maven-dependency-plugin:3.3.0:get"

while read -r ARTIFACT_ID; do
  echo "# ${ARTIFACT_ID}"
  mvn --quiet --batch-mode --settings "${SETTINGS_PARTH}" --global-settings "${SETTINGS_PARTH}" "${DEPENDENCY_GET}" "-Dtransitive=false" "-Dartifact=${ARTIFACT_ID}"
done < <(
  jq -r '.[] | .[0]+":"+.[1]+":LATEST"' "${CREDENTIALS_METHODS_JSON}" |\
    grep -v '_:jdk8.zip' |\
    sort -u
)
