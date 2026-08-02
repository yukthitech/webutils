import { newVueUiComponent } from './input-fields.js';

var CM_BASE = '/lib/codemirror-5.65.16';
var MARKED_SRC = '/lib/marked-15.0.7/marked.min.js';
var DOMPURIFY_SRC = '/lib/dompurify-3.2.4/purify.min.js';

var _mdLibsPromise = null;

function loadCss(href)
{
	if(document.querySelector('link[data-yk-md="' + href + '"]'))
	{
		return;
	}

	var link = document.createElement('link');
	link.rel = 'stylesheet';
	link.href = href;
	link.setAttribute('data-yk-md', href);
	document.head.appendChild(link);
}

function loadScript(src)
{
	return new Promise(function(resolve, reject)
	{
		var existing = document.querySelector('script[data-yk-md="' + src + '"]');
		if(existing)
		{
			if(existing.getAttribute('data-yk-md-loaded') === '1')
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
		script.setAttribute('data-yk-md', src);
		script.onload = function()
		{
			script.setAttribute('data-yk-md-loaded', '1');
			resolve();
		};
		script.onerror = function()
		{
			reject(new Error('Failed to load ' + src));
		};
		document.head.appendChild(script);
	});
}

function ensureMarkdownLibs()
{
	if(_mdLibsPromise)
	{
		return _mdLibsPromise;
	}

	loadCss(CM_BASE + '/lib/codemirror.css');

	_mdLibsPromise = loadScript(CM_BASE + '/lib/codemirror.js')
		.then(function() { return loadScript(CM_BASE + '/mode/xml/xml.js'); })
		.then(function() { return loadScript(CM_BASE + '/mode/javascript/javascript.js'); })
		.then(function() { return loadScript(CM_BASE + '/mode/css/css.js'); })
		.then(function() { return loadScript(CM_BASE + '/mode/htmlmixed/htmlmixed.js'); })
		.then(function() { return loadScript(CM_BASE + '/mode/clike/clike.js'); })
		.then(function() { return loadScript(CM_BASE + '/mode/markdown/markdown.js'); })
		.then(function() { return loadScript(MARKED_SRC); })
		.then(function() { return loadScript(DOMPURIFY_SRC); });

	return _mdLibsPromise;
}

function scrollRatio(el)
{
	var max = el.scrollHeight - el.clientHeight;
	if(max <= 0)
	{
		return 0;
	}

	return el.scrollTop / max;
}

function applyScrollRatio(el, ratio)
{
	var max = el.scrollHeight - el.clientHeight;
	if(max <= 0)
	{
		return;
	}

	el.scrollTop = ratio * max;
}

newVueUiComponent('yk-markdown-editor', {
	"props": {
		"height": { "type": String, "default": "400px" },
		"syncScroll": { "type": Boolean, "default": true },
		"viewMode": { "type": String, "default": "split" }
	},

	"data": {
		"previewHtml": "",
		"leftWidthPercent": 50,
		"activeViewMode": "split",
		"libsReady": false
	},

	"computed": {
		"editorPaneStyle": function()
		{
			if(this.activeViewMode === 'edit')
			{
				return { "flex": "1 1 auto", "width": "100%" };
			}

			return {
				"flex": "0 0 " + this.leftWidthPercent + "%",
				"maxWidth": "calc(" + this.leftWidthPercent + "% - 3px)"
			};
		},

		"previewPaneStyle": function()
		{
			if(this.activeViewMode === 'preview')
			{
				return { "flex": "1 1 auto", "width": "100%" };
			}

			return { "flex": "1 1 auto", "minWidth": 0 };
		}
	},

	"watch": {
		"viewMode": function(newVal)
		{
			this.setViewMode(newVal || 'split');
		}
	},

	"beforeUnmount": function()
	{
		this.teardownResizeListeners();
		this.destroyEditor();
	},

	"methods": {
		"onMounted": function()
		{
			this.activeViewMode = this.viewMode || 'split';

			var self = this;
			ensureMarkdownLibs().then(function()
			{
				self.libsReady = true;
				self.$nextTick(function()
				{
					self.initEditor();
					self.renderPreview(self.fieldValue || '');
				});
			}).catch(function(err)
			{
				console.error('yk-markdown-editor: failed to load libraries', err);
			});
		},

		"onModelValueChanged": function(newVal)
		{
			var content = newVal ? newVal : '';
			this.setEditorContent(content);
			this.renderPreview(content);
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
				console.error('CodeMirror is not available for yk-markdown-editor');
				return;
			}

			if(this._cm)
			{
				return;
			}

			var self = this;
			this._cm = CodeMirror(this.$refs.cmHost, {
				"value": this.fieldValue ? this.fieldValue : '',
				"mode": "markdown",
				"lineNumbers": true,
				"lineWrapping": true,
				"viewportMargin": Infinity,
				"tabSize": 2
			});

			this._cm.on('change', function()
			{
				self.onEditorValueChange();
			});

			this._cm.on('scroll', function()
			{
				self.onEditorScroll();
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
			this.renderPreview(newContent);

			if(this.fieldValue !== newContent)
			{
				this.fieldValue = newContent;
			}
		},

		"renderPreview": function(md)
		{
			var source = md ? md : '';

			if(typeof marked === 'undefined' || typeof DOMPurify === 'undefined')
			{
				this.previewHtml = '';
				return;
			}

			try
			{
				var html = marked.parse(source);
				this.previewHtml = DOMPurify.sanitize(html);
			}
			catch(ex)
			{
				console.error('yk-markdown-editor: preview render failed', ex);
				this.previewHtml = '';
			}
		},

		"setViewMode": function(mode)
		{
			var next = (mode === 'edit' || mode === 'preview' || mode === 'split') ? mode : 'split';
			this.activeViewMode = next;
			this.$emit('update:viewMode', next);

			var self = this;
			this.$nextTick(function()
			{
				if(self._cm)
				{
					self._cm.refresh();
				}
			});
		},

		"onEditorScroll": function()
		{
			if(!this.syncScroll || this._syncingScroll || this.activeViewMode !== 'split')
			{
				return;
			}

			var cm = this._cm;
			var preview = this.$refs.previewPane;
			if(!cm || !preview)
			{
				return;
			}

			var info = cm.getScrollInfo();
			var max = info.height - info.clientHeight;
			var ratio = max <= 0 ? 0 : (info.top / max);

			this._syncingScroll = true;
			applyScrollRatio(preview, ratio);

			var self = this;
			requestAnimationFrame(function()
			{
				self._syncingScroll = false;
			});
		},

		"onPreviewScroll": function()
		{
			if(!this.syncScroll || this._syncingScroll || this.activeViewMode !== 'split')
			{
				return;
			}

			var cm = this._cm;
			var preview = this.$refs.previewPane;
			if(!cm || !preview)
			{
				return;
			}

			var ratio = scrollRatio(preview);
			var info = cm.getScrollInfo();
			var max = info.height - info.clientHeight;

			this._syncingScroll = true;
			cm.scrollTo(null, ratio * (max > 0 ? max : 0));

			var self = this;
			requestAnimationFrame(function()
			{
				self._syncingScroll = false;
			});
		},

		"startResize": function(event)
		{
			if(this.activeViewMode !== 'split')
			{
				return;
			}

			event.preventDefault();
			var split = this.$refs.splitContainer;
			if(!split)
			{
				return;
			}

			var self = this;
			this._onResizeMove = function(ev)
			{
				var rect = split.getBoundingClientRect();
				if(rect.width <= 0)
				{
					return;
				}

				var pct = ((ev.clientX - rect.left) / rect.width) * 100;
				self.leftWidthPercent = Math.max(20, Math.min(80, pct));
			};

			this._onResizeUp = function()
			{
				self.teardownResizeListeners();
				self.$nextTick(function()
				{
					if(self._cm)
					{
						self._cm.refresh();
					}
				});
			};

			document.addEventListener('mousemove', this._onResizeMove);
			document.addEventListener('mouseup', this._onResizeUp);
		},

		"teardownResizeListeners": function()
		{
			if(this._onResizeMove)
			{
				document.removeEventListener('mousemove', this._onResizeMove);
				this._onResizeMove = null;
			}

			if(this._onResizeUp)
			{
				document.removeEventListener('mouseup', this._onResizeUp);
				this._onResizeUp = null;
			}
		}
	},

	"template": `
		<div class="form-group yk-markdown-editor" :id="fieldInfo.name">
			<div class="yk-md-header">
				<label class="webutil-field-label form-label mb-0" v-if="!hideLabel && fieldInfo.label && fieldInfo.label.length &gt; 0">{{fieldInfo.label}}:</label>
				<div class="yk-md-toolbar btn-group btn-group-sm" role="group" aria-label="Markdown view mode">
					<button type="button" class="btn btn-outline-secondary"
						:id="fieldInfo.name + '-md-mode-edit'"
						:class="{ active: activeViewMode === 'edit' }"
						title="Edit only"
						aria-label="Edit only"
						@click="setViewMode('edit')"><i class="bi bi-layout-sidebar" aria-hidden="true"></i></button>
					<button type="button" class="btn btn-outline-secondary"
						:id="fieldInfo.name + '-md-mode-split'"
						:class="{ active: activeViewMode === 'split' }"
						title="Split view"
						aria-label="Split view"
						@click="setViewMode('split')"><i class="bi bi-layout-split" aria-hidden="true"></i></button>
					<button type="button" class="btn btn-outline-secondary"
						:id="fieldInfo.name + '-md-mode-preview'"
						:class="{ active: activeViewMode === 'preview' }"
						title="Preview only"
						aria-label="Preview only"
						@click="setViewMode('preview')"><i class="bi bi-layout-sidebar-reverse" aria-hidden="true"></i></button>
				</div>
			</div>
			<div class="yk-md-split" ref="splitContainer"
				:class="{ 'is-invalid': displayError() }"
				:style="{ height: height }">
				<div class="yk-md-editor" v-show="activeViewMode !== 'preview'" :style="editorPaneStyle">
					<div ref="cmHost" class="yk-md-cm-host" :id="fieldInfo.name + '-md-editor'"></div>
				</div>
				<div class="yk-md-handle" v-show="activeViewMode === 'split'"
					:id="fieldInfo.name + '-md-handle'"
					title="Drag to resize"
					@mousedown="startResize"></div>
				<div class="yk-md-preview" v-show="activeViewMode !== 'edit'"
					:id="fieldInfo.name + '-md-preview'"
					ref="previewPane" :style="previewPaneStyle"
					@scroll="onPreviewScroll">
					<div class="yk-md-preview-body markdown-body" v-html="previewHtml"></div>
				</div>
			</div>
			<div class="invalid-feedback" v-if="displayError()">{{fieldInfo.error}}</div>
		</div>
	`
});
