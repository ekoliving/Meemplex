
class MajiRPC.bindings.BindingFactory {
	
	var inboundBindings:Object = {};
	
	var outboundBindings:Object = {};
	
	public function BindingFactory() {
		// default bindings
		registerInbound("org.openmaji.common.Binary",            MajiRPC.bindings.InboundBinary);
		registerInbound("org.openmaji.common.Linear",            MajiRPC.bindings.InboundLinear);
		registerInbound("net.majitek.maji.system.space.CategoryClient", MajiRPC.bindings.InboundCategoryClient);
		registerInbound("net.majitek.maji.system.meem.FacetClient",     MajiRPC.bindings.InboundFacetClient);
	}


	public function createInbound(specification:String) : MajiRPC.InboundBinding {
		var bindingClass = inboundBindings[specification];
		
		if (bindingClass == undefined) {
			//bindingClass = MajiRPC.InboundBinding;
		}
		
		return new bindingClass();
	}
	
	public function registerInbound(specification:String, bindingClass:Function) {
		inboundBindings[specification] = bindingClass;		
	}
}