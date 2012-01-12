/**
 * A facet event
 */
class MajiRPC.FacetHealthEvent {
	public var meemPath:String;
	public var facetId:String;
	public var lifeCyceState:String;
	public var bindingState:Number;

	/**
	 */
	public function FacetHealthEvent(meemPath:String, facetId:String) {
		this.meemPath = meemPath;
		this.facetId = facetId;
	}
}