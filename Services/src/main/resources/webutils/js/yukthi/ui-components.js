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