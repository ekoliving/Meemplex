/**
 * A facet event
 */
class MajiRPC.FacetEvent {
	public var meemPath:String;
	public var facetId:String;
	public var facetClass:String;
	public var method:String;
	public var args:Array;
	
	/**
	 */
	public function FacetEvent(meemPath:String, facetId:String, method:String, args:Array) {
		this.meemPath = meemPath;
		this.facetId = facetId;
		this.method = method;
		this.args = args;
	}
}