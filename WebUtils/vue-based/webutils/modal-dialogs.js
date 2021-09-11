$.newVueComponent = function(name, vueData){
	var defData = {
		"props": {
			"field": {
			},
			"formData": {
				"required": true
			},
			
			"name": { "type": String, "default": "" },
			"label": { "type": String, "default": "" },
			"dataType": { "type": String},
			
			"required": { "type": Boolean, "default": false },
			"requiredMessage": { "type": String, "default": "Value is mandatory" },
	
			"minLen": { "type": Number, "default": -1 },
			"minLenMessage": { "type": String, "default": "Min length of value should be ${config.value}" },
			
			"maxLen": { "type": Number, "default": -1 },
			"maxLenMessage": { "type": String, "default": "Max length of value can be ${config.value}" },
	
			"pattern": String,
			"patternMessage": { "type": String, "default": "Value is not matching with required pattern" }
		},

		"watch": {
			"fieldValue": function(newVal, oldVal)
			{
				this.validateAndSetValue(newVal);
			}
		},

		"methods": {
			"reset": function(val) {
				this.fieldValue = val ? val : "";
			},
			
			"buildFieldInfo": function() {
				if(this.field)
				{
					for(var fld in this.field)
					{
						this.fieldInfo[fld] = this.field[fld];
					}
				}
				
				if(this.name && this.name.length > 0)
				{
					this.fieldInfo.name = this.name;
				}
		
				if(this.dataType && this.dataType.length > 0)
				{
					this.fieldInfo.dataType = this.dataType;
				}
				
				if(this.inputType && this.inputType.length > 0)
				{
					this.fieldInfo.inputType = this.inputType;
				}
		
				if(!this.fieldInfo.dataType)
				{
					this.fieldInfo.dataType = "STRING";
				}
				
				if(!this.fieldInfo.inputType)
				{
					this.fieldInfo.inputType = "text";
				}
		
				if(this.label && this.label.length > 0)
				{
					this.fieldInfo.label = this.label;
				}
				
				if(!this.fieldInfo.validations)
				{
					this.fieldInfo.validations = [];
				}
				
				if(this.minLen > 0)
				{
					this.fieldInfo.validations.push({
						"name": "minLength",
						"config": {"value": this.minLen},
						"message": this.minLenMessage
					});
				}
		
				if(this.maxLen > 0)
				{
					this.fieldInfo.validations.push({
						"name": "maxLength",
						"config": {"value": this.maxLen},
						"message": this.maxLenMessage
					});
				}
		
				if(this.pattern && this.pattern.length > 0)
				{
					this.fieldInfo.validations.push({
						"name": "pattern",
						"config": {"regexp": this.pattern},
						"message": this.patternMessage
					});
				}
				
				if(this.required)
				{
					this.fieldInfo.validations.push({
						"name": "required",
						"config": {},
						"message": this.requiredMessage
					});
				}
		
				this.validateAndSetValue(this.formData.data[this.fieldInfo.name]);
			},
			
			"getColSize": function() {
				var colCount = ('size' in this.fieldInfo) ? this.fieldInfo.size : 12;
				return "col-md-" + colCount;
			},
			
			"displayError": function() {
				return this.fieldInfo.error && this.formData.displayErrors;
			},
			
			"validateAndSetValue": function(newVal) {
				this.formData.data[this.fieldInfo.name] = newVal;
				this.fieldInfo.error = null;
				
				try
				{
					$.validationService.validate(
							this.fieldInfo.dataType,
							this.fieldInfo.validations, 
							newVal, this.data);
					
					$.utils.removeArrElement(this.formData.errorFields, this.fieldInfo.name);
				}catch(err)
				{
					if(err.message)
					{
						this.fieldInfo.error = err.message;
					}
					else
					{
						console("Error: ", err);
						this.fieldInfo.error = ""  + err;
					}
					
					if(this.formData.errorFields.indexOf(this.fieldInfo.name) < 0)
					{
						this.formData.errorFields.push(this.fieldInfo.name);
					}
				}
				
				this.$forceUpdate();
			}
		}
	};
	
	var mergeSubcomponent = function(source, dest, propName)
	{
		if(!dest[propName])
		{
			dest[propName] = source[propName];
		}
		else
		{
			for(var attr in source[propName])
			{
				if(dest[propName][attr])
				{
					continue;
				}
				
				dest[propName][attr] = source[propName][attr];
			}
		}
	};
	
	var newDefData = defData; 
	newDefData.props = $.utils.deepClone(newDefData.props);
	
	mergeSubcomponent(defData, vueData, 'props');
	mergeSubcomponent(defData, vueData, 'methods');
	vueData.watch = defData.watch;
	
	return Vue.component(name, vueData);
}

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
var ykInputField = $.newVueComponent('yk-input-field', {
	"data": function() {
		return {
			"fieldInfo": {},
			"fieldValue": ""
		}; 
	},
	
	"created": function() {
		this.buildFieldInfo();
	},
	
	"template": `
		<div :class="getColSize()">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<input
				:name="fieldInfo.name"
				:type="fieldInfo.inputType" 
				class="form-control webutil-field" 
				:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
				v-model="fieldValue"
				:class="{'is-invalid': displayError()}"
				/>
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

var ykTextareaField = $.newVueComponent('yk-textarea-field', {
	"data": function() {
		return {
			"fieldInfo": {},
			"fieldValue": ""
		}; 
	},
	
	"created": function() {
		this.buildFieldInfo();
	},
	
	"template": `
		<div :class="getColSize()">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<textarea
				:name="fieldInfo.name"
				class="form-control webutil-field" 
				rows="3"
				:placeholder="fieldInfo.placeHolder ? fieldInfo.placeHolder : fieldInfo.label" 
				v-model="fieldValue" 
				:class="{'is-invalid': displayError()}"
				></textarea
			<div class="invalid-feedback">{{fieldInfo.error}}</div>
		</div>
	`
});

var yLovField = $.newVueComponent('yk-lov-field', {
	"data": function() {
		return {
			"lovOptions": [],
			"fieldInfo": {},
			"fieldValue": ""
		}; 
	},
	
	"created": function() {
		this.buildFieldInfo();

		$.restService.fetchLovValues(this.field.lovDetails.lovName, this.field.lovDetails.lovType, this.setLovValues);
		this.validateAndSetValue(this.formData.data[this.fieldInfo.name]);
	},
	
	"updated": function() {
		this.$nextTick(function () {
		    // Code that will run only after the entire view has been re-rendered
			var selectElem = $(this.$el).find("select");
			$(selectElem).selectpicker();
			
			var enclosingDiv = $(this.$el).find("div.dropdown");
			$(enclosingDiv).addClass('form-control');
			
			$(enclosingDiv).find("li").addClass("webutil-dropdown-item");
		});
	},
	
	"methods": {
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
			
			for(var lov of lovList)
			{
				this.lovOptions.push(lov);
			}
		}
	},

	"template": `
		<div :class="getColSize()">
			<label class="webutil-field-label form-label">{{fieldInfo.label}}:</label>
			<select 
				class="selectpicker"
				data-style="webutil-dropdown"
				data-live-search="true"
				
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

var ykForm = Vue.component('yk-form', {
	"props": {
		"method": String,
		"url": String,
		
		"formData": {
			"required": true
		},
		
		"validationActivated": {
			"default": false
		}
	},
	
	"components": {
		"yk-input-field": ykInputField
	},
	
	"methods":
	{
		"submitForm": function()
		{
			if(this.formData.errorFields.length > 0)
			{
				$(this.$el).find("[name=" + this.formData.errorFields[0] + "]").focus();
				this.formData.displayErrors = true;
				this.$emit('submit', false);
				return;
			}
			
			if(this.method == "POST")
			{
				$.restService.invokePost(
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
			this.$emit('submit', true, result);			
		},
		
		"submitFailed": function(result) {
			if(!result.response)
			{
				result.response = {"message": "Failed to contact server."};
			}
			
			this.$emit('submit', false, result);
		}
	},
	
	template: `
		<div style="width: 100%;">
			<slot></slot>
		</div>
	`
});

var ykSearchForm = Vue.component('yk-search-form', {
	"props": {
		//Name of the query to be displayed
		"queryName": { "type": String, "required": true },
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
			"searchPerformed": false
		}
	},
	
	"created": function() {
		$.restService.invokeGet(
				"/api/search/fetch/" + this.queryName + "/query/def", 
				null,
				{
					"context": this, 
					"onSuccess": this.setFormData 
				}
			);
		
		var colSize = 12 / this.columnCount;
		this.columnClass = "col-md-" + colSize;
	},
	
	"methods":
	{
		"setFormData": function(result) {
			var modelDef = result.response.modelDef;
			$.utils.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
		},
		
		"refreshSearch": function() {
			//if serarch is not performed yet, return
			if(!this.searchPerformed)
			{
				return;
			}
			
			this.search();
		},
		
		"search": function() {
			this.searchPerformed = true;
			this.formData.displayErrors = true;
			
			if(this.formData.errorFields.length > 0)
			{
				return;
			}
			
			var searchCriteria = JSON.stringify(this.formData.data);

			$.restService.invokeGet(
					"/api/search/execute/" + this.queryName, 
					{"queryModelJson": searchCriteria},
					{
						"context": this, 
						"onSuccess": this.searchResults 
					}
				);
		},
		
		"searchResults": function(result)
		{
			this.$emit("search", result.response);
		},
		
		"searchResultsError": function(result)
		{
			$.utils.info("Search failed with error: " + result.response.message);
		}
	},
	
	template: `
		<div class="webutils-search-box">
			<div :key="row.index" class="row" v-for="row in modelFieldRows">
	 			<component
	 				:key="field.index"
	 				:is="field.componentType"
	 				:formData.sync="formData"
	 				:field="field"
	 				
	 				v-for="field in row.fields"
	 				/>
			</div>
			
			<div style="width: 100%; text-align: right;">
				<button type="button" class="btn btn-primary webutil-button" @click="search">Search</button>
			</div>
		</div>
	`
});

var ykSearchResults = Vue.component('yk-search-results', {
	"data": function() {
		return {
			"headings": [],
			"rows": [],
			"rowCount": -1,
			"hasRows": false,
			"searchExecuted": false,
			"searchResult": null,
			
			"lastSelectedRow": -1
		}
	},
	
	"methods":
	{
		"setSearchResults": function(searchResult) {
			this.headings.splice(0, this.headings.length);
			this.rows.splice(0, this.rows.length);
			this.searchResult = searchResult;
			this.lastSelectedRow = -1;
			
			this.rowCount = searchResult.searchResults.length;
			this.hasRows = (this.rowCount > 0);
			this.searchExecuted = true;
			
			var colIdx = 0;
			
			for(var col of searchResult.searchColumns)
			{
				if(!col.displayable)
				{
					continue;
				}
				
				this.headings.push({"index": "heading-" + colIdx, "value": col.heading});
				colIdx++;
			}
			
			var rowIdx = 0;

			for(var row of searchResult.searchResults)
			{
				var searchRow = [];
				var colIdx = 0;
				var searchObj = {};
				
				for(var cellVal of row.data)
				{
					searchObj[searchResult.searchColumns[colIdx].name] = cellVal;
					
					if(!searchResult.searchColumns[colIdx].displayable)
					{
						colIdx++;
						continue;
					}

					searchRow.push({"index": rowIdx + "-" + colIdx, "value": cellVal});
					colIdx++;
				}
				
				this.rows.push({"index": "row-" + rowIdx, "rowId": "" + rowIdx, "data": searchRow, "dataMap": searchObj});
				rowIdx++;
			}
			
			//remove previous selections
			var lastElem = $(this.$el).find("tr.selected");
			$(lastElem).removeClass("selected");
			
		},
		
		"selectRow": function(row) {
			var idx = row.rowId;
			
			if(this.lastSelectedRow >= 0)
			{
				var lastElem = $(this.$el).find("tr[rowid='" + this.lastSelectedRow + "']");
				$(lastElem).removeClass("selected");
			}
			
			var selectElem = $(this.$el).find("tr[rowid='" + idx + "']");
			$(selectElem).addClass("selected");
			this.lastSelectedRow = parseInt(idx);
			
			this.$emit("select", row.dataMap);
		}
	},
	
	template: `
		<div class="webutils-search-results-container">
			<div class="content" v-if="searchExecuted &amp;&amp; hasRows">
				<table class="webutils-search-results">
					<tr>
						<th :key="heading.index" v-for="heading in headings">
							{{heading.value}}
						</th>
					</tr>
					<tr :key="row.index" :rowid="row.rowId" v-for="row in rows" @click="selectRow(row)">
						<td :key="cell.index" v-for="cell in row.data" v-html="cell.value">
						</td>
					</tr>
				</table>
			</div>
			<div class="footer" v-if="hasRows">
				Total Result Count: {{rowCount}} 
			</div>
			<div class="footer" v-if="!searchExecuted">
				No search is executed yet.
			</div>
			<div class="footer" v-if="searchExecuted &amp;&amp; !hasRows">
				No records found with given criteria.
			</div>
		</div>
	`
});

var ykModalDialog = Vue.component('yk-modal-dialog', {
	"props": {
		"id": String,
		"title": String,
		"submitText": String,
		
		"size": {
			"type": String,
			"default": "modal-xl"
		}
	},
	
	"methods":
	{
		"display": function(callback)
		{
			$.modalManager.openModal(this.id, {
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
		<div class="modal fade" :id="id" tabindex="-1" aria-hidden="true">
			<div :class="'modal-dialog ' + size">
				<div class="modal-content">
					<div class="modal-header modal-title">
						{{title}}
					</div>
					
					<div class="modal-body">
						<slot></slot>
					</div>
					
					<div class="modal-footer" style="padding: 0.1rem">
						<button type="button" class="btn btn-primary webutil-button" @click="$emit('submit')">{{submitText}}</button>
						<button type="button" class="btn btn-danger webutil-button" data-bs-dismiss="modal">Cancel</button>
					</div>
				</div>
			</div>
		</div>
	`
});

var ykModelFormDialog = Vue.component('yk-model-form-dialog', {
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
	
	"components": {
		"yk-input-field": ykInputField
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
			"formSubmitted": false
		}
	},

	"created": function() {
		$.restService.invokeGet(
				"/api/models/fetch/" + this.modelName, 
				null,
				{
					"context": this, 
					"onSuccess": this.setFormData 
				}
			);
		
		var colSize = 12 / this.columnCount;
		this.columnClass = "col-md-" + colSize;
	},

	"methods":
	{
		"display": function(callback)
		{
			this.saveLabel = "Save";
			this.formSubmitted = false;
			
			$.modalManager.openModal(this.id, {
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

		"setFormData": function(result) {
			var modelDef = result.response.modelDef;
			$.utils.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
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
				$.restService.invokePost(
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
		<div class="modal fade" :id="id" tabindex="-1" aria-hidden="true">
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
});

var ykDialogs = Vue.component('yk-dialogs', {
	data: function() {
		return  {
			"alertMessage": "",
			
			"confirmMessage": "",
			"confirmResult": false,
			"confirmCallback": null
		};
	},
	
	"created": function() {
		$.utils.ykDialogs = this;
	},
	
	"methods": {
		"displayAlert": function(message, callback) {
			message = $.isArray(message) ? $.utils.format(message[0], message, 1) : message;
			message = message.replace(/\\n/g, "<br/>");
			
			this.alertMessage = message;
			
			$.modalManager.openModal("webutilsAlertDialog", {
				context: {"callback": callback},
				
				//on show, highlight okay button
				onShow: function() {
					$('#webutilsAlertDialog .btn-primary').focus();
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
		
		"displayInfo": function(message) {
			message = $.isArray(message) ? $.utils.format(message[0], message, 1) : message;
			
			//set content and display			
			$("#webutilsInfoBox .content").html(message);
			$("#webutilsInfoBox").css("display", "block");
			
			//get timeout period
			var time = $.appConfiguration.infoTimeOutSec ? $.appConfiguration.infoTimeOutSec : 5;
			
			//auto timer after which info should auto close
			setTimeout(function() {
				$("#webutilsInfoBox").css("display", "none");
			}, (time * 1000));
		},
		
		"closeInfo": function() {
			$('#webutilsInfoBox').css("display", "none");
		},
		
		"displayConfirm": function(message, callback) {
			message = $.isArray(message) ? $.utils.format(message[0], message, 1) : message;
			message = message.replace(/\\n/g, "<br/>");
			
			this.confirmMessage = message;
			this.confirmCallback = callback;
			this.confirmResult = false;
			
			$.modalManager.openModal("webutilsConfirmDialog", {
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
		}
	},
	
	template: `
		<div>
			<div class="modal fade" id="webutilsAlertDialog" tabindex="-1" role="dialog" data-backdrop="static">
				<div class="modal-dialog" role="document" style="max-width: 50em;">
					<div class="modal-content">
						<!-- Content -->
						<div class="modal-body" style="padding: 1.5em;" v-html="alertMessage">
						</div>
						
						<!-- Footer -->
						<div class="modal-footer" style="padding: 0.5em;">
							<button type="button" class="btn btn-primary action-button" @click="closeAlert">Okay</button>
						</div>
					</div>
				</div>
			</div>

			<div id="webutilsInfoBox" class="webutils-info-message" style="display: none">
				<div class="content" style="width: 100%;">
				</div>
				<button type="button" class="webutils-info-close-button" aria-label="Close" @click="closeInfo">
					<span aria-hidden="true">&times;</span>
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
		</div>
	`
});
