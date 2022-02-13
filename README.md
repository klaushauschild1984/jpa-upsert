```
    _____
 __|  _  |__   _____   ______   ______   _____      __
|  | | |    | |     | |   ___| |   ___| |     |   _|  |_
|  |_| |    | |    _|  `-.`-.  |   ___| |     \  |_    _|
|______|  __| |___|   |______| |______| |__|\__\   |__|
   |_____| :: upsert ::                   v(0.0-SNAPSHOT)
```

[![Build Status](https://app.travis-ci.com/klaushauschild1984/jpa-upsert.svg?branch=master)](https://app.travis-ci.com/klaushauschild1984/jpa-upsert)

# JPA Upsert - Java Persistence API Update/Insert

## The problem

Whenever you work with JPA within a project there is a point wehre you need data loading. Either it is for updating data on a running instance or providing test data for local development.

The goto approach to "solve" this is by utilizing fixtures with low-level access to the database providing a number of SQL statements containing this data. This work but feels odd due to you main touching point with data is the persistence layer of your JPA implementation. Another downside is that all logic provided by the persistence layer like automatic ID generation, validation or auditing is bypassed by executing plain SQL statements. You either have to mimic all of this or ignore it.

## The inspiration

SAP Hybris eCommerce platform has a quiet nice approach to address this issue: [Impex](http://javainsimpleway.com/overview-of-impex/). This is the idea of [Imp]ort/[Ex]port of data via CSV formatted files, which can be easily written by non-technicians and also apply all the logic found in the persistence layer.

One main advantage by this functionality is that foreign objects that need to be referenced can be addresses by some "human-readable" fields, rather than a primary key. There are as well default values, macro definitions and even the possibility to implement custom logic to translate string values from the file into complex objects within the persistence layer.

Another handy feature is its `INSERT_UPDATE` keyword which takes this projects name inspiration from. Data specified in this scope will either be inserted as new data into the database or already present data will be updated.

## The solution

As SAP Hybris ships with ita very own persistence layer implementation which does not fit into any commonly used Java-based persistence API there is a JPA compatible implementation required. You can find this right here: `jpa-upsert`.

This projects aims to mimic SAP Hybris's Impex approach of specifying data and addresses some issues.

# Usage

## General usage

At any point where you need to work with data through `jpa-upsert` you have to build it like

    JpaUpsert
        .builder()
        .entityManager(entityManager)
        .entityPackage("package.to.your.jpa.models")
        .build()

This will build a `JpaUpsert`. The least amount of information you have to apply is an JPA `EntityManager` and the base package where your JPA models are defined.

After that you can `apply` any `Resoucre` containing the "data instructions". Multiple `apply` call can be chained.

## Basic data format

    [
        {
            "operation": "INSERT",
            "entity": "Person",
            "header": "name[unique] ; age ; gender[default = DIVERSE]",
            "data": [
                "Ada ; 19 ; FEMALE",
                "John ; 38 ; MALE",
                "Alan ; 26 ; "
            ]
        }
    ]

* `operation`
  * specify the operation to execute
* `entity`
  * specify the entity to work on
* `header`
  * specify the header of the data table below listing all essential columns with their modifiers
* `data`
  * a list of data lines which forms the data table
  * each line corresponds to a single entity

## Operations

* `INSERT`
  * inserts the specified entity into the database
  * if the entity already exists in the database this data line is skipped
  * no updates are performed
* `UPDATE`
  * finds an unique entity by its "unique" columns an updates all other fields
  * if the entity can not be found this line is skipped
* `UPSERT`
  * combined operation of `INSERT` and `UPDATE`
  * finds a unique entity or creates it from scratch
* `DELETE`
  * deletes an entity specified by its "unique" columns

## Modifiers

* `UNIQUE`
  * classifies a column as unique
  * multiple "unique" columns will be treated together
  * is necessary to specify for `INSERT` and `DELETE` operation
* `DEFAULT`
  * specifies a default value for this colum
  * this value will be applied if the matching column in the data line is blank

## Data conversion

* Strings
  * Strings will be stored as specified but trimmed
* Numbers
  * it will be tried to `parse` a number corresponding to the fields type
  * all the rules implemented in common parsing methods are applied
* Dates
  * timestamp?
  * date format via modifier
  * supported types
* Enums
  * based on the fields enum type the respective `valueOf()` method will be used for parsing

There is the possibility to implement custom translation to cover any other case which is not built-in.

# Limitations / TODOs

* translation incomplete
* only INSERT is currently supported
* all JPA entities have to exist in one package
* to instantiate an entity it has to be "build" by a [Lombok builder](https://projectlombok.org/features/Builder)

# Contribution

Pull requests or issue reports are welcome. Although this project is still in the phase to find its final shape which means many features are missing. Add a feature idea in the issues section.

To achieve targeted source code formatting make sure to perform at least `mvn validate` before pushing. This will trigger the Prettier plugin and reformats all sources.

# Future ideas

* [Liquibase](https://liquibase.org/) integration
* maintain "history" of already applied files in database to avoid multiple executions
