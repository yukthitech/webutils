import {$userService} from "./user-service.js";
import {$pageUrl} from "./common.js";

export var navBarComponents = {};

/**
 * @typedef {object} NavItem
 * @property {String} id - A unique ID for the navigation item.
 * @property {String} label - The text to display for the item.
 * @property {String} route - The URL hash value associated with this item (e.g., 'home').
 * @property {String} componentName - The tag name of the Vue component to be loaded when this item is active.
 * @property {String} script - The URL path to the JavaScript file where the component is defined.
 * @property {String} [uri] - The URL path to the HTML file for the component's template (if not an SFC).
 * @property {String} [icon] - A CSS class for an icon (e.g., 'bi bi-gear'). Used by side-bar.
 * @property {String} [iconColor] - A CSS color for the icon (e.g., 'blue'). Used by side-bar.
 * @property {boolean} [disabled] - If true, the item will be disabled.
 */

/**
 * A responsive, top navigation bar, typically used as the main navigation for an application.
 * It manages routing via URL hashes and dynamically loads and renders child components.
 * @vue-component
 */
navBarComponents['yk-route-nav-bar'] = {
	"props": {
		/**
		 * A unique identifier for the navigation bar. This is used to manage routing state in the URL hash.
		 * @type {String}
		 * @required
		 */
		"id": { "type": String},
		
		/**
		 * The URL path to the logo image to be displayed on the left side.
		 * @type {String}
		 */
		"logoIcon": { "type": String},
		
		/**
		 * The `id` of the navigation item that should be selected by default on initial load.
		 * @type {String}
		 */
		"defaultItem": { "type": String},
		
		/**
		 * An array of navigation item objects to be displayed in the bar.
		 * @type {Array<NavItem>}
		 * @required
		 */
		"items": {
			type: Object,
			"required": true
		},
		
		/**
		 * If `true`, the user display name and logout button will be hidden.
		 * @type {Boolean}
		 * @default false
		 */
		"disableUserInfo": { "type": Boolean, "default": false },

		/**
		 * If `true`, renders with a style suitable for a sub-navigation bar.
		 * @type {Boolean}
		 * @default false
		 */
		"subnav": { "type": Boolean, "default": false },
	},
	
	"data": function() {
		return {
			/**
			 * A map from item ID to the full item object for quick lookups.
			 * @type {Object<String, NavItem>}
			 */
			"idToItem": {},
			
			/**
			 * The currently active navigation item object.
			 * @type {NavItem}
			 */
			"activeItem": null,
			
			/**
			 * Holds the application instance.
			 * @type {object}
			 */
			"appInstance": null,
			
			/**
			 * Holds the details of the currently logged-in user.
			 * @type {object}
			 */
			"userDetails": null
		}; 
	},
	
	"created": function() {
		this.activate();
	},
	
	"methods":
	{
		/**
		 * Initializes the component by building the item map, setting the initial active route from the URL hash,
		 * and fetching user details.
		 * @private
		 */
		"activate": function() {
			for(let item of this.items) {
				this.idToItem[item.id] = item;
				item.active = false;
			}

			let routeParams = $pageUrl.fetchInfo().hashParams;

			if(!routeParams || !routeParams[this.id] || !this.setActiveRoute(routeParams[this.id])) {
				this.onClick(this.defaultItem);
			}

			if(!this.disableUserInfo) {
				$userService.getUserDetails($.proxy(function(userDet){
					this.userDetails = userDet;
				}, this));
			}
		},
		
		/**
		 * Sets the active item based on a route value from the URL.
		 * @param {String} route - The route value to match.
		 * @returns {boolean} True if a matching item was found and activated, false otherwise.
		 * @private
		 */
		"setActiveRoute": function(route) {
			for(let id in this.idToItem) {
				if(this.idToItem[id].route == route){
					this.onClick(id);
					return true;
				}
			}
			
			return false;
		},
		
		/**
		 * Sets the specified item as the active one.
		 * @param {String} id - The ID of the item to activate.
		 * @returns {boolean} True if the item was successfully activated.
		 * @private
		 */
		"setActiveItem": function(id) {
			if(!this.idToItem[id]) {
				return false;
			}
			
			if(this.activeItem){
				if(this.activeItem.id == id){
					return true;
				}
				
				this.activeItem.active = false;
			}

			this.activeItem = this.idToItem[id];
			this.activeItem.active = true;
			return true;
		},
		
		/**
		 * Asynchronously loads the component definition (script and template) for a navigation item if it hasn't been loaded yet.
		 * @param {NavItem} activeItem - The item whose component needs to be loaded.
		 * @param {Function} callback - A callback function to execute after the component is loaded.
		 * @private
		 */
		"loadNavComponent": async function(activeItem, callback) {
			// if the component def is already loaded
			if(activeItem.componentDef || !activeItem.componentName) {
				callback(activeItem);
				return;
			}
			
			// if the component def is not yet loaded
			const {default: componentDef} = await import(activeItem.script);
			
			let response = await fetch(activeItem.uri);
			let htmlContent = await response.text();
			
			componentDef.template = htmlContent;
			activeItem.componentDef = componentDef;
			
			callback(activeItem);
			$.application.component(activeItem.componentName, componentDef);
		},
		
		/**
		 * Handles the click event on a navigation item. It sets the item as active, updates the URL hash,
		 * and emits the `navChanged` event.
		 * @param {String} id - The ID of the clicked item.
		 */
		"onClick": function(id) {
			if(!this.setActiveItem(id)) {
				return;
			}
			
			this.loadNavComponent(this.activeItem, $.proxy(function(activeItem){
				let routeInfo = {"hashParams": {}};
				routeInfo.hashParams[this.id] = activeItem.route;
				$pageUrl.modifyInfo(routeInfo);
	
				this.$emit('navChanged', activeItem);					
			}, this));
		},
		
		/**
		 * Handles the click event on the logo, activating the default item.
		 */
		"onDefaultClick": function() {
			this.onClick(this.defaultItem);
		},
		
		/**
		 * Handles the logout action.
		 */
		"onLogout": function() {
			$userService.logout();
		}
	},
	
	template: `
		<nav class="navbar navbar-expand-lg navbar-light bg-light" :class="subnav ? 'yk-subnavbar' : 'yk-navbar'">
			<div class="container-fluid">
				<a class="navbar-brand" style="padding: 0px; margin-right: 5rem; cursor: pointer;" @click="onDefaultClick" v-if="logoIcon">
					<img :src="logoIcon" alt="Logo" class="navbar-logo">
				</a>
				<div v-if="subnav" style="margin-right: 15rem;">&nbsp;</div>
				<button class="navbar-toggler" type="button"
					data-bs-toggle="collapse" 
					:data-bs-target="'#' + id + '-navbarSupportedContent'"
					:aria-controls="'#' + id + '-navbarSupportedContent'" 
					aria-expanded="false"
					aria-label="Toggle navigation">
					<span class="navbar-toggler-icon"></span>
				</button>
				<div class="collapse navbar-collapse" :id="id + '-navbarSupportedContent'">
					<ul class="navbar-nav me-auto mb-2 mb-lg-0" >
						<li class="nav-item" v-for="item in items">
							<a :id="item.id" class="nav-link" :class="item.active ? 'yk-active-nav-link' : 'yk-nav-link'" 
								aria-current="page" @click="onClick(item.id)">
								{{item.label}}
							</a>
						</li>
					</ul>
					<div class="d-flex yk-navbar-user" v-if="!disableUserInfo">
						<span>{{userDetails.displayName}}</span> 
						| 
						<a href="#" @click="onLogout"> Logout </a>
					</div>
				</div>
			</div>
		</nav>
	`
};

/**
 * A vertical navigation bar, typically used for sub-navigation within a specific section of an application.
 * It manages routing via URL hashes and dynamically loads and renders child components.
 * @vue-component
 */
navBarComponents['yk-route-side-bar'] = {
	"props": {
		/**
		 * A unique identifier for the side bar. This is used to manage routing state in the URL hash.
		 * @type {String}
		 * @required
		 */
		"id": { "type": String},
		
		/**
		 * The `id` of the navigation item that should be selected by default on initial load.
		 * @type {String}
		 */
		"defaultItem": { "type": String},
		
		/**
		 * An array of navigation item objects to be displayed in the bar.
		 * @type {Array<NavItem>}
		 * @required
		 */
		"items": {
			type: Object,
			"required": true
		},
		
	},
	
	"data": function() {
		return {
			/**
			 * A map from item ID to the full item object for quick lookups.
			 * @type {Object<String, NavItem>}
			 */
			"idToItem": {},
			/**
			 * The currently active navigation item object.
			 * @type {NavItem}
			 */
			"activeItem": null,
			/**
			 * Holds the application instance.
			 * @type {object}
			 */
			"appInstance": null,
			/**
			 * Holds the details of the currently logged-in user.
			 * @type {object}
			 */
			"userDetails": null
		}; 
	},
	
	"created": function() {
		this.activate();
	},
	
	"methods":
	{
		/**
		 * Initializes the component by building the item map and setting the initial active route from the URL hash.
		 * @private
		 */
		"activate": function() {
			for(let item of this.items) {
				this.idToItem[item.id] = item;
				item.active = false;
			}

			let routeParams = $pageUrl.fetchInfo().hashParams;

			if(!routeParams || !routeParams[this.id] || !this.setActiveRoute(routeParams[this.id])) {
				this.onClick(this.defaultItem);
			}

			if(!this.disableUserInfo) {
				$userService.getUserDetails($.proxy(function(userDet){
					this.userDetails = userDet;
				}, this));
			}
		},
		
		/**
		 * Sets the active item based on a route value from the URL.
		 * @param {String} route - The route value to match.
		 * @returns {boolean} True if a matching item was found and activated, false otherwise.
		 * @private
		 */
		"setActiveRoute": function(route) {
			for(let id in this.idToItem) {
				if(this.idToItem[id].route == route){
					this.onClick(id);
					return true;
				}
			}
			
			return false;
		},
		
		/**
		 * Sets the specified item as the active one.
		 * @param {String} id - The ID of the item to activate.
		 * @returns {boolean} True if the item was successfully activated.
		 * @private
		 */
		"setActiveItem": function(id) {
			if(!this.idToItem[id]) {
				return false;
			}
			
			if(this.activeItem){
				if(this.activeItem.id == id){
					return true;
				}
				
				this.activeItem.active = false;
			}

			this.activeItem = this.idToItem[id];
			this.activeItem.active = true;
			return true;
		},
		
		/**
		 * Asynchronously loads the component definition (script and template) for a navigation item if it hasn't been loaded yet.
		 * @param {NavItem} activeItem - The item whose component needs to be loaded.
		 * @param {Function} callback - A callback function to execute after the component is loaded.
		 * @private
		 */
		"loadNavComponent": async function(activeItem, callback) {
			// if the component def is already loaded
			if(activeItem.componentDef || !activeItem.componentName) {
				callback(activeItem);
				return;
			}
			
			// if the component def is not yet loaded
			const {default: componentDef} = await import(activeItem.script);
			
			let response = await fetch(activeItem.uri);
			let htmlContent = await response.text();
			
			componentDef.template = htmlContent;
			activeItem.componentDef = componentDef;
			
			callback(activeItem);
			$.application.component(activeItem.componentName, componentDef);
		},
		
		/**
		 * Handles the click event on a navigation item. It sets the item as active, updates the URL hash,
		 * and emits the `navChanged` event.
		 * @param {String} id - The ID of the clicked item.
		 */
		"onClick": function(id) {
			if(!this.setActiveItem(id)) {
				return;
			}
			
			this.loadNavComponent(this.activeItem, $.proxy(function(activeItem){
				let routeInfo = {"hashParams": {}};
				routeInfo.hashParams[this.id] = activeItem.route;
				$pageUrl.modifyInfo(routeInfo);
	
				this.$emit('navChanged', activeItem);					
			}, this));
		},
		
		/**
		 * Handles the click event on the logo, activating the default item.
		 */
		"onDefaultClick": function() {
			this.onClick(this.defaultItem);
		},
	},
	
	template: `
		<div class="yk-sidebar d-flex flex-column p-2" :id="id + '-sidebar'">
		    <a :id="item.id" class="text-decoration-none" v-for="item in items" 
				:class="item.active ? 'yk-active-nav-link' : 'yk-nav-link'"
				@click="onClick(item.id)">
		        <i :class="item.icon" :style="'font-size: 1.8rem;color: ' + item.iconColor + ';'" :title="item.label"></i>
		        <span class="menu-text" style="margin-top: 0.5rem; margin-left: 10px;">{{item.label}}</span>
		    </a>
		    <button class="btn btn-secondary mt-auto" onclick="toggleSidebar()">Toggle</button>
		</div>
	`
};
