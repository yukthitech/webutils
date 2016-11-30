$.application.controller('mainController', ["$scope", "$rootScope", "logger", "actionHelper", "$state", 
                                            function($scope, $rootScope, logger, actionHelper, $state) {
	
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
		
		$(".preProcessHidden").removeClass("preProcessHidden");
		
		// Active user is ready fetch the projects
		if(this.$scope.activeUser.userId > 1)
		{
			$scope.$broadcast("activeUserIsReady");
		}
		
		
	}, {"$scope": $scope}));
	
	$rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams)
	{
		$("#" + toState.tab + "_tab").tab("show");
		
		if($state.current.leftMenu)
		{
			$("#appLeftMenu").css("display", "block");
			$("#mainContentContainer").removeClass("col-md-12");
			$("#mainContentContainer").addClass("col-md-10");
		} 
		else
		{
			$("#appLeftMenu").css("display", "none");
			$("#mainContentContainer").removeClass("col-md-10");
			$("#mainContentContainer").addClass("col-md-12");
		}
		
		$scope.currentState = $state.current;
	});
	
	$scope.getCurrentState = function() {
		return $scope.currentState;
	};
	
	$scope.hasRole = function(roleName) {
		if(!$scope.activeUser)
		{
			return true;
		}
		
		return ($scope.activeUser.roles.indexOf(roleName) >= 0);
	};
	
	$scope.getAppConfiguration = function(name) {
		return $.appConfiguration[name];
	};
	
}]);
