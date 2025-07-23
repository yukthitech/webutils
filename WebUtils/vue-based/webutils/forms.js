import {$restService} from "./rest-service.js";
import {$utils} from "./common.js";
import {$modelDefService} from "./model-def-service.js";

export var formComponents = {};

formComponents['yk-form'] = {
	"props": {
		
		/**
		 * http method to be used
		 */
		"method": String,
		
		/**
		 * url to be submitted to
		 */
		"url": String,
		
		/**
		 * form-data to be populated as initial values
		 * and where input data will be maintained
		 */
		"formData": {
			type: Object,
			"required": true
		},
		
		"validationActivated": {
			"default": false
		}
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
};

formComponents['yk-search-form'] = {
	"props": {
		//Name of the query to be displayed
		"queryName": { "type": String, "required": true },
		"columnCount": { "type": Number, "default": 2 },
		"simpleSearch": { "type": Boolean, "default": false }
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
		$restService.invokeGet(
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
			$utils.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
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
			var url = this.simpleSearch ? "/api/search/search/" : "/api/search/execute/";
			
			$restService.invokeGet(
					url + this.queryName, 
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
			$utils.info("Search failed with error: " + result.response.message);
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
			
			<div style="width: 100%; text-align: right; margin-top: 0.2rem;">
				<button type="button" class="btn btn-primary webutil-button" @click="search">Search</button>
			</div>
		</div>
	`
};

formComponents['yk-search-results'] = {
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
		},

		"onDoubleClick": function(row) {
			this.$emit("double-click", row.dataMap);
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
					<tr :key="row.index" :rowid="row.rowId" v-for="row in rows" @click="selectRow(row)" @dblclick="onDoubleClick(row)">
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
};

formComponents['yk-model-form'] = {
	"props": {
		"modelName": { "type": String, "required": true },
		"columnCount": { "type": Number, "default": 1 },
		
		/**
		 * Custom style that can be applied to top level div layer.
		 */
		"style": { "type": String, "required": false, "default": ""},
		
		/**
		 * Flag indicating if the authentication context should be used.
		 * If set to true, auth will not be used while fetching common info
		 * like LOV, model-def, etc.
		 */
		"noAuth": { "type": Boolean, "default": false },
		
		/**
		 * This should be an array of group objects. Each object should have
		 * 		label, array of field names part of this group, default flag indicating if this is default group
		 * 
		 * Fields which are not falling under any group will be added to default group. The last
		 * group which is marked as default will be taken as default group.
		 * 
		 * If no default group is present, for extra fields a default group will be added at end.
		 * 
		 * Default: All fields will be considered as one group.
		 */
		"groups": { "type": Array},

		/**
		 * Used to set initial value for the field.
		 * This also helps in 2-way binding with parent fields using v-model
		 */
		"modelValue": {}
	},
	
	"data": function() {
		return {
			"modelFieldGroups": [
			],
			"columnClass": "col-md-6",
			
			"fieldChangeListeners": {},
			
			"formStatusListener": null,
			
			/**
			 * Maintains the form data from all fields.
			 */
			"formData": {},
			
			/**
			 * Flag which controls if errors has to be displayed at field level or not.
			 * Till the form is evaluated no errors will be displayed.
			 */
			"displayErrors": false,
			
			/**
			 * Flag indicating if mount is completed. Only after mount during data
			 * model change event is fired.
			 */
			"mountCompleted": false
		}
	},

	"created": function() {
		
		if(this.modelValue) {
			this.formData = this.modelValue;
		}
		
		$restService.fetchModelDef(this.modelName, $.proxy(this.setModelDef, this), this.noAuth);
		
		var colSize = 12 / this.columnCount;
		this.columnClass = "col-md-" + colSize;
	},
	
	"mounted": function() 
	{
		this.mountCompleted = true;
	},
	
	"updated": function() {
		//for cross dependent fields, populate watchers
		for(let group of this.modelFieldGroups)
		{
			for(let row of group.rows)
			{
				for(let fld of row.fields)
				{
					if(!fld.lovDetails || !fld.lovDetails.parentField)
					{
						continue;
					}
					
					let refFld = this.$refs["field_" + fld.index][0];
					
					let parentDetails = refFld.getParentDetails();
					
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
		}
	},
	
	"methods":
	{
		"setModelDef": function(modelDef) {
			modelDef = $utils.deepClone(modelDef);
			// Divide fields into groups and rows
			$modelDefService.divideModelGroups(modelDef, this.modelFieldGroups, this.columnCount, this.groups);
		},

		"onFieldValueChange": function(newVal, fieldInfo)
		{
			if(this.fieldChangeListeners[fieldInfo.name])
			{
				var listeners = this.fieldChangeListeners[fieldInfo.name];
	
				for(var i = 0; i < listeners.length; i++)
				{
					listeners[i](newVal);
				}
			}
			
			if(!this.mountCompleted)
			{
				return;
			}
			
			let errFld = this.getFirstErrorField();
			let formHasErrors = (errFld != null);
			
			if(this.formStatusListener) {
				this.formStatusListener(formHasErrors);
			}
		},
		
		"raiseModelChangeEvent": function() {
			this.$nextTick($.proxy(function(){
				this.$emit('update:modelValue', this.formData);
			}, this));
			
		},
		
		"setServerErrors": function(errors) {
			let errMap = {};
			
			for(let err of errors) {
				errMap[err.field] = err;
			}
			
			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						if(errMap[fld.name]) {
							this.$refs["field_" + fld.index][0].setServerError(errMap[fld.name]);
						}
					}
				}
			}
			
			$(this.$el).find("[name=" + errors[0].field + "]").focus();
		},
		
		"setFormStatusListener": function(listener) {
			this.formStatusListener = listener;
		},
		
		/**
		 * Checks if any field in the form is having error. If it does,
		 * this method returns the first field with error.
		 */
		"getFirstErrorField": function() {
			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						// return if the form is not displayed yet
						if(!this.$refs["field_" + fld.index]) {
							return null;	
						}
						
						let fldRef = this.$refs["field_" + fld.index][0];
						let err = fldRef.getError();
						
						if(err) {
							return fld.name;
						}
					}
				}
			}
			
			return null;
		},
		
		/**
		 * Evaluates the form for errors. In case errors are found
		 * error display gets enabled and an exception is thrown.
		 */
		"evaluateForm": function()
		{
			let errFld = this.getFirstErrorField();
			
			if(errFld) {
				this.displayErrors = true;
				$(this.$el).find("[name=" + errFld + "]").focus();
				throw "Error field: " + errFld;
			}
			
			return this.formData;
		},
	},
	
	template: `
		<div :style="style">
			<div>
				<slot name="header"></slot>
			</div>
			
			<div class="webutils-group" v-for="group in modelFieldGroups">
				<div v-if="group.label" class="webutls-group-label">{{group.label}}</div>
					
				<div :key="row.index" class="row" v-for="row in group.rows">
					<div :class="columnClass" v-for="field in row.fields">
						<component
							:ref="'field_' + field.index"
							
							:field="field"
							v-model="formData[field.name]"
							:enableError="displayErrors"
							
							:key="field.index"
							:is="field.componentType"
							:no-auth="noAuth"
							:empty-option="'Select ' + field.label"
							@value-changed="onFieldValueChange"
							@update:modelValue="raiseModelChangeEvent"
							/>
					</div>
				</div>
			</div>
			
			<div>
				<slot name="footer"></slot>
			</div>
		</div>
	`
};

formComponents['yk-multi-row-model-form'] = {
	"props": {
		/**
		 * Name of the model for the rows.
		 */
		"modelName": { "type": String, "required": true },
		
		/**
		 * Object to be used while adding default row.
		 */
		"defaultValue": {"type": Object, "required": false},
		
		/**
		 * Property to be executed on row to fetch label that
		 * in turn will be used during confirm box display during
		 * deletion.
		 */
		"labelProp": { "type": String, "required": true },
		
		/**
		 * Can be used to set initial rows for the field.
		 * This also helps in 2-way binding with parent fields using v-model
		 */
		"modelValue": {"type": Array, "required": false},
	},
	
	"data": function() {
		return {
			"rows": [],
			"defaultRow": null,
			"idCounter": 1,
		}
	},

	"created": function() {
		this.defaultRow = this.defaultValue;
		
		if(!this.defaultRow)
		{
			this.defaultRow = {};
		}
		
		if(this.modelValue)
		{
			for(let row of this.modelValue)
			{
				let newRow = $utils.deepClone(row);
				newRow._id = this.idCounter;
				this.idCounter++;
				
				this.rows.push(newRow);
			}
		}
		
		if(this.rows.length == 0)
		{
			this.addNewRow();
		}
	},
	
	"methods":
	{
		"onValueChange": function() {
			let clonedRows = $utils.deepClone(this.rows);
			
			for(let row of clonedRows)
			{
				delete row._id;
			}
			
			this.$emit("update:modelValue", clonedRows);
		},
		
		"onModelChange": function(newVal) {
			this.$nextTick($.proxy(this.onValueChange, this));
		},
		
		"addNewRow": function() {
			let newRow = $utils.deepClone(this.defaultRow);
			newRow._id = this.idCounter;
			this.idCounter++;
			
			this.rows.push(newRow);
			this.onValueChange();
		},
		
		"removeRow": function(row) {
			let label = '<none>';
			
			try
			{
				label = eval("row." + this.labelProp);
			}catch(ex)
			{
				// ignore
			}
			
			$utils.confirm("Are you sure you want to remove entry: " + label, $.proxy(function(accepted) {
				
				if(!accepted)
				{
					return;
				}
				
				let idx = this.rows.indexOf(row);
				
				if(idx >= 0) {
					this.rows.splice(idx, 1);
					this.onValueChange();
				}
			}, this));
		}
	},
	
	template: `
		<div>
			<div style="border-radius: 5px; border: 1px ridge rgb(200, 200, 200); padding: 5px 10px 40px 5px;">
				<!--
					:key is important for vue.js to ensure right row is removed during deletion. 
				 -->
				<div v-for="row in rows" :key="row._id">
				
					<yk-model-form :model-name="modelName" :column-count="4" v-model="row"
						:style="'padding: 0px 10px 1px 10px;background-color: rgb(240, 240, 240);margin: 5px 0px;border-radius: 10px; position: relative;'"
						@update:modelValue="onModelChange">
			    		<template #header>
							<div style="position: absolute; top: -5px; right: 5px;">
								<span class="remove-button" title="Delete" style="font-size: 1.2rem;" @click="removeRow(row)">x</span>
					        </div>
			    		</template>
					</yk-model-form>
					
				</div>
				
				<button class="webutils-btn-primary" style="float: right; margin-top: 3px;" @click="addNewRow()">Add Entry</button>
			</div>
		</div>
	`
};
