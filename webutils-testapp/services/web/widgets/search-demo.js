import * as Webutils from "/lib/webutils/webutils-app.js";

Webutils.newVueApp({
	data: function() {
		return {
			selectedRow: null
		};
	},
	methods: {
		onSearch: function(searchResponse) {
			this.selectedRow = null;
			if(this.$refs.results)
			{
				this.$refs.results.setSearchResults(searchResponse);
			}
		},
		onSelectRow: function(dataMap) {
			this.selectedRow = dataMap;
		}
	}
}).mount("#ykApp");
