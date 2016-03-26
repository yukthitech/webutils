$.slideImages = function(id) {
	var element = $("#" + id);
	var interval = element.attr("interval");
	
	if(!interval)
	{
		interval = 2000;
	}
	else
	{
		interval = parseInt(interval);
	}
	
	var context = {"element": element, "mouseEntered": false};
	
	context.changeImageIndexBy = function(val) {
		//find currently active image
		var slideImages = element.find(".slideImage");
		var activeIndex = -1;
		
		for(var i = 0; i < slideImages.length; i++)
		{
			if($(slideImages.get(i)).hasClass("slideImageActive"))
			{
				activeIndex = i;
				break;
			}
		}
		
		//move to next Image
		$(slideImages[activeIndex]).removeClass("slideImageActive");
		$(slideImages[activeIndex]).addClass("slideImageInactive");
		activeIndex += val;
		
		if(activeIndex >= slideImages.length)
		{
			activeIndex = 0;
		}
		
		if(activeIndex < 0)
		{
			activeIndex = slideImages.length - 1;
		}
		
		$(slideImages[activeIndex]).removeClass("slideImageInactive");
		$(slideImages[activeIndex]).addClass("slideImageActive");
	};
	
	setInterval($.proxy(function(){
		
		if(this.mouseEntered)
		{
			return;
		}

		this.changeImageIndexBy(1);
	}, context),  interval);
	
	var mouseEnterFunc = $.proxy(function(){
		this.mouseEntered = true;
	}, context);
	
	var mouseExitFunc = $.proxy(function(){
		this.mouseEntered = false;
	}, context);
	
	element.mousemove(mouseEnterFunc);
	element.mouseleave(mouseExitFunc);
	
	element.find(".slideImageNavBut").mousemove(mouseEnterFunc);
	element.find(".slideImageNavBut").mouseleave(mouseExitFunc);
	
	element.find(".leftNav").click($.proxy(function(){
		this.changeImageIndexBy(-1);
	}, context));
	
	element.find(".rightNav").click($.proxy(function(){
		this.changeImageIndexBy(1);
	}, context));
	
};

$.marquee = function(id, speed) {
	var element = $("#" + id);
	var iSpeed = 100;
	var contentElem = $(element.find(".contentElem").first());
	
	try
	{
		iSpeed = parseInt(speed);
	}catch(ex)
	{}
	
	var context = {
		"element": element, 
		"speed": iSpeed, 
		"mouseEntered": false, 
		"contentElem": contentElem
	};
	
	setInterval($.proxy(function(){
		if(this.mouseEntered)
		{
			return;
		}

		var containerHeight = context.element.height();
		var height = context.contentElem.height();
		var y = context.contentElem.position().top;
		
		if( (0-y) > height)
		{
			context.contentElem.css("top", containerHeight);
		}
		else
		{
			context.contentElem.css("top", y - 5);
		}
	}, context),  iSpeed);
	
	var mouseEnterFunc = $.proxy(function(){
		this.mouseEntered = true;
	}, context);
	
	var mouseExitFunc = $.proxy(function(){
		this.mouseEntered = false;
	}, context);
	
	element.mousemove(mouseEnterFunc);
	element.mouseleave(mouseExitFunc);
};

$.verticalTabs = function(element, $compile, $scope) {
	var contentContainer = $(element.find(".vtabContent").get(0));
	var tabs = element.find(".vtab");
	var activeTab = $(element.find(".vtab-active").get(0));

	var context = {
		"contentContainer" : contentContainer,
		"activeTabId": activeTab.attr("id"),
		"$scope" : $scope,
		"$compile": $compile,
		
		"loadHtml": function(htmlPath){
			$.get(htmlPath, $.proxy(function(data) {
				this.contentContainer.html(data);
				
				html = this.$compile(this.contentContainer.contents())(this.$scope);
				this.$scope.$digest();
			}, this));
		}
	};
	
	var tabClickFunc = $.proxy(function(e){
		var elem = $(e.currentTarget);
		var newTabId = elem.attr("id");
		
		if(newTabId == this.activeTabId)
		{
			return;
		}
		
		$("#" + this.activeTabId).removeClass("vtab-active").addClass("vtab");
		elem.removeClass("vtab").addClass("vtab-active");
		this.activeTabId = newTabId;
		
		var htmlPath = elem.attr("html-src");
		this.loadHtml(htmlPath);
	}, context);

	context.loadHtml(activeTab.attr("html-src"));

	tabs.on("click", tabClickFunc);
	activeTab.on("click", tabClickFunc)
};

$.imageField = function(element, $scope, field, actionHelper) {

	var imgElement = element.find("div.imgdiv");
	var fileElement = element.find("input[type='file']");
	
	$(imgElement).off('click').on('click', $.proxy(function(){
		$(this.fileElement).click();
	}, {"element": element, "$scope": $scope, "field": field, "actionHelper": actionHelper, "fileElement": fileElement}));
	
	$(fileElement).off('change').on('change', $.proxy(function(){
		var files = $(this.fileElement).get(0).files;
		
		if(!files || files.length <= 0)
		{
			return;
		}
		
		var model = {"file" : files[0]};

		this.actionHelper.invokeAction("files.upload", model, null, $.proxy(function(response, respConfig){
			if(response.code != 0)
			{
				throw "Failed to upload specified file";
			}
			
			var imageModel = {"fileId" : response.id, "newImage": true};
			
			eval("this.$scope." + field + "= imageModel");
			
			try
			{
				this.$scope.$digest();
			}catch(ex)
			{}
		}, this));
	}, {"element": element, "$scope": $scope, "field": field, "actionHelper": actionHelper, "fileElement": fileElement}));
};