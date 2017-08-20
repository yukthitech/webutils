## WebUtils

**WebUtils** is a multi tiered framework spanning from ui to model layer. The framework is divided into multi layers, so that each layer can be used independently. 

**UI layer** is created based on angular-js, jquery, bootstrap css.
**Controller layer** is based on Spring MVC, Spring Validation frameworks.
**Model layer** is based on [Yukthi Data Framework] (https://github.com/yukthitech/utils/tree/master/yukthi-data).

The goal of this framework is to provide most common functionalities as part of framework and avoid commonly faced problems:

1. Models along with validations defined in controller layer will automatically generate the ui form along with client side validations. So that code need not be duplicated on the ui side along with controller side.

2. Standard way of authentication and authorization.

3. Standard way of creating and managing extended fields (where customers/users can defined custom fields).

4. File Management.

5. Sending Notifications (currently only emails are supported).

6. Standard way of search ui. Ui directives are added for displaying search query and results. And processing is also part of framework. For adding search screen a search query bean and search result are the only things needs to be defined by developer.

