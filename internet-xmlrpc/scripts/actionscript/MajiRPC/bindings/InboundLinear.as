
class MajiRPC.bindings.InboundLinear extends MajiRPC.InboundBinding {
	
	public function InboundLinear() {
		super();
		FacetClass  = "org.openmaji.common.Linear";
	}

	public function valueChanged(n:Number, min:Number, max:Number) : Void {
		trace ("+++ InboundLinear.valueChanged: " + n +", min=" + min + ", max=" + max);
	}
}