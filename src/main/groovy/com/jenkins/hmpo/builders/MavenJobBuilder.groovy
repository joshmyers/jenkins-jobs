package com.jenkins.hmpo.builders

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.jobs.MavenJob
import com.jenkins.hmpo.utils.CommonUtils

/**
 * MavenJob Builder
 *
 * <p>
 *      creates a mavenJob with colorized input,
 *      log rotator, email notifications and build claiming
 * </p>
 * @param name used to name the job
 * @param description job description
 * @param goals Maven goals to build
 * @param gitBranchToBuild Branch to check out for SCM
 * @param gitProject Git project for SCM
 * @param gitRepository Git repoistory for SCM
 * @param gitUrl Git URL for SCM
 * @param gitlabPush trigger on Gitlab Push to repo (boolean)
 * @param promote should this job promote builds (boolean)
 * @param emails list of developer to get notifications
 */

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class MavenJobBuilder {
    String name
    String description
    String goals = 'clean test'
    String gitBranchToBuild = 'master'
    String gitProject = 'hmpo-pex'
    String gitRepository
    String gitUrl = 'git.com.hmpo.net'
    Boolean gitlabPush = false
    Boolean promote = false
    List<String> emails = []

    MavenJob build(DslFactory dslFactory) {
        dslFactory.mavenJob(name) {
            gitRepository = gitRepository ?: name
            it.description this.description
            CommonUtils.addDefaults(delegate)
            publishers {
                if (emails) {
                    publishers {
                        CommonUtils.addExtendedEmail(delegate, emails)
                    }
                }
            }
            scm {
                git {
                    extensions {
                        wipeOutWorkspace()
                    }
                    remote {
                       url("git@${gitUrl}:${gitProject}/${gitRepository}.git")
                    }
                    branch(branchToBuild)
                }
                goals(goals)
            }
            if (gitlabPush) {
                triggers {
                    gitlabPush {
                        buildOnMergeRequestEvents(true)
                        buildOnPushEvents(true)
                        enableCiSkip(true)
                        setBuildDescription(false)
                        rebuildOpenMergeRequest('source')
                        commentTrigger('rebuild')
                        skipWorkInProgressMergeRequest(false)
                    }
                }
            }
            if (promote) {
                properties{
                    promotions {
                        promotion {
                            name('int')
                            conditions {
                                manual('tester')
                            }
                            actions {
                                shell('echo foo;')
                            }
                        }
                    }
                }
            }
        }
    }
}
