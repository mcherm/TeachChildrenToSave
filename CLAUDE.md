# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TeachChildrenToSave (TCTS) is a Java web application coordinating financial literacy education. Banks send volunteers to teach in schools. Teachers create events (teaching sessions), volunteers sign up for events, and bank admins approve their bank's volunteers. Two sites exist: Delaware (DE) and Florida (FL), each with separate databases.

## Build and Development Commands

- **Build**: `mvn clean install`
- **Run Tests**: `mvn test` (very few tests exist; integration tests are disabled)
- **Output**: `target/TeachChildrenToSave.war`
- **Deploy**: AWS Elastic Beanstalk (see `src/main/doc/deployment_instructions.txt`)

## Technology Stack

- **Framework**: Spring MVC 6.1.5 with Jakarta Servlet API
- **Java Version**: 17
- **Database**: DynamoDB (single-table design) via AWS SDK v2
- **Email**: AWS SES via AWS SDK v2
- **File Storage**: AWS S3 via AWS SDK v2
- **Template Engine**: Apache Velocity (email templates only; views are JSP)
- **Views**: JSP pages in `src/main/webapp/WEB-INF/pages/`
- **Build Tool**: Maven

## User Roles and Workflow

Four user types, each with their own home page:

| Role | Home Page | Description |
|------|-----------|-------------|
| **Teacher** | `teacherHome.htm` | Belongs to a School. Creates and manages events. |
| **Volunteer** | `volunteerHome.htm` | Belongs to a Bank. Signs up for available events. |
| **BankAdmin** | `bankAdminHome.htm` | Extends Volunteer (is also a Volunteer). Manages their bank's volunteers (approve/suspend). |
| **SiteAdmin** | `siteAdminHome.htm` | Site-wide administrator. Manages schools, banks, allowed values, site settings. |

Workflow:
1. Teachers register → affiliated with a School
2. Teachers create Events (classes) specifying date, time, grade, delivery method, number of students
3. Volunteers register → affiliated with a Bank → initially UNCHECKED until bank admin approves
4. Approved volunteers browse and sign up for available events
5. Email notifications sent on signup/cancellation

## Core Data Models (`src/main/java/com/tcts/datamodel/`)

### User hierarchy
- `User` (abstract): userId, email, hashedPassword, salt, firstName, lastName, userType, phoneNumber, resetPasswordToken
- `Teacher extends User`: schoolId, linkedSchool
- `Volunteer extends User`: bankId, approvalStatus (UNCHECKED/CHECKED/SUSPENDED), bankSpecificData, address fields (street, suite, city, state, zip), linkedBank
- `BankAdmin extends Volunteer`: (no additional fields; is both a BankAdmin and a Volunteer in SessionData)
- `SiteAdmin extends User`: (no additional fields)

### Other models
- `Event`: eventId, teacherId, eventDate, eventTime, grade, deliveryMethod, numberStudents, notes, volunteerId (null/"0" = no volunteer), linkedTeacher, linkedVolunteer
- `Bank`: bankId, bankName, minLMIForCRA, bankSpecificDataLabel, linkedBankAdmins
- `School`: schoolId, name, addressLine1, city, state, zip, county, schoolDistrict, phone, lmiEligible, slc
- `Document`: name, showToTeacher, showToVolunteer, showToBankAdmin
- `ApprovalStatus` enum: UNCHECKED(0), CHECKED(1), SUSPENDED(2)
- `UserType` enum: TEACHER, VOLUNTEER, BANK_ADMIN, SITE_ADMIN

## Database Architecture

### Interface and implementations
- `DatabaseFacade` (`com.tcts.database`): The full database API interface
- `SingleTableDynamoDbDatabase`: Production implementation — single DynamoDB table, all record types in one table with `table_key` as hash key
- `CachingDatabase`: Wraps SingleTableDynamoDbDatabase with caching (available but not default)
- `DatabaseFactory`: Spring component that creates the DB based on `databaseToUse` in application.properties (`SingleTableDynamoDB` or `CachedDB`)

### DynamoDB design
- Table name pattern: `TCTS.{site}.{environment}` (e.g., `TCTS.DE.prod`, `TCTS.FL.dev`)
- Single-table design with `table_key` as primary partition key
- Global secondary indices for email lookups and volunteer-on-event queries
- Sentinel value `NO_VOLUNTEER = "0"` used in `event_volunteer_id` field instead of null
- DynamoDB cannot store empty strings; missing fields are returned as `""` by `getStringField()`
- `DatabaseField` enum lists all field names used in the database
- `SingleTableDynamoDBSetup`: Standalone utility to create/reinitialize a DynamoDB table (run via `main()`)

### Allowed values (stored in DB)
The DB stores configurable lists the admin can edit:
- **Allowed dates**: Valid event dates for the current year
- **Allowed times**: e.g., "9:00 AM", "10:00 AM"
- **Allowed grades**: e.g., "4th grade", "5th grade"
- **Allowed delivery methods**: e.g., "In-person", "Virtual"

### Site settings (stored in DB, key-value pairs)
- `SiteEmail`: The from/reply-to email address
- `CurrentYear`: Current program year (shown on homepage)
- `EventDatesOnHomepage`: Text listing dates shown on public homepage
- `ShowDocuments`: "yes"/"no" to show/hide the documents section

## Multi-Site Architecture

Two live sites share one codebase but use separate databases:
- **DE** (Delaware): `teachchildrentosaveday.org`
- **FL** (Florida): `teachchildrentosave-fl.org`

Site is determined at runtime from the HTTP request's server hostname via `SitesConfig` → `sites.properties`. This means one deployed WAR can serve either site.

- `SitesConfig` (`com.tcts.common`): Spring component with `getSite(request)` returning "DE" or "FL"
- `sites.properties`: Maps hostnames to site codes; `localhost=DE`, `127.0.0.1=FL`
- Site-specific JSP: `WEB-INF/pages/sites/{DE|FL|TEST}/aboutBody.jsp`
- Documents in S3 are stored in site-specific folder paths

## Configuration Files

- `src/main/resources/application.properties`: AWS credentials, `databaseToUse`, `dynamoDB.environment` — **not in version control** (use `.template` as reference)
- `src/main/resources/application.properties.template`: Template showing required keys
- `src/main/resources/sites.properties`: Hostname → site mapping
- `src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml`: Spring MVC configuration (view resolvers, Velocity config, S3 multipart resolver)

## Controller Architecture (`src/main/java/com/tcts/controller/`)

URL pattern: most endpoints use `*.htm`. Controllers use `@RequestMapping`.

| Controller | Key URLs |
|------------|----------|
| `HomePageController` | `/`, `volunteerHome.htm`, `teacherHome.htm`, `bankAdminHome.htm`, `siteAdminHome.htm` |
| `LoginController` | `login.htm` |
| `LogoutController` | `logout.htm` |
| `RegisterController` | `register.htm` (choose type) |
| `TeacherRegistrationController` | `registerTeacher.htm` |
| `VolunteerRegistrationController` | `registerVolunteer.htm` |
| `CreateEventController` | `createEvent.htm` |
| `EventController` | `event.htm`, `events.htm`, `editEvent.htm` |
| `EventDetailsController` | `eventDetails.htm` |
| `EventRegistrationController` | `eventRegistration.htm`, `eventRegistrationBySiteAdmin.htm` |
| `CancelWithdrawController` | `teacherCancel.htm`, `volunteerWithdraw.htm` |
| `OpeningsController` | `openings.htm` (list of available events for volunteers) |
| `BankController` | `banks.htm`, `addBank.htm`, `editBank.htm`, `deleteBank.htm` |
| `SchoolController` | `schools.htm`, `addSchool.htm`, `editSchool.htm`, `deleteSchool.htm` |
| `VolunteerController` | `volunteers.htm`, `volunteer.htm`, `editVolunteer.htm` |
| `TeacherController` | `teachers.htm`, `teacher.htm`, `editTeacher.htm` |
| `BankAdminActionsController` | Approve/suspend volunteers |
| `DocumentController` | `documents.htm` (admin document management) |
| `AboutController` | `about.htm` |
| `ContactController` | `contact.htm` |
| `AdminViewStatisticsController` | `adminViewStatistics.htm` |
| `ExcelDownloadController` | Downloads data as Excel spreadsheet |
| `EmailAnnouncementController` | `emailAnnouncement.htm` |
| `ManageDatabaseController` | `manageDatabase.htm` |
| `ManagePasswordsController` | `managePasswords.htm` |
| `ResetPasswordController` | `resetPassword.htm`, `forgotPassword.htm` |
| `EditPersonalDataController` | `editPersonalData.htm`, `editVolunteerPersonalData.htm` |
| `InitializeTheDatabaseController` | `initializeTheDatabase.htm` |
| `SetBankSpecificFieldLabelController` | Sets the bank-specific extra volunteer field label |
| `AdminEditController` | `editAllowedValues.htm`, `listAllowedValues.htm`, `editSiteSetting.htm`, `viewSiteSettings.htm`, etc. |
| `ExceptionHandlerControllerAdvice` | Global exception handler → `genericError.jsp` |

## Session Management

`SessionData` (`com.tcts.common`) is stored in the HTTP session. Each controller calls `SessionData.fromSession(session)` to retrieve it (throws `NotLoggedInException` if not logged in).

Key detail: **BankAdmin is also a Volunteer** — when a BankAdmin is logged in, both `sessionData.getBankAdmin()` and `sessionData.getVolunteer()` return non-null.

## Email System

- `EmailUtil` (`com.tcts.email`): Low-level AWS SES sender using SDK v2. From-address comes from `SiteEmail` site setting.
- `EmailSender`: High-level email operations (volunteer signup notification, cancellation, etc.)
- `TemplateUtil`: Renders Apache Velocity templates into HTML email bodies
- Email templates in `src/main/resources/template/*.vm`:
  - `volunteerSignUpToTeacher.vm`, `volunteerSignUpToVolunteer.vm`
  - `volunteerUnregisterEventToTeacher.vm`, `teacherCancelEventToVolunteer.vm`
  - `passwordReset.vm`, `contactUs.vm`, `emailAnnouncement.vm`, `announcementReceipt.vm`

## S3 Integration

`S3Util` (`com.tcts.S3Bucket`) handles document file upload/download. Documents are stored in S3 in site-specific folder paths. Presigned URLs (short-lived) are generated for downloads and passed to JSP views.

## View Structure

JSP files in `src/main/webapp/WEB-INF/pages/`. Common includes:
- `include/header.jsp` / `include/header_innerPage.jsp`
- `include/footer.jsp`
- `include/navigation.jsp`
- `include/errors.jsp`
- `include/commonHead.jsp` / `include/commonHead_innerPage.jsp`

Static assets (CSS, JS, images, fonts) in `src/main/webapp/tcts/`.

## Form Data (`src/main/java/com/tcts/formdata/`)

Forms normally have a corresponding `*FormData` class (simple POJOs with getters/setters), although sometimes these are shared. Validation errors are collected in `Errors` class.

## Security

- `SecurityUtil` (`com.tcts.util`): SHA-256 password hashing with salt
- Session-based authentication (no Spring Security framework)
- Role checking done manually at the top of each controller method
- AWS credentials stored in `application.properties` (excluded from version control)

## Current Development Tasks

See `todo.txt` for active tasks. Key open items:
- Fix FL site "Contact Us" page, logos, document upload bug, add FAQ
- Bug: headers in `header.jsp` need customization per site
- Bug: Teacher homepage doesn't show class descriptions
- Enable prefetching/caching in `DatabaseFactory`
- Write tests

## Testing

- Unit tests use JUnit 4; very few tests exist
- No integration tests
