#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: $0 <new_version>"
    exit 1
fi

VERSION=$1
SUB_PROJECTS=("kodemy-auth" "kodemy-backend" "kodemy-notification" "kodemy-search")
LOCAL_REPO_DIR="local-repo/pl/sknikod/kodemy-common/$VERSION"

(./gradlew -Pversion="$VERSION" jar) || {
  echo "Failed to build project"
  exit 1
}

for SUB_PROJECT in "${SUB_PROJECTS[@]}"; do
  DESTINATION_DIR="../$SUB_PROJECT/$LOCAL_REPO_DIR"
  mkdir -p "$DESTINATION_DIR"
  cp "build/libs/kodemy-common-$VERSION-plain.jar" "$DESTINATION_DIR/kodemy-common-$VERSION.jar" || {
    echo "Failed to copy JAR to $DESTINATION_DIR"
    rm -rd "$DESTINATION_DIR"
    exit 1
  }

  sed "s/\${version}/$VERSION/g" template.pom > "$DESTINATION_DIR/kodemy-common-$VERSION.pom" || {
    echo "Failed to generate POM file"
    rm -rd "$DESTINATION_DIR"
    exit 1
  }

done

echo "Built and shared kodemy-common-$VERSION.jar and kodemy-common-$VERSION.pom to all $LOCAL_REPO_DIR"