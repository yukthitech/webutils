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
				"/api/search/" + this.queryName + "/query/def", 
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
			var modelDef = result.response.value || result.response.modelDef;
			$modelDefService.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
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
				<button :id="'yk-search-submit-' + queryName" type="button" class="btn btn-primary webutil-button" @click="search">Search</button>
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
		"modelValue": {},

		/**
		 * Enable error display for the form. Generally set to true when form is submitted.
		 */
		"enableError": {"type": Boolean, default: false},

		/**
		 * Server error message for the form.
		 */
		"serverErrors": {"type": Object, default: null},
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
			 * Flag indicating if mount is completed. Only after mount during data
			 * model change event is fired.
			 */
			"mountCompleted": false,
			
			/**
			 * Flag to prevent recursive updates when modelValue changes externally
			 */
			"updatingFromExternal": false
		}
	},

	"watch": {
		"modelValue": function(newVal, oldVal) {
			//console.log("Form model value changed to: ", newVal, oldVal);
			if(newVal) {
				this.updatingFromExternal = true;
				this.formData = $utils.deepClone(newVal);
				this.$nextTick(() => {
					this.updatingFromExternal = false;
				});
			}
		},

		"serverErrors": function(newVal) {
			// Focus on first error field
			if(!newVal) {
				return;
			}

			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						if(newVal[fld.name]) {
							$(this.$el).find("[name=" + fld.name + "]").focus();
							break;
						}
					}
				}	
			}
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
			// Don't emit if we're currently updating from external changes
			if(this.updatingFromExternal) {
				return;
			}
			
			this.$nextTick($.proxy(function(){
				this.$emit('update:modelValue', this.formData);
			}, this));
			
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
		 * Runs validate() on every field in the form. Used by custom layouts and multi-row nested forms.
		 * @returns {boolean} true if all fields are valid
		 */
		"validateAllFields": function() {
			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						if(!this.$refs["field_" + fld.index]) {
							continue;
						}
						let fldRef = this.$refs["field_" + fld.index][0];
						if(fldRef.validate && !fldRef.validate()) {
							return false;
						}
					}
				}
			}
			return true;
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
					<div :class="field.fullWidth ? 'col-md-12' : columnClass" v-for="field in row.fields">
						<component
							:ref="'field_' + field.index"
							
							:field="field"
							v-model="formData[field.name]"
							:enableError="enableError"
							:server-error="serverErrors ? serverErrors[field.name] : null"
							
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

		/**
		 * Enable error display for the form. Generally set to true when form is submitted.
		 */
		"enableError": {"type": Boolean, default: false},

		/**
		 * Server error messages for the rows.
		 */
		"serverErrors": {"type": Array, default: null},
	},
	
	"data": function() {
		return {
			"rows": [],
			"defaultRow": null,
			"idCounter": 1,
		}
	},

	"watch": {
		"modelValue": function(newVal, oldVal) {
			// Clear existing rows
			this.rows.splice(0, this.rows.length);
			
			// Reset ID counter to avoid conflicts
			this.idCounter = 1;
			
			// Add new rows from modelValue
			if(newVal && Array.isArray(newVal)) {
				for(let row of newVal) {
					let newRow = $utils.deepClone(row);
					newRow._id = this.idCounter;
					this.idCounter++;
					
					this.rows.push(newRow);
				}
			}
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
	},
	
	"methods":
	{
		"getRowServerError": function(rowIndex) {
			if(!this.serverErrors || !Array.isArray(this.serverErrors))
			{
				return null;
			}

			// Backward compatibility: direct row-index array with error object per position.
			let directErr = this.serverErrors[rowIndex];
			if(directErr && (directErr.index == null || typeof(directErr.error) == "undefined"))
			{
				return directErr;
			}

			for(let errRow of this.serverErrors)
			{
				if(!errRow || errRow.index != rowIndex)
				{
					continue;
				}

				return errRow.error ? errRow.error : null;
			}

			return null;
		},

		"onValueChange": function() {
			let clonedRows = $utils.deepClone(this.rows);
			
			for(let row of clonedRows)
			{
				delete row._id;
			}
			
			this.$emit("update:modelValue", clonedRows);
		},
		
		"onModelChange": function(rowIndex, newVal) {
			if(rowIndex < 0 || rowIndex >= this.rows.length || !newVal)
			{
				return;
			}
			
			let existingRow = this.rows[rowIndex];
			let updatedRow = $utils.deepClone(newVal);
			updatedRow._id = existingRow._id;
			
			// Keep same row reference to avoid resetting nested input state on first selection.
			for(let prop in existingRow)
			{
				if(prop == "_id")
				{
					continue;
				}
				
				if(!(prop in updatedRow))
				{
					delete existingRow[prop];
				}
			}
			
			for(let prop in updatedRow)
			{
				existingRow[prop] = updatedRow[prop];
			}
			
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
		},

		/**
		 * Validates each nested row form (yk-model-form per row).
		 * @returns {boolean} true if all rows are valid or there are no rows
		 */
		"validateAllRows": function() {
			if(!this.rows.length) {
				return true;
			}
			let refs = this.$refs.nestedRowForm;
			let forms = Array.isArray(refs) ? refs : (refs ? [refs] : []);
			for(let i = 0; i < forms.length; i++) {
				if(forms[i].validateAllFields && !forms[i].validateAllFields()) {
					return false;
				}
			}
			return true;
		}
	},
	
	template: `
		<div class="webutils-multi-row-model-form">
			<div style="border-radius: 5px; border: 1px ridge rgb(200, 200, 200); padding: 15px 20px 50px 20px;">
				<div v-if="rows.length === 0" class="webutils-multi-row-empty-placeholder" style="display: flex; align-items: center; justify-content: center; min-height: 80px; color: #9a9a9a; font-size: 14px; text-align: center; padding: 8px 16px 16px;">
					No entries yet. Use Add Entry to add one.
				</div>
				<!--
					:key is important for vue.js to ensure right row is removed during deletion. 
				 -->
				<div v-for="(row, rowIndex) in rows" :key="row._id" class="webutils-multi-row-model-form-row">
				
					<yk-model-form ref="nestedRowForm" :model-name="modelName" :column-count="4" :model-value="row" :enable-error="enableError" 
						:server-errors="getRowServerError(rowIndex)"
						:style="'margin: 5px 0px; border-radius: 10px; position: relative; border: 1px solid #c0c0c0; box-shadow: 0 2px 6px rgba(0,0,0,0.10);'"
						@update:modelValue="onModelChange(rowIndex, $event)">
			    		<template #header>
							<div style="position: absolute; top: 5px; right: 10px; z-index: 10;">
								<button type="button" class="webutils-remove-button" title="Delete this entry" @click="removeRow(row)" aria-label="Delete">
									<span class="remove-icon">×</span>
								</button>
					        </div>
			    		</template>
					</yk-model-form>
					
				</div>
				
				<button class="webutils-btn-primary" style="float: right; margin-top: 3px;" @click="addNewRow()">Add Entry</button>
			</div>
		</div>
	`
};
