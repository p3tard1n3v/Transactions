# Rake Task

      In order to run rake task use:
      mvn spring-boot:run -Dspring-boot.run.arguments="rake:task:cvs:import:merchants=C:\Users\Petar\Desktop\merchats.csv" -Dspring-boot.run.jvmArguments='-Dserver.port=8085'`
## csv examples:
### merchants.csv

      name,password,role,description,email,status,total_transaction_sum,username
      Bagira,Bagira,user,Bagiradescription,bagira@gmail.com,active,0,test4
      Lidl,Lidl,user,Lidldescription,lidl@gmail.com,active,0,test3
      Bila,Bila,user,Biladescription,bila@gmail.com,inactive,0,test4
      test5,test5,admin

# Cron job:
`Cron job is activated on every half of hour to delete transaction older than an hour.`

# Rest endpoints: 
`Endpoints basic authentication.`
## Upload transaction
### Request
`POST /api/transactions/create`

      curl -i -H 'Accept: application/json' X POST -d 'amount:11111111&customerEmail:email23@emailadasdaad1.com&customerPhone:0888345678' http://localhost:8080/api/transactions/create

### Response

      HTTP/1.1 201 Created
      Date: Mon, 12 Feb 2024 11:02:08 GMT
      Status: 201 Created
      Connection: close
      Content-Type: text/plain;charset=UTF-8
      Content-Length: 18
      
      Transaction create

## Delete Merchant
### Request
`DELETE /merchants/3/delete`

      curl -i -H 'Accept: */*' http://localhost:8080/merchants/3/delete

### Response

      HTTP/1.1 200 OK
      Date: Mon, 12 Feb 2024 11:05:38 GMT
      Status: 200 OK
      Connection: close
      Content-Type: text/plain;charset=UTF-8
      Content-Length: 31
      
      Merchant deleted successfully!

## Update Merchant
### Request
`PATCH http://localhost:8080/merchants/3/edit`

      curl -i -H 'Accept: */*' -X PUT -d 'id:3&name:merchant1&email:test@merchant1.com&totalTransactionSum:0.23&status:ACTIVE&description:desc' http://localhost:8080/merchants/3/edit

### Response

      HTTP/1.1 200 OK
      Date: Mon, 12 Feb 2024 11:17:55 GMT
      Status: 200 OK
      Connection: close
      Content-Type: text/plain;charset=UTF-8
      Content-Length: 31
      
      Merchant updated successfully!

# Web interface: 

      1) Login functionality
      2) Merchant - edit, delete, view
      3) Transaction - view transaction of particular Merchant

# Technical depth
## Transaction logic 
### Authorized Transaction

      Authorized transaction is most basic transaction for a merchant.
      It converts to Error Transaction if references another transaction.
      It converts to Reversal Transaction if amount is zero.
      Can be approved. Then Approved transaction is created with the same amount, 
      that has reference to the original authorized transaction.

### Error Transaction

      It is converted from Authorized transaction that references another transaction.

### Reversal Transaction

      It is converted from Authorized transaction if amount is zero.

### Approved Transaction

      It is created when Authorized transaction is approved. 
      It has the same amount and references the original Authorized Transaction.

### Refund Transaction

      It is created when Approved transaction is refuned. 
      It has the same amount as approved transaction and references the original Authorized Transaction.


# Notes: 

      HTML theme is taken from https://github.com/teddysmithdev/RunGroop-Java/
