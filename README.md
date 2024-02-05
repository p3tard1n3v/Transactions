<h3>Rake Task</h3>

In order to run rake task use:
mvn spring-boot:run -Dspring-boot.run.arguments="rake:task:cvs:import:users=C:\Users\Petar\Desktop\users.csv,rake:task:cvs:import:merchants=C:\Users\Petar\Desktop\merchats.csv" -Dspring-boot.run.jvmArguments='-Dserver.port=8085'
csv examples:
1) user.csv <br/>
   username,password,role<br/>
   test5,test5,admin<br/>
   test4,test4,user<br/>
   test3,test3,user<br/>
2) merchants.csv<br/>
   name,description,email,status,total_transaction_sum,username<br/>
   Bagira,Bagiradescription,bagira@gmail.com,active,0,test4<br/>
   Lidl,Lidldescription,lidl@gmail.com,active,0,test3<br/>
   Bila,Biladescription,bila@gmail.com,inactive,0,test4<br/>

<h3>Cron job: </h3>
Cron job is activated on every half of hour to delete transaction older than an hour.

<h3>Rest endpoint: </h3>
There is a rest endpoint that uploads transactions to merchant. The method is Post. 
Uses basic authentication. 

<h3>Web interface: </h3>
1) Login functionality 
2) Merchant - edit, delete, view
3) Transaction - view transaction of particular Merchant

<h3>Notes: </h3>
HTML theme is taken from https://github.com/teddysmithdev/RunGroop-Java/
