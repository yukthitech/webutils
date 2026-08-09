import { newVueUiComponent } from './input-fields.js';
import { $restService } from './rest-service.js';
import { $utils } from './common.js';
import { $validationService } from './validation.js';
import Multiselect from '/lib/vue-multiselect-3.2.0/vue-multiselect.esm.js';

var _multiselectCssLoaded = false;

function ensureMultiselectCss()
{
	if(_multiselectCssLoaded)
	{
		return;
	}

	var href = '/lib/vue-multiselect-3.2.0/vue-multiselect.min.css';
	if(document.querySelector('link[data-yk-multiselect="' + href + '"]'))
	{
		_multiselectCssLoaded = true;
		return;
	}

	var link = document.createElement('link');
	link.rel = 'stylesheet';
	link.href = href;
	link.setAttribute('data-yk-multiselect', href);
	document.head.appendChild(link);
	_multiselectCssLoaded = true;
}

/**
 * Shared LOV fetch / parent-dependency / searchable helpers for all LOV widgets.
 */
var lovSharedBase = {
	"components": {
		"Multiselect": Multiselect
	},

	"props": {
		"staticLovType": { "type": String, "default": "" },
		"dynamicLovName": { "type": String, "default": "" },
		"storedLovName": { "type": String, "default": "" },
		"emptyOption": { "type": String, "default": "" },
		"display": { "type": String, "default": "block" },
	},

	"data": {
		"lovOptions": [],
		"searchable": false,
		"selectedOption": null,
		"selectedOptions": [],
		"parentValue": null,
		"lastFetchedParentValue": undefined,
		"pendingParentFetch": undefined,
	},

	"methods": {
		"onMounted": function()
		{
			ensureMultiselectCss();
		},

		"initLovDetailsFromProps": function()
		{
			if(!this.fieldInfo.lovDetails)
			{
				this.fieldInfo.lovDetails = {};
			}

			if(this.staticLovType && this.staticLovType.length > 0)
			{
				this.fieldInfo.lovDetails.lovType = "STATIC_TYPE";
				this.fieldInfo.lovDetails.lovName = this.staticLovType;
			}
			else if(this.dynamicLovName && this.dynamicLovName.length > 0)
			{
				this.fieldInfo.lovDetails.lovType = "DYNAMIC_TYPE";
				this.fieldInfo.lovDetails.lovName = this.dynamicLovName;
			}
			else if(this.storedLovName && this.storedLovName.length > 0)
			{
				this.fieldInfo.lovDetails.lovType = "STORED_TYPE";
				this.fieldInfo.lovDetails.lovName = this.storedLovName;
			}

			if(!this.fieldInfo.lovDetails.lovType)
			{
				this.fieldInfo.lovDetails.lovType = "STORED_TYPE";
			}
		},

		"isMultiValued": function()
		{
			return !!(this.fieldInfo && this.fieldInfo.multiValued);
		},

		"isTaggable": function()
		{
			if(!this.fieldInfo || !this.fieldInfo.lovDetails)
			{
				return false;
			}

			// Only String editable LOVs can create new values; persist=false disables create in UI
			if(!this.fieldInfo.lovDetails.editableLov)
			{
				return false;
			}

			return this.fieldInfo.lovDetails.persist !== false;
		},

		"refreshSearchable": function()
		{
			this.searchable = this.lovOptions.length > 3;
		},

		"getParentDetails": function()
		{
			if(!this.fieldInfo.lovDetails || !this.fieldInfo.lovDetails.parentField)
			{
				return null;
			}

			return {
				"name": this.fieldInfo.lovDetails.parentField,
				"callback": $.proxy(this.onParentFieldChange, this)
			};
		},

		"clearLovOptions": function()
		{
			if(this.lovOptions.length > 0)
			{
				this.lovOptions.splice(0, this.lovOptions.length);
			}
			this.refreshSearchable();
		},

		"onParentFieldChange": function(newParentVal)
		{
			this.parentValue = newParentVal;

			if(this.lastFetchedParentValue !== newParentVal)
			{
				this.lastFetchedParentValue = undefined;
				this.pendingParentFetch = undefined;
				this.clearLovOptions();
				this.selectedOption = null;
				this.selectedOptions = [];
			}
		},

		"ensureDependentLovLoaded": function()
		{
			if(!this.fieldInfo.lovDetails || !this.fieldInfo.lovDetails.parentField)
			{
				return;
			}

			if(!this.parentValue)
			{
				this.clearLovOptions();
				return;
			}

			if(this.lastFetchedParentValue === this.parentValue || this.pendingParentFetch === this.parentValue)
			{
				return;
			}

			var parentForFetch = this.parentValue;
			this.pendingParentFetch = parentForFetch;
			$restService.fetchLovValues(
				this.fieldInfo.lovDetails.lovName,
				this.fieldInfo.lovDetails.lovType,
				$.proxy(function(lovList) {
					if(this.pendingParentFetch === parentForFetch)
					{
						this.pendingParentFetch = undefined;
					}
					if(this.parentValue !== parentForFetch)
					{
						return;
					}
					this.lastFetchedParentValue = parentForFetch;
					this.setLovValues(lovList);
				}, this),
				parentForFetch,
				this.noAuth
			);
		},

		"onDropdownOpen": function()
		{
			this.ensureDependentLovLoaded();
		},

		"setLovValues": function(lovList)
		{
			if(this.lovOptions.length > 0)
			{
				this.lovOptions.splice(0, this.lovOptions.length);
			}

			if(this.emptyOption && this.emptyOption.length > 0)
			{
				this.lovOptions.push({"id": "", "label": this.emptyOption});
			}

			for(var lov of lovList)
			{
				this.lovOptions.push(lov);
			}

			this.refreshSearchable();

			if(this.syncSelectionFromFieldValue)
			{
				this.syncSelectionFromFieldValue();
			}
		},

		"coerceLovId": function(id)
		{
			if(id == null || id === "")
			{
				return id;
			}

			// Stored Long LOVs: API may return id as string; keep numeric for model binding
			if(typeof id === "number")
			{
				return id;
			}

			var s = ("" + id).trim();
			if(/^\d+$/.test(s))
			{
				return Number(s);
			}

			// STATIC enum LOV ids are enum names
			return id;
		},

		"findOptionById": function(id)
		{
			if(id == null || id === "")
			{
				return null;
			}

			for(var opt of this.lovOptions)
			{
				if(("" + opt.id) === ("" + id))
				{
					return opt;
				}
			}

			return null;
		},

		"findOptionWithLabel": function(label)
		{
			if(!label)
			{
				return null;
			}

			for(var opt of this.lovOptions)
			{
				if(opt.label == label)
				{
					return opt;
				}
			}

			var labelLower = ("" + label).toLowerCase();
			for(var opt2 of this.lovOptions)
			{
				if(("" + opt2.label).toLowerCase() === labelLower)
				{
					return opt2;
				}
			}

			return {"id": label, "label": label, "isNew": true};
		},

		"onErrorStatus": function(newErrStatus)
		{
			if(!newErrStatus)
			{
				$(this.$el).find(".is-invalid").removeClass("is-invalid");
			}
		},
	}
};


/**
 * Simple / enum / Long LOV — selection only (no create). Supports multi when field.multiValued.
 */
newVueUiComponent('yk-lov-field', {
	"extends": lovSharedBase,

	"methods": {
		"onCreate": function()
		{
			this.initLovDetailsFromProps();

			if(this.isMultiValued())
			{
				this.fieldValue = Array.isArray(this.modelValue) ? this.modelValue : [];
				this.selectedOptions = [];
			}
			else
			{
				this.fieldValue = (this.modelValue != null && this.modelValue !== "") ? this.modelValue : "";
				this.selectedOption = null;
			}

			if(!this.fieldInfo.lovDetails.parentField)
			{
				$restService.fetchLovValues(this.fieldInfo.lovDetails.lovName, this.fieldInfo.lovDetails.lovType, this.setLovValues, null, this.noAuth);
			}
		},

		"syncSelectionFromFieldValue": function()
		{
			if(this.isMultiValued())
			{
				var ids = Array.isArray(this.fieldValue) ? this.fieldValue : [];
				var opts = [];
				for(var id of ids)
				{
					var opt = this.findOptionById(id);
					if(opt)
					{
						opts.push(opt);
					}
				}
				this.selectedOptions = opts;
			}
			else
			{
				this.selectedOption = this.findOptionById(this.fieldValue);
			}
		},

		"onModelValueChanged": function(newVal)
		{
			if(this.isMultiValued())
			{
				this.fieldValue = Array.isArray(newVal) ? newVal : [];
			}
			else
			{
				this.fieldValue = newVal;
			}
			this.syncSelectionFromFieldValue();
		},

		"onSimpleSelect": function(selected)
		{
			if(this.isMultiValued())
			{
				this.selectedOptions = selected || [];
				this.fieldValue = this.selectedOptions.map($.proxy(function(o) {
					return this.coerceLovId(o.id);
				}, this));
			}
			else
			{
				this.selectedOption = selected;
				this.fieldValue = selected ? this.coerceLovId(selected.id) : "";
			}
		},

		"reset": function(val)
		{
			if(this.isMultiValued())
			{
				this.fieldValue = Array.isArray(val) ? val : [];
			}
			else
			{
				this.fieldValue = val != null ? val : "";
			}
			this.syncSelectionFromFieldValue();
		},
	},

	"template": `
		<div class="form-group yk-lov-field" :id="fieldInfo.name" :style="'display:' + display">
			<label class="webutil-field-label form-label" v-if="!hideLabel && fieldInfo.label">{{fieldInfo.label}}:</label>
			<Multiselect
				:model-value="isMultiValued() ? selectedOptions : selectedOption"
				@update:model-value="onSimpleSelect"
				:options="lovOptions"
				:multiple="isMultiValued()"
				:searchable="searchable"
				:taggable="false"
				:close-on-select="!isMultiValued()"
				:clear-on-select="false"
				:preserve-search="true"
				label="label"
				track-by="id"
				:placeholder="'Select ' + (fieldInfo.label || '')"
				:class="{'is-invalid': displayError()}"
				@open="onDropdownOpen"
			>
			</Multiselect>
			<div class="invalid-feedback" v-if="displayError()" style="display:block;">{{fieldInfo.error}}</div>
		</div>
	`
});


/**
 * Single String editable LOV — create gated by lovDetails.persist.
 */
newVueUiComponent('yk-editable-lov-field', {
	"extends": lovSharedBase,

	"methods": {
		"onCreate": function()
		{
			this.initLovDetailsFromProps();
			this.fieldValue = this.modelValue ? this.modelValue : "";
			this.selectedOption = null;

			if(!this.fieldInfo.lovDetails.parentField)
			{
				$restService.fetchLovValues(this.fieldInfo.lovDetails.lovName, this.fieldInfo.lovDetails.lovType, this.setLovValues, null, this.noAuth);
			}
		},

		"syncSelectionFromFieldValue": function()
		{
			if(!this.fieldValue)
			{
				this.selectedOption = null;
				return;
			}

			if(this.lovOptions && this.lovOptions.length > 0)
			{
				this.selectedOption = this.findOptionWithLabel(this.fieldValue);
			}
			else
			{
				this.selectedOption = {"id": this.fieldValue, "label": this.fieldValue, "isNew": false};
			}
		},

		"onModelValueChanged": function(newVal)
		{
			this.fieldValue = newVal ? newVal : "";
			this.syncSelectionFromFieldValue();
		},

		"onEditableSelect": function(selected)
		{
			this.selectedOption = selected;
			this.fieldValue = selected ? selected.label : "";
		},

		"onTag": function(newLabel)
		{
			if(!this.isTaggable())
			{
				return;
			}

			var term = newLabel ? ("" + newLabel).trim() : "";
			if(!term)
			{
				return;
			}

			var option = this.findOptionWithLabel(term);
			if(option.isNew)
			{
				this.lovOptions.push(option);
				this.refreshSearchable();
			}

			this.selectedOption = option;
			this.fieldValue = option.label;
		},
	},

	"template": `
		<div class="form-group yk-editable-lov-field" :id="fieldInfo.name" :style="'display:' + display">
			<label class="webutil-field-label form-label" v-if="!hideLabel && fieldInfo.label">{{fieldInfo.label}}:</label>
			<div class="yk-lov-multiselect-wrap" :class="{'yk-lov-is-new': selectedOption && selectedOption.isNew}">
				<Multiselect
					:model-value="selectedOption"
					@update:model-value="onEditableSelect"
					:options="lovOptions"
					:multiple="false"
					:searchable="searchable || isTaggable()"
					:taggable="isTaggable()"
					tag-placeholder="Add as new"
					:close-on-select="true"
					:clear-on-select="false"
					:preserve-search="true"
					label="label"
					track-by="label"
					:placeholder="'Select ' + (fieldInfo.label || '')"
					:class="{'is-invalid': displayError()}"
					@tag="onTag"
					@open="onDropdownOpen"
				>
				</Multiselect>
				<div class="new-tag" v-if="selectedOption && selectedOption.isNew">New</div>
			</div>
			<div class="invalid-feedback" v-if="displayError()" style="display:block;">{{fieldInfo.error}}</div>
		</div>
	`
});


/**
 * Multi String editable LOV — create gated by lovDetails.persist.
 */
newVueUiComponent('yk-multi-editable-lov-field', {
	"extends": lovSharedBase,

	"methods": {
		"onCreate": function()
		{
			this.initLovDetailsFromProps();
			this.fieldValue = Array.isArray(this.modelValue) ? this.modelValue : [];
			this.selectedOptions = [];

			if(!this.fieldInfo.lovDetails.parentField)
			{
				$restService.fetchLovValues(this.fieldInfo.lovDetails.lovName, this.fieldInfo.lovDetails.lovType, this.setLovValues, null, this.noAuth);
			}
		},

		"formatIndexedErrors": function(errorList)
		{
			if(!errorList)
			{
				return "";
			}

			if(typeof(errorList) == "string")
			{
				return errorList;
			}

			if(!Array.isArray(errorList))
			{
				return "";
			}

			var messages = [];
			for(var errEntry of errorList)
			{
				if(!errEntry || !errEntry.error)
				{
					continue;
				}
				if(errEntry.index != null)
				{
					messages.push("[" + errEntry.index + "] " + errEntry.error);
				}
				else
				{
					messages.push("" + errEntry.error);
				}
			}
			return messages.join(", ");
		},

		"onServerError": function(newVal)
		{
			this.fieldInfo.error = this.formatIndexedErrors(newVal);
		},

		"syncSelectionFromFieldValue": function()
		{
			var labels = Array.isArray(this.fieldValue) ? this.fieldValue : [];
			var opts = [];
			for(var label of labels)
			{
				var option = this.findOptionWithLabel(label);
				if(option)
				{
					opts.push(option);
				}
			}
			this.selectedOptions = opts;
		},

		"onModelValueChanged": function(newVal)
		{
			this.fieldValue = Array.isArray(newVal) ? newVal : [];
			this.syncSelectionFromFieldValue();
		},

		"validateNewOptionValue": function(newLabel)
		{
			var valueValidations = this.fieldInfo.valueValidations;
			if(!valueValidations || valueValidations.length == 0)
			{
				return true;
			}

			try
			{
				$validationService.validate(this.fieldInfo.dataType, valueValidations, newLabel, {});
				return true;
			}
			catch(err)
			{
				var errMsg = (err && err.message) ? err.message : ("" + err);
				$utils.alert(errMsg);
				return false;
			}
		},

		"onMultiSelect": function(selected)
		{
			this.selectedOptions = selected || [];
			this.fieldValue = this.selectedOptions.map(function(o) { return o.label; });
			this.onFieldValueChange(this.fieldValue);
		},

		"onTag": function(newLabel)
		{
			if(!this.isTaggable())
			{
				return;
			}

			var term = newLabel ? ("" + newLabel).trim() : "";
			if(!term)
			{
				return;
			}

			var option = this.findOptionWithLabel(term);
			for(var opt of this.selectedOptions)
			{
				if(opt.label == option.label)
				{
					$utils.alert("Option is already selected: " + option.label);
					return;
				}
			}

			if(!this.validateNewOptionValue(option.label))
			{
				return;
			}

			if(option.isNew)
			{
				this.lovOptions.push(option);
				this.refreshSearchable();
			}

			this.selectedOptions = this.selectedOptions.concat([option]);
			this.fieldValue = this.selectedOptions.map(function(o) { return o.label; });
			this.onFieldValueChange(this.fieldValue);
		},
	},

	"template": `
		<div class="form-group yk-multi-editable-lov-field" :id="fieldInfo.name" :style="'display:' + display">
			<label class="webutil-field-label form-label" v-if="!hideLabel && fieldInfo.label">{{fieldInfo.label}}:</label>
			<Multiselect
				:model-value="selectedOptions"
				@update:model-value="onMultiSelect"
				:options="lovOptions"
				:multiple="true"
				:searchable="searchable || isTaggable()"
				:taggable="isTaggable()"
				tag-placeholder="Add as new"
				:close-on-select="false"
				:clear-on-select="false"
				:preserve-search="true"
				label="label"
				track-by="label"
				:placeholder="'Add ' + (fieldInfo.label || '')"
				:class="{'is-invalid': displayError()}"
				@tag="onTag"
				@open="onDropdownOpen"
			>
			</Multiselect>
			<div class="invalid-feedback" v-if="displayError()" style="display:block;">{{fieldInfo.error}}</div>
		</div>
	`
});
