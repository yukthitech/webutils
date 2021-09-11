var ALERTS_DLG_ID = "webutilsAlertDialog";
var CONFIRM_DLG_ID = "webutilsConfirmDialog";
var INFO_BOX_ID = "webutilsInfoBox";
var IN_PRGORESS_DLG_ID = "webutilsInProgressDialog";

$.appConfiguration = {
	"debugEnabled": true,
	"errorEnabled": true,
	"traceEnabled": false
};

$.dataTypeMapping = {
	"STRING": {"componentType": "yk-input-field", "inputType": "text"},
	"PASSWORD": {"componentType": "yk-input-field", "inputType": "password"},
	"MULTI_LINE_STRING": {"componentType": "yk-textarea-field", "inputType": "text"},	
	"LIST_OF_VALUES": {"componentType": "yk-lov-field", "inputType": "text"}	
};

$.utils = {
	"ARG_PATERN" : /\{\}/g,
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
	
	"divideModelRows": function(modelDef, modelFieldRows, columnCount) {
		if(modelFieldRows.length > 0)
		{
			modelFieldRows.splice(0, modelFieldRows.length);
		}
		
		var rowCount = modelDef.fields.length / columnCount;
		var nextRow = {"fields": [], "index": 0};
		var curCol = 0;
		var fldIdx = 0;
		var colSize = 12 / columnCount;
		
		for(var fieldDef of modelDef.fields)
		{
			if(fieldDef.displayable == false)
			{
				continue;
			}
			
			var dataType = fieldDef.fieldType;
			var typeMapping = $.dataTypeMapping[dataType];
			
			if(!typeMapping)
			{
				dataType = "STRING";
				typeMapping = $.dataTypeMapping[dataType];
			}
			
			fieldDef.dataType = dataType;
			fieldDef.inputType = typeMapping["inputType"];
			fieldDef.componentType = typeMapping["componentType"];
			fieldDef.index = fldIdx;
			fieldDef.size = colSize;
			
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
			
			nextRow.fields.push(fieldDef);
			curCol ++;
			
			if(curCol >= columnCount)
			{
				modelFieldRows.push(nextRow);
				curCol = 0;
				nextRow = {"fields": [], "index": modelFieldRows.length};
			}
			
			fldIdx++;
		}
		
		if(nextRow.fields.length > 0)
		{
			modelFieldRows.push(nextRow);
		}
	},
	
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
	
	"confirm" : function(message, callback) {
		this.ykDialogs.displayConfirm(message, callback);
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


$.logger = {
	"debugEnabled" : ($.appConfiguration.debugEnabled ? true : false),
	"errorEnabled" : ($.appConfiguration.errorEnabled ? true : false),
	"traceEnabled" : ($.appConfiguration.traceEnabled ? true : false),

	"log": function(prefix, message, args) {
		//if message is not an object (like error etc), print the object directly
		if((typeof message) != 'string')
		{
			console.log(prefix);
			console.log(message);
			return;
		}
		
		var finalMsg = $.utils.format(message, args);
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


$.modalManager = {
	"modalStack": [],
		
	"openModal": function(id, config) {
		var dlg = $("#" + id);
		
		if(dlg.length < 0)
		{
			throw "No modal found with specified id - " + id;
		}
		
		$('#' + id).off('shown.bs.modal').on('shown.bs.modal', $.proxy(function (e) {
			this.modalStack.push(this.id);
			
			var zIndex = 1040 + (10 * $('.modal:visible').length);
			$("#" + this.id).css('z-index', zIndex);
			
			if(this.config && this.config.onShow)
			{
				var onShow = this.config.onShow;
				
				if(this.config.context)
				{
					onShow = $.proxy(onShow, this.config.context);
				}
				
				onShow();
			}

			//setTimeout because the .modal-backdrop isn't created when the 
			//  event show.bs.modal is triggered
			setTimeout(function() {
		        $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
		    }, 2);
			
		}, {"modalStack": this.modalStack, "config": config, "id": id}));

		$('#' + id).off('hidden.bs.modal').on('hidden.bs.modal', $.proxy(function (e) {
			var idx = this.modalStack.indexOf(this.id);
			
			if(idx < 0)
			{
				return;
			}
			
			this.modalStack.splice(idx, 1);
			
			if(this.modalStack.length > 0)
			{
				$('body').addClass('modal-open');
			}
			else
			{
				$('body').removeClass('modal-open');
				$('body').css('padding', "0px");
			}
			
			if(this.config && this.config.onHide)
			{
				var onHide = this.config.onHide;
				
				if(this.config.context)
				{
					onHide = $.proxy(onHide, this.config.context);
				}
				
				onHide();
			}
		}, {"modalStack": this.modalStack, "config": config, "id": id}));
		
		$('#' + id).modal('show');
	}
};

$.userService = {
	"getUserDetails": function(successCallback, errorCallback) {
		var json = sessionStorage.getItem("userDetails");
		
		if(json)
		{
			$.userDetails = JSON.parse(json);
			
			if(successCallback)
			{
				successCallback($.userDetails);
			}
			
			return;
		}
		
		var onUserFetch = $.proxy(function(result) {
			$.userDetails = result.response.model;
			sessionStorage.setItem("userDetails", JSON.stringify($.userDetails));
			
			if(this.successCallback)
			{
				this.successCallback($.userDetails);
			}
		}, {"successCallback": successCallback});
		
		var onUserFetchError = $.proxy(function(result) {
			if(this.errorCallback)
			{
				this.errorCallback();
			}
		}, {"errorCallback": errorCallback});

		$.restService.invokeGet("/api/auth/user", null, 
				{"context": this, "onSuccess": onUserFetch, "onError": onUserFetchError, "async": false});
	},
	
	"clearSession": function() {
		$.userDetails = null;
		sessionStorage.removeItem("userDetails");
		sessionStorage.removeItem("authToken");
	},
	
	"hasRoles": function(roleStr) {
		if(!roleStr)
		{
			return true;
		}
		
		if(!$.userDetails)
		{
			this.getUserDetails();
			
			if(!$.userDetails)
			{
				return false;
			}
		}
		
		var roles = roleStr.split(/\s*\,\s*/);
		
		for(var role of roles)
		{
			if($.userDetails.roles.indexOf(role) >= 0)
			{
				return true;
			}
		}
		
		return false;
	}
};
