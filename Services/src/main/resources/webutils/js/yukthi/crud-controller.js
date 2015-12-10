$.application.factory('crudController', ["logger", "actionHelper", "utils", function(logger, actionHelper, utils){
	var crudController = {
		"extend" : function($scope, config){
			$scope.selectedId = null;
			$scope.selectedName = null;
			
			$scope.searchQuery = null;
			$scope.selectedRow = null;
			
			$scope.newModelMode = false;
			$scope.crudConfig = config;
			
			$scope.$on('searchResultSelectionChanged', function(event, data){
				$scope.selectedId = data.selectedRow ? data.selectedRow["id"] : -1;
				$scope.selectedName = data.selectedRow ? data.selectedRow[$scope.crudConfig.nameColumn] : null;
				
				$scope.searchQuery = data.searchQuery;
				$scope.selectedRow = data.selectedRow ? data.selectedRow : null;
				
				logger.trace("Row selection changed. Selected id - {}", $scope.selectedId);
			});
			
			$scope.addEntry = function(e) {
				logger.trace("Add {} is triggered..", $scope.crudConfig.name);
				
				$scope.newModelMode = true;
				$("#" + $scope.crudConfig.modelDailogId +" [yk-read-only='true']").prop('disabled', false);
				$scope.model = {};
				
				$("#" + $scope.crudConfig.modelDailogId).modal({
					  show: true
				});
			};

			$scope.editEntry = function(e) {
				logger.trace("Edit is triggered..");
				
				var readResponse = actionHelper.invokeAction($scope.crudConfig.readAction, null, {
					"id": $scope.selectedId
				});
				
				if(!readResponse || readResponse.code != 0 || !readResponse.model)
				{
					logger.error("Failed to read {} with id - {}", $scope.crudConfig.name, $scope.selectedId);
					utils.alert(["Failed to read {} with id - {}", $scope.crudConfig.name, $scope.selectedId]);
					return;
				}
				
				$scope.newModelMode = false;
				
				$scope.model = readResponse.model;
				$scope.model.required = ($scope.model.required == true) ? "true" : "false";
				
				$("#" + $scope.crudConfig.modelDailogId + " [yk-read-only='true']").prop('disabled', true);

				$('#' + $scope.crudConfig.modelDailogId).modal({
					  show: true
				});
			};

			$scope.deleteEntry = function(e) {
				logger.trace("Delete {} is triggered: {}", $scope.crudConfig.name,  $scope.selectedId);
				
				var deleteOp = function(confirmed) {
					
					if(!confirmed)
					{
						logger.trace("Delete operation is cancelled by user.");
						return;
					}
					
					var baseResponse = actionHelper.invokeAction($scope.crudConfig.deleteAction, null, {
						"id": $scope.selectedId
					});
					
					if(!baseResponse || baseResponse.code != 0)
					{
						logger.error("An error occurred while deleting {} - {}. Server Error - {}", $scope.crudConfig.name, $scope.selectedName, baseResonse.message);
						utils.alert(["An error occurred while deleting {} - {}. <BR/>Server Error - {}", $scope.crudConfig.name, $scope.selectedName, baseResonse.message]);
					}
					
					$scope.$broadcast("rowsModified");
					
					logger.trace("Successfully deleted {} '{}'", $scope.crudConfig.name, $scope.selectedName);
					utils.info(["Successfully deleted {} '{}'", $scope.crudConfig.name, $scope.selectedName]);
				};
				
				utils.confirm(["Are you sure you want to delete {} '{}' ?", $scope.crudConfig.name, $scope.selectedName], deleteOp);
			};
			
			$scope.saveChanges = function(e) {
				logger.trace("{} save is called", $scope.crudConfig.name);
				
				try
				{
					var response = null;
					
					if($scope.newModelMode)
					{
						response = actionHelper.invokeAction($scope.crudConfig.saveAction, $scope.model);
					}
					else
					{
						response = actionHelper.invokeAction($scope.crudConfig.updateAction, $scope.model);
					}
					
					$('#' + $scope.crudConfig.modelDailogId).modal('hide');

					if(response.code == 0)
					{
						$scope.$broadcast("rowsModified");
						
						logger.trace("{} is saved successfully!!", $scope.crudConfig.name);
						utils.info(["{} is saved successfully!!", $scope.crudConfig.name]);
					}
					else
					{
						logger.error("Failed to save changes.<BR/>ServerError: {}", response.message);
						utils.alert(["Failed to save changes.<BR/>ServerError: {}", response.message]);
					}
				}catch(ex)
				{
					if(ex.message)
					{
						logger.error("An error occurred while saving extension field.<BR/>Server Error: {}", ex.message);
						utils.alert(["An error occurred while saving extension field.<BR/>Server Error: {}", ex.message]);
						return;
					}
					
					logger.error("An error occurred while saving extension field.<BR/>Server Error: {}", ex);
					utils.alert(["An error occurred while saving extension field.<BR/>Server Error: {}", ex]);
				}
			};
			
			
		}
	};

	return crudController;
}]);

