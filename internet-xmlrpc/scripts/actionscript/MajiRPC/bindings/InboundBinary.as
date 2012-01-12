
class MajiRPC.bindings.InboundBinary extends MajiRPC.InboundBinding {

	public function InboundBinary() {
		super();
		FacetClass  = "org.openmaji.common.Binary";
	}

	public function valueChanged(b:Boolean) : Void {
		trace ("+++ InboundBinary.valueChanged: " + b);
	}
}