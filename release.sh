#!/bin/bash

echo "Did you already update the CHANGELOG.md file ? And eventually the README.md file ? (yes/no)"
read answer

if [[ "$answer" == "yes" ]]; then
    export GPG_TTY=$(tty)
    mvn clean release:clean release:prepare -P release
    echo "Execute 'mvn release:perform' right now ??? (yes/no)"
    read release_perform
    if [[ "$release_perform" == "yes" ]]; then
        mvn release:perform -P release
    fi
else
    echo >&2 "Please, check that the CHANGELOG.md file is up to date !!!"
fi

