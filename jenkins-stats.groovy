import com.cloudbees.hudson.plugins.folder.Folder
import groovy.transform.Field
import hudson.model.Job
import hudson.model.User
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject

import jenkins.plugins.git.GitTagSCMHead
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead
import org.jenkinsci.plugins.workflow.multibranch.BranchJobProperty


//globally scoped vars
j = Jenkins.instance
@Field HashMap count_by_type = [:]
count = j.getAllItems(Job.class).size()
jobs_with_builds = j.getAllItems(Job.class)*.getNextBuildNumber().findAll { it > 1 }.size()
global_total_builds = j.getAllItems(Job.class)*.getNextBuildNumber().sum { ((int) it) - 1 }
j.getAllItems().each { i ->
    count_by_type[i.class.simpleName.toString()] = (count_by_type[i.class.simpleName.toString()])? count_by_type[i.class.simpleName.toString()]+1 : 1
}

List known_users = User.getAll()*.id
organizations = j.getAllItems(Folder.class).findAll { !(it.name in known_users) }.size()
projects = j.getAllItems(WorkflowMultiBranchProject.class).size()
total_users = User.getAll().size()

total_pull_requests = j.getAllItems(WorkflowMultiBranchProject.class)*.getAllItems(Job.class).flatten().findAll {
    it.getProperty(BranchJobProperty)?.branch?.head in PullRequestSCMHead
}*.getNextBuildNumber().sum {
    ((int) it) - 1
}

total_tag_releases = j.getAllItems(WorkflowMultiBranchProject.class)*.getAllItems(Job.class).flatten().findAll {
    it.getProperty(BranchJobProperty)?.branch?.head in GitTagSCMHead
}*.getNextBuildNumber().sum {
    ((int) it) - 1
}

//display the information
println "Number of GitHub Organizations: ${organizations}"
println "Number of GitHub Projects: ${projects}"
println "Number of Jenkins jobs: ${count}"
println "Jobs with more than one build: ${jobs_with_builds}"
println "Number of users: ${total_users}"
println "Global total number of builds: ${global_total_builds}"
println "Global total number of pull requests executed: ${total_pull_requests}"
println "Global total number of tag releases executed: ${total_tag_releases}"
println "Count of projects by type."
count_by_type.each {
    println "  ${it.key}: ${it.value}"
}
//null because we don't want a return value in the Script Console
null
