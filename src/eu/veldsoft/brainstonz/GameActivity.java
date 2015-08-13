package eu.veldsoft.brainstonz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {
	private String MoveLabelText = "";
	private ImageView spaces[][] = new ImageView[4][4];
	private int black[][] = new int[4][4];
	private int white[][] = new int[4][4];

	private int getPosition(View view) {
		for (int j = 0, position = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++, position++) {
				if (spaces[i][j] == view) {
					return position;
				}
			}
		}

		return -1;
	}

	private void updateViews() {
		((TextView) findViewById(R.id.move_text)).setText(MoveLabelText);

		for (int j = 0, position = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++, position++) {
				int player = EventHandler.getInstance().playerAt(position);
				switch (player) {
				case 1:
					spaces[i][j].setImageResource(black[i][j]);
					break;
				case 2:
					spaces[i][j].setImageResource(white[i][j]);
					break;
				default:
					spaces[i][j].setImageBitmap(null);
					break;
				}
			}
		}
	}

	private void reset() {
		shuffleStonz();
		for (ImageView array[] : spaces) {
			for (ImageView view : array) {
				view.setImageBitmap(null);
			}
		}
	}

	private void shuffleStonz() {
		for (int i = 0; i < black.length; i++) {
			for (int j = 0; j < black[i].length; j++) {
				switch (Util.PRNG.nextInt(5)) {
				case 0:
					black[i][j] = R.drawable.bs0;
					break;
				case 1:
					black[i][j] = R.drawable.bs1;
					break;
				case 2:
					black[i][j] = R.drawable.bs2;
					break;
				case 3:
					black[i][j] = R.drawable.bs3;
					break;
				case 4:
					black[i][j] = R.drawable.bs4;
					break;
				}
			}
		}
		for (int i = 0; i < white.length; i++) {
			for (int j = 0; j < white[i].length; j++) {
				switch (Util.PRNG.nextInt(5)) {
				case 0:
					white[i][j] = R.drawable.ws0;
					break;
				case 1:
					white[i][j] = R.drawable.ws1;
					break;
				case 2:
					white[i][j] = R.drawable.ws2;
					break;
				case 3:
					white[i][j] = R.drawable.ws3;
					break;
				case 4:
					white[i][j] = R.drawable.ws4;
					break;
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		BrainstonzAI.is = getResources().openRawResource(R.raw.tree);

		shuffleStonz();

		spaces[0][0] = (ImageView) findViewById(R.id.stone00);
		spaces[0][1] = (ImageView) findViewById(R.id.stone01);
		spaces[0][2] = (ImageView) findViewById(R.id.stone02);
		spaces[0][3] = (ImageView) findViewById(R.id.stone03);
		spaces[1][0] = (ImageView) findViewById(R.id.stone10);
		spaces[1][1] = (ImageView) findViewById(R.id.stone11);
		spaces[1][2] = (ImageView) findViewById(R.id.stone12);
		spaces[1][3] = (ImageView) findViewById(R.id.stone13);
		spaces[2][0] = (ImageView) findViewById(R.id.stone20);
		spaces[2][1] = (ImageView) findViewById(R.id.stone21);
		spaces[2][2] = (ImageView) findViewById(R.id.stone22);
		spaces[2][3] = (ImageView) findViewById(R.id.stone23);
		spaces[3][0] = (ImageView) findViewById(R.id.stone30);
		spaces[3][1] = (ImageView) findViewById(R.id.stone31);
		spaces[3][2] = (ImageView) findViewById(R.id.stone32);
		spaces[3][3] = (ImageView) findViewById(R.id.stone33);

		for (ImageView array[] : spaces) {
			for (ImageView view : array) {
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						EventHandler handler = EventHandler.getInstance();

						int position = GameActivity.this.getPosition(v);

						if (!(handler.isValidSpace(position) && !handler.computerMoving)) {
							return;
						}

						int temp;
						switch (handler.gamestate) {
						case PREGAME:
						case TIE:
						case P1WIN:
						case P2WIN:
							return;
						case FIRSTMOVE:
							handler.state = BrainstonzState.set(handler.state,
									position, 1);
							handler.newTurn(2);
							break;
						case P1M1:
							handler.state = BrainstonzState.set(handler.state,
									position, 1);
							// Check for a win
							if ((temp = BrainstonzState.terminal(handler.state)) != 0) {
								handler.endGame(temp);
								break;
							}
							if (BrainstonzState
									.getPair(handler.state, position) == 1) {
								MoveLabelText = handler.moveLabels[1];
								handler.gamestate = GameState.P1R1;
							} else {
								if (!BrainstonzState
										.hasEmptySpace(handler.state)) {
									handler.endGame(0);
									break;
								}
								MoveLabelText = handler.moveLabels[2];
								handler.gamestate = GameState.P1M2;
							}
							break;
						case P1M2:
							handler.state = BrainstonzState.set(handler.state,
									position, 1);
							// Check for a win
							if ((temp = BrainstonzState.terminal(handler.state)) != 0) {
								handler.endGame(temp);
								break;
							}
							if (BrainstonzState
									.getPair(handler.state, position) == 1) {
								MoveLabelText = handler.moveLabels[3];
								handler.gamestate = GameState.P1R2;
							} else
								handler.newTurn(2);
							break;
						case P2M1:
							handler.state = BrainstonzState.set(handler.state,
									position, 2);
							// Check for a win
							if ((temp = BrainstonzState.terminal(handler.state)) != 0) {
								handler.endGame(temp);
								break;
							}
							if (BrainstonzState
									.getPair(handler.state, position) == 2) {
								MoveLabelText = handler.moveLabels[1];
								handler.gamestate = GameState.P2R1;
							} else {
								if (!BrainstonzState
										.hasEmptySpace(handler.state)) {
									handler.endGame(0);
									break;
								}
								MoveLabelText = handler.moveLabels[2];
								handler.gamestate = GameState.P2M2;
							}
							break;
						case P2M2:
							handler.state = BrainstonzState.set(handler.state,
									position, 2);
							// Check for a win
							if ((temp = BrainstonzState.terminal(handler.state)) != 0) {
								handler.endGame(temp);
								break;
							}
							if (BrainstonzState
									.getPair(handler.state, position) == 2) {
								MoveLabelText = handler.moveLabels[3];
								handler.gamestate = GameState.P2R2;
							} else
								handler.newTurn(1);
							break;
						case P1R1:
							handler.state = BrainstonzState.set(handler.state,
									position, 0);
							MoveLabelText = handler.moveLabels[2];
							handler.gamestate = GameState.P1M2;
							break;
						case P1R2:
							handler.state = BrainstonzState.set(handler.state,
									position, 0);
							handler.newTurn(2);
							break;
						case P2R1:
							handler.state = BrainstonzState.set(handler.state,
									position, 0);
							MoveLabelText = handler.moveLabels[2];
							handler.gamestate = GameState.P2M2;
							break;
						case P2R2:
							handler.state = BrainstonzState.set(handler.state,
									position, 0);
							handler.newTurn(1);
							break;
						}

						updateViews();
					}
				});
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_game:
			EventHandler.getInstance().newGame();
			updateViews();
			break;
		case R.id.start_game:
			EventHandler.getInstance().newGame();
			updateViews();
			break;
		case R.id.options:
			startActivity(new Intent(GameActivity.this, SettingsActivity.class));
			break;
		case R.id.help:
			startActivity(new Intent(GameActivity.this, HelpActivity.class));
			break;
		case R.id.about:
			startActivity(new Intent(GameActivity.this, AboutActivity.class));
			break;
		case R.id.exit_game:
			finish();
			System.exit(0);
			break;
		}
		return true;
	}
}
