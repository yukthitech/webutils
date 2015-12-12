$.application.controller('searchQueryController', ["$scope", "actionHelper", "logger", "utils", function($scope, actionHelper, logger, utils) {
	$scope.modelDef = null;
	$scope.searchQueryName = null;
	
	$scope.searchQuery = {};
	$scope.searchResults = [];
	
	$scope.searchResultDef = null;
	
	$scope.selectedIndex = null;
	
	$scope.searchExecuted = false;
	
	$scope.performSearch = function(e) {
		logger.trace("Search is triggered for query - " + $scope.searchQueryName);
		
		//if result definition is not present in controller fetch it from server
		if(!$scope.searchResultDef)
		{
			var resDefresponse = actionHelper.invokeAction("search.fetch.resultDef", null, {"name" : $scope.searchQueryName});
			
			if(!resDefresponse)
			{
				logger.error("An error occurred while executing search query. Please try refreshing the page");
				utils.alert("An error occurred while executing search query. Please try refreshing the page");
				return;
			}
			
			$scope.searchResultDef = resDefresponse.modelDef;
		}
		
		try
		{
			$scope.searchResults = [];
			
			var queryJson = JSON.stringify($scope.searchQuery);
			var request = {
				"queryModelJson" : 	queryJson,
				"pageSize": -1,
				"name": $scope.searchQueryName
			};
			
			var result = actionHelper.invokeAction("search.execute", null, request);

			if(result.searchResults && result.searchResults)
			{
				$scope.searchResults = result.searchResults;
				
				logger.trace("With specified search criteria found results of count - {}", $scope.searchResults.length);
			}
			else
			{
				logger.trace("No results found with specified criteria.");
			}
			
			$scope.searchExecuted = true;
			
			//ensure parent is informed that there is no selected row
			$scope.$emit('searchResultSelectionChanged', {
				"index": -1,
				"selectedRow": null,
				"searchQuery": $scope.searchQuery
			});
		}catch(ex)
		{
			if(ex.message)
			{
				utils.alert("An error occurred while executing search query. Server Error: <BR/>" + ex.message);
				return;
			}
			
			utils.alert("An error occurred while executing search query. Server Error: <BR/>" + ex);
		}

		return false;
	};
	
	$scope.rowSelected = function(index) {
		
		//remove current selection if any
		if($scope.selectedIndex != null)
		{
			$("#" + $scope.searchQueryId + " table.searchResults tr[search-row-id='" + $scope.selectedIndex + "']").removeClass('info');
		}
		
		//set new selected row id
		$scope.selectedIndex = index;
		
		//highlight selected row
		$("#" + $scope.searchQueryId + " table.searchResults tr[search-row-id='" + $scope.selectedIndex + "']").addClass('info');
		
		//send selection event to parent controller
		$scope.$emit('searchResultSelectionChanged', {
			"index": $scope.selectedIndex,
			"selectedRow": $scope.searchResults[$scope.selectedIndex],
			"searchQuery": $scope.searchQuery
		});
	};

	$scope.$on('rowsModified', function(event, data){
		
		if(!$scope.searchExecuted)
		{
			return;
		}
		
		$scope.performSearch();
	});

	$scope.$on('invokeSearch', function(event, data){
		$scope.performSearch();
	});
}]);