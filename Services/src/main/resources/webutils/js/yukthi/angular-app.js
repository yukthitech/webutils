$.application = angular.module("application", ["ngSanitize", "ui.router"]);
$.application["directiveTemplateEngine"] = new TemplateEngine();

$.application.filter('unsafe', ["$sce", function($sce) { 
	return $sce.trustAsHtml; 
}]);

/*
 * Function to define custom angular element directive
 */
$.addElementDirective = function(directiveObj) {
	
	console.log("Adding custom directive - '" + directiveObj.name + "' with priority - " + directiveObj.priority);

	$.application.directive(directiveObj.name, ['$compile', 'actionHelper', 'clientContext', 'validator', 'utils', 'logger', "modelDefService",
	                       function($compile, actionHelper, clientContext, validator, utils, logger, modelDefService) {
		var directive = {};

		directive.restrict = 'E'; /* restrict this directive to elements */
		
		directive.priority = this.priority;

		directive.compile =  $.proxy(function($element, attributes) {
			
			var jqelement = $($element);
			
			//if mandatory attr is specified for tag
			if(this.requiredAttr)
			{
				//ensure all mandatory attr are provided
				for(var i = 0; i < this.requiredAttr.length; i++)
				{
					if(!jqelement.attr(this.requiredAttr[i]))
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
					"utils": utils,
					"logger": logger,
					"modelDefService": modelDefService,
					"$compile": $compile,
					
					"invokeAction": function(actionName, requestEntity, params, callback) {
						return actionHelper.invokeAction(actionName, requestEntity, params, callback);
					},
					
					"executeAsyncSteps": function(asyncContext, functionLst) {
						this.utils.executeAsyncSteps(asyncContext, functionLst);
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
				};

				try
				{
					var processTemplate = $.proxy(function() {
						var context = this.context;
						var $compile = this.$compile;
						var $scope = this.$scope;
						var $element = this.$element;
						
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
							context.$result = e;
							this.directiveObj.postScript(context);
						}
					}, {"directiveObj": this.directiveObj, "element": this.elementHtml, 
						"context": context, "$compile" : $compile,
						"$scope": $scope, "$element": $element,
						"element": this.element});
					
					//if pre process is defined, execute preprocess which is in turn
						//expected to execute processTemplate
					if(this.directiveObj.preProcess)
					{
						this.directiveObj.preProcess(context, processTemplate);
					}
					//if pre process is not specified, execute processTemplate directly
					else
					{
						processTemplate();
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
				"invokeAction": function(actionName, requestEntity, params, callback) {
					return actionHelper.invokeAction(actionName, requestEntity, params, callback);
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
			
			/*
			if(this.recompile)
			{
				$compile(element.contents())($scope);
			}
			*/
			
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
	
	//Cross Browser Fix : directivesParent.children does not work in IE. IE supports childNodes property on node
	var children = directivesParent.children ? directivesParent.children : directivesParent.childNodes;
	var tagName = null;
	var CAP_PATTERN = /([A-Z])/g;
	var child = null, contentChild = null, scriptChild= null;
	var postScript = null, precompileScript = null, preprocess = null;
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
			
			preprocess = child.find("pre-process");
			postScript = child.find("post-script");
			precompileScript = child.find("pre-compile-script");
			
			//if pre process function is defined
			if(preprocess.length > 0)
			{
				eval("function preProcessFunc(context, processTemplate){" + preprocess.text() + "}");
			}
			else
			{
				preProcessFunc = null;
			}
			
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
				preProcess: preProcessFunc,
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
				//"recompile" : (child.attr("recompile") == "true") ? true : false,
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
