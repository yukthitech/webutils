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

-   **Backend:**
    -   **Spring Boot:** The core of the backend framework.
    -   **yukthi-data:** A powerful ORM for data persistence. [Documentation](https://raw.githubusercontent.com/yukthitech/utils/refs/heads/master/yukthi-orm/README.MD)
    -   **yukthi-free-marker:** For processing FreeMarker templates. [Documentation](https://raw.githubusercontent.com/yukthitech/utils/refs/heads/master/yukthi-free-marker/README.md)
    -   **yukthi-validators:** For server-side request validation, used alongside Hibernate/Jakarta Validators. [Documentation](https://raw.githubusercontent.com/yukthitech/utils/refs/heads/master/yukthi-validators/README.md)
-   **Persistence:**
    -   **Liquibase:** For database schema versioning and metadata management.
-   **UI:**
    -   **Vue.js, Bootstrap, jQuery:** The core UI technologies.
    -   **bootstrap-datepicker:** A date picker widget.
    -   **bootstrap-icons & fontawesome-free:** For glyph icons.
    -   **tinymce:** A rich content editor.
    -   **vue-cookies:** for handling cookies.

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

    Next, you need to create an entity class that maps to the table you just created. This class should be placed in the `services` module. An entity class can extend one of the following base classes:
    -   `com.yukthitech.webutils.repository.WebutilsBaseEntity`: This will include `id` and `version` (for optimistic locking) in the entity.
    -   `com.yukthitech.webutils.repository.WebutilsTrackedEntity`: This is an extension of `WebutilsBaseEntity`, which will additionally include creation/updation details (like `createdBy`, `createdOn`, `updatedBy`, `updatedOn`).

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

    Now, you need to create a repository interface for your entity. This interface should extend `com.yukthitech.webutils.repository.IWebutilsRepository` and be placed in the `services` module. The `IWebutilsRepository` itself extends `com.yukthitech.persistence.ICrudRepository`, which provides basic common methods to save, update, delete, find by ID, search, count, and manage transactions.

    ```java
    public interface IProductRepository extends IWebutilsRepository<ProductEntity> {

    }
    ```

4.  **Create the model:**

    Next, you need to create a model class (a DTO) that represents the data transferred between the client and the server. This class should be placed in the `commons` module and should be annotated with `@com.yukthitech.webutils.common.annotations.Model` to be used for dynamic UI generation.

    Model classes generally extend `com.yukthitech.webutils.common.BaseModel`, which adds common `id` and `version` fields.

    **In the Model class:**
    ```java
    //...
    private Long userId;
    //...
    ```

    Here is an example of a simple model:
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

    For entity fields that represent relations (e.g., a `UserEntity` object field), the corresponding model will typically have a simple ID field (e.g., `userId` of type `long`). The `@com.yukthitech.persistence.annotations.PropertyMapping` annotation is used on the entity's relation field to bridge this gap. It tells the framework how to map the model's ID field to the `id` subproperty of the related entity during persistence (saving/updating from a model) and how to map the entity's relation back to the model's ID field when fetching data.

    For example:

    **In the Entity class:**
    ```java
    //...
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @PropertyMapping(type = JobSeekerModel.class, from = "userId", subproperty = "id")
    private UserEntity user;
    //...
    ```

5.  **Create the service:**

    Now, you need to create a service class that contains the business logic for your entity. This class should be placed in the `services` module. Service classes generally extend `com.yukthitech.webutils.services.BaseCrudService`, which provides the following functionality:
    -   Maps models to entities and vice-versa.
    -   Provides basic CRUD functionality.
    -   Injects tracking data (user and dates) if the entity is a `WebutilsTrackedEntity`.
    -   Persists files separately if the entity has file-based fields.

    ```java
    @Service
    public class ProductService extends BaseCrudService<ProductModel, ProductEntity, IProductRepository> {

    }
    ```

6.  **Create the controller:**

    Finally, you need to create a controller class that exposes the APIs for your entity. This class should be placed in the `services` module.
    -   For simple CRUD operations, extend `com.yukthitech.webutils.controllers.SimpleBaseCrudController`.
    -   If the model contains file/image fields, extend `com.yukthitech.webutils.controllers.BaseControllerWithAttachments`.

    The controller class should be annotated with `@com.yukthitech.webutils.annotations.ActionName("entityName")`, and each method should also be annotated with `@com.yukthitech.webutils.annotations.ActionName("operationName")`. The combination of these names is used by the UI to invoke the API.

    ```java
    @Action("product")
    @RestController
    @RequestMapping("/products")
    public class ProductController extends SimpleBaseCrudController<ProductModel, ProductService> {

    }
    ```

7.  **Create the UI:**

    Now, you can create a UI for your entity. You can use the framework's dynamic UI generation capabilities by referencing the model name in a `<yukthi-form>` component.

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
-   The default value is specified using the `@DefaultValue` annotation. The value can be a static string, a number, a boolean, or it can be loaded from a classpath resource or a system property.

    **Examples:**

    -   **Static String:** `@DefaultValue("8.3333%")`
    -   **Number:** `@DefaultValue("90")`
    -   **Boolean:** `@DefaultValue("true")`
    -   **From Classpath Resource:** `@DefaultValue(resource = "/terms_n_conditions_template.html")`
    -   **From System Property:** `@DefaultValue(property = "jobOpening.smsTemplateForNew")`

-   The field type is determined based on the Java type of the field and any annotations that are present.

### Field Types and UI Widget Mapping

The framework determines a normalized `FieldType` for each model field, which in turn dictates the UI widget used for that field. This process is influenced by various annotations. Additionally, validation annotations are processed and included in the `FieldDef` to enable client-side validation.

The following steps outline how a field's `FieldType` is determined:

-   **`@LOV`**: Marks a field as a "List of Values," which will be rendered as a dropdown (select) or list in the UI. Refer to the `LOV (List of Values)` section for more details.

    ```java
    @LOV(name = "activeEmployerLov")
    private Long employerId;
    ```

-   **`@DateTime`**: Marks a field to accept both date and time, typically rendered with a date-time picker widget.

-   **`@Captcha`**: Designates a field for captcha verification. The field type should be `ValueWithToken`.

-   **`@NeedVerification`**: Used for fields that require verification, such as a phone number or email address. The field type should be `ValueWithToken`, and the `VerificationType` can be specified (e.g., `VerificationType.MOBILE` or `VerificationType.EMAIL`).

-   **Enum Types**: If a field's type is an `enum`, it is automatically treated as a static LOV, with the enum constants populating the dropdown options.

-   **`@CustomType`**: Allows for a custom field type, where the UI widget must be manually implemented.

-   **Standard Java Types**: For fields without the above annotations, the `FieldType` is determined by the Java type:
    -   `String`: `STRING`
    -   `int`, `long`, `short` (and wrappers): `INTEGER`
    -   `float`, `double` (and wrappers): `FLOAT`
    -   `boolean` (and wrapper): `BOOLEAN`
    -   `Date`: `DATE`
    -   `FileInfo`: `FILE` (allows file uploads)
    -   `ImageInfo`: `IMAGE` (allows image uploads with a preview)
    -   `Color`: `COLOR`

-   **String Field Annotations**: For `String` fields, the following annotations further refine the UI widget:
    -   `@MultilineText`: `MULTI_LINE_STRING` (renders a `<textarea>`)
    -   `@Password`: `PASSWORD` (renders a password input with masked text)
    -   `@Html`: `HTML` (renders a rich text editor like TinyMCE)
    -   `@Color`: `COLOR` (renders a color picker widget)

### Field Behavior Annotations

The following annotations control the behavior and appearance of UI fields:

-   `@ReadOnly`: Makes a field non-editable.
-   `@NonDisplayable`: Hides a field from the UI, though its value is still sent to the client.
-   `@FullWidth`: In a multi-column layout, this field will span the full width of the form.
-   `@Format`: Specifies the display format, typically for `Date` fields (e.g., `@Format("dd/MM/yyyy")`).

### Validation Annotations

Validation annotations from `jakarta.validation.constraints` (e.g., `@Required`, `@MaxLen`, `@Min`, `@Pattern`) are processed and included in the `FieldDef`. This allows the UI framework to perform client-side validation, providing immediate feedback to the user.

## LOV (List of Values)

A field can be marked as a "List of Values" (LOV) using the `@LOV` annotation. This will render the field as a dropdown or list in the UI. There are three types of LOVs:

-   **`STATIC_TYPE`**: This type is automatically assigned when the field is an `enum`. The enum constants become the LOV options. The label for each option can be customized using the `@Label` annotation on the enum constant.

    ```java
    public enum Priority {
        LOW, 
        @Label("Medium Priority")
        MEDIUM, 
        HIGH
    }
    
    // In the model:
    private Priority taskPriority;
    ```

-   **`DYNAMIC_TYPE`**: This LOV fetches its options from a service or repository method at runtime. The `name` attribute of the `@LOV` annotation is used to reference the method.

    -   **`@LovMethod`**: Annotate a service method with `@LovMethod` to expose it as a dynamic LOV. The method should return a `List` of `LovOption` objects.

        ```java
        // In a service class:
        @LovMethod(name = "activeUsersLov")
        public List<LovOption> getActiveUsers() {
            // ... fetch active users and return as List<LovOption>
        }

        // In the model:
        @LOV(name = "activeUsersLov", type = LovType.DYNAMIC_TYPE)
        private Long userId;
        ```

    -   **`@LovQuery`**: Annotate a repository method with `@LovQuery` to expose it as a dynamic LOV. 

        ```java
        // In a repository interface:
        @LovQuery(name = "customerLov", labelField = "name", valueField = "id")
        public List<Customer> findActiveCustomers(SearchQuery searchQuery);

        // In the model:
        @LOV(name = "customerLov", type = LovType.DYNAMIC_TYPE)
        private Long customerId;
        ```

-   **`STORED_TYPE`**: This LOV is persisted in the database. The options are fetched from the `WEBUTILS_STORED_LOV` and `WEBUTILS_STORED_LOV_OPTION` tables. This type of LOV can be configured to allow users to add new options at runtime.

    -   **Editable LOV**: If the field type is `EditableLovValue`, the user will be able to add new options to the stored LOV at runtime.

        ```java
        // In the model:
        @LOV(name = "productCategories", type = LovType.STORED_TYPE)
        private EditableLovValue category;
        ```

    -   **Non-Editable LOV**: For non-editable stored LOVs, the field type should be `String`, `long`, or `int`.

        ```java
        // In the model:
        @LOV(name = "statusCodes", type = LovType.STORED_TYPE)
        private String statusCode;
        ```

-   **Dependent LOVs**: The `parentField` attribute of the `@LOV` annotation can be used to create dependent LOVs. The parent field must be defined before the child field in the model. The options of the child LOV will be filtered based on the value of the parent field.

    ```java
    // In the model:
    @LOV(name = "countryLov")
    private String country;

    @LOV(name = "stateLov", parentField = "country")
    private String state;
    ```

## Search Operations

WebUtils supports highly dynamic, metadata-driven search operations. The following steps outline how to define search functionality:

1.  **Define the Search Query Method:**

    In your repository interface, create a method annotated with `@com.yukthitech.webutils.annotations.SearchQueryMethod`. This annotation registers the method as a searchable query.

    -   `name`: A unique name for the search query.
    -   `queryModel`: The class that defines the search filter fields.
    -   `customizer`: An optional `ISearchResultCustomizer` implementation to modify the results.

    The method must accept a single parameter of type `com.yukthitech.persistence.repository.search.SearchQuery` and return a `List` of result POJOs. You can also use `@com.yukthitech.persistence.repository.annotations.OrderBy` to specify a default sort order.

    Before invoking the search method, the registered `ISecurityService` (if any) method `isAuthorized(SecurityInvocationContext context)` will be invoked to check for authorization of the current user to execute this search method. Application-specific annotations can be used on these methods to provide authorization information.

    ```java
    // In IProductRepository.java
    @SearchQueryMethod(name = "productSearch", queryModel = ProductSearchQuery.class)
    @OrderBy("name")
    public List<ProductSearchResult> searchProducts(SearchQuery searchQuery);
    ```

2.  **Create the Query Model:**

    Create a query model POJO that defines the fields for the search filter. This class should be annotated with `@com.yukthitech.webutils.common.annotations.Model` at the class level, enabling the framework to collect dynamic metadata for rendering the search widget.

    -   Each field that acts as a filter should be annotated with `@com.yukthitech.persistence.repository.annotations.Condition` from `yukthi-data` to define the query condition (e.g., `Operator.LIKE`, `Operator.GE`). Only non-null fields will be considered when building the search query.
    -   These fields can be annotated with `@com.yukthitech.webutils.common.annotations.ContextAttribute` by passing a property expression to the `value` attribute. The property expression will be executed on the attribute map of `WebutilsContext`. If specified, the current field value will be ignored, and the result of the specified property expression will be used. For example, `currentUser.userId` will inject the current user ID as this field value, which in turn will be used as a query condition.

    ```java
    // In ProductSearchQuery.java
    @Model
    public class ProductSearchQuery {
        @Condition(op = Operator.LIKE, ignoreCase = true)
        private String name;

        @Condition(op = Operator.GE)
        private Double minPrice;

        @Condition(op = Operator.LE)
        private Double maxPrice;

        @ContextAttribute("currentUser.userId")
        private Long createdByUserId;

        // Getters and setters
    }
    ```

3.  **Create the Result Model:**

    Create a result POJO object that is used by the search method in the return type. This class should also be annotated with `@com.yukthitech.webutils.common.annotations.Model`, which helps the framework to collect the metadata required for display.

    -   Fields of this class will be annotated with `@com.yukthitech.persistence.repository.annotations.Field` from `yukthi-data` to map entity field names to these POJO fields. Fields without the `@Field` annotation will not be populated.
    -   This POJO can extend `com.yukthitech.webutils.common.BasicSearchResult`, which includes common fields like `id` and other tracking fields.
    -   The `@com.yukthitech.webutils.common.annotations.SearchFieldInfo` annotation can be used to specify the order of fields in the UI (mainly used with a big value like 1000 to ensure these fields are displayed at the end). The `resultType` attribute of this annotation can be used to mark phone and email fields so that in the UI, corresponding action icons will be displayed for these fields (like a call option for a phone and a send mail option for a mailId).

    ```java
    // In ProductSearchResult.java
    @Model
    public class ProductSearchResult extends BasicSearchResult {
        @Field("name")
        private String name;

        @Field("price")
        private Double price;

        @SearchFieldInfo(resultType = SearchResultType.EMAIL)
        @Field("vendor.email")
        private String vendorEmail;

        @SearchFieldInfo(order = 1000)
        @Field("creationDate")
        private Date creationDate;

        // Getters and setters
    }
    ```

4.  **Implement Customizers (Optional):**

    -   An application can register implementations of `com.yukthitech.webutils.search.ISearchCustomizer` as a Spring service. If available, this interface's `customizeQuery()` method will be invoked before the execution of the query method by passing a context object that will have the search method details (being invoked) and the current query object. The query object can be customized as per business needs.
    -   An application can register implementations of `com.yukthitech.webutils.search.ISearchResultCustomizer` as a Spring service. If available, this interface's `customize()` method will be invoked after the execution of the query method by passing a context object that will have the search method details (invoked) and the obtained results. The result objects can be customized as per business needs.

5.  **Security and User Space Filtering:**

    If the application has an `ISecurityService` and its `getUserSpaceIdentity()` method returns a non-empty string, then the query will be injected with a condition to limit the search `spaceIdentity` with this user-space.

### Search Controllers

The search query widget from the UI uses APIs exposed by the following controllers:

-   **`SearchSettingsController`**: This exposes CRUD operations for `SearchSettingsEntity`, which holds customized settings per query for the current user. These settings mainly define which result columns to be displayed, their order, and the page size.
-   **`SearchController`**: Exposes APIs to:
    -   Get the metadata of the search query used to display the search widget.
    -   Expose the search query. Along with the query POJO JSON, this API accepts pagination details like `pageSize` and `pageNo`.
    -   Export the search results of the current query as an Excel file.

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

