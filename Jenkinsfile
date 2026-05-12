pipeline {
    agent any

    stages {
        stage('DEBUG - Jenkinsfile loaded') {
            steps {
                echo 'YES - Jenkins is executing the real Jenkinsfile'
                sh 'pwd'
                sh 'ls -la'
            }
        }

        stage('Checkout frontend') {
            steps {
                dir('product-obs-frontend') {
                    git branch: 'main', url: 'https://github.com/AhmedouSalem/product-obs-frontend.git'
                }
            }
        }

        stage('Backend - Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Frontend - Install & Build') {
            steps {
                dir('product-obs-frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('DEBUG - Check compose and frontend path') {
            steps {
                sh '''
                    echo "Current directory:"
                    pwd

                    echo "Workspace content:"
                    ls -la

                    echo "Frontend folder content:"
                    ls -la product-obs-frontend || true

                    echo "docker-compose.e2e.yml context lines:"
                    grep -n "context" docker-compose.e2e.yml || true

                    echo "Resolved docker compose config:"
                    docker compose -f docker-compose.e2e.yml config | grep -A 8 "frontend-e2e"
                 '''
            }
        }

        stage('Start E2E Stack') {
            steps {
                sh 'docker compose -f docker-compose.e2e.yml down -v || true'
                sh 'docker compose -f docker-compose.e2e.yml up -d --build'
            }
        }

        stage('Wait for services') {
            steps {
                sh 'sleep 25'
                sh 'docker compose -f docker-compose.e2e.yml ps'
            }
        }

        stage('Cypress E2E Tests') {
            steps {
                dir('product-obs-frontend') {
                    sh 'npx cypress run'
                }
            }
        }

        stage('Deploy normal stack') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d --build'
            }
        }
    }

    post {
        always {
            sh 'docker compose -f docker-compose.e2e.yml down -v || true'
        }

        success {
            echo 'Pipeline succeeded.'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}