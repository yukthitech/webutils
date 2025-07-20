import {$utils, $mergeObjProperty} from "./common.js";
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
			"enableError": {"type": Boolean, default: false},
			
			/**
			 * Can be used to set initial value for the field.
			 * This also helps in 2-way binding with parent fields using v-model
			 */
			"modelValue": {}
		},

		"watch": {
			"fieldValue": function(newVal, oldVal)
			{
				this.onFieldValueChange(newVal);
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
				
				this.validateAndSetValue(this.fieldValue);
			},
			
			"getError": function() {
				return this.fieldInfo.error;
			},
			
			"displayError": function() {
				return this.fieldInfo.error && this.enableError;
			},
			
			"validateAndSetValue": function(newVal) {
				var curErrStatus = this.displayError();
				
				this.fieldInfo.error = null;
				
				try
				{
					$validationService.validate(
							this.fieldInfo.dataType,
							this.fieldInfo.validations, 
							newVal, this.data);
					
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
			
			"setServerError": function(error) {
				this.fieldInfo.error = error.message;
				
				if(this.onServerError) {
					this.onServerError(error);
				}
			}
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
			<label class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<input
					:name="fieldInfo.name"
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
				<div class="invalid-feedback">{{fieldInfo.error}}</div>
			</div>
		</div>
	`
});

newVueUiComponent('yk-input-file', {
	"data": {
		"fromServer": false			
	},

	"methods": {
		"onBrowseClick": function(){
			this.$refs["fileButton"].click();
		},
		
		"onFileChange": function() {
			let file = this.$refs["fileButton"].files[0];
			this.fieldValue = {"fileName": file.name, "file": file};
			this.fromServer = false;
		},
		
		"onClearFile": function() {
			this.fieldValue = null;
		}
	},
	
	"template": `
		<div class="form-group">
			<label class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<div style="width: 100%; display: inline; border: 1px ridge rgb(200,200,200); border-radius: 5px;">
					<div style="display: inline-block; padding: 3px 3px 3px 0.5rem; height: 100%;">
						<div v-if="fieldValue" style="overflow: hidden; display: inline-block;" class="ng-hide">
							<div role="button" data-toggle="tooltip" title="Remove" class="yk-remove-button" @click="onClearFile">
								x
							</div>
							<!-- Span when file info comes from server -->
							<span v-if="fromServer" style="cursor: default;" data-toggle="tooltip" :title="fieldValue.fileName" class="ng-hide">
								<a href="#" class="ng-binding"> 
									{{fieldValue.fileName}}
								</a>
							</span>
							<!-- Span when user adds a file -->
							<span v-if="!fromServer" style="cursor: default;" data-toggle="tooltip" title="" class="ng-binding ng-hide">
								{{fieldValue.fileName}}
							</span>
						</div>
						
						<!-- Span for place holder -->
						<div v-if="!fieldValue" style="color: rgb(200, 200, 200); margin-left: 0.5em;  display: inline;" class="">
								{{fieldInfo.label}}
						</div>
					</div>

					<button type="button" class="btn btn-primary action-button fileProxy" 
							style="padding: 0.3em; float: right;" @click="onBrowseClick" data-toggle="tooltip" title="Browse">...</button>
					<input type="file" ref="fileButton" style="display: none;" @change="onFileChange">										
				</div>

				<div class="invalid-feedback">{{fieldInfo.error}}</div>
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
		"fromServer": false			
	},

	"methods": {
		"onBrowseClick": function(){
			this.$refs["fileButton"].click();
		},
		
		"onFileChange": function() {
			let file = this.$refs["fileButton"].files[0];
			this.fieldValue = {"fileName": file.name, "file": file};
			this.fromServer = false;
			
			let imgElem = $(this.$refs["contentDiv"]).find(".uploaded-image");
			imgElem.attr("src", URL.createObjectURL(this.fieldValue.file));
		},
		
		"onClearFile": function() {
			this.fieldValue = null;
			this.$refs["fileButton"].value = null;
		}
	},
	
	"template": `
		<div class="form-group">
			<label class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<div style="display: inline; border: 1px ridge rgb(200,200,200); border-radius: 5px;">
					<div ref="contentDiv" :style="'display: inline-block; width: ' + width + 'px; height: ' + height + 'px;'">
						<div v-show="fieldValue" style="overflow: hidden; display: inline; width: 100%;" class="ng-hide">
							<div role="button" data-toggle="tooltip" title="Remove" class="yk-remove-button" @click="onClearFile"
								style="position: absolute;margin-left: 2px; margin-top: 2px;background-color: rgb(255, 0, 0, 0.4);">
								x
							</div>
							<!-- Span when file info comes from server -->
							<span v-if="fromServer" style="cursor: default;" data-toggle="tooltip" :title="fieldValue.fileName" class="ng-hide">
								<a href="#" class="ng-binding"> 
									{{fieldValue.fileName}}
								</a>
							</span>
							
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
							@click="onBrowseClick" data-toggle="tooltip" title="Browse">Upload</button>
					<input type="file" ref="fileButton" style="display: none;"
							accept="image/gif, image/jpg, image/jpeg, image/png" 
							@change="onFileChange">										
				</div>

				<div class="invalid-feedback">{{fieldInfo.error}}</div>
			</div>
		</div>
	`
});

/**
 * Input field with verification support.
 */
newVueUiComponent('yk-ver-input-field', {
	"data": {
		"verified": false,
		"otpToken": null,
		"verifiedValue": null,
	},
	
	"computed": {
		"otpValue": {
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

				if(this.verifiedValue) {
					this.verified = (this.verifiedValue == value);
				}
								
				this.onFieldValueChange(this.fieldValue);
			}
		}
	},

	"methods": {
		"onVerify": function(inst) {
			if(this.verified) {
				return;
			}
			
			if(this.fieldInfo.error && this.fieldInfo.error != 'Verification is not done.'
				&& this.fieldInfo.error != 'Token expired.'
			  )
			{
				$utils.alert("Please correct below error in value and then try: \n<span class=\"webutils-prompt-error\">" + this.fieldInfo.error + "</span>");
				return;
			}
			
			$restService.invokePost(
				"/api/verification/sendOtp/" + this.fieldInfo.verificationType + "/" + this.fieldValue.value, 
				null,
				{
					"context": this, 
					"onSuccess": this.onOtpGeneration,
					"onError": function(err) {
						$utils.alert("Otp sending failed with error: \n<span class=\"webutils-prompt-error\">" + err.response.message + "</span>");
					}
				}
			);
		},
		
		"onOtpGeneration": function(result) {
			this.otpToken = result.response.token;
			
			$utils.input("Please enter OTP sent to your " + this.fieldInfo.verificationType, null, $.proxy(function(otpVal) {
				if(otpVal == null) {
					return;
				}
				
				$restService.invokePost(
					"/api/verification/verify", 
					{
						"token": this.otpToken,
						"type": this.fieldInfo.verificationType,
						"value": this.fieldValue.value,
						"otp": otpVal
					},
					{
						"context": this, 
						"onSuccess": this.onOtpVerify,
						"onError": $.proxy(function(err) {
							$utils.alert("Otp verification failed with error: \n<span class=\"webutils-prompt-error\">" + err.response.message + "</span>");
							this.fieldInfo.error = "Failed to send otp";
						}, this)
					}
				);
			}, this));
		},
		
		"onOtpVerify": function(result) {
			this.fieldValue.token = result.response.token;
			
			this.verifiedValue = this.fieldValue.value;
			this.verified = true;
			
			this.clearError(this.fieldValue);
			this.onFieldValueChange(this.fieldValue);
		},
		
		"onValidateValue": function() {
			if(!this.verified) {
				throw "Verification is not done.";
			}
		},
		
		"onServerError": function(error) {
			// if the code is expired
			if(error.code == 4402) {
				this.verified = false;
				this.otpToken = null;
				this.verificationToken = null;
			}
		}
	},
	
	"template": `
		<div class="form-group">
			<label class="webutil-field-label">{{fieldInfo.label}}:</label>
			<div class="input-group">
				<input
					:name="fieldInfo.name"
					:type="fieldInfo.inputType" 
					class="form-control" 
					:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
					v-model="otpValue"
					:class="{'is-invalid': displayError()}"
					/>
				<div class="input-group-append">
					<span :class="verified ? 'input-group-text verified-button' : 'input-group-text verify-button'" @click="onVerify">
						{{verified ? 'Verified' : 'Verify'}}
					</span>
				</div>
				<div class="invalid-feedback">{{fieldInfo.error}}</div>
			</div>
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
			<label>Retype the characters from image:</label><br/>
			<img class="captchaImg" src="" width="200" height="50" style="width: 200px; height: 50px;">
			<span style="cursor: pointer; color: blue; font-size: 1.5rem;" title="Refresh" @click="onLoadCaptcha">
				<i class="bi bi-arrow-repeat"></i>
			</span>
			<input type="text" class="form-control" v-model="captchaValue" placeholder="Retype the characters from image" 
				style="margin-top: 4px; width: 20em;"
				:class="{'is-invalid': displayError()}">
				
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-textarea-field', {
	"template": `
		<div class="form-group">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<textarea
				:name="fieldInfo.name"
				class="form-control webutil-field" 
				rows="3"
				:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
				v-model="fieldValue" 
				:class="{'is-invalid': displayError()}"
				></textarea>
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-html-editor', {
	"props": {
		//Toolbar options can be found @ https://www.tiny.cloud/docs/advanced/available-toolbar-buttons/
		"toolbar": { "type": String, "default": `
				bold italic underline subscript superscript |
				forecolor backcolor|
				bullist numlist checklist |
				fontselect fontsizeselect |
				indent outdent|
				quicktable |
				alignleft aligncenter alignright alignjustify |
				emoticons
			` },
		"plugins": { "type": Array, "default": () => ["lists", "emoticons", "quickbars", "table"]},
		"menubar": { "type": Boolean, "default": true} 
	},
	
	"methods": {
		"onMounted"	: function() 
		{
			tinymce.init({
				selector: '#' + this.fieldInfo.name,
				plugins: this.plugins,
				toolbar: this.toolbar,
				init_instance_callback: $.proxy(this.onInitComplete, this),
				onchange_callback: $.proxy(this.onValueChange, this)
			});
		},
		
		"onInitComplete": function(inst)
		{
			if(this.fieldValue)
			{
				inst.setContent(this.fieldValue);
			}
		},
		
		"setFieldValue": function(newVal)
		{
			this.fieldValue = newVal;
			tinyMCE.get(this.fieldInfo.name).setContent(this.fieldValue);
		},
		
		"getFieldValue": function()
		{
			return tinyMCE.get(this.fieldInfo.name).getContent();
		},
		
		"onValueChange": function()
		{
			var newContent = tinyMCE.get(this.fieldInfo.name).getContent();
			this.validateAndSetValue(newContent);
		}
	},
	
	"template": `
		<div class="form-group">
			<label class="webutil-field-label form-label" v-if="fieldInfo.label && fieldInfo.label.length &gt; 0">{{fieldInfo.label}}:</label>
			<textarea
				:id="fieldInfo.name"
				:name="fieldInfo.name"
				rows="6"
				:class="{'is-invalid': displayError()}"
				></textarea>
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-switch', {
	"template": `
		<div class="form-group">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<div class="form-check form-switch">
			  <input class="form-check-input webutil-switch" type="checkbox" :name="fieldInfo.name" :id="fieldInfo.name" v-model="fieldValue">
			</div>			
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-lov-field', {
	"data": {
		"lovOptions": [],
		"searchable": true
	},
	
	"props": {
		"staticLovType": { "type": String, "default": "" },
		"dynamicLovName": { "type": String, "default": "" },
		"storedLovName": { "type": String, "default": "" },
		"noAuth": { "type": Boolean, "default": false },
		"emptyOption": { "type": String, "default": "" },
		"display": { "type": String, "default": "block" },
	},
	
	"updated": function() {
		var selectElem = $(this.$el).find("select");
		$(selectElem).selectpicker('refresh');
		
		this.$nextTick($.proxy(this.refreshDropDown, this));
	},
	
	"methods": {
		"onCreate": function() {
			this.fieldValue = this.modelValue;

			if(this.staticLovType.length > 0)
			{
				this.fieldInfo.lovDetails = {"lovType": "STATIC_TYPE", "lovName": this.staticLovType};
			}
			else if(this.dynamicLovName.length > 0)
			{
				this.fieldInfo.lovDetails = {"lovType": "DYNAMIC_TYPE", "lovName": this.dynamicLovName};
			}
			else if(this.storedLovName.length > 0)
			{
				this.fieldInfo.lovDetails = {"lovType": "STORED_TYPE", "lovName": this.storedLovName};
			}
			
			if(!this.fieldInfo.lovDetails.parentField)
			{
				$restService.fetchLovValues(this.fieldInfo.lovDetails.lovName, this.fieldInfo.lovDetails.lovType, this.setLovValues, null, this.noAuth);
				
				if(this.formData)
				{
					this.validateAndSetValue(this.formData.data[this.fieldInfo.name]);
				}
			}
			else
			{
				//With parent dependency, state will not be changed. Hence updated() will not be called
				//  hence picker invocation is called directly
				var selectElem = $(this.$el).find("select");
				$(selectElem).selectpicker('refresh');
				this.$nextTick($.proxy(this.refreshDropDown, this));
			}
		},
		
		"onMounted": function() {
		},
		
		"refreshDropDown": function()
		{
		    // Code that will run only after the entire view has been re-rendered
			var selectElem = $(this.$el).find("select");
			$(selectElem).selectpicker();
			
			var enclosingDiv = $(this.$el).find("div.dropdown");
			$(enclosingDiv).addClass('form-control');
			
			$(enclosingDiv).find("li").addClass("webutil-dropdown-item");
		},
		
		"getParentDetails": function() 
		{
			console.log("Method: getParentDetails");
			if(!this.fieldInfo.lovDetails.parentField)
			{
				return null;
			}
			
			return {
				"name": this.fieldInfo.lovDetails.parentField, 
				"callback": $.proxy(this.onParentFieldChange, this)
			};
		},
		
		"onParentFieldChange": function(newParentVal)
		{
			$restService.fetchLovValues(this.fieldInfo.lovDetails.lovName, this.fieldInfo.lovDetails.lovType, this.setLovValues, newParentVal, this.noAuth);
			this.validateAndSetValue(this.formData.data[this.fieldInfo.name]);
		},
		
		"reset": function(val) {
			this.fieldValue = val ? val : "";
			var selectElem = $(this.$el).find("select");
			
			$(selectElem).val('default');
			$(selectElem).selectpicker("refresh");
			
			if(val)
			{
				$(selectElem).selectpicker('val', "" + val);
			}
		},

		"setLovValues": function(lovList) {
			if(this.lovOptions.length > 0)
			{
				this.lovOptions.splice(0, this.lovOptions.length);
			}
			
			if(this.emptyOption.length > 0)
			{
				this.lovOptions.push({"label": this.emptyOption, "value": ""});
			}
			
			for(var lov of lovList)
			{
				this.lovOptions.push(lov);
			}
			
			this.searchable = (this.lovOptions.length > 5);
			this.$nextTick($.proxy(this.refreshDropDown, this));
		},
		
		"onErrorStatus": function(newErrStatus) {
			if(!newErrStatus) {
				$(this.$el).find(".is-invalid").removeClass("is-invalid");
			}
		}
	},

	"template": `
		<div class="form-group" :style="'display:' + display">
			<label class="webutil-field-label form-label" v-if="fieldInfo.label">{{fieldInfo.label}}:</label>
			<select 
				class="selectpicker"
				data-style="webutil-dropdown"
				:data-live-search="searchable"
				:data-show-subtex="true"
				
				:name="fieldInfo.name"
				v-model="fieldValue"
				:class="{'is-invalid': displayError()}"
				>
				<option v-for="lov in lovOptions" :key="lov.value" :value="lov.value">
					{{lov.label}}
				</option>
			</select>
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

let editableLovBase = {
	"props": {
		debounceTime: {type: Number, default: 300},
		storedLovName: {type: String, default: ""},
	},

	"data": {
		debounceTimeout: null,
		searchTerm: "",
		filteredOptions: [],
		highlightedIndex: -1,
		lovOptions: [],
		dropdownStyle: "",
	},
	
	"updated": function() {
		var selectElem = $(this.$el).find("select");
		$(selectElem).selectpicker('refresh');
		
		this.$nextTick($.proxy(this.refreshDropDown, this));
	},
	
	"methods": {
		"onMounted": function() {
			document.addEventListener('click', this.handleClickOutside);
		},
		
		"clearSelection": function() {
			this.searchTerm = "";
		},
		
		"onCreate": function() {
			if(!this.fieldInfo.lovDetails)
			{
				this.fieldInfo.lovDetails = {};
			}
			
			if(!this.fieldInfo.lovDetails.lovType)
			{
				this.fieldInfo.lovDetails.lovType = "STORED_TYPE";
			}
			
			if(this.storedLovName)
			{
				this.fieldInfo.lovDetails.lovName = this.storedLovName;
			}

			$restService.fetchLovValues(this.fieldInfo.lovDetails.lovName, this.fieldInfo.lovDetails.lovType, this.setLovValues, null, this.noAuth);
			
			if(this.onLovCreate)
			{
				this.onLovCreate();
			}
		},

		"setLovValues": function(lovList) {
			if(this.lovOptions.length > 0) {
				this.lovOptions.splice(0, this.lovOptions.length);
			}
			
			for(var lov of lovList) {
				this.lovOptions.push(lov);
			}
		},

		"onErrorStatus": function(newErrStatus) {
			if(!newErrStatus) {
				$(this.$el).find(".is-invalid").removeClass("is-invalid");
			}
		},
		
		"onInput": function(){
			clearTimeout(this.debounceTimeout);
			this.debounceTimeout = setTimeout($.proxy(function(){
				this.filteredOptions = this.fetchOptions(this.searchTerm);
				this.highlightedIndex = -1;

				this.$nextTick($.proxy(this.adjustDropDownHeight, this));
			}, this), this.debounceTime);
		},
		
		adjustDropDownHeight: function() {
			let inputRef = this.$refs["inputField"];
			let dropDownDivRef = this.$refs["dropDown"];
			
			if (!inputRef || !dropDownDivRef)
			{
				return;
			}
			
			// reset the height of drop down to auto
			dropDownDivRef.style["max-height"] = 'none';

			let inputRect = inputRef.getBoundingClientRect();
			let dropdownHeight = dropDownDivRef.offsetHeight;
			let viewportHeight = window.innerHeight;

			let spaceBelow = viewportHeight - inputRect.bottom;
			let spaceAbove = inputRect.top;
			
			if (spaceBelow >= dropdownHeight || spaceBelow >= spaceAbove) {
			  // Enough space below, show dropdown below input
			  this.dropdownStyle = {
			    top: `${inputRect.height + 7}px`,
			    bottom: 'auto',
			    maxHeight: `${spaceBelow-20}px`,
			  };
			} else {
			  // Not enough space below, show dropdown above input
			  this.dropdownStyle = {
			    top: 'auto',
			    bottom: `${inputRect.height + 7}px`,
			    maxHeight: `${spaceAbove-20}px`,
			  };
			}			
		},

		fetchOptions: function(term) {
			let res = [];
			
			term = term ? term.toLowerCase() : null;
			
			for(let lov of this.lovOptions) {
				if(!term || lov.label.toLowerCase().indexOf(term) >= 0) {
					res.push(lov.label);
				}
			}
			
			return res;
		},
		
		selectOption: function(option) {
			this.searchTerm = option;
			this.filteredOptions = [];
			this.highlightedIndex = -1;
			
			if(this.optionSelected) {
				this.optionSelected(option);
			}
		},
		
		highlightNext: function() {
			if (this.highlightedIndex < this.filteredOptions.length - 1) {
				this.highlightedIndex++;
			}
		},

		highlightPrev: function() {
			if (this.highlightedIndex > 0) {
				this.highlightedIndex--;
			}
		},
		
		highlightIndex: function(index) {
			if(index >= 0 && index < this.filteredOptions.length) {
				this.highlightedIndex = index;
			}
		},
		
		selectHighlighted: function() {
			if(this.highlightedIndex >= 0 && this.highlightedIndex < this.filteredOptions.length) {
				this.selectOption(this.filteredOptions[this.highlightedIndex]);
			}
		},
		
		hideDropdown: function() {
			this.filteredOptions = [];
		},
		
		onBlur: function() {
			setTimeout($.proxy(function(){
				this.hideDropdown();
			}, this), 500);			
		},
		
		renderOption: function(optionText) {
			if(this.searchTerm.length == 0) {
				return optionText;
			}
			
			let expr = this.searchTerm.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
			return optionText.replaceAll(new RegExp('(' + expr + ')', "gi"), '<span class="match">$1</span>');
		},
		
		handleClickOutside: function(event) {
			let dropDownDivRef = this.$refs["dropDown"];
			
			if (dropDownDivRef != event.target) {
		        this.hideDropdown();
			}
		},
		
		findOptionWithLabel: function(label) {
			if(!label) {
				return null;
			}

			for(let opt of this.lovOptions) {
				if(opt.label == label) {
					return opt;
				}
			}

			return {"label": label, "isNew": true};
		}
		
	}
};


newVueUiComponent('yk-editable-lov-field', {
	"extends": editableLovBase,
	
	"data": {
		selectedOption: null
	},

	"watch": {
		"searchTerm": function(newVal, oldVal)
		{
			this.selectedOption = this.findOptionWithLabel(newVal);
			
			if(this.selectedOption == null) {
				this.fieldValue = "";
				return;
			}

			this.fieldValue = this.selectedOption.isNew ? 
					{"newValue": this.selectedOption.label, "label": this.selectedOption.label} 
					: {"id": this.selectedOption.value, "label": this.selectedOption.label};
		}
	},

	"template": `
		<div class="form-group">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>

			<div class="yuk-autocomplete">
			  <input
			  	ref="inputField"
			  	class="form-control yk-input-field"
				style="padding: .2rem .75rem;"
			    v-model="searchTerm"
			    @input="onInput"
				@focus="onInput"
				@click="onInput"
				@blur="onBlur"
			    @keydown.down="highlightNext"
			    @keydown.up="highlightPrev"
			    @keydown.enter="selectHighlighted"
				@keydown.esc="hideDropdown"
			    :placeholder="'Select ' + fieldInfo.label"
			  />
			  
			  <div class="new-tag" v-if="selectedOption && selectedOption.isNew">New</div>
			  
			  <ul
			  	ref="dropDown" 
			  	v-show="filteredOptions.length" :style="dropdownStyle">
			    <li
			      v-for="(option, index) in filteredOptions"
			      :key="index"
			      :class="{ highlighted: index === highlightedIndex }"
			      @click="selectOption(option)"
			      @mouseover="highlightIndex(index)"
			    >
			      <span v-html="renderOption(option)"></span>
			    </li>
			  </ul>
			</div>
			
			
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

newVueUiComponent('yk-multi-editable-lov-field', {
	"extends": editableLovBase,

	"data": {
		selectedOption: null,
		selectedOptions: []
	},

	"watch": {
		"searchTerm": function(newVal, oldVal)
		{
			this.selectedOption = this.findOptionWithLabel(newVal);
		}
	},

	"methods": {
		"onLovCreate": function() {
			this.fieldValue = [];
		},
		
		"optionSelected": function(newLabel) {
			if(!newLabel) {
				return;
			}
			
			let option = this.findOptionWithLabel(newLabel);
			
			if(!option) {
				return;
			}
			
			for(let opt of this.selectedOptions) {
				if(opt.label == option.label) {
					$utils.alert("Option is already selected: " + option.label);
					return;
				}
			}
			
			this.selectedOptions.push(option);
			
			let opt = option.isNew ? 
					{"newValue": option.label} : {"id": option.value};
					
			this.fieldValue.push(opt);
			this.onFieldValueChange(this.fieldValue);
			this.searchTerm = "";
		},
		
		removeOption: function(option) {
			let idx = this.selectedOptions.indexOf(option);
			
			if(idx >= 0) {
				this.selectedOptions.splice(idx, 1);
				this.fieldValue.splice(idx, 1);
				this.onFieldValueChange(this.fieldValue);
			}
		},
		
		fetchOptions: function(term) {
			let res = [];
			
			term = term ? term.toLowerCase() : null;
			
			for(let lov of this.lovOptions) {
				// if value is already selected, skip it
				if(this.selectedOptions.indexOf(lov) >= 0) {
					continue;
				}
				
				if(!term || lov.label.toLowerCase().indexOf(term) >= 0) {
					res.push(lov.label);
				}
			}
			
			return res;
		},
		
	},
	
	"template": `
		<div class="form-group">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			
			<div style="1px ridge rgb(200, 200, 200); position: relative; border-radius: 5px;" class="yk-multi-editable-lov-field">
				<div v-for="option in selectedOptions" class="selected-tag">
					{{option.label}}
					<span @click="removeOption(option)" class="remove-button" title="Delete">x</span>
					<div class="new-tag" v-if="option.isNew" style="top: -5px;font-size: 0.45rem;">New</div>
				</div>
				<div class="yuk-autocomplete" style="width: 40%; margin-bottom: 1.5rem;">
				  <input
				  	ref="inputField"
				  	class="form-control yk-input-field"
				    v-model="searchTerm"
				    @input="onInput"
					@focus="onInput"
					@blur="onBlur"
				    @keydown.down="highlightNext"
				    @keydown.up="highlightPrev"
				    @keydown.enter="selectHighlighted"
					@keydown.esc="hideDropdown"
				    :placeholder="'Add ' + fieldInfo.label"
					style="margin-top: 0.5rem;margin-left: 0.5rem; padding: .2rem .75rem;"
				  />
				  
				  <ul
				  	ref="dropDown" 
				  	v-show="filteredOptions.length || (selectedOption && selectedOption.isNew)" 
					:style="dropdownStyle">

						<li v-if="selectedOption && selectedOption.isNew"
						  :key="-1"
						  :class="{ highlighted: highlightedIndex == -1}"
						  @click="selectOption(selectedOption.label)"
						  @mouseover="highlightIndex(-1)"
						>
							<span class="match">New - </span>
							<span v-html="selectedOption.label"></span>
						</li>
					    <li
					      v-for="(option, index) in filteredOptions"
					      :key="index"
					      :class="{ highlighted: index === highlightedIndex }"
					      @click="selectOption(option)"
					      @mouseover="highlightIndex(index)"
					    >
					      <span v-html="renderOption(option)"></span>
					    </li>
				  </ul>
				</div>
			</div>
			
			
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});
