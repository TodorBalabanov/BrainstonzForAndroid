package eu.veldsoft.brainstonz;

class GameModel {

	static final String[] moveLabels = { "Place First Stone", "Remove A Stone",
			"Place Second Stone", "Remove A Stone", "First Move", "Game Over" };

	static final String[] turnLabels = { "BLACK'S TURN", "WHITE'S TURN",
			"IT'S A TIE!", "BLACK WINS!", "WHITE WINS!" };

	static String moveText = "";
	static String turnText = "";

	static BrainstonzPlayer player1;
	static BrainstonzPlayer player2;

	protected static int state;
	protected static GameState gamestate;

	protected boolean computerMoving;
	protected double[] aiSkillz;
	protected int delay;

	protected Successor succ = null;

	private static GameModel me = null;

	public static GameModel getInstance() {
		if (me == null)
			return (me = new GameModel());
		return me;
	}

	private int getDelay() {
		return 2000 - delay;
	}

	public GameModel() {
		player1 = BrainstonzPlayer.HUMAN;
		player2 = BrainstonzPlayer.COMPUTER;
		state = 0;
		computerMoving = false;
		gamestate = GameState.PREGAME;
		aiSkillz = new double[] { 0.0, 0.0, 0.0 };
	}

	public void newGameButton() {
		switch (gamestate) {
		case PREGAME:
			newGame();
			return;

		case P1WIN:
		case P2WIN:
		case TIE:
			preGame();
			break;
		}
	}

	public boolean isValidSpace(int position) {
		if (computerMoving)
			return false;
		switch (gamestate) {
		case PREGAME:
		case TIE:
		case P1WIN:
		case P2WIN:
			return false;
		case FIRSTMOVE:
		case P1M1:
		case P1M2:
		case P2M1:
		case P2M2:
			return BrainstonzState.get(state, position) == 0;
		case P1R1:
		case P1R2:
			return BrainstonzState.get(state, position) == 2;
		case P2R1:
		case P2R2:
			return BrainstonzState.get(state, position) == 1;
		}
		return false;
	}

	protected void preGame() {
		computerMoving = false;
		state = 0;
		gamestate = GameState.PREGAME;
	}

	protected void newGame() {
		computerMoving = false;
		state = 0;
		turnText = turnLabels[0];
		moveText = moveLabels[4];
		gamestate = GameState.FIRSTMOVE;
		if (player1 == BrainstonzPlayer.COMPUTER) {
			computerTurn(1);
		}
	}

	protected void endGame(int winner) {
		turnText = turnLabels[winner + 2];
		moveText = moveLabels[5];

		switch (winner) {
		case 0:
			gamestate = GameState.TIE;
			break;
		case 1:
			gamestate = GameState.P1WIN;
			break;
		case 2:
			gamestate = GameState.P2WIN;
			break;
		}
	}

	protected void newTurn(int player) {
		int temp;
		// Check for a win
		if ((temp = BrainstonzState.terminal(state)) != 0) {
			endGame(temp);
			return;
		}
		// Check for a tie
		if (BrainstonzState.successors(state, player).size() == 0) {
			endGame(0);
			return;
		}
		computerMoving = false;

		turnText = turnLabels[player - 1];
		moveText = moveLabels[0];

		if (player == 1) {
			gamestate = GameState.P1M1;
			if (player1 == BrainstonzPlayer.COMPUTER) {
				computerTurn(player);
			}
		} else {
			gamestate = GameState.P2M1;
			if (player2 == BrainstonzPlayer.COMPUTER) {
				computerTurn(player);
			}
		}
	}

	protected void computerTurn(final int player) {
		computerMoving = true;
		succ = BrainstonzAI.move(state, player, aiSkillz[player]);

		Thread computer = new Thread(new Runnable() {
			@Override
			public void run() {
				if (succ == null) {
					return;
				}
				int setVal;
				try {
					Thread.sleep(getDelay());
				} catch (InterruptedException e) {
				}
				for (int i = 0; i < 4; i++) {
					if (succ.moves[i] != -1) {
						/*
						 * Even --> player, Odd --> 0 (remove)
						 */
						setVal = ((i + 1) % 2) * player;
						state = BrainstonzState.set(state, succ.moves[i],
								setVal);
						for (int j = i + 1; j < 4; j++) {
							if (succ.moves[j] != -1) {
								moveText = moveLabels[j];
								break;
							}
						}
						try {
							Thread.sleep(getDelay());
						} catch (InterruptedException e) {
						}
					}
				}

				newTurn(BrainstonzState.opponent[player]);
			}
		});
		computer.start();
	}

	public int playerAt(int position) {
		return BrainstonzState.get(state, position);
	}
}
