import {$utils, $mergeObjProperty, $appConfiguration} from "./common.js";
import {$validationService} from "./validation.js";
import {$restService} from "./rest-service.js";

export const inputFieldComponents = {};

export function newVueUiComponent(name, vueData){
	var defData = {
		"data": {
			"fieldInfo": {},
			"fieldValue": "",
			"mountCompleted": false
		},
		
		"props": {
			"field": {"type": Object, required: true},

			/**
			 * Enable error display for the field. Generally set to true when form is submitted.
			 */
			"enableError": {"type": Boolean, default: false},

			/**
			 * Server error message for the field.
			 */
			"serverError": {"type": [String, Array, Object], default: null},
			
			/**
			 * Can be used to set initial value for the field.
			 * This also helps in 2-way binding with parent fields using v-model
			 */
			"modelValue": {},

			/**
			 * Hide the label for the field.
			 */
			"hideLabel": {"type": Boolean, default: false},
		},

		/**
		 * IMP NOTE: Sub field changes of  fieldValue or modelValue will not trigger
		 * below watch methods. Those needs to be watched separately. 
		 */
		"watch": {
			"fieldValue": function(newVal, oldVal)
			{
				//console.log("On field value change: ", newVal);
				this.onFieldValueChange(newVal);
			},
			// Model value is meant for 2-way binding
			"modelValue": function(newVal, oldVal)
			{
				//console.log("On model value change: ", newVal);
				this.fieldValue = newVal;

				if(this.onModelValueChanged)
				{
					this.onModelValueChanged(newVal);
				}
			},

			"serverError": function(newVal) {
				// Use Vue.set or direct assignment to ensure reactivity
				this.fieldInfo.error = newVal;
				
				if(this.onServerError) {
					this.onServerError(newVal);
				}
			}
		},
		
		"setup": function() {
			if(this && this.onSetup)
			{
				this.onSetup();
			}
		},

		"created": function() {
			this.fieldValue = this.modelValue;

			this.buildFieldInfo();
			
			if(this.onCreate)
			{
				this.onCreate();
			}
			
			// Call onModelValueChanged if it exists to handle initial value
			if(this.onModelValueChanged)
			{
				this.onModelValueChanged(this.modelValue);
			}
		},
		
		"mounted": function() 
		{
			this.mountCompleted = true;
			
			if(this.onMounted)
			{
				this.onMounted();
			}
		},

		"methods": {
			"onFieldValueChange": function(newVal) {
				this.validateAndSetValue(newVal);
				
				if(this.mountCompleted)
				{
					this.$emit('update:modelValue', newVal);
				}

				if(this.onValueChange)
				{
					this.onValueChange(newVal);
				}
			},
			
			"reset": function(val) {
				this.fieldValue = val ? val : "";
			},
			
			"buildFieldInfo": function() {
				for(var fld in this.field)
				{
					this.fieldInfo[fld] = this.field[fld];
				}
				
				if(!this.fieldInfo.validations)
				{
					this.fieldInfo.validations = [];
				}
				
				// Initialize error property to ensure reactivity
				if(this.fieldInfo.error === undefined) {
					this.fieldInfo.error = null;
				}
				
				this.validateAndSetValue(this.fieldValue);
			},
			
			"getError": function() {
				return this.fieldInfo.error;
			},
			
			"displayError": function() {
				return this.fieldInfo.error && this.enableError;
			},

			"validate": function() {
				this.fieldInfo.error = null;
				
				try
				{
					$validationService.validate(
							this.fieldInfo.dataType,
							this.fieldInfo.validations, 
							this.fieldValue, this.data);
					
					/*
					 * If no errors are presnt, check if component has onValidateValue() method defined and invoke it.
					 * this method is suppose to validate value and throw error in case of validation failure, which
					 * would be handled by catch block.
					 */					
					if(!this.fieldInfo.error && this.onValidateValue) {
						this.onValidateValue();
					}
				}catch(err)
				{
					if(err.message)
					{
						this.fieldInfo.error = err.message;
					}
					else
					{
						this.fieldInfo.error = ""  + err;
					}
				}

				return !this.fieldInfo.error; // true if no error, false otherwise
			},

			
			"validateAndSetValue": function(newVal) {
				var curErrStatus = this.displayError();
				
				this.validate();
				//this.$forceUpdate();
				
				//input event helps in 2-way binding of current field with parent model field
				//  fieldInfo is passed extra, in case explicit this data is required
				this.$emit('valueChanged', newVal, this.fieldInfo);
				
				var newErrStatus = this.displayError();
				
				if(curErrStatus != newErrStatus && this.onErrorStatus)
				{
					this.onErrorStatus(newErrStatus);
				}
			},
			
			"clearError": function(curValue) {
				// clear the error
				this.fieldInfo.error = "";

				// inform parent about status change
				this.$emit('valueChanged', curValue, this.fieldInfo);

				// invoke field based error status method
				if(this.onErrorStatus)
				{
					this.onErrorStatus(newErrStatus);
				}
			},
		}
	};
	
	var newDefData = {};
	let mergeProps = ['data', 'props', 'methods', "watch"];
	
	newDefData.props = $utils.deepClone(defData.props);
	newDefData.data =  $utils.deepClone(defData.data);
	
	let copyBeanProp = function(dest, src) {
		// merge standard props
		for(let prop of mergeProps) {
			$mergeObjProperty(dest, src, prop);
		}

		// copy other props from parent object
		for(let prop in src) {
			if(mergeProps.indexOf(prop) >= 0) {
				continue;
			}
			
			if(!dest[prop]) {
				dest[prop] = src[prop];
			}
		}
	};
	
	// if the parent object is specified
	if(vueData.extends) {
		let parentObj = vueData.extends;
		delete vueData.extends;
		
		copyBeanProp(vueData, parentObj);
	}
	
	copyBeanProp(vueData, defData);

	/*
	vueData.mounted = defData.mounted;
	vueData.watch = defData.watch;
	vueData.created = defData.created;
	vueData.setup = defData.setup;
	*/
	
	let finalData = vueData.data;
	vueData.data = $.proxy(function() {
		return $utils.deepClone(this);
	}, finalData);
	
	inputFieldComponents[name] = vueData;
};

/**
 * Used to render model field which gets all the required info
 * from model-form which in turn is expected to be obtained from server.
 */
inputFieldComponents['yk-model-field'] = {
	"props": {
		"modelDef": { "type": Object, "required": false },
		"fieldName": { "type": String, "required": false },
		
		"formData": { "type": Object, "required": false },
		
		"columnCount": { "type": Number, "default": 12 },
		/**
		 * Enable error display for the field. Generally set to true when form is submitted.
		 */
		"enableError": {"type": Boolean, default: false},

		/**
		 * Server error message for the field.
		 */
		"serverError": {"type": [String, Array, Object], default: null},
		
		/**
		 * Can be used to set initial value for the field.
		 * This also helps in 2-way binding with parent fields using v-model
		 */
		"modelValue": {},

		/**
		 * Hide the label for the field.
		 */
		"hideLabel": {"type": Boolean, default: false},
	},
	
	"computed": {
		"fieldValue": {
			get() {
				return this.modelValue;
			},
			set(value) {
				this.$emit("update:modelValue", value);
			}
		}
	},

	"data": function() {
		return {
			"field": null,
			"columnClass": "col-md-12",
		}
	},

	"created": function() {
		if(!this.modelDef.fieldIndex) {
			throw "Specified model-def is not indexed (use $modelDefService.populateFieldDetails()): " + this.fieldName;
		}
		
		this.field = this.modelDef.fieldIndex[this.fieldName];
		
		if(!this.field) {
			throw "No field found with name: " + this.fieldName;
		}
		
		this.columnClass = "col-md-" + this.columnCount;
	},

	"methods": {
		"getError": function() {
			return this.$refs["field"].getError();
		},

		"validate": function() {
			return this.$refs["field"].validate();
		}
	},
	
	template: `
		<div :class="columnClass">
			<component
				ref="field"
				:key="field.index"
				:is="field.componentType"
				:formData.sync="formData"
				:field="field"
				:empty-option="'Select ' + field.label"
				v-model="fieldValue"
				:enable-error="enableError"
				:server-error="serverError"
				:hide-label="hideLabel"
				/>
		</div>
	`
};



/**
 * Custom-node to add input field.
 * Parameters:
 * 		* field
 * 			* name: 
 * 				Name of the field. And also the name of field to be populated in 'data'.
 * 			* label:
 * 				Label to be used for the field.
 * 			# size:
 * 				Number of columns to be used for this field. Defaults to 12.
 * 			# placeHolder:
 * 				Place holder for the field. Defaults to label.
 * 			# validations:
 * 				list of validations to be done on this field. Each should be object of format
 * 					{"name": "minValue", config: {"value": 10}}
 * 		* data
 * 			field data	
 * 			 
 */
newVueUiComponent('yk-input-field', {
	"data": {
		// below field is used by passwor field, to swap visibility
		"showPassword": false
	},
	
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<input
					:name="fieldInfo.name"
					:id="fieldInfo.name"
					:type="showPassword ? 'text' : fieldInfo.inputType" 
					class="form-control yk-input-field" 
					:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
					v-model="fieldValue"
					:class="{'is-invalid': displayError()}"
					/>
				<div class="input-group-append" v-if="fieldInfo.inputType == 'password'">
				    <span class="input-group-text" @click="showPassword = !showPassword">
				        <span :class="showPassword ? 'bi bi-eye' : 'bi bi-eye-slash'"></span>
				    </span>
				</div>
				<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
			</div>
		</div>
	`
});

newVueUiComponent('yk-input-file', {
	"data": {
		"fromServer": false,
		"fileName": "",
		"downloadUrl": ""
	},

	"watch": {
		"fieldInfo.groupName": function() {
			this.updateDownloadUrl();
		}
	},

	"methods": {
		"extractDisplayFileName": function(fullFileName) {
			if(!fullFileName || typeof(fullFileName) !== "string")
			{
				return "";
			}

			let idx = fullFileName.indexOf("#");
			return idx >= 0 ? fullFileName.substring(idx + 1) : fullFileName;
		},

		"updateDownloadUrl": function() {
			if(!this.fromServer || !this.fieldValue || !this.fieldInfo || !this.fieldInfo.groupName)
			{
				this.downloadUrl = "";
				return;
			}

			let groupName = encodeURIComponent(this.fieldInfo.groupName);
			let fileName = encodeURIComponent(this.fieldValue);
			this.downloadUrl = "/api/file/" + groupName + "/" + fileName;
		},

		"onBrowseClick": function(){
			this.$refs["fileButton"].click();
		},
		
		"onFileChange": function() {
			let file = this.$refs["fileButton"].files[0];

			if(!file)
			{
				return;
			}

			this.fileName = file.name;
			this.fieldValue = file;
			this.fromServer = false;
			this.updateDownloadUrl();
		},

		"onModelValueChanged": function(newVal) {
			if(!newVal)
			{
				this.fromServer = false;
				this.fileName = "";
				this.updateDownloadUrl();
				return;
			}

			if(typeof(newVal) === "string")
			{
				this.fromServer = true;
				this.fileName = this.extractDisplayFileName(newVal);
				this.updateDownloadUrl();
				return;
			}

			this.fromServer = false;
			this.fileName = newVal.name ? newVal.name : "";
			this.updateDownloadUrl();
		},

		"onServerFileClick": function(event) {
			if(!this.downloadUrl)
			{
				event.preventDefault();
			}
		},
		
		"onClearFile": function() {
			this.fieldValue = null;
			this.fileName = "";
			this.fromServer = false;
			this.downloadUrl = "";
			this.$refs["fileButton"].value = null;
		}
	},
	
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<div style="width: 100%; display: inline; border: 1px solid #ced4da; border-radius: 5px;">
					<div style="display: inline-block; padding: 3px 3px 3px 0.5rem; height: 100%;">
						<div v-if="fieldValue" style="overflow: hidden; display: inline-block;" class="ng-hide">
							<div role="button" data-toggle="tooltip" title="Remove" class="yk-remove-button" @click="onClearFile">
								x
							</div>
							<!-- Span when file info comes from server -->
							<span v-if="fromServer" style="cursor: default;" data-toggle="tooltip" :title="fileName" class="ng-hide">
								<a :href="downloadUrl" class="ng-binding" @click="onServerFileClick"> 
									{{fileName}}
								</a>
							</span>
							<!-- Span when user adds a file -->
							<span v-if="!fromServer" style="cursor: default;" data-toggle="tooltip" title="" class="ng-binding ng-hide">
								{{fileName}}
							</span>
						</div>
						
						<!-- Span for place holder -->
						<div v-if="!fieldValue" style="color: rgb(200, 200, 200); margin-left: 0.5em;  display: inline;" class="">
								{{fieldInfo.label}}
						</div>
					</div>

					<button type="button" class="btn btn-primary action-button fileProxy" 
							style="padding: 0.3em; float: right;" @click="onBrowseClick" data-toggle="tooltip" title="Browse"
							:aria-label="'Browse ' + fieldInfo.label">...</button>
					<input type="file" ref="fileButton" style="display: none;" @change="onFileChange">										
				</div>

				<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
			</div>
		</div>
	`
});

newVueUiComponent('yk-input-image', {
	"props": {
		"width": { "type": Number, "default": 150},
		"height": { "type": Number, "default": 150},
	},

	"data": {
		"fromServer": false,
		"serverImageUrl": "",
		"uploadedObjectUrl": null
	},

	"watch": {
		"fieldInfo.groupName": function() {
			this.updateServerImageUrl();
			this.setServerImagePreview();
		}
	},

	"unmounted": function() {
		if(this.uploadedObjectUrl)
		{
			URL.revokeObjectURL(this.uploadedObjectUrl);
			this.uploadedObjectUrl = null;
		}
	},

	"methods": {
		"updateServerImageUrl": function() {
			if(!this.fromServer || !this.fieldValue || !this.fieldInfo || !this.fieldInfo.groupName)
			{
				this.serverImageUrl = "";
				return;
			}

			let groupName = encodeURIComponent(this.fieldInfo.groupName);
			let fileName = encodeURIComponent(this.fieldValue);
			this.serverImageUrl = "/api/image/" + groupName + "/" + fileName;
		},

		"setServerImagePreview": function() {
			let imgElem = $(this.$refs["contentDiv"]).find(".server-image");
			imgElem.attr("src", this.serverImageUrl ? this.serverImageUrl : "");
		},

		"setUploadedImagePreview": function(file) {
			let imgElem = $(this.$refs["contentDiv"]).find(".uploaded-image");

			if(this.uploadedObjectUrl)
			{
				URL.revokeObjectURL(this.uploadedObjectUrl);
				this.uploadedObjectUrl = null;
			}

			if(!file)
			{
				imgElem.attr("src", "");
				return;
			}

			this.uploadedObjectUrl = URL.createObjectURL(file);
			imgElem.attr("src", this.uploadedObjectUrl);
		},

		"onBrowseClick": function(){
			this.$refs["fileButton"].click();
		},
		
		"onFileChange": function() {
			let file = this.$refs["fileButton"].files[0];

			if(!file)
			{
				return;
			}
			
			this.fieldValue = file;
			this.fromServer = false;
			this.serverImageUrl = "";
			
			this.setUploadedImagePreview(this.fieldValue);
		},

		"onModelValueChanged": function(newVal) {
			if(!newVal)
			{
				this.fromServer = false;
				this.serverImageUrl = "";
				this.setUploadedImagePreview(null);
				this.setServerImagePreview();
				return;
			}

			if(typeof(newVal) === "string")
			{
				this.fromServer = true;
				this.setUploadedImagePreview(null);
				this.updateServerImageUrl();
				this.setServerImagePreview();
				return;
			}

			this.fromServer = false;
			this.serverImageUrl = "";
			this.setServerImagePreview();
			this.setUploadedImagePreview(newVal);
		},
		
		"onClearFile": function() {
			this.fieldValue = null;
			this.fromServer = false;
			this.serverImageUrl = "";
			this.setUploadedImagePreview(null);
			this.setServerImagePreview();
			this.$refs["fileButton"].value = null;
		}
	},
	
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<div style="display: inline; border: 1px solid #ced4da; border-radius: 5px;">
					<div ref="contentDiv" :style="'display: inline-block; width: ' + width + 'px; height: ' + height + 'px;'">
						<div v-show="fieldValue" style="overflow: hidden; display: inline; width: 100%;" class="ng-hide">
							<div role="button" data-toggle="tooltip" title="Remove" class="yk-remove-button" @click="onClearFile"
								style="position: absolute;margin-left: 2px; margin-top: 2px;background-color: rgb(255, 0, 0, 0.4);">
								x
							</div>
							
							<!-- Image from the server -->
							<img class="server-image" v-show="fromServer" :width="width" :height="height" /> 

							<!-- Span when user adds a image -->
							<img class="uploaded-image" v-show="!fromServer" :width="width" :height="height" /> 
						</div>
						
						<!-- Span for place holder -->
						<div v-if="!fieldValue" 
							:style="'color: rgb(200, 200, 200); margin-left: 0.5em;  display: table-cell; vertical-align: middle; text-align: center; width: ' + width + 'px; height: ' + height + 'px;'">
								Upload {{fieldInfo.label}}
						</div>
					</div>

					<button type="button" class="yk-img-upload-btn"
							@click="onBrowseClick" data-toggle="tooltip" title="Browse" :aria-label="'Upload ' + fieldInfo.label">Upload</button>
					<input type="file" ref="fileButton" style="display: none;"
							accept="image/gif, image/jpg, image/jpeg, image/png" 
							@change="onFileChange">										
				</div>

				<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
			</div>
		</div>
	`
});

/**
 * Input field with OTP verification support.
 * Handles OTP sending, verification, resend with rate limiting, and expiration timers.
 */
newVueUiComponent('yk-ver-input-field', {
	"props": {
		/**
		 * Optional OTP provider function. If provided, this function will be called
		 * instead of the API endpoint for generating OTP.
		 * 
		 * Function signature: function(fieldId, valueToVerify)
		 * Should return a Promise that resolves to: {token, expiresOn, retryAfterSec, attemptsRemaining}
		 * 
		 * @param {string} fieldId - Field ID in format "ModelName.fieldName"
		 * @param {string} valueToVerify - The value to verify (email or mobile number)
		 * @returns {Promise<{token: string, expiresOn: number, retryAfterSec: number, attemptsRemaining: number}>}
		 */
		"otpProvider": {"type": Function, default: null}
	},
	
	"data": {
		"otpSent": false,
		"otpToken": null,
		"otpValue": "",
		"expirationTimer": null,
		"resendTimer": null,
		"attemptCount": 0,
		"maxAttemptsReached": false,
		"expirationInterval": null,
		"resendInterval": null,
		"valueToVerify": "",
		"expiresOn": null,
		"retryAfterSec": null,
		"attemptsRemaining": null
	},

	"watch": {
		"fieldValue.valueToVerify": function(newVal, oldVal) {

			// Skip if the value is not changed (which is expected with user interaction)
			if(this.valueToVerify === newVal) {
				return;
			}
			
			console.log("On field value change: ", newVal);
			// Update internal state when model value changes (e.g., from server)
			// Only sync valueToVerify (phone number, email id, etc.)
			var oldValue = this.valueToVerify;
			this.valueToVerify = newVal;

			// Reset OTP state if value changed
			if(this.otpSent && oldValue && oldValue !== newVal.valueToVerify) {
				this.resetOtpState();
			}
		}
	},
	
	"computed": {
		"contactValue": {
			get() {
				if(!this.fieldValue || !this.fieldValue.valueToVerify) {
					return "";
				}
				return this.fieldValue.valueToVerify;
			},
			set(value) {
				if(!this.fieldValue) {
					this.fieldValue = {};
				}
				
				var oldValue = this.fieldValue.valueToVerify;
				this.valueToVerify = value;
				this.fieldValue.valueToVerify = value;
				
				// Reset OTP state when value changes
				if(this.otpSent && oldValue && oldValue !== value) {
					this.resetOtpState();
				}
				
				this.onFieldValueChange(this.fieldValue);
			}
		},
		"expirationTimeDisplay": function() {
			if(!this.expirationTimer || this.expirationTimer <= 0) {
				return "";
			}
			var minutes = Math.floor(this.expirationTimer / 60);
			var seconds = this.expirationTimer % 60;
			return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
		},
		"resendTimeDisplay": function() {
			if(!this.resendTimer || this.resendTimer <= 0) {
				return "";
			}
			return this.resendTimer + "s";
		},
		"canResend": function() {
			return this.otpSent && this.resendTimer <= 0 && !this.maxAttemptsReached;
		},
		"adminContact": function() {
			return $appConfiguration.adminContactNumber || " ";
		}
	},

	"methods": {
		"onCreated": function() {
			// Initialize field value structure
			if(!this.fieldValue) {
				this.fieldValue = {};
			}
			if(this.fieldValue.valueToVerify) {
				this.valueToVerify = this.fieldValue.valueToVerify;
			} else if(this.modelValue && this.modelValue.valueToVerify) {
				// Initialize from modelValue if present
				this.fieldValue.valueToVerify = this.modelValue.valueToVerify;
				this.valueToVerify = this.modelValue.valueToVerify;
			}
		},
		
		"onMounted": function() {
			// Clean up any existing intervals
			this.clearIntervals();
		},
		
		"beforeUnmount": function() {
			this.clearIntervals();
		},
		
		"clearIntervals": function() {
			if(this.expirationInterval) {
				clearInterval(this.expirationInterval);
				this.expirationInterval = null;
			}
			if(this.resendInterval) {
				clearInterval(this.resendInterval);
				this.resendInterval = null;
			}
		},
		
		"resetOtpState": function() {
			this.otpSent = false;
			this.otpToken = null;
			this.otpValue = "";
			this.expirationTimer = null;
			this.resendTimer = null;
			this.expiresOn = null;
			this.retryAfterSec = null;
			this.attemptsRemaining = null;
			this.clearIntervals();
			if(this.fieldValue) {
				this.fieldValue.value = "";
				this.fieldValue.token = null;
			}
		},
		
		"startExpirationTimer": function() {
			if(!this.expiresOn) {
				return;
			}
			
			// Ensure expiresOn is a number (timestamp in milliseconds)
			var expiresOnValue = this.expiresOn;
			if(typeof expiresOnValue === 'string') {
				expiresOnValue = new Date(expiresOnValue).getTime();
			} else if(typeof expiresOnValue !== 'number') {
				// If invalid, don't start timer
				return;
			}
			
			// Calculate remaining time from expiresOn timestamp
			var now = Date.now();
			var remainingSeconds = Math.max(0, Math.floor((expiresOnValue - now) / 1000));
			this.expirationTimer = remainingSeconds;
			
			// Update expiresOn to ensure it's a number for future calculations
			this.expiresOn = expiresOnValue;
			
			this.clearIntervals();
			
			this.expirationInterval = setInterval($.proxy(function() {
				var now = Date.now();
				// Ensure expiresOn is a number
				var expiresOnValue = typeof this.expiresOn === 'number' ? this.expiresOn : new Date(this.expiresOn).getTime();
				var remainingSeconds = Math.max(0, Math.floor((expiresOnValue - now) / 1000));
				this.expirationTimer = remainingSeconds;
				
				if(this.expirationTimer <= 0) {
					clearInterval(this.expirationInterval);
					this.expirationInterval = null;
					this.fieldInfo.error = "OTP has expired. Please request a new one.";
				}
			}, this), 1000);
		},
		
		"startResendTimer": function() {
			if(this.retryAfterSec === null || this.retryAfterSec === undefined) {
				return;
			}
			
			this.resendTimer = this.retryAfterSec;
			
			if(this.resendInterval) {
				clearInterval(this.resendInterval);
			}
			
			this.resendInterval = setInterval($.proxy(function() {
				this.resendTimer--;
				if(this.resendTimer <= 0) {
					clearInterval(this.resendInterval);
					this.resendInterval = null;
				}
			}, this), 1000);
		},
		
		"onSendOtp": function() {
			// Validate input value first - required validation
			if(!this.valueToVerify || this.valueToVerify.trim() === "") {
				this.fieldInfo.error = "Please enter a valid " + (this.fieldInfo.verificationType === "EMAIL" ? "email address" : "mobile number");
				return;
			}
			
			// Validate format based on verification type
			var trimmedValue = this.valueToVerify.trim();
			if(this.fieldInfo.verificationType === "EMAIL") {
				// Basic email format validation
				var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
				if(!emailRegex.test(trimmedValue)) {
					this.fieldInfo.error = "Please enter a valid email address";
					return;
				}
			} else {
				// Basic mobile number validation (digits only, at least 10 digits)
				var mobileRegex = /^\d{10,}$/;
				if(!mobileRegex.test(trimmedValue)) {
					this.fieldInfo.error = "Please enter a valid mobile number (at least 10 digits)";
					return;
				}
			}
			
			// Check if max attempts reached
			if(this.maxAttemptsReached) {
				var adminContact = this.adminContact;
				$utils.alert("Maximum verification attempts reached. Please contact our support team at " + adminContact + " for assistance.");
				return;
			}
			
			// Get field ID for API call
			// Field ID format: "ModelName.fieldName" (e.g., "JobSeekerVerification.emailOtp")
			var fieldId = this.fieldInfo.id;
			if(!fieldId && this.fieldInfo.modelName && this.fieldInfo.name) {
				fieldId = this.fieldInfo.modelName + "." + this.fieldInfo.name;
			}
			if(!fieldId) {
				$utils.alert("Field ID not found. Cannot send OTP.");
				return;
			}
			
			// Use otpProvider if provided, otherwise use API
			if(this.otpProvider && typeof this.otpProvider === 'function') {
				// Call custom OTP provider
				var self = this;
				Promise.resolve(this.otpProvider(fieldId, this.valueToVerify))
					.then(function(result) {
						// Ensure result is in the expected format
						if(result && typeof result === 'object') {
							// If result is already in the format {token, expiresOn, retryAfterSec, attemptsRemaining}
							if(result.token !== undefined) {
								self.onOtpSent({response: result});
							} else {
								// If result is wrapped in a response object
								self.onOtpSent(result);
							}
						} else {
							self.handleOtpError({response: {message: "Invalid response from OTP provider"}});
						}
					})
					.catch(function(err) {
						self.handleOtpError(err);
					});
			} else {
				// Use default API call
				$restService.invokePost(
					"/api/otp/send/" + fieldId + "/" + encodeURIComponent(this.valueToVerify),
					null,
					{
						"context": this,
						"onSuccess": this.onOtpSent,
						"onError": $.proxy(function(err) {
							this.handleOtpError(err);
						}, this)
					}
				);
			}
		},
		
		"onOtpSent": function(result) {
			// API returns BasicReadResponse<SendOtpResponse>
			// If using otpProvider, result might be directly the response object
			var otpResponse = (result && result.response && result.response.value) ? result.response.value : null;
			
			if(!otpResponse) {
				console.log("Encountered invalid OTP response", otpResponse);
				throw "Encountered invalid OTP response";
			}
			
			// Extract values from server response
			this.otpToken = otpResponse.token;
			// Ensure expiresOn is a number (timestamp in milliseconds)
			// Handle both string and number formats
			var expiresOnValue = otpResponse.expiresOn;
			if(typeof expiresOnValue === 'string') {
				// If it's a string, try to parse it
				this.expiresOn = new Date(expiresOnValue).getTime();
			} else if(typeof expiresOnValue === 'number') {
				// If it's already a number, use it directly
				this.expiresOn = expiresOnValue;
			} else {
				// If invalid, calculate expiration from current time + default duration (5 minutes)
				this.expiresOn = Date.now() + (5 * 60 * 1000);
			}
			this.retryAfterSec = otpResponse.retryAfterSec;
			this.attemptsRemaining = otpResponse.attemptsRemaining;
			
			this.otpSent = true;
			this.attemptCount++;
			this.otpValue = "";
			
			// Update field value
			if(!this.fieldValue) {
				this.fieldValue = {};
			}
			this.fieldValue.valueToVerify = this.valueToVerify;
			this.fieldValue.token = this.otpToken;
			
			// Clear any previous errors
			this.fieldInfo.error = null;
			
			// Check if max attempts reached based on server response
			if(this.attemptsRemaining !== null && this.attemptsRemaining <= 0) {
				this.maxAttemptsReached = true;
			}
			
			// Start timers using server-provided values
			this.startExpirationTimer();
			this.startResendTimer();
			
			this.onFieldValueChange(this.fieldValue);
		},
		
		"handleOtpError": function(err) {
			var errorParams = err.response && err.response.errorParameters;
			
			if(errorParams) {
				if(errorParams.errorType === "quickRetryAttempt") {
					var retryAfter = errorParams.retryAfterSec || 30;
					this.resendTimer = retryAfter;
					this.startResendTimer();
					$utils.alert("Please wait " + retryAfter + " seconds before requesting a new OTP.");
				} else if(errorParams.errorType === "maxAttemptsExpired") {
					var retryAfterHour = errorParams.retryAfterHour || 24;
					this.maxAttemptsReached = true;
					var adminContact = this.adminContact;
					$utils.alert("Maximum verification attempts reached. Please wait " + retryAfterHour + " hours before trying again, or contact our support team at " + adminContact + " for assistance.");
				} else {
					$utils.alert("Failed to send OTP: \n<span class=\"webutils-prompt-error\">" + (err.response.message || "Unknown error") + "</span>");
				}
			} else {
				$utils.alert("Failed to send OTP: \n<span class=\"webutils-prompt-error\">" + (err.response.message || "Unknown error") + "</span>");
			}
		},
		
		"onResendOtp": function() {
			if(!this.canResend) {
				return;
			}
			
			// Reset OTP state but keep attempt count
			this.otpSent = false;
			this.otpToken = null;
			this.otpValue = "";
			this.expirationTimer = null;
			this.clearIntervals();
			
			// Send new OTP
			this.onSendOtp();
		},
		
		"onOtpValueChange": function() {
			if(!this.fieldValue) {
				this.fieldValue = {};
			}
			this.fieldValue.value = this.otpValue;
			this.fieldValue.valueToVerify = this.valueToVerify;
			this.fieldValue.token = this.otpToken;
			
			// Clear error when user types
			if(this.fieldInfo.error && this.fieldInfo.error.indexOf('OTP') !== -1) {
				this.fieldInfo.error = null;
			}
			
			this.onFieldValueChange(this.fieldValue);
		},
		
		"onValidateValue": function() {
			// Server-side validation will handle OTP verification
			// Just ensure we have the required data structure
			if(!this.fieldValue || !this.fieldValue.valueToVerify) {
				throw "Please enter " + (this.fieldInfo.verificationType === "EMAIL" ? "email address" : "mobile number");
			}
			
			if(!this.otpSent || !this.otpToken) {
				throw "Please request an OTP first by clicking the Verify button.";
			}
			
			if(!this.otpValue || this.otpValue.trim() === "") {
				throw "Please enter the OTP code.";
			}
			
			// Ensure field value has all required properties
			if(!this.fieldValue.value) {
				this.fieldValue.value = this.otpValue;
			}
			if(!this.fieldValue.token) {
				this.fieldValue.token = this.otpToken;
			}
		},
		
		"onServerError": function(error) {
			// Handle server-side errors (e.g., expired token, invalid OTP)
			if(error.code == 4402 || (error.message && error.message.indexOf("expired") !== -1)) {
				this.resetOtpState();
				this.fieldInfo.error = "OTP has expired. Please request a new one.";
			} else if(error.message && error.message.indexOf("not valid") !== -1) {
				this.fieldInfo.error = "Invalid OTP. Please check and try again.";
			}
		}
	},
	
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label">{{fieldInfo.label}}:</label>
			
			<!-- Contact Input Field -->
			<div class="input-group" style="margin-bottom: 0.5rem;">
				<input
					:name="fieldInfo.name + '_value'"
					:type="fieldInfo.inputType" 
					class="form-control" 
					:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
					v-model="contactValue"
					:class="{'is-invalid': displayError()}"
					:disabled="otpSent"
					/>
				<div class="input-group-append">
					<button 
						type="button"
						class="input-group-text verify-button" 
						@click="onSendOtp"
						:disabled="otpSent || maxAttemptsReached"
						style="cursor: pointer; user-select: none;"
						>
						{{otpSent ? 'OTP Sent' : 'Verify'}}
					</button>
				</div>
			</div>
			
			<!-- OTP Input Section (shown after OTP is sent) -->
			<div v-if="otpSent" class="otp-section" style="margin-top: 1rem;">
				<div class="input-group" style="margin-bottom: 0.5rem;">
					<input
						:name="fieldInfo.name + '_otp'"
						type="text" 
						class="form-control" 
						placeholder="Enter OTP code" 
						v-model="otpValue"
						@input="onOtpValueChange"
						maxlength="10"
						style="text-align: center; font-size: 0.9rem; letter-spacing: 0.2rem; font-weight: bold;"
						/>
				</div>
				
				<div class="otp-info" style="font-size: 0.85rem; color: #666; margin-bottom: 0.5rem; display: inline;">
					<span v-if="expirationTimer > 0">
						OTP expires in: <strong>{{expirationTimeDisplay}}</strong>
					</span>
					<span v-else style="color: #E74C3C;">
						OTP has expired
					</span>
				</div>
				
				<div class="otp-actions" style="display: flex; justify-content: space-between; align-items: center; display: inline; margin-left: 2rem;">
					<button 
						type="button"
						class="btn btn-link" 
						@click="onResendOtp"
						:disabled="!canResend"
						style="padding: 0; font-size: 0.85rem; text-decoration: none;"
						>
						<span v-if="resendTimer > 0">Resend OTP in {{resendTimeDisplay}}</span>
						<span v-else>Resend OTP</span>
					</button>
					
					<span v-if="maxAttemptsReached" style="color: #E74C3C; font-size: 0.85rem;">
						Max attempts reached. Contact admin at {{adminContact}}
					</span>
				</div>
			</div>
			
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});

/**
 * Input field with verification support.
 */
newVueUiComponent('yk-captcha-field', {
	"computed": {
		"captchaValue": {
			get() {
				if(!this.fieldValue || !this.fieldValue.value) {
					return "";
				}
				
				return this.fieldValue.value;
			},
			set(value) {
				if(!this.fieldValue) {
					this.fieldValue = {};
				}
				
				this.fieldValue.value = value;
				this.onFieldValueChange(this.fieldValue);
			}
		}
	},
	
	"methods": {
		"onCreate": function() {
			this.onLoadCaptcha();
		},
		
		"onLoadCaptcha": function() {
			$restService.invokeGet(
				"/api/captcha", 
				null,
				{
					"context": this, 
					"onSuccess": function(result){
						let resp = result.response;
						let base64Img = resp.imageBase64;
						base64Img = 'data:image/png;base64,' + base64Img;
						$(this.$el).find(".captchaImg").attr("src", base64Img);
						
						if(!this.fieldValue)
						{
							this.fieldValue = {};
						}
						
						this.fieldValue.token = resp.token;
						this.onFieldValueChange(this.fieldValue);
					},
					"onError": function(result){
						console.error("Failed to load captcha with error: ", result);
					}
				}
			);
		},
		
		"onValidateValue": function() {
			if(!this.fieldValue && !this.fieldValue.token) {
				throw "Failed to load captcha. Please try reloading page.";
			}
		},
	},
	
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label">Retype the characters from image:</label><br/>
			<img class="captchaImg" :id="fieldInfo.name + '-image'" src="" width="200" height="50" style="width: 200px; height: 50px;">
			<span style="cursor: pointer; color: blue; font-size: 1.5rem;" title="Refresh" @click="onLoadCaptcha">
				<i class="bi bi-arrow-repeat"></i>
			</span>
			<input type="hidden" :id="fieldInfo.name + '-token'" :value="fieldValue && fieldValue.token ? fieldValue.token : ''"/>
			<input type="text" class="form-control" :name="fieldInfo.name" :id="fieldInfo.name" v-model="captchaValue" placeholder="Retype the characters from image" 
				style="margin-top: 4px; width: 20em;"
				:class="{'is-invalid': displayError()}">
				
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-textarea-field', {
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<textarea
				:name="fieldInfo.name"
				:id="fieldInfo.name"
				class="form-control webutil-field" 
				rows="3"
				:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
				v-model="fieldValue" 
				:class="{'is-invalid': displayError()}"
				></textarea>
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-html-editor', {
	"props": {
		// Quill toolbar options: https://quilljs.com/docs/modules/toolbar/
		"toolbar": { "type": [Array, Object], "default": () => ([
			["bold", "italic", "underline"],
			[{ "size": ["small", false, "large", "huge"] }],
			[{ "color": [] }, { "background": [] }],
			[{ "list": "ordered" }, { "list": "bullet" }],
			[{ "indent": "-1" }, { "indent": "+1" }],
			[{ "align": [] }],
			["link", "clean"]
		])}
	},
	
	"methods": {
		"onMounted"	: function() 
		{
			if(typeof Quill == "undefined")
			{
				console.error("Quill library is not loaded for yk-html-editor");
				return;
			}

			// Quill 2 must not be stored in Vue 2 reactive data — wrapping the instance breaks
			// internal Proxies and causes selection errors (e.g. null offset in normalizedToRange).
			this._quillEditor = new Quill(this.$refs.editorContainer, {
				theme: "snow",
				modules: {
					toolbar: this.toolbar
				}
			});

			this._quillEditor.on("text-change", $.proxy(this.onEditorValueChange, this));
			
			if(this.fieldValue)
			{
				this.setEditorContent(this.fieldValue);
			}
		},
		
		"onModelValueChanged": function(newVal)
		{
			this.setEditorContent(newVal ? newVal : "");
		},
		
		"setEditorContent": function(content)
		{
			var quill = this._quillEditor;
			if(!quill)
			{
				return;
			}
			
			var htmlContent = content ? content : "";
			var curContent = quill.root.innerHTML;
			
			if(curContent == htmlContent)
			{
				return;
			}
			
			try
			{
				var delta = quill.clipboard.convert({ "html": htmlContent, "text": "" });
				quill.setContents(delta, "api");
				quill.setSelection(0, 0, "silent");
			}
			catch(ex)
			{
				console.log("Error: ", ex);
			}
		},
		
		"getFieldValue": function()
		{
			var quill = this._quillEditor;
			if(!quill)
			{
				return this.fieldValue ? this.fieldValue : "";
			}
			
			// Treat Quill's empty content as empty string for validations/binding.
			if(quill.getText().trim().length == 0)
			{
				return "";
			}
			
			return quill.root.innerHTML;
		},
		
		"onEditorValueChange": function()
		{
			var newContent = this.getFieldValue();
			
			if(this.fieldValue != newContent)
			{
				this.fieldValue = newContent;
			}
		}
	},
	
	"template": `
		<div class="form-group yk-html-editor">
			<label class="webutil-field-label form-label" v-if="!hideLabel && fieldInfo.label && fieldInfo.label.length &gt; 0">{{fieldInfo.label}}:</label>
			<div
				:id="fieldInfo.name"
				:class="{'is-invalid': displayError()}"
				ref="editorContainer"
				></div>
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-switch', {
	"methods": {
		/**
		 * Coerce any value to a real boolean so the model never holds "" / null
		 * (which breaks JSON deserialization into primitive boolean).
		 */
		"toBoolean": function(val) {
			return val === true || val === "true" || val === 1 || val === "1";
		},

		"onCreate": function() {
			var initial = this.modelValue;
			if(initial === undefined || initial === null || initial === "") {
				initial = this.fieldInfo.defaultValue;
			}
			this.fieldValue = this.toBoolean(initial);
		},

		"onModelValueChanged": function(newVal) {
			var boolVal = this.toBoolean(newVal);
			if(this.fieldValue !== boolVal) {
				this.fieldValue = boolVal;
			}
		},

		/**
		 * Override base reset (which maps falsy values to "") so unchecked
		 * switches always persist as false, not "".
		 */
		"reset": function(val) {
			if(val === undefined || val === null || val === "") {
				val = this.fieldInfo.defaultValue;
			}
			var boolVal = this.toBoolean(val);
			this.fieldValue = boolVal;
			// Ensure parent model is updated even when value was already boolean false
			this.$emit('update:modelValue', boolVal);
		}
	},
	"template": `
		<div class="form-group">
			<label v-if="!hideLabel" class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<div class="form-check form-switch">
			  <input class="form-check-input webutil-switch" type="checkbox" :name="fieldInfo.name" :id="fieldInfo.name" v-model="fieldValue">
			</div>			
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});

