package pl.pwr.rafalz.arenagrozy;

import pl.pwr.rafalz.arenagrozy.adapters.PagerLevelAdapter;
import pl.pwr.rafalz.arenagrozy.data.DataBase;
import pl.pwr.rafalz.arenagrozy.tools.Toolbox;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class LevelsActivity extends FragmentActivity {
	private ViewPager pager;
	public static boolean expectedPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.levels_activity);
		// look at onresume

		// Debug mode
		if (!Toolbox.TEST_MODE) {
			// hide test buttons
			findViewById(R.id.unlocklev).setVisibility(View.GONE);
			findViewById(R.id.resetlvl).setVisibility(View.GONE);
		}
	}

	/**
	 * prepare progresses of levels
	 */
	private void refreshLevelProgress() {
		DataBase.initSharedPreferences(this);
		unlockFirstLevel();
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new PagerLevelAdapter(getSupportFragmentManager(),
				this));
	}

	private void unlockFirstLevel() {
		DataBase.unlockLevel(1);
	}

	/**
	 * onResume+ play music if optmusic is on
	 */
	@Override
	protected void onResume() {
		super.onResume();
		refreshLevelProgress();

		expectedPause = false; // reset settings
		MenuMusic.playMusic();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// avoid exception -erase level data form view
		pager.setAdapter(null);

		if (!expectedPause)
			MenuMusic.pauseMusic();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		expectedPause = true;
		MenuMusic.playButtonSound(this, R.raw.spell4);
	}

	/**
	 * debug button
	 * 
	 * @param view
	 */
	public void resetLevels(View view) {
		DataBase.resetLevels();
		refreshLevelProgress();
	}

	/**
	 * debug button
	 * 
	 * @param view
	 */
	public void unlockLevels(View view) {
		DataBase.unlockLevels();
		refreshLevelProgress();
	}
}
