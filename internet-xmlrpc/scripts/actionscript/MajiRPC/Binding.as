import mx.events.EventDispatcher;

/**
 *
 */
class MajiRPC.Binding {
	
	private var _meemPath:String;
	private var _facetId:String; 
	private var _facetClass: String;
	
	private var _clientGateway: MajiRPC.RPCClient
	
	function addEventListener() {};
 	function removeEventListener() {};	
	function dispatchEvent() {};	
	
	/**
	 * Constructor
	 */
	public function Binding() {
		// initilize this class as an event dispatcher
		mx.events.EventDispatcher.initialize(this);
	}
	
	
	/* -------------------- getters and setters --------------------- */
	
	[Inspectable()]
	public function get MeemPath() : String {
		return this._meemPath;
	}
	
	public function set MeemPath(path : String) {
		unbind();
		this._meemPath = path;
		bind();
	}
	
	[Inspectable()]
	public function get FacetId() : String {
		return this._facetId;
	}
	
	public function set FacetId(id : String) {
		unbind();
		this._facetId = id;
		bind();
	}

	[Inspectable()]
	public function get FacetClass() : String {
		return this._facetClass;
	}
	
	public function set FacetClass(c : String) {
		unbind();
		this._facetClass = c;
		bind();
	}

	[Inspectable()]
	public function get ClientGateway() : MajiRPC.RPCClient {
		return this._clientGateway;
	}
	
	public function set ClientGateway(gateway : MajiRPC.RPCClient) {
		unbind();
		this._clientGateway = gateway;
		bind();
	}

	/**
	 * Called when a FacetHealthEvent is received from the RPCClient.
	 */
	public function facetHealthEvent(event:Object):Void {
		// is the event for this Binding?
		if (forThis(event)) {
//			trace("+++ Got facet health event '" + event.meemPath + "." + event.facetId + "': lcs=" + event.lifeCyceState + ", bs=" + event.bindingState);
			dispatchEvent(event);		// dispatch the event
		}
	}
	

	function bind():Void {		
		if (_clientGateway != undefined && 
			_meemPath != undefined && 
			_facetId != undefined
		)
		{
			_clientGateway.addEventListener("facetHealthEvent", this);
		}
	}

	function unbind():Void {
		// TODO unregister this binding
		_clientGateway.removeEventListener("facetHealthEvent", this);
	}

	/**
	 * is the event for this Binding?
	 */
	function forThis(event:Object) : Boolean {
		return ( 
			(event.meemPath.toLowerCase() == _meemPath.toLowerCase()) && 
			(event.facetId.toLowerCase() == _facetId.toLowerCase())
		) ;
	}


}

