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

## Other Resources

* [Job DSL Playground](http://job-dsl.herokuapp.com/) - App for debugging Job DSL scripts.
* [Job DSL API Viewer](https://jenkinsci.github.io/job-dsl-plugin/) - Full Job DSL syntax reference.
