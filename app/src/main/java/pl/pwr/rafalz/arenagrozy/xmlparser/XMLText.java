package pl.pwr.rafalz.arenagrozy.xmlparser;

import android.graphics.Color;

public class XMLText extends XMLData{
	private int textId;
	//private String text;//TODO id
	private int color = Color.BLACK;//default color for text is black
	
	
	public XMLText() {
		super();
		// TODO Auto-generated constructor stub
	}

	public XMLText(int textId, int color) {
		super();
		this.textId = textId;
		this.color = color;
	}

	@Override
	public String toString() {
		return "Text id: "+ textId + " color: " + color;// + " string: " + text;
	}
	
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}

	public int getTextId() {
		return textId;
	}

	public void setTextId(int textId) {
		this.textId = textId;
	}
	
	

}
