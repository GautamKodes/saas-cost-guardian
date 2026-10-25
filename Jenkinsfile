
pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Docker') {
            steps {
                bat 'docker --version'
                bat 'docker compose version'
            }
        }

        stage('Validate Compose') {
            steps {
                bat 'docker compose config -q'
            }
        }

        stage('Build Services') {
            steps {
                bat 'docker compose build'
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

