#!/usr/bin/env bash
[ -d /seed_jobs/jobs.d/ ] && \
  (ls  /seed_jobs/jobs.d | xargs -I {} mkdir -p /var/jenkins_home/jobs/{})  && \
  (ls  /seed_jobs/jobs.d | xargs -I {} cp -n /seed_jobs/jobs.d/{} /var/jenkins_home/jobs/{}/config.xml)

exec /usr/local/bin/jenkins.sh
