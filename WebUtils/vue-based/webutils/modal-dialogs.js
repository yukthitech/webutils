import {$restService} from "./rest-service.js";
import {$utils, $appConfiguration} from "./common.js";

export var dialogComponents = {};

var $modalManager = {
	"modalStack": [],
		
	"openModal": function(id, config) {
		var dlg = $("#" + id);
		
		if(dlg.length < 0)
		{
			throw "No modal found with specified id - " + id;
		}
		
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

		$('#' + id).off('hidden.bs.modal').on('hidden.bs.modal', $.proxy(function (e) {
			var idx = this.modalStack.indexOf(this.id);
			
			if(idx < 0)
			{
				return;
			}
			
			this.modalStack.splice(idx, 1);
			
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
	
	"closeModal": function(id)
	{
		$('#' + id).modal('hide');
	}
};


dialogComponents['yk-modal-dialog'] = {
	"props": {
		"id": String,
		"title": String,
		"submitText": String,
		"closeText": String,
		
		"size": {
			"type": String,
			"default": "modal-xl"
		},
		
		/*
		 * Margin to be used for modal on the top. If not specified
		 * default margin will be used.
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
		
		"close": function() 
		{
			$('#' + this.id).modal('hide');
		}
	},
	
	template: `
		<div class="modal fade" :id="id" tabindex="-1">
			<div :class="'modal-dialog ' + size" :style="topMargin ? 'margin-top: ' + topMargin : ''">
				<div class="modal-content">
					<div class="modal-header modal-title webutils-modal-header">
						{{dataTitle ? dataTitle : title}}
						
						<button class="modal-close-button" data-bs-dismiss="modal">
							<i class="fa-solid fa-xmark"></i>
						</button>
					</div>
					
					
					<div class="modal-body">
						<slot></slot>
					</div>
					
					<div class="modal-footer" style="padding: 0.1rem">
						<button type="button" class="btn btn-primary webutil-button" v-if="submitText" @click="$emit('submit')">{{submitText}}</button>
						<button type="button" class="btn btn-danger webutil-button" data-bs-dismiss="modal">{{closeText}}</button>
					</div>
				</div>
			</div>
		</div>
	`
};

dialogComponents['yk-model-form-dialog'] = {
	"props": {
		"id": String,
		"title": String,
		
		"method": { "type": String, "required": true },
		"url": { "type": String, "required": true },

		"size": {
			"type": String,
			"default": "modal-xl"
		},
		"modelName": { "type": String, "required": true },
		"columnCount": { "type": Number, "default": 2 }
	},
	
	"data": function() {
		return {
			"modelFieldRows": [
			],
			"columnClass": "col-md-6",
			"formData": {
				"data": {},
				"errorFields": [],
				"displayErrors": false
			},
			"saveLabel": "Save",
			"formSubmitted": false,
			
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
		
		"displayForEdit": function(data, callback)
		{
			this.formSubmitted = false;
			
			this.reset(data);
			this.display(callback);
			this.saveLabel = "Update";
		},
		
		"close": function() 
		{
			$('#' + this.id).modal('hide');
		},

		"setFormData": function(modelDef) {
			$utils.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
		},
		
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
	
		"submitSuccess": function(result) {
			this.formSubmitted = false;
			this.$emit('submit', true, result);
			$modalManager.closeModal(this.id);
		},
		
		"submitFailed": function(result) {
			this.formSubmitted = false;
			
			if(!result.response)
			{
				result.response = {"message": "Failed to contact server."};
			}
			
			this.$emit('submit', false, result);
		},
		
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

dialogComponents['yk-dialogs'] = {
	data: function() {
		return  {
			"alertMessage": "",
			
			"inputMessage": "",
			"inputValue": null,
			"inputCallback": null,
			
			"confirmMessage": "",
			"confirmResult": false,
			"confirmCallback": null,
			
			// List of functions being executed with in-progress dialog
			"inProgressFunctions": [],
			"inProgressDisplayed": false, // Flag indicating if in-progress dialog is displayed
		};
	},
	
	"methods": {
		"processMessage": function(message) {
			message = $.isArray(message) ? $utils.format(message[0], message, 1) : message;
			message = message.replace(/\n/g, "<br/>");

			return message;			
		},
		
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
		
		"closeAlert": function() {
			$('#webutilsAlertDialog').modal('hide');
		},
		
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

		"closeInput": function(res) {
			if(!res) {
				this.inputValue = null;
			}
			
			$('#webutilsInputDialog').modal('hide');
		},
		
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
		
		"closeInfo": function() {
			$('#webutilsInfoBox').css("display", "none");
		},
		
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
		
		"closeConfirm": function(res)
		{
			this.confirmResult = res;
			$('#webutilsConfirmDialog').modal('hide');
		},
		
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
