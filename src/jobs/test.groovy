import com.jenkins.hmpo.builders.BaseJobBuilder
import com.jenkins.hmpo.builders.MavenJobBuilder

new BaseJobBuilder()
    .name("foo")
    .description("foo")
    .build(this)

new MavenJobBuilder()
    .name('bar')
    .description('bar job')
    .goals('clean test')
    .gitProject('hmpo-badger')
    .gitRepository('ilikebadgers')
    .gitUrl('git.com.badgers.net')
    .gitlabPush(true)
    .promote(true)
    .build(this)
