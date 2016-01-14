$.application.controller('mainController', ["$scope", "$rootScope", "logger", "actionHelper", function($scope, $rootScope, logger, actionHelper) {
	
	$scope.activeUser = null;
	
	actionHelper.invokeAction("auth.fetch.activeUser", null, null, $.proxy(function(activeUserResp, resConfig){
		if(!resConfig.success)
		{
			console.error("An error occurred while fetching active user details.");
			console.error(lpageConfig);
			throw "Failed to fetch active user details";
		}
		
		this.$scope.activeUser = activeUserResp.model;
		
		try
		{
			this.$scope.$digest();
		}catch(ex)
		{}
		
	}, {"$scope": $scope}));
	
	$rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams)
	{
		console.log('Moved to state - ' + toState.name + ". Activating tab - " + toState.tab);
		$("#" + toState.tab + "_tab").tab("show");
	});
	
	$scope.hasRole = function(roleName) {
		if(!$scope.activeUser)
		{
			return true;
		}
		
		return ($scope.activeUser.roles.indexOf(roleName) >= 0);
	};
	
}]);
