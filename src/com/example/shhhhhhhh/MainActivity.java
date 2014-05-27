package com.example.shhhhhhhh;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	SoundMeter meter = new SoundMeter();
	MediaPlayer mp1;
	MediaPlayer mp2;
	MediaPlayer mp3;
	MediaPlayer mp4;
	MediaPlayer mp5;

	int count = 0;

	Timer timer;
	Timer barTimer;
	double amplitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button buttonOne = (Button) findViewById(R.id.button1);
		final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		final SeekBar seek = (SeekBar) findViewById(R.id.seekBar1);
		bar.getProgressDrawable().setColorFilter(Color.BLUE, Mode.SRC_IN);
		meter.start();
		timer = new Timer();
		barTimer = new Timer();
		barTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				amplitude = meter.getAmplitude();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int amp = (int) ((amplitude / 32767) * 100);
						bar.setProgress(amp);

					}
				});
			}

		}, 0, 1000);

		mp1 = MediaPlayer.create(this, R.raw.shh1);
		mp2 = MediaPlayer.create(this, R.raw.shh2);
		mp3 = MediaPlayer.create(this, R.raw.shh3);
		mp4 = MediaPlayer.create(this, R.raw.shh4);
		mp5 = MediaPlayer.create(this, R.raw.shh5);

		buttonOne.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				timer.cancel();
				timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						amplitude = meter.getAmplitude();
						int progress = seek.getProgress();
						double max = progress/100.0 * 32767;
						if (amplitude > max) {
							switch (count) {
							case 0:
								mp1.start();
								count++;
								break;
							case 1:
								mp2.start();
								count++;
								break;
							case 2:
								mp3.start();
								count++;
								break;
							case 3:
								mp4.start();
								count++;
								break;
							case 4:
								mp5.start();
								count = 0;
							}
							try {
							    Thread.sleep(10000);
							} catch(InterruptedException ex) {
							    Thread.currentThread().interrupt();
							}
						}
					}

				}, 0, 1000);
			}
		});
		Button buttonTwo = (Button) findViewById(R.id.button2);
		meter.start();
		buttonTwo.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				bar.setProgress(0);
				timer.cancel();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class SoundMeter {

		private MediaRecorder mRecorder = null;

		public void start() {
			if (mRecorder == null) {
				mRecorder = new MediaRecorder();
				mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mRecorder.setOutputFile("/dev/null");
				try {
					mRecorder.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mRecorder.start();
			}
		}

		public void stop() {
			if (mRecorder != null) {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}
		}

		public double getAmplitude() {
			if (mRecorder != null)
				return mRecorder.getMaxAmplitude();
			else
				return 0;

		}
	}

}
