#!/usr/bin/env groovy

package com.example

class Docker implements Serializable {

    private final def script

    Docker(script) {
        this.script = script
    }

    def buildDockerImage(String imageName) {
        script.echo "Building Docker image: ${imageName}"
        script.sh "docker build -t '${imageName}' ."
    }

    def dockerLogin() {
        script.echo 'Logging into Docker...'
        script.withCredentials([
            script.usernamePassword(
                credentialsId: 'docker-credentials',
                passwordVariable: 'PASS',
                usernameVariable: 'USER'
            )
        ]) {
            script.sh 'echo "$PASS" | docker login -u "$USER" --password-stdin'
        }
    }

    def dockerPush(String imageName) {
        script.echo 'Pushing image into Docker repo...'
        script.sh "docker push '${imageName}'"
    }

    def deploy(String host,
           String credentialId,
           String imageName,
           String containerName,
           String port) {

    script.sshagent([credentialId]) {
        script.sh """
            ssh -o StrictHostKeyChecking=no ec2-user@${host} '
                docker pull ${imageName}
                docker stop ${containerName} || true
                docker rm ${containerName} || true
                docker run -d --name ${containerName} -p ${port}:${port} ${imageName}
            '
        """
    }
}
}
