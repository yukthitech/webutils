/**
 * This controller scope will have following attributes
 * 	searchQueryId - Html id of search query panel.
 * 	searchQueryName - Name of the search query
 */
$.application.controller('searchQueryController', ["$scope", "actionHelper", "logger", "utils", "validator", "modelDefService", "clientContext",
                          function($scope, actionHelper, logger, utils, validator, modelDefService, clientContext) {
	$scope.name = "searchQueryControllerScope-" + $.nextScopeId();
	$scope.modelDef = null;
	$scope.searchQueryName = null;
	
	$scope.searchQuery = {};
	$scope.searchResults = [];
	
	$scope.searchResultDef = null;
	
	$scope.selectedIndex = null;
	
	$scope.searchExecuted = false;
	
	$scope.defaultValues = {};
	
	$scope.searchSettings = {};
	$scope.currentPageNo = 1;
	$scope.pageCount = 1;
	$scope.recordCount = -1;
	
	$scope.init = function(){
		for(var fld in $scope.defaultValues)
		{
			$scope.searchQuery[fld] = $scope.defaultValues[fld];
		}
	};
	
	/**
	 * Initializes the errors object on scope if required
	 */
	$scope.initErrors = function(modelPrefix) {
		if(!$scope.errors)
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
		
		if(!$scope.modelDef)
		{
			modelDefService.getSearchQueryDef($scope.searchQueryName, $.proxy(function(modelDefResp){
				this.$scope.modelDef = modelDefResp.modelDef;
			}, {"$scope": $scope}));
		}
	};

	$scope.performSearch = function(searchCriteria) {
		logger.trace("Search is triggered for query - " + $scope.searchQueryName);
		
		//TODO: Move init errors to post rendering
		$scope.initErrors("searchQuery");
		
		try
		{
			$scope.customizeSearchQuery($scope.searchQuery);
		}catch(ex)
		{
			//ignore customization 
		}
		
		if(!validator.validateModel($scope.searchQuery, $scope.modelDef, $scope.errors.searchQuery))
		{
			utils.alert("Please correct the errors and then try!");
			return;
		}
		
		if(!searchCriteria)
		{
			searchCriteria = {
				"pageNumber": 1,
				"fetchCount": true
			};
		}
		
		var stepExecContext = {
			"actionHelper": actionHelper,
			"$scope": $scope,
			"logger": logger,
			"utils": utils,
			"searchCriteria": searchCriteria
		};
		
		utils.executeAsyncSteps(stepExecContext, [
			
			//executes the search query
			function(callback) {
				var queryJson = JSON.stringify(this.$scope.searchQuery);
				var request = {
					"queryModelJson" : 	queryJson,
					"pageNumber": this.searchCriteria.pageNumber,
					"name": this.$scope.searchQueryName,
					"fetchCount": this.searchCriteria.fetchCount
				};
				
				this.actionHelper.invokeAction("search.execute", null, request, callback);
			},
			
			//populate the search results on ui
			function(callback, result, respConfig) {
				if(result.searchResults && result.searchResults)
				{
					this.logger.trace("With specified search criteria found results of count - {}", $scope.searchResults.length);
				}
				else
				{
					this.logger.trace("No results found with specified criteria.");
				}
				
				var searchResultDef = {"fields": []};
				this.$scope.searchResults = [];
				this.$scope.searchResultDef = searchResultDef;
				
				if(this.searchCriteria.fetchCount)
				{
					this.$scope.recordCount = result.totalCount;
				}
				
				if(result.searchResults.length > 0)
				{
					for(var i = 0; i < result.searchColumns.length; i++)
					{
						searchResultDef.fields.push({
							"displayable": result.searchColumns[i].displayable, 
							"label": result.searchColumns[i].heading, 
							"name": result.searchColumns[i].name
						});
					}
					
					var resultObj = null;
					
					for(var i = 0; i < result.searchResults.length; i++)
					{
						resultObj = {};
						
						for(var j = 0; j < result.searchColumns.length; j++)
						{
							resultObj[result.searchColumns[j].name] = result.searchResults[i].data[j];
						}
						
						this.$scope.searchResults.push(resultObj);
					}
				}
				
				this.$scope.searchExecuted = true;
				this.$scope.selectedIndex = -1;
				
				var popupDiv = $("#" + this.$scope.searchQueryId + " div.popupIcons");
				popupDiv.css("display", "none");
				
				//ensure digest runs on parent to refresh the results
					//ignore exceptions as exception is apply is already in progress
				try
				{
					this.$scope.$parent.$digest();
				}catch(ex)
				{}
				
				//ensure parent is informed that there is no selected row
				this.$scope.$emit('searchResultSelectionChanged', {
					"index": -1,
					"selectedRow": null,
					"searchQuery": this.$scope.searchQuery
				});
			}
		]);

		return false;
	};
	

	$scope.exportResults = function(e) {
		logger.trace("Eport results is triggered for query - " + $scope.searchQueryName);
		
		$scope.initErrors("searchQuery");
		
		try
		{
			$scope.customizeSearchQuery($scope.searchQuery);
		}catch(ex)
		{
			//ignore customization 
		}

		if(!validator.validateModel($scope.searchQuery, $scope.modelDef, $scope.errors.searchQuery))
		{
			utils.alert("Please correct the errors and then try!");
			return;
		}
		
		var queryJson = JSON.stringify($scope.searchQuery);
		var request = {
				"queryModelJson" : 	queryJson,
				"pageSize": -1,
				"name": $scope.searchQueryName
			};

		var queryUrl = actionHelper.actionUrl("search.export", request);
		
		var exportForm = $("#" + $scope.searchQueryId + " form[name='exportForm']");
		exportForm.find("input[name='queryModelJson']").val(queryJson);
		exportForm.find("input[name='AUTH_TOKEN']").val(clientContext.authToken);
		exportForm.attr("action", queryUrl);
		exportForm.attr("method", "GET");
		
		exportForm.submit();

		return false;
	};

	$scope.rowSelected = function(index, event) {
		//remove current selection if any
		if($scope.selectedIndex != null)
		{
			$("#" + $scope.searchQueryId + " table.searchResults tr[search-row-id='" + $scope.selectedIndex + "']").removeClass('info');
		}
		
		//set new selected row id
		var popupDiv = $("#" + $scope.searchQueryId + " div.popupIcons");
		var popupDivParent = popupDiv.parent();
		var selectedRow = $("#" + $scope.searchQueryId + " table.searchResults tr[search-row-id='" + index + "']");

		//set popup y position just above the selected row
		var popupTop = selectedRow.offset().top - popupDiv.height();
		
		//ensure the popup x position is not resulting parent overflow
		var maxX = popupDivParent.offset().left + popupDivParent.width() - popupDiv.width() - 5;
		
		popupDiv.css("display", "block");
		
		popupDiv.offset({
			top: popupTop, 
			left: (event.pageX >  maxX) ? maxX : event.pageX 
		});
		
		
		$scope.selectedIndex = index;
		
		//highlight selected row
		selectedRow.addClass('info');
		
		//send selection event to parent controller
		$scope.$emit('searchResultSelectionChanged', {
			"index": $scope.selectedIndex,
			"selectedRow": $scope.searchResults[$scope.selectedIndex],
			"searchQuery": $scope.searchQuery,
			"searchQueryName": $scope.searchQueryName
		});

		try
		{
			$scope.$parent.$digest();
		}catch(ex)
		{}
	};

	$scope.$on('rowsModified', function(event, data){
		if(!$scope.searchExecuted)
		{
			return;
		}
		
		$scope.performSearch({
			"pageNumber": $scope.currentPageNo,
			"fetchCount": true
		});
	});

	$scope.$on('invokeSearch', function(event, data){
		if(data.searchQuery)
		{
			$scope.searchQuery = data.searchQuery; 
		}

		$scope.performSearch({
			"pageNumber": $scope.currentPageNo,
			"fetchCount": false
		});
	});

	$scope.validateField = function(name, modelPrefix) {
		$scope.initErrors(modelPrefix);

		try
		{
			var model = $scope[modelPrefix];
			validator.validateField(model, $scope.modelDef, name);
			$scope.errors[modelPrefix][name] = "";
		}catch(ex)
		{
			if((typeof ex) != 'string')
			{
				logger.error(ex);
			}
			
			$scope.errors[modelPrefix][name] = ex;
		}
	};

	$scope.validateExtendedField = function(name, modelPrefix) {
		$scope.initErrors(modelPrefix);

		try
		{
			var model = $scope[modelPrefix];
			validator.validateExtendedField(model, $scope.modelDef, name);
			$scope.errors.searchQuery.extendedFields[name] = "";
		}catch(ex)
		{
			if((typeof ex) != 'string')
			{
				logger.error(ex);
			}

			$scope.errors[modelPrefix].extendedFields[name] = ex;
		}
	};
	
	
	
	//////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	
	$scope.changeRowSelection = function(index) {
		$scope.selectedIndex = index;
		
		$scope.$emit('searchResultSelectionChanged', {
		   "index": $scope.selectedIndex,
		   "selectedRow": $scope.searchResults[$scope.selectedIndex],
		   "searchQuery": $scope.searchQuery,
		   "searchQueryName": $scope.searchQueryName
		  });		
	};
	
	$scope.customizeSettings = function() {
		var id = $scope.searchQueryId;
		var queryName = $scope.searchQueryName;
		
		actionHelper.invokeAction("searchSettings.read", null, {"queryName": $scope.searchQueryName}, $.proxy(function(result, respConfig){
			var settings = result.model;
			
			if(!settings)
			{
				console.log("No settings found to customize.");
				return;
			}
			
			this.$scope.searchSettings = settings;

			try
			{
				$scope.$digest();
			}catch(ex)
			{}

			utils.openModal(id + "_search_settings", {
				context: {"$scope": this.$scope}
			});
			
		}, {"$scope": $scope}));
		
	};
	
	/**
	 * Called by column up action of result-customize-dialog.
	 */
	$scope.moveColumnUp = function(event) {
		var targetElement = event.target;
		var rowElem = $(targetElement).parents("[grid-row]");
		
		var idx = parseInt(rowElem.attr("grid-row"));
		
		//ignore call from top row
		if(idx == 0)
		{
			console.log("Ignoring top row...");
			return;
		}
		
		var columns = $scope.searchSettings.searchColumns;
		var tmp = columns[idx - 1];
		columns[idx - 1] = columns[idx];
		columns[idx] = tmp;
		
		try
		{
			$scope.$digest();
		}catch(ex)
		{}
	};
	
	$scope.moveColumnToTop = function(event) {
		var targetElement = event.target;
		var rowElem = $(targetElement).parents("[grid-row]");
		
		var idx = parseInt(rowElem.attr("grid-row"));
		
		if(idx == 0)
		{
			console.log("Ignoring top row...");
			return;
		}
		
		var columns = $scope.searchSettings.searchColumns;
		var tmp = columns[idx];
		
		//remove element at current index
		columns.splice(idx, 1);
		
		//add remove element at top
		columns.splice(0, 0, tmp);
		
		try
		{
			$scope.$digest();
		}catch(ex)
		{}
	};

	/**
	 * Called by column down action of result-customize-dialog.
	 */
	$scope.moveColumnDown = function(event) {
		var targetElement = event.target;
		var rowElem = $(targetElement).parents("[grid-row]");
		
		var idx = parseInt(rowElem.attr("grid-row"));

		var columns = $scope.searchSettings.searchColumns;

		//ignore call from top row
		if(idx == columns.length - 1)
		{
			console.log("Ignoring bottom row..");
			return;
		}
		
		var tmp = columns[idx + 1];
		columns[idx + 1] = columns[idx];
		columns[idx] = tmp;
		
		try
		{
			$scope.$digest();
		}catch(ex)
		{}
	};
	
	$scope.moveColumnToBottom = function(event) {
		var targetElement = event.target;
		var rowElem = $(targetElement).parents("[grid-row]");
		
		var idx = parseInt(rowElem.attr("grid-row"));
		
		var columns = $scope.searchSettings.searchColumns;

		//ignore call from top row
		if(idx == columns.length - 1)
		{
			console.log("Ignoring bottom row..");
			return;
		}
		
		var tmp = columns[idx];
		
		//remove element at current index
		columns.splice(idx, 1);
		
		//add removed element at bottom
		columns.splice(columns.length, 0, tmp);
		
		try
		{
			$scope.$digest();
		}catch(ex)
		{}
	};
	
	$scope.saveSettings = function() {
		console.log("Save is invoked..");
		
		if(!$scope.searchSettings)
		{
			return;
		}
		
		var id = $scope.searchQueryId;
		var queryName = $scope.searchQueryName;
		var modalId = id + "_search_settings";
		var saveOp = $scope.searchSettings.id ? false : true;

		$('#' + modalId).modal('hide');
		
		var saveCallback = $.proxy(function(response, respConfig) {

			if(response.code == 0)
			{
				this.logger.trace("Settings are updated successfully!!");
				this.utils.info(["Settings are updated successfully!!"]);
			}
			else
			{
				var op = this.saveOp ? "Save" : "Update";
				
				this.logger.error("Failed to {} changes.<BR/>ServerError: {}", op, response.message);
				this.utils.alert(["Failed to {} changes.<BR/>ServerError: {}", op, response.message], $.proxy(function(){
					$('#' + this.modalId).modal('show');
				}, this));
			}
			
		}, {"$scope": $scope, "logger": logger, "utils": utils, "modalId": id + "_search_settings", "saveOp": saveOp});
		
		$scope.searchSettings.searchQueryName = queryName;
		
		if(saveOp)
		{
			actionHelper.invokeAction("searchSettings.save", $scope.searchSettings, null, saveCallback);
		}
		else
		{
			actionHelper.invokeAction("searchSettings.update", $scope.searchSettings, null, saveCallback);
		}
		
	};
	
}]);