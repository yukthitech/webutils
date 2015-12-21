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
				"validate" : function(model, validationDef, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					if(value.length > validationDef.value)
					{
						return false;
					}
					
					return true;
				}
			},
			"minLength" : {
				"validate" : function(model, validationDef, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					if(value.length < validationDef.value)
					{
						return false;
					}
					
					return true;
				}
			},
			"pattern" :{
				"validate" : function(model, validationDef, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					var pattern = new RegExp(validationDef.regexp);
					
					if(!pattern.test(value))
					{
						return fals;
					}
					
					return true;
				}
			},
			"mispattern" :{
				"validate" : function(model, validationDef, value) {
					if((typeof value) != 'string')
					{
						return true;
					}
					
					var pattern = new RegExp(validationDef.regexp);
					
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
				"validate" : function(model, validationDef, value) {
					if(!value)
					{
						return false;
					}
					
					if((typeof value) != 'string')
					{
						if(value.trim().length <= 0)
						{
							return false;
						}
					}
					
					return true;
				}
			},
			"mandatoryOption" :{
				"validate" : function(model, validationDef, value) {
					if(value)
					{
						return true;
					}
					
					var otherFieldNames = validationDef.fields;
					
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
				"validate" : function(model, validationDef, value) {
					var otherValue = model[validatorDef.field];
					
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
				"validate" : function(model, validationDef, value) {
					return true;
				}
			},
			"greaterThanDateField" :{
				"validate" : function(model, validationDef, value) {
					return true;
				}
			},
			"greaterThanEqualsDateField" :{
				"validate" : function(model, validationDef, value) {
					return true;
				}
			},
			"lessThanDateField" :{
				"validate" : function(model, validationDef, value) {
					return true;
				}
			},
			"lessThanEqualsDateField" :{
				"validate" : function(model, validationDef, value) {
					return true;
				}
			},
			"pastOrToday" :{
				"validate" : function(model, validationDef, value) {
					return true;
				}
			},
			
			/////////////////////////////////////////////////////////
			//Number related validations
			////////////////////////////////////////////////////////////
			"greaterThan" :{
				"validate" : function(model, validationDef, value) {
					var otherValue = model[validatorDef.field];
					
					if(value <= otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"greaterThanEquals" :{
				"validate" : function(model, validationDef, value) {
					var otherValue = model[validatorDef.field];
					
					if(value < otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"lessThan" :{
				"validate" : function(model, validationDef, value) {
					var otherValue = model[validatorDef.field];
					
					if(value >= otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"lessThanEquals" :{
				"validate" : function(model, validationDef, value) {
					var otherValue = model[validatorDef.field];
					
					if(value > otherValue)
					{
						return false;
					}
					
					return true;
				}
			},
			"minValue" :{
				"validate" : function(model, validationDef, value) {
					if(value < validatorDef.value)
					{
						return false;
					}
					
					return true;
				}
			},
			"maxValue" :{
				"validate" : function(model, validationDef, value) {
					if(value > validatorDef.value)
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
		"validateModel" : function(model, modelDef) {
			var fields = modelDef.fields;
			var value = null;
			var validations = null;
			
			for(var i = 0 ; i < fields; i++)
			{
				value = model[fields[i]];
				
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
				
				for(var j = 0; j < validations.length; j++)
				{
					//check if the validator corresponding to server validation is available
					if(!this.validators[validations[j].name])
					{
						continue;
					}
					
					//perform validation
					if( !this.validators[validations[j].name].validate(model, validations[j], value) )
					{
						throw validations[j].errorMessage.replace("${value}", value);
					}
				}
			}
			//TODO: Add validations for extension fields
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
				if( !this.validators[validations[j].name].validate(model, validations[j], value) )
				{
					throw validations[j].errorMessage.replace("${value}", value);
				}
			}
		},
		
		"validateExtendedField" : function(model, modelDef, fieldName) {
			
		}
	};

	return dataValidations;
}]);
