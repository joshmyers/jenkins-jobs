ARG JENKINS_VERSION

FROM jenkins/jenkins:${JENKINS_VERSION}-alpine

USER root

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh gettext make docker tree

RUN adduser jenkins docker

ADD seed_jobs /seed_jobs
ADD init.sh /init.sh

RUN chown -R jenkins:jenkins /init.sh && \
      chmod +x ./init.sh

USER jenkins

ARG JENKINS_USER
ARG JENKINS_PASS
ARG JENKINS_PORT

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dhudson.DNSMultiCast.disabled=true -Djava.awt.headless=true -Dsun.net.inetaddr.ttl=60 -Duser.timezone=PST -Dorg.jenkinsci.plugins.gitclient.Git.timeOut=60 -Djava.util.logging.config.file=/var/jenkins_home/log.properties"

ENV JENKINS_UC="https://updates.jenkins.io"

COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

COPY init.groovy /usr/share/jenkins/ref/init.groovy.d/

EXPOSE "$JENKINS_PORT"

ENTRYPOINT ["/bin/tini", "--", "/init.sh"]
