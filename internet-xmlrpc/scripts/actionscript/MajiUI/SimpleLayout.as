import MajiUI.MajiForm;

/**
 * Layout manager 
 * - menu at left
 * - other UI components in grid on right
 */
class MajiUI.SimpleLayout {

	var _components;		// components to layout
	var _form:MajiForm;				// form on which to layout components

	// boundary;
	var _left;
	var _top;
	var _right;
	var _bottom;
	
	// space between components
	var _vspace;
	var _hspace;

	public function SimpleLayout(form:MajiForm) {
		this._form = form;
		_components = {};
		
		_left = 150;
		_top = 10;
		_right = 10;
		_bottom = 10;	
		_vspace = 5;
		_hspace = 5;
	}

	public function addMenuComponent(component:Object):Void {
	}

	public function addComponent(name:String, component:Object):Void {
		// TODO check if already exists
		_components[name] = component;
		
		// TODO include layout constraints
	}
	
	public function removeComponent(name:String):Void {
		_components[name] = null;
	}
	
	public function layout():Void {
		//trace("laying out the form");
		
		// TODO (resize and) move the components on the form
		
		var x = _left;
		var y = _top;
		
		var maxheight = 20;
		for (var name in _components) {
			var component = _components[name];
			//trace("component[" + name + "]=" + component);
			
			if (component) {
				var width = x;
				if (component._width) {
					width += component._width;
				}
				else {
					width += 50;
				}
				
				if (width > (_form._width - _right) ) {
					// too wide, next row down
					y += (maxheight + _vspace);
					maxheight = 20;
					x = _left;
				}
				
				component._x = x;
				component._y = y;
	
				if (component._height > maxheight) {
					maxheight = component._height;
				}
				
				if (component._width) {
					x += component._width;
				}
				else {
					x += 50;
				}
				x += _vspace;
			}
		}
	}

}