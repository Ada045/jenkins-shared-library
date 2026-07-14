#!/usr/bin/env groovy

def call() {
    echo "building the application for ${env.BRANCH_NAME ?: 'unknown'}"
    sh 'mvn clean package'
}
