$.application = angular.module("application", ["ngSanitize", "ui.router"]);
$.application["directiveTemplateEngine"] = new TemplateEngine();


$.application.controller('mainController', ["$scope", "$rootScope", function($scope, $rootScope) {
	$rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams)
	{
		console.log('Moved to state - ' + toState.name + ". Activating tab - " + toState.tab);
		$("#" + toState.tab + "_tab").tab("show");
	});
}]);


/*
 * Function to define custom angular element directive
 */
$.addElementDirective = function(directiveObj) {
	
	console.log("Adding custom directive - '" + directiveObj.name + "' with priority - " + directiveObj.priority);

	$.application.directive(directiveObj.name, ['$compile', 'actionHelper', 'clientContext', 'validator', function($compile, actionHelper, clientContext, validator) {
		var directive = {};

		directive.restrict = 'E'; /* restrict this directive to elements */
		
		directive.priority = this.priority;

		directive.compile =  $.proxy(function($element, attributes) {
			//if mandatory attr is specified for tag
			if(this.requiredAttr)
			{
				//ensure all mandatory attr are provided
				for(var i = 0; i < this.requiredAttr.length; i++)
				{
					if(!attributes[this.requiredAttr[i]])
					{
						throw "Mandatory attribute '" + this.requiredAttr[i] + "' is not defined for tag <" + this.tagName + ">"; 
					}
				}
			}
			
			var elementHtml = $('<div>').append( $($element[0]).clone() ).html();
			elementHtml = $(elementHtml);
			$($element[0]).html("");
			
			var linkFunc = $.proxy(function($scope, $element, attributes) {
				var element = this.element;
				
				var context = {
					"attributes": attributes,
					"$scope": $scope,
					"element": element,
					"actualElement": $($element[0]),
					"clientContext": clientContext,
					"actionHelper": actionHelper,
					"validator": validator,
					"invokeAction": function(actionName, requestEntity, params) {
						return actionHelper.invokeAction(actionName, requestEntity, params);
					},
					"bodyAsHtml" : function() {
						return this.element.html();
					},
					"flexAttr" : function(name) {
						var attrVal = this.element.attr(name);
						
						if(!attrVal || attrVal.length <= 0)
						{
							attrVal = this.element.find(name).html();
						}
						
						if(!attrVal)
						{
							return "";
						}
						
						return attrVal;
					},
					"attr" : function(name, defVal, elem) {
						if(!elem)
						{
							elem = this.element;
						}
						
						var attrVal = elem.attr(name);
						
						return (!attrVal || attrVal.length == 0) ? defVal : attrVal;
					},
					"attrStr": function() {
						var attrStr = "";
						$.each(this.element.get(0).attributes, function(i, attr){
							attrStr += ' ' + attr.name + '="' + attr.value + '"';
						});
						
						return attrStr;
					},
					"log": function(mssg) {
						console.log(mssg);
					}
				};

				try
				{
					var html = $.application["directiveTemplateEngine"].processTemplate(context, this.element.get(0).localName);
					html = $(html);
					
					//if pre compile script is specified, execute it after making result available on context
					if(this.directiveObj.preCompileScript)
					{
						context.$result = html;
						this.directiveObj.preCompileScript(context);
					}
					
					var e = $compile(html)($scope);
					$element.replaceWith(e);
				
					if(this.directiveObj.postScript)
					{
						this.directiveObj.postScript(context);
					}
				}catch(ex)
				{
					console.error("An error occurred while processing directive - " + $element[0].localName);
					console.error(ex);
					
					throw ex;
				}
			}, {"directiveObj": directiveObj, "element": elementHtml});
			
			return linkFunc;
		}, directiveObj);
			

		return directive;
	}]);
};

/*
 * Function to define custom angular attribute directive
 */
$.addAttributeDirective = function(directiveObj) {
	
	console.log("Adding custom attribute directive - '" + directiveObj.name + "' with priority - " + directiveObj.priority);

	$.application.directive(directiveObj.name, ['$compile', 'actionHelper', 'clientContext', function($compile, actionHelper, clientContext) {
		var directive = {};

		directive.restrict = 'A'; /* restrict this directive to attributes */

		directive.link = $.proxy(function($scope, $element, attributes) {
			var element = $($element[0]);
			
			//fetch the attribute value and add to context
			var attrValue = element.attr(this.attrName);
			
			var context = {
				"attributes": attributes,
				"$scope": $scope,
				"element": element,
				"clientContext": clientContext,
				"actionHelper": actionHelper,
				"attributeValue" : attrValue,
				"invokeAction": function(actionName, requestEntity, params) {
					return actionHelper.invokeAction(actionName, requestEntity, params);
				},
				"bodyAsHtml" : function() {
					return this.element.html();
				},
				"attr" : function(name, defVal, elem) {
					if(!elem)
					{
						elem = this.element;
					}
					
					var attrVal = elem.attr(name);
					
					return (!attrVal || attrVal.length == 0) ? defVal : attrVal;
				},
			};
			
			//remove directive attribute to avoid repeated execution
			//element.removeAttr(this.attrName);
			
			//eval directive script
			this.scriptFunc(context);
			
			if(this.recompile)
			{
				$compile(element.contents())($scope);
			}
			
			//IMP NOTE: Done do anything here as eval() can have return statement (and if it does this part will never
					//gets executed) 
        }, directiveObj);

		return directive;
	}]);
};

/*
 * Loads specified template path and registers custom directives
 */
$.loadCustomDirectives = function(templateFilePath) {
	var directivesParent = $.makeJsonCall(templateFilePath, null, {cache: false, dataType: "xml", "methodType": "GET"});
	directivesParent = directivesParent.documentElement;
	var children = directivesParent.children;
	var tagName = null;
	var CAP_PATTERN = /([A-Z])/g;
	var child = null, contentChild = null, scriptChild= null;
	var postScript = null, precompileScript = null;
	var priority = null;
	
	for(var i = 0; i < children.length; i++)
	{
		if(children[i].nodeName == "directive")
		{
			child = $(children[i]);
			childName = child.attr("name");
			requiredAttr = child.attr("required-attr");
			
			priority = child.attr("priority");
			priority = priority ? parseInt(priority) : 1005;
			
			
			if(requiredAttr && requiredAttr.length > 0)
			{
				requiredAttr = requiredAttr.split(/\s*\,\s*/g);
			}
			else
			{
				requiredAtrr = [];
			}
			
			tagName = childName.replace(CAP_PATTERN, '-$1');
			tagName = tagName.toLowerCase();
			
			contentChild = child.find("content").first();
			postScript = child.find("post-script");
			precompileScript = child.find("pre-compile-script");
			
			//if post script is defined
			if(postScript.length > 0)
			{
				//create dynamic function with name scriptFunc which has code from template
				eval("function scriptFunc(context){" + postScript.text() + "}");
			}
			else
			{
				scriptFunc = null;
			}

			if(precompileScript.length > 0)
			{
				//create dynamic function with name precompileScriptFunc which has code from template
				eval("function precompileScriptFunc(context){" + precompileScript.text() + "}");
			}
			else
			{
				precompileScriptFunc = null;
			}

			$.application["directiveTemplateEngine"].addTemplate(tagName, $(contentChild));
			$.addElementDirective({
				name : childName,
				postScript: scriptFunc,
				preCompileScript: precompileScriptFunc, 
				tagName: tagName,
				requiredAttr: requiredAttr,
				priority: priority
			});
		}

		if(children[i].nodeName == "attr-directive")
		{
			child = $(children[i]);
			childName = child.attr("name");
			
			tagName = childName.replace(CAP_PATTERN, '-$1');
			tagName = tagName.toLowerCase();
			
			priority = child.attr("priority");
			priority = priority ? parseInt(priority) : 1005;

			scriptChild = child.find("script").first();
			
			//create dynamic function with name scriptFunc which has code from template
			eval("function scriptFunc(context){" + scriptChild.text() + "}");
			
			$.addAttributeDirective({
				name : childName,
				scriptFunc: scriptFunc,
				attrName: tagName,
				"recompile" : (child.attr("recompile") == "true") ? true : false,
				priority: priority
			});
		}

		if(children[i].nodeName == "template")
		{
			child = $(children[i]);
			childName = child.attr("name");
			$.application["directiveTemplateEngine"].addTemplate(childName, child);
		}
	}
	
};

/*
 * Load the custom directive templates at starting
 */
function init()
{
	if(!$.appConfiguration)
	{
		console.error("No appliation configuration is defined.");
		return;
	}
	
	//Load templates
	var templateFiles = $.appConfiguration.templates;

	if(templateFiles)
	{
		for(var i = 0; i < templateFiles.length; i++)
		{
			console.log("Loading template file - " + templateFiles[i]);
			$.loadCustomDirectives(templateFiles[i]);
		}
	}
};

init();
