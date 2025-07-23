var $dataTypeMapping = {
	"STRING": {"componentType": "yk-input-field", "inputType": "text"},
	"DATE": {"componentType": "yk-input-field", "inputType": "date"},
	"PASSWORD": {"componentType": "yk-input-field", "inputType": "password"},
	"INTEGER": {"componentType": "yk-input-field", "inputType": "number"},
	"FLOAT": {"componentType": "yk-input-field", "inputType": "number"},
	"MULTI_LINE_STRING": {"componentType": "yk-textarea-field", "inputType": "text"},
		
	"LIST_OF_VALUES": {"componentType": "yk-lov-field", "inputType": "text"},
	"EDITABLE_LIST_OF_VALUES": {"componentType": "yk-editable-lov-field", "inputType": "text"},
	"MULTI_EDITABLE_LIST_OF_VALUES": {"componentType": "yk-multi-editable-lov-field", "inputType": "text"},
	
	"BOOLEAN": {"componentType": "yk-switch", "inputType": "text"},
	
	"VERIFICATION": {"componentType": "yk-ver-input-field", "inputType": "text"},
	"CAPTCHA": {"componentType": "yk-captcha-field", "inputType": "text"},	

	"FILE": {"componentType": "yk-input-file", "inputType": "text"},	
	"IMAGE": {"componentType": "yk-input-image", "inputType": "text"},	
	
};

export var $modelDefService = {

	"populateFieldDef": function(fieldDef) {
		if(fieldDef.displayable == false)
		{
			return;
		}

		var dataType = fieldDef.fieldType;
		var typeMapping = $dataTypeMapping[dataType];

		if(!typeMapping)
		{
			dataType = "STRING";
			typeMapping = $dataTypeMapping[dataType];
		}

		if(dataType == "LIST_OF_VALUES" && fieldDef.lovDetails.editableLov)
		{
			if(fieldDef.multiValued) {
				typeMapping = $dataTypeMapping["MULTI_EDITABLE_LIST_OF_VALUES"];
			} else {
				typeMapping = $dataTypeMapping["EDITABLE_LIST_OF_VALUES"];					
			}
		}

		fieldDef.dataType = dataType;
		fieldDef.inputType = typeMapping["inputType"];
		fieldDef.componentType = typeMapping["componentType"];

		/*
		if(fieldDef.validations)
		{
			for(var validation of fieldDef.validations)
			{
				if(validation.values && !validation.config)
				{
					validation.config = validation.values;
				}
			}
		}
		*/
	},

	/**
	 * Expected to be used by applications to populated field definitions
	 * on model-def. This is generally done when modelDef needs to be used
	 * in customized layouts.
	 */
	"populateFieldDetails" : function(modelDef) {
		// Create index of fields based on name
		let nameToField = {};
		
		for(let fieldDef of modelDef.fields)
		{
			nameToField[fieldDef.name] = fieldDef;
		}
		
		// Load the fields
		for(var fieldDef of modelDef.fields)
		{
			this.populateFieldDef(fieldDef);
		}
		
		modelDef.fieldIndex = nameToField;
	},

	/**
	 * Divides the fields into groups and also populate field details.
	 */
	"divideModelGroups": function(modelDef, modelFieldGroups, columnCount, groups) {
		//if the array already has elements, clear them
		if(modelFieldGroups.length > 0)
		{
			modelFieldGroups.splice(0, modelFieldGroups.length);
		}
		
		// if groups is not defined, create default group
		if(!groups) {
			groups = [{"default": true}];
		}
		
		// Create result grouping array and field-to-group mapping
		let resultGrouping = [];
		let fieldToGroup = {};
		let defaultGroup = null;
		
		for(let group of groups) {
			let fieldGroup = {"label": group.label, "rows": []};
			resultGrouping.push(fieldGroup);
			
			if(group.default) {
				defaultGroup = fieldGroup;
			}
			
			if(!group.fields) {
				continue;
			}
			
			for(let field of group.fields) {
				fieldToGroup[field] = fieldGroup;
			}
		}
		
		// if no default group is defined add one at end
		if(!defaultGroup) {
			defaultGroup = {"label": null, "rows": []};
			resultGrouping.push(defaultGroup);
		}
		
		// Add row-wise-fields to the groups
		let fldIdx = 0;
		let colSize = 12 / columnCount;
		
		for(var fieldDef of modelDef.fields)
		{
			if(fieldDef.displayable == false)
			{
				continue;
			}
			
			// deter the group to which cur field has to be added
			let fieldGroup = fieldToGroup[fieldDef.name];
			fieldGroup = fieldGroup ? fieldGroup : defaultGroup;
			
			this.populateFieldDef(fieldDef);

			fieldDef.index = fldIdx;
			fieldDef.size = colSize;
			
			// fetch or create last row
			let lastRow = null;
			
			if(fieldGroup.rows.length > 0) {
				lastRow = fieldGroup.rows[fieldGroup.rows.length - 1];
			}
			else {
				lastRow = {"fields": [], "index": 0};
				fieldGroup.rows.push(lastRow);
			}

			// if last row is already full create new last row			
			if(lastRow.fields.length >= columnCount) {
				lastRow = {"fields": [], "index": fieldGroup.rows.length};
				fieldGroup.rows.push(lastRow);
			}
			
			lastRow.fields.push(fieldDef);
			fldIdx++;
		}
		
		// copy non-empty groups to final result
		for(let group of resultGrouping) {
			if(group.rows.length == 0) {
				continue;
			}
			
			modelFieldGroups.push(group);
		}
	},
};
