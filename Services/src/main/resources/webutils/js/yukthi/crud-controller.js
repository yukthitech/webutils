$.application.factory('crudController', ["logger", "actionHelper", "utils", "validator", function(logger, actionHelper, utils, validator){
	var crudController = {
		"extend" : function($scope, config){
			$scope.name = "" + config.name + $.nextScopeId();
			
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
				
				var readResponse = null;
				
				//if read operator is specified, use the same
				if($scope.crudConfig.readOp)
				{
					readResponse = $scope.crudConfig.readOp($scope, actionHelper);
				}
				//if read op is not specified, invoke read action
				else
				{
					readResponse = actionHelper.invokeAction($scope.crudConfig.readAction, null, {
						"id": $scope.selectedId
					});
				}
				
				if(!readResponse || readResponse.code != 0 || !readResponse.model)
				{
					logger.error("Failed to read {} with id - {}", $scope.crudConfig.name, $scope.selectedId);
					utils.alert(["Failed to read {} with id - {}", $scope.crudConfig.name, $scope.selectedId]);
					return;
				}
				
				$scope.newModelMode = false;
				
				$scope.model = readResponse.model;
				
				$("#" + $scope.crudConfig.modelDailogId + " [yk-read-only='true']").prop('disabled', true);

				$('#' + $scope.crudConfig.modelDailogId).modal({
					  show: true
				});
			};

			$scope.deleteEntry = function(e) {
				logger.trace("Delete {} is triggered: {}", $scope.crudConfig.name,  $scope.selectedId);
				
				var deleteOp = $.proxy(function(confirmed) {
					
					if(!confirmed)
					{
						this.logger.trace("Delete operation is cancelled by user.");
						return;
					}
					
					var baseResponse = null;
					
					//if explicit delete operator is specified, use the same
					if(this.$scope.crudConfig.deleteOp)
					{
						baseResponse = this.$scope.crudConfig.deleteOp(this.$scope, actionHelper);
					}
					//if delete op is not specified, invoke delete action
					else
					{
						baseResponse = actionHelper.invokeAction(this.$scope.crudConfig.deleteAction, null, {
							"id": this.selectedId
						});
					}
					
					if(!baseResponse || baseResponse.code != 0)
					{
						this.logger.error("An error occurred while deleting {} - {}. Server Error - {}", 
								this.$scope.crudConfig.name, this.selectedName, baseResonse.message);
						this.utils.alert(["An error occurred while deleting {} - {}. <BR/>Server Error - {}", 
						        this.$scope.crudConfig.name, this.selectedName, baseResonse.message]);
						return;
					}
					
					this.$scope.$broadcast("rowsModified");
					
					this.logger.trace("Successfully deleted {} '{}'", this.$scope.crudConfig.name, this.selectedName);
					this.utils.info(["Successfully deleted {} '{}'", this.$scope.crudConfig.name, this.selectedName]);
				}, {"$scope": $scope, "selectedName": $scope.selectedName, "selectedId": $scope.selectedId, "logger": logger, "utils": utils});
				
				utils.confirm(["Are you sure you want to delete {} '{}' ?", $scope.crudConfig.name, $scope.selectedName], deleteOp);
			};
			
			/**
			 * Initializes the errors object on scope if required
			 */
			$scope.initErrors = function() {
				if(!$scope.errors)
				{
					$scope.errors = {};
				}

				if(!$scope.errors.model)
				{
					$scope.errors.model = {};
				}

				if(!$scope.errors.model.extendedFields)
				{
					$scope.errors.model.extendedFields = {};
				}
			};
			
			$scope.saveChanges = function(e) {
				logger.trace("{} save is called", $scope.crudConfig.name);
				
				try
				{
					$scope.initErrors();
					
					if(!validator.validateModel($scope.model, $scope.modelDef, $scope.errors.model))
					{
						utils.alert("Please correct the errors and then try!", function(){
							$('body').addClass('modal-open');
						});
						
						return;
					}
					
					//if validate operator is configured
					if($scope.crudConfig.validateOp)
					{
						//invoke model specific validations
						var errors = $scope.crudConfig.validateOp($scope.model, $scope);
						
						//if errors are found, update error model and return
						if(errors && errors.length > 0)
						{
							for(var i = 0; i < errors.length; i++)
							{
								$scope.errors.model[errors[i].field] = errors[i].message; 
							}
							
							utils.alert("Please correct the errors and then try!", function(){
								$('body').addClass('modal-open');
							});
							
							return;
						}
					}
					
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
					
						if($scope.newModelMode)
						{
							logger.trace("{} is saved successfully!!", $scope.crudConfig.name);
							utils.info(["{} is saved successfully!!", $scope.crudConfig.name]);
						}
						else
						{
							logger.trace("{} is updated successfully!!", $scope.crudConfig.name);
							utils.info(["{} is updated successfully!!", $scope.crudConfig.name]);
						}
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
			
			$scope.validateField = function(name, modelPrefix) {
				$scope.initErrors();

				try
				{
					var model = $scope[modelPrefix];
					validator.validateField(model, $scope.modelDef, name);
					$scope.errors.model[name] = "";
				}catch(ex)
				{
					if((typeof ex) != 'string')
					{
						logger.error(ex);
					}
					
					$scope.errors.model[name] = ex;
				}
			};

			$scope.validateExtendedField = function(name, modelPrefix) {
				$scope.initErrors();

				try
				{
					var model = $scope[modelPrefix];
					validator.validateExtendedField(model, $scope.modelDef, name);
					$scope.errors.model.extendedFields[name] = "";
				}catch(ex)
				{
					if((typeof ex) != 'string')
					{
						logger.error(ex);
					}

					$scope.errors.model.extendedFields[name] = ex;
				}
			};
		}
	};

	return crudController;
}]);

