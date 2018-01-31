import com.jenkins.hmpo.builders.MavenJobBuilder

new MavenJobBuilder()
    .name('dcs-dap-submission-worker-high')
    .goals('-U clean package  -Punit')
    .build(this)
