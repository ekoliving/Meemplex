class MajiUI.MajiSlider extends MovieClip {
	var _value;
	
	var _inboundBinding;
	var _outboundBinding;

	var _sliderWidth;

	public function MajiSlider() {
	}

	public function set value(v:Number):Void {
		trace ("value = " + v);
		_outboundBinding.valueChanged(v, 0, 1);
//		this._value = v;
	}
	
	public function get value():Number {
		return _value;
	}
	
	public function get sliderWidth():Number {
		return _sliderWidth;
	}
	
	public function set sliderWidth(w:Number):Void {
		this._sliderWidth = Math.round(w);
	}
	
	/**
	 * Method called by inbound binding.
	 */
	public function valueChanged(v:Number, min:Number, max:Number):Void {
		trace ("got value: " + v);
		this._value = (v - min) / (max - min);
		
		// update display of button
		if (v > 0) {
			gotoAndPlay("trueframe");
		}
		else {
			gotoAndPlay("falseframe");
		}
	}
	
	public function facetHealthEvent(event:Object):Void {
		// TODO enable, disable the component
		trace("got health event: " + event);
	}
	
	public function facetEvent(event:Object):Void {
			var args:Array = event.args;
			var f = eval("this." + event.method);
			if (f == undefined) {
				trace("This button does not know of function: " + event.method);
			}
			else {
				f.apply(this, args);
			}
	}
	
	public function set label(label:String) {
		this.label = label;
	}

}