import MajiRPC.FacetEvent;

class MajiRPC.OutboundBinding extends MajiRPC.Binding {
	
	/**
	 * arbirary method call
	 */ 
	public function __resolve(name:String) {
		
		// reserved names
		if (name == "_meemPath") {
			return undefined;
		}
		if (name == "_facetId") {
			return undefined;
		}
		if (name == "_facetClass") {
			return undefined;
		}
		if (name == "_rpcClient") {
			return undefined;
		}
		if (name == "handleEvent") {
			return undefined;
		}
		if (name == "facetHealthEventHandler") {
			return undefined;
		}
		if (name == "__q_facetHealthEvent") {
			return undefined;
		}
		
		//trace("made an outbound call to " + name);

		// assume the call is on an outbound Facet
		var f:Function = function () { 
			arguments.unshift(name);
			this.sendFunction.apply(this, arguments); 
		};

		// create a new object method and assign it the reference
		this[name] = f;
		
		// return the reference to the function
		return f;
	}
	
	private function sendFunction(method:String) : Void {
		// make an event and send it to the MajiRPC
		
		arguments.shift();
		//trace("Called " + method + " ( " + arguments.join(',') + " )");
		
		if (this.ClientGateway) {
			var event = new FacetEvent();
			
			event.meemPath   = this.MeemPath;
			event.facetId    = this.FacetId;
			event.facetClass = this.FacetClass;
			event.method     = method;
			event.args       = arguments;
			
			ClientGateway.facetEvent(event);
		}
		
	}
	
	function bind() : Void {
		super.bind();
		
		if (ClientGateway != undefined && 
			MeemPath != undefined && 
			FacetId != undefined && 
			FacetClass != undefined) 
		{
			super.ClientGateway.addOutbound(this);
		}
	}

	function unbind():Void {
		super.ClientGateway.removeOutbound(this);
	}

}