# MedCare Hospital Management System - Agent Guide

## Build/Test Commands
- **Build:** `mvn clean package` or `mvn clean install`
- **Test:** `mvn test` (single test: `mvn test -Dtest=TestClassName`)
- **Deploy:** `mvn clean package` → deploy WAR to Tomcat
- **Clean:** `mvn clean`

## Architecture
- **Technology Stack:** Jakarta EE 10, JSF 4.0, PrimeFaces, Hibernate 6.4, MySQL 8.0, CDI/Weld
- **Package Structure:** `org.medcare.*` with dao, models, service, views, utils, enums, config
- **Database:** MySQL (medicare_db) with Hibernate ORM, connection via hibernate.cfg.xml
- **Frontend:** JSF/PrimeFaces (login.xhtml, welcome.xhtml) with backing beans
- **Core Models:** User, Patient, Doctor, Appointment, Receptionist, AppointmentArchive, PatientArchive

## Code Style
- **Naming:** PascalCase classes, camelCase methods/variables, boolean fields use "is" prefix
- **Imports:** Jakarta EE frameworks first, then alphabetical order
- **Error Handling:** RuntimeException with descriptive messages, transaction rollback in catch blocks
- **Annotations:** Jakarta validation (@NotBlank, @Email), JPA (@Entity, @Table), CDI (@Inject, @Named)
- **Fields:** Private with public getters/setters, null checks with early returns
- **Constants:** Static final in UPPER_CASE

## Database
- **Connection:** MySQL localhost:3306/medicare_db (see hibernate.cfg.xml)
- **Schema:** Auto-generated via hibernate.hbm2ddl.auto=update
