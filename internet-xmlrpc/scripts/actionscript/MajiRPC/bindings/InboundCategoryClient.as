class MajiRPC.bindings.InboundCategoryClient extends MajiRPC.InboundBinding {

	public function InboundCategoryClient() {
		super();
		FacetClass  = "net.majitek.maji.system.space.CategoryClient";
	}

	public function entriesAdded(entries:Array) : Void {
		trace ("+++ InboundCategoryClient.entriesAdded: " + entries);
		for (var i=0; i<entries.length; i++) {
			trace("got entry : " + entries[i].name + ", " + entries[i].meem);
		}
	}
	
	public function entriesRemoved(entries:Array) : Void {
		trace ("+++ InboundCategoryClient.entriesRemoved: " + entries);
	}
	
	public function entryRenamed(oldName:String, oldPath:String, newName:String, newPath:String) {
		trace ("+++ InboundCategoryClient.entryRenamed: " + newPath + " :: " + oldName + " -> " + newName);
	}

}