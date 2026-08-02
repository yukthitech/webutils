import * as Webutils from "/lib/webutils/webutils-app.js";

Webutils.newVueApp({
	data: function() {
		return {
			selectedRow: null,
			lastActionMsg: ""
		};
	},
	methods: {
		onSearch: function(searchResponse) {
			this.selectedRow = null;
			this.lastActionMsg = "";
			if(this.$refs.results)
			{
				this.$refs.results.setSearchResults(searchResponse);
			}
		},
		onPageChange: function(pageNumber) {
			if(this.$refs.searchForm)
			{
				this.$refs.searchForm.gotoPage(pageNumber);
			}
		},
		onSelectRow: function(dataMap) {
			this.selectedRow = dataMap;
		},
		onSettingsClick: function() {
			this.lastActionMsg = "Settings dialog opened.";
		},
		onSettingsSaved: function(payload) {
			this.selectedRow = null;
			this.lastActionMsg = "Settings saved (page size " + (payload && payload.pageSize) + "). Refreshing search…";
			if(this.$refs.searchForm)
			{
				this.$refs.searchForm.refreshSearch();
			}
		},
		onAddAction: function() {
			this.lastActionMsg = "Add action (global).";
		},
		onExportAction: function() {
			this.lastActionMsg = "Export action (global).";
		},
		onViewAction: function(payload) {
			this.lastActionMsg = "View action: " + JSON.stringify(payload.row);
		},
		onEditAction: function(payload) {
			this.lastActionMsg = "Edit action: " + JSON.stringify(payload.row);
		},
		onDeleteAction: function(payload) {
			this.lastActionMsg = "Delete action: " + JSON.stringify(payload.row);
		}
	}
}).mount("#ykApp");
