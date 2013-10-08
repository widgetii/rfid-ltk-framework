#!/bin/sh

cd target && java -jar $(find plugins -name 'org.eclipse.equinox.launcher_*.jar') "$@"
