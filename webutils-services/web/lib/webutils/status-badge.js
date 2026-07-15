/**
 * Colored status chip (WebUtils). Maps known statuses to Bootstrap badge colors.
 *
 * Usage: <yk-status-badge :status="row.status" :id-prefix="'emp-app-status-'" :id-value="row.id"/>
 */
export var ykStatusBadge = {
	props: {
		status: { type: [String, Object], default: "" },
		/** Optional id attribute prefix; combined with idValue when both set. */
		idPrefix: { type: String, default: "" },
		idValue: { type: [String, Number], default: null },
		/** Explicit element id overrides idPrefix+idValue. */
		elementId: { type: String, default: "" }
	},

	computed: {
		label: function() {
			if(this.status == null) {
				return "";
			}
			return String(this.status);
		},
		badgeClass: function() {
			var key = this.label.toUpperCase();
			var map = {
				PENDING: "bg-warning text-dark",
				ACCEPTED: "bg-success",
				REJECTED: "bg-danger",
				ACTIVE: "bg-success",
				DRAFT: "bg-secondary",
				CLOSED: "bg-dark",
				REGISTERED: "bg-primary",
				INACTIVE: "bg-secondary"
			};
			return "badge yk-status-badge " + (map[key] || "bg-secondary");
		},
		resolvedId: function() {
			if(this.elementId) {
				return this.elementId;
			}
			if(this.idPrefix && this.idValue != null && this.idValue !== "") {
				return this.idPrefix + this.idValue;
			}
			return null;
		}
	},

	template: `
		<span :id="resolvedId" :class="badgeClass" :data-status="label">{{ label }}</span>
	`
};
