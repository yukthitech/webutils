var NEXT_ID = 1;

$.application.controller('searchQueryController', ["$scope", "clientContext", function($scope, clientContext) {
	$scope.modelDef = null;
	$scope.id = NEXT_ID++;
	
	$scope.searchQuery={};
	
	$scope.performSearch = function(e) {
		console.log("search is triggered..");
		return false;
	};
}]);