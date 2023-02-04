# JavaORM

This is a light implementation of Django ORM in Java.
The purpose is to drastically reduce the number of code lines written for the models and repositories of a DDD application.

## Features
1. Creates the database tables based on the Java classes annotated with ```@DBEntity```
2. Drops the database tables previously created 
3. Performs CRUD operations without the need of writing SQL statements
   1. Select data by primary key from a table
   2. Select all data from a table
   3. Insert entity data into the created tables
   4. Delete data by primary key from a table
   5. Update data by primary key from a table
4. Creates the ```ConnectionManager``` from database name, username, password and port which is by default 5432

## Supports
1. Only PostgreSQL
1. Basic data types
   - int, Integer
   - String
   - double, Double, float, Float
   - boolean, Boolean
   - LocalDateTime, LocalDate, LocalTime
   - Enums
2. Aggregated _DBEntity_ types as foreign keys
3. Field foreign keys (non aggregated)
3. Foreign key update and delete rules: ```@Cascade```, ```@SetNull```, ```@NoAction```
4. Composite primary keys in non referenced tables
5. Inheritance in ```@DBEntity``` entities

## Limitations
1. All the ```@DBEntity``` entities must have a default constructor
2. All the ```@DBEntity``` entities must have getters and setters following the pattern ```get/set + FieldName```, where ```FieldName``` has the first letter in uppercase and the others as the original field
3. All the ```@DBEntity``` entities must have primary keys
4. All the ```@DBEntity``` entities must have at most one auto increment field
5. The primary key of an  ```@DBEntity``` must have a basic data type

## Classes
- ```ORM```
- ```ConnectionManager```: handles the url, username, passdword of the database

# Warning
Please don't use the other classes, even though they are public. This issue will be fixed soon.
