package org.openmaji.implementation.tool.eclipse.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.openmaji.common.VariableMap;
import org.openmaji.common.VariableMapClient;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.intermajik.model.SimplePoint;
import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.meem.wedge.reference.ContentClient;



/*
 import org.openmaji.implementation.tool.eclipse.util.WorksheetUtility;
 WorksheetUtility utility = new WorksheetUtility();
 utility.updateMeemOnWorksheet("/binary","/aa");
 */
public class WorksheetUtility {
	
	/**
	 * 
	 * @param pathToMeem
	 * @param pathToWorksheet
	 */
	public void updateMeemOnWorksheet(String pathToMeem, String pathToWorksheet) {
		ServerGateway serverGateway = ServerGateway.spi.create();

		MeemPath worksheetMeemPath = MeemPath.spi.create(Space.HYPERSPACE, pathToWorksheet);
		Meem worksheetMeem = Meem.spi.get(worksheetMeemPath);
		VariableMap variableMap = (VariableMap) ReferenceHelper.getTarget(worksheetMeem, "variableMap", VariableMap.class);

		VariableMapClient variableMapClient = new VariableMapUpdater(pathToMeem, variableMap);
		Facet proxy = serverGateway.getTargetFor(variableMapClient, VariableMapClient.class);
		Reference reference = Reference.spi.create("variableMapClient", proxy, true, null);
		worksheetMeem.addOutboundReference(reference, true);
	}

	/**
	 * 
	 */
	private class VariableMapUpdater implements VariableMapClient, ContentClient {
		private final String pathToMeem;

		private final VariableMap variableMap;

		private Entry<Serializable, Serializable> entry;

		public VariableMapUpdater(String pathToMeem, VariableMap variableMap) {
			this.pathToMeem = pathToMeem;
			this.variableMap = variableMap;
		}

		public void changed(Entry<Serializable, Serializable>[] entries) {
			for (int i = 0; i < entries.length; i++) {
				Entry<Serializable, Serializable> entry = entries[i];
				Object object = entry.getKey();
				ElementPath elementPath = (ElementPath) object;
				String key = (String) elementPath.toString();
				if (key.endsWith(pathToMeem)) {
					this.entry = entry;
					return;
				}
			}
			System.err.println("Did not find expected Meem on worksheet");
		}

		public void removed(Serializable key) {
		}

		public void contentSent() {
			if (entry == null) {
				System.err.println("entry not set");
				return;
			}

			ValueBag valueBag = (ValueBag) entry.getValue();
			Iterator iterator = valueBag.getIds();
			while (iterator.hasNext()) {
				String id = (String) iterator.next();
				//Object object = valueBag.get(id);
				/*
				 * "view mode" = ViewMode. id = interface org.openmajik.implementation.intermajik.model.ViewModeConstants.iconic
				 *                         name = "Iconic View"
				 * "collapsed" = Boolean
				 * "show system wedges" = Boolean
				 * "size" = SimpleDimension
				 * "child orders" = Object[]
				 */
				if ("location".equals(id)) {
					SimplePoint simplePoint = (SimplePoint) valueBag.get(id);
					simplePoint.x = 200;
					simplePoint.y = 200;
					variableMap.update(entry.getKey(), valueBag);
				}
			}
		}

		public void contentFailed(String reason) {
			System.err.println("Content failed: " + reason);
		}
	}

}
