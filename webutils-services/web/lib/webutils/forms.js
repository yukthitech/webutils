import { h } from '/lib/vue-3.4.31/vue.esm-browser.js';
import {$restService} from "./rest-service.js";
import {$utils} from "./common.js";
import {$modelDefService} from "./model-def-service.js";

export var formComponents = {};

formComponents['yk-form'] = {
	"props": {
		
		/**
		 * http method to be used
		 */
		"method": String,
		
		/**
		 * url to be submitted to
		 */
		"url": String,
		
		/**
		 * form-data to be populated as initial values
		 * and where input data will be maintained
		 */
		"formData": {
			type: Object,
			"required": true
		},
		
		"validationActivated": {
			"default": false
		}
	},
	
	"methods":
	{
		"submitForm": function()
		{
			if(this.formData.errorFields.length > 0)
			{
				$(this.$el).find("[name=" + this.formData.errorFields[0] + "]").focus();
				this.formData.displayErrors = true;
				this.$emit('submit', false);
				return;
			}
			
			if(this.method == "POST")
			{
				$restService.invokePost(
						this.url, 
						this.formData.data,
						{
							"context": this, 
							"onSuccess": this.submitSuccess, 
							"onError": this.submitFailed
						}
					);
			}
		},
	
		"submitSuccess": function(result) {
			this.$emit('submit', true, result);			
		},
		
		"submitFailed": function(result) {
			if(!result.response)
			{
				result.response = {"message": "Failed to contact server."};
			}
			
			this.$emit('submit', false, result);
		}
	},
	
	template: `
		<div style="width: 100%;">
			<slot></slot>
		</div>
	`
};

formComponents['yk-search-form'] = {
	"props": {
		"queryName": { "type": String, "required": true },
		"columnCount": { "type": Number, "default": 2 },
		"simpleSearch": { "type": Boolean, "default": false },
		/**
		 * Listing default page size sent to execute-search.
		 * Server may override via persisted search settings; the response pageSize is adopted so client stays in sync.
		 */
		"pageSize": { "type": Number, "default": 100 }
	},
	
	"data": function() {
		return {
			"modelFieldRows": [
			],
			"columnClass": "col-md-6",
			"formData": {
				"data": {},
				"errorFields": [],
				"displayErrors": false
			},
			"searchPerformed": false,
			"currentPage": 1,
			/**
			 * Working page size — starts from prop, then tracks server response.pageSize.
			 */
			"activePageSize": 5
		}
	},

	"watch": {
		"pageSize": function(newVal) {
			if(typeof newVal === "number" && newVal >= 1)
			{
				this.activePageSize = newVal;
			}
		}
	},
	
	"created": function() {
		this.activePageSize = (this.pageSize >= 1) ? this.pageSize : 5;

		$restService.invokeGet(
				"/api/search/" + this.queryName + "/query/def", 
				null,
				{
					"context": this, 
					"onSuccess": this.setFormData 
				}
			);
		
		var colSize = 12 / this.columnCount;
		this.columnClass = "col-md-" + colSize;
	},
	
	"methods":
	{
		"setFormData": function(result) {
			var modelDef = result.response.value || result.response.modelDef;
			$modelDefService.divideModelRows(modelDef, this.modelFieldRows, this.columnCount);
		},
		
		"refreshSearch": function() {
			if(!this.searchPerformed)
			{
				return;
			}
			
			this.executeSearch(this.currentPage);
		},

		/**
		 * Runs a new search from page 1 (Search button).
		 */
		"search": function() {
			this.executeSearch(1);
		},

		/**
		 * Re-runs the last search criteria at the given page (pagination).
		 */
		"gotoPage": function(pageNumber) {
			if(!this.searchPerformed)
			{
				return;
			}

			this.executeSearch(pageNumber);
		},

		"executeSearch": function(pageNumber) {
			this.searchPerformed = true;
			this.formData.displayErrors = true;
			
			if(this.formData.errorFields.length > 0)
			{
				return;
			}

			var pageNo = (typeof pageNumber === "number" && pageNumber > 0) ? pageNumber : 1;
			this.currentPage = pageNo;

			var pageSize = (this.activePageSize >= 1) ? this.activePageSize
					: ((this.pageSize >= 1) ? this.pageSize : 5);
			
			var searchCriteria = JSON.stringify(this.formData.data);
			var url = this.simpleSearch ? "/api/search/search/" : "/api/search/execute/";
			var params = {
				"queryModelJson": searchCriteria,
				"pageNumber": pageNo,
				"pageSize": pageSize,
				"fetchCount": true
			};
			
			$restService.invokeGet(
					url + this.queryName, 
					params,
					{
						"context": this, 
						"onSuccess": this.searchResults 
					}
				);
		},
		
		"searchResults": function(result)
		{
			// Keep client page size in sync with whatever the server effectively used
			if(result.response && result.response.pageSize >= 1)
			{
				this.activePageSize = result.response.pageSize;
			}
			this.$emit("search", result.response);
		},
		
		"searchResultsError": function(result)
		{
			$utils.info("Search failed with error: " + result.response.message);
		}
	},
	
	template: `
		<div class="webutils-search-box">
			<div :key="row.index" class="row" v-for="row in modelFieldRows">
				<div :class="field.fullWidth ? 'col-md-12' : columnClass"
						:key="field.index"
						v-for="field in row.fields">
					<component
						:is="field.componentType"
						:field="field"
						v-model="formData.data[field.name]"
						:enable-error="formData.displayErrors"
						/>
				</div>
			</div>
			
			<div class="webutils-search-actions">
				<button :id="'yk-search-submit-' + queryName" type="button" class="btn btn-primary webutil-button" @click="search">Search</button>
			</div>
		</div>
	`
};

/**
 * Registers a custom cell renderer for a search-result column.
 * Place as a child of yk-search-results; default slot receives { value, row }.
 */
formComponents['yk-field-customizer'] = {
	"props": {
		"field": { "type": String, "required": true }
	},
	"inject": {
		"ykSearchResultsApi": { "default": null }
	},
	"mounted": function() {
		if(this.ykSearchResultsApi)
		{
			this.ykSearchResultsApi.registerFieldCustomizer(this.field, this);
		}
	},
	"unmounted": function() {
		if(this.ykSearchResultsApi)
		{
			this.ykSearchResultsApi.unregisterFieldCustomizer(this.field, this);
		}
	},
	"render": function() {
		return null;
	}
};

/**
 * Action button for search results toolbar and floating selection panel.
 *
 * Props:
 *   id, label, icon — identity / tooltip / Bootstrap Icons class (e.g. bi-pencil)
 *   color — CSS color for the icon (default inherits toolbar style)
 *   rowAction — when true, hidden until a row is selected; also shown in the floating panel.
 *               when false (default), always visible in the toolbar (global: add, export, …).
 */
formComponents['yk-search-action'] = {
	"props": {
		"id": { "type": String, "required": false },
		"label": { "type": String, "required": true },
		"icon": { "type": String, "required": false, "default": "" },
		"color": { "type": String, "required": false, "default": "" },
		"rowAction": { "type": Boolean, "required": false, "default": false }
	},
	"emits": ["action"],
	"inject": {
		"ykSearchResultsApi": { "default": null }
	},
	"mounted": function() {
		if(this.ykSearchResultsApi)
		{
			this.ykSearchResultsApi.registerAction(this);
		}
	},
	"unmounted": function() {
		if(this.ykSearchResultsApi)
		{
			this.ykSearchResultsApi.unregisterAction(this);
		}
	},
	"methods": {
		"trigger": function(row, event) {
			this.$emit("action", { "row": row, "event": event });
		}
	},
	"render": function() {
		return null;
	}
};

/**
 * Renders a search cell using a registered field customizer or EMAIL/PHONE defaults.
 */
formComponents['yk-search-cell'] = {
	"props": {
		"customizer": { "type": Object, "default": null },
		"value": {},
		"row": { "type": Object, "required": true },
		"searchResultType": { "type": String, "default": "NONE" }
	},
	"render": function() {
		if(this.customizer && this.customizer.$slots && this.customizer.$slots.default)
		{
			return this.customizer.$slots.default({
				"value": this.value,
				"row": this.row
			});
		}

		var display = (this.value === null || this.value === undefined) ? "" : String(this.value);
		var type = (this.searchResultType || "NONE").toUpperCase();

		if(display && type === "EMAIL")
		{
			return h("a", {
				"href": "mailto:" + display,
				"class": "webutils-search-cell-link",
				"onClick": function(e) { e.stopPropagation(); }
			}, display);
		}

		if(display && (type === "PHONE_NO" || type === "PHONE"))
		{
			return h("a", {
				"href": "tel:" + display.replace(/\s+/g, ""),
				"class": "webutils-search-cell-link",
				"onClick": function(e) { e.stopPropagation(); }
			}, display);
		}

		return h("span", display);
	}
};

formComponents['yk-search-results'] = {
	"props": {
		"title": { "type": String, "default": "" },
		/**
		 * Search query name — required to open/save per-user search settings.
		 */
		"queryName": { "type": String, "default": "" }
	},

	"emits": ["select", "double-click", "page-change", "settings-click", "settings-saved"],

	"provide": function() {
		var self = this;
		return {
			"ykSearchResultsApi": {
				"registerFieldCustomizer": function(field, cmp) { self.registerFieldCustomizer(field, cmp); },
				"unregisterFieldCustomizer": function(field, cmp) { self.unregisterFieldCustomizer(field, cmp); },
				"registerAction": function(cmp) { self.registerAction(cmp); },
				"unregisterAction": function(cmp) { self.unregisterAction(cmp); }
			}
		};
	},
	
	"data": function() {
		return {
			"headings": [],
			"rows": [],
			"rowCount": 0,
			"totalCount": 0,
			"pageNumber": 1,
			"pageSize": 0,
			"hasRows": false,
			"searchExecuted": false,
			"searchResult": null,
			"lastSelectedRow": -1,
			"selectedRowData": null,
			"fieldCustomizers": {},
			"actions": [],
			"floatingPanel": {
				"visible": false,
				"x": 0,
				"y": 0
			},
			"resizeState": null,
			"_ignoreNextDocClick": false,
			"_onDocMouseMove": null,
			"_onDocMouseUp": null,
			"_onDocClick": null,
			"_onDocKeyDown": null,

			"settingsLoading": false,
			"settingsSaving": false,
			"settingsId": null,
			"settingsVersion": null,
			"settingsPageSize": 5,
			"settingsColumns": []
		}
	},

	"computed": {
		"totalPages": function() {
			if(!this.pageSize || this.pageSize <= 0 || this.totalCount <= 0)
			{
				return this.hasRows ? 1 : 0;
			}
			return Math.max(1, Math.ceil(this.totalCount / this.pageSize));
		},
		"rangeLabel": function() {
			if(!this.hasRows)
			{
				return "";
			}

			var total = this.totalCount > 0 ? this.totalCount : this.rowCount;
			var size = this.pageSize > 0 ? this.pageSize : this.rowCount;
			var page = this.pageNumber > 0 ? this.pageNumber : 1;
			var start = (page - 1) * size + 1;
			var end = Math.min(page * size, total);
			if(end < start)
			{
				end = start + this.rowCount - 1;
			}
			return "(" + start + "-" + end + ") of " + total;
		},
		"pageOptions": function() {
			var pages = [];
			var total = this.totalPages;
			for(var i = 1; i <= total; i++)
			{
				pages.push(i);
			}
			return pages;
		},
		"canGoFirstOrPrev": function() {
			return this.pageNumber > 1;
		},
		"canGoNextOrLast": function() {
			return this.pageNumber < this.totalPages;
		},
		"hasRowSelected": function() {
			return this.lastSelectedRow >= 0 && this.selectedRowData != null;
		},
		"globalActions": function() {
			return this.actions.filter(function(a) { return !a.rowAction; });
		},
		"rowActions": function() {
			return this.actions.filter(function(a) { return !!a.rowAction; });
		},
		"toolbarActions": function() {
			var list = this.globalActions.slice();
			if(this.hasRowSelected)
			{
				list = list.concat(this.rowActions);
			}
			return list;
		},
		"showFloatingActions": function() {
			return this.floatingPanel.visible && this.rowActions.length > 0 && this.hasRowSelected;
		},
		"tableStyle": function() {
			var total = 0;
			var headings = this.headings || [];
			for(var i = 0; i < headings.length; i++)
			{
				total += headings[i].width > 0 ? headings[i].width : 0;
			}
			if(total <= 0)
			{
				return {};
			}
			return { "width": total + "px" };
		},
		"editableSettingsColumns": function() {
			return this.settingsColumns.filter(function(c) { return !c.backend; });
		}
	},

	"mounted": function() {
		var self = this;
		this._onDocMouseMove = function(e) { self.onColumnResizeMove(e); };
		this._onDocMouseUp = function(e) { self.onColumnResizeEnd(e); };
		this._onDocClick = function(e) { self.onDocumentClick(e); };
		this._onDocKeyDown = function(e) {
			if(e.key === "Escape")
			{
				self.hideFloatingPanel();
			}
		};
		document.addEventListener("mousemove", this._onDocMouseMove);
		document.addEventListener("mouseup", this._onDocMouseUp);
		document.addEventListener("click", this._onDocClick);
		document.addEventListener("keydown", this._onDocKeyDown);
	},

	"unmounted": function() {
		document.removeEventListener("mousemove", this._onDocMouseMove);
		document.removeEventListener("mouseup", this._onDocMouseUp);
		document.removeEventListener("click", this._onDocClick);
		document.removeEventListener("keydown", this._onDocKeyDown);
	},
	
	"methods":
	{
		"registerFieldCustomizer": function(field, cmp) {
			this.fieldCustomizers = Object.assign({}, this.fieldCustomizers, { [field]: cmp });
		},

		"unregisterFieldCustomizer": function(field, cmp) {
			if(this.fieldCustomizers[field] === cmp)
			{
				var next = Object.assign({}, this.fieldCustomizers);
				delete next[field];
				this.fieldCustomizers = next;
			}
		},

		"registerAction": function(cmp) {
			if(this.actions.indexOf(cmp) < 0)
			{
				this.actions = this.actions.concat([cmp]);
			}
		},

		"unregisterAction": function(cmp) {
			this.actions = this.actions.filter(function(a) { return a !== cmp; });
		},

		"setSearchResults": function(searchResult) {
			this.headings.splice(0, this.headings.length);
			this.rows.splice(0, this.rows.length);
			this.searchResult = searchResult;
			this.lastSelectedRow = -1;
			this.selectedRowData = null;
			this.hideFloatingPanel();
			
			var resultRows = searchResult.searchResults || [];
			this.rowCount = resultRows.length;
			this.hasRows = (this.rowCount > 0);
			this.searchExecuted = true;
			this.pageNumber = searchResult.pageNumber > 0 ? searchResult.pageNumber : 1;
			this.pageSize = searchResult.pageSize > 0 ? searchResult.pageSize : this.rowCount;
			this.totalCount = searchResult.totalCount > 0 ? searchResult.totalCount : this.rowCount;
			
			var colIdx = 0;
			
			for(var col of (searchResult.searchColumns || []))
			{
				if(!col.displayable)
				{
					continue;
				}

				this.headings.push({
					"index": "heading-" + colIdx,
					"value": col.heading,
					"name": col.name,
					"searchResultType": col.searchResultType || "NONE",
					"width": null
				});
				colIdx++;
			}
			
			var rowIdx = 0;

			for(var row of resultRows)
			{
				var searchRow = [];
				var colIdxAll = 0;
				var searchObj = {};
				var displayColIdx = 0;
				
				for(var cellVal of row.data)
				{
					var fullCol = searchResult.searchColumns[colIdxAll];
					searchObj[fullCol.name] = cellVal;
					
					if(!fullCol.displayable)
					{
						colIdxAll++;
						continue;
					}

					searchRow.push({
						"index": rowIdx + "-" + displayColIdx,
						"value": cellVal,
						"name": fullCol.name,
						"searchResultType": fullCol.searchResultType || "NONE"
					});
					displayColIdx++;
					colIdxAll++;
				}
				
				this.rows.push({
					"index": "row-" + rowIdx,
					"rowId": "" + rowIdx,
					"data": searchRow,
					"dataMap": searchObj
				});
				rowIdx++;
			}

			var self = this;
			this.$nextTick(function() {
				self.ensureColumnWidths();
			});
		},

		"ensureColumnWidths": function() {
			var n = this.headings.length;
			if(n <= 0)
			{
				return;
			}

			var content = this.$el ? this.$el.querySelector(".content") : null;
			var available = content && content.clientWidth > 0 ? content.clientWidth : 800;
			var defaultW = Math.max(60, Math.floor(available / n));

			for(var i = 0; i < n; i++)
			{
				if(!(this.headings[i].width > 0))
				{
					this.headings[i].width = defaultW;
				}
			}
		},
		
		"selectRow": function(row, event) {
			var idx = parseInt(row.rowId, 10);

			// Single selection only — replace any previous selection
			this.lastSelectedRow = idx;
			this.selectedRowData = row.dataMap;
			
			this.$emit("select", row.dataMap);

			if(this.rowActions.length > 0 && event)
			{
				this._ignoreNextDocClick = true;
				this.showFloatingPanel(event.clientX, event.clientY);
			}
			else
			{
				this.hideFloatingPanel();
			}
		},

		"onDoubleClick": function(row) {
			this.$emit("double-click", row.dataMap);
		},

		"showFloatingPanel": function(clientX, clientY) {
			var x = clientX + 8;
			var y = clientY + 8;
			var maxX = window.innerWidth - 180;
			var maxY = window.innerHeight - 80;
			if(x > maxX) { x = Math.max(8, maxX); }
			if(y > maxY) { y = Math.max(8, maxY); }

			this.floatingPanel = {
				"visible": true,
				"x": x,
				"y": y
			};
		},

		"hideFloatingPanel": function() {
			this.floatingPanel = {
				"visible": false,
				"x": 0,
				"y": 0
			};
		},

		"onDocumentClick": function(event) {
			if(this._ignoreNextDocClick)
			{
				this._ignoreNextDocClick = false;
				return;
			}

			if(!this.floatingPanel.visible)
			{
				return;
			}

			var panel = this.$refs.floatingActions;
			if(panel && panel.contains(event.target))
			{
				return;
			}

			this.hideFloatingPanel();
		},

		"actionButtonId": function(action, idx, suffix) {
			var base = action.id || ("yk-search-action-" + idx);
			return suffix ? (base + suffix) : base;
		},

		"actionIconStyle": function(action) {
			if(action.color)
			{
				return { "color": action.color };
			}
			return {};
		},

		"onActionClick": function(action, event) {
			event.stopPropagation();
			action.trigger(this.selectedRowData, event);
			this.hideFloatingPanel();
		},

		"onToolbarActionClick": function(action, event) {
			event.stopPropagation();
			var row = action.rowAction ? this.selectedRowData : null;
			action.trigger(row, event);
		},

		"onSettingsClick": function(event) {
			this.$emit("settings-click", event);
			this.openSettingsDialog();
		},

		"openSettingsDialog": function() {
			if(!this.queryName)
			{
				$utils.info("Search settings require query-name on yk-search-results.");
				return;
			}

			this.settingsLoading = true;
			$restService.invokeGet(
					"/api/search/settings/read/" + encodeURIComponent(this.queryName),
					null,
					{
						"context": this,
						"onSuccess": this.onSettingsLoaded,
						"onError": this.onSettingsLoadError
					}
				);
		},

		"onSettingsLoaded": function(result) {
			this.settingsLoading = false;
			var model = result.response.value || result.response.model;
			if(!model)
			{
				$utils.info("Failed to load search settings.");
				return;
			}

			this.settingsId = model.id || null;
			this.settingsVersion = model.version || null;
			this.settingsPageSize = model.pageSize >= 1 ? model.pageSize : 5;
			this.settingsColumns = JSON.parse(JSON.stringify(model.searchColumns || []));

			var self = this;
			this.$nextTick(function() {
				var el = document.getElementById("yk-search-settings-dialog");
				if(el && typeof bootstrap !== "undefined")
				{
					bootstrap.Modal.getOrCreateInstance(el).show();
				}
			});
		},

		"onSettingsLoadError": function(result) {
			this.settingsLoading = false;
			var msg = (result && result.response && result.response.message)
					? result.response.message : "Failed to load search settings.";
			$utils.info(msg);
		},

		"settingsColumnKey": function(col) {
			if(col.fields && col.fields.length && col.fields[0].propertyName)
			{
				return col.fields[0].propertyName;
			}
			if(col.fields && col.fields.length && col.fields[0].field)
			{
				return col.fields[0].field;
			}
			return (col.label || "col").replace(/\s+/g, "-").toLowerCase();
		},

		"editableColumnIndex": function(col) {
			var editable = this.editableSettingsColumns;
			for(var i = 0; i < editable.length; i++)
			{
				if(editable[i] === col)
				{
					return i;
				}
			}
			return -1;
		},

		"moveSettingsColumn": function(col, direction) {
			var editable = this.editableSettingsColumns;
			var eidx = this.editableColumnIndex(col);
			var swapWith = editable[eidx + direction];
			if(!swapWith)
			{
				return;
			}

			var all = this.settingsColumns;
			var a = all.indexOf(col);
			var b = all.indexOf(swapWith);
			if(a < 0 || b < 0)
			{
				return;
			}

			var copy = all.slice();
			copy[a] = swapWith;
			copy[b] = col;
			this.settingsColumns = copy;
		},

		"moveSettingsColumnUp": function(col) {
			this.moveSettingsColumn(col, -1);
		},

		"moveSettingsColumnDown": function(col) {
			this.moveSettingsColumn(col, 1);
		},

		"closeSettingsDialog": function() {
			var el = document.getElementById("yk-search-settings-dialog");
			if(el && typeof bootstrap !== "undefined")
			{
				var instance = bootstrap.Modal.getInstance(el);
				if(instance)
				{
					instance.hide();
				}
			}
		},

		"saveSettings": function() {
			var pageSize = parseInt(this.settingsPageSize, 10);
			if(isNaN(pageSize) || pageSize < 1 || pageSize > 1000)
			{
				$utils.info("Page size must be between 1 and 1000.");
				return;
			}

			var columns = JSON.parse(JSON.stringify(this.settingsColumns));
			for(var i = 0; i < columns.length; i++)
			{
				columns[i].order = i;
				if(columns[i].required && !columns[i].backend)
				{
					columns[i].displayed = true;
				}
			}

			var payload = {
				"searchQueryName": this.queryName,
				"pageSize": pageSize,
				"searchColumns": columns
			};
			if(this.settingsId)
			{
				payload.id = this.settingsId;
			}
			if(this.settingsVersion != null)
			{
				payload.version = this.settingsVersion;
			}
			else
			{
				payload.version = 1;
			}

			this.settingsSaving = true;
			$restService.invokePost(
					"/api/search/settings/saveOrUpdate",
					payload,
					{
						"context": this,
						"onSuccess": this.onSettingsSaved,
						"onError": this.onSettingsSaveError
					}
				);
		},

		"onSettingsSaved": function(result) {
			this.settingsSaving = false;
			if(result.response && result.response.id)
			{
				this.settingsId = result.response.id;
			}
			this.pageSize = parseInt(this.settingsPageSize, 10);
			this.closeSettingsDialog();
			this.$emit("settings-saved", {
				"pageSize": this.pageSize,
				"queryName": this.queryName
			});
		},

		"onSettingsSaveError": function(result) {
			this.settingsSaving = false;
			var msg = (result && result.response && result.response.message)
					? result.response.message : "Failed to save search settings.";
			$utils.info(msg);
		},

		"changePage": function(pageNumber) {
			var page = parseInt(pageNumber, 10);
			if(isNaN(page) || page < 1 || page > this.totalPages || page === this.pageNumber)
			{
				return;
			}
			this.$emit("page-change", page);
		},

		"goFirstPage": function() {
			this.changePage(1);
		},

		"goPrevPage": function() {
			this.changePage(this.pageNumber - 1);
		},

		"goNextPage": function() {
			this.changePage(this.pageNumber + 1);
		},

		"goLastPage": function() {
			this.changePage(this.totalPages);
		},

		"onPageSelect": function(event) {
			this.changePage(event.target.value);
		},

		"columnStyle": function(heading) {
			if(heading && heading.width > 0)
			{
				return {
					"width": heading.width + "px",
					"min-width": heading.width + "px",
					"max-width": heading.width + "px"
				};
			}
			return {};
		},

		"cellColumnStyle": function(cellName) {
			for(var i = 0; i < this.headings.length; i++)
			{
				if(this.headings[i].name === cellName)
				{
					return this.columnStyle(this.headings[i]);
				}
			}
			return {};
		},

		"onColumnResizeStart": function(heading, event) {
			event.preventDefault();
			event.stopPropagation();
			this.resizeState = {
				"heading": heading,
				"startX": event.clientX,
				"startWidth": heading.width || event.target.parentElement.offsetWidth
			};
		},

		"onColumnResizeMove": function(event) {
			if(!this.resizeState)
			{
				return;
			}
			var delta = event.clientX - this.resizeState.startX;
			var width = Math.max(60, this.resizeState.startWidth + delta);
			this.resizeState.heading.width = width;
		},

		"onColumnResizeEnd": function() {
			this.resizeState = null;
		}
	},
	
	template: `
		<div class="webutils-search-results-container">
			<div style="display:none"><slot></slot></div>

			<div class="webutils-search-results-header" v-if="searchExecuted">
				<div class="webutils-search-results-title">{{ title }}</div>
				<button type="button"
						class="btn btn-sm btn-light webutils-search-settings-btn"
						id="yk-search-results-settings"
						title="Settings"
						@click="onSettingsClick">
					<span class="bi bi-gear"></span>
				</button>
			</div>

			<div class="webutils-search-results-actions" v-if="searchExecuted &amp;&amp; toolbarActions.length">
				<button type="button"
						class="webutils-search-action-btn"
						:id="actionButtonId(action, idx, '')"
						:key="'action-' + (action.id || idx)"
						:title="action.label"
						v-for="(action, idx) in toolbarActions"
						@click="onToolbarActionClick(action, $event)">
					<span :class="'bi ' + (action.icon || 'bi-circle')"
							:style="actionIconStyle(action)"></span>
				</button>
			</div>

			<div class="content" v-if="searchExecuted &amp;&amp; hasRows">
				<table class="webutils-search-results" :style="tableStyle">
					<thead>
						<tr>
							<th :key="heading.index"
									v-for="heading in headings"
									:style="columnStyle(heading)">
								<span class="webutils-search-th-label">{{ heading.value }}</span>
								<span class="webutils-search-col-resizer"
										@mousedown="onColumnResizeStart(heading, $event)"></span>
							</th>
						</tr>
					</thead>
					<tbody>
						<tr :key="row.index"
								:rowid="row.rowId"
								:class="{ selected: lastSelectedRow === parseInt(row.rowId, 10) }"
								v-for="row in rows"
								@click="selectRow(row, $event)"
								@dblclick="onDoubleClick(row)">
							<td :key="cell.index"
									v-for="cell in row.data"
									:style="cellColumnStyle(cell.name)">
								<yk-search-cell
									:customizer="fieldCustomizers[cell.name]"
									:value="cell.value"
									:row="row.dataMap"
									:search-result-type="cell.searchResultType"
									/>
							</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div class="footer webutils-search-results-footer" v-if="hasRows">
				<span class="webutils-search-range">{{ rangeLabel }}</span>
				<span class="webutils-search-page-nav">
					<button type="button" class="btn btn-sm btn-light"
							id="yk-search-page-first"
							title="First page"
							:disabled="!canGoFirstOrPrev"
							@click="goFirstPage">
						<span class="bi bi-chevron-double-left"></span>
					</button>
					<button type="button" class="btn btn-sm btn-light"
							id="yk-search-page-prev"
							title="Previous page"
							:disabled="!canGoFirstOrPrev"
							@click="goPrevPage">
						<span class="bi bi-chevron-left"></span>
					</button>
					<select class="form-select form-select-sm webutils-search-page-select"
							id="yk-search-page-select"
							:value="pageNumber"
							@change="onPageSelect">
						<option v-for="p in pageOptions" :key="'page-' + p" :value="p">{{ p }}</option>
					</select>
					<button type="button" class="btn btn-sm btn-light"
							id="yk-search-page-next"
							title="Next page"
							:disabled="!canGoNextOrLast"
							@click="goNextPage">
						<span class="bi bi-chevron-right"></span>
					</button>
					<button type="button" class="btn btn-sm btn-light"
							id="yk-search-page-last"
							title="Last page"
							:disabled="!canGoNextOrLast"
							@click="goLastPage">
						<span class="bi bi-chevron-double-right"></span>
					</button>
				</span>
			</div>
			<div class="footer" v-if="!searchExecuted">
				No search is executed yet.
			</div>
			<div class="footer" v-if="searchExecuted &amp;&amp; !hasRows">
				No records found with given criteria.
			</div>

			<div class="webutils-search-floating-actions"
					ref="floatingActions"
					v-if="showFloatingActions"
					:style="{ left: floatingPanel.x + 'px', top: floatingPanel.y + 'px' }"
					@click.stop>
				<button type="button"
						class="webutils-search-action-btn"
						:id="actionButtonId(action, idx, '-float')"
						:key="'float-action-' + (action.id || idx)"
						:title="action.label"
						v-for="(action, idx) in rowActions"
						@click="onActionClick(action, $event)">
					<span :class="'bi ' + (action.icon || 'bi-circle')"
							:style="actionIconStyle(action)"></span>
				</button>
			</div>

			<div class="modal fade" id="yk-search-settings-dialog" tabindex="-1" aria-labelledby="yk-search-settings-title">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-header webutils-modal-header">
							<span id="yk-search-settings-title">Search Settings</span>
							<button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
						</div>
						<div class="modal-body">
							<div class="mb-3">
								<label class="form-label" for="yk-search-settings-page-size">Page size</label>
								<input type="number"
										id="yk-search-settings-page-size"
										class="form-control form-control-sm"
										min="1"
										max="1000"
										v-model.number="settingsPageSize"/>
								<div class="form-text">Between 1 and 1000. Applied to this search query for your user.</div>
							</div>

							<div class="webutils-search-settings-columns">
								<div class="webutils-search-settings-col-header">Columns</div>
								<div class="webutils-search-settings-col-row"
										:key="'settings-col-' + settingsColumnKey(col)"
										v-for="(col, idx) in editableSettingsColumns">
									<label class="webutils-search-settings-col-label">
										<input type="checkbox"
												:id="'yk-search-settings-col-' + settingsColumnKey(col) + '-display'"
												v-model="col.displayed"
												:disabled="col.required"/>
										<span>{{ col.label }}</span>
										<span class="text-muted" v-if="col.required"> (required)</span>
									</label>
									<span class="webutils-search-settings-col-order">
										<button type="button"
												class="btn btn-sm btn-light"
												:id="'yk-search-settings-col-' + settingsColumnKey(col) + '-up'"
												title="Move up"
												:disabled="editableColumnIndex(col) &lt;= 0"
												@click="moveSettingsColumnUp(col)">
											<span class="bi bi-arrow-up"></span>
										</button>
										<button type="button"
												class="btn btn-sm btn-light"
												:id="'yk-search-settings-col-' + settingsColumnKey(col) + '-down'"
												title="Move down"
												:disabled="editableColumnIndex(col) &gt;= editableSettingsColumns.length - 1"
												@click="moveSettingsColumnDown(col)">
											<span class="bi bi-arrow-down"></span>
										</button>
									</span>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
							<button type="button"
									id="yk-search-settings-save"
									class="btn btn-primary btn-sm"
									:disabled="settingsSaving || settingsLoading"
									@click="saveSettings">
								{{ settingsSaving ? 'Saving…' : 'Save' }}
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	`
};

formComponents['yk-model-form'] = {
	"props": {
		"modelName": { "type": String, "required": true },
		"columnCount": { "type": Number, "default": 1 },
		
		/**
		 * Custom style that can be applied to top level div layer.
		 */
		"style": { "type": String, "required": false, "default": ""},
		
		/**
		 * Flag indicating if the authentication context should be used.
		 * If set to true, auth will not be used while fetching common info
		 * like LOV, model-def, etc.
		 */
		"noAuth": { "type": Boolean, "default": false },
		
		/**
		 * This should be an array of group objects. Each object should have
		 * 		label, array of field names part of this group, default flag indicating if this is default group
		 * 
		 * Fields which are not falling under any group will be added to default group. The last
		 * group which is marked as default will be taken as default group.
		 * 
		 * If no default group is present, for extra fields a default group will be added at end.
		 * 
		 * Default: All fields will be considered as one group.
		 */
		"groups": { "type": Array},

		/**
		 * Used to set initial value for the field.
		 * This also helps in 2-way binding with parent fields using v-model
		 */
		"modelValue": {},

		/**
		 * Enable error display for the form. Generally set to true when form is submitted.
		 */
		"enableError": {"type": Boolean, default: false},

		/**
		 * Server error message for the form.
		 */
		"serverErrors": {"type": Object, default: null},
	},
	
	"data": function() {
		return {
			"modelFieldGroups": [
			],
			"columnClass": "col-md-6",
			
			"fieldChangeListeners": {},
			
			"formStatusListener": null,
			
			/**
			 * Maintains the form data from all fields.
			 */
			"formData": {},
			
			/**
			 * Flag indicating if mount is completed. Only after mount during data
			 * model change event is fired.
			 */
			"mountCompleted": false,
			
			/**
			 * Flag to prevent recursive updates when modelValue changes externally
			 */
			"updatingFromExternal": false
		}
	},

	"watch": {
		"modelValue": function(newVal, oldVal) {
			//console.log("Form model value changed to: ", newVal, oldVal);
			if(newVal) {
				this.updatingFromExternal = true;
				this.formData = $utils.deepClone(newVal);
				this.$nextTick(() => {
					this.updatingFromExternal = false;
				});
			}
		},

		"serverErrors": function(newVal) {
			// Focus on first error field
			if(!newVal) {
				return;
			}

			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						if(newVal[fld.name]) {
							$(this.$el).find("[name=" + fld.name + "]").focus();
							break;
						}
					}
				}	
			}
		}
	},
	
	"created": function() {
		
		if(this.modelValue) {
			this.formData = this.modelValue;
		}
		
		$restService.fetchModelDef(this.modelName, $.proxy(this.setModelDef, this), this.noAuth);
		
		var colSize = 12 / this.columnCount;
		this.columnClass = "col-md-" + colSize;
	},
	
	"mounted": function() 
	{
		this.mountCompleted = true;
	},
	
	"updated": function() {
		// Rebuild parent watchers each update (avoid accumulating duplicate listeners)
		this.fieldChangeListeners = {};

		for(let group of this.modelFieldGroups)
		{
			for(let row of group.rows)
			{
				for(let fld of row.fields)
				{
					if(!fld.lovDetails || !fld.lovDetails.parentField)
					{
						continue;
					}
					
					let refFld = this.$refs["field_" + fld.index][0];
					
					let parentDetails = refFld.getParentDetails();
					
					if(!parentDetails)
					{
						continue;
					}
		
					if(!this.fieldChangeListeners[parentDetails.name])
					{
						this.fieldChangeListeners[parentDetails.name] = [];
					}
					
					this.fieldChangeListeners[parentDetails.name].push(parentDetails.callback);
				}
			}
		}
	},
	
	"methods":
	{
		"setModelDef": function(modelDef) {
			modelDef = $utils.deepClone(modelDef);
			// Divide fields into groups and rows
			$modelDefService.divideModelGroups(modelDef, this.modelFieldGroups, this.columnCount, this.groups);
		},

		"onFieldValueChange": function(newVal, fieldInfo)
		{
			if(this.fieldChangeListeners[fieldInfo.name])
			{
				var listeners = this.fieldChangeListeners[fieldInfo.name];
	
				for(var i = 0; i < listeners.length; i++)
				{
					listeners[i](newVal);
				}
			}
			
			if(!this.mountCompleted)
			{
				return;
			}
			
			let errFld = this.getFirstErrorField();
			let formHasErrors = (errFld != null);
			
			if(this.formStatusListener) {
				this.formStatusListener(formHasErrors);
			}
		},
		
		"raiseModelChangeEvent": function() {
			// Don't emit if we're currently updating from external changes
			if(this.updatingFromExternal) {
				return;
			}
			
			this.$nextTick($.proxy(function(){
				this.$emit('update:modelValue', this.formData);
			}, this));
			
		},
		
		"setFormStatusListener": function(listener) {
			this.formStatusListener = listener;
		},
		
		/**
		 * Checks if any field in the form is having error. If it does,
		 * this method returns the first field with error.
		 */
		"getFirstErrorField": function() {
			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						// return if the form is not displayed yet
						if(!this.$refs["field_" + fld.index]) {
							return null;	
						}
						
						let fldRef = this.$refs["field_" + fld.index][0];
						let err = fldRef.getError();
						
						if(err) {
							return fld.name;
						}
					}
				}
			}
			
			return null;
		},

		/**
		 * Runs validate() on every field in the form. Used by custom layouts and multi-row nested forms.
		 * @returns {boolean} true if all fields are valid
		 */
		"validateAllFields": function() {
			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						if(!this.$refs["field_" + fld.index]) {
							continue;
						}
						let fldRef = this.$refs["field_" + fld.index][0];
						if(fldRef.validate && !fldRef.validate()) {
							return false;
						}
					}
				}
			}
			return true;
		},

		/**
		 * Validates all fields. Preferred entry point for page submit handlers
		 * (e.g. this.$refs.form.validate()).
		 * @returns {boolean} true if all fields are valid
		 */
		"validate": function() {
			return this.validateAllFields();
		},

		/**
		 * Returns the current form model (field name → value).
		 * Syncs values from field widgets first so CodeMirror-backed editors
		 * contribute their latest content even if a tick was missed.
		 * @returns {object}
		 */
		"getModel": function() {
			for(let group of this.modelFieldGroups)
			{
				for(let row of group.rows)
				{
					for(let fld of row.fields)
					{
						var refList = this.$refs["field_" + fld.index];
						if(!refList || !refList[0])
						{
							continue;
						}

						var fldRef = refList[0];
						if(fldRef.getFieldValue)
						{
							this.formData[fld.name] = fldRef.getFieldValue();
						}
					}
				}
			}

			return this.formData;
		},
		
		/**
		 * Evaluates the form for errors. In case errors are found
		 * error display gets enabled and an exception is thrown.
		 */
		"evaluateForm": function()
		{
			let errFld = this.getFirstErrorField();
			
			if(errFld) {
				this.displayErrors = true;
				$(this.$el).find("[name=" + errFld + "]").focus();
				throw "Error field: " + errFld;
			}
			
			return this.formData;
		},
	},
	
	template: `
		<div :style="style">
			<div>
				<slot name="header"></slot>
			</div>
			
			<div class="webutils-group" v-for="group in modelFieldGroups">
				<div v-if="group.label" class="webutls-group-label">{{group.label}}</div>
					
				<div :key="row.index" class="row" v-for="row in group.rows">
					<div :class="field.fullWidth ? 'col-md-12' : columnClass" v-for="field in row.fields">
						<component
							:ref="'field_' + field.index"
							
							:field="field"
							v-model="formData[field.name]"
							:enableError="enableError"
							:server-error="serverErrors ? serverErrors[field.name] : null"
							
							:key="field.index"
							:is="field.componentType"
							:no-auth="noAuth"
							:empty-option="'Select ' + field.label"
							@value-changed="onFieldValueChange"
							@update:modelValue="raiseModelChangeEvent"
							/>
					</div>
				</div>
			</div>
			
			<div>
				<slot name="footer"></slot>
			</div>
		</div>
	`
};

formComponents['yk-multi-row-model-form'] = {
	"props": {
		/**
		 * Name of the model for the rows.
		 */
		"modelName": { "type": String, "required": true },
		
		/**
		 * Object to be used while adding default row.
		 */
		"defaultValue": {"type": Object, "required": false},
		
		/**
		 * Property to be executed on row to fetch label that
		 * in turn will be used during confirm box display during
		 * deletion.
		 */
		"labelProp": { "type": String, "required": true },
		
		/**
		 * Can be used to set initial rows for the field.
		 * This also helps in 2-way binding with parent fields using v-model
		 */
		"modelValue": {"type": Array, "required": false},

		/**
		 * Enable error display for the form. Generally set to true when form is submitted.
		 */
		"enableError": {"type": Boolean, default: false},

		/**
		 * Server error messages for the rows.
		 */
		"serverErrors": {"type": Array, default: null},
	},
	
	"data": function() {
		return {
			"rows": [],
			"defaultRow": null,
			"idCounter": 1,
		}
	},

	"watch": {
		"modelValue": function(newVal, oldVal) {
			// Clear existing rows
			this.rows.splice(0, this.rows.length);
			
			// Reset ID counter to avoid conflicts
			this.idCounter = 1;
			
			// Add new rows from modelValue
			if(newVal && Array.isArray(newVal)) {
				for(let row of newVal) {
					let newRow = $utils.deepClone(row);
					newRow._id = this.idCounter;
					this.idCounter++;
					
					this.rows.push(newRow);
				}
			}
		}
	},
	
	"created": function() {
		this.defaultRow = this.defaultValue;
		
		if(!this.defaultRow)
		{
			this.defaultRow = {};
		}
		
		if(this.modelValue)
		{
			for(let row of this.modelValue)
			{
				let newRow = $utils.deepClone(row);
				newRow._id = this.idCounter;
				this.idCounter++;
				
				this.rows.push(newRow);
			}
		}
	},
	
	"methods":
	{
		"getRowServerError": function(rowIndex) {
			if(!this.serverErrors || !Array.isArray(this.serverErrors))
			{
				return null;
			}

			// Backward compatibility: direct row-index array with error object per position.
			let directErr = this.serverErrors[rowIndex];
			if(directErr && (directErr.index == null || typeof(directErr.error) == "undefined"))
			{
				return directErr;
			}

			for(let errRow of this.serverErrors)
			{
				if(!errRow || errRow.index != rowIndex)
				{
					continue;
				}

				return errRow.error ? errRow.error : null;
			}

			return null;
		},

		"onValueChange": function() {
			let clonedRows = $utils.deepClone(this.rows);
			
			for(let row of clonedRows)
			{
				delete row._id;
			}
			
			this.$emit("update:modelValue", clonedRows);
		},
		
		"onModelChange": function(rowIndex, newVal) {
			if(rowIndex < 0 || rowIndex >= this.rows.length || !newVal)
			{
				return;
			}
			
			let existingRow = this.rows[rowIndex];
			let updatedRow = $utils.deepClone(newVal);
			updatedRow._id = existingRow._id;
			
			// Keep same row reference to avoid resetting nested input state on first selection.
			for(let prop in existingRow)
			{
				if(prop == "_id")
				{
					continue;
				}
				
				if(!(prop in updatedRow))
				{
					delete existingRow[prop];
				}
			}
			
			for(let prop in updatedRow)
			{
				existingRow[prop] = updatedRow[prop];
			}
			
			this.$nextTick($.proxy(this.onValueChange, this));
		},
		
		"addNewRow": function() {
			let newRow = $utils.deepClone(this.defaultRow);
			newRow._id = this.idCounter;
			this.idCounter++;
			
			this.rows.push(newRow);
			this.onValueChange();
		},
		
		"removeRow": function(row) {
			let label = '<none>';
			
			try
			{
				label = eval("row." + this.labelProp);
			}catch(ex)
			{
				// ignore
			}
			
			$utils.confirm("Are you sure you want to remove entry: " + label, $.proxy(function(accepted) {
				
				if(!accepted)
				{
					return;
				}
				
				let idx = this.rows.indexOf(row);
				
				if(idx >= 0) {
					this.rows.splice(idx, 1);
					this.onValueChange();
				}
			}, this));
		},

		/**
		 * Validates each nested row form (yk-model-form per row).
		 * @returns {boolean} true if all rows are valid or there are no rows
		 */
		"validateAllRows": function() {
			if(!this.rows.length) {
				return true;
			}
			let refs = this.$refs.nestedRowForm;
			let forms = Array.isArray(refs) ? refs : (refs ? [refs] : []);
			for(let i = 0; i < forms.length; i++) {
				if(forms[i].validateAllFields && !forms[i].validateAllFields()) {
					return false;
				}
			}
			return true;
		}
	},
	
	template: `
		<div class="webutils-multi-row-model-form">
			<div style="border-radius: 5px; border: 1px ridge rgb(200, 200, 200); padding: 15px 20px 50px 20px;">
				<div v-if="rows.length === 0" class="webutils-multi-row-empty-placeholder" style="display: flex; align-items: center; justify-content: center; min-height: 80px; color: #9a9a9a; font-size: 14px; text-align: center; padding: 8px 16px 16px;">
					No entries yet. Use Add Entry to add one.
				</div>
				<!--
					:key is important for vue.js to ensure right row is removed during deletion. 
				 -->
				<div v-for="(row, rowIndex) in rows" :key="row._id" class="webutils-multi-row-model-form-row">
				
					<yk-model-form ref="nestedRowForm" :model-name="modelName" :column-count="4" :model-value="row" :enable-error="enableError" 
						:server-errors="getRowServerError(rowIndex)"
						:style="'margin: 5px 0px; border-radius: 10px; position: relative; border: 1px solid #c0c0c0; box-shadow: 0 2px 6px rgba(0,0,0,0.10);'"
						@update:modelValue="onModelChange(rowIndex, $event)">
			    		<template #header>
							<div style="position: absolute; top: 5px; right: 10px; z-index: 10;">
								<button type="button" class="webutils-remove-button" title="Delete this entry" @click="removeRow(row)" aria-label="Delete">
									<span class="remove-icon">×</span>
								</button>
					        </div>
			    		</template>
					</yk-model-form>
					
				</div>
				
				<button class="webutils-btn-primary" style="float: right; margin-top: 3px;" @click="addNewRow()">Add Entry</button>
			</div>
		</div>
	`
};
