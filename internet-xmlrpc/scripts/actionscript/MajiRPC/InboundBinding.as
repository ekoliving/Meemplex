
class MajiRPC.InboundBinding extends MajiRPC.Binding {
	
	public function InboundBinding() {
		super();
	}

	public function facetEvent(event:Object) : Void {
		//trace ("InboundBinding got facetEvent: " + event.meemPath + ":" + event.facetId + "." + event.method + "(" + event.args + ")");

		// check if the event is for this binding
		if (forThis(event)) {
			var args:Array = event.args;
			
			var f = eval("this." + event.method);

			//trace ("got function for '" + event.method + "': " + f);
			if (f == undefined) {
				trace("This binding does not know of function: " + event.method);
			}
			else {
				f.apply(this, args);
			}
			
			dispatchEvent(event);		// dispatch the event
		}
	}
	
	public function contentSent() {
		// content completed
	}
	
	private function bind() : Void {
		super.bind();
		
//		trace("InboundBinding: bind()");
		if (ClientGateway != undefined && 
			MeemPath != undefined && 
			FacetId != undefined && 
			FacetClass != undefined) 
		{
//			trace("InboundBinding: bind():addInbound()");
			super.ClientGateway.addInbound(this);
		}
	}
	
	private function unbind() : Void {
		if (ClientGateway != undefined && 
			MeemPath != undefined && 
			FacetId != undefined && 
			FacetClass != undefined)
		{
			super.ClientGateway.removeInbound(this);
		}
	}
	
}