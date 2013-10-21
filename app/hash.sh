#!/bin/bash
#
# Password hash generator.
# Generated SHA-256 hash value can be used by Spring Security.
#

passwd="$1"
[ "$passwd" == "" ] && echo "Password not specified" && exit 1

line=$( echo -n "$1" | sha256sum -t )
arr=($line)
echo "${arr[0]}"
