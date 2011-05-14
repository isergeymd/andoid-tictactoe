package com.safonov.TicaTacToe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class TicTacToe extends Activity implements OnClickListener {

	private View continueButton;
	private View startButton;
	private View exitButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		continueButton = findViewById(R.id.continueButton);
		continueButton.setOnClickListener(this);

		boolean exist = isPreviousGameExists();
		// should we display continue button
		if (exist == false) {
			continueButton.setVisibility(4);
		}
		startButton = findViewById(R.id.startButton);
		startButton.setOnClickListener(this);

		exitButton = findViewById(R.id.exitButton);
		exitButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean exist = isPreviousGameExists();
		if (exist == true) {
			continueButton.setVisibility(0);
		} else {
			continueButton.setVisibility(4);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continueButton:
			startGame(Game.CONTINUE);
			break;

		case R.id.startButton:
			newGameDialog();
			break;
		case R.id.exitButton:
			finish();
			break;
		}
	}

	private void newGameDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.whosFirstLabel)
				.setItems(R.array.whosFirst,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								startGame(i);
							}
						}).show();
	}
	/** @param i - 0 starts AI first, 1 starts User*/
	private void startGame(int i) {
		Intent intent = new Intent(TicTacToe.this, Game.class);
		intent.putExtra(Game.PLAYER, i);
		startActivity(intent);
	}

	/** @return true if previous game exist, false otherwise */
	private boolean isPreviousGameExists() {
		String def = null;
		SharedPreferences preferences = getSharedPreferences(Game.PREF_GAME, 0);
		String existPrevious = preferences.getString(Game.PREF_NAME, def);

		if (existPrevious == null)
			return false;
		else
			return true;
	}
}
