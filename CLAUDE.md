# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TeachChildrenToSave is a Java web application for coordinating financial literacy education volunteers with teachers. It manages registration, event scheduling, and coordination between banks, volunteers, teachers, and schools for financial literacy programs.

## Build and Development Commands

### Building the Application
- **Build**: `mvn clean install`
- **Clean**: `mvn clean`
- **Run Tests**: `mvn test` (excludes integration tests)
- **Integration Tests**: Currently disabled in Maven configuration (see pom.xml lines 219-235)

### Deployment
- Application builds to `target/TeachChildrenToSave.war`
- Deployed to AWS Elastic Beanstalk (see src/main/doc/deployment_instructions.txt)
- Environment configuration via `src/main/resources/application.properties`

## Architecture Overview

### Technology Stack
- **Framework**: Spring MVC 6.1.5 with Jakarta Servlet API
- **Java Version**: 17
- **Database**: DynamoDB (primary), with legacy MySQL support
- **Template Engine**: Apache Velocity
- **Build Tool**: Maven
- **Deployment**: AWS Elastic Beanstalk + Tomcat

### Database Architecture
The application supports multiple database implementations through `DatabaseFactory` but only one is working today:
- `SingleTableDynamoDB` (current production - configured in application.properties)

Database configuration is controlled by the `databaseToUse` property in application.properties.

### Core Data Models
Located in `src/main/java/com/tcts/datamodel/`:
- **User hierarchy**: User (abstract) → Teacher, Volunteer, BankAdmin, SiteAdmin
- **Event**: Represents teaching sessions with date, time, grade, delivery method
- **Bank**: Financial institutions providing volunteers
- **School**: Educational institutions with teachers
- **Document**: File attachments visible to different user types

### Controller Architecture
Spring MVC controllers in `src/main/java/com/tcts/controller/`:
- Role-based home pages (Teacher, Volunteer, BankAdmin, SiteAdmin)
- Event management (creation, registration, cancellation)
- User management and authentication
- Document management with S3 integration

### Key Configuration Files
- `src/main/resources/application.properties`: Database and AWS configuration
- `src/main/resources/sites.properties`: Multi-site configuration
- `src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml`: Spring MVC configuration

### User Workflow
1. **Registration**: Teachers and volunteers register with their respective institutions
2. **Event Creation**: Teachers create events (classes) for specific dates/times
3. **Volunteer Signup**: Volunteers browse and sign up for available events
4. **Approval Process**: Bank admins approve volunteers before they can participate
5. **Coordination**: Email notifications coordinate between teachers and volunteers

## Important Notes

### Environment-Specific Behavior
- Application behavior varies by environment (`prod`, `test`, `dev`) configured in application.properties
- Database information varies by site (`DE`, `FL`) to support multiple different sites.
- DynamoDB table names include a site and an environment suffix (e.g., "TCTS.DE.prod")

### Current Development Tasks
See `todo.txt` for active development tasks. Key ongoing work includes:
- Migrating away from MySQL to DynamoDB-only
- Enabling prefetching and caching in database layer
- UI improvements for single-option dropdowns

### Testing
- Unit tests use JUnit 4, but there are currently very few unit tests
- There are currently no integration tests
- Test data utilities available in `src/main/java/com/tcts/database/MockDataCreator.java`

### Security Considerations
- Password hashing with salt (see `SecurityUtil`)
- AWS credentials in application.properties (not in version control template)
- Session-based authentication with role-based access control