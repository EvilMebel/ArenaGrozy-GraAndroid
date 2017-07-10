package pl.pwr.rafalz.arenagrozy;

import java.util.List;

import pl.pwr.rafalz.arenagrozy.adapters.StatsListAdapter;
import pl.pwr.rafalz.arenagrozy.adapters.StatsSkillFrame;
import pl.pwr.rafalz.arenagrozy.data.DataBase;
import pl.pwr.rafalz.arenagrozy.data.HeroDB;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class StatsActivity extends Activity {

	private ListView list;
	private ImageButton hero1b;
	private ImageButton hero2b;
	private ImageButton hero3b;
	private TextView stats_exp;
	List<StatsSkillFrame> skills_hero1 = null;
	List<StatsSkillFrame> skills_hero2 = null;
	List<StatsSkillFrame> skills_hero3 = null;
	private static String stats_exp_string = null;
	private int h1_exp;
	private int h2_exp;
	private int h3_exp;

	private int whichHero = 1;

	int scrollYstate = 0;
	int top;
	private boolean expectedPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_activity);

		list = (ListView) findViewById(R.id.stats_skills);
		hero1b = (ImageButton) findViewById(R.id.stats_hero1);
		hero2b = (ImageButton) findViewById(R.id.stats_hero2);
		hero3b = (ImageButton) findViewById(R.id.stats_hero3);
		stats_exp = (TextView) findViewById(R.id.stats_exp);

		stats_exp_string = getResources().getString(R.string.stats_exp);

		loadHeroesLevels();
		hero1b.performClick();// quick prepare style in layout
		list.setAdapter(new StatsListAdapter(this, skills_hero1, 1));

		// hide test buttons
		if (!Toolbox.TEST_MODE) {
			findViewById(R.id.addexp).setVisibility(View.GONE);
			findViewById(R.id.resskill).setVisibility(View.GONE);
		}

	}

	/**
	 * load heroes skills
	 */
	private void loadHeroesLevels() {
		skills_hero1 = HeroDB.getHero1Levels(this);
		skills_hero2 = HeroDB.getHero2Levels(this);
		skills_hero3 = HeroDB.getHero3Levels(this);

		h1_exp = DataBase.getHeroExp(1);
		h2_exp = DataBase.getHeroExp(2);
		h3_exp = DataBase.getHeroExp(3);
	}

	/**
	 * show hero 1 skills
	 * 
	 * @param view
	 */
	public void openHero1(View view) {
		playButtonSound();
		whichHero = 1;
		skills_hero1 = HeroDB.getHero1Levels(this);
		list.setAdapter(new StatsListAdapter(this, skills_hero1, 1));
		hero1b.setBackgroundColor(Color.WHITE);
		hero2b.setBackgroundColor(Color.TRANSPARENT);
		hero3b.setBackgroundColor(Color.TRANSPARENT);

		h1_exp = DataBase.getHeroExp(1);
		stats_exp.setText(stats_exp_string + " " + h1_exp);
	}

	/**
	 * show hero 2 skills
	 * 
	 * @param view
	 */
	public void openHero2(View view) {
		playButtonSound();
		whichHero = 2;
		skills_hero2 = HeroDB.getHero2Levels(this);
		list.setAdapter(new StatsListAdapter(this, skills_hero2, 2));
		hero1b.setBackgroundColor(Color.TRANSPARENT);
		hero2b.setBackgroundColor(Color.WHITE);
		hero3b.setBackgroundColor(Color.TRANSPARENT);

		h2_exp = DataBase.getHeroExp(2);
		stats_exp.setText(stats_exp_string + " " + h2_exp);
	}

	/**
	 * show hero 3 skills
	 * 
	 * @param view
	 */
	public void openHero3(View view) {
		playButtonSound();
		whichHero = 3;
		skills_hero3 = HeroDB.getHero3Levels(this);
		list.setAdapter(new StatsListAdapter(this, skills_hero3, 3));
		hero1b.setBackgroundColor(Color.TRANSPARENT);
		hero2b.setBackgroundColor(Color.TRANSPARENT);
		hero3b.setBackgroundColor(Color.WHITE);

		h3_exp = DataBase.getHeroExp(3);
		stats_exp.setText(stats_exp_string + " " + h3_exp);
	}

	/**
	 * test button - add exp for current hero
	 * 
	 * @param view
	 */
	public void addExp10(View view) {
		playButtonSound();
		saveScrollPosition();
		int exp = DataBase.getHeroExp(whichHero);
		DataBase.setHeroExp(whichHero, exp + 100);

		switch (whichHero) {
		case 1:
			hero1b.performClick();
			break;
		case 2:
			hero2b.performClick();
			break;
		case 3:
			hero3b.performClick();
			break;
		}

		resumeScrollPosition();
		// finish();
	}

	/**
	 * test button - reset skills for all heroes
	 * 
	 * @param view
	 */
	public void resetSkills(View view) {
		playButtonSound();
		DataBase.resetSkills();
		finish();
		expectedPause = true;
	}

	/**
	 * refresh lists of skills for current hero
	 * 
	 * @param whichHero
	 */
	public void updateExp(int whichHero) {
		switch (whichHero) {
		case 1:
			hero1b.performClick();
			break;
		case 2:
			hero2b.performClick();
			break;
		case 3:
			hero3b.performClick();
			break;
		}
	}

	/**
	 * save position on list
	 */
	public void saveScrollPosition() {
		scrollYstate = list.getFirstVisiblePosition();
		View v = list.getChildAt(0);
		top = (v == null) ? 0 : v.getTop();
	}

	/**
	 * resume position on list after refreshing data
	 */
	public void resumeScrollPosition() {
		list.setSelectionFromTop(scrollYstate, top);
	}

	/**
	 * play music if opt is on
	 */
	@Override
	protected void onResume() {
		super.onResume();
		DataBase.initSharedPreferences(this);
		expectedPause = false; // reset settings
		MenuMusic.playMusic();
	}

	/**
	 * pause music if opt is on
	 */
	@Override
	protected void onPause() {
		super.onPause();

		if (!expectedPause)
			MenuMusic.pauseMusic();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		playButtonSound();
		expectedPause = true;
	}

	/**
	 * play button special effect
	 */
	private void playButtonSound() {
		MenuMusic.playButtonSound(this, R.raw.spell4);
	}
}
