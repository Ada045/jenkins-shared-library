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

    def deploy(String imageName) {

        def dockerCmd = """
            docker pull ${imageName}
            docker stop java-app || true
            docker rm java-app || true
            docker run -d --name java-app -p 8081:8081 ${imageName}
        """

        sshagent(['ec2-server-key']) {
            script.sh "ssh -o StrictHostKeyChecking=no ec2-user@63.177.99.170 '${dockerCmd}'"
        }
    }
}
