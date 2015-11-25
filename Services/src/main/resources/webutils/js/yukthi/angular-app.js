$.application = angular.module("application", ["ngSanitize"]);
$.application["directiveTemplateEngine"] = new TemplateEngine();

/*
 * Function to define custom angular element directive
 */
$.addElementDirective = function(directiveObj) {
	
	console.log("Adding custom directive - " + directiveObj.name);

	$.application.directive(directiveObj.name, function($compile) {
		var directive = {};

		directive.restrict = 'E'; /* restrict this directive to elements */

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
			
			var linkFunc = $.proxy(function($scope, $element, attributes) {
				var context = {
						"attributes": attributes,
						"$scope": $scope,
						"element": $($element[0]),
						"invokeAction": function(actionName, type) {
							
							if(!type)
							{
								type = 'json';
							}
							
							return $.makeJsonCall(actionName, null, {cache: false, dataType: type, "methodType": "GET"});
						},
						"bodyAsHtml" : function() {
							return this.element.html();
						}
					};
					
				var html = $.application["directiveTemplateEngine"].processTemplate(context, $element[0].localName);
				var e = $compile(html)($scope);
				$element.replaceWith(e);
				
				if(this.postScript)
				{
					this.postScript(context);
				}
			}, this);
			
			return linkFunc;
		}, directiveObj);
			

		return directive;
	});


	/*
	directive.link = $.proxy(function($scope, $element, attributes) {
		var context = {
			"attributes": attributes,
			"$scope": $scope,
			"element": $($element[0]),
			"invokeAction": function(actionName, type) {
				
				if(!type)
				{
					type = 'json';
				}
				
				return $.makeJsonCall(actionName, null, {cache: false, dataType: type, "methodType": "GET"});
			},
			"bodyAsHtml" : function() {
				return this.element.html();
			}
		};
		
		var html = $.application["directiveTemplateEngine"].processTemplate(context, $element[0].localName);
		var e = $compile(html)($scope);
		$element.replaceWith(e);
		
		if(this.postScript)
		{
			this.postScript(context);
		}
		
    }, directiveObj);
    */
};

/*
 * Function to define custom angular attribute directive
 */
$.addAttributeDirective = function(directiveObj) {
	
	console.log("Adding custom attribute directive - " + directiveObj.name);

	$.application.directive(directiveObj.name, function($compile) {
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
				"attributeValue" : attrValue,
				"invokeAction": function(actionName, type) {
					
					if(!type)
					{
						type = 'json';
					}
					
					return $.makeJsonCall(actionName, null, {cache: false, dataType: type, "methodType": "GET"});
				},
				"bodyAsHtml" : function() {
					return this.element.html();
				}
			};
			
			//remove directive attribute to avoid repeated execution
			//element.removeAttr(this.attrName);
			
			//eval directive script
			this.scriptFunc(context);
			
			//IMP NOTE: Done do anything here as eval() can have return statement (and if it does this part will never
					//gets executed) 
        }, directiveObj);

		return directive;
	});
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
	var postScript = null;
	
	for(var i = 0; i < children.length; i++)
	{
		if(children[i].nodeName == "directive")
		{
			child = $(children[i]);
			childName = child.attr("name");
			requiredAttr = child.attr("required-attr");
			
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

			$.application["directiveTemplateEngine"].addTemplate(tagName, $(contentChild));
			$.addElementDirective({
				name : childName,
				postScript: scriptFunc,
				tagName: tagName,
				requiredAttr: requiredAttr
			});
		}

		if(children[i].nodeName == "attr-directive")
		{
			child = $(children[i]);
			childName = child.attr("name");
			
			tagName = childName.replace(CAP_PATTERN, '-$1');
			tagName = tagName.toLowerCase();
			
			scriptChild = child.find("script").first();
			
			//create dynamic function with name scriptFunc which has code from template
			eval("function scriptFunc(context){" + scriptChild.text() + "}");
			
			$.addAttributeDirective({
				name : childName,
				scriptFunc: scriptFunc,
				attrName: tagName
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

