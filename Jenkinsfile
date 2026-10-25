
pipeline {
    agent any

    options {
        timestamps()
        skipDefaultCheckout(true)
    }

    environment {
        DOCKER = 'C:\\Users\\Arpit Lad\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Docker') {
            steps {
                bat '"%DOCKER%" --version'
                bat '"%DOCKER%" compose version'
            }
        }

        stage('Validate Compose') {
            steps {
                bat '"%DOCKER%" compose config -q'
            }
        }

        stage('Build Services') {
            steps {
                bat '"%DOCKER%" compose build'
            }
        }
    }

    post {
        success {
            echo 'SaaS Cost Guardian build completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
