import * as Webutils from "/lib/webutils/webutils-app.js";
import {$restService} from "/lib/webutils/rest-service.js";
import {$utils} from "/lib/webutils/common.js";

Webutils.newVueApp({
	data: function() {
		return {
			formData: {},
			submitTried: false,
			fieldErrors: {},
			error: "",
			lastResponse: null
		};
	},
	methods: {
		submitForm: function() {
			this.submitTried = true;
			this.error = "";
			this.fieldErrors = {};

			if(!this.$refs.lovForm.validate())
			{
				this.error = "Please fix validation errors.";
				return;
			}

			var payload = this.$refs.lovForm.getModel();
			$restService.invokePost("/api/testapp/lov-demo/submit", payload, {
				context: this,
				onSuccess: function(result) {
					this.lastResponse = result.response;
					$utils.alert("LOV demo submit succeeded.");
				},
				onError: function(err) {
					this.error = (err && err.response && err.response.message) ? err.response.message : "Submit failed";
					if(err && err.response && err.response.fieldErrors)
					{
						this.fieldErrors = err.response.fieldErrors;
					}
				}
			});
		}
	}
}).mount("#ykApp");
