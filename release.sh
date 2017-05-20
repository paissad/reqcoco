#!/bin/bash

mvn clean release:clean release:prepare -P release
mvn release:perform
