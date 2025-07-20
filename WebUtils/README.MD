# WebUtils

WebUtils is a powerful and flexible multi-tier framework built upon the robust foundations of Spring Boot and a curated selection of other cutting-edge technologies. This framework is designed to streamline the development of modern web applications by providing a comprehensive suite of features that cater to both the backend and frontend aspects of your project.

## Table of Contents

- [WebUtils](#webutils)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Dependencies](#dependencies)
  - [Framework Modules](#framework-modules)
  - [Application's Structure](#applications-structure)
  - [Webutils Entities and Tables](#webutils-entities-and-tables)
  - [Application Properties](#application-properties)
  - [Application Configurations](#application-configurations)
  - [Authentication](#authentication)
  - [Authorization](#authorization)
  - [End-to-End Flow](#end-to-end-flow)
  - [Dynamic UI Generation](#dynamic-ui-generation)
  - [LOV (List of Values)](#lov-list-of-values)
  - [Search Operations](#search-operations)
  - [Important Features or Services](#important-features-or-services)
  - [Application Configuration](#application-configuration)
  - [Email Service](#email-service)
  - [Async Task Service](#async-task-service)
  - [Other services](#other-services)

## Features

- **Backend:**
  - Simplified persistence definition and querying.
  - Out-of-the-box CRUD (Create, Read, Update, Delete) flows for new entities.
  - Ready-to-use, fully customizable authentication and authorization support.
  - A rich library of reusable services.
  - APIs to expose application metadata, enabling the generation of dynamic user interfaces.
- **UI:**
  - A modern UI framework built with Vue.js, jQuery, and Bootstrap.
  - A collection of reusable services and components.
  - A platform for generating dynamic UIs based on server-side API contracts (metadata).

## Dependencies

- **Backend:**
  - **Spring Boot:** The core of the backend framework.
  - **yukthi-data:** A powerful ORM for data persistence.
  - **yukthi-free-marker:** For processing FreeMarker templates.
  - **yukthi-validators:** For server-side request validation, used alongside Hibernate/Jakarta Validators.
- **Persistence:**
  - **Liquibase:** For database schema versioning and metadata management.
- **UI:**
  - **Vue.js, Bootstrap, jQuery:** The core UI technologies.
  - **bootstrap-datepicker:** A date picker widget.
  - **bootstrap-icons & fontawesome-free:** For glyph icons.
  - **tinymce:** A rich content editor.
  - **vue-cookies:** for handling cookies.

## Framework Modules

- **Services:** Contains all backend services, including generic services and abstract classes with generic CRUD functionality.
- **Commons:** Contains POJOs and related classes that define the contract between the clients and the application server.
- **Client:** A base client library that can be used by application-specific Java-based client libraries.
- **WebUtils:** The parent POM module for the project. It includes:
  - **dbschema:** Defines the schema of tables used by the framework, using Liquibase for schema versioning.
  - **vue-based:** A collection of JS libraries that applications can use for quick UI development. This folder also contains the "webutils" subfolder, which provides Vue.js-based services and components for generating dynamic UIs.

## Application's Structure

While the framework is flexible, we recommend the following project structure for applications using WebUtils:

- **dbschema:** Contains the Liquibase binary and DB schema version files for both WebUtils and the application.
- **Parent project module:** The root module of your application (e.g., `my-app`).
- **Common module:** Depends on `webutils-commons` and contains all request/response model POJOs, validations, and search queries (e.g., `my-app-common`).
- **Service module:** Depends on `webutils-services` and contains controllers, services, repositories, and entities (e.g., `my-app-services`).
- **Webapp module:** Contains the web code (HTML, JS, CSS) and the UI code from `webutils-web`. This module is the entry point of the project and includes the launcher application (e.g., `my-app-webapp`). It typically has the following structure:
  - `src/main/java`: Java code with the launcher.
  - `src/main/config`: Configuration files required by the application.
  - `web`: The actual UI code.
    - `web/lib`: Contains all dependency UI libraries, including the "webutils" UI framework.
    - `web/common`: Common services, utils, and configuration for different UI pages.
    - `web/*`: Other folders as per the application's needs.

## Webutils Entities and Tables

WebUtils expects certain tables to be part of the database to support its features. The following are the entities and their corresponding tables that are expected or supported by the framework. The ones marked as mandatory should be included in your application's database schema.

- **Mandatory Tables (`WebUtils/dbschema/webutils_basic.xml`)**
  - **UserEntity:**
    - **Table:** `WEBUTILS_USERS`
    - **Purpose:** Maintains basic details of application users and is required for authentication.
  - **SessionEntity:**
    - **Table:** `WEBUTILS_USER_SESSION`
    - **Purpose:** Maintains user session tokens for the application.
  - **FileEntity:**
    - **Table:** `WEBUTILS_FILE_ENTITY`
    - **Purpose:** Stores files uploaded by users.

- **LOV (List of Values) Tables (`WebUtils/dbschema/webutils_stored_lov.xml`)**
  - **StoredLovEntity:**
    - **Table:** `WEBUTILS_STORED_LOV`
    - **Purpose:** Represents a parent LOV that can be identified by a name.
  - **StoredLovOptionEntity:**
    - **Table:** `WEBUTILS_STORED_LOV_OPTION`
    - **Purpose:** Stores the options (key-value pairs) for a given LOV.

- **Mail Template Tables (`WebUtils/dbschema/webutils_email.xml`)**
  - **MailTemplateEntity:**
    - **Table:** `WEBUTILS_MAIL_TEMPLATE`
    - **Purpose:** Represents a mail template (based on FreeMarker) that can be used to generate and send emails.

To use these tables in your application, you need to copy the appropriate version files into your DB versioning system.

## Application Properties

The following are the application properties that can be specified in your application's `application.properties` file to configure WebUtils:

| Property Name | Description | Default Value |
| --- | --- | --- |
| `app.auth.enabled` | Enables or disables authentication on requests. | `true` |
| `app.date.format` | The default date format for JSON serialization/deserialization. | `dd/MM/yyyy` |
| `webutils.auth.protectedBaseUri` | The prefix for URIs that should be protected by authentication. | (empty string) |
| `webutils.ui.freeUris` | Comma-separated list of exact URIs that can be accessed without authentication. | (none) |
| `webutils.noAuth.models` | Comma-separated list of model names that can be accessed without authentication. | (none) |
| `webutils.noAuth.static.lovs` | Comma-separated list of static LOV names that can be accessed without authentication. | (none) |
| `webutils.noAuth.dynamic.lovs` | Comma-separated list of dynamic LOV names that can be accessed without authentication. | (none) |
| `webutils.ui.path` | Comma-separated list of UI paths to be considered as static resources. | (none) |
| `webutils.ui.resourcesFolder` | The folder where UI files can be found, relative to the current folder. | (none) |
| `webutils.ui.welcomePage` | The default page to be displayed when the user accesses the root path (`/`). | `index.html` |
| `webutils.ui.clientCache.disabled` | If `true`, client-side caching for static resources is disabled. | `false` |
| `webutils.ui.clientCache.cacheTimeInSec` | The cache time in seconds for static resources. | `3600` |
| `webutils.session.timeOut.inSecs` | The user session timeout in seconds. | `36_000` |
| `webutils.session.updateGap.inSecs` | The minimum time in seconds after which the session access time is updated. | `120` |
| `webutils.fmarker.excludeDefaultDirectives` | If `true`, the default directives from `yukthi-freemarker` are disabled. | `false` |
| `webutils.fmarker.excludeDefaultMehods` | If `true`, the default methods from `yukthi-freemarker` are disabled. | `false` |
| `webutils.async.threadCount` | The thread pool size for background and scheduled tasks. | `5` |
| `webutils.email.verification.template` | The name of the email template used for sending OTPs. | (none) |
| `webutils.verification.otpTokenTimeSec` | The OTP token validity time in seconds. | `180` |
| `webutils.verification.verificationTokenTimeSec` | The verification token validity time in seconds. | `600` |
| `webutils.user.tenantSpaceBased` | If `true`, users are associated with tenant spaces. | `false` |
| `webutils.app.base.package` | The application-specific package to be scanned for classes and methods. | (none) |

## Application Configurations

Your application should have a Spring configuration XML file (starting with `spring-`) in the classpath root. This file should configure the following beans:

- **DataSource and RepositoryFactory:** For persistence.
- **EmailServerSettings:** For configuring the SMTP mail server.

Here is an example of what this configuration might look like:

```xml
<bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
    <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/my-app-db" />
    <property name="username" value="root" />
    <property name="password" value="password" />
</bean>

<bean id="repositoryFactory" class="com.yukthitech.persistence.repository.RepositoryFactory">
    <property name="dataSource" ref="dataSource" />
</bean>

<bean id="emailServerSettings" class="com.yukthitech.webutils.services.EmailServerSettings">
    <property name="host" value="smtp.gmail.com" />
    <property name="port" value="587" />
    <property name="username" value="my-email@gmail.com" />
    <property name="password" value="my-password" />
</bean>
```

## Authentication

To implement authentication, you need to provide a Spring service that implements the `IAuthenticationService` interface. This interface has methods for authentication, password changes, and password resets. The implementation should return a `UserDetails` object, which contains the user entity and the roles assigned to the user.

A default implementation, `DefaultAuthenticationService`, is provided, which uses the `UserEntity` table. You can extend this class to provide custom functionality, such as custom user details or application-specific roles.

Here is an example of a custom authentication service:

```java
@Service
public class MyAuthenticationService extends DefaultAuthenticationService {

    @Override
    public UserDetails<Role> toUserDetails(UserEntity user) {
        // Building custom user details with info from user and extra meta info
        //   Add required app specific roles to user details based on business needs
        AppUserDetails userDet = ...;
        return userDet;
    }

    @Override
    public void notifyResetPassword(UserDetails<Role> userDetails, String resetPassword) {
        // Send a notification to the user about the password reset
    }
}
```

## Authorization

To implement authorization, you need to provide a Spring service that implements the `ISecurityService` interface. This interface has methods for authorizing requests to different APIs and operations.

The most important method to implement is `isAuthorized(SecurityInvocationContext context)`, which is used to authorize API requests. The context object provides details about the API and the controller method being invoked.

Here is an example of a custom security service:

```java
@Service
public class MySecurityService implements ISecurityService {

    @Override
    public boolean isAuthorized(SecurityInvocationContext context) {
        // Custom authorization logic here
        return true;
    }

    @Override
    public boolean isAuthorized(WebutilsOperation operation) {
        // Authorize WebUtils-controlled operations
        return true;
    }

    @Override
    public boolean isAuthorized(FileEntity fileEntity) {
        // Authorize access to user-uploaded files
        return true;
    }

    @Override
    public boolean isAuthorized(StoredLovEntity lovEntity) {
        // Authorize access to stored LOVs
        return true;
    }

    @Override
    public void addSecurityCustomization(FileEntity fileEntity) {
        // Add custom attributes to the file entity before saving
    }
}
```

## End-to-End Flow

This section provides a step-by-step guide on how to create a new entity with CRUD operations in a WebUtils application. We will use a "Product" entity as an example.

1.  **Create the table:**

    First, you need to create the table for your entity in the database. You can do this by adding a new changeset to your Liquibase schema file.

    ```xml
    <changeSet author="author_name" id="product_table">
        <createTable tableName="PRODUCTS">
            <column name="ID" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="NAME" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="DESCRIPTION" type="VARCHAR(1000)"/>
            <column name="PRICE" type="DECIMAL(10, 2)"/>
        </createTable>
    </changeSet>
    ```

2.  **Create the entity:**

    Next, you need to create an entity class that maps to the table you just created. This class should be placed in the `services` module.

    ```java
    @Entity
    @Table(name = "PRODUCTS")
    public class ProductEntity extends WebutilsTrackedEntity {

        @Column(name = "NAME")
        private String name;

        @Column(name = "DESCRIPTION")
        private String description;

        @Column(name = "PRICE")
        private Double price;

        // Getters and setters
    }
    ```

3.  **Create the repository:**

    Now, you need to create a repository interface for your entity. This interface should extend `IWebutilsRepository` and be placed in the `services` module.

    ```java
    public interface IProductRepository extends IWebutilsRepository<ProductEntity> {

    }
    ```

4.  **Create the model:**

    Next, you need to create a model class that represents the data that will be transferred between the client and the server. This class should be placed in the `commons` module.

    ```java
    @Model
    public class ProductModel extends BaseModel {

        @NotEmpty
        private String name;

        private String description;

        @Min(0)
        private Double price;

        // Getters and setters
    }
    ```

5.  **Create the service:**

    Now, you need to create a service class that contains the business logic for your entity. This class should extend `BaseCrudService` and be placed in the `services` module.

    ```java
    @Service
    public class ProductService extends BaseCrudService<ProductModel, ProductEntity, IProductRepository> {

    }

    ```

6.  **Create the controller:**

    Finally, you need to create a controller class that exposes the APIs for your entity. This class should extend `SimpleBaseCrudController` and be placed in the `services` module.

    ```java
    @Action("product")
    @RestController
    @RequestMapping("/products")
    public class ProductController extends SimpleBaseCrudController<ProductModel, ProductService> {

    }
    ```

7.  **Create the UI:**

    Now, you can create a UI for your entity. You can use the framework's dynamic UI generation capabilities to create a form for your entity.

    ```html
    <div id="product-form">
        <yukthi-form model-name="ProductModel"></yukthi-form>
    </div>

    <script>
        new Vue({
            el: '#product-form'
        });
    </script>
    ```

## Dynamic UI Generation

WebUtils can automatically generate UIs for your models. This is done by using the `@Model` annotation on your model classes. The framework then uses this annotation to generate a `ModelDef` object, which contains the metadata for the model. This metadata is then used by the UI framework to generate the UI.

The `ModelDef` object is built by the `ModelDefBuilder` class. The following is a high-level overview of how the `ModelDef` is built:

-   The model name is the simple name of the class, unless it is overridden by the `name` attribute of the `@Model` annotation.
-   The label is derived from the class name, unless it is overridden by the `@Label` annotation.
-   The date format is obtained from the application configuration properties.
-   A `FieldDef` object is built for each non-static field in the class. Fields with the `@IgnoreField` annotation are ignored.

The `FieldDef` object is built by the `FieldDefBuilder` class. The following is a high-level overview of how the `FieldDef` is built:

-   The name is the same as the field name.
-   The label is derived from the field name, unless it is overridden by the `@Label` annotation.
-   The description is specified using the `@Description` annotation.
-   The default value is specified using the `@DefaultValue` annotation.
-   The field type is determined based on the Java type of the field and any annotations that are present.

### Field Types

The following are the supported field types:

-   `STRING`
-   `INTEGER`
-   `FLOAT`
-   `BOOLEAN`
-   `DATE`
-   `FILE`
-   `IMAGE`
-   `COLOR`
-   `MULTI_LINE_STRING`
-   `PASSWORD`
-   `HTML`

### Field Annotations

The following are the supported field annotations:

-   `@LOV`: Marks a field as a list of values.
-   `@DateTime`: Marks a field as a date-time field.
-   `@Captcha`: Marks a field as a captcha field.
-   `@NeedVerification`: Marks a field as a field that needs verification.
-   `@CustomType`: Marks a field as a custom type.
-   `@MultilineText`: Marks a string field as a multi-line text field.
-   `@Password`: Marks a string field as a password field.
-   `@Html`: Marks a string field as an HTML field.
-   `@Color`: Marks a string field as a color field.
-   `@ReadOnly`: Marks a field as read-only.
-   `@NonDisplayable`: Marks a field as non-displayable.
-   `@FullWidth`: Marks a field as a full-width field.
-   `@Format`: Specifies the format for a date field.

## LOV (List of Values)

A field can be marked as a "List of Values" (LOV) using the `@LOV` annotation. An LOV can be one of the following types:

-   **`STATIC_TYPE`**: This type is automatically set by the framework when the field type is an enum. The enum values themselves become the options for the LOV.
-   **`DYNAMIC_TYPE`**: This type of LOV references a service or repository method. At runtime, these methods are invoked to get the list of options for the LOV.
-   **`STORED_TYPE`**: This is a persisted variety of LOV, where the LOV details (such as name and label) and its options are stored in the database. When the LOV is displayed, the options are fetched from the database. With configuration, this LOV enables users to add new options at runtime.

For a field, the dynamic or stored type can be specified using the `type` attribute of the `@LOV` annotation. The `name` attribute specifies the name of the LOV to be used.

If the field type is `EditableLovValue`, the field will be considered editable (meaning the user can add more options at runtime). The editable feature is only possible for `STORED_TYPE`.

For non-editable fields, the field type should be one of `int`, `long`, or `String`.

The `parentField` attribute of the `@LOV` annotation can be used to refer to another field in the same class. The parent field should be present before the current field. This indicates that the options of the current field are based on the value of the `parentField`. For example, a "state" field would depend on a "country" field.

For enum-based `STATIC_TYPE` LOVs, the value and label for the options will be the enum constant names. The label of an enum constant can be customized or overridden using the `@Label` annotation.

The `@LovMethod` annotation can be used to mark a service method as an LOV method, which can provide a dynamic LOV at runtime. This method can then be referenced by the `@LOV` annotation for `DYNAMIC_TYPE`.

The `@LovQuery` annotation can be used to mark a repository method as an LOV method, which can provide a dynamic LOV at runtime. This method can then be referenced by the `@LOV` annotation for `DYNAMIC_TYPE`.

## Search Operations

WebUtils supports highly dynamic search operations end-to-end, which are fully metadata-driven.

Below are the steps on how an application can define search methods and their corresponding customization behavior:

1.  Mark a repository method with the `@SearchQueryMethod` annotation. In this annotation, the `name` attribute can be used to specify a name for the search query, and the `queryModel` can be used to specify the query POJO type that will define the query conditions. Optionally, the `customizer` attribute can be used to specify a query result customizer, which will be invoked after the search results are obtained. This can modify the result values as per the business logic.

    -   These methods should accept a single parameter of type `SearchQuery`.
    -   These methods should always return a `List` of result POJOs.
    -   These search methods are invoked via APIs exposed by WebUtils. Before invoking the search method, the registered `ISecurityService` (if any) method `isAuthorized(SecurityInvocationContext context)` will be invoked to check for authorization of the current user to execute this search method. So, application annotations can be used on these methods to provide authorization information.
    -   The `@OrderBy` annotation from `yukthi-data` can be used to specify the field details by which ordering should be done.

2.  Create a query POJO (referred to by the `queryModel` attribute of the `@SearchQueryMethod` annotation).

    -   This should have the `@Model` annotation at the class level so that dynamic metadata can be collected by the framework, which in turn will be used to render the search widget. All the rules mentioned in the model-def building will be applicable here as well.
    -   Each field of this class will have `@Condition` annotations from `yukthi-data` defining the conditions to be applied in the search query. Only non-null fields will be considered when building the search query.
    -   These fields can be annotated with the `@ContextAttribute` annotation by passing a property expression to the `value` attribute. The property expression will be executed on the attribute map of `WebutilsContext`. If specified, the current field value will be ignored, and the result of the specified property expression will be used. Note that the current user details will be available on request threads with the name `currentUser`, so an expression like `currentUser.userId` will inject the current user ID as this field value, which in turn will be used as a query condition.

3.  Create a result POJO object that is used by the search method in the return type. This also should be annotated with `@Model`, which helps the framework to collect the metadata required for display.

    -   Fields of this class will be annotated with the `@Field` annotation from `yukthi-data` to map the entity field name to these POJO fields. Fields without the `@Field` annotation will not be populated.
    -   This POJO can extend `BasicSearchResult`, which includes common fields like `id` and other tracking fields.
    -   The `@SearchFieldInfo` annotation can be used to specify the order of fields in the UI (mainly used with a big value like 1000 to ensure these fields are displayed at the end). The `resultType` attribute of this annotation can be used to mark phone and email fields so that in the UI, corresponding action icons will be displayed for these fields (like a call option for a phone and a send mail option for a mailId).

4.  An application can register implementations of `ISearchCustomizer` as a Spring service. If available, this interface's `customizeQuery()` method will be invoked before the execution of the query method by passing a context object that will have the search method details (being invoked) and the current query object. The query object can be customized as per business needs.

5.  If the application has an `ISecurityService` and its `getUserSpaceIdentity()` method returns a non-empty string, then the query will be injected with a condition to limit the search `spaceIdentity` with this user space.

6.  An application can register implementations of `ISearchResultCustomizer` as a Spring service. If available, this interface's `customize()` method will be invoked after the execution of the query method by passing a context object that will have the search method details (invoked) and the obtained results. The result objects can be customized as per business needs.

The search query widget from the UI uses APIs exposed by the following controllers:

-   **`SearchSettingsController`**: This exposes CRUD operations for `SearchSettingsEntity`, which holds customized settings per query for the current user. These settings mainly define which result columns to be displayed, their order, and the page size.
-   **`SearchController`**: Exposes an API to get the metadata of the search query used to display the search widget, an API to expose the search query, and an API to export the search results of the current query as an Excel file.

## Important Features or Services

### Application Configuration

In any Spring application, static configurations (which do not change at runtime) are maintained in `application.properties` (or similar files). Configurations that are dynamic and may be changed at runtime can be managed using `AppConfigService`, which provides the following methods:

-   `void set(String name, Object value)`: To add or update a configuration.
-   `Object get(String name)`: To fetch a configuration object for a specified name.
-   `boolean delete(String name)`: To delete a configuration.

Note that the values can be any custom objects (they do not have to be strings). During a save, the value is converted into JSON (with type information).

### Email Service

The `EmailService` class can be used to send emails. It has the following two methods to send emails:

-   `void sendEmail(EmailServerSettings settings, MailTemplateEntity template, Object context)`: This uses the specified settings (SMTP details, authentication details, etc.) to connect to the mail server and uses the specified template entity to compose the to/cc/bcc list, subject, and message body. Using the specified context, it processes these templates and sends the mail appropriately.
-   `void sendEmail(String templateName, Object context)`: This method fetches the mail template entity with the specified name from the database and uses the `EmailServerSettings` bean configured in the Spring context. It then uses the fetched template entity to compose the to/cc/bcc list, subject, and message body. Using the specified context, it processes these templates and sends the mail appropriately.

The context object specified in the above methods is generally used to process mail template strings. These objects also serve the following extra purposes:

-   If the context is an instance of `IMailCustomizer`, the "From" field can be specified using the `getFromId()` method. By default, this will be the same as the `userName` configured in the email server settings.
-   If the context class is annotated with `@MailTemplateConfig`, the class fields will be scanned for `@MailAttachment` annotations. The content of the field annotated with `@MailAttachment` will be added as an attachment to the mail. The following types are supported for these attachment fields:
    -   `File`: The specified file will be attached to the mail.
    -   `FileAttachment`: Along with the file, an attachment name can be specified using this POJO.
    -   `Collection`: If a collection, then it should be a collection of `FileAttachment`.
    -   `String`: A file attachment will be generated with the current field's string content.
    -   `byte[]`: A file attachment will be generated with the current field's byte array content.
    -   `Image`: An image file will be generated with the specified image and will be attached to the mail.

By default, the name of the attachment is obtained from the `name` attribute of the `@MailAttachment` annotation. The `FileAttachment` type can be used to specify a dynamic name.

### Async Task Service

`AsyncTaskService` is a service that can be used to execute or schedule tasks in the background. The following methods are available:

-   `executeTask(String name, Runnable runnable, long delay, TimeUnit timeUnit)`: Executes the specified runnable only once after the specified delay.
-   `executeTask(String name, Runnable runnable)`: Executes the specified runnable in the background immediately.
-   `scheduleWithFixedDelay(String name, Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit)`: Schedules the specified runnable repeatedly with the specified delay gap.

Spring-registered services/components can have methods annotated with the below annotations, which get auto-registered with `AsyncTaskService`:

-   `@ScheduledTask`: Used to execute the target method at a specified time.
-   `@RepetitiveTask`: Used to execute the target method with a specified delay between executions.

### Other services

-   **`ClassScannerService`**: Helps in finding classes and methods with a specified annotation or methods with a specified annotation. The packages that need to be scanned can be specified by applications using the property `webutils.app.base.package`. By default, the `com.yukthitech.webutils` package is part of the scan.
-   **`CurrentUserService`**: This service's `getCurrentUserDetails()` method can be invoked to fetch the user details for the current request on the current thread (if the current thread is not a request thread, then `null` will be returned).
-   **`WebutilsRepositoryFactory`**: By default, the CRUD service mentioned above will have access to the current repository instance. To access other repositories, this factory's `getRepository(Class<R> repoType)` method can be used.
-   **`FreeMarkerService`**: This service, during startup, creates a `FreeMarkerEngine` instance and registers all the methods annotated with `@FreeMarkerMethod` and `@FreeMarkerDirective` annotations. This service can be used to process templates and condition-templates, which in turn can use all the application-registered FreeMarker methods. Applications can exclude the loading of `yukthi-freemarker`-provided default methods/directives using the boolean-valued application properties `webutils.fmarker.excludeDefaultMehods`/`webutils.fmarker.excludeDefaultDirectives`, respectively.