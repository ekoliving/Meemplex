package org.meemplex.internet.gwt.client.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.meemplex.internet.gwt.client.event.Filtered;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class FilterSelector extends Composite  {

	private static FilterSelectorUiBinder uiBinder = GWT.create(FilterSelectorUiBinder.class);

	interface FilterSelectorUiBinder extends UiBinder<Widget, FilterSelector> {
	}

	
	//@UiField ToggleButton buttonAll;
	@UiField ToggleButton buttonLights;
	@UiField ToggleButton buttonBlinds;
	@UiField ToggleButton buttonLocks;
	
	@UiField ToggleButton buttonHouse;
	@UiField ToggleButton buttonBed1;
	@UiField ToggleButton buttonBed2;
	@UiField ToggleButton buttonBed3;

	private List<ToggleButton> buttons = new ArrayList<ToggleButton>();
	
	private Set<Filtered> filterHandlers = new HashSet<Filtered>();
	
	public FilterSelector() {
		initWidget(uiBinder.createAndBindUi(this));
		buttons.add(buttonLights);
		buttons.add(buttonBlinds);
		buttons.add(buttonLocks);
		buttons.add(buttonHouse);
		buttons.add(buttonBed1);
		buttons.add(buttonBed2);
		buttons.add(buttonBed3);
	}

	public void addFiltered(Filtered handler) {
		filterHandlers.add(handler);
	}
	
	public void removeFiltered(Filtered handler) {
		filterHandlers.remove(handler);
	}
	
	
	private void sendFilter(String selector) {
		for (Filtered f : filterHandlers) {
			f.filter(selector);
		}
	}
	
	private String functionFilter = "*";
	private String locationFilter = "*";
	
	
	private void filter() {
		if (functionFilter == null) {
			if (locationFilter == null) {
				sendFilter("*");
			}
			else {
				sendFilter(locationFilter);
			}
		}
		else {
			if (locationFilter == null) {
				sendFilter(functionFilter);
			}
			else {
				sendFilter(functionFilter + locationFilter);
			}
		}
	}
	
	private void uncheckOtherButtons(ToggleButton keep) {
		for (ToggleButton button : buttons) {
			if ( !keep.equals(button) ) {
				button.setValue(false);
			}
		}
	}

//	@UiHandler("buttonAll")
//	void handleAllClick(ClickEvent event) {
//		functionFilter = null;
//		filter();
//	}
	
	@UiHandler("buttonLights")
	void handleLightsClick(ClickEvent event) {
		locationFilter = null;
		functionFilter = ".light";
		uncheckOtherButtons(buttonLights);
		filter();
	}
	
	@UiHandler("buttonBlinds")
	void handleBLindsClick(ClickEvent event) {
		locationFilter = null;
		functionFilter = ".blind";
		uncheckOtherButtons(buttonBlinds);
		filter();
	}
	
	@UiHandler("buttonLocks")
	void handleLocksClick(ClickEvent event) {
		locationFilter = null;
		functionFilter = ".lock";
		uncheckOtherButtons(buttonLocks);
		filter();
	}
	
	@UiHandler("buttonHouse")
	void handleHouseClick(ClickEvent event) {
		functionFilter = null;
		locationFilter = ".house";
		uncheckOtherButtons(buttonHouse);
		filter();
	}
	
	@UiHandler("buttonBed1")
	void handleBed1Click(ClickEvent event) {
		functionFilter = null;
		locationFilter = ".bed1";
		uncheckOtherButtons(buttonBed1);
		filter();
	}
	
	@UiHandler("buttonBed2")
	void handleBed2Click(ClickEvent event) {
		functionFilter = null;
		locationFilter = ".bed2";
		uncheckOtherButtons(buttonBed2);
		filter();
	}
	
	@UiHandler("buttonBed3")
	void handleBed3Click(ClickEvent event) {
		functionFilter = null;
		locationFilter = ".bed3";
		uncheckOtherButtons(buttonBed3);
		filter();
	}
}
