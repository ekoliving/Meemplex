import MajiRPC.InboundBinding;
import MajiUI.MajiForm;
import MajiUI.FormFacetClient;

/**
 *
 */
class MajiUI.FormCategoryClient extends InboundBinding {
	private var _form:MajiForm;
	
	private var _items;
	
	public function FormCategoryClient(form:MajiForm) {
		super();
		this._form  = form;
		this._items = {};
		super.FacetId     = "categoryClient";
		super.FacetClass  = "net.majitek.maji.system.space.CategoryClient";
	}

	public function entriesAdded(entries:Array) : Void {
		trace ("+++ FormCategoryClient.entriesAdded: " + entries);
		for (var i=0; i<entries.length; i++) {
			trace("got entry : " + entries[i].name + ", " + entries[i].meem);

			var item:FormFacetClient = new FormFacetClient(_form, entries[i].name, entries[i].meem);
			item.ClientGateway = ClientGateway;
			_items[entries[i].name] = item;
		}
	}

	public function entriesRemoved(entries:Array) : Void {
		trace ("+++ FormCategoryClient.entriesRemoved: " + entries);
		for (var i=0; i<entries.length; i++) {
			trace("removed entry : " + entries[i].name + ", " + entries[i].meem);

			var item = _items[entries[i].name];
			if (item != undefined) {
				item.unbind();
				_items[entries[i].name] = undefined;
			}
		}
	}
	
	public function entryRenamed(oldName:String, oldPath:String, newName:String, newPath:String) : Void {
		trace ("+++ FormCategoryClient.entryRenamed: " + newPath + " :: " + oldName + " -> " + newName);
		
		// TODO check if item has the right MeemPath
		var item = _items[oldName];
		if (item != undefined) {
			item.unbind();
			_items[oldName] = undefined;
		}
		
		// create new item
		item = new FormFacetClient(_form, newName, newPath);
		item.ClientGateway = ClientGateway;

		_items[newName] = item;
	}
}