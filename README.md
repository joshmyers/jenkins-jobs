# Jenkins Job DSL Gradle project

This project uses Gradle for building and testing Jenkin DSL jobs. It was inspired by [this example](https://github.com/sheehan/job-dsl-gradle-example)

## File structure

    .
    ├── src
    │   ├── jobs                # DSL script files
    │   ├── main
    │   │   ├── groovy          # support classes
    │   │   └── resources
    │   │       └── idea.gdsl   # IDE support for IDEA
    │   ├── scripts             # scripts to use with "readFileFromWorkspace"
    │   └── test
    │       └── groovy          # specs
    └── build.gradle            # build file

## Testing

`./gradlew test` runs the specs.

[JobScriptsSpec](src/test/groovy/com/dslexample/JobScriptsSpec.groovy) 
will loop through all DSL files and make sure they don't throw any exceptions when processed. All XML output files are written to `build/debug-xml`. 
This can be useful if you want to inspect the generated XML before check-in. Plugins providing auto-generated DSL must be added to the build dependencies.

If you prefer to stub the auto-generated DSL, you can use [JobScriptsSpecAlternative](src/test/groovy/com/dslexample/JobScriptsSpecAlternative.groovy),
though you may miss some syntax errors.

## Seed Job

See the [seed job](seed_jobs/jobs.d/hmpo_seed_job) which pulls in this repo, builds and creates DSL jobs

Note that starting with Job DSL 1.60 the "Additional classpath" setting is not available when
[Job DSL script security](https://github.com/jenkinsci/job-dsl-plugin/wiki/Script-Security) is enabled, which
is why we explicity switch off script security for JobDSL, see [init.groovy](init.groovy)

## Example / Using helper classes

# Examples

## Base Job Builder

```groovy
import com.jenkins.hmpo.BaseJobBuilder

new BaseJobBuilder()
    .name("foo")
    .description("foo description")
    .emails(["foo@example.com","bar@example.com"])
    .build(this)
```

## Maven Job Builder

```groovy
import com.jenkins.hmpo.MavenJobBuilder

new MavenJobBuilder()
    .name('bar')
    .description('bar job')
    .goals('clean test')
    .gitProject('hmpo-badger')
    .gitRepository('ilikebadgers')
    .gitURL('git.com.badgers.net')
    .gitlabPush(true)
    .promote(true)
    .build(this)
```

## Common Utils

### Defaults

```groovy
import com.jenkins.hmpo.utils.CommonUtils

job('badgers'){
    CommonUtils.addDefaults(delegate)
}
```

### Extended Email

```groovy
import com.jenkins.hmpo.utils.CommonUtils

job("example"){
    CommonUtils.addExtendedEmail(delegate, emails = 'foo@example.com, bar@example.com')
}

// override accepts emails as a list. Compatible with builders
job('example'){
    CommonUtils.addExtendedEmail(delegate, emails = ['foo@example.com', 'bar@example.com'])
}

// Override default email triggers.
job('example'){
    CommonUtils.addExtendedEmail(delegate, emails, triggers=['statusChanged'])
}

// Override default email pre-send script, by providing a groovy code
job('example'){
    CommonUtils.addExtendedEmail(delegate, emails, triggers, sendToDevs, senToReq, includeCulprits, sendToRecipient, "cancel = true")
}

// Override default email pre-send script by providing path to the script file
job('example'){
    CommonUtils.addExtendedEmail(delegate, emails, triggers, sendToDevs, sendToReq, includeCulprits, sendToRecipient, "\${SCRIPT, template='path/to/script.groovy'}"
}
```

### Inject global passwords

```groovy
import com.jenkins.hmpo.BaseJobBuilder
import com.jenkins.hmpo.utils.CommonUtils

new BaseJobBuilder(
        name: "sample-base-job-with-additional-config",
        description: "A job with some additional configurations added"
).build(this).with {
    CommonUtils.addInjectGlobalPasswords(delegate)
}
```

### Add shell parsing rules

```groovy
import com.jenkins.hmpo.BaseJobBuilder
import com.jenkins.hmpo.utils.CommonUtils

new BaseJobBuilder(
        name: "sample-base-job-with-log-parsing",
        description: "A job with log parsing added"
).build(this).with {
    //how to use CommonUtils; pass a custom filename to override the default
    CommonUtils.addLogParserPublisher(delegate, "/var/lib/jenkins/some_rules_file.txt")
}
```

### Add virtualenv to a shell step

```groovy
import com.jenkins.hmpo.BaseJobBuilder
import com.jenkins.hmpo.utils.CommonUtils

new BaseJobBuilder(
        name: "sample-base-job-with-virtualenv",
        description: "A job that creates and activates a python 2.7 virtualenv"
).build(this).with {
    steps {
        shell( CommonUtils.python27Virtualenv + """
                # pip install ansible
                ls -la
                env
                echo "Hello world"
            """.stripIndent()
        )
    }
}
```

### Add a performance publisher block

```groovy
import com.jenkins.hmpo.BaseJobBuilder
import com.jenkins.hmpo.utils.CommonUtils

new BaseJobBuilder(
        name: "sample-base-job-with-performance-publisher",
        description: "A job with a performance publisher. It does not include the actual bits that run the load tests"
).build(this).with {
    steps {
        shell("echo 'Run jmeter tests here'")
    }
    CommonUtils.addPerformancePublisher(delegate,failedThresholdPositive=10, failedThresholdNegative=10, unstableThresholdPositive=5, unstableThresholdNegative=5)
}
```

## Other Resources

* [Job DSL Playground](http://job-dsl.herokuapp.com/) - App for debugging Job DSL scripts.
* [Job DSL API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) - Full Job DSL syntax reference.
