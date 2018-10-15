package pl.pwr.rafalz.arenagrozy.adapters;

import pl.pwr.rafalz.arenagrozy.data.HeroDB;

public class StatsSkillFrame {
	private int nameId;
	private int helpId;
	private int current_level;
	private int max_level;
	private int[] cost;

	public StatsSkillFrame(int nameID, int helpId, int current_level) {
		super();
		this.nameId = nameID;
		this.helpId = helpId;
		this.current_level = current_level;
		this.cost = HeroDB.getCosts(nameID);
		max_level = cost.length;
	}
	
	
	/**
	 * get cost of next level. If skill have max level void will return -1
	 * @return
	 */
	public int getCostForNextLevel() {
		int cost;
		int c_lvl = getCurrent_level();
		int max_lvl = getMax_level();
		
		//avoid outOfBoundsException
		if(c_lvl < max_lvl) {
			cost = getCost()[c_lvl];
		} else {
			cost = -1;
		}
		
		return cost;
	}

	public int getNameId() {
		return nameId;
	}

	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

	public int getHelpId() {
		return helpId;
	}

	public void setHelpId(int helpId) {
		this.helpId = helpId;
	}

	public int getCurrent_level() {
		return current_level;
	}

	public void setCurrent_level(int current_level) {
		this.current_level = current_level;
	}

	public int getMax_level() {
		return max_level;
	}

	public void setMax_level(int max_level) {
		this.max_level = max_level;
	}

	public int[] getCost() {
		return cost;
	}

	public void setCost(int[] cost) {
		this.cost = cost;
	}
	
	
	

	

}
