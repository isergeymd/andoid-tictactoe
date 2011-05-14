package com.safonov.TicaTacToe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

public class Game extends Activity {

	public static final String PLAYER = "com.andoid.TicaTacToe.Game.PLAYER";
	public static final String PREF_GAME = "tictactoe";
	public static final String PREF_NAME = "myPrefs";

	public static final int EMPTY = 0;
	public static final int CROSS = 1; // cross
	public static final int CIRCLE = 2; // circle

	protected static final int CONTINUE = -1;
	private int grid[] = new int[3 * 3];
	private int player;
	private int oponent;
	private int curMove;

	private GameView gameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setGrid();
		// who will be first
		int temp = getIntent().getIntExtra(PLAYER, CIRCLE);

		// if continue game
		if (temp == -1) {
			getSavedState();
			if (curMove == oponent) {
				computerMove();
			}
			// removing old preferences
			SharedPreferences preferences = getSharedPreferences(PREF_GAME, 0);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.commit();

		}
		// normal mode
		else {
			temp += 1;
			if (temp == 1) {
				oponent = CROSS;
				player = CIRCLE;
			} else {
				player = CROSS;
				oponent = CIRCLE;
			}
			// if AI first
			if (oponent == 1) {
				computerMove();
			}
		}
		gameView = new GameView(this);
		setContentView(gameView);
		gameView.requestFocus();

		// If the activity is restarted, do a continue next time
		getIntent().putExtra(PLAYER, CONTINUE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Save the current GRID if not full
		if (checkGameFinished() == 0) {
			SharedPreferences settings = getSharedPreferences(PREF_GAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(PREF_NAME, setSavedState());
			editor.commit();
		}
	}

	/** set data from previous unfinished game */
	private void getSavedState() {
		String puz;
		String def = "000" + "000" + "000" + "121";
		SharedPreferences settings = getSharedPreferences(PREF_GAME, 0);
		puz = settings.getString(PREF_NAME, def);

		for (int i = 0; i < puz.length(); i++) {
			if (i == puz.length() - 1)
				curMove = puz.charAt(i) - '0';
			if (i == puz.length() - 2)
				oponent = puz.charAt(i) - '0';
			if (i == puz.length() - 3)
				player = puz.charAt(i) - '0';
			if (i < puz.length() - 3)
				grid[i] = puz.charAt(i) - '0';
		}
	}

	/** Convert an array into a grid string */
	private String setSavedState() {
		StringBuilder buf = new StringBuilder();
		for (int element : grid) {
			buf.append(element);
		}
		buf.append(player);
		Log.v("before save", "t" + player);
		buf.append(oponent);
		Log.v("before save", "t" + oponent);
		buf.append(curMove);
		Log.v("before save", "t" + curMove);
		return buf.toString();
	}

	/** set grid in 0 values, beginning state */
	public void setGrid() {
		for (int i = 0; i < grid.length; i++) {
			grid[i] = 0;
		}
	}

	/**
	 * get value from grid on specified position
	 * 
	 * @param x
	 *            - column
	 * @param y
	 *            - string
	 * @return 0 if values is wrong
	 */
	public int getGridPosition(int x, int y) {
		if (y * 3 + x < grid.length)
			return grid[y * 3 + x];
		else
			return 0;
	}

	/**
	 * set value from grid to specified value
	 * 
	 * @param x
	 *            - column
	 * @param y
	 *            - string
	 * @return true if is succeeded, and false otherwise
	 */
	public boolean setGridPosition(int x, int y, int value) {
		if (y * 3 + x < grid.length)
			if (grid[y * 3 + x] == 0) {
				grid[y * 3 + x] = value;
				return true;
			}
		return false;
	}

	public int getPlayer() {
		return player;
	}

	public void setCurMove(int mov) {
		curMove = mov;
	}

	public int getOponent() {
		return oponent;
	}

	/**
	 *@return 0 - game continuing 1 - won cross, 2 - won circle, 3 - drawn game
	 */
	public int checkGameFinished() {
		boolean full = true;
		int name = 0;

		// check rows
		for (int j = 0, k = 0; j < 3; j++, k += 3) {
			if (grid[k] != EMPTY && grid[k] == grid[k + 1]
					&& grid[k] == grid[k + 2]) {
				name = grid[k];
				return name;
			}
			if (full && (grid[k] == 0 || grid[k + 1] == 0 || grid[k + 2] == 0)) {
				full = false;
			}
		}

		// check columns
		for (int i = 0; i < 3; i++) {
			if (grid[i] != EMPTY && grid[i] == grid[i + 3]
					&& grid[i] == grid[i + 6]) {
				name = grid[i];
				return name;
			}
		}

		// check diagonals
		if (grid[0] != EMPTY && grid[0] == grid[1 + 3]
				&& grid[0] == grid[2 + 6]) {
			name = grid[0];
			return name;
		} else if (grid[2] != EMPTY && grid[2] == grid[1 + 3]
				&& grid[2] == grid[0 + 6]) {
			name = grid[2];
			return name;
		}

		// if board is full, and no winners
		if (full) {
			name = 3;
			return name;
		}

		return name;
	}

	/** AI random move */
	public void computerMove() {
		boolean isEmpty = false;
		int selected = -1;
		do {
			if (grid[4] == 0)
				selected = 4;
			else
				selected = (int) (Math.random() * 9);
			if (grid[selected] == 0) {
				isEmpty = true; // the looping will end
			}
		} while (!isEmpty);
		Log.v("comute move", String.valueOf(selected));
		grid[selected] = oponent;
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_MENU) {
			new AlertDialog.Builder(this).setItems(R.array.optionsDialog,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							if (i == 1) {
								finish();
							}
							if (i == 0) {
								// continue game
							}

						}
					}).create().show();
		}
		return super.onKeyDown(keycode, event);
	}

}
