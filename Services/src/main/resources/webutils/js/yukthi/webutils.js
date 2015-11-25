function init()
{
	if(!$.appConfiguration)
	{
		console.error("No appliation configuration is defined.");
		return;
	}
	
	//Load templates
	var templateFiles = $.appConfiguration.templates;

	if(templateFiles)
	{
		for(var i = 0; i < templateFiles.length; i++)
		{
			console.log("Loading template file - " + templateFiles[i]);
			$.loadCustomDirectives(templateFiles[i]);
		}
	}
};

init();

/*
function addCssFile(path)
{
	var head  = document.getElementsByTagName('head')[0];
    var link  = document.createElement('link');
    link.rel  = 'stylesheet';
    link.type = 'text/css';
    link.href = path;
    link.media = 'all';
    head.appendChild(link);
}

function addJsFile(path)
{
	var head  = document.getElementsByTagName('head')[0];
    var script  = document.createElement('script');
    script.type = 'text/javascript';
    script.src = path;
    script.media = 'all';
    head.appendChild(script);
}

addScript("../webutils/js/jquery-2.1.4.min.js");
*/
