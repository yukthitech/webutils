$.application.controller('mainController', ["$scope", "$rootScope", "logger", "actionHelper", "$state", "utils",
                                            function($scope, $rootScope, logger, actionHelper, $state, utils) {
	
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
			logger.debug("Broadcasting activeUserIsReady event...");
			$scope.$broadcast("activeUserIsReady");
		}
		else
		{
			logger.warn("Though user is assigned to scope, user-details is found without valid id.");
		}
		
	}, {"$scope": $scope}));
	
	$rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams)
	{
		$("#" + toState.tab + "_tab").tab("show");
		console.log("Current statue: ", $state.current);
		
		
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
	
	$scope.sendMail = function(template, mail) {
		$scope.$broadcast("sendMail", {"template": template, "mail": mail});
	};
	
	$scope.displayMulitResponseDialog = function(data) {
		$scope.$broadcast("displayMultiResponseDialog", data);
	};
	
	/**
	 * Copies the source element inner text to clipboard.
	 */
	$scope.copyToClipboard = function($event, mssg) {
		var elem = $event.currentTarget;
		
		clipboard.writeText($(elem).get(0).innerText);

		if(mssg && mssg.length > 0)
		{
			utils.info([mssg]);
		}
		
		return false;
	};
	
	/**
	 * Main controller init method, which is current a place holder.
	 */
	$scope.initMainController = function() {
	};
	
}]);
