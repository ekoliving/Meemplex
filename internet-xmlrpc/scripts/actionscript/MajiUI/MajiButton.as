import MajiRPC.InboundBinding;
import MajiRPC.OutboundBinding;

class MajiUI.MajiButton extends MovieClip {

	
	[Inspectable(defaultValue="LightButtonClip")]
	public var movieClip:String;


	var _value:Boolean;
	
	var _inboundBinding;
	var _outboundBinding;
	
	var _title;
	var _type:String;	
	
	var buttonMovieClip:MovieClip;
	
	public function MajiButton() {
		//movieClip = "LightButtonClip";
		//_label = "Lights";
		_value = undefined;
	}
	
	public function set InboundBinding(binding:InboundBinding) {
		this._inboundBinding = binding;
		_inboundBinding.addEventListener("facetEvent", this);
		_inboundBinding.addEventListener("facetHealthEvent", this);

	}

	public function set OutboundBinding(binding:OutboundBinding) {
		this._outboundBinding = binding;
	}

	/**
	 * Method called by inbound binding.
	 */
	public function valueChanged(v:Boolean):Void {
		//trace ("got value: " + v);
		this._value = v != 0;
		
		// update display of button
		if (v > 0) {
			gotoAndPlay("trueframe");
			buttonMovieClip.gotoAndPlay("trueframe");

		}
		else {
			gotoAndPlay("falseframe");
			buttonMovieClip.gotoAndPlay("falseframe");
		}
	}
	
	public function facetHealthEvent(event:Object):Void {
		// TODO enable, disable the component
		trace("got health event: " + event);
	}
	
	public function facetEvent(event:Object):Void {
		trace("got event: " + event);
			var args:Array = event.args;
			var f = eval("this." + event.method);
			if (f == undefined) {
				trace("This button does not know of function: " + event.method);
			}
			else {
				f.apply(this, args);
			}
	}
	
	[Inspectable(defaultValue="ENTRANCE")]
	public function set title(label:String) {
		this._title = label;
	}
	
	[Inspectable(defaultValue="LIGHTS :")]
	public function set type(s:String) {
		this._type = s;
	}
	
	public function get value():Boolean {
		return _value;
	}
	
	public function set value(v:Boolean) {
		var val = v ? 1 : 0;
		trace("binding = " + _outboundBinding + ". value: " + v + ", val=" + val);
		_outboundBinding.valueChanged(v);
		//_value = value;
	}

}