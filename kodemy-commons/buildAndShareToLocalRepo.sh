#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 <new_version>"
    exit 1
fi

PACKAGE_NAME=kodemy-commons
VERSION=$1
SUB_PROJECTS=("kodemy-auth" "kodemy-backend" "kodemy-notification" "kodemy-search")
LOCAL_REPO_DIR="local-repo/pl/sknikod/$PACKAGE_NAME/$VERSION"

(./gradlew -Pversion="$VERSION" jar) || {
  echo "Failed to build project"
  exit 1
}

for SUB_PROJECT in "${SUB_PROJECTS[@]}"; do
  DESTINATION_DIR="../$SUB_PROJECT/$LOCAL_REPO_DIR"
  mkdir -p "$DESTINATION_DIR"
  cp "build/libs/$PACKAGE_NAME-$VERSION-plain.jar" "$DESTINATION_DIR/$PACKAGE_NAME-$VERSION.jar" || {
    echo "Failed to copy JAR to $DESTINATION_DIR"
    rm -rd "$DESTINATION_DIR"
    exit 1
  }

  sed "s/\${version}/$VERSION/g" template.pom > "$DESTINATION_DIR/$PACKAGE_NAME-$VERSION.pom" || {
    echo "Failed to generate POM file"
    rm -rd "$DESTINATION_DIR"
    exit 1
  }

  echo "Built and copied $PACKAGE_NAME-$VERSION to $SUB_PROJECT /local-repo"

done