$.application.factory('validator', ["logger", function(logger){
	var dataValidations = {
		"dataTypes" : {
			"STRING" : {
				"validate": function(value, modelDef, fieldDef){
				}
			},
			"MULTI_LINE_STRING" : {
				"validate": function(value, modelDef, fieldDef){
				}
			},
			"INTEGER": {
				"validate": function(value, modelDef, fieldDef){
					if(!/^\d+$/.test(value))
					{
						throw "Invalid integer value specified - " + value;
					}
				}
			},
			"FLOAT" : {
				"validate": function(value, modelDef, fieldDef){
					if(!/^\d+\.\d+$/.test(value) && !/^\d+$/.test(value))
					{
						throw "Invalid decimal value specified - " + value;
					}
				}
			},
			"DECIMAL" : {
				"validate": function(value, modelDef, fieldDef){
					if(!/^\d+\.\d+$/.test(value) && !/^\d+$/.test(value))
					{
						throw "Invalid decimal value specified - " + value;
					}
				}
			},
			"BOOLEAN": {
				"validate": function(value, modelDef, fieldDef){
					if(value != "true" && value != "false")
					{
						throw "Invalid boolean value specified - " + value;
					}
				}
			},
			"DATE" : {
				"validate": function(value, modelDef, fieldDef){
				}
			},
			"LIST_OF_VALUES" : {
				"validate": function(value, modelDef, fieldDef){
				}
			},
			"FILE" : {
				"validate": function(value, modelDef, fieldDef){
				}
			}
		},
		
		"validators": {
			
			/////////////////////////////////////////////////////////
			//String related validations
			////////////////////////////////////////////////////////////
			"maxLength" : {
				"validate" : function(model, validationDefValues, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					if(value.length > validationDefValues.value)
					{
						return false;
					}
					
					return true;
				}
			},
			"minLength" : {
				"validate" : function(model, validationDefValues, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					if(value.length < validationDefValues.value)
					{
						return false;
					}
					
					return true;
				}
			},
			"pattern" :{
				"validate" : function(model, validationDefValues, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					var pattern = new RegExp("^" + validationDefValues.regexp + "$");
					
					if(!pattern.test(value))
					{
						return false;
					}
					
					return true;
				}
			},
			"mispattern" :{
				"validate" : function(model, validationDefValues, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					var pattern = new RegExp(validationDefValues.regexp);
					
					if(pattern.test(value))
					{
						return false;
					}
					
					return true;
				}
			},
			
			
			/////////////////////////////////////////////////////////
			//Generic validations
			////////////////////////////////////////////////////////////
			"required" :{
				"validate" : function(model, validationDefValues, value) {
					if(value == null || value == undefined)
					{
						return false;
					}
					
					if((typeof value) == 'string')
					{
						if(value.trim().length <= 0)
						{
							return false;
						}
					}
					
					return true;
				},
			
				"customizeUi": function(labelField) {
					labelField.addClass("yk-field-label-mandatory");
				}
			},
			"mandatoryOption" :{
				"validate" : function(model, validationDefValues, value) {
					if(value)
					{
						return true;
					}
					
					var otherFieldNames = validationDefValues.fields;
					
					for(var i = 0; i < otherFieldNames.length; i++)
					{
						if(model[otherFieldNames[i]])
						{
							return true;
						}
					}

					return false;
				}
			},
			"matchWith" :{
				"validate" : function(model, validationDefValues, value) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					if(value == otherValue)
					{
						return true;
					}
					
					return false;
				}
			},
			
			/////////////////////////////////////////////////////////
			//Date related validations
			////////////////////////////////////////////////////////////
			"futureOrToday" :{
				"validate" : function(model, validationDefValues, value, modelDef) {
					if(!value)
					{
						return true;
					}
					
					var momentDate = moment(value, modelDef.jsDateFormat);
					var today = new Date();
					
					if(momentDate.isSame(today, 'day') || momentDate.isAfter(today, 'day'))
					{
						return true;
					}
					
					return false;
				}
			},
			"greaterThanDateField" :{
				"validate" : function(model, validationDefValues, value, modelDef) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					var momentDate = moment(value, modelDef.jsDateFormat);
					var otherDate = moment(otherValue, modelDef.jsDateFormat);
					
					if(momentDate.isAfter(otherDate, 'day'))
					{
						return true;
					}
					
					return false;
				}
			},
			"greaterThanEqualsDateField" :{
				"validate" : function(model, validationDefValues, value, modelDef) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					var momentDate = moment(value, modelDef.jsDateFormat);
					var otherDate = moment(otherValue, modelDef.jsDateFormat);
					
					if(momentDate.isSame(otherDate, 'day') || momentDate.isAfter(otherDate, 'day'))
					{
						return true;
					}
					
					return false;
				}
			},
			"lessThanDateField" :{
				"validate" : function(model, validationDefValues, value, modelDef) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					var momentDate = moment(value, modelDef.jsDateFormat);
					var otherDate = moment(otherValue, modelDef.jsDateFormat);
					
					if(momentDate.isBefore(otherDate, 'day'))
					{
						return true;
					}
					
					return false;
				}
			},
			"lessThanEqualsDateField" :{
				"validate" : function(model, validationDefValues, value, modelDef) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					var momentDate = moment(value, modelDef.jsDateFormat);
					var otherDate = moment(otherValue, modelDef.jsDateFormat);
					
					if(momentDate.isSame(otherDate, 'day') || momentDate.isBefore(otherDate, 'day'))
					{
						return true;
					}
					
					return false;
				}
			},
			"pastOrToday" :{
				"validate" : function(model, validationDefValues, value, modelDef) {
					if(!value)
					{
						return true;
					}
					
					var momentDate = moment(value, modelDef.jsDateFormat);
					var today = new Date();
					
					if(momentDate.isSame(today, 'day') || momentDate.isBefore(today, 'day'))
					{
						return true;
					}
					
					return false;
				}
			},
			
			/////////////////////////////////////////////////////////
			//Number related validations
			////////////////////////////////////////////////////////////
			"greaterThan" :{
				"validate" : function(model, validationDefValues, value) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					if(value <= otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"greaterThanEquals" :{
				"validate" : function(model, validationDefValues, value) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					if(value < otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"lessThan" :{
				"validate" : function(model, validationDefValues, value) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					if(value >= otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"lessThanEquals" :{
				"validate" : function(model, validationDefValues, value) {
					var otherValue = model[validationDefValues.field];
					
					if(!value || !otherValue)
					{
						return true;
					}
					
					if(value > otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"minValue" :{
				"validate" : function(model, validationDefValues, value) {
					if(!value)
					{
						return true;
					}
					
					if(value < validationDefValues.value)
					{
						return false;
					}
					
					return true;
				}
			},
			"maxValue" :{
				"validate" : function(model, validationDefValues, value) {
					if(!value)
					{
						return true;
					}
					
					if(value > validationDefValues.value)
					{
						return false;
					}
					
					return true;
				}
			},
		},
		
		/**
		 * Executes validations on all fields of the model. In case validation fails 
		 * appropriate error will be thrown
		 */
		"validateModel" : function(model, modelDef, errors) {
			var fields = modelDef.fields;
			var value = null;
			var validations = null;
			var noErrorsFound = true;
			
			for(var i = 0 ; i < fields.length; i++)
			{
				value = model[fields[i].name];
				
				if((typeof value) == 'string')
				{
					if(value.length <= 0)
					{
						value = null;
					}
				}
				
				validations = fields[i].validations;
				
				//if no validations found on field
				if(!validations)
				{
					continue;
				}
				
				//loop through field validations and perform it on field value
				for(var j = 0; j < validations.length; j++)
				{
					//check if the validator corresponding to server validation is available
					if(!this.validators[validations[j].name])
					{
						continue;
					}
					
					//perform validation. If error found, move on to next field
					if( !this.validators[validations[j].name].validate(model, validations[j].values, value, modelDef) )
					{
						errors[fields[i].name] = validations[j].errorMessage.replace("${value}", value);
						logger.error("Field '{}' resulted in error - {}", fields[i].name, errors[fields[i].name]);
						
						noErrorsFound = false;
						break;
					}
				}
			}
			//Add validations for extension fields
			var fieldDef = null;
			
			if(!errors.extendedFields)
			{
				errors.extendedFields = {};
			}
			
			for(var extFldName in modelDef.extensionFieldMap)
			{
				fieldDef = modelDef.extensionFieldMap[extFldName];
				value = model.extendedFields ? model.extendedFields[extFldName] : null;

				//ensure for mandatory fields value is provided
				if(fieldDef.required && (value == null || value == undefined))
				{
					errors.extendedFields[extFldName] = "Value can not be empty";
					logger.error("Ext-Field '{}' resulted in error - {}", extFldName, errors.extendedFields[extFldName]);
					noErrorsFound = false;
				}
				
				//ignore fields with empty value
				if(!value)
				{
					continue;
				}
				
				if(fieldDef.type == 'STRING' || fieldDef.type == 'MULTI_LINE_STRING')
				{
					if(value.length > fieldDef.maxLength)
					{
						errors.extendedFields[extFldName] = "Value length should be less than " + fieldDef.maxLength;
						logger.error("Ext-Field '{}' resulted in error - {}", extFldName, errors.extendedFields[extFldName]);
						noErrorsFound = false;
					}
				}
			}
			
			return noErrorsFound;
		},
		
		"validateField" : function(model, modelDef, fieldName) {
			var fieldDef = modelDef.fieldMap[fieldName];

			//if field def is not found
			if(!fieldDef)
			{
				logger.error("No field found with name - {}", fieldName);
				return;
			}
			
			var validations = fieldDef.validations;
			var value = model[fieldName];
			
			//if no validations are found on field
			if(!validations)
			{
				return;
			}
			
			for(var j = 0; j < validations.length; j++)
			{
				//check if the validator corresponding to server validation is available
				if(!this.validators[validations[j].name])
				{
					continue;
				}
				
				//perform validation
				if( !this.validators[validations[j].name].validate(model, validations[j].values, value, modelDef) )
				{
					throw validations[j].errorMessage.replace("${value}", value);
				}
			}
		},
		
		"validateExtendedField" : function(model, modelDef, fieldName) {
			var fieldDef = modelDef.extensionFieldMap[fieldName];

			//if field def is not found
			if(!fieldDef)
			{
				logger.error("No extended field def found with name - {}", fieldName);
				return;
			}
			
			var value = model.extendedFields[fieldName];
			
			if((typeof value) == 'string' && value.length <= 0)
			{
				value = null;
			}
			
			if(fieldDef.required && (value == null || value == undefined))
			{
				throw "Value is required";
			}
			
			if(fieldDef.type == 'STRING' || fieldDef.type == 'MULTI_LINE_STRING')
			{
				if(value.length > fieldDef.maxLength)
				{
					throw "Value length should be less than " + fieldDef.maxLength;
				}
			}
		},
		
		/**
		 * Customized the fields ui according to the validators presents on them
		 */
		"customizeUi" : function(containerElement, modelDef) {
			var fields = modelDef.fields;
			containerElement = $(containerElement);

			var validations = null;
			var labelField = null;
			
			//loop through the fields
			for(var i = 0 ; i < fields.length; i++)
			{
				validations = fields[i].validations;
				
				//if no validations are present ignore the field
				if(!validations)
				{
					continue;
				}
				
				labelField = null;
				
				//loop through validators
				for(var j = 0; j < validations.length; j++)
				{
					//if validator does not have customization ignore current validator
					if( !this.validators[validations[j].name].customizeUi)
					{
						continue;
					}
					
					//on demand find the ui field
					if(!labelField)
					{
						labelField = containerElement.find("[field-label='" + fields[i].name + "']");
					}
					
					//customize as per validator
					this.validators[validations[j].name].customizeUi(labelField);
				}
			}
			
			var extField = null;
			
			//Add customizations for extension fields
			for(var extFldName in modelDef.extensionFieldMap)
			{
				extField = modelDef.extensionFieldMap[extFldName];

				if(extField.required)
				{
					//% is used to differentiate normal fields from extended fields
					labelField = containerElement.find("[field-label='%" + extField.name + "']");
					this.validators["required"].customizeUi(labelField);
				}
			}
		}
	};

	return dataValidations;
}]);
