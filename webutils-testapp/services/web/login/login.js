import * as Webutils from "/lib/webutils/webutils-app.js";
import {$restService} from "/lib/webutils/rest-service.js";

Webutils.newVueApp({
	data: function() {
		return {
			username: "test@test.com",
			password: "test",
			userSpace: "test",
			error: "",
			busy: false
		};
	},
	methods: {
		login: function() {
			this.error = "";
			this.busy = true;
			var self = this;

			$restService.invokePost("/api/testapp/auth/login", {
				username: this.username,
				password: this.password,
				userSpace: this.userSpace
			}, {
				context: this,
				includeAuthToken: false,
				onSuccess: function(result) {
					var token = result.response && result.response.authToken;
					if(!token)
					{
						self.error = "Login response missing authToken.";
						self.busy = false;
						return;
					}
					sessionStorage.setItem("authToken", token);
					sessionStorage.setItem("loginUrl", "/login/login.html");
					window.location.href = "/index.html";
				},
				onError: function(err) {
					self.busy = false;
					self.error = (err && err.response && err.response.message)
						? err.response.message
						: "Login failed";
				}
			});
		}
	}
}).mount("#ykApp");
