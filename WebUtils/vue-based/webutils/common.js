export var $appConfiguration = {
	"debugEnabled": true,
	"errorEnabled": true,
	"traceEnabled": false
};

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

export var $utils = {
	"ARG_PATERN" : /\{\}/g,
	
	/**
	 * This dialogs variable will be set by newVueApp() while creating 
	 * the app for page.
	 */
	"ykDialogs": null,
	
	"format": function(message, args, argIdx) {
		
		var idx = (argIdx == null || argIdx == undefined) ? 1 : argIdx;
		var finalMsg = null;
		
		if(typeof message == "string")
		{
			finalMsg = message.replace(this.ARG_PATERN, function(match, p1){
				var argVal = (idx < args.length) ? args[idx] : null;
				
				if(argVal && (typeof argVal == "object"))
				{
					argVal = JSON.stringify(argVal);
				}
				
				idx++;
				return argVal ? argVal : "null";
			});
		}
		else if(message && (typeof message == "object"))
		{
			finalMsg = JSON.stringify(message);
		}
		else
		{
			finalMsg = message;
		}

		return finalMsg;
	},
	
	"formatStr": function()
	{
		if(arguments.length == 0)
		{
			return "";
		}

		if(arguments.length == 1)
		{
			return arguments[0];
		}
		
		var argCopy = [...arguments];
		argCopy.splice(0, 1)
		
		return this.format(arguments[0], argCopy, 0);
	},
	
	
	"processTemplate": function(message, context) {
		var finalMsg = message.replace(/\$\{(.*)\}/, function(match, p1){
			
			//add "context." prefix to expression, so that all expressions refer to context variable above
			var expression = "context." + p1;
			
			var res = null;
			
			try
			{
				res = eval(expression);
			}catch(ex)
			{
				console.error("An error occurred while parsing expression '" + expression + "'. Error: " + ex);
			}
			
			if(!res)
			{
				res = "";
			}
			
			return res;
		});
		
		return finalMsg;
	},
	
	"deepClone": function(object) {
		return JSON.parse(JSON.stringify(object));
	},
	
	"removeArrElement": function(array, element){
		var  index = array.indexOf(element);
		
		if(index < 0)
		{
			return;
		}
		
		array.splice(index, 1);
	},
	
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
	},
	
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
	
	/**
	 * Used to display alert box.
	 * 
	 * @param message Message to be displayed. Can be a string or array. In case of array, first element 
	 * 		is expected to be format string and rest params for this format string.
	 * 		New line (\n) will be replaced with <br>
	 * @param callback Callback function that will be called once alert box is closed.
	 */
	"alert": function(message, callback) {
		this.ykDialogs.displayAlert(message, callback);
	},

	/*
	 * As info is closed by default by time and also can be closed by close button, in timer
	 * function displayTime is used, to ensure new infos are not getting closed because of old timer
	 */
	"info" : function(message) {
		this.ykDialogs.displayInfo(message);
	},
	
	/**
	 * Used to display confirmation box with yes/no options.
	 * 
	 * @param message Message to be displayed. Can be a string or array. In case of array, first element 
	 * 		is expected to be format string and rest params for this format string.
	 * 		New line (\n) will be replaced with <br>
	 * @param callback Callback function that will be called when yes/no is clicked. A 
	 * boolean flag is passed to this function to indicate if "yes" button is clicked or not.
	 */
	"confirm" : function(message, callback) {
		this.ykDialogs.displayConfirm(message, callback);
	},
	
	/**
	 * Used to display input box to accept value from user.
	 * 
	 * @param message Message to be displayed. Can be a string or array. In case of array, first element 
	 * 		is expected to be format string and rest params for this format string.
	 * 		New line (\n) will be replaced with <br>
	 * @param initValue initial value to be set before display
	 * @param callback Callback function that will be called with user input value. If cancel is clicked
	 * 	callback will be called with null value.
	 */
	"input" : function(message, initValue, callback) {
		this.ykDialogs.displayInput(message, initValue, callback);
	},
	
	"executeWithInProgress": function(func) {
		this.ykDialogs.executeWithInProgress(func);
	},

	/**
	 * Executes list of function specified by "functionLst" in a sequential order. During every function call
	 * a callback method is passed, which needs to be invoked by the executing function at end, to execute the next
	 * step function in the flow. If callback method is invoked with arguments, those arguments are passed to next method
	 * from second argument.
	 * 
	 * This helps greatly in executing ajax based functions in sequential order.
	 * 
	 * As a first argument a context is accepted. All the functions are executed as part of the same context. So that data
	 * can be shared between the functions using simple "this."
	 * 
	 * @param context Context object on which the specified functions will be called.
	 * @param functionLst list of functions to be invoked in sequential chained manner.
	 */
	"executeAsyncSteps" : function(context, functionLst) {
		
		//if context is not defined, use empty object
		if(!context)
		{
			context = {};
		}
		
		var invokeContext = {
			"context": context, 
			"functionLst": functionLst, 
			"index": -1, 
		};
		
		var invokeNextFunction = $.proxy(function() {
			this.index++;
			
			if(this.index >= functionLst.length)
			{
				return;
			}
			
			//wrap the target function using proxy, under specified context
			var func = $.proxy(this.functionLst[this.index], this.context);
			
			//build function invocation script which flattens current method arguments
				// into secondary arguments
			var funcInvokeStr = 'func(this.invokeNextFunction';
			
			if(arguments && arguments.length > 0)
			{
				for(var i = 0; i < arguments.length; i++)
				{
					funcInvokeStr += ", arguments[" + i + "]";
				}
			}
			
			funcInvokeStr += ")";
			
			//execute function invocation string
			eval(funcInvokeStr);
		}, invokeContext);
		
		invokeContext.invokeNextFunction = invokeNextFunction;
		
		//invoke first function
		invokeNextFunction();
	}
};

export var $logger = {
	"debugEnabled" : ($appConfiguration.debugEnabled ? true : false),
	"errorEnabled" : ($appConfiguration.errorEnabled ? true : false),
	"traceEnabled" : ($appConfiguration.traceEnabled ? true : false),

	"log": function(prefix, message, args) {
		//if message is not an object (like error etc), print the object directly
		if((typeof message) != 'string')
		{
			console.log(prefix);
			console.log(message);
			return;
		}
		
		var finalMsg = $utils.format(message, args);
		console.log("[" + prefix + "] " + finalMsg);
	},
	
	"debug" : function(message) {
		
		if(!this.debugEnabled)
		{
			return;
		}
		
		this.log("DEBUG", message, arguments);
	},

	"error" : function(message) {
		
		if(!this.errorEnabled)
		{
			return;
		}
		
		this.log("ERROR", message, arguments);
	},
	
	"trace" : function(message) {
		
		if(!this.traceEnabled)
		{
			return;
		}
		
		this.log("TRACE", message, arguments);
	},

	"warn" : function(message) {
		
		if(!this.traceEnabled)
		{
			return;
		}
		
		this.log("WARN", message, arguments);
	}
};

/**
 * Global Function (available as part of all classes)
 * Compares the first argument with the even-indexed arguments. And returns immediate odd-indexed argument.
 *    Ex: caseFunc(var, 1, 'one', 2, 'two') ---> would help to convert number to string based on value of var.
 *
 * If odd number of arguments are specified, then the last argument will be considered as default value.
 */
export function $caseFunc()
{
	//if minimum of 3 arguments are not available, return null
	if(arguments.length <= 3)
	{
		return null;
	}
	
	var checkVar = arguments[0];
	var defValue = (arguments.length % 2 == 0) ? arguments[arguments.length - 1] : null;
	var maxLen =  (arguments.length % 2 == 0) ? arguments.length - 1 : arguments.length; 
	
	for(var i = 1; i < maxLen; i+=2)
	{
		if(arguments[i] == checkVar)
		{
			return arguments[i + 1];
		}
	}
	
	return defValue;
};

export function $mergeObjProperty(dest, source, propName)
{
	if(!source[propName])
	{
		return;
	}
	
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

/**
 * Utility to access hash part and query parameters
 * from/to url.
 */
export var $pageUrl = {
	"extractParamMap": function(paramStr) {
		if(!paramStr || paramStr.length == 0) {
			return null;
		}
		
		const urlParams = new URLSearchParams(paramStr);
		const queryParams = {};

		urlParams.forEach((value, key) => {
		    queryParams[key] = value;
		});
		
		return queryParams;
	},
	
	/**
	 * Extracts info (hash part and query params) from current url.
	 */
	"fetchInfo": function() {
		// Get the current URL
		const url = window.location.href;

		// Extract the part after the #
		const hashIndex = url.indexOf('#');
		let hashPart = null;
		let queryParamPart = window.location.search;

		if (hashIndex !== -1) {
		    // Extract the hash part
		    hashPart = url.substring(hashIndex + 1);
		    
		    // Check if the hash part contains query parameters
		    const queryIndex = hashPart.indexOf('?');
		    if (queryIndex !== -1) {
				queryParamPart = hashPart.substring(queryIndex + 1);
		        hashPart = hashPart.substring(0, queryIndex);
		    }
		}

		return {
		    "hashPart": hashPart,
			"hashParams": this.extractParamMap(hashPart),
		    "queryParams": this.extractParamMap(queryParamPart)
		};
	},
	
	/**
	 * Sets specified url into current url. Also pushes the
	 * modified url to history.
	 * 
	 * urlInfo can have following parameters
	 * 	hashPart = Hash part of the url
	 *  hashParams = map of key-value pairs that will be used as hash (this will be considered only hashPart is not specified)
	 *  queryParams = map of query params that needs to be set
	 */
	"modifyInfo": function(urlInfo) {
		// Get the current URL
		const url = new URL(window.location);
		let modified = false;
		
		// Update the hash part
		if(urlInfo.hashPart) {
			url.hash = urlInfo.hashPart;
			modified = true;
		}
		// If instead of hash, hash-params is specified
		else if(urlInfo.hashParams) {
			// fetch existing hash params
			let urlHash = url.hash;
			
			if(urlHash && urlHash.length > 0 && urlHash.charAt(0) == '#') {
				urlHash = urlHash.substring(1);
			}
			
			let params = this.extractParamMap(urlHash);
			
			// if hash params existing in current url, overwrite with new params
			if(params) {
				for(let key in urlInfo.hashParams) {
					params[key] = urlInfo.hashParams[key];
				}
			}
			// if no hash params exist in url, use new params
			else {
				params = urlInfo.hashParams;
			}
			
			let paramStr = '';
			
			for(let key in params) {
				if(paramStr.length > 0) {
					paramStr += "&";
				}
				
				paramStr += key + "=" + params[key];
			}

			url.hash = paramStr;			
			modified = true;
		}

		// Clear existing search parameters
		url.search = ''; 

		// Append the new query parameters
		if(urlInfo.queryParams) {
			for (const key in urlInfo.queryParams) {
			    if (urlInfo.queryParams.hasOwnProperty(key)) {
			        url.searchParams.set(key, urlInfo.queryParams[key]);
					modified = true;
			    }
			}
		}

		// Update the URL without reloading the page
		if(modified){
			window.history.pushState({}, '', url.toString());
		}
	}
};