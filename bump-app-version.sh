#!/bin/bash

function log() {
  logger -s -t "$1" -p user."$2" "PID $$: $3"
}

function isFileAccessible() {
  local FILE=$1

  if [ ! -f $FILE ] || [ ! -r $FILE ] || [ ! -w $FILE ]
  then
    log "$0: ${APP_NAME}" INFO "Unable to access file ${FILE}. Check if file is a regular file and has rw permissions."
    return 1
  fi
  return 0
}

function help() {
    echo "Valid Options: [-v <app version number>]"
}

while getopts "v:" OPTIONS
do
  case "$OPTIONS" in
    v)
        log "$0: ${APP_NAME}" INFO "Bumping application version to ${NEW_VERSION}...."
        NEW_VERSION="$OPTARG"
        VERSION_FILE=VERSION.txt
        POM_FILE=pom.xml
        RELEASENOTES_FILE=RELEASENOTES.md
        README_FILE=README.md
        FILE_LIST=("$VERSION_FILE" "$POM_FILE" "$RELEASENOTES_FILE" "$README_FILE")

        for FILE in ${FILE_LIST[@]}
        do
          isFileAccessible $FILE
          if [ $? -ne 0 ]
          then
            log "$0: ${APP_NAME}" INFO "failed to bump application version to ${NEW_VERSION}...."
            exit 1
          fi
        done

        APP_NAME=$(sed -n "14p" pom.xml | sed -e "s/<name>//" | sed -e "s+</name>++" | xargs)
        sed -i "1s/.*/${NEW_VERSION}/" $VERSION_FILE
        sed -i "13s+.*+	<version>${NEW_VERSION}</version>+" $POM_FILE
        sed -i "s/# Release Notes for v.*/# Release Notes for v${NEW_VERSION}/" $RELEASENOTES_FILE
        sed -i "s/# ${APP_NAME} v.*/# ${APP_NAME} v${NEW_VERSION}/" $README_FILE

        log "$0: ${APP_NAME}" INFO "Application version bumped to ${NEW_VERSION}...."
      ;;
    *)
      echo help
      ;;
  esac
done
exit 0