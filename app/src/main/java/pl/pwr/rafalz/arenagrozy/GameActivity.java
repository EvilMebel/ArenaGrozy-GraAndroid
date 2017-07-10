package pl.pwr.rafalz.arenagrozy;

import java.lang.ref.WeakReference;

import pl.pwr.rafalz.arenagrozy.data.DataBase;
import pl.pwr.rafalz.arenagrozy.view.GameView;
import pl.pwr.rafalz.arenagrozy.xmlparser.LevelReader;
import pl.pwr.rafalz.arenagrozy.xmlparser.XMLLevelInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class GameActivity extends Activity {
	private String tag = getClass().getName();

	private GameView game;
	public static WeakReference<GameActivity> act;
	private int level;

	public static boolean expectedPause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_activity);

		act = new WeakReference<GameActivity>(this);
		// get information about level for game
		Intent intent = getIntent();
		Bundle info = intent.getExtras();
		level = info.getInt("LEVEL");

		game = new GameView(getApplicationContext());
		Acceleration();
		new LoadingAsyncTask().execute();

	}

	/*
	 * turn on hardware acceleration
	 */
	@SuppressLint("NewApi")
	private void Acceleration() {
		game.setLayerType(View.LAYER_TYPE_HARDWARE, null);
	}

	private void resetGame(int level) {
		setContentView(R.layout.loading_activity);

		game.freeMemory();
		this.level = level;
		game = new GameView(getApplicationContext());
		new LoadingAsyncTask().execute();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.d(tag, "OMG LOW MEMORY!");
		game.onLowMemory();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("Glowne Okno", "onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("Glowne Okno", "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("Glowne Okno", "onResume");

		game.resumeMusic();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("Glowne Okno", "onPause");
		game.pauseMusic();
		game.pause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("Glowne Okno", "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		game.freeMemory();
		Log.d("Glowne Okno", "onDestroy");
	}

	/*
	 * async task for loading level data
	 */
	class LoadingAsyncTask extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			game = new GameView(getApplicationContext());
		}

		/**
		 * loading level data
		 * */
		protected String doInBackground(String... args) {
			XMLLevelInfo levelInfo = LevelReader.open(level, act.get());
			game.loadData(levelInfo, level);
			return null;
		}

		/**
		 * on post show game
		 */
		protected void onPostExecute(String file_url) {
			setContentView(game);
		}

	}

	/**
	 * prepare dialog view by wanted layoutId resource
	 * 
	 * @param layoutId
	 * @return
	 */
	private Dialog prepareDialog(int layoutId) {
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(layoutId);
		return dialog;
	}

	/**
	 * show dialog if user pressed back button. Prevent losing game data!
	 */
	@Override
	public void onBackPressed() {
		game.pause();
		final Dialog dialog = prepareDialog(R.layout.exit_dialog);
		((TextView) dialog.findViewById(R.id.text))
				.setText(R.string.game_dial_exit);

		// dont close game
		(dialog.findViewById(R.id.dial_no))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		// close game
		(dialog.findViewById(R.id.dial_yes))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});
		dialog.show();
	}

	/**
	 * dialog when all heroes are dead
	 */
	public void showLoseDialog() {
		game.pause();
		final Dialog dialog = prepareDialog(R.layout.lose_dialog);
		dialog.setCancelable(false);

		// dont close game
		(dialog.findViewById(R.id.dial_reset))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						resetGame(level);
					}
				});

		// close game
		(dialog.findViewById(R.id.dial_levels))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});
		dialog.show();
	}

	/**
	 * dialog when level is completed. At least one hero is alive
	 */
	public void showWinDialog() {
		final Dialog dialog = prepareDialog(R.layout.win_dialog);
		dialog.setCancelable(false);

		// dont close game
		(dialog.getWindow().findViewById(R.id.dial_reset))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						resetGame(level);
					}
				});

		// close game
		(dialog.findViewById(R.id.dial_levels))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});

		// next level - if any exists
		TextView t = (TextView) dialog.findViewById(R.id.dial_next_level);
		if (level + 1 > DataBase.LEVELS_COUNT) {
			t.setVisibility(View.GONE);
		} else {
			t.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					resetGame(level + 1);
				}
			});
		}
		dialog.show();
	}

	/**
	 * show hint dialog specified by level data
	 * 
	 * @param strId
	 */
	public void showHintDialog(int strId) {
		game.pause();
		final Dialog dialog = prepareDialog(R.layout.hint_dialog);
		dialog.setCancelable(false);

		// hint text
		((TextView) dialog.findViewById(R.id.text)).setText(strId);

		// ok button
		(dialog.findViewById(R.id.dial_yes))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						game.resume();
					}
				});

		dialog.show();
	}
}
