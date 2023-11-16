# The script below is listing all Github repositories list from a Jenkins server. It also removes all duplicate values of "repositoryuRL"

import groovy.json.JsonBuilder

def getGitHubRepositoriesFromJobs() {
    def repositories = []

    Jenkins.instance.getAllItems(hudson.model.Job.class).each { job ->
        println("Processing job: ${job.name}")

        def scm
        if (job instanceof hudson.model.FreeStyleProject) {
            scm = job.scm
        } else if (job instanceof org.jenkinsci.plugins.workflow.job.WorkflowJob) {
            // For WorkflowJob, try to find GitSCM from the SCM sources
            def scmSources = job.getSCMs()
            scmSources.each { source ->
                if (source instanceof hudson.plugins.git.GitSCM) {
                    scm = source
                }
            }
        }

        if (scm instanceof hudson.plugins.git.GitSCM) {
            def repoInfo = [
                jobName: job.name,
                repositoryUrl: scm.userRemoteConfigs.collect { it.url }.join(', ')
            ]

            // Check for duplicate repository URLs
            if (!repositories.find { it.repositoryUrl == repoInfo.repositoryUrl }) {
                repositories.add(repoInfo)
            }
        } else {
            println("Job ${job.name} does not use Git as SCM.")
        }
    }

    return repositories
}

// Main script
def allRepositories = getGitHubRepositoriesFromJobs()

// Print repositories as JSON
def jsonString = new JsonBuilder(allRepositories).toPrettyString()
println("Exporting GitHub repositories from Jenkins jobs:")
println(jsonString)
