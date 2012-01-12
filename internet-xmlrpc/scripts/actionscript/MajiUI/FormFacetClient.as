import MajiRPC.InboundBinding;
import MajiUI.MajiForm;

/**
 * Listens for Facets on a Meem
 */
class MajiUI.FormFacetClient extends InboundBinding {

	private var _form:MajiForm;
	private var _entryName:String;
	
	private var _facets;

	public function FormFacetClient(form:MajiForm, name:String, meemPath:String) {
		super();
		
		_form       = form;
		_entryName  = name;
		_facets     = {};
		
		FacetId     = "facetClientFacet";
		FacetClass  = "net.majitek.maji.system.meem.FacetClient";
		MeemPath    = meemPath;
		
	}

	public function hasA(facetId:String, specification:String, direction:String) : Void {
		//trace ("+++ FormFacetClient: " + _entryName + " hasA: " + facetId + ", " + specification + ", " + direction);

		// TODO create or update a visual component that handles this "specification" and "direction"
		// TODO the component has 1 or more bindings (i.e. handle 1 or more facets)
	
		
		if (_facets[facetId] == undefined) {
			var facet = {};
			facet.facetId    = facetId;
			facet.facetClass = specification;
			facet.direction  = direction;
			
			_facets[facetId] = facet;
			
			// TODO sometimes configure existing component instead of adding new one, for other facets 
			_form.newComponent(_entryName, facetId, specification);
/*
			if (specification == "org.openmaji.common.Binary") {
				// TODO create a MajiButton
				trace ("+++ FormFacetClient.hasA: " + _entryName + "." + facetId + ", " + specification + ", " + direction);
			}
			else if (specification == "org.openmaji.common.Linear") {
				// TODO create a MajiSlider
				trace ("+++ FormFacetClient.hasA: " + _entryName + "." + facetId + ", " + specification + ", " + direction);
				_form.newComponent(_entryName, facetId, specification);
			}
			else if (specification == "net.majitek.maji.system.space.CategoryClient") {
				// TODO create a menu item and new sub-MajiForm
				trace ("+++ FormFacetClient.hasA: " + _entryName + "." + facetId + ", " + specification + ", " + direction);
				_form.newComponent(_entryName, facetId, specification);
			}
			else {
				// unhandled Facet type
//				trace ("--- FormFacetClient.hasA: " + _entryName + "." + facetId + ", " + specification + ", " + direction);
			}
*/
		}
		else {
//			trace ("already have this facet: " + _entryName + "." + facetId);	
		}
	}

	
	public function unbind() {
		super.unbind();
		
		// TODO destroy visual component
		
		for (var facetId in _facets) {
			var facet = _facets[facetId];
			trace ("facet entry: " + facet);
			trace ("destroying facet entry: " + _entryName + ", " + facet.facetId);
			_form.destroyComponent(_entryName, facet.facetId);
		}
	}	
}