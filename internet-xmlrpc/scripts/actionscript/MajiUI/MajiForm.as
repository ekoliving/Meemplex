import mx.screens.Form;
import MajiUI.FormCategoryClient;
import MajiRPC.RPCClient;

/*
 *
 */
class MajiUI.MajiForm  extends Form {

	private var _childForms:Object;	// name mapped list

	private var _childControls:Object;
	
	private var _layout:MajiUI.SimpleLayout;   // layout manager
/*	
	private _rpcClient:MajiRPC.RPCClient;
	
	private _meemPath:String;				// meemPath for the category
	private _facetId:String    = "categoryClient";
	private _facetClass:String = "net.majitek.maji.system.space.CategoryClient";
*/	
	private var _formCategoryClient:FormCategoryClient;
	
	/**
	 * Constructor
	 */
	public function MajiForm() {
		_layout = new MajiUI.SimpleLayout(this);
		_formCategoryClient = new FormCategoryClient(this);	
		_childForms = {};
		_childControls = {};
	}
	
	public function set ClientGateway(gateway : RPCClient) {
		//_rpcClient = client;
		_formCategoryClient.ClientGateway = gateway;
	}
	
	public function set MeemPath(path : String) {
		//_meemPath = path;
		_formCategoryClient.MeemPath = path;
	}
	
	
	public function newComponent(entryName:String, facetId:String, specification:String) {
//		trace("creating new button: " + entryName + "." + facetId);

		var instanceName = entryName + "." + facetId
		if (this[instanceName]) {
			// already have this entry
			return;
		}
		
		// TODO create different vidual objects for different bindings
		if (specification == "org.openmaji.common.Binary") {
			// TODO create a MajiButton
			trace ("+++ creating a button for " + instanceName);

			var button = attachMovie("MajiButton", instanceName, this.getNextHighestDepth());
	
			button.label = entryName;
	
			// TODO configure button
			
			/*
			button.onPress = function() {
				this.startDrag(this, false, 0, 0, 800, 600);
			}
			var f = function() { this.stopDrag(); };
			button.onRelease = f;
			button.onReleaseOutside = f;
			button.onDragOut = f;
			*/
	//		trace ("form component = " + button);
	//		trace ("form component " + instanceName + " = " + this[instanceName]);
			
			_layout.addComponent(instanceName, button);
			layout();
		}
		else if (specification == "org.openmaji.common.Linear") {
			trace ("+++ creating a slider for " + instanceName);
			
			var button = attachMovie("MajiSlider", instanceName, this.getNextHighestDepth());
			button.label = entryName;

			// TODO configure the MajiSlider
			
			_layout.addComponent(instanceName, button);
			layout();
		}
		else if (specification == "net.majitek.maji.system.space.CategoryClient") {
			trace ("+++ creating a form and menu item for " + instanceName);
			// TODO create a menu item and new sub-MajiForm
		}
		else {
			// unhandled Facet type
//				trace ("--- FormFacetClient.hasA: " + _entryName + "." + facetId + ", " + specification + ", " + direction);
		}

	}
	
	public function destroyComponent(entryName:String, facetId:String) {
		var instanceName = entryName + "." + facetId
		
		if (this[instanceName]) {
			destroyObject(instanceName);
		}
		_layout.removeComponent(instanceName);
		layout();
	}

	public function layout() {
		_layout.layout();
	}

}