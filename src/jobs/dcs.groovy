import com.jenkins.hmpo.builders.MavenJobBuilder

new MavenJobBuilder()
    .name('dcs-dap-submission-worker-high')
    .goals('-U clean package  -Punit')
    .build(this)

new MavenJobBuilder()
    .name('dcs-rest-referee-api-low')
    .build(this)

new MavenJobBuilder()
    .name('dcs-rest-referee-dwp-api-low')
    .build(this)

new MavenJobBuilder()
    .name('dcs-rest-referee-mi-api-high')
    .build(this)

new MavenJobBuilder()
    .name('dcs-rest-referee-verify-low')
    .build(this)

new MavenJobBuilder()
    .name('dcs-soap-atos-payment-stub-low')
    .build(this)

new MavenJobBuilder()
    .name('dcs-soap-dwp-cis-stub-low')
    .build(this)

new MavenJobBuilder()
    .name('or-rest-oab-low')
    .build(this)
