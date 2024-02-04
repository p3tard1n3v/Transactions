Authentication logic is taken from https://github.com/teddysmithdev/RunGroop-Java/tree/master/src/main/java/com/rungroop/web/security

In order to run rake task use:
mvn spring-boot:run -Dspring-boot.run.arguments="rake:task:cvs:import:users=C:\Users\Petar\Desktop\users.csv,rake:task:cvs:import:merchants=C:\Users\Petar\Desktop\merchats.csv" -Dspring-boot.run.jvmArguments='-Dserver.port=8085'