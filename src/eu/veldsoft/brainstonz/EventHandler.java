package eu.veldsoft.brainstonz;

class EventHandler {

	public static final String[] moveLabels = { "Place First Stone",
			"Remove A Stone", "Place Second Stone", "Remove A Stone",
			"First Move", "Game Over" };

	public static final String[] turnLabels = { "BLACK'S TURN", "WHITE'S TURN",
			"IT'S A TIE!", "BLACK WINS!", "WHITE WINS!" };

	public static BrainstonzPlayer player1;
	public static BrainstonzPlayer player2;

	protected static int state;
	protected static GameState gamestate;

	protected boolean computerMoving;
	protected double[] aiSkillz;

	protected Successor succ = null;
	
	private static EventHandler me = null;

	public static EventHandler getInstance() {
		if (me == null)
			return (me = new EventHandler());
		return me;
	}

	public EventHandler() {
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
		gamestate = GameState.FIRSTMOVE;
		if (player1 == BrainstonzPlayer.COMPUTER) {
			computerTurn(1);
		}
	}

	protected void endGame(int winner) {
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
	}

	public int playerAt(int position) {
		return BrainstonzState.get(state, position);
	}
}
