import {$restService} from "./rest-service.js";
import {$utils} from "./common.js";
import {$modelDefService} from "./model-def-service.js";

export var formComponents = {};

/*
formComponents['yk-form'] = {
	"props": {
		
		/**
		 * http method to be used
		 * /
		"method": String,
		
		/**
		 * url to be submitted to
		 * / 
		"url": String,
		
		/**
		 * form-data to be populated as initial values
		 * and where input data will be maintained
		 * /
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
*/

/**
 * @component yk-model-form
 * A powerful component that dynamically generates a complete form based on a server-side model definition.
 * It handles field rendering, layout, client-side validation, and provides hooks for submission and error handling.
 * The component fetches the model's definition from the server upon creation.
 */
formComponents['yk-model-form'] = {
	"props": {
		/**
		 * The name of the model (e.g., `JobSeekerModel`) to generate the form for.
		 * @type {String}
		 * @required
		 */
		"modelName": { "type": String, "required": true },
		/**
		 * The number of columns to use for the form layout.
		 * @type {Number}
		 * @default 1
		 */
		"columnCount": { "type": Number, "default": 1 },
		
		/**
		 * Custom CSS style to be applied to the form's root element.
		 * @type {String}
		 */
		"style": { "type": String, "required": false, "default": ""},
		
		/**
		 * If `true`, fetches the model definition and related data (like LOVs) without sending authentication tokens.
		 * @type {Boolean}
		 * @default false
		 */
		"noAuth": { "type": Boolean, "default": false },
		
		/**
		 * An array of objects to define field grouping and layout.
		 * Each object can have `label` and `fields` (an array of field names).
		 * Fields not specified in a group will be added to a default group at the end
		 * or to the group marked as default. Each object of this array is expected to be
		 * of format - 
		 *    {"label": "default-group", "default": true}
		 *    {"label": "Group-1", fields: ["field1", "field2"]}
		 * @type {Array}
		 */
		"groups": { "type": Array},

		/**
		 * Binds the form's data object using `v-model`, allowing two-way data binding.
		 * @type {Object}
		 */
		"modelValue": {}
	},
	
	"data": function() {
		return {
			/**
			 * An array of field groups, structured for rendering rows and columns.
			 * @type {Array}
			 */
			"modelFieldGroups": [],
			
			/**
			 * The calculated Bootstrap column class (e.g., 'col-md-6').
			 * @type {String}
			 */
			"columnClass": "col-md-6",
			
			/**
			 * A map to hold listener functions for dependent fields (e.g., cascading LOVs).
			 * @type {Object}
			 */
			"fieldChangeListeners": {},
			
			/**
			 * A listener function that gets invoked whenever the form's overall error status changes.
			 * @type {Function}
			 */
			"formStatusListener": null,
			
			/**
			 * The core object that holds the data for all fields in the form.
			 * @type {Object}
			 */
			"formData": {},
			
			/**
			 * A flag to control when validation errors should be displayed. It is set to `true` by `evaluateForm()`.
			 * @type {Boolean}
			 */
			"displayErrors": false,
			
			/**
			 * A flag to prevent events from firing before the component is fully mounted.
			 * @type {Boolean}
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
		/**
		 * Callback function that receives the model definition from the server
		 * and uses the model-def-service to structure it for rendering.
		 * @param {object} modelDef - The model definition object from the server.
		 */
		"setModelDef": function(modelDef) {
			modelDef = $utils.deepClone(modelDef);
			// Divide fields into groups and rows
			$modelDefService.divideModelGroups(modelDef, this.modelFieldGroups, this.columnCount, this.groups);
		},

		/**
		 * Handles the valueChanged event from child input components. It triggers
		 * dependent field logic and notifies the form status listener of any change in error state.
		 * @param {*} newVal - The new value of the field.
		 * @param {object} fieldInfo - The field definition object.
		 */
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
		
		/**
		 * Emits the `update:modelValue` event to ensure two-way data binding with the parent component.
		 */
		"raiseModelChangeEvent": function() {
			this.$nextTick($.proxy(function(){
				this.$emit('update:modelValue', this.formData);
			}, this));
			
		},
		
		/**
		 * Takes an array of field-specific error objects from a server response and displays them
		 * next to the corresponding fields.
		 * @param {Array<object>} errors - An array of error objects, where each object has `field` and `message` properties.
		 */
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
		
		/**
		 * Registers a listener function that is invoked whenever the form's overall error status changes.
		 * @param {Function} listener - The callback function, which will receive a boolean `formHasErrors`.
		 */
		"setFormStatusListener": function(listener) {
			this.formStatusListener = listener;
		},
		
		/**
		 * Checks if any field in the form has a validation error.
		 * @returns {string|null} The name of the first field with an error, or `null` if the form is valid.
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
		 * Evaluates the form for client-side validation errors. If errors are found,
		 * it enables error display and throws an exception. If the form is valid,
		 * it returns the form's data object.
		 * @returns {object} The form's data object.
		 * @throws {string} An error message indicating the first field with an error.
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

/**
 * @component yk-multi-row-model-form
 * A component that helps in accepting one or more model objects of the same type from the user.
 * This widget displays one row by default for accepting a child model object.
 * Users can add, modify, or remove rows.
 * The component uses `v-model` for two-way data binding of the array of model objects.
 */
formComponents['yk-multi-row-model-form'] = {
	"props": {
		/**
		 * The name of the model for each row (e.g., `LanguageDetails`).
		 * @type {String}
		 * @required
		 */
		"modelName": { "type": String, "required": true },
		
		/**
		 * A default object to be used when adding a new, empty row.
		 * @type {Object}
		 * @optional
		 */
		"defaultValue": {"type": Object, "required": false},
		
		/**
		 * The property name in each row object to be used as a label for confirmation dialogs,
		 * especially during deletion.
		 * @type {String}
		 * @required
		 */
		"labelProp": { "type": String, "required": true },
		
		/**
		 * Binds the array of row data using `v-model`, allowing two-way data binding.
		 * @type {Array}
		 * @optional
		 */
		"modelValue": {"type": Array, "required": false},
	},
	
	"data": function() {
		return {
			/**
			 * The internal array that holds the row data, including a unique `_id` for Vue's key binding.
			 * @type {Array}
			 */
			"rows": [],
			
			/**
			 * Holds the cloned `defaultValue` prop for creating new rows.
			 * @type {Object}
			 */
			"defaultRow": null,
			
			/**
			 * A simple counter to generate unique `_id` for new rows.
			 * @type {Number}
			 */
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
		/**
		 * Emits the `update:modelValue` event with the current state of the rows (without the internal `_id`).
		 * This is the primary mechanism for `v-model` to work.
		 */
		"onValueChange": function() {
			let clonedRows = $utils.deepClone(this.rows);
			
			for(let row of clonedRows)
			{
				delete row._id;
			}
			
			this.$emit("update:modelValue", clonedRows);
		},
		
		/**
		 * A handler that calls `onValueChange` on the next tick when a child model form's value changes.
		 * @param {*} newVal - The new value from the child form.
		 */
		"onModelChange": function(newVal) {
			this.$nextTick($.proxy(this.onValueChange, this));
		},
		
		/**
		 * Clones the `defaultRow`, assigns a new unique `_id`, adds it to the `rows` array,
		 * and triggers an update event.
		 */
		"addNewRow": function() {
			let newRow = $utils.deepClone(this.defaultRow);
			newRow._id = this.idCounter;
			this.idCounter++;
			
			this.rows.push(newRow);
			this.onValueChange();
		},
		
		/**
		 * Prompts the user for confirmation before removing a specific row from the `rows` array.
		 * If confirmed, it removes the row and triggers an update event.
		 * @param {Object} row - The row object to be removed.
		 */
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

formComponents['yk-search-form'] = {
	"props": {
		//Name of the query to be displayed
		"queryName": { "type": String, "required": true },
		"columnCount": { "type": Number, "default": 2 },
		
		/**
		 * Flag indicating if the search has to be peformed to 
		 * fetch results in tabular format or as simple objects.
		 * Defaults true, so that the results obtaied in compatible
		 * with "yk-search-results" widget.
		 */
		"tabularSearch": { "type": Boolean, "default": true }
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
			var url = this.tabularSearch ? "/api/search/execute/" : "/api/search/search/";
			
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