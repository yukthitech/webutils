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
Manages user authentication and details.

**Methods:**
-   `authenticate(loginInfo)`: Authenticates a user.
-   `getLoginInfoFromCache()`: Retrieves cached login information.
-   `getUserDetails(successCallback, errorCallback, forceRefresh)`: Fetches user details.
-   `clearSession()`: Clears user session data.
-   `logout()`: Logs out the current user.
-   `hasRoles(roleStr)`: Checks if the current user has specified roles.

**Usage:**
```javascript
import { $userService } from './user-service.js';

$userService.authenticate({
    userName: "testuser",
    password: "password123",
    rememberMe: true,
    successCallback: () => {
        console.log("Login successful!");
    },
    errorCallback: () => {
        console.error("Login failed.");
    }
});
```

## Validation Service

### `$validationService`
Provides client-side validation functionalities.

**Methods:**
-   `validate(dataType, validations, value, model)`: Validates a value against a set of rules.

**Usage:**
```javascript
import { $validationService } from './validation.js';

try {
    $validationService.validate("STRING", [{ name: "required" }], "someValue", {});
    console.log("Validation successful.");
} catch (error) {
    console.error("Validation failed:", error.message);
}
```

## Vue.js Components

### Form Components (`forms.js`)

#### `<yk-form>`
A generic form component for submitting data.

**Props:**
-   `method`: HTTP method (e.g., "POST", "PUT").
-   `url`: API endpoint URL.
-   `formData`: Object to hold form data and validation errors.
-   `validationActivated`: Boolean to control error display.

**Events:**
-   `submit`: Emitted on form submission, with success status and result.

**Usage:**
```html
<template>
    <yk-form method="POST" url="/api/products" :form-data="productForm" @submit="onFormSubmit">
        <yk-input-field :field="productNameField" v-model="productForm.data.name" />
        <!-- other fields -->
        <button type="button" @click="$refs.productForm.submitForm()">Save</button>
    </yk-form>
</template>

<script>
import { formComponents } from './forms.js';
import { inputFieldComponents } from './input-fields.js';

export default {
    components: {
        'yk-form': formComponents['yk-form'],
        'yk-input-field': inputFieldComponents['yk-input-field'],
    },
    data() {
        return {
            productForm: {
                data: { name: '' },
                errorFields: [],
                displayErrors: false
            },
            productNameField: { name: 'name', label: 'Product Name', validations: [{ name: 'required' }] }
        };
    },
    methods: {
        onFormSubmit(success, result) {
            if (success) {
                $utils.info("Product saved successfully!");
            } else {
                $utils.alert("Failed to save product: " + result.response.message);
            }
        }
    }
}
</script>
```

#### `<yk-search-form>`
Component for building dynamic search forms based on server-side query models.

**Props:**
-   `queryName`: Name of the search query model.
-   `columnCount`: Number of columns for layout (default: 2).
-   `simpleSearch`: Boolean, if true uses a simplified search API.

**Events:**
-   `search`: Emitted with search results.

**Usage:**
```html
<template>
    <yk-search-form query-name="productSearch" @search="onSearchResults" />
</template>

<script>
import { formComponents } from './forms.js';

export default {
    components: {
        'yk-search-form': formComponents['yk-search-form'],
    },
    methods: {
        onSearchResults(results) {
            console.log("Search results:", results);
            // Pass results to a <yk-search-results> component
        }
    }
}
</script>
```

#### `<yk-search-results>`
Displays search results in a table format.

**Events:**
-   `select`: Emitted when a row is selected.
-   `double-click`: Emitted when a row is double-clicked.

**Usage:**
```html
<template>
    <yk-search-results ref="searchResultsComp" @select="onRowSelect" @double-click="onRowDoubleClick" />
</template>

<script>
import { formComponents } from './forms.js';

export default {
    components: {
        'yk-search-results': formComponents['yk-search-results'],
    },
    mounted() {
        // Example: Set search results from a parent component or API call
        // this.$refs.searchResultsComp.setSearchResults(apiResponse.searchResults);
    },
    methods: {
        onRowSelect(rowData) {
            console.log("Selected row:", rowData);
        },
        onRowDoubleClick(rowData) {
            console.log("Double-clicked row:", rowData);
        }
    }
}
</script>
```

#### `<yk-model-form>`
Generates a form dynamically based on a server-side model definition.

**Props:**
-   `modelName`: Name of the model to generate the form for.
-   `columnCount`: Number of columns for layout (default: 1).
-   `style`: Custom CSS style for the form.
-   `noAuth`: Boolean, if true, fetches model def without authentication.
-   `groups`: Array of objects to define field grouping.
-   `modelValue`: (v-model) Binds form data.

**Slots:**
-   `header`: Content to display at the top of the form.
-   `footer`: Content to display at the bottom of the form.

**Usage:**
```html
<template>
    <yk-model-form model-name="ProductModel" :column-count="2" v-model="productData">
        <template #header>
            <h3>Product Details</h3>
        </template>
        <template #footer>
            <button @click="saveProduct">Save</button>
        </template>
    </yk-model-form>
</template>

<script>
import { formComponents } from './forms.js';

export default {
    components: {
        'yk-model-form': formComponents['yk-model-form'],
    },
    data() {
        return {
            productData: {} // This will be populated by the form
        };
    },
    methods: {
        saveProduct() {
            try {
                const dataToSave = this.$refs.modelForm.evaluateForm();
                console.log("Saving:", dataToSave);
                // Call API to save data
            } catch (error) {
                console.error("Form has errors:", error);
            }
        }
    }
}
</script>
```

#### `<yk-multi-row-model-form>`
Allows managing multiple rows of data, where each row is a model form.

**Props:**
-   `modelName`: Name of the model for each row.
-   `defaultValue`: Default object to use when adding a new row.
-   `labelProp`: Property name in each row object to use as a label for confirmation dialogs.
-   `modelValue`: (v-model) Binds the array of row data.

**Usage:**
```html
<template>
    <yk-multi-row-model-form model-name="ItemModel" label-prop="itemName" v-model="items" />
</template>

<script>
import { formComponents } from './forms.js';

export default {
    components: {
        'yk-multi-row-model-form': formComponents['yk-multi-row-model-form'],
    },
    data() {
        return {
            items: [] // Array of item objects
        };
    }
}
</script>
```

### Input Field Components (`input-fields.js`)

These components are generally used within `<yk-model-form>` or `<yk-form>` and are dynamically rendered based on the model definition.

#### `<yk-input-field>`
Standard text input field.

**Props:**
-   `field`: Field definition object from the model.
-   `enableError`: Boolean to enable/disable error display.
-   `modelValue`: (v-model) Binds the field's value.

**Usage (within `<yk-model-form>`):**
```html
<!-- Rendered automatically by yk-model-form -->
<yk-model-form model-name="UserModel" v-model="userData" />
```
**Direct Usage (less common):**
```html
<template>
    <yk-input-field :field="usernameField" v-model="username" :enable-error="true" />
</template>

<script>
import { inputFieldComponents } from './input-fields.js';

export default {
    components: {
        'yk-input-field': inputFieldComponents['yk-input-field'],
    },
    data() {
        return {
            username: '',
            usernameField: { name: 'username', label: 'Username', inputType: 'text', validations: [{ name: 'required' }] }
        };
    }
}
</script>
```

#### `<yk-input-file>`
File upload input.

#### `<yk-input-image>`
Image upload input with preview.

#### `<yk-ver-input-field>`
Input field with verification (e.g., OTP for mobile/email).

#### `<yk-captcha-field>`
Input field for captcha verification.

#### `<yk-textarea-field>`
Multi-line text input (textarea).

#### `<yk-html-editor>`
Rich text editor (TinyMCE integration).

#### `<yk-switch>`
Toggle switch for boolean values.

#### `<yk-lov-field>`
Dropdown for List of Values (LOV).

#### `<yk-editable-lov-field>`
Editable LOV field, allowing users to add new options.

#### `<yk-multi-editable-lov-field>`
Multi-select editable LOV field.

### Modal Dialog Components (`modal-dialogs.js`)

#### `<yk-modal-dialog>`
A customizable modal dialog component.

**Props:**
-   `id`: Unique ID for the modal.
-   `title`: Title of the modal.
-   `submitText`: Text for the submit button.
-   `closeText`: Text for the close button.
-   `size`: Modal size (e.g., "modal-lg", "modal-xl").
-   `topMargin`: Custom top margin.

**Events:**
-   `submit`: Emitted when the submit button is clicked.

**Methods:**
-   `display(callback, config)`: Displays the modal.
-   `close()`: Closes the modal.

**Usage:**
```html
<template>
    <yk-modal-dialog id="myModal" title="My Custom Dialog" submit-text="Save" close-text="Close" @submit="onModalSubmit">
        <p>This is the content of my modal dialog.</p>
    </yk-modal-dialog>
</template>

<script>
import { dialogComponents } from './modal-dialogs.js';

export default {
    components: {
        'yk-modal-dialog': dialogComponents['yk-modal-dialog'],
    },
    methods: {
        openModal() {
            this.$refs.myModal.display();
        },
        onModalSubmit() {
            console.log("Modal submitted!");
            this.$refs.myModal.close();
        }
    }
}
</script>
```

#### `<yk-model-form-dialog>`
A modal dialog that dynamically generates a form based on a model.

**Props:**
-   `id`: Unique ID for the modal.
-   `title`: Title of the modal.
-   `method`: HTTP method for form submission.
-   `url`: API endpoint for form submission.
-   `size`: Modal size.
-   `modelName`: Name of the model for the form.
-   `columnCount`: Number of columns for the form layout.

**Events:**
-   `submit`: Emitted on form submission.

**Methods:**
-   `display(callback, extraConfig)`: Displays the modal for new entry.
-   `displayForEdit(data, callback)`: Displays the modal for editing an existing entry.
-   `close()`: Closes the modal.

**Usage:**
```html
<template>
    <yk-model-form-dialog id="productFormModal" title="Product Editor" method="POST" url="/api/products" model-name="ProductModel" @submit="onProductFormSubmit" />
</template>

<script>
import { dialogComponents } from './modal-dialogs.js';

export default {
    components: {
        'yk-model-form-dialog': dialogComponents['yk-model-form-dialog'],
    },
    methods: {
        openNewProductModal() {
            this.$refs.productFormModal.display();
        },
        editProduct(productData) {
            this.$refs.productFormModal.displayForEdit(productData);
        },
        onProductFormSubmit(success, result) {
            if (success) {
                $utils.info("Product saved!");
            } else {
                $utils.alert("Error saving product: " + result.response.message);
            }
        }
    }
}
</script>
```

#### `<yk-dialogs>`
A global component that provides utility dialogs (alert, confirm, input, info, in-progress). This component should be placed once in the root Vue application.

**Usage (typically in `index.html` or root Vue component):**
```html
<template>
    <div id="app">
        <!-- Your main application content -->
        <yk-dialogs ref="ykDialogs" />
    </div>
</template>

<script>
import { dialogComponents } from './modal-dialogs.js';
import { newVueApp } from './webutils-app.js';

const app = newVueApp({
    components: {
        'yk-dialogs': dialogComponents['yk-dialogs'],
    },
    mounted() {
        // $utils.ykDialogs is automatically set by newVueApp
        $utils.alert("Welcome to the app!");
    }
});
app.mount('#app');
</script>
```

### Navigation Bar Components (`nav-bar.js`)

#### `<yk-route-nav-bar>`
A top navigation bar component that handles routing based on hash changes.

**Props:**
-   `id`: Unique ID for the nav bar.
-   `logoIcon`: URL for the logo image.
-   `defaultItem`: ID of the default active item.
-   `items`: Array of navigation items (each with `id`, `label`, `route`, `componentName`, `script`, `uri`).
-   `disableUserInfo`: Boolean to hide user info.
-   `subnav`: Boolean, if true, renders as a sub-navigation bar.

**Events:**
-   `navChanged`: Emitted when the active navigation item changes.

**Usage:**
```html
<template>
    <yk-route-nav-bar id="mainNav" logo-icon="/images/logo.png" default-item="home" :items="mainMenuItems" @nav-changed="onNavChange" />
</template>

<script>
import { navBarComponents } from './nav-bar.js';

export default {
    components: {
        'yk-route-nav-bar': navBarComponents['yk-route-nav-bar'],
    },
    data() {
        return {
            mainMenuItems: [
                { id: 'home', label: 'Home', route: 'home', componentName: 'home-page', script: './pages/home.js', uri: './pages/home.html' },
                { id: 'products', label: 'Products', route: 'products', componentName: 'product-list', script: './pages/products.js', uri: './pages/products.html' },
            ]
        };
    },
    methods: {
        onNavChange(activeItem) {
            console.log("Navigation changed to:", activeItem.id);
            // Update main content area based on activeItem.componentDef
        }
    }
}
</script>
```

#### `<yk-route-side-bar>`
A side navigation bar component, similar to `yk-route-nav-bar` but for vertical navigation.

**Props:**
-   `id`: Unique ID.
-   `defaultItem`: Default active item.
-   `items`: Array of navigation items (can include `icon`, `iconColor`).

**Usage:**
```html
<template>
    <yk-route-side-bar id="sideNav" default-item="dashboard" :items="sideMenuItems" @nav-changed="onSideNavChange" />
</template>

<script>
import { navBarComponents } from './nav-bar.js';

export default {
    components: {
        'yk-route-side-bar': navBarComponents['yk-route-side-bar'],
    },
    data() {
        return {
            sideMenuItems: [
                { id: 'dashboard', label: 'Dashboard', route: 'dashboard', icon: 'bi bi-speedometer', iconColor: 'blue', componentName: 'dashboard-page', script: './pages/dashboard.js', uri: './pages/dashboard.html' },
                { id: 'settings', label: 'Settings', route: 'settings', icon: 'bi bi-gear', iconColor: 'gray', componentName: 'settings-page', script: './pages/settings.js', uri: './pages/settings.html' },
            ]
        };
    },
    methods: {
        onSideNavChange(activeItem) {
            console.log("Side navigation changed to:", activeItem.id);
        }
    }
}
</script>
```

