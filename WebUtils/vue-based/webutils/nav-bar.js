import {$userService} from "./user-service.js";
import {$pageUrl} from "./common.js";

export var navBarComponents = {};

navBarComponents['yk-route-nav-bar'] = {
	"props": {
		/**
		 * Unique id for this nav bar. This is used in routes also.
		 */
		"id": { "type": String},
		
		/**
		 * Logo to be used on nav bar
		 */
		"logoIcon": { "type": String},
		
		/**
		 * Default item id in the list. Will be used when logo
		 * is clicked.
		 */
		"defaultItem": { "type": String},
		
		/**
		 * List of nav bar items. Each should have properties
		 *   id - id to be used for item
		 *   label - Label to be used
		 *   contents - List of content object. Each object contains 
		 * 		- locator: where content has to be populated
		 *      - uri: Uri to be loaded
		 * 		- script - Module script to be loaded
		 *   disabled - Flag indicating if this item is disabled
		 */
		"items": {
			type: Object,
			"required": true
		},
		
		/**
		 * Flag indicating if the user info should be displayed or not.
		 */
		"disableUserInfo": { "type": Boolean, "default": false },

		/**
		 * Flag indicating if this is a subnav bar.
		 */
		"subnav": { "type": Boolean, "default": false },
	},
	
	"data": function() {
		return {
			"idToItem": {},
			"activeItem": null,
			"appInstance": null,
			"userDetails": null
		}; 
	},
	
	"created": function() {
		this.activate();
	},
	
	"methods":
	{
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
		
		"setActiveRoute": function(route) {
			for(let id in this.idToItem) {
				if(this.idToItem[id].route == route){
					this.onClick(id);
					return true;
				}
			}
			
			return false;
		},
		
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
		
		"onDefaultClick": function() {
			this.onClick(this.defaultItem);
		},
		
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


navBarComponents['yk-route-side-bar'] = {
	"props": {
		/**
		 * Unique id for this nav bar. This is used in routes also.
		 */
		"id": { "type": String},
		
		/**
		 * Default item id in the list. Will be used when logo
		 * is clicked.
		 */
		"defaultItem": { "type": String},
		
		/**
		 * List of nav bar items. Each should have properties
		 *   id - id to be used for item
		 *   label - Label to be used
		 *   contents - List of content object. Each object contains 
		 * 		- locator: where content has to be populated
		 *      - uri: Uri to be loaded
		 * 		- script - Module script to be loaded
		 *   disabled - Flag indicating if this item is disabled
		 */
		"items": {
			type: Object,
			"required": true
		},
		
	},
	
	"data": function() {
		return {
			"idToItem": {},
			"activeItem": null,
			"appInstance": null,
			"userDetails": null
		}; 
	},
	
	"created": function() {
		this.activate();
	},
	
	"methods":
	{
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
		
		"setActiveRoute": function(route) {
			for(let id in this.idToItem) {
				if(this.idToItem[id].route == route){
					this.onClick(id);
					return true;
				}
			}
			
			return false;
		},
		
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
