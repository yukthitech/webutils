import { createApp } from '/lib/vue-3.4.31/vue.esm-browser.js';
import { $mergeObjProperty, $utils } from './common.js';
import { inputFieldComponents } from './input-fields.js';
import { formComponents } from './forms.js';
import { dialogComponents } from './modal-dialogs.js';
import { navBarComponents } from './nav-bar.js';

export function addDefaultComponents(app) {
	//register intput field components in new vue application
	for(var name in inputFieldComponents)
	{
		app.component(name, inputFieldComponents[name]);
	}

	for(var name in formComponents)
	{
		app.component(name, formComponents[name]);
	}

	for(var name in dialogComponents)
	{
		app.component(name, dialogComponents[name]);
	}

	for(var name in navBarComponents)
	{
		app.component(name, navBarComponents[name]);
	}
}

export function newVueApp(vueData)
{
	var defData = {
		"mounted": function() {
			$("#webutilsPageLoading").css("display", "none");
			$("#ykApp").css("display", "block");
			
			if(this.onMounted)
			{
				this.onMounted();
			}
			
			$utils.ykDialogs = this.$refs.ykDialogs;
		},

		methods: 
		{
		}
	};
	
	$mergeObjProperty(defData, vueData, 'components');
	$mergeObjProperty(defData, vueData, 'methods');
	
	if(vueData.mounted)
	{
		if(!vueData.methods)
		{
			vueData.methods = {};
		}
		
		vueData.methods.onMounted = vueData.mounted;
	}
	
	vueData.mounted = defData.mounted;
	
	let vueApplication = createApp(vueData);
	$.application = vueApplication;
	addDefaultComponents(vueApplication);

	return vueApplication;
}
