$.application = angular.module("application", []);
$.application["directiveTemplateEngine"] = new TemplateEngine();

$.addElementDirective = function(directiveObj) {
	$.application.directive(directiveObj.name, function($compile) {
		var directive = {};

		directive.restrict = 'E'; /* restrict this directive to elements */

		directive.link = function($scope, $element, attributes) {
			var context = {
				"attributes": attributes,
				"$scope": $scope,
				"element": $($element[0]),
				"invokeAction": function(actionName) {
					return $.makeJsonCall(actionName, null, {cache: false, dataType: "json", "methodType": "GET"});
				} 
			};
			
			var html = $.application["directiveTemplateEngine"].processTemplate(context, $element[0].localName);
			var e = $compile(html)($scope);
			$element.replaceWith(e);
        };

		return directive;
	});
};

$.loadCustomDirectives = function(templateFilePath) {
	var directivesParent = $.makeJsonCall(templateFilePath, null, {cache: false, dataType: "xml", "methodType": "GET"});
	directivesParent = directivesParent.documentElement;
	var children = directivesParent.children;
	var tagName = null;
	var CAP_PATTERN = /([A-Z])/g;
	var child = null, contentChild = null;
	var postScript = null;
	
	for(var i = 0; i < children.length; i++)
	{
		if(children[i].nodeName == "directive")
		{
			child = $(children[i]);
			childName = child.attr("name");
			
			tagName = childName.replace(CAP_PATTERN, '-$1');
			tagName = tagName.toLowerCase();
			
			contentChild = child.find("content").first();
			postScript = child.find("post-script").first().text();
			
			$.application["directiveTemplateEngine"].addTemplate(tagName, $(contentChild));
			$.addElementDirective({
				name : childName,
				postScript: postScript
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

