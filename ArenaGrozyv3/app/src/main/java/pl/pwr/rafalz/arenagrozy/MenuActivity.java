package pl.pwr.rafalz.arenagrozy;

import pl.pwr.rafalz.arenagrozy.data.DataBase;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class MenuActivity extends Activity {

	private boolean music;
	private boolean sound;
	private boolean lol;

	private boolean expectedPause = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_activity);

		// prepare music resource
		MenuMusic.initMusic(this);
	}

	private void refreshMusic() {
		DataBase.initSharedPreferences(this);

		music = DataBase.getOptMusic();
		sound = DataBase.getOptSound();

		refreshViewSound();
		refreshViewMusic();

		MenuMusic.playMusic();
	}

	public void openStats(View view) {
		playButtonSound();
		Intent intent = new Intent(this, StatsActivity.class);
		startActivity(intent);
		expectedPause = true;
	}

	public void openLevels(View view) {
		playButtonSound();
		Intent intent = new Intent(this, LevelsActivity.class);
		startActivity(intent);
		expectedPause = true;
	}

	public void changeSound(View view) {
		sound = !sound;
		DataBase.setOptSound(sound);
		refreshViewSound();
		playButtonSound();
	}

	public void changeMusic(View view) {
		playButtonSound();
		music = !music;
		DataBase.setOptMusic(music);
		refreshViewMusic();

		if (music)
			MenuMusic.playMusic();
		else
			MenuMusic.pauseMusic();
	}

	private void refreshViewMusic() {
		TextView tv = (TextView) findViewById(R.id.menu_music);
		if (music) {
			tv.setText(R.string.menu_music_on);
		} else {
			tv.setText(R.string.menu_music_off);
		}
	}

	private void refreshViewSound() {
		TextView tv = (TextView) findViewById(R.id.menu_sound);
		if (sound) {
			tv.setText(R.string.menu_sound_on);
		} else {
			tv.setText(R.string.menu_sound_off);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void hideNavigationBar() {
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		// | View.SYSTEM_UI_FLAG_FULLSCREEN;
		if (android.os.Build.VERSION.SDK_INT >= 11)
			decorView.setSystemUiVisibility(uiOptions);
	}

	/**
	 * if user pressed back -> show dialog
	 */
	@Override
	public void onBackPressed() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.exit_dialog);

		// dont close game
		((TextView) dialog.findViewById(R.id.dial_no))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						playButtonSound();
						dialog.dismiss();
					}
				});

		// close game
		((TextView) dialog.findViewById(R.id.dial_yes))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						playButtonSound();
						dialog.dismiss();
						finish();
					}
				});

		dialog.show();

	}

	/**
	 * play music if status is on
	 */
	@Override
	protected void onResume() {
		super.onResume();
		refreshMusic();
		expectedPause = false; // reset settings
	}

	/**
	 * pause music if status is on
	 */
	@Override
	protected void onPause() {
		super.onPause();

		if (!expectedPause)
			MenuMusic.pauseMusic();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MenuMusic.stopMusic();
	}

	/**
	 * buttons special effect
	 */
	private void playButtonSound() {
		MenuMusic.playButtonSound(this, R.raw.spell4);
	}
}
