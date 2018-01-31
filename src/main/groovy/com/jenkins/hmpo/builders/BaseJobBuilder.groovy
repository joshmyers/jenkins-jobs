package com.jenkins.hmpo.builders

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

/**
 * The base job building block
 *
 * <p>
 *      creates a job with colorized input,
 *      log rotator, email notifications and build claiming
 * </p>
 * @param name used to name the job
 * @param description job description
 * @param emails list of developer to get notifications
 */

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BaseJobBuilder {

    String name
    String description
    List<String> emails = []

    Job build(DslFactory dslFactory) {
        dslFactory.job(name) {
            it.description this.description
            logRotator {
                numToKeep 5
            }
            steps {
                shell('echo foo;')
            }
            publishers {
                if (emails) {
                    mailer emails.join(' ')
                }
            }
        }
    }
}
