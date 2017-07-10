package pl.pwr.rafalz.arenagrozy.xmlparser;

import java.util.List;

/**
 * Class which contains all ifno about level -enemies, hints, texts
 * @author Zientara
 *
 */
public class XMLLevelInfo {
	private int backgroundType;
	private List<List<XMLData>> data;
	public int getBackgroundType() {
		return backgroundType;
	}
	public void setBackgroundType(int backgroundType) {
		this.backgroundType = backgroundType;
	}
	public List<List<XMLData>> getData() {
		return data;
	}
	public void setData(List<List<XMLData>> data) {
		this.data = data;
	}
}
