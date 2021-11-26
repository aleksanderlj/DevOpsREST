pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh '''#!/bin/bash
docker-compose build
'''
      }
    }

    stage('Test') {
      steps {
        sh '''#!/bin/bash
        mvn test
        '''
      }
    }

    stage('Deploy') {
      steps {
        sh '''#!/bin/bash
docker push amtoft/devops_rest_app
sudo caprover deploy -i amtoft/devops_rest_app -a rest -n captain-01 -p jonatandahl'''
      }
    }

  }
}