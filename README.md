# Code​ ​Challenge
We would like to have a restful API for our statistics. The main use case for our API is to
calculate real-time statistic from the last 60 seconds. There will be two APIs, one of them is
called every time a transaction is made. It is also the sole input of this rest API. The other one
returns the statistic based of the transactions of the last 60 seconds.

## Specs
````
POST /transactions
````
Every time a new transaction happened, this endpoint will be called.

Body:
```
{
    "amount": 12.3,
    "timestamp": 1478192204000
}
```

Where:
````
● amount - transaction amount
● timestamp - transaction time in epoch in millis in UTC time zone (this is not current
timestamp)
````
Returns: Empty body with either 201 or 204.
````
● 201 - in case of success
● 204 - if transaction is older than 60 seconds
````
Where:
````
● amount is a double specifying the amount
● time is a long specifying unix time format in milliseconds
````

````
GET /statistics
````
This returns statistics of last 60 seconds transactions.
```
Response:
{
"sum": 1000,
"avg": 100,
"max": 200,
"min": 50,
"count": 10
}
```
Where:
```
● sum is a double specifying the total sum of transaction value in the last 60 seconds

● avg is a double specifying the average amount of transaction value in the last 60
seconds
● max is a double specifying single highest transaction value in the last 60 seconds
● min is a double specifying single lowest transaction value in the last 60 seconds
● count is a long specifying the total number of transactions happened in the last 60
seconds.
```

## Prerequisites

The project requires Java 8 and Maven to build.

## Running the Application

### Maven
Form the base directory, just run:
```
mvn spring-boot:run
```
## Java Standalone Application
```
mvn clean package 
java -jar target/statistics-0.0.1-SNAPSHOT.jar
```