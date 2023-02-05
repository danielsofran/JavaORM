# JavaORM

This is a light implementation of Django ORM in Java.
The purpose is to drastically reduce the number of code lines written for the models and repositories of a DDD application.

## Features
1. Creates the database tables based on the Java classes annotated with ```@DBEntity```
2. Drops the database tables specified
3. Performs CRUD operations without the need of writing SQL statements
   1. Select data by primary key from a table
   2. Select all data from a table
   3. Insert entity data into the created tables
   4. Delete data by primary key from a table
   5. Update data by primary key from a table
4. Creates the ```ConnectionManager``` from database name, username, password and port which is by default *5432*

## Databases
1. PostgresSql

## Supports
1. Basic data types
   - int, Integer
   - long, Long
   - short, Short
   - char, Character
   - String
   - double, Double, float, Float
   - boolean, Boolean
   - LocalDateTime, LocalDate, LocalTime
   - Enums
2. Aggregated ```@DBEntity``` types as foreign keys
3. Field foreign keys (non aggregated)
4. Foreign key update and delete rules: ```@Cascade```, ```@SetNull```, ```@NoAction```
5. Composite primary keys in non referenced tables (Not recommended)
6. Inheritance from ```@DBEntity``` entities

## Limitations
1. All the ```@DBEntity``` entities must have a default constructor
2. All the ```@DBEntity``` entities must have getters and setters following the pattern ```get/set + FieldName```, where ```FieldName``` has the first letter in uppercase and the others as the original field
3. All the ```@DBEntity``` entities must have primary keys
4. All the ```@DBEntity``` entities must have at most one auto increment field
5. The primary key of an  ```@DBEntity``` must have a basic data type
6. The values of primary keys required at Select/Update/Delete must follow the order in which they were declared inside the class 

## Classes
- ```ORM```
- ```ConnectionManager```: handles the url, username, password of the database
- ```OrmException```: base exception to all ones that can be thrown inside the ORM
- ```DuplicateDataException```: when inserting an entity with non Auto Increment primary key
- ```DataNotFoundExeption```: when selecting an entity which does not exist by primary key(s) 

## Examples
- ORM: can be found in the package _tries.model_handle_
- Entity declaration: can be found in package _models.demo_

## Usage
Copy the _orm_ package into the top level of your module.

# Warning
Please don't use the other classes, even though they are public. This issue will be fixed soon.<br>
The driver for PostgresSQL is mandatory and must be included.
