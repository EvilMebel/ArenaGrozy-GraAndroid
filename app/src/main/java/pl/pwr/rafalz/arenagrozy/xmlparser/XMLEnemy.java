package pl.pwr.rafalz.arenagrozy.xmlparser;

public class XMLEnemy extends XMLData{
	private int enemyId;
	private float level;
	
	
	public XMLEnemy() {
		super();
	}

	public XMLEnemy(int enemyId, float level) {
		super();
		this.enemyId = enemyId;
		this.level = level;
	}

	@Override
	public String toString() {
		return "Enemy id: "+ enemyId + " level: " + level;
	}
	
	public int getEnemyId() {
		return enemyId;
	}
	public void setEnemyId(int enemyId) {
		this.enemyId = enemyId;
	}
	public float getLevel() {
		return level;
	}
	public void setLevel(float level) {
		this.level = level;
	}

}
