#!/bin/bash

mvn clean verify release:clean release:prepare -P release
mvn release:perform
