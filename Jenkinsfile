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
        export M2_HOME=/home/s185118/maven/apache-maven-3.8.4
        export PATH=$PATH:$M2_HOME/bin
        mvn test
        '''
      }
    }

    stage('Deploy') {
      steps {
        sh '''#!/bin/bash
docker push amtoft/devops_rest_app
caprover deploy -i amtoft/devops_rest_app -a rest -n captain-01 -p jonatandahl'''
      }
    }

  }
}