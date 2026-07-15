import {$utils} from "./common.js";
import {$restService} from "./rest-service.js";

/**
 * Generic payment checkout widget (WebUtils).
 * Opens Razorpay when keyId + Razorpay script are available; otherwise posts a Razorpay-shaped
 * webhook to /api/payment/webhook/razorpay (dev path; signature skipped when app.devEnvironment=true).
 */
export var ykPaymentCheckout = {
	props: {
		order: { type: Object, required: true },
		name: { type: String, default: "" },
		email: { type: String, default: "" },
		contact: { type: String, default: "" },
		buttonLabel: { type: String, default: "Pay now" },
		buttonId: { type: String, default: "ykPaymentCheckout" },
		onSuccess: { type: Function, default: null },
		onFailure: { type: Function, default: null }
	},

	methods: {
		pay: function() {
			var order = this.order;
			if(!order || !order.gatewayOrderId) {
				$utils.alert("Payment order is not ready.");
				return;
			}

			if(!order.keyId || typeof Razorpay === "undefined") {
				var paymentId = "pay_dev_" + Date.now();
				$restService.invokePost("/api/payment/webhook/razorpay", {
					"event": "payment.captured",
					"payload": {
						"payment": {
							"entity": {
								"id": paymentId,
								"order_id": order.gatewayOrderId
							}
						}
					}
				}, {
					"context": this,
					"onSuccess": function(result) {
						if(this.onSuccess) {
							this.onSuccess(result.response || result);
						}
					},
					"onError": function(err) {
						if(this.onFailure) {
							this.onFailure(err);
						} else {
							$utils.alert("Payment failed.");
						}
					},
					"includeAuthToken": false
				});
				return;
			}

			var self = this;
			var options = {
				"key": order.keyId,
				"amount": order.amount,
				"currency": order.currency || "INR",
				"name": "Payment",
				"order_id": order.gatewayOrderId,
				"prefill": {
					"name": this.name,
					"email": this.email,
					"contact": this.contact
				},
				"handler": function(response) {
					if(self.onSuccess) {
						self.onSuccess(response);
					}
				},
				"modal": {
					"ondismiss": function() {
						if(self.onFailure) {
							self.onFailure({ dismissed: true });
						}
					}
				}
			};

			new Razorpay(options).open();
		}
	},

	template: `
		<button type="button" class="webutils-btn-primary" @click="pay" :id="buttonId">
			{{ buttonLabel }}
		</button>
	`
};
