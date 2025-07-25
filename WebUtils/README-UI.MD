# WebUtils UI Framework Documentation

This document provides an overview and usage examples for the WebUtils UI framework, located in `WebUtils/vue-based/webutils`. This framework is built using Vue.js, jQuery, and Bootstrap, offering a collection of reusable services and components to streamline web application development.

## Application Initialization

Setting up an application to use the WebUtils UI framework involves a few key steps to ensure all dependencies are correctly loaded and the Vue application is properly configured.

### 1. Dependency Setup

For the WebUtils framework to function, applications must create a `lib` folder at the web root. All contents from the `WebUtils\vue-based` directory, which includes dependency libraries (like Vue, Bootstrap, jQuery) and the WebUtils UI framework code, must be copied into this `lib` folder.

### 2. Include Required Libraries in HTML

In any HTML page where the WebUtils framework is used, you must include the necessary CSS and JavaScript files. This ensures that all required styles and libraries are available to the browser.

**Example (`index.html`):**
```html
<!-- Bootstrap CSS -->
<link href="/lib/bootstrap-5.1.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">

<!-- Bootstrap Icons -->
<link href="/lib/bootstrap-icons-1.3.0/bootstrap-icons.css" rel="stylesheet" />

<!-- Font Awesome Icons -->
<link href="/lib/fontawesome-free-6.7.2-web/css/all.min.css" rel="stylesheet">

<!-- Bootstrap Select CSS -->
<link href="/lib/bootstrap-select-1.14.0/bootstrap-select.min.css" rel="stylesheet" />

<!-- WebUtils Framework CSS -->
<link href="/lib/webutils/webutils.css" rel="stylesheet" />

<!-- Core JS Libraries -->
<script src="/lib/jquery-3.6.0/jquery.min.js"></script>
<script src="/lib/popper-2.9.3/popper.min.js"></script>
<script src="/lib/bootstrap-5.1.0/js/bootstrap.min.js"></script>

<!-- Bootstrap Select JS -->
<script src="/lib/bootstrap-select-1.14.0/bootstrap-select.min.js"></script>
```

### 3. Global Application Configuration

Create a single JavaScript file (e.g., `common/app-config.js`) to manage application-level configurations. In this file, import `$appConfiguration` from the WebUtils library and set the desired properties. This file should contain global settings that are not specific to a single page or flow.

The `$appConfiguration` object supports the following properties:

| Property         | Type     | Default | Description                                                                                                                            |
| ---------------- | -------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| `debugEnabled`   | `boolean`| `true`  | If `true`, all debug logs will be printed to the console.                                                                              |
| `errorEnabled`   | `boolean`| `true`  | If `true`, all error logs will be printed to the console.                                                                              |
| `traceEnabled`   | `boolean`| `false` | If `true`, all trace logs will be printed to the console.                                                                              |
| `infoTimeOutSec` | `int`    | `5`     | The timeout in seconds after which info alerts will automatically disappear.                                                           |
| `onSessionExpiry`| `function`| `null`  | A function to be called when a server API call results in a session expiry. This should ideally redirect the user to the login page. |
| `onLogout`       | `function`| `null`  | A function to be called when the user logs out. This can be used to redirect to a logout confirmation or login page.                 |

**Example (`common/app-config.js`):**
```javascript
import { $appConfiguration } from '/lib/webutils/common.js';

// Disable debug and trace logs in production
$appConfiguration.debugEnabled = false;
$appConfiguration.traceEnabled = false;

// Set a custom timeout for info messages
$appConfiguration.infoTimeOutSec = 10;

// Handle session expiry by redirecting to the login page
$appConfiguration.onSessionExpiry = function() {
    alert("Your session has expired. You will be redirected to the login page.");
    // Assuming login page is at /login.html
    window.location.href = '/login.html?returnUrl=' + encodeURIComponent(window.location.href);
};

// Handle logout
$appConfiguration.onLogout = function() {
    // Redirect to a page confirming logout
    window.location.href = '/logout-success.html';
};
```

### 4. Page-Specific JavaScript

For each HTML page, create a corresponding JavaScript file (e.g., `product/index.js` for `product/index.html`). This file will contain the logic specific to that page.

Inside this file:
1.  Create a Vue application instance using the `Webutils.newVueApp()` utility method. This helper function initializes the Vue app and automatically registers all standard WebUtils components and services (like dialogs, forms, and REST services), making them available within the app.
2.  Define your application-specific data, methods, and computed properties within the object passed to `newVueApp()`.
3.  Mount the created Vue app to an HTML element on your page using its ID.

**Example (`jobseeker/index.js`):**
```javascript
import { Webutils } from '/lib/webutils/webutils.js';

// Create a new Vue app with page-specific logic
const app = Webutils.newVueApp({
    data() {
        return {
            message: "Welcome to the Job Seeker Portal!",
            jobs: []
        };
    },
    methods: {
        fetchJobs() {
            // uses $restService, which is auto-injected by newVueApp()
            this.$restService.invokeGet("/api/jobs", null, {
                onSuccess: (result) => {
                    this.jobs = result.response;
                }
            });
        }
    },
    mounted() {
        this.fetchJobs();
    }
});

// Mount the app to the '#app' element in the HTML
app.mount('#app');
```

### 5. Final HTML Page Setup

Finally, update your HTML page to bring everything together:

1.  **Include Scripts:** Add `<script>` tags for your application config file and the page-specific JavaScript file. Make sure these are declared as `type="module"`.
2.  **Define App Container:** Create a `div` element that will serve as the root container for your Vue application. This element's ID must match the one used in `app.mount()`.
3.  **Include Dialogs Component:** Add the `<yk-dialogs>` custom component inside your app container. This component is required for the WebUtils framework's built-in alert, confirmation, and info dialogs to work correctly.

**Example (`jobseeker/index.html`):**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Job Seeker Portal</title>
    <!-- Include all CSS and JS from Step 2 here -->
    <link href="/lib/bootstrap-5.1.0/css/bootstrap.min.css" rel="stylesheet">
    ...
    <script src="/lib/jquery-3.6.0/jquery.min.js"></script>
    ...

    <!-- 1. Include app and page scripts -->
    <script src="/common/app-config.js" type="module"></script>
    <script src="/jobseeker/index.js" type="module"></script>
</head>
<body>
    <!-- 2. Define the Vue application container -->
    <div id="app">
        <h1>{{ message }}</h1>
        
        <ul>
            <li v-for="job in jobs" :key="job.id">{{ job.title }}</li>
        </ul>

        <!-- 3. Include the global dialogs component -->
        <yk-dialogs></yk-dialogs>
    </div>
</body>
</html>
```

## Core Utilities

### `$utils`
A collection of general-purpose utility methods.

To use these utilities in your application's JavaScript files, you can import them directly from `common.js`:

```javascript
import { $utils } from '/lib/webutils/common.js';

// Now you can use the methods, for example:
$utils.alert("This is an alert!");
```

**Methods:**
-   `format(message, args, argIdx)`: Formats a string message with arguments.
    **Usage:**
    ```javascript
    import { $utils } from './common.js';
    $utils.format("Hello, {}! Today is {}.", ["World", "Monday"]); // "Hello, World! Today is Monday."
    ```
-   `formatStr(...args)`: Formats a string similar to `format` but takes arguments directly.
    **Usage:**
    ```javascript
    import { $utils } from './common.js';
    $utils.formatStr("User: {}, ID: {}", "John Doe", 123); // "User: John Doe, ID: 123"
    ```
-   `processTemplate(message, context)`: Processes a template string with a given context.
    **Usage:**
    ```javascript
    import { $utils } from './common.js';
    let context = { name: "Alice", age: 30 };
    $utils.processTemplate("Name: ${name}, Age: ${age}", context); // "Name: Alice, Age: 30"
    ```
-   `deepClone(object)`: Performs a deep clone of an object.
    **Usage:**
    ```javascript
    import { $utils } from './common.js';
    let original = { a: 1, b: { c: 2 } };
    let cloned = $utils.deepClone(original);
    ```
-   `removeArrElement(array, element)`: Removes an element from an array.
    **Usage:**
    ```javascript
    import { $utils } from './common.js';
    let arr = [1, 2, 3];
    $utils.removeArrElement(arr, 2); // arr is now [1, 3]
    ```
-   `populateFieldDef(fieldDef)`: Populates data type and component type for a field definition.
-   `populateFieldDetails(modelDef)`: Populates field details for all fields in a model definition.
-   `divideModelGroups(modelDef, modelFieldGroups, columnCount, groups)`: Divides model fields into groups and rows for UI layout.
-   `alert(message, callback)`: Displays an alert dialog.

    **Parameters:**
    -   `message` (String | Array): The message to display. If an array is provided, the first element is a format string and the rest are arguments.
    -   `callback` (Function): An optional function to be executed after the dialog is closed.

    **Usage:**
    ```javascript
    $utils.alert("Operation completed successfully!");
    
    $utils.alert("User '{}' was created.", ["John"], () => {
        console.log("Alert closed.");
    });
    ```

-   `info(message)`: Displays an informational message that closes automatically. `$appConfiguration.infoTimeOutSec` defines how long the info message is displayed.
    **Parameters:**
    -   `message` (String | Array): The message to display. If an array is provided, the first element is a format string and the rest are arguments.

    **Usage:**
    ```javascript
    $utils.info("Data saved.");
    ```

-   `confirm(message, callback)`: Displays a confirmation dialog with "Yes" and "No" options.

    **Parameters:**
    -   `message` (String | Array): The message to display. If an array is provided, the first element is a format string and the rest are arguments.
    -   `callback` (Function): A function that will be called with `true` if "Yes" is clicked, and `false` otherwise.

    **Usage:**
    ```javascript
    $utils.confirm("Are you sure you want to delete?", (result) => {
        if (result) {
            console.log("User confirmed.");
        }
    });
    ```

-   `input(message, initValue, callback)`: Displays a dialog to get input from the user.

    **Parameters:**
    -   `message` (String | Array): The message to display. If an array is provided, the first element is a format string and the rest are arguments.
    -   `initValue` (String): The initial value to display in the input field.
    -   `callback` (Function): A function that will be called with the user's input. If the user cancels, it's called with `null`.

    **Usage:**
    ```javascript
    $utils.input("Enter your name:", "Guest", (name) => {
        if (name !== null) {
            console.log("User entered: " + name);
        }
    });
    ```
-   `executeWithInProgress(func)`: Executes a function while displaying an "in progress" dialog.
-   `executeAsyncSteps(context, functionLst)`: Executes a list of functions sequentially, which is particularly useful for chaining asynchronous operations like API calls. Each function in the list receives a `next` callback as its first argument, which must be called to proceed to the next function in the sequence. 

	**Parameters:**
	-   `context` (Object): An object that will be used as the `this` context for all functions in the list, allowing them to share state.
	-   `functionLst` (Array): An array of functions to execute in order.

	**Usage:**
	```javascript
	const asyncFlow = {
    	data: {},
    	fetchedUser: function(next, userId) {
        	this.$restService.invokeGet(`/api/users/${userId}`, null, {
            	onSuccess: (result) => {
                	this.data.user = result.response;
                	next(result.response.addressId); // Pass addressId to the next step
            	}
        	});
    	},
    	fetchAddress: function(next, addressId) {
        	this.$restService.invokeGet(`/api/addresses/${addressId}`, null, {
            	onSuccess: (result) => {
                	this.data.address = result.response;
                	next(); // Continue to the final step
            	}
        	});
    	},
    	showResults: function(next) {
        	$utils.alert(`User ${this.data.user.name} lives at ${this.data.address.city}`);
        	next();
    	}
	};

	// Start the asynchronous sequence
	$utils.executeAsyncSteps(asyncFlow, [
    	function(next) { next(123); }, // Start with a user ID
    	asyncFlow.fetchUser,
    	asyncFlow.fetchAddress,
    	asyncFlow.showResults
	]);
	```

### `$logger`
Provides logging functionalities (debug, error, trace, warn).
To use this logger in your application's JavaScript files, you can import them directly from `common.js`:

**Usage:**
```javascript
import { $logger } from '/lib/webutils/common.js';
$logger.debug("This is a debug message.");
$logger.error("An error occurred: {}", errorObject);
```

### `$caseFunc()`
A global function to compare a value with a list of cases and return a corresponding result.

**Usage:**
```javascript
import { $caseFunc } from '/lib/webutils/common.js';
let status = 1;
let statusText = $caseFunc(status, 1, "Active", 2, "Inactive", "Unknown"); // "Active"
```

### `$pageUrl`
Utility to access and modify URL hash and query parameters.

**Methods:**
-   `fetchInfo()`: Fetches the hash part and query parameters from the current URL.

    **Returns:** An object with the following structure (current url: http://domain.com/api#k1=v1&k2=v2?q1=val1:
    ```json
    {
        "hashPart": "k1=v1&k2=v2",
        "hashParams": { "k1": "v1", "k2": "v2" },
        "queryParams": { "q1": "val1" }
    }
    ```

-   `modifyInfo(urlInfo)`: Modifies the current URL without reloading the page.

    **Parameters:**
    -   `urlInfo` (Object): An object containing the URL parts to modify.
        -   `hashPart` (String): The new hash part of the URL.
        -   `hashParams` (Object): Key-value pairs to be set as hash parameters. This is only considered if `hashPart` is not specified.
        -   `queryParams` (Object): Key-value pairs to be set as query parameters.

**Usage:**
```javascript
import { $pageUrl } from '/lib/webutils/common.js';

// 1. Read URL info
let urlInfo = $pageUrl.fetchInfo();
console.log("Current Hash:", urlInfo.hashPart);
console.log("Query Param 'id':", urlInfo.queryParams.id);

// 2. Modify URL
$pageUrl.modifyInfo({ 
    hashPart: "new-section", 
    queryParams: { id: 456, mode: "edit" } 
});

// 3. Modify only hash params
$pageUrl.modifyInfo({ 
    hashParams: { tab: "details" }
});
```

## REST Service

### `$restService`
Handles all REST API interactions with the backend. To use this service, import it from `rest-service.js`:

```javascript
import { $restService } from '/lib/webutils/rest-service.js';
```

#### Common `settings` Object

All `invoke` methods accept a `settings` object to configure the request. The callbacks (`onSuccess`, `onError`, `onResult`) are executed within the provided `context`.

| Property          | Type      | Default           | Description                                                                                                |
| ----------------- | --------- | ----------------- | ---------------------------------------------------------------------------------------------------------- |
| `context`         | `Object`  | `null`            | The `this` context for the callback functions.                                                             |
| `onSuccess`       | `Function`| `null`            | Callback function executed on a successful (2xx) response. Receives a `result` object.                     |
| `onError`         | `Function`| `null`            | Callback function executed on an error (non-2xx) response. Receives an `error` object.                     |
| `onResult`        | `Function`| `null`            | Callback function executed after `onSuccess` or `onError`.                                                 |
| `contentType`     | `String`  | `application/json`| The content type of the request.                                                                           |
| `async`           | `boolean` | `true`            | Whether the request should be asynchronous.                                                                |
| `multipart`       | `boolean` | `false`           | Set to `true` for multipart/form-data requests (e.g., file uploads).                                       |
| `includeAuthToken`| `boolean` | `true`            | If `false`, the `AUTH_TOKEN` header will not be sent with the request.                                     |

**Methods:**
-   `invokePost(url, body, settings)`: Sends a POST request.
    -   `url` (String): The API endpoint.
    -   `body` (Object): The request payload, which will be JSON-stringified.
    -   `settings` (Object): Configuration for the request.

-   `invokePut(url, body, settings)`: Sends a PUT request.
    -   `url` (String): The API endpoint.
    -   `body` (Object): The request payload, which will be JSON-stringified.
    -   `settings` (Object): Configuration for the request.

-   `invokeGet(url, params, settings)`: Sends a GET request.
    -   `url` (String): The API endpoint.
    -   `params` (Object): An object of query parameters to be appended to the URL.
    -   `settings` (Object): Configuration for the request.

-   `invokeDelete(url, params, settings)`: Sends a DELETE request.
    -   `url` (String): The API endpoint.
    -   `params` (Object): An object of query parameters to be appended to the URL.
    -   `settings` (Object): Configuration for the request.

-   `fetchModelDef(name, successCallback, isNoAuthReq)`: Fetches a model definition from the server and caches it.
    -   `name` (String): The name of the model.
    -   `successCallback` (Function): A function to call with the fetched model definition.
    -   `isNoAuthReq` (boolean): If `true`, the request is made to a non-authenticated endpoint.

-   `fetchLovValues(name, lovType, successCallback, parentValue, isNoAuthReq)`: Fetches a List of Values (LOV) from the server and caches it.
    -   `name` (String): The name of the LOV.
    -   `lovType` (String): The type of LOV.
    -   `successCallback` (Function): A function to call with the fetched LOV list.
    -   `parentValue` (any): An optional parent value for dependent LOVs.
    -   `isNoAuthReq` (boolean): If `true`, the request is made to a non-authenticated endpoint.

**Usage Example:**
```javascript
const newProduct = { name: "New Gadget", price: 99.99 };

$restService.invokePost("/api/products", newProduct, {
    onSuccess: (result) => {
        $utils.info(`Product created with ID: ${result.response.id}`);
    },
    onError: (error) => {
        $utils.alert(`Error: ${error.response.message}`);
    }
});
```

## User Service

### `$userService`
Manages user authentication, session, and details. To use this service, import it from `user-service.js`:

```javascript
import { $userService } from '/lib/webutils/user-service.js';
```

**Methods:**

-   `authenticate(loginInfo)`: Authenticates a user against the backend. On successful login, it stores the received authentication token in `sessionStorage` and as a cookie. If `rememberMe` is enabled, it also caches the login credentials in `localStorage`.
    -   `loginInfo` (Object): An object containing login credentials and callbacks.
        -   `userName` (String): The user's username.
        -   `password` (String): The user's password.
        -   `rememberMe` (boolean): If `true`, the credentials will be stored in `localStorage`.
        -   `successCallback` (Function): A callback executed on successful login.
        -   `errorCallback` (Function): A callback executed on login failure.

-   `getLoginInfoFromCache()`: Retrieves the user's credentials (`userName`, `password`, `rememberMe`) that were previously cached in `localStorage` if the "Remember Me" option was selected during a prior login.
    -   **Returns:** An object with the structure `{ userName, password, rememberMe }` if cached data exists, otherwise `null`.

-   `getUserDetails(successCallback, errorCallback, forceRefresh)`: Fetches the details of the currently logged-in user. It first checks for cached details in `sessionStorage`. If not found, or if `forceRefresh` is true, it makes a REST call to the server.
    -   `successCallback` (Function): A callback executed with the user details object.
    -   `errorCallback` (Function): A callback executed if fetching details fails.
    -   `forceRefresh` (boolean): If `true`, forces a refetch from the server, ignoring any cached data.
    -   **Returns (via callback):** A `UserDetails` object with the following structure, plus any application-specific fields:
        ```json
        {
            "userId": 101,
            "userName": "testuser",
            "userSpace": "default",
            "displayName": "Test User",
            "roles": ["USER_ROLE", "ADMIN_ROLE"],
            "jsDateFormat": "dd/MM/yyyy"
        }
        ```

-   `clearSession()`: Clears all user-related data from the current session, including the auth token and user details from `sessionStorage`.

-   `logout()`: Logs the current user out by making a call to the backend, clearing the session via `clearSession()`, removing the auth cookie, and finally invoking the global `$appConfiguration.onLogout()` handler.

-   `hasRoles(roleStr)`: Checks if the currently logged-in user has one or more of the specified roles.
    -   `roleStr` (String): A comma-separated string of role names to check (e.g., `"ADMIN,SUPERVISOR"`).
    -   **Returns:** `true` if the user has at least one of the specified roles, otherwise `false`.

**Usage Example:**
```javascript
// On a login page
function attemptLogin() {
    const loginData = {
        userName: this.username,
        password: this.password,
        rememberMe: this.rememberMe,
        successCallback: () => {
            // Redirect to dashboard on success
            window.location.href = '/dashboard.html';
        },
        errorCallback: () => {
            this.errorMessage = "Login failed. Please try again.";
        }
    };
    $userService.authenticate(loginData);
}

// On a protected page, check for a specific role
function checkAdminAccess() {
    if (!$userService.hasRoles("ADMIN_ROLE")) {
        $utils.alert("Access Denied: You do not have administrative privileges.");
        return false;
    }
    return true;
}
```

## Vue.js Components

All the component widgets mentioned below will be available by default within the HTML element marked as your Vue app. This is because the application's Vue instance is created using the `Webutils.newVueApp()` method, which automatically registers all available widgets, making them ready for use in your templates.



### Generic Field Component: `<yk-model-field>`

This powerful component acts as a wrapper that can render *any* of the above input widgets dynamically. Instead of choosing a specific widget, you provide it with the model definition and the field name, and it automatically selects and configures the correct widget based on the field's metadata (like `dataType`, `lovDetails`, etc.).

This is the preferred way to build forms when using a `<yk-model-form>`, as it handles the rendering logic for you.

**Props:**

*   `modelDef` (Object, required): The full model definition object from the server.
*   `fieldName` (String, required): The name of the field to render.
*   `formData` (Object): The form's data object, used for data binding.
*   `columnCount` (Number, default: `12`): The Bootstrap grid column count for the field.

**Example Usage (inside a custom form):**

```html
<!-- This will render a <yk-input-field> for the 'name' and a <yk-lov-field> for the 'category' -->
<yk-model-field :model-def="productModel" field-name="name" v-model="product.name"></yk-model-field>
<yk-model-field :model-def="productModel" field-name="category" v-model="product.category"></yk-model-field>
```

### Form Components (`forms.js`)

#### `<yk-model-form>`

A powerful component that dynamically generates a complete form based on a server-side model definition. It handles field rendering, layout, client-side validation, and provides hooks for submission and error handling.

**Props:**

*   `modelName` (String, required): The name of the model (e.g., `UserModel`) to generate the form for. The component fetches the model's definition from the server.
*   `columnCount` (Number, default: `1`): The number of columns to use for the form layout.
*   `style` (String): Custom CSS style to be applied to the form's root element.
*   `noAuth` (Boolean, default: `false`): If `true`, fetches the model definition without sending authentication tokens.
*   `groups` (Array): An array of objects to define field grouping and layout. Each object can have the following attributes:
    *   `label` (String): The label to display for the group.
    *   `fields` (Array): A list of field names that should be part of this group.
    *   `default` (Boolean): A flag indicating if this group should be considered the default group. Fields not specified in any group will be added here. If no default group is specified, a new group without a label will be added at the end for the remaining fields.
    *   **Examples:**
        *   `{"label": "Basic Info", "default": true}`
        *   `{"label": "Address Info", fields: ["street", "city", "stateId", "pinCode"]}`
*   `modelValue` (Object): Binds the form's data object using `v-model`, allowing two-way data binding.

**Slots:**

*   `header`: Content to display at the top of the form, before any fields.
*   `footer`: Content to display at the bottom of the form, typically used for action buttons like 'Submit' or 'Cancel'.

**Events:**

*   `update:modelValue`: Emitted on every change to any field in the form, keeping the parent's `v-model` object in sync.

**Methods:**

*   `evaluateForm()`: Triggers validation on all fields. If any validation fails, it displays the errors and throws an exception. If all validations pass, it returns the form's data object.
*   `setFormStatusListener(callback)`: Registers a listener function that is invoked whenever the form's overall error status changes. The callback receives a boolean `formHasErrors`.
*   `setServerErrors(errors)`: Takes an array of field-specific error objects from a server response and displays them next to the corresponding fields. This is useful for validations that can only be performed on the server (e.g., checking for a unique username).

**Example:**

Here is how to implement a user registration form using `<yk-model-form>`.

**1. The HTML (`user-registration.html`)**

The component is used directly in the HTML. We bind the form data to `userModel` and the group definitions to `formGroups`. The footer slot is used for the submit button and a global error message.

```html
<div id="app">
    <h1>User Registration</h1>

    <yk-model-form 
        ref="registrationForm"
        model-name="UserModel"
        :column-count="2"
        :groups="formGroups"
        v-model="userModel">

        <!-- Use the footer slot for buttons and global messages -->
        <template #footer>
            <div v-if="globalError" class="alert alert-danger">{{ globalError }}</div>
            <button @click="submitRegistration" class="btn btn-primary">Register</button>
        </template>

    </yk-model-form>
</div>
```

**2. The JavaScript (`user-registration.js`)**

The corresponding JavaScript file manages the form's state and logic.

```javascript
import { Webutils } from '/lib/webutils/webutils.js';

const app = Webutils.newVueApp({
    data() {
        return {
            // Data model for the form, bound using v-model
            userModel: {},
            // Global error message for server-side issues
            globalError: null,
            // Definition for grouping fields in the form
            formGroups: [
                {
                    label: "Account Details",
                    fields: ["username", "password", "confirmPassword"]
                },
                {
                    label: "Contact Information",
                    fields: ["email", "phone"]
                }
            ]
        };
    },
    methods: {
        /**
         * Called when the Register button is clicked.
         */
        submitRegistration() {
            try {
                // 1. Evaluate the form. Throws an error if client-side validation fails.
                const formData = this.$refs.registrationForm.evaluateForm();

                // 2. If validation passes, submit the data to the server.
                this.$restService.invokePost("/api/users/register", formData, {
                    onSuccess: (result) => {
                        $utils.alert("Registration successful!");
                    },
                    onError: this.onRegistrationError
                });

            } catch (error) {
                // This catch block is triggered by evaluateForm() on validation failure.
                console.log("Client-side validation failed.");
            }
        },

        /**
         * Handles errors from the server API call.
         */
        onRegistrationError(error) {
            // Check if the error response contains field-specific errors
            if (error.response && error.response.fieldErrors) {
                // 3. Display server-side validation errors on the form fields.
                this.globalError = "Please correct the errors.";
                this.$refs.registrationForm.setServerErrors(error.response.fieldErrors);
            } else {
                // Display a generic global error
                this.globalError = error.response ? error.response.message : "An unknown error occurred.";
            }
        },

        /**
         * Listener for the form's overall validation status.
         */
        onFormStatusChange(formHasErrors) {
            // 4. If the form becomes error-free, clear the global error message.
            if (!formHasErrors) {
                this.globalError = null;
            }
        }
    },
    mounted() {
        // 5. Register the status listener when the component is mounted.
        this.$refs.registrationForm.setFormStatusListener(this.onFormStatusChange);
    }
});

app.mount('#app');
```

#### `<yk-multi-row-model-form>`
The `<yk-multi-row-model-form>` component is designed to accept one or more model objects of the same type from the user. By default, it displays a single row for the child model object, and users can dynamically add, modify, or remove rows.

**Key Features:**
- Displays one row by default for a child model object.
- Allows users to add more rows (more model objects).
- Enables modification of existing model row values.
- Provides an option to remove existing model rows.

**Props:**
-   `modelName`: (String, required) The name of the model for each row.
-   `modelValue`: (Array, required) Used with `v-model` for two-way binding of the multi-row model data.
-   `labelProp`: (String, required) The property name in each row object to be used as a label for confirmation dialogs, especially during deletion.
-   `defaultValue`: (Object, optional) A default object to be used when adding a new, empty row.

**Events:**
-   `update:modelValue`: This event is emitted whenever there is a change in the rows (addition, modification, or removal), allowing for seamless two-way data binding with `v-model`.

**Example Usage:**
Consider a `UserProfile` model that contains a list of `LanguageDetails` objects. To allow a user to input multiple languages, `<yk-multi-row-model-form>` can be used.

*   **Parent Model:** `UserProfile.java`
*   **Child Model:** `LanguageDetails.java`

The following snippet from `profile-basic.html` shows how to implement this:
```html
<yk-multi-row-model-form modelName="LanguageDetails" 
    :defaultValue="newLangRow"
    labelProp="language.label"
    v-model="formData.languages">
</yk-multi-row-model-form>
```
In this example:
- `modelName="LanguageDetails"` specifies that each row will be a `LanguageDetails` object.
- `v-model="formData.languages"` binds the component to the `languages` array within the `formData` object.
- `labelProp="language.label"` indicates that the `label` property of the `language` object within a row should be used in confirmation messages.
- `:defaultValue="newLangRow"` provides a template for new rows.


#### `<yk-search-form>`
A component that dynamically builds a search form based on a server-side query model definition. It fetches the required fields, renders the appropriate inputs, and handles the execution of the search.

**Props:**
-   `queryName` (String, required): The name of the search query model (e.g., `productSearch`) that the form should be built for.
-   `columnCount` (Number, default: `2`): The number of columns to use for the form layout.
-   `tabularSearch` (Boolean, default: `true`): A flag indicating the expected search result format. When `true`, it calls an API endpoint that returns results structured for the `<yk-search-results>` component.

**Events:**
-   `search(results)`: Emitted when the search is successfully executed. The `results` object is the response from the server, which can be directly passed to the `setSearchResults` method of the `<yk-search-results>` component.

**Methods:**
-   `search()`: Programmatically triggers a search.
-   `refreshSearch()`: Re-executes the last performed search with the same criteria.

#### `<yk-search-results>`
A component designed to display search results in a table format. It is intended to be used in conjunction with `<yk-search-form>`.

**Props:**
-   This component does not have any props. It is controlled via its methods.

**Events:**
-   `select(rowData)`: Emitted when a user clicks on a row. `rowData` is an object containing the data for the selected row.
-   `double-click(rowData)`: Emitted when a user double-clicks on a row.

**Methods:**
-   `setSearchResults(searchResult)`: The primary method used to populate the table with data. The `searchResult` object should be the payload received from the `<yk-search-form>`'s `search` event.

**Example: Implementing a Search Page**

This example demonstrates how to create a complete search and results view for products.

**1. The HTML**

Set up the search form and the results table. The `@search` event from the form is wired to the `onSearchResults` method, and the results table is given a `ref` so its methods can be called.

```html
<div id="app">
    <h3>Search for Products</h3>
    <yk-search-form 
        query-name="productSearch" 
        @search="onSearchResults">
    </yk-search-form>

    <hr/>

    <h3>Search Results</h3>
    <yk-search-results 
        ref="searchResultsTable" 
        @select="onRowSelected" 
        @double-click="onRowDoubleClicked">
    </yk-search-results>
</div>
```

**2. The JavaScript**

The script orchestrates the data flow between the two components.

```javascript
import { Webutils } from '/lib/webutils/webutils.js';

const app = Webutils.newVueApp({
    data() {
        return {
            selectedProduct: null
        };
    },
    methods: {
        /**
         * This method is called when the yk-search-form emits the 'search' event.
         */
        onSearchResults(results) {
            console.log("Received search results:", results);
            // Use the ref to call the method on the results component
            this.$refs.searchResultsTable.setSearchResults(results);
        },

        /**
         * Called when a row is selected in the results table.
         */
        onRowSelected(rowData) {
            console.log("Row selected:", rowData);
            this.selectedProduct = rowData;
            $utils.info(`Product '${rowData.name}' selected.`);
        },

        /**
         * Called when a row is double-clicked.
         */
        onRowDoubleClicked(rowData) {
            console.log("Row double-clicked:", rowData);
            $utils.alert(`Viewing details for '${rowData.name}'.`);
            // You could open an edit dialog here, for example:
            // this.$refs.productEditDialog.displayForEdit(rowData);
        }
    }
});

app.mount('#app');
```


### Modal Dialog Components (`modal-dialogs.js`)

#### `<yk-modal-dialog>`
A customizable modal dialog component that can be used to display custom content, forms, or information to the user. It provides a structured layout with a header, body, and footer, and can be controlled programmatically.

**Props:**
-   `id` (String, required): A unique identifier for the modal dialog. This ID is used to open and close the modal programmatically.
-   `title` (String): The text to display in the modal's header.
-   `submitText` (String): The text for the primary (submit) button in the footer. If not provided, the button will not be displayed.
-   `closeText` (String): The text for the secondary (close/cancel) button in the footer.
-   `size` (String, default: `modal-xl`): The size of the modal dialog. Accepts standard Bootstrap modal size classes like `"modal-lg"`, `"modal-sm"`, etc.
-   `topMargin` (String): A custom CSS top margin for the dialog (e.g., `"5rem"`).

**Slots:**
-   Default Slot: The content placed inside the `<yk-modal-dialog>` tags will be rendered as the body of the modal.

**Events:**
-   `submit`: Emitted when the submit button (as defined by `submitText`) is clicked.

**Methods:**
-   `display(callback, config)`: Displays the modal.
    -   `callback` (Function, optional): A function to be executed when the modal is hidden.
    -   `config` (Object, optional): A configuration object. Can contain `title` to dynamically set the modal title.
-   `close()`: Closes (hides) the modal dialog.

**Example Usage:**

Here is how to create and control a modal dialog to confirm a user action.

**1. The HTML**

Place the `<yk-modal-dialog>` in your component's template. Use the `ref` attribute to access its methods from your script. The content for the modal body goes inside the component tags.

```html
<div id="app">
    <button @click="showConfirmation" class="btn btn-danger">Delete Item</button>

    <yk-modal-dialog 
        ref="confirmDialog"
        id="deleteConfirmModal" 
        title="Confirm Deletion" 
        submit-text="Yes, Delete" 
        close-text="Cancel"
        size="modal-md"
        @submit="handleDelete">

        <!-- This is the modal body -->
        <p>Are you sure you want to delete this item? This action cannot be undone.</p>

    </yk-modal-dialog>
</div>
```

**2. The JavaScript**

Use the `ref` to call the `display()` and `close()` methods on the modal component.

```javascript
import { Webutils } from '/lib/webutils/webutils.js';

const app = Webutils.newVueApp({
    methods: {
        showConfirmation() {
            // Use the ref to access the component and call its display method
            this.$refs.confirmDialog.display();
        },
        handleDelete() {
            console.log("Delete action confirmed by user.");
            
            // Perform the delete operation here...
            
            // Close the modal after the action is complete
            this.$refs.confirmDialog.close();
            $utils.info("Item has been deleted.");
        }
    }
});

app.mount('#app');
```

#### `<yk-model-form-dialog>`
A modal dialog that contains a dynamically generated form based on a server-side model definition. It is designed for both creating new records and updating existing ones, complete with built-in form submission and handling.

**Props:**
-   `id` (String, required): A unique identifier for the modal dialog.
-   `title` (String, required): The text to display in the modal's header.
-   `method` (String, required): The HTTP method (e.g., `"POST"`, `"PUT"`) to use for form submission.
-   `url` (String, required): The API endpoint URL where the form data will be submitted.
-   `size` (String, default: `modal-xl`): The size of the modal dialog (e.g., `"modal-lg"`).
-   `modelName` (String, required): The name of the model to be used for generating the form fields.
-   `columnCount` (Number, default: `2`): The number of columns to use for the form layout.

**Events:**
-   `submit(success, result)`: Emitted when the form submission is complete.
    -   `success` (Boolean): `true` if the submission was successful, `false` otherwise.
    -   `result` (Object): The full response object from the server.

**Methods:**
-   `display(callback, extraConfig)`: Displays the modal for creating a new entry.
    -   `callback` (Function, optional): A function to be executed after the dialog is closed.
    -   `extraConfig` (Object, optional): An object for additional configuration. Can be used to set a custom `saveLabel`.
-   `displayForEdit(data, callback)`: Displays the modal for editing an existing entry, pre-filling the form with the provided `data`.
    -   `data` (Object): The object containing the data to edit.
    -   `callback` (Function, optional): A function to be executed after the dialog is closed.
-   `close()`: Closes (hides) the modal dialog.

**Example Usage:**

Here is how to use `<yk-model-form-dialog>` to manage a "Product" entity.

**1. The HTML**

Define the component in your template, setting its properties and listening for the `submit` event. Use a `ref` to access its methods.

```html
<div id="app">
    <button @click="openNewProductDialog" class="btn btn-primary">Add Product</button>
    <button @click="editProduct(existingProduct)" class="btn btn-secondary">Edit Product</button>

    <yk-model-form-dialog 
        ref="productDialog"
        id="productFormModal" 
        title="Product Editor" 
        method="POST" 
        url="/api/products" 
        model-name="ProductModel"
        @submit="onProductFormSubmit">
    </yk-model-form-dialog>
</div>
```

**2. The JavaScript**

Manage the dialog's state and handle its submission event.

```javascript
import { Webutils } from '/lib/webutils/webutils.js';

const app = Webutils.newVueApp({
    data() {
        return {
            // Example existing product data for editing
            existingProduct: {
                id: 123,
                name: "Super Widget",
                price: 19.99,
                inStock: true
            }
        };
    },
    methods: {
        /**
         * Opens the dialog in "new" mode.
         */
        openNewProductDialog() {
            // The callback will be executed when the dialog closes for any reason.
            const onDialogClose = () => console.log("New product dialog closed.");
            this.$refs.productDialog.display(onDialogClose);
        },

        /**
         * Opens the dialog in "edit" mode, pre-filled with data.
         */
        editProduct(productData) {
            const onDialogClose = () => console.log("Edit product dialog closed.");
            this.$refs.productDialog.displayForEdit(productData, onDialogClose);
        },

        /**
         * Handles the result of the form submission.
         */
        onProductFormSubmit(success, result) {
            if (success) {
                $utils.info("Product saved successfully!");
                // Optionally, refresh your list of products here
            } else {
                // The dialog remains open on failure, showing validation errors.
                $utils.alert("Error saving product: " + result.response.message);
            }
        }
    }
});

app.mount('#app');
```

### Navigation Components (`nav-bar.js`)

These components provide routing and dynamic content loading capabilities for building single-page application (SPA) style user interfaces. They manage navigation state via URL hashes and dynamically load and render child components based on the selected navigation item.

#### `<yk-route-nav-bar>`
A responsive, top navigation bar, typically used as the main navigation for an application. It can display a logo, user information, and a list of navigation items.

**Props:**
-   `id` (String, required): A unique identifier for the navigation bar.
-   `logoIcon` (String): The URL path to the logo image to be displayed on the left side.
-   `defaultItem` (String): The `id` of the navigation item that should be selected by default when the page loads.
-   `items` (Array, required): An array of navigation item objects. Each object defines a link in the bar and has the following structure:
    -   `id` (String): A unique ID for the item.
    -   `componentName` (String): The tag name of the Vue component to be loaded when this item is active (e.g., `home-page`).
    
    -   `label` (String): The text to display for the item.
    -   `uri` (String): The URL path to the HTML file for the component's template (if not an SFC).
    -   `route` (String): The URL hash value associated with this item (e.g., `home`).
    -   `script` (String): The URL path to the JavaScript file where the component is defined.
-   `disableUserInfo` (Boolean, default: `false`): If `true`, the user display name and logout button will be hidden.
-   `subnav` (Boolean, default: `false`): If `true`, renders with a style suitable for a sub-navigation bar.

**Events:**
-   `navChanged(activeItem)`: Emitted when a navigation item is clicked and the route changes. The `activeItem` object contains the full definition of the item that was selected.

**Example Usage:**

This example shows a main application page with a top navigation bar that loads different "page" components into a content area.

**1. The HTML (`index.html`)**

```html
<div id="app">
    <yk-route-nav-bar
        id="mainNav"
        logo-icon="/images/app-logo.png"
        default-item="home"
        :items="menuItems"
        @nav-changed="onNavChange">
    </yk-route-nav-bar>

    <!-- The dynamic component will be rendered here -->
    <div class="container mt-3">
        <component :is="activeComponent"></component>
    </div>

    <yk-dialogs></yk-dialogs>
</div>
```

**2. The JavaScript (`index.js`)**

```javascript
import { Webutils } from '/lib/webutils/webutils.js';

const app = Webutils.newVueApp({
    data() {
        return {
            activeComponent: null, // Holds the currently active page component
            menuItems: [
                {
                    id: 'home', 
                    label: 'Home', 
                    route: 'home', 
                    componentName: 'home-page',
                    uri: '/home/home.html',
                    script: '/home/home.js'
                },
                {
                    id: 'products', 
                    label: 'Products', 
                    route: 'products', 
                    componentName: 'product-page', 
                    uri: '/product/product.html',
                    script: '/product/products.js'
                }
            ]
        };
    },
    methods: {
        /**
         * Handles the navChanged event from the nav bar.
         */
        onNavChange(navItem) {
            console.log(`Navigation changed to: ${navItem.label}`);
            // The component definition is attached to the nav item object
            this.activeComponent = navItem.componentDef;
        }
    }
});

app.mount('#app');
```

#### `<yk-route-side-bar>`
A vertical navigation bar, typically used for sub-navigation within a specific section of an application (e.g., a user profile page with multiple tabs).

**Props:**
-   `id` (String, required): A unique identifier for the side bar.
-   `defaultItem` (String): The `id` of the navigation item to select by default.
-   `items` (Array, required): An array of navigation item objects. The structure is the same as for `<yk-route-nav-bar>`, but with two additional optional properties for icons:
    -   `icon` (String): A CSS class for an icon (e.g., `bi bi-gear`).
    -   `iconColor` (String): A CSS color for the icon (e.g., `blue`).

**Events:**
-   `navChanged(activeItem)`: Emitted when a navigation item is clicked. The `activeItem` object contains the full definition of the selected item.

**Example Usage:**

This example shows a user profile page with a side menu to switch between different profile sections.

**1. The HTML (`profile.html`)**

```html
<div id="app" class="d-flex">
    <yk-route-side-bar
        id="profileSideNav"
        default-item="basicInfo"
        :items="profileMenuItems"
        @nav-changed="onSideNavChange">
    </yk-route-side-bar>

    <div class="flex-grow-1 p-3">
        <component :is="activeProfileSection"></component>
    </div>

    <yk-dialogs></yk-dialogs>
</div>
```

**2. The JavaScript (`profile.js`)**

```javascript
import { Webutils } from '/lib/webutils/webutils.js';

const app = Webutils.newVueApp({
    data() {
        return {
            activeProfileSection: null,
            profileMenuItems: [
                {
                    id: 'basicInfo',
                    label: 'Basic Info',
                    route: 'basic',
                    icon: 'fa fa-user',
                    componentName: 'basic-info-editor',
                    uri: '/profile/basic-info.html',
                    script: '/profile/basic-info.js'
                },
                {
                    id: 'security',
                    label: 'Security',
                    route: 'security',
                    icon: 'fa fa-lock',
                    componentName: 'security-settings',
                    uri: '/profile/security.html'
                    script: '/profile/security.js'
                }
            ]
        };
    },
    methods: {
        onSideNavChange(navItem) {
            this.activeProfileSection = navItem.componentDef;
        }
    }
});

app.mount('#app');
```

## For Developers: Extending the Framework

### Adding Custom Validations

The WebUtils validation framework is designed to be extensible, allowing you to add new custom validations that flow from server-side Java annotations to the client-side JavaScript.

#### The Validation Flow

1.  **Server-Side Annotation:** A validation starts with a Java annotation (either a standard Jakarta/Hibernate one or a custom one) on a model's field.

2.  **Metadata Extraction:** The `ValidationDefBuilder` service on the server inspects these annotations. It uses a mapping configuration (`validation-mapping.xml`) to translate the Java annotation into a `ValidationDef` object. This object contains:
    *   `name`: A short name for the validation (e.g., `maxLength`).
    *   `config`: A map of the annotation's attributes (e.g., `{ value: 100 }`).
    *   `errorMessage`: The error message to be displayed on failure.

3.  **Client-Side Service:** This `ValidationDef` object is sent to the client and used by the `$validationService` in `validation.js`.

4.  **Client-Side Execution:** The `$validationService`'s `validate()` method is called for a field. It looks up the corresponding validator function in its `validators` map using the `name` from the `ValidationDef` and executes it.

#### Steps to Add a New Validation

Let's say you want to add a new `@ZipCode` validation.

1.  **Create the Java Annotation:**
    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Constraint(validatedBy = ZipCodeValidator.class) //for server-side validation
    public @interface ZipCode {
        String message() default "Invalid ZIP code";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
    ```

2.  **Update `validation-mapping.xml`:**
    Add a new entry to map your annotation to a client-side validator name.
    ```xml
    <validationConfig 
        type="com.yourcompany.validation.annotations.ZipCode" 
        targetType="java.lang.String" 
        name="zipCode" />
    ```

3.  **Implement the Client-Side Validator in `validation.js`:**
    -   **Add a default message:**
        ```javascript
        "defaultMessages": {
            // ... other messages
            "zipCode": "Invalid ZIP code format."
        },
        ```
    -   **Add the validator function:**
        ```javascript
        "validators": {
            // ... other validators
            "zipCode": function(config, value) {
                if (!value) return true; // Don't validate empty values, use @Required for that
                return /^\d{5}(-\d{4})?$/.test(value);
            }
        }
        ```

Now, whenever a model field is annotated with `@ZipCode`, it gets rendered in a `<yk-model-form>` and the `zipCode` validator will be automatically triggered on the client side.

### Adding New Input Widgets

New input widgets can be added to `input-fields.js` by using the `newVueUiComponent()` factory function. This function provides a baseline of common functionality for all input fields.

#### Available Input Widgets

| Name                          | Purpose                                                                                             | Server Type                     | Sample Image                                                                                                |
| ----------------------------- | --------------------------------------------------------------------------------------------------- | ------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| `yk-input-field`              | A versatile input for text, passwords, emails, numbers, etc.                                        | `STRING`, `PASSWORD`, `INTEGER` | <img src="https://raw.githubusercontent.com/yukthitech/webutils/master/WebUtils/tmp/input-fields.png?raw=true" alt="Input Field" width="200"/> |
| `yk-textarea-field`           | A multi-line text input area.                                                                       | `MULTI_LINE_STRING`             |                                                                                                             |
| `yk-switch`                   | A toggle switch for boolean values.                                                                 | `BOOLEAN`                       | <img src="https://raw.githubusercontent.com/yukthitech/webutils/master/WebUtils/tmp/input-fields.png?raw=true" alt="Switch Field" width="100"/> |
| `yk-lov-field`                | A dropdown select field populated from a List of Values (LOV).                                      | `LIST_OF_VALUES`                |                                                                                                             |
| `yk-editable-lov-field`       | An advanced dropdown that allows selecting an existing option or typing a new one.                  | `EDITABLE_LIST_OF_VALUES`       |                                                                                                             |
| `yk-multi-editable-lov-field` | Similar to the editable LOV, but allows for selecting and creating multiple values.                 | `MULTI_EDITABLE_LIST_OF_VALUES` |                                                                                                             |
| `yk-input-file`               | A component for uploading a single file.                                                            | `FILE`                          |                                                                                                             |
| `yk-input-image`              | A specialized file upload component for images with a preview.                                      | `IMAGE`                         |                                                                                                             |
| `yk-ver-input-field`          | An input field that requires verification (e.g., OTP).                                              | `VERIFICATION`                  |                                                                                                             |
| `yk-captcha-field`            | A field for CAPTCHA verification.                                                                   | `CAPTCHA`                       |                                                                                                             |
| `yk-html-editor`              | A rich text editor powered by TinyMCE.                                                              | `MULTI_LINE_STRING`             |                                                                                                             |


#### Core `newVueUiComponent` Functionality

*   **Events:**
    *   `created`: Initializes the component's `fieldValue` from the `modelValue` prop and builds the `fieldInfo` object.
    *   `watch`: A watcher on the `fieldValue` triggers the `onFieldValueChange` method whenever the value changes.
    *   `update:modelValue`: Emitted to enable two-way `v-model` binding with the parent.
    *   `valueChanged`: Emitted with the new value and the field's info, allowing parent components to react to changes.

*   **Exposed Methods:**
    *   `reset(val)`: Resets the field's value to the provided value or an empty string.
    *   `getError()`: Returns the current validation error message, if any.
    *   `displayError()`: Returns `true` if there is an error and the `enableError` prop is true.
    *   `setServerError(error)`: Allows a parent component (like `<yk-form>`) to push a server-side validation error to the field.

#### Steps to Add a New Widget

1.  **Define the Vue Component:** Create a new Vue component object.
2.  **Use the Factory Function:** Call `newVueUiComponent('your-widget-name', yourComponentObject)`.
3.  **Define the Template:** Provide the HTML structure for your widget in the `template` property.
4.  **Add Custom Logic:** If needed, add custom `data`, `props`, and `methods` to your component object. You can also hook into the component lifecycle by defining methods like `onCreate` and `onMounted`.
5.  **Map to Server Data Type:** In `WebUtils/vue-based/webutils/model-def-service.js`, do one of the following:
    *   Add a direct mapping in the `$dataTypeMapping` object from a server-side data type to your new component.
    *   Or, if the mapping is conditional, add logic to the `populateFieldDef()` method to set the `componentType` based on the field's metadata.

**Example: Creating a simple `<yk-color-picker>` widget:**

```javascript
// In WebUtils/vue-based/webutils/input-fields.js

newVueUiComponent('yk-color-picker', {
    template: `
        <div class="form-group">
            <label class="webutil-field-label">{{fieldInfo.label}}:</label>
            <input
                type="color" 
                class="form-control form-control-color" 
                :name="fieldInfo.name"
                v-model="fieldValue"
                :title="fieldInfo.label"
            />
            <div class="invalid-feedback d-block">{{fieldInfo.error}}</div>
        </div>
    `
});

// In WebUtils/vue-based/webutils/model-def-service.js
var $dataTypeMapping = {
    // ... other mappings
    "COLOR": {"componentType": "yk-color-picker", "inputType": "text"},
};
```

This new widget will now be available for use and will automatically have all the standard validation and data-binding features.