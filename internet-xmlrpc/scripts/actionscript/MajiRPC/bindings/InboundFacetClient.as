class MajiRPC.bindings.InboundFacetClient extends MajiRPC.InboundBinding {

	public function InboundFacetClient() {
		super();
		FacetClass  = "net.majitek.maji.system.meem.FacetClient";
	}
	
	public function hasA(facetId:String, specification:String, direction:String) : Void {
		trace ("+++ InboundFacetClient.hasA: " + facetId + ", " + specification + ", " + direction);
	}
}