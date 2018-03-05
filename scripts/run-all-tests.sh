#!/bin/sh

# Run all tests with code coverage.
./gradlew :library:clean :library:jacocoTestReport
