import {$utils, $appConfiguration} from "/lib/webutils/common.js";

$appConfiguration.onSessionExpiry = function() {
	if(this["redirectionStarted"])
	{
		return;
	}

	this["redirectionStarted"] = true;
	$utils.alert("Your session is expired. Redirecting to login..", function() {
		window.location = "/login/login.html";
	});
};

$appConfiguration.onLogout = function() {
	window.location = "/login/login.html";
};

$appConfiguration.otp = {
	verificationTokenTimeSec: 600,
	minRetryDurationSec: 30,
	maxAttempts: 6,
	maxAttemptsDurationHours: 24
};

$appConfiguration.adminContactNumber = "N/A";
