package org.meemplex.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="categoryEntry")
public class CategoryEntry extends Identifiable  implements Serializable {
	
	private static final long serialVersionUID = 0L;
	
	private String name;
	
	private Identifiable meem;

	public CategoryEntry() {
    }
	
	public CategoryEntry(String name, Identifiable meem) {
		this.name = name;
		this.meem = meem;
    }
	
	public void setName(String name) {
	    this.name = name;
    }

	@XmlAttribute
	public String getName() {
	    return name;
    }

	public void setMeem(Identifiable meem) {
	    this.meem = meem;
    }

	public Identifiable getMeem() {
	    return meem;
    }
}
