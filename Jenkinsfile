pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out from GitHub...'
                checkout scm
            }
        }
        stage('Build') {
            steps {
                echo 'Building Spring Boot App...'
                // gradlew 파일에 실행 권한을 주고 빌드합니다.
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }
        stage('Check Result') {
            steps {
                echo 'Build Finished!'
                sh 'ls build/libs'
            }
        }
    }
}