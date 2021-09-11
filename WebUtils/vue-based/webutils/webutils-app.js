
$.newVueApp = function(vueData)
{
	var defData = {
		"components": {
			"yk-dialogs": ykDialogs,
			"yk-modal-dialog": ykModalDialog,
			"yk-form": ykForm
		},
		
		"mounted": function() {
			$("#webutilsPageLoading").css("display", "none");
			$("#ykApp").css("display", "block");
			
		},

		methods: 
		{
			"alert": function(message, callback)
			{
				this.$refs.ykDialogs.displayAlert(message, callback);
			}
		}
	};
	
	var mergeSubcomponent = function(source, dest, propName)
	{
		if(!dest[propName])
		{
			dest[propName] = source[propName];
		}
		else
		{
			for(var attr in source[propName])
			{
				dest[propName][attr] = source[propName][attr];
			}
		}
	};
	
	mergeSubcomponent(defData, vueData, 'components');
	mergeSubcomponent(defData, vueData, 'methods');
	vueData.mounted = defData.mounted;
	
	return new Vue(vueData);
}