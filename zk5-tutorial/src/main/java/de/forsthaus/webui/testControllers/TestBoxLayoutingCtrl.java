package de.forsthaus.webui.testControllers;

import java.util.List;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class TestBoxLayoutingCtrl extends GenericForwardComposer {

	private static final long serialVersionUID = -3639146891743901462L;

	private Listbox alignValue;
	private Listbox packValue;
	private Listbox vflexValue;
	private Listbox hflexValue;

	private Vbox vbox;
	private Hbox hbox;

	public void onAssignLayout() {
		assignValue(hbox);
		assignValue(vbox);
	}

	@SuppressWarnings("unchecked")
	public void assignValue(Box box) {
		box.setAlign(String.valueOf(alignValue.getSelectedItem().getValue()));
		box.setPack(String.valueOf(packValue.getSelectedItem().getValue()));
		String hflexString = String.valueOf(hflexValue.getSelectedItem().getValue());
		String vflexString = String.valueOf(vflexValue.getSelectedItem().getValue());
		List<HtmlBasedComponent> children = box.getChildren();
		for (HtmlBasedComponent child : children) {
			child.setHflex(hflexString);
			child.setVflex(vflexString);
		}
	}

	private void addButtonChild(Box box) {
		int nextCount = box.getChildren().size();
		Button child = new Button("Button" + nextCount);
		box.appendChild(child);
		box.invalidate();
	}

	private void addBoxChild(Box box) {
		int nextCount = box.getChildren().size();
		Box child = new Box();
		child.setStyle("border: 1px solid blue");
		child.appendChild(new Label("Box-" + nextCount));
		box.appendChild(child);
		box.invalidate();
	}

	private void addLabelChild(Box box) {
		int nextCount = box.getChildren().size();
		Label child = new Label("Label-" + nextCount);
		child.setStyle("border: 1px solid blue");
		box.appendChild(child);
		box.invalidate();
	}

	private void addTextboxChild(Box box) {
		int nextCount = box.getChildren().size();
		Textbox child = new Textbox("Textbox-" + nextCount);
		box.appendChild(child);
		box.invalidate();
	}

	private void addWindowChild(Box box) {
		int nextCount = box.getChildren().size();
		Window child = new Window();
		child.setBorder("normal");
		child.appendChild(new Label("Window-" + nextCount));
		box.appendChild(child);
		box.invalidate();
	}

	public void onClearChildren() {
		hbox.getChildren().clear();
		vbox.getChildren().clear();
	}

	public void onAddButtonChild() {
		addButtonChild(hbox);
		addButtonChild(vbox);
	}

	public void onAddBoxChild() {
		addBoxChild(hbox);
		addBoxChild(vbox);
	}

	public void onAddWindowChild() {
		addWindowChild(hbox);
		addWindowChild(vbox);
	}

	public void onAddLabelChild() {
		addLabelChild(hbox);
		addLabelChild(vbox);
	}

	public void onAddTextboxChild() {
		addTextboxChild(hbox);
		addTextboxChild(vbox);
	}

	@SuppressWarnings("unchecked")
	private void set100PercentWidth(Box box) {
		List<HtmlBasedComponent> children = box.getChildren();
		for (HtmlBasedComponent child : children) {
			child.setWidth("100%");
		}
	}

	@SuppressWarnings("unchecked")
	private void set100PercentHeight(Box box) {
		List<HtmlBasedComponent> children = box.getChildren();
		for (HtmlBasedComponent child : children) {
			child.setHeight("100%");
		}
	}

	@SuppressWarnings("unchecked")
	private void resetWidthAndHeight(Box box) {
		List<HtmlBasedComponent> children = box.getChildren();
		for (HtmlBasedComponent child : children) {
			child.setHeight("");
			child.setWidth("");
		}
	}

	public void onResetWidthAndHeight() {
		resetWidthAndHeight(hbox);
		resetWidthAndHeight(vbox);
	}

	public void onFullPercentWidth() {
		set100PercentWidth(hbox);
		set100PercentWidth(vbox);
	}

	public void onFullPercentHeight() {
		set100PercentHeight(hbox);
		set100PercentHeight(vbox);
	}

	public void onHboxRefresh() {
		hbox.invalidate();
	}

	public void onVboxRefresh() {
		vbox.invalidate();
	}
}
