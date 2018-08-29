pipeline {
   agent {
      dockerfile {
         dir '.'
         additionalBuildArgs "--build-arg PROXY_CERTIFICATE_URL=\"${env.PROXY_CERTIFICATE_URL}\" --build-arg http_proxy=\"${env.http_proxy}\"  --build-arg https_proxy=\"${env.https_proxy}\" --build-arg ftp_proxy=\"${env.ftp_proxy}\" --build-arg no_proxy=\"${env.no_proxy}\""
         args "--privileged"
      }
   }

   options {
      disableConcurrentBuilds()
      buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
   }

   stages {
      stage('Checkout') {
         steps {
           script {
             deleteDir()
           }
           checkout scm
         }
      }

      stage('Build') {
         steps {
            script {
               if (isUnix()) {
                  sh './gradlew $GRADLE_OPTIONS clean jar check'
               } else {
                  bat 'gradlew.bat $GRADLE_OPTIONS clean jar check'
               }
            }
         }
      }

      stage('Post Build') {
         steps {
            script {
               step([$class: 'JUnitResultArchiver', testResults: '**/generated/test-reports/**/*.xml'])
               if ((currentBuild.result == null) || (currentBuild.result == 'SUCCESS')) {
                  step([
                     $class: 'ArtifactArchiver',
                     artifacts: '**/generated/*.jar',
                     fingerprint: false
                  ])
               } else {
                  echo "currentBuild.result: " + currentBuild.result
               }
            }
         }
      }
   }
}