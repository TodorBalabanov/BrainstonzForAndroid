/**
 * 
 */
package eu.veldsoft.brainstonz;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Administrator
 *
 */
public class Brainstonz {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		try {
			BrainstonzAI.load();
			ImageLoader.load();
			UIManager
					.setLookAndFeel("net.beeger.squareness.SquarenessLookAndFeel");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error loading Brainstonz. Sorry.", "Error Loading",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {

			@SuppressWarnings("serial")
			@Override
			public void run() {
				final GamePanel gamepanel = new GamePanel();
				JFrame frame = new JFrame() {
					@Override
					public void dispose() {
						if (gamepanel != null)
							gamepanel.dispose();
						super.dispose();
					}
				};
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.add(gamepanel);
				frame.setIconImage(ImageLoader.icon);
				frame.setTitle("Brainstonz\u2122 by McWiz");
				frame.pack();
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setVisible(true);
			}

		});
	}

}
