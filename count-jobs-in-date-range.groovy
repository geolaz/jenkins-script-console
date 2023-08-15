/*** BEGIN META {
 "name" : "Count executed Jobs",
 "comment" : "Count Jobs executed in a range of time",
 "core": "1.609",
 "authors" : [
 { name : "geolaz" }
 ]
 } END META**/

 jenkins = Jenkins.instance

 Calendar after = Calendar.getInstance()
 Calendar before = Calendar.getInstance()
 //set(int year, int month, int date, int hourOfDay, int minute,int second)
 after.set(2023,0,1,0,0,0)
 before.set(2023,6,31,0,0,0)
 println "Jobs executed between " + after.getTime() + " - " + before.getTime()
 count = 0
 for (job in jenkins.getAllItems(Job.class)) {
   for(Run run in job.getBuilds()){
         if (run.getTimestamp()?.before(before) && run.getTimestamp()?.after(after)) {
                 count == count++
                 
         }
   }
 }
println(count)
