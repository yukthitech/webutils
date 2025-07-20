import {$logger, $utils} from "./common.js";

export var $validationService = {
	"dataTypePatterns": {
		"INTEGER": /^\d+$/,
		"FLOAT": /^\d+\.\d+$/,
		"DECIMAL": /^\d+\.\d+$/,
		"BOOLEAN": /(true)|(false)/
	},
	
	"defaultMessages": {
		"minLength" : "Min length of value should be ${value}",
		"maxLength" : "Max length of value can be ${value}",
		"pattern" : "Value is not matching with required pattern",
		"mispattern" : "Value is matching with unwanted pattern",
		
		"required" : "Value is mandatory",
		"mandatoryOption" : "Value is mandatory for fields ${validation.config.fields}",
		"matchWith" : "Value is not matching with ${validation.config.field}",
		
		"futureOrToday" : "Value should be greater than or equal to today",
		"greaterThanDateField" : "Value should be greater than field ${validation.config.field}",
		"greaterThanEqualsDateField" : "Value should be greater than or equal to field ${value}",
		"lessThanDateField" : "Value should be less than field ${value}",
		"lessThanEqualsDateField" : "Value should be less than or equal to field ${value}",
		"pastOrToday" : "Value should be less than or equal to today",
		
		"greaterThan" : "Value should be greater than field ${validation.config.field}",
		"greaterThanEquals" : "Value should be greater than or equal to field ${validation.config.field}",
		"lessThan" : "Value should be less than field ${validation.config.field}",
		"lessThanEquals" : "Value should be less than or equal to field ${validation.config.field}",
		
		"minValue" : "Value should be less than or equal to ${value}",
		"maxValue" : "Value should be less than or equal to ${value}"
	},
		
	"validators": {
		
		/////////////////////////////////////////////////////////
		//String related validations
		////////////////////////////////////////////////////////////
		"maxLength" : function(config, value) {
			if((typeof value) != 'string')
			{
				return true;
			}
			
			if(value.length > config.value)
			{
				return false;
			}
			
			return true;
		},
		"minLength" : function(config, value) {
			if((typeof value) != 'string')
			{
				return true;
			}
			
			if(value.length < config.value)
			{
				return false;
			}
			
			return true;
		},
		"pattern" : function(config, value) {
			if((typeof value) != 'string')
			{
				return true;
			}
			
			if(value.length == 0)
			{
				return true;
			}
			
			var pattern = new RegExp("^" + config.regexp + "$");
			
			if(!pattern.test(value))
			{
				return false;
			}
			
			return true;
		},
		"mispattern" : function(config, value) {
			if((typeof value) != 'string')
			{
				return true;
			}
			
			if(value.length == 0)
			{
				return true;
			}

			var pattern = new RegExp(config.regexp);
			
			if(pattern.test(value))
			{
				return false;
			}
			
			return true;
		},
		
		
		/////////////////////////////////////////////////////////
		//Generic validations
		////////////////////////////////////////////////////////////
		"required" : function(config, value) {
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
		"mandatoryOption" : function(config, value, model) {
			if(value)
			{
				return true;
			}
			
			var otherFieldNames = config.fields;
			
			for(var i = 0; i < otherFieldNames.length; i++)
			{
				if(model[otherFieldNames[i]])
				{
					return true;
				}
			}

			return false;
		},
		
		"matchWith" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			if(value == otherValue)
			{
				return true;
			}
			
			return false;
		},
		
		/////////////////////////////////////////////////////////
		//Date related validations
		////////////////////////////////////////////////////////////
		"futureOrToday" : function(config, value) {
			if(!value)
			{
				return true;
			}
			
			var momentDate = moment(value, config.jsDateFormat);
			var today = new Date();
			
			if(momentDate.isSame(today, 'day') || momentDate.isAfter(today, 'day'))
			{
				return true;
			}
			
			return false;
		},
		"greaterThanDateField" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			var momentDate = moment(value, config.jsDateFormat);
			var otherDate = moment(otherValue, config.jsDateFormat);
			
			if(momentDate.isAfter(otherDate, 'day'))
			{
				return true;
			}
			
			return false;
		},
		"greaterThanEqualsDateField" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			var momentDate = moment(value, config.jsDateFormat);
			var otherDate = moment(otherValue, config.jsDateFormat);
			
			if(momentDate.isSame(otherDate, 'day') || momentDate.isAfter(otherDate, 'day'))
			{
				return true;
			}
			
			return false;
		},
		"lessThanDateField" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			var momentDate = moment(value, config.jsDateFormat);
			var otherDate = moment(otherValue, config.jsDateFormat);
			
			if(momentDate.isBefore(otherDate, 'day'))
			{
				return true;
			}
			
			return false;
		},
		"lessThanEqualsDateField" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			var momentDate = moment(value, config.jsDateFormat);
			var otherDate = moment(otherValue, config.jsDateFormat);
			
			if(momentDate.isSame(otherDate, 'day') || momentDate.isBefore(otherDate, 'day'))
			{
				return true;
			}
			
			return false;
		},
		"pastOrToday" : function(config, value) {
			if(!value)
			{
				return true;
			}
			
			var momentDate = moment(value, config.jsDateFormat);
			var today = new Date();
			
			if(momentDate.isSame(today, 'day') || momentDate.isBefore(today, 'day'))
			{
				return true;
			}
			
			return false;
		},
		
		/////////////////////////////////////////////////////////
		//Number related validations
		////////////////////////////////////////////////////////////
		"greaterThan" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			if(value <= otherValue)
			{
				return false;
			}
			
			return true;
		},
		"greaterThanEquals" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			if(value < otherValue)
			{
				return false;
			}
			
			return true;
		},
		"lessThan" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			if(value >= otherValue)
			{
				return false;
			}
			
			return true;
		},
		"lessThanEquals" : function(config, value, model) {
			var otherValue = model[config.field];
			
			if(!value || !otherValue)
			{
				return true;
			}
			
			if(value > otherValue)
			{
				return false;
			}
			
			return true;
		},
		"minValue" : function(config, value) {
			if(!value)
			{
				return true;
			}
			
			if(value < config.value)
			{
				return false;
			}
			
			return true;
		},
		"maxValue" :function(config, value) {
			if(!value)
			{
				return true;
			}
			
			if(value > config.value)
			{
				return false;
			}
			
			return true;
		},
	},
	
	"validate": function(dataType, validations, value, model) 
	{
		if(!validations || validations.length == 0)
		{
			return;
		}
		
		for(var validation of validations)
		{
			if(!this.validators[validation.name])
			{
				$logger.warn("Invalid validator used: " + validation.name);
				continue;
			}
			
			if(!this.validators[validation.name](validation.config, value, model))
			{
				var context = {
					"validation": validation,
					"value": value,
					"model": model
				};
				
				var mssg = validation.errorMessage;
				mssg = mssg ? mssg : this.defaultMessages[validation.name];
				
				throw {
					"validation": validation,
					"message": $utils.processTemplate(mssg, context)
				};
			}
		}
	}
}