package pl.pwr.rafalz.arenagrozy.adapters;

public class LevelsFrame {
	private int stars;
	private boolean locked;
	private int levelNumber;
	
	
	/**
	 * create level item. If stars == -1 level will be locked
	 * @param stars
	 * @param filename
	 */
	public LevelsFrame(int stars, int filename) {
		super();
		if(stars == -1)
		{
			locked = true;
			this.stars = 0;
		} else {
			this.stars = stars;
		}
		this.stars = stars;
		this.levelNumber = filename;
	}


	public int getStars() {
		return stars;
	}


	public void setStars(int stars) {
		this.stars = stars;
	}


	public boolean isLocked() {
		return locked;
	}


	public void setLocked(boolean locked) {
		this.locked = locked;
	}


	public int getLevelNumber() {
		return levelNumber;
	}


	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}
}
