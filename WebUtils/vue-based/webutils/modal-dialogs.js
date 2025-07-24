import {$restService} from "./rest-service.js";
import {$utils, $appConfiguration} from "./common.js";

export var dialogComponents = {};

/**
 * @typedef {object} ModalConfig
 * @property {function} [onShow] - Optional callback function to be executed when the modal is shown.
 * @property {function} [onHide] - Optional callback function to be executed when the modal is hidden.
 * @property {object} [context] - Optional context to be used for the onShow and onHide callbacks.
 */

/**
 * This is an internal object (not expected to be used outside of framework) and this helps in managing display multiple modal dialogs 
 * one over the other and to safely close them in order.
 * @type {object}
 */
var $modalManager = {
	/**
	 * Maintains list of modal dialog ids which are currently open.
	 * @type {Array<String>}
	 */
	"modalStack": [],
		
	/**
	 * Opens a modal dialog with the specified id.
	 * @param {String} id - The id of the modal dialog to open.
	 * @param {ModalConfig} [config] - Optional configuration for the modal dialog.
	 */
	"openModal": function(id, config) {
		var dlg = $("#" + id);
		
		if(dlg.length < 0)
		{
			throw "No modal found with specified id - " + id;
		}
		
		// Add code what needs to happen when modal dialog is DISPLAYED
		//   before that remove previous listeners if any (with off method)
		$('#' + id).off('shown.bs.modal').on('shown.bs.modal', $.proxy(function (e) {
			this.modalStack.push(this.id);
			
			var zIndex = 1040 + (10 * $('.modal:visible').length);
			$("#" + this.id).css('z-index', zIndex);
			
			if(this.config && this.config.onShow)
			{
				var onShow = this.config.onShow;
				
				if(this.config.context)
				{
					onShow = $.proxy(onShow, this.config.context);
				}
				
				onShow();
			}

			//setTimeout because the .modal-backdrop isn't created when the 
			//  event show.bs.modal is triggered
			setTimeout(function() {
		        $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
		    }, 2);
			
		}, {"modalStack": this.modalStack, "config": config, "id": id}));

		// Add code what needs to happen when modal dialog is CLOSED/HIDDEN
		//   before that remove previous listeners if any (with off method)
		$('#' + id).off('hidden.bs.modal').on('hidden.bs.modal', $.proxy(function (e) {
			var idx = this.modalStack.indexOf(this.id);
			
			if(idx < 0)
			{
				return;
			}
			
			this.modalStack.splice(idx, 1);
			
			/**
			 * Based on whether more dialogs are open apart from current dialog
			 * set the approp CSS classes (to overcome bootstrap issues of handling multiple dialogs) 
			 */
			if(this.modalStack.length > 0)
			{
				$('body').addClass('modal-open');
			}
			else
			{
				$('body').removeClass('modal-open');
				$('body').css('padding', "0px");
			}
			
			if(this.config && this.config.onHide)
			{
				var onHide = this.config.onHide;
				
				if(this.config.context)
				{
					onHide = $.proxy(onHide, this.config.context);
				}
				
				onHide();
			}
		}, {"modalStack": this.modalStack, "config": config, "id": id}));
		
		$('#' + id).modal('show');
	},
	
	/**
	 * Closes a modal dialog with the specified id.
	 * @param {String} id - The id of the modal dialog to close.
	 */
	"closeModal": function(id)
	{
		$('#' + id).modal('hide');
	}
};

/**
 * A customizable modal dialog component that can be used to display custom content, forms, or information to the user. 
 * It provides a structured layout with a header, body, and footer, and can be controlled programmatically.
 * @vue-component
 */
dialogComponents['yk-modal-dialog'] = {
	"props": {
		/**
		 * A unique identifier for the modal dialog. This ID is used to open and close the modal programmatically.
		 * @type {String}
		 * @required
		 */
		"id": String,
		
		/**
		 * The text to display in the modal's header.
		 * @type {String}
		 */
		"title": String,
		
		/**
		 * The text for the primary (submit) button in the footer. If not provided, the button will not be displayed.
		 * @type {String}
		 */
		"submitText": String,
		
		/**
		 * The text for the secondary (close/cancel) button in the footer.
		 * @type {String}
		 */
		"closeText": String,
		
		/**
		 * The size of the modal dialog. Accepts standard Bootstrap modal size classes like "modal-lg", "modal-sm", etc.
		 * @type {String}
		 * @default 'modal-xl'
		 */
		"size": {
			"type": String,
			"default": "modal-xl"
		},
		
		/**
		 * A custom CSS top margin for the dialog (e.g., "5rem").
		 * @type {String}
		 */
		"topMargin": String 
	},
	
	"data": function() {
		return {
			"dataTitle": null
		}
	},
	
	"methods":
	{
		/**
		 * Displays the modal.
		 * @param {Function} [callback] - A function to be executed when the modal is hidden.
		 * @param {object} [config] - A configuration object.
		 * @param {String} [config.title] - If specified, this title will be used as dynamic title for the dialog.
		 */
		"display": function(callback, config)
		{
			if(config && config.title)
			{
				this.dataTitle = config.title;
			}
			
			$modalManager.openModal(this.id, {
				context: {"callback": callback, "id": this.id},
				
				//on show, highlight okay button
				onShow: function() 
				{
					var firstFld = $('#' + this.id).find('input[type=text],textarea,select').filter(':visible:first');
					$(firstFld).focus();
				},
				
				//on hide, call the callback if specified
				onHide: function() {
					if(this.callback)
					{
						this.callback();
					}
				}
			});
		},
		
		/**
		 * Closes (hides) the modal dialog.
		 */
		"close": function() 
		{
			$('#' + this.id).modal('hide');
		}
	},
};

/**
 * A modal dialog that contains a dynamically generated form based on a server-side model definition.
 * It is designed for both creating new records and updating existing ones, complete with built-in form submission and handling.
 * @vue-component
 */
dialogComponents['yk-model-form-dialog'] = {
	"props": {
		/**
		 * A unique identifier for the modal dialog.
		 * @type {String}
		 * @required
		 */
		"id": String,
		
		/**
		 * The text to display in the modal's header.
		 * @type {String}
		 * @required
		 */
		"title": String,
		
		/**
		 * The HTTP method (e.g., "POST", "PUT") to use for form submission.
		 * @type {String}
		 * @required
		 */
		"method": { "type": String, "required": true },
		
		/**
		 * The API endpoint URL where the form data will be submitted.
		 * @type {String}
		 * @required
		 */
		"url": { "type": String, "required": true },

		/**
		 * The size of the modal dialog (e.g., "modal-lg").
		 * @type {String}
		 * @default 'modal-xl'
		 */
		"size": {
			"type": String,
			"default": "modal-xl"
		},
		
		/**
		 * The name of the model to be used for generating the form fields.
		 * @type {String}
		 * @required
		 */
		"modelName": { "type": String, "required": true },
		
		/**
		 * The number of columns to use for the form layout.
		 * @type {Number}
		 * @default 2
		 */
		"columnCount": { "type": Number, "default": 2 }
	},
	
	"data": function() {
		return {
			/**
			 * Holds the structured field definitions for rendering in rows and columns.
			 * @type {Array<object>}
			 */
			"modelFieldRows": [
			],
			
			/**
			 * CSS class for Bootstrap grid columns, calculated from `columnCount`.
			 * @type {String}
			 */
			"columnClass": "col-md-6",
			
			/**
			 * The main data object for the form.
			 * @type {object}
			 * @property {object} data - The actual model data object being edited.
			 * @property {Array<String>} errorFields - An array of field names that currently have validation errors.
			 * @property {boolean} displayErrors - A flag to control the visibility of validation errors.
			 */
			"formData": {
				"data": {},
				"errorFields": [],
				"displayErrors": false
			},
			
			/**
			 * The text label for the submit button (e.g., 'Save', 'Update').
			 * @type {String}
			 */
			"saveLabel": "Save",
			
			/**
			 * A flag to prevent duplicate form submissions.
			 * @type {boolean}
			 */
			"formSubmitted": false,
			
			/**
			 * A map to manage listeners for fields that have dependents (e.g., parent LOVs).
			 * @type {object}
			 */
			"fieldChangeListeners": {}
		}
	},

	"created": function() {
		$restService.fetchModelDef(this.modelName, $.proxy(this.setFormData, this), false);
		
		var colSize = 12 / this.columnCount;
		this.columnClass = "col-md-" + colSize;
	},
	
	"updated": function() {
		//for cross dependent fields, populate watchers
		for(var row of this.modelFieldRows)
		{
			for(var fld of row.fields)
			{
				if(!fld.lovDetails && !!fld.lovDetails.parentField)
				{
					continue;
				}
				
				var refFld = this.$refs["field_" + fld.index][0];
				
				var parentDetails = refFld.getParentDetails();
				
				if(!parentDetails)
				{
					continue;
				}
	
				if(!this.fieldChangeListeners[parentDetails.name])
				{
					this.fieldChangeListeners[parentDetails.name] = [];
				}
				
				this.fieldChangeListeners[parentDetails.name].push(parentDetails.callback);
			}
		}
	},
	
	"methods":
	{
		/**
		 * Internal method to handle value changes, primarily for triggering updates in dependent fields.
		 * @param {*} newVal - The new value of the field.
		 * @param {object} fieldInfo - The field's information object.
		 */
		"onFieldValueChange": function(newVal, fieldInfo)
		{
			console.log("Field value change: ", fieldInfo, newVal);

			if(!this.fieldChangeListeners[fieldInfo.name])
			{
				return;
			}
			
			var listeners = this.fieldChangeListeners[fieldInfo.name];
			
			for(var i = 0; i < listeners.length; i++)
			{
				listeners[i](newVal);
			}
		},
		
		/**
		 * Displays the modal for creating a new entry.
		 * @param {Function} [callback] - A function to be executed after the dialog is closed.
		 * @param {object} [extraConfig] - An object for additional configuration.
		 * @param {String} [extraConfig.saveLabel] - Custom label for the save button.
		 */
		"display": function(callback, extraConfig)
		{
			this.saveLabel = (extraConfig && extraConfig.saveLabel)? extraConfig.saveLabel : "Save";
			this.formSubmitted = false;
			
			$modalManager.openModal(this.id, {
				context: {"callback": callback, "id": this.id},
				
				//on show, highlight okay button
				onShow: function() 
				{
					var firstFld = $('#' + this.id).find('input[type=text],textarea,select').filter(':visible:first');
					$(firstFld).focus();
				},
				
				//on hide, call the callback if specified
				onHide: function() {
					if(this.callback)
					{
						this.callback();
					}
				}
			});
		},
		
		/**
		 * Displays the modal for editing an existing entry, pre-filling the form with the provided data.
		 * @param {object} data - The object containing the data to edit.
		 * @param {Function} [callback] - A function to be executed after the dialog is closed.
		 */
		"displayForEdit": function(data, callback)
		{
			this.formSubmitted = false;
			
			this.reset(data);
			this.display(callback);
			this.saveLabel = "Update";
		},
		
		/**
		 * Closes (hides) the modal dialog.
		 */
		"close": function() 
		{
			$('#' + this.id).modal('hide');
		},

		/**
		 * Internal method to process the model definition from the server and arrange fields into rows.
		 * @param {object} modelDef - The model definition object.
		 */
		"setFormData": function(modelDef) {
			$utils.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
		},
		
		/**
		 * Validates the form and submits the data to the configured URL and method.
		 * Emits a 'submit' event with the result.
		 */
		"submitForm": function()
		{
			//this flag will ensure the api is invoked only once
			if(this.formSubmitted)
			{
				return;
			}
			
			console.log("Submit form is called..");
			if(this.formData.errorFields.length > 0)
			{
				$(this.$el).find("[name=" + this.formData.errorFields[0] + "]").focus();
				this.formData.displayErrors = true;
				this.$emit('submit', false);
				
				this.formSubmitted = false;
				return;
			}
			
			if(this.method == "POST")
			{
				$restService.invokePost(
						this.url, 
						this.formData.data,
						{
							"context": this, 
							"onSuccess": this.submitSuccess, 
							"onError": this.submitFailed
						}
					);
			}
		},
	
		/**
		 * Internal success callback for the form submission API call.
		 * @param {object} result - The success result from the server.
		 */
		"submitSuccess": function(result) {
			this.formSubmitted = false;
			this.$emit('submit', true, result);
			$modalManager.closeModal(this.id);
		},
		
		/**
		 * Internal error callback for the form submission API call.
		 * @param {object} result - The error result from the server.
		 */
		"submitFailed": function(result) {
			this.formSubmitted = false;
			
			if(!result.response)
			{
				result.response = {"message": "Failed to contact server."};
			}
			
			this.$emit('submit', false, result);
		},
		
		/**
		 * Resets the form with new data, clearing all existing values and validation errors.
		 * @param {object} [newData] - The new data to populate the form with. If not provided, the form will be cleared.
		 */
		"reset": function(newData) {
			newData = newData ? newData : {};
			this.formData.data = newData;
			
			this.formData.errorFields.splice(0, this.formData.errorFields.length);
			this.formData.displayErrors = false;
			
			for(var row of this.modelFieldRows)
			{
				for(var fld of row.fields)
				{
					this.$refs["field_" + fld.index][0].reset(newData[fld.name]);
				}
			}
		},
	},
	
	template: `
		<div class="modal fade" :id="id" tabindex="-1">
			<div :class="'modal-dialog ' + size">
				<div class="modal-content">
					<div class="modal-header modal-title">
						{{title}}
					</div>
					
					<div class="modal-body">
						<div :key="row.index" class="row" v-for="row in modelFieldRows">
				 			<component
				 				:ref="'field_' + field.index"
				 				:key="field.index"
				 				:is="field.componentType"
				 				:formData.sync="formData"
				 				:field="field"
								@input="onFieldValueChange"
				 				
				 				v-for="field in row.fields"
				 				/>
						</div>
					</div>
					
					<div class="modal-footer" style="padding: 0.1rem">
						<button type="button" class="btn btn-primary webutil-button" @click="submitForm">{{saveLabel}}</button>
						<button type="button" class="btn btn-danger webutil-button" data-bs-dismiss="modal">Cancel</button>
					</div>
				</div>
			</div>
		</div>
	`
};

/**
 * A global component that provides standard, application-wide dialogs such as alert, confirm, input, and info messages.
 * This component is essential for the `$utils` helper functions (`$utils.alert`, `$utils.confirm`, etc.) to work correctly.
 * It should be included once in the main application template (e.g., index.html) to make these dialogs available globally.
 * The methods on this component are not intended to be called directly, but are instead invoked by the wrapper functions in `$utils`.
 * @vue-component
 */
dialogComponents['yk-dialogs'] = {
	data: function() {
		return  {
			/**
			 * The message content for the alert dialog.
			 * @type {String}
			 */
			"alertMessage": "",
			
			/**
			 * The message/label for the input dialog.
			 * @type {String}
			 */
			"inputMessage": "",
			
			/**
			 * The value bound to the input field in the input dialog.
			 * @type {*}
			 */
			"inputValue": null,
			
			/**
			 * The callback function to be executed when the input dialog is closed.
			 * @type {Function}
			 */
			"inputCallback": null,
			
			/**
			 * The message content for the confirmation dialog.
			 * @type {String}
			 */
			"confirmMessage": "",
			
			/**
			 * Stores the result (true for 'Yes', false for 'No') of the confirmation dialog.
			 * @type {boolean}
			 */
			"confirmResult": false,
			
			/**
			 * The callback function to be executed when the confirmation dialog is closed.
			 * @type {Function}
			 */
			"confirmCallback": null,
			
			/**
			 * A queue of functions to be executed while the 'in-progress' dialog is shown.
			 * @type {Array<Function>}
			 */
			"inProgressFunctions": [],
			
			/**
			 * A flag indicating if the 'in-progress' dialog is currently visible.
			 * @type {boolean}
			 */
			"inProgressDisplayed": false, // Flag indicating if in-progress dialog is displayed
		};
	},
	
	"methods": {
		/**
		 * Internal helper to process and format a message string, handling arrays and newlines.
		 * @param {String|Array} message - The message to process.
		 * @returns {String} The formatted HTML string.
		 * @private
		 */
		"processMessage": function(message) {
			message = $.isArray(message) ? $utils.format(message[0], message, 1) : message;
			message = message.replace(/\n/g, "<br/>");

			return message;			
		},
		
		/**
		 * Displays a modal alert dialog. Called via `$utils.alert()`.
		 * @param {String|Array} message - The message to display.
		 * @param {Function} [callback] - A function to execute after the dialog is closed.
		 * @private
		 */
		"displayAlert": function(message, callback) {
			this.alertMessage = this.processMessage(message);
			
			$modalManager.openModal("webutilsAlertDialog", {
				context: {"callback": callback},
				
				//on show, highlight okay button
				onShow: function() {
					$('#webutilsAlertDialog .webutils-btn-primary').focus();
				},
				
				//on hide, call the callback if specified
				onHide: function() {
					if(this.callback)
					{
						this.callback();
					}
				}
			});
		},
		
		/**
		 * Closes the alert dialog.
		 * @private
		 */
		"closeAlert": function() {
			$('#webutilsAlertDialog').modal('hide');
		},
		
		/**
		 * Displays a modal dialog to get input from the user. Called via `$utils.input()`.
		 * @param {String|Array} message - The message/label to display.
		 * @param {*} initialValue - The initial value for the input field.
		 * @param {Function} callback - The function to call with the user's input.
		 * @private
		 */
		"displayInput": function(message, initialValue, callback) {
			this.inputMessage = this.processMessage(message);
			this.inputValue = initialValue;
			this.inputCallback = callback;
			
			$modalManager.openModal("webutilsInputDialog", {
				context: {"callback": callback, "$this": this},
				
				//on show, highlight okay button
				onShow: function() {
					$('#webutilsInputDialog input').focus();
				},
				
				//on hide, call the callback if specified
				onHide: function() {
					if(this.callback)
					{
						this.$this.inputCallback(this.$this.inputValue);
					}
				}
			});
			
		},

		/**
		 * Closes the input dialog, passing the result to the callback.
		 * @param {boolean} res - Indicates if the dialog was confirmed (true) or canceled (false).
		 * @private
		 */
		"closeInput": function(res) {
			if(!res) {
				this.inputValue = null;
			}
			
			$('#webutilsInputDialog').modal('hide');
		},
		
		/**
		 * Displays a non-modal info message that disappears automatically. Called via `$utils.info()`.
		 * @param {String|Array} message - The message to display.
		 * @private
		 */
		"displayInfo": function(message) {
			message = this.processMessage(message);
			
			//set content and display			
			$("#webutilsInfoBox .content").html(message);
			$("#webutilsInfoBox").css("display", "block");
			
			//get timeout period
			var time = $appConfiguration.infoTimeOutSec ? $appConfiguration.infoTimeOutSec : 5;
			
			//auto timer after which info should auto close
			setTimeout(function() {
				$("#webutilsInfoBox").css("display", "none");
			}, (time * 1000));
		},
		
		/**
		 * Closes the info message box.
		 * @private
		 */
		"closeInfo": function() {
			$('#webutilsInfoBox').css("display", "none");
		},
		
		/**
		 * Displays a modal confirmation dialog. Called via `$utils.confirm()`.
		 * @param {String|Array} message - The confirmation message.
		 * @param {Function} callback - The function to call with the result (true/false).
		 * @private
		 */
		"displayConfirm": function(message, callback) {
			this.confirmMessage = this.processMessage(message);
			this.confirmCallback = callback;
			this.confirmResult = false;
			
			$modalManager.openModal("webutilsConfirmDialog", {
				context: {"callback": callback, "$this": this},
				
				//on show, highlight okay button
				onShow: function() {
					$('#webutilsConfirmDialog .btn-primary').focus();
				},
				
				//on hide, call the callback if specified
				onHide: function() {
					if(this.callback)
					{
						this.$this.confirmCallback(this.$this.confirmResult);
					}
				}
			});
		},
		
		/**
		 * Closes the confirmation dialog, passing the result to the callback.
		 * @param {boolean} res - The user's choice (true for 'Yes', false for 'No').
		 * @private
		 */
		"closeConfirm": function(res)
		{
			this.confirmResult = res;
			$('#webutilsConfirmDialog').modal('hide');
		},
		
		/**
		 * Executes a function while displaying a global 'in-progress' dialog. Called via `$utils.executeWithInProgress()`.
		 * @param {Function} func - The function to execute.
		 * @private
		 */
		"executeWithInProgress": function(func) {
			this.inProgressFunctions.push(func);
			
			let wrapperFunc = $.proxy(function() {
				this.func();
				
				let idx = this.$this.inProgressFunctions.indexOf(func);
				
				if(idx >= 0) {
					this.$this.inProgressFunctions.splice(idx, 1);
				}
				
				if(this.$this.inProgressFunctions.length == 0) {
					$('#webutilsInProgressDialog').modal('hide');
				}
			}, {"func": func, "$this": this});
			
			if(this.inProgressDisplayed) {
				wrapperFunc();
				return;
			}
			
			this.inProgressDisplayed = true;
			
			$modalManager.openModal("webutilsInProgressDialog", {
				context: {"func": wrapperFunc, "$this": this},
				
				onShow: function() {
					this.func();
				},

				onHide: function() {
					this.$this.inProgressDisplayed = false;
				}
			});
		}
	},

	
	template: `
		<div>
			<div class="modal fade" id="webutilsAlertDialog" tabindex="-1" role="dialog" data-backdrop="static">
				<div class="modal-dialog webutils-modal-dialog" role="document">
					<div class="modal-content">
						<!-- Content -->
						<div class="modal-body" v-html="alertMessage">
						</div>
						
						<!-- Footer -->
						<div class="modal-footer" style="padding: 5px;">
							<button type="button" class="webutils-btn-primary" @click="closeAlert">Okay</button>
						</div>
					</div>
				</div>
			</div>

			<div class="modal fade" id="webutilsInputDialog" tabindex="-1" role="dialog" data-backdrop="static">
				<div class="modal-dialog webutils-modal-dialog" role="document">
					<div class="modal-content">
						<!-- Content -->
						<div class="form-group">
						    <label class="webutil-field-label" v-html="inputMessage"></label>
						    <input type="text" class="form-control" v-model="inputValue">
						</div>

						<!-- Footer -->
						<div class="modal-footer" style="padding: 5px;">
							<button type="button" class="webutils-btn-primary" @click="closeInput(true)">Okay</button>
							<button type="button" class="webutils-btn-cancel" @click="closeInput(false)">Cancel</button>
						</div>
					</div>
				</div>
			</div>

			<div id="webutilsInfoBox" class="webutils-info-message" style="display: none">
				<div class="content" style="width: 100%;">
				</div>
				<button type="button" class="webutils-info-close-button" aria-label="Close" @click="closeInfo">
					<span>&times;</span>
				</button>
			</div>

			<div class="modal fade" id="webutilsConfirmDialog" tabindex="-1" role="dialog" data-backdrop="static">
				<div class="modal-dialog" role="document" style="max-width: 50em;">
					<div class="modal-content">
						<!-- Content -->
						<div class="modal-body" style="padding: 1.5em;" v-html="confirmMessage">
						</div>
						
						<!-- Footer -->
						<div class="modal-footer" style="padding: 0.5em;">
							<button type="button" class="btn btn-primary action-button" @click="closeConfirm(true)">Yes</button>
							<button type="button" class="btn btn-danger cancel-button" @click="closeConfirm(false)">No</button>
						</div>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="webutilsInProgressDialog" tabindex="-1" role="dialog" data-backdrop="static">
				<div class="modal-dialog" role="document" style="max-width: 50em; width: 50em;">
					<div class="modal-content">
						<!-- Content -->
						<div class="modal-body" style="padding: 1.5em;">
							<div class="webutils-progress-container">
								<div class="webutils-progress-bar"></div>
							</div>
						</div>
						
						<div style="margin-top: 0.5rem; width: 100%; text-align: center; font-weight: bold;font-size: 0.8rem;">
							Page is loading, please wait...
						</div>
					</div>
				</div>
			</div>
			
		</div>
	`
};
