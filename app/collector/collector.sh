#!/bin/sh

cd target && \
  java -Dlog4.configuration=file:configuration/log4j.xml \
  -Dlogback.configuration=configuration/logback.xml \
  -jar $(find plugins -name 'org.eclipse.equinox.launcher_*.jar') "$@"
