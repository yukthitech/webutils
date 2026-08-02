import { newVueUiComponent } from './input-fields.js';

var CM_BASE = '/lib/codemirror-5.65.16';
var _langLibsPromise = null;

function loadCss(href)
{
	if(document.querySelector('link[data-yk-lang="' + href + '"]'))
	{
		return;
	}

	var link = document.createElement('link');
	link.rel = 'stylesheet';
	link.href = href;
	link.setAttribute('data-yk-lang', href);
	document.head.appendChild(link);
}

function loadScript(src)
{
	return new Promise(function(resolve, reject)
	{
		var existing = document.querySelector('script[data-yk-lang="' + src + '"]');
		if(existing)
		{
			if(existing.getAttribute('data-yk-lang-loaded') === '1')
			{
				resolve();
				return;
			}

			existing.addEventListener('load', function() { resolve(); });
			existing.addEventListener('error', function() { reject(new Error('Failed to load ' + src)); });
			return;
		}

		var script = document.createElement('script');
		script.src = src;
		script.setAttribute('data-yk-lang', src);
		script.onload = function()
		{
			script.setAttribute('data-yk-lang-loaded', '1');
			resolve();
		};
		script.onerror = function()
		{
			reject(new Error('Failed to load ' + src));
		};
		document.head.appendChild(script);
	});
}

function ensureLanguageLibs()
{
	if(_langLibsPromise)
	{
		return _langLibsPromise;
	}

	loadCss(CM_BASE + '/lib/codemirror.css');
	loadCss(CM_BASE + '/addon/fold/foldgutter.css');

	_langLibsPromise = loadScript(CM_BASE + '/lib/codemirror.js')
		.then(function() { return loadScript(CM_BASE + '/mode/javascript/javascript.js'); })
		.then(function() { return loadScript(CM_BASE + '/mode/xml/xml.js'); })
		.then(function() { return loadScript(CM_BASE + '/addon/fold/foldcode.js'); })
		.then(function() { return loadScript(CM_BASE + '/addon/fold/foldgutter.js'); })
		.then(function() { return loadScript(CM_BASE + '/addon/fold/brace-fold.js'); })
		.then(function() { return loadScript(CM_BASE + '/addon/fold/xml-fold.js'); });

	return _langLibsPromise;
}

function resolveCmMode(languageType)
{
	if(languageType === 'XML')
	{
		return 'xml';
	}

	// JSON and JSON_SCHEMA
	return { name: 'javascript', json: true };
}

newVueUiComponent('yk-language-editor', {
	"props": {
		"height": { "type": String, "default": "220px" }
	},

	"data": {
		"libsReady": false
	},

	"methods": {
		"onMounted": function()
		{
			var self = this;
			ensureLanguageLibs().then(function()
			{
				self.libsReady = true;
				self.$nextTick(function()
				{
					self.initEditor();
				});
			}).catch(function(err)
			{
				console.error('yk-language-editor: failed to load libraries', err);
			});
		},

		"onModelValueChanged": function(newVal)
		{
			this.setEditorContent(newVal ? newVal : '');
		},

		"getFieldValue": function()
		{
			if(this._cm)
			{
				return this._cm.getValue();
			}

			return this.fieldValue ? this.fieldValue : '';
		},

		"initEditor": function()
		{
			if(typeof CodeMirror === 'undefined' || !this.$refs.cmHost)
			{
				console.error('CodeMirror is not available for yk-language-editor');
				return;
			}

			if(this._cm)
			{
				return;
			}

			var self = this;
			var languageType = this.fieldInfo.languageType || 'JSON';
			var mode = resolveCmMode(languageType);

			this._cm = CodeMirror(this.$refs.cmHost, {
				"value": this.fieldValue ? this.fieldValue : '',
				"mode": mode,
				"lineNumbers": true,
				"lineWrapping": false,
				"tabSize": 2,
				"indentUnit": 2,
				"foldGutter": true,
				"gutters": ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
				"extraKeys": {
					"Ctrl-Q": function(cm) { cm.foldCode(cm.getCursor()); }
				}
			});

			this._cm.on('change', function()
			{
				self.onEditorValueChange();
			});

			this.$nextTick(function()
			{
				if(self._cm)
				{
					self._cm.refresh();
				}
			});
		},

		"destroyEditor": function()
		{
			if(this._cm)
			{
				var wrapper = this._cm.getWrapperElement();
				if(wrapper && wrapper.parentNode)
				{
					wrapper.parentNode.removeChild(wrapper);
				}

				this._cm = null;
			}
		},

		"setEditorContent": function(content)
		{
			var cm = this._cm;
			if(!cm)
			{
				return;
			}

			var text = content ? content : '';
			if(cm.getValue() === text)
			{
				return;
			}

			var cursor = cm.getCursor();
			cm.setValue(text);
			try
			{
				cm.setCursor(cursor);
			}
			catch(ex)
			{
				// ignore invalid cursor after external replace
			}
		},

		"onEditorValueChange": function()
		{
			var newContent = this.getFieldValue();
			if(this.fieldValue !== newContent)
			{
				this.fieldValue = newContent;
			}
		}
	},

	"beforeUnmount": function()
	{
		this.destroyEditor();
	},

	"template": `
		<div class="form-group yk-language-editor" :id="fieldInfo.name">
			<label class="webutil-field-label form-label" v-if="!hideLabel && fieldInfo.label && fieldInfo.label.length &gt; 0">{{fieldInfo.label}}:</label>
			<div class="yk-lang-editor-wrap"
				:class="{ 'is-invalid': displayError() }"
				:style="{ height: height }">
				<div ref="cmHost" class="yk-lang-cm-host" :id="fieldInfo.name + '-lang-editor'"></div>
			</div>
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});
