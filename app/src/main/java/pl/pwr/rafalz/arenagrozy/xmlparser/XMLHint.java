package pl.pwr.rafalz.arenagrozy.xmlparser;

public class XMLHint extends XMLData {
	private int hintId;

	
	public XMLHint() {
		super();
	}

	public XMLHint(int hintId) {
		super();
		this.hintId = hintId;
	}

	@Override
	public String toString() {
		return "Hint id: "+ hintId;
	}
	
	public int getHintId() {
		return hintId;
	}

	public void setHintId(int hintId) {
		this.hintId = hintId;
	}
	
}
