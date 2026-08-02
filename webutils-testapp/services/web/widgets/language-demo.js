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
			lastResponse: null,
			sampleLoaded: false
		};
	},
	mounted: function() {
		this.loadSample();
	},
	methods: {
		loadSample: function() {
			var self = this;
			$restService.invokeGet("/api/testapp/language-demo/sample", null, {
				context: this,
				onSuccess: function(result) {
					var sample = result.response && result.response.value ? result.response.value : null;
					if(sample)
					{
						self.formData = sample;
						self.sampleLoaded = true;
					}
				},
				onError: function(err) {
					self.error = (err && err.response && err.response.message)
						? err.response.message
						: "Failed to load sample";
				}
			});
		},
		submitForm: function() {
			this.submitTried = true;
			this.error = "";
			this.fieldErrors = {};
			this.lastResponse = null;

			if(!this.$refs.langForm.validate())
			{
				this.error = "Please fix validation errors.";
				return;
			}

			var payload = this.$refs.langForm.getModel();
			$restService.invokePost("/api/testapp/language-demo/submit", payload, {
				context: this,
				onSuccess: function(result) {
					this.lastResponse = result.response;
					$utils.alert("Language demo submit succeeded.");
				},
				onError: function(err) {
					this.error = (err && err.response && err.response.message) ? err.response.message : "Submit failed";
					var errors = (err && err.errors) ? err.errors
						: (err && err.response && err.response.errors) ? err.response.errors : null;
					if(errors)
					{
						this.fieldErrors = errors;
					}
				}
			});
		}
	}
}).mount("#ykApp");
