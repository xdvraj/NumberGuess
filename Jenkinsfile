pipeline {
    agent { label 'something' }
    
    tools {
        jdk 'Jdk17'
        maven 'maven'
    }

    stages {
        stage('Pipeline Started') {
            steps {
                echo 'Executing Pipeline For Repo'
            }
        }
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/xdvraj/NumberGuess.git'
            }
        }
        stage('Compile') {
            steps {
            sh   'mvn compile'
            }
        }
        stage('Testing') {
            steps {
               sh 'mvn test'
            }
        }
        stage('Package') {
            steps {
               sh 'mvn package'
            }
        }
    }
}
