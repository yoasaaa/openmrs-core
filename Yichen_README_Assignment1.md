
## Introduction 

OpenMRS is an open-source electronic medical record (EMR) system designed to improve healthcare delivery in resource-constrained environments. The system provides healthcare providers a robust, scalable, and user-driven platform for efficiently managing patient data. It supports customization to fit various healthcare needs and integrates multiple healthcare workflows. OpenMRS is developed primarily in Java and follows a modular architecture that allows easy extensions and integrations. Using cloc to analyze the codebase, the system comprises approximately 497,418 lines of code, with 139,671 lines written in Java.

## Build

### Prerequisites

#### Java

OpenMRS is a Java application which is why you need to install a Java JDK.

If you want to build the master branch you will need a Java JDK of minimum version 8.

#### Maven

Install the build tool [Maven](https://maven.apache.org/).

You need to ensure that Maven uses the Java JDK needed for the branch you want to build.

To do so execute

```bash
mvn -version
```

which will tell you what version Maven is using. Refer to the [Maven docs](https://maven.apache.org/configure.html) if you need to configure Maven.

#### Git

Install the version control tool [git](https://git-scm.com/) and clone this repository with

```bash
git clone https://github.com/openmrs/openmrs-core.git
```

### Build Command

After you have taken care of the [Prerequisites](#prerequisites)

Execute the following

```bash
cd openmrs-core
mvn clean package
```

This will generate the OpenMRS application in `webapp/target/openmrs.war` which you will have to deploy into an application server like for example [tomcat](https://tomcat.apache.org/) or [jetty](http://www.eclipse.org/jetty/).

### Deploy

For development purposes you can simply deploy the `openmrs.war` into the application server jetty via

```bash
cd openmrs-core/webapp
mvn jetty:run
```

If all goes well (check the console output) you can access the OpenMRS application at `localhost:8080/openmrs`.

Refer to [Getting Started as a Developer - Maven](https://wiki.openmrs.org/display/docs/Maven) for some more information
on useful Maven commands and build options.


## Selected Feature: Provider

The `Provider` class locates at `openmrscore/api/src/main/java/org/openmrs/Provider.java`.
It represents a person who may provide care to a patient during an encounter.

## Existing Test Cases
The existing test cases for the `Provider` class locates at `openmrscore/api/src/test/java/org/openmrs/ProviderTest.java`.
They are written using JUnit and focus on testing two methods: `getName()` and `toString()`.
1. `getName_shouldReturnPersonFullNameIfPersonIsNotNullOrNullOtherwise()`
	- Objective: Verify that the `getName()` method returns the full name of the associated `Person` object if the `Person` is not `null`; otherwise, it should return `null`.
2. `toString_shouldReturnPersonAllNamesWithSpecificFormat()`
	- Objective: Verify that the `toString()` method correctly formats the `Provider` object into a string representation, including its ID and the full name of the associated `Person`.

The test cases can be executed using Maven(run `mvn test`) or the built-in test runner in an IDE like IntelliJ IDEA or Eclipse.

## Partition Strategy
### `getName()`
The input domain of the `getName()` method depends on the state of two attributes of the `Provider` object:
1. The `Person` object (`getPerson()`) associated with the `Provider`.
2. The `PersonName` object (`getPerson().getPersonName()`).

Since both attributes can either be `null` or have a valid instance, we define the following partitions:

| Partition                       | `Person` Object | `PersonName` Object |
| ------------------------------- | --------------- | ------------------- |
| 1. Valid Person with Name       | Not Null        | Not Null            |
| 2. Valid Person with Empty Name | Not Null        | Null                |
| 3. Person is Null               | Null            | N/A                 |
**Note:** The scenario where `Person` is `null`, but `PersonName` is not, is not possible, because `PersonName` is always associated with a `Person` object.



| Test Case Name                                                         | Objective                                           | Expected Result  |
| ---------------------------------------------------------------------- | --------------------------------------------------- | ---------------- |
| `getName_shouldReturnPersonFullNameIfPersonIsNotNullOrNullOtherwise（）` | Check behavior when `Person` and `PersonName` exist | Return full name |
| `getName_shouldReturnNullIfPersonNameIsNull()`                         | Ensure method handles `PersonName` being `null`     | Return `null`    |
| `getName_shouldReturnNullIfPersonIsNull()`                             | Ensure  method  handles a null `Person`             | Return `null`    |

### `toString()`
The method's behavior is influenced by two key factors:
1. providerId
2. `person` object and the `getNames()` method output


| Partition                       | `Person` Object | `getNames()` Output |
| ------------------------------- | --------------- | ------------------- |
| 1. Person is Null               | Null            | N/A                 |
| 2. Valid Person with Empty Name | Not Null        | `[]` (empty list)   |
| 2. Valid Person with Empty Name | Not Null        | `[Single Name]`     |
| 3. Person is Null               | Not Null        | `[Multiple Names]`  |


| Test Case Name                                               | Objective                                       | Expected Result                                                              |
| ------------------------------------------------------------ | ----------------------------------------------- | ---------------------------------------------------------------------------- |
| `toString_shouldReturnSpecificFormatIfPersonIsNull()`        | Ensure  method  handles a null `Person`         | Return `"[Provider: providerId:1 providerName: ]"`                           |
| `toString_shouldReturnSpecificFormatIfPersonNamesIsEmpty()`  | Ensure method handles `PersonName` being `null` | `"[Provider: providerId:1 providerName:[] ]"`                                |
| `toString_shouldReturnSpecificFormatIfPersonHasSingleName()` | Ensure method correctly formats a single name.  | `"[Provider: providerId:1 providerName:[givenName] ]"`                       |
| `toString_shouldReturnPersonAllNamesWithSpecificFormat()`    | Ensure method correctly formats multiple names. | `"[Provider: providerId:1 providerName:[givenName middleName familyName] ]"` |
