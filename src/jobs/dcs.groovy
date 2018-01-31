import com.jenkins.hmpo.builders.MavenJobBuilder

new MavenJobBuilder()
    .name('dcs-dap-submission-worker-high')
    .goals('-U clean package  -Punit')
    .gitProject('hmpo-badger')
    .gitRepository('ilikebadgers')
    .gitUrl('git.com.badgers.net')
    .gitlabPush(true)
    .promote(true)
    .build(this)
