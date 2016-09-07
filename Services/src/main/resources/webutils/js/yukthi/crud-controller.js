/*
 * Following are the list of configuration parameters/functions that can be passed as config to extend() method
 * name - Name of the controller
 * modelName - Model Name
 * nameColumn - Name of the column in search results which can be used to identify the selected row
 * modelDailogId - Id of the model dialog to be displayed during Create/update operations
 * saveAction - Action name to be invoked on save
 * updateAction - Action name to be invoked on update
 * readAction - Action name to be invoked for reading model data by passing selected row id.
 * deleteAction - Action name to be invoked for delete operation by passing selected row id.
 * searchQueryName - Optional. If specified, "searchResultSelectionChanged" broadcast events will be limited to this search query name alone.
 * 
 * readOp($scope, actionHelper, callback) - 
 * 				Function that will be used to read model data. If specified, "readAction" will not be used.
 *  
 * deleteOp($scope, actionHelper, callback) - 
 * 				Function that will be used for delete operation. If specified "deleteAction" will not be used.
 * 
 * customizeOp(model, $scope) - 
 * 				Optional. If specified this function will be invoked before saving. This method can be used to
 *				customize the model data before saving (which in some cases, can not be done in standard way).
 * 
 * validateOp(model, $scope) - 
 * 				Optional. If specified this function will be invoked before saving. In case of errors, this function
 * 				should return non-zero array of object, and each object should contain following properties
 * 					field - Name of the field
 * 					message - Error message for the field.
 * 
 * postSaveOp(model, $scope) -
 * 				Optional. If specified this function will be invoked after save or update.
 * 
 * postDeleteOp(selectedId, $scope) -
 * 				Optional. If specified this function will be invoked after delete.
 * 
 * onDisplay(model)-
 * 				Optional. If specified this function will be invoked for edit and add new record.
 * 
 * onChange(field, isExtendedField, model, $scope) - 
 * 				Event method. If specified, this method will be called whenever a field value is changed. This can be
 * 				used to control the ui on value change events.
 */
$.application.factory('crudController', ["logger", "actionHelper", "utils", "validator", "modelDefService", 
                function(logger, actionHelper, utils, validator, modelDefService){
	
	var crudController = {
		"extend" : function($scope, config){
			$scope.name = "" + config.name + $.nextScopeId();
			
			$scope.modelName = config.modelName ? config.modelName : null;
			
			$scope.selectedId = null;
			$scope.selectedName = null;
			
			$scope.searchQuery = null;
			$scope.selectedRow = null;
			
			$scope.dlgModeField = "newModelMode";
			$scope.crudConfig = config;
			
			$scope.defaultValues = {};
			
			$scope.invalidateModelDef = false;
			$scope.extension = null;

			$scope.$watch(function(){
				
				if(!$scope.model)
				{
					return null;
				}
				return $scope.model.name;
			}, function(newVal, oldVal){
			});
			
			$scope.$on('searchResultSelectionChanged', function(event, data){
				if($scope.crudConfig.searchQueryName && data.searchQueryName != $scope.crudConfig.searchQueryName)
				{
					return;
				}
				
				$scope.selectedId = data.selectedRow ? data.selectedRow["id"] : -1;
				$scope.selectedName = data.selectedRow ? data.selectedRow[$scope.crudConfig.nameColumn] : null;
				
				$scope.searchQuery = data.searchQuery;
				$scope.selectedRow = data.selectedRow ? data.selectedRow : null;
				$scope.selectedIndex = data.index;
				
				logger.trace("Row selection changed. Selected id - {}", $scope.selectedId);
			});
			
			$scope.addEntry = function(e) {
				logger.trace("Add {} is triggered..", $scope.crudConfig.name);
				$scope.initErrors("model", true);
				
				if($scope.crudConfig.onBeforeShow)
				{
					$scope.crudConfig.onBeforeShow(true, $scope);
				}
				
				$scope[$scope.dlgModeField] = true;
				$("#" + $scope.crudConfig.modelDailogId +" [yk-read-only='true']").prop('disabled', false);
				$scope.model = {};
				
				for(var fld in $scope.defaultValues)
				{
					$scope.model[fld] = $scope.defaultValues[fld];
				}

				utils.openModal($scope.crudConfig.modelDailogId, {
					context: {"$scope": $scope},
					
					"onShow": function(){
						$("#" + this.$scope.crudConfig.modelDailogId +" input").first().focus();
						
						// on display model dialog
						if(this.$scope.crudConfig.onDisplay)
						{
							this.$scope.crudConfig.onDisplay(this.$scope.model);
						}
					}
				});
			};

			$scope.editEntry = function(e) {
				logger.trace("Edit is triggered..");
			
				//initialize errors along with model-def
				$scope.initErrors("model", true);
				
				if($scope.crudConfig.onBeforeShow)
				{
					$scope.crudConfig.onBeforeShow(false, $scope);
				}

				var readResponse = null;
				
				//callback method, to be called after model is read from server
				var editCallback = $.proxy(function(readResponse, respConfig) {
					if(!readResponse || readResponse.code != 0 || !readResponse.model)
					{
						this.logger.error("Failed to read {} with id - {}", this.$scope.crudConfig.name, this.$scope.selectedId);
						this.utils.alert(["Failed to read {} with id - {}", this.$scope.crudConfig.name, this.$scope.selectedId]);
						return;
					}
					
					$scope[$scope.dlgModeField] = false;
					
					var model = readResponse.model;
					var modelDef = this.$scope.modelDef;
					
					/*
					 * All extended field values are by default String. For int/decimal fields convert string into int/decimal
					 * so that they are visible in ui during binding.
					 */
					if(modelDef.extensionFieldMap && model.extendedFields)
					{
						var extendedField = null;
						
						for(var name in modelDef.extensionFieldMap)
						{
							extendedField = modelDef.extensionFieldMap[name];
							
							if(extendedField.type == 'INTEGER')
							{
								try
								{
									model.extendedFields[name] = parseInt( model.extendedFields[name] );
								}catch(ex)
								{
									model.extendedFields[name] = null;
								}
							}
							else if(extendedField.type == 'DECIMAL')
							{
								try
								{
									model.extendedFields[name] = parseFloat( model.extendedFields[name] );
								}catch(ex)
								{
									model.extendedFields[name] = null;
								}
							}
							else if(extendedField.type == 'BOOLEAN')
							{
								try
								{
									var val = model.extendedFields[name];
									
									if(val)
									{
										model.extendedFields[name] = ( ("" + val).toLowerCase() == "true" ) ? true : false;
									}
								}catch(ex)
								{
									model.extendedFields[name] = null;
								}
							}
						}
					}
					
					this.$scope.model = model;
					this.$scope.$digest();
					
					$("#" + this.$scope.crudConfig.modelDailogId + " [yk-read-only='true']").prop('disabled', true);

					utils.openModal(this.$scope.crudConfig.modelDailogId, {
						context: {"$scope": $scope},
						
						"onShow": function(){
							$("#" + this.$scope.crudConfig.modelDailogId +" input").first().focus();
							console.log("During Edit Display....");
							console.log(this.$scope.name);
							console.log(this.$scope.model);
							
							// on display model dialog
							if(this.$scope.crudConfig.onDisplay)
							{
								this.$scope.crudConfig.onDisplay(this.$scope.model);
							}
							
						}
					});
				}, {"$scope": $scope, "logger": logger, "utils": utils});
				
				//if read operator is specified, use the same
				if($scope.crudConfig.readOp)
				{
					$scope.crudConfig.readOp($scope, actionHelper, editCallback);
				}
				//if read op is not specified, invoke read action
				else
				{
					actionHelper.invokeAction($scope.crudConfig.readAction, null, {
						"id": $scope.selectedId
					}, editCallback);
				}
			};

			$scope.deleteEntry = function(e) {
				logger.trace("Delete {} is triggered: {}", $scope.crudConfig.name,  $scope.selectedId);
				
				var deleteOp = $.proxy(function(confirmed) {
					
					if(!confirmed)
					{
						this.logger.trace("Delete operation is cancelled by user.");
						return;
					}
					
					var deleteCallback = $.proxy(function(baseResponse, respConfig) {
						if(!baseResponse || baseResponse.code != 0)
						{
							this.logger.error("An error occurred while deleting {} - {}. Server Error - {}", 
									this.$scope.crudConfig.name, this.selectedName, baseResponse.message);
							this.utils.alert(["An error occurred while deleting {} - {}. <BR/>Server Error - {}", 
							        this.$scope.crudConfig.name, this.selectedName, baseResponse.message]);
							return;
						}

						this.$scope.$broadcast("rowsModified");

						if(this.$scope.rowsModified)
						{
							this.$scope.rowsModified();
						}
						
						this.logger.trace("Successfully deleted {} '{}'", this.$scope.crudConfig.name, this.selectedName);
						this.utils.info(["Successfully deleted {} '{}'", this.$scope.crudConfig.name, this.selectedName]);
						
						if(this.$scope.crudConfig.postDeleteOp)
						{
							this.$scope.crudConfig.postDeleteOp(this.selectedId, this.$scope);
						}
						
					}, {"$scope": this.$scope, "selectedName": this.$scope.selectedName, 
						"selectedId": this.$scope.selectedId, "logger": this.logger, "utils": this.utils});
					
					//if explicit delete operator is specified, use the same
					if(this.$scope.crudConfig.deleteOp)
					{
						 this.$scope.crudConfig.deleteOp(this.$scope, actionHelper, deleteCallback);
					}
					//if delete op is not specified, invoke delete action
					else
					{
						actionHelper.invokeAction(this.$scope.crudConfig.deleteAction, null, {
							"id": this.selectedId
						}, deleteCallback);
					}
					
				}, {"$scope": $scope, "selectedName": $scope.selectedName, "selectedId": $scope.selectedId, "logger": logger, "utils": utils});
				
				utils.confirm(["Are you sure you want to delete {} '{}' ?", $scope.crudConfig.name, $scope.selectedName], deleteOp);
			};
			
			/**
			 * Initializes the errors object on scope if required
			 */
			$scope.initErrors = function(modelPrefix, clean) {
				
				if(!$scope.errors || clean)
				{
					$scope.errors = {};
				}

				if(!$scope.errors[modelPrefix])
				{
					$scope.errors[modelPrefix] = {};
				}

				if(!$scope.errors[modelPrefix].extendedFields)
				{
					$scope.errors[modelPrefix].extendedFields = {};
				}
				
				if(!$scope.modelDef || $scope.invalidateModelDef)
				{
					//if initModelDef is defined, use to init model def
					if($scope.initModelDef)
					{
						$scope.initModelDef();
					}
					//if initModelDef  is not defined, get it from 
					else
					{
						modelDefService.getModelDef($scope.modelName, $.proxy(function(modelDefResp){
							this.$scope.modelDef = modelDefResp.modelDef;
						}, {"$scope": $scope}), $scope.extension);
					}
					
					$scope.invalidateModelDef = false;
				}
				
				//if clean is requested, force changes to ui
				if(clean)
				{
					try
					{
						$scope.$digest();
					}catch(ex)
					{}
				}
			};
			
			$scope.saveChanges = function(e) {
				logger.trace("{} save is called", $scope.crudConfig.name);
				
				$scope.initErrors("model");
				
				//if customize operator is provided, invoke it 
				if($scope.crudConfig.customizeOp)
				{
					$scope.crudConfig.customizeOp($scope.model, $scope);
				}
				
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
				
				var saveCallback = $.proxy(function(response, respConfig) {
					$('#' + this.$scope.crudConfig.modelDailogId).modal('hide');

					if(response.code == 0)
					{
						this.$scope.$broadcast("rowsModified");
						
						if(this.$scope.rowsModified)
						{
							this.$scope.rowsModified();
						}
					
						if(this.$scope[this.$scope.dlgModeField])
						{
							this.logger.trace("{} is saved successfully!!", this.$scope.crudConfig.name);
							this.utils.info(["{} is saved successfully!!", this.$scope.crudConfig.name]);
						}
						else
						{
							this.logger.trace("{} is updated successfully!!", this.$scope.crudConfig.name);
							this.utils.info(["{} is updated successfully!!", this.$scope.crudConfig.name]);
						}
						
						if(this.$scope.crudConfig.postSaveOp)
						{
							this.$scope.crudConfig.postSaveOp(this.model, this.$scope);
						}
					}
					else
					{
						var op = this.$scope[this.$scope.dlgModeField] ? "Save" : "Update";
						
						this.logger.error("Failed to {} changes.<BR/>ServerError: {}", op, response.message);
						this.utils.alert(["Failed to {} changes.<BR/>ServerError: {}", op, response.message], $.proxy(function(){
							$('#' + this.$scope.crudConfig.modelDailogId).modal('show');
						}, this));
					}
					
				}, {"$scope": $scope, "logger": logger, "utils": utils, "model": $scope.model});
				
				if($scope[$scope.dlgModeField])
				{
					actionHelper.invokeAction($scope.crudConfig.saveAction, $scope.model, null, saveCallback);
				}
				else
				{
					actionHelper.invokeAction($scope.crudConfig.updateAction, $scope.model, null, saveCallback);
				}
			};
			
			$scope.validateField = function(name, modelPrefix) {
				$scope.initErrors(modelPrefix);

				try
				{
					var model = $scope[modelPrefix];
					var modelDef = $scope.modelDef;
					
					if($scope.getModelDef)
					{
						modelDef = $scope.getModelDef(modelPrefix);
					}
					
					validator.validateField(model, modelDef, name);
					$scope.errors[modelPrefix][name] = "";
				}catch(ex)
				{
					if((typeof ex) != 'string')
					{
						logger.error(ex);
					}
					
					$scope.errors[modelPrefix][name] = ex;
				}

				//invoke on change event listener if specified
				if($scope.onChange)
				{
					$scope.onChange(name, false, $scope.model, $scope);
				}
			};

			$scope.validateExtendedField = function(name, modelPrefix) {
				$scope.initErrors("model");

				try
				{
					validator.validateExtendedField($scope.model, $scope.modelDef, name);
					$scope.errors.model.extendedFields[name] = "";
				}catch(ex)
				{
					if((typeof ex) != 'string')
					{
						logger.error(ex);
					}

					$scope.errors.model.extendedFields[name] = ex;
				}

				//invoke on change event listener if specified
				if($scope.onChange)
				{
					$scope.onChange(name, true, $scope.model, $scope);
				}
			};
			
			$scope.getFieldImageUrl = function(name) {
				var fieldVal = null;
				
				//get the field value. This would throw exception, if model is not yet ready
				try
				{
					fieldVal = eval("$scope." + name);
				}catch(ex)
				{
					return null;
				}
				
				if(!fieldVal)
				{
					return;
				}
				
				var imgUrl = actionHelper.actionUrl('files.fetch', {'id': fieldVal.fileId});
				return imgUrl;
			};
			
			$scope.getResultFieldCustomizer = function() {
				return $scope.resultFieldCustomizer;
			};
			
			$scope.getActionUrl = function(actionName, params) {
				return actionHelper.actionUrl(actionName, params);
			};
			
			$scope.resetModelDef = function() {
				$scope.invalidateModelDef = true;
			};
		}
	};

	return crudController;
}]);

