# JavaORM

This is a light implementation of Django ORM in Java.
The purpose is to drastically reduce the number of code lines written for the models and repositories of a DDD application.

## Requirements

1. Java 8 or higher
2. Postgres 15 or higher

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

## Database Compatibility
1. PostgresSql

## Annotations
1. ```@DBEntity``` - marks the class as being a table in the database
2. ```@PK``` - marks the field as being the primary key of the table. Can be used on multiple fields to mark a composite primary key
3. ```@AutoInc``` - marks the field as auto generated inside the DB
4. ```@FK(Table, RefCol)``` - marks the annotated field in the current table as foreign key to the **column** specified in RefCol (string) of the **table** specified in Table (class)
5. ```@DBNotNull``` - marks field as not null
6. ```@Cascade```, ```@SetNull```, ```@NoAction```: foreign key update and delete rules

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
   - Any cursom Enums
2. Aggregated ```@DBEntity``` types as foreign keys (a class annotated as ```@DBEntity``` containing another class annotated as ```@DBEntity```)
3. Field foreign keys (non aggregated), via ```@FK``` annotation
5. Composite primary keys in non referenced tables (Not recommended)
6. Inheritance from ```@DBEntity``` entities

## Limitations
1. All the ```@DBEntity``` entities must have a default constructor
2. All the ```@DBEntity``` entities must have getters and setters following the pattern ```get/set + FieldName```, where ```FieldName``` has the first letter in uppercase and the others as the original field
3. All the ```@DBEntity``` entities must have primary keys
4. All the ```@DBEntity``` entities must have at most one auto increment field
5. The primary key of an  ```@DBEntity``` must have a basic data type
6. The values of primary keys required at Select/Update/Delete must follow the order in which they were declared inside the class
7. In case of entities with multiple references, the references must be inserted in the DB in the natural order, from the ones with less references to the ones with more references. For example, if you have a class A with a reference to B and C, and B has a reference to C, you must insert first A, then B and finally C. The ORM will not check this for you.
8. By default, the ORM will order the classes sent for creating or dropping tables in the order of their dependencies. But this may cause problems, so it's better to specify the classes in the right order.

## External Classes
- ```ORM```: Facade for DDL and DML operations. Requires a ```ConnectionManager``` instance
- ```ConnectionManager```: handles the url, username, password of the database
- ```OrmException```: base exception to all ones that can be thrown inside the ORM
- ```DuplicateDataException```: when inserting an entity with non Auto Increment primary key
- ```DataNotFoundExeption```: when selecting an entity which does not exist by primary key(s) 

## Examples

### Basic usage
```java
// Initialize ORM
ORM orm = new ORM(new ConnectionManager("TestORM", "postgres", "password", "localhost:5432"));

// Create tables
orm.createTables(MData.class, Persoana.class, Angajat.class);

// Insert data
MData data = new MData();
data.setData("test");
data = orm.insert(data);

// Complex relationship example
Persoana manager = new Persoana();
manager.setNume("Manager");
manager = orm.insert(manager);

Angajat employee = new Angajat();
employee.setNume("Employee");
employee.setSef(manager);
employee = orm.insert(employee);

// Cleanup
orm.dropTables(Angajat.class, Persoana.class, MData.class);
```

### Repository example
```java
public class Repository<ID, E>{
    private final Class<E> table;
    private final ORM orm;
    private final Validator<E> validator;

    public Repository(Class<E> table, ConnectionManager connectionManager)
    {
        this.table = table;
        orm = new ORM(connectionManager);
        validator = null;
    }

    public Repository(Class<E> table, ConnectionManager connectionManager, Validator<E> validator)
    {
        this.table = table;
        orm = new ORM(connectionManager);
        this.validator = validator;
    }

    public E findOne(ID id) throws ValidationException, RepositoryException
    {
        if(id == null)
            throw new ValidationException("Id must not be null");
        try{
            E rez = orm.select(table, id);
            if(validator!=null)
                validator.validate(rez);
            return rez;
        }
        catch (DataNotFoundException ex){
            throw new NotExistentException("No id "+id+" found in "+table.getSimpleName());
        }
        catch (ValidationException ex){
            throw new ValidationException(table.getSimpleName()+"("+id+"): "+ex.getMessage());
        }
        catch (Exception ex){ throw new RepositoryException(ex.getMessage()); }
    }

    public E findOne(Predicate<E> predicate) throws ValidationException, RepositoryException
    {
        if(predicate == null)
            throw new ValidationException("The predicate must not be null");
        try{
            List<E> rez = orm.select(table);
            E ret = rez.stream().filter(predicate).findFirst().orElse(null);
            if(ret == null)
                throw new DataNotFoundException();
            if(validator!=null)
                validator.validate(ret);
            return ret;
        }
        catch (DataNotFoundException ex){
            throw new NotExistentException("No data found in "+table.getSimpleName());
        }
        catch (ValidationException ex){
            throw new ValidationException(table.getSimpleName()+": "+ex.getMessage());
        }
        catch (Exception ex){ throw new RepositoryException(ex.getMessage()); }
    }

    public List<E> findAll() throws RepositoryException {
        try {
            return orm.select(table);
        }
        catch (Exception ex)
        {
            throw new RepositoryException(ex.getMessage());
        }
    }

    public E save(E entity) throws RepositoryException, ValidationException
    {
        try{
            if(validator != null)
                validator.validate(entity);
            return orm.insert(entity);
        }
        catch (DuplicateDataException ex) {
            throw new DuplicatedElementException(ex.getMessage());
        }
        catch (ValidationException ve){
            throw ve;
        }
        catch (Exception ex)
        {
            throw new RepositoryException(ex.getMessage());
        }
    }

    public void delete(ID id) throws RepositoryException
    {
        if(id == null)
            throw new ValidationException("Id must not be null");
        try{
            orm.delete(table, id);
        }
        catch (Exception ex)
        {
            throw new RepositoryException(ex.getMessage());
        }
    }

    public void update(ID id, E entity) throws ValidationException, RepositoryException
    {
        if(id == null)
            throw new ValidationException("Id must not be null");
        if(entity == null)
            throw new ValidationException("The entity must not be null");
        if(validator!=null)
            validator.validate(entity);
        try{
            orm.update(entity, id);
        }
        catch (Exception ex)
        {
            throw new RepositoryException(ex.getMessage());
        }
    }
}
```

### Complete test examples

- **ORM**: can be found in the package [tries.model_handle](https://github.com/danielsofran/JavaORM/tree/master/tries/model_handle)
- **Entity types/declaration**: can be found in package [models.demo](https://github.com/danielsofran/JavaORM/tree/master/models/demo)
