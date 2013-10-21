#!/bin/sh

cd target && \
  java -Dlog4j.configuration=file:configuration/log4j.xml \
  -Dlogback.configurationFile=configuration/logback.xml \
  -jar $(find plugins -name 'org.eclipse.equinox.launcher_*.jar') "$@"
