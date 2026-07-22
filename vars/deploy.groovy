#!/usr/bin/env groovy

import com.example.Docker

def call(String host,
         String credentialId,
         String imageName,
         String containerName,
         String port) {

    Docker docker = new Docker(this)

    docker.deploy(
        host,
        credentialId,
        imageName,
        containerName,
        port
    )
}