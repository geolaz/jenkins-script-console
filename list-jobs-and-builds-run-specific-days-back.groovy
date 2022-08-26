def jobNamePattern ='.*'   // adjust to folder/job regex as needed
def daysBack = 365   // adjust to how many days back to report on
def timeToDays = 24*60*60*1000  // converts msec to days

println "Job Name: ( # builds: last ${daysBack} days / overall )  Last Status\n   Number | Trigger | Status | Date | Duration\n"

Jenkins.instance.allItems.findAll() {
  it instanceof Job && it.fullName.matches(jobNamePattern)
}.each { job ->
  builds = job.getBuilds().byTimestamp(System.currentTimeMillis() - daysBack*timeToDays, System.currentTimeMillis())
  println job.fullName + ' ( ' + builds.size() + ' / ' + job.builds.size() + ' )  ' + job.getLastBuild()?.result
  
  // individual build details
  builds.each { build ->
    println '   ' + build.number + ' | ' + build.getCauses()[0].getShortDescription() + ' | ' + build.result + ' | ' + build.getTimestampString2() + ' | ' + build.getDurationString()
  }
}
return
