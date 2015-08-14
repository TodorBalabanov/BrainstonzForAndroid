package eu.veldsoft.brainstonz;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

class SwingEventHandler extends GameModel {
	protected Thread computerThread = null;

	private static SwingEventHandler me = null;

	private GameBoard board = null;
	
	private SidePanel sidePanel = null;

	public static SwingEventHandler getInstance() {
		if (me == null)
			return (me = new SwingEventHandler());
		return me;
	}

	public void registerBoard(GameBoard board) {
		this.board = board;
	}

	public void registerSidePanel(SidePanel sidePanel) {
		this.sidePanel = sidePanel;
	}

	protected void highlightWinningSet() {
		final int[] set = BrainstonzState.winningSet(state);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (set != null) {
					for (int i = 0; i < 4; i++)
						board.spaces[set[i]].setHighlighted(true);
				}
				board.repaint();
			}
		});
	}

	@Override
	protected void computerTurn(final int player) {
		super.computerTurn(player);

		sidePanel.setNewGameEnabled(false);
		computerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (succ == null) {
					return;
				}
				int setVal;
				GameSpace space;
				try {
					Thread.sleep(getDelay());
				} catch (InterruptedException e) {
				}
				for (int i = 0; i < 4; i++) {
					if (succ.moves[i] != -1) {
						setVal = ((i + 1) % 2) * player; // Even --> player, Odd
															// --> 0 (remove)
						space = board.spaces[succ.moves[i]];
						//TODO Separate business logic from presentation. 
						state = BrainstonzState.set(state, succ.moves[i], setVal);
						space.setHighlighted(true);
						space.repaint();
						for (int j = i + 1; j < 4; j++) {
							if (succ.moves[j] != -1) {
								sidePanel.setMoveLabelText(moveLabels[j]);
								break;
							}
						}
						try {
							Thread.sleep(getDelay());
						} catch (InterruptedException e) {
						}
						space.setHighlighted(false);
						space.repaint();
					}
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						newTurn(BrainstonzState.opponent[player]);
					}
				});
			}

		});
		computerThread.start();
	}

	@Override
	public void newGameButton() {
		switch (gamestate) {
		case PREGAME:
		case P1WIN:
		case P2WIN:
		case TIE:
			break;
			
		default:
			Object[] options = { "Yes", "No" };
			if (JOptionPane.showOptionDialog(null, "Quit the current game?",
					"New Game", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, ImageLoader.questionIcon,
					options, options[1]) != JOptionPane.YES_OPTION) {
				return;
			}
			return;
		}
		
		
		super.newGameButton();
	}

	public void mouseClick(GameSpace space, MouseEvent e) {
		if (isValidSpace(space.getPosition()) && !computerMoving) {
			int position = space.getPosition();
			int temp;
			switch (gamestate) {
			case PREGAME:
			case TIE:
			case P1WIN:
			case P2WIN:
				return;
			case FIRSTMOVE:
				state = BrainstonzState.set(state, position, 1);
				newTurn(2);
				break;
			case P1M1:
				state = BrainstonzState.set(state, position, 1);
				// Check for a win
				if ((temp = BrainstonzState.terminal(state)) != 0) {
					endGame(temp);
					break;
				}
				if (BrainstonzState.getPair(state, position) == 1) {
					sidePanel.setMoveLabelText(moveLabels[1]);
					gamestate = GameState.P1R1;
				} else {
					if (!BrainstonzState.hasEmptySpace(state)) {
						endGame(0);
						break;
					}
					sidePanel.setMoveLabelText(moveLabels[2]);
					gamestate = GameState.P1M2;
				}
				break;
			case P1M2:
				state = BrainstonzState.set(state, position, 1);
				// Check for a win
				if ((temp = BrainstonzState.terminal(state)) != 0) {
					endGame(temp);
					break;
				}
				if (BrainstonzState.getPair(state, position) == 1) {
					sidePanel.setMoveLabelText(moveLabels[3]);
					gamestate = GameState.P1R2;
				} else
					newTurn(2);
				break;
			case P2M1:
				state = BrainstonzState.set(state, position, 2);
				// Check for a win
				if ((temp = BrainstonzState.terminal(state)) != 0) {
					endGame(temp);
					break;
				}
				if (BrainstonzState.getPair(state, position) == 2) {
					sidePanel.setMoveLabelText(moveLabels[1]);
					gamestate = GameState.P2R1;
				} else {
					if (!BrainstonzState.hasEmptySpace(state)) {
						endGame(0);
						break;
					}
					sidePanel.setMoveLabelText(moveLabels[2]);
					gamestate = GameState.P2M2;
				}
				break;
			case P2M2:
				state = BrainstonzState.set(state, position, 2);
				// Check for a win
				if ((temp = BrainstonzState.terminal(state)) != 0) {
					endGame(temp);
					break;
				}
				if (BrainstonzState.getPair(state, position) == 2) {
					sidePanel.setMoveLabelText(moveLabels[3]);
					gamestate = GameState.P2R2;
				} else
					newTurn(1);
				break;
			case P1R1:
				state = BrainstonzState.set(state, position, 0);
				sidePanel.setMoveLabelText(moveLabels[2]);
				gamestate = GameState.P1M2;
				break;
			case P1R2:
				state = BrainstonzState.set(state, position, 0);
				newTurn(2);
				break;
			case P2R1:
				state = BrainstonzState.set(state, position, 0);
				sidePanel.setMoveLabelText(moveLabels[2]);
				gamestate = GameState.P2M2;
				break;
			case P2R2:
				state = BrainstonzState.set(state, position, 0);
				newTurn(1);
				break;
			}
			space.setHighlighted(false);
			space.repaint();
		}
	}

	public void mouseEnter(GameSpace space, MouseEvent e) {
		if (isValidSpace(space.getPosition())) {
			space.setHighlighted(true);
			space.repaint();
		}
	}

	public void mouseExit(GameSpace space, MouseEvent e) {
		switch (gamestate) {
		case P1WIN:
		case P2WIN:
			return;
		}
		space.setHighlighted(false);
		space.repaint();
	}
	
	@Override
	protected void preGame() {
		super.preGame();
		
		board.reset();
		sidePanel.enableControls(true);
		sidePanel.setNewGameButtonText("Start Game");
		sidePanel.setInstructionContext(-1);
	}
	
	@Override
	protected void newGame() {
		super.newGame();
		sidePanel.setTurnLabelText(turnLabels[0], 1);
		sidePanel.setMoveLabelText(moveLabels[4]);
		sidePanel.enableControls(false);
		sidePanel.setNewGameButtonText("New Game");
		board.reset();
		aiSkillz[1] = sidePanel.player1.getSkill();
		aiSkillz[2] = sidePanel.player2.getSkill();
		player1 = sidePanel.player1.getType();
		player2 = sidePanel.player2.getType();
	}
	
	@Override
	protected void endGame(int winner) {
		super.endGame(winner);
		
		switch (winner) {
		case 0:
			break;
		case 1:
			highlightWinningSet();
			break;
		case 2:
			highlightWinningSet();
			break;
		}

		sidePanel.setTurnLabelText(turnLabels[winner + 2], winner);
		sidePanel.setMoveLabelText(moveLabels[5]);
		board.repaint();
	}
	
	@Override
	protected void newTurn(int player) {
		super.newTurn(player);
		sidePanel.setNewGameEnabled(true);
		sidePanel.setTurnLabelText(turnLabels[player - 1], player);
		sidePanel.setMoveLabelText(moveLabels[0]);
	}

	public int getDelay() {
		return sidePanel.getDelay();
	}
}
