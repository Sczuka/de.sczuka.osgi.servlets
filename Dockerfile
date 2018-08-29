FROM openjdk:alpine

ARG JENKINS_GROUP_ID=1000
ARG JENKINS_USER_ID=1000
ARG PROXY_CERTIFICATE_URL=""

ENV http_proxy "$http_proxy"
ENV https_proxy "$https_proxy"
ENV ftp_proxy "$ftp_proxy"
ENV no_proxy "$no_proxy"

RUN addgroup -g ${JENKINS_GROUP_ID} jenkins && \
    adduser -D -u ${JENKINS_USER_ID} -G jenkins jenkins

RUN apk --no-cache add curl

RUN if [ ! -z "${PROXY_CERTIFICATE_URL}" ]; then \
   curl ${PROXY_CERTIFICATE_URL} > /tmp/certificate.cer ; \
   keytool -noprompt -import -v -trustcacerts -alias my-proxy -file /tmp/certificate.cer -storepass changeit -keystore /etc/ssl/certs/java/cacerts ; \
fi

USER jenkins
