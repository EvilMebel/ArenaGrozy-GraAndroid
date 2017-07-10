package pl.pwr.rafalz.arenagrozy;

import pl.pwr.rafalz.arenagrozy.data.DataBase;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * class for background music and sound effects in all menu
 * 
 * @author Evil
 * 
 */
public class MenuMusic {

	public static MediaPlayer mediaPlayer;
	public static MediaPlayer effectPlayer;

	public static void initMusic(Context context) {
		mediaPlayer = MediaPlayer.create(context, R.raw.music_menu_puzzle);
		mediaPlayer.setLooping(true);
	}

	public static void pauseMusic() {
		if (mediaPlayer != null && mediaPlayer.isPlaying())
			mediaPlayer.pause();
	}

	public static void stopMusic() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();

			mediaPlayer.release();
		}
	}

	public static void playMusic() {
		if (DataBase.getOptMusic() && mediaPlayer != null)
			mediaPlayer.start();
	}

	public static void playButtonSound(Context context, int resId) {
		effectPlayer = MediaPlayer.create(context, resId);
		if (DataBase.getOptSound()) {
			// remember to free memory on the end of sound
			effectPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mp.isPlaying())
						mp.stop();
					mp.release();
				}
			});
			effectPlayer.start();
		}
	}
}
