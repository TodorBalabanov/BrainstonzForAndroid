/**
 * 
 */
package eu.veldsoft.brainstonz;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Administrator
 *
 */
class BrainstonzAI {

	private static byte[] tree = null;

	static InputStream is = null;
	
	public static void load(InputStream is) throws IOException {
		// Get the size of the file
		long length = is.available();

		if (length <= 0)
			throw new IOException("Zero length file!");

		if (length > Integer.MAX_VALUE)
			throw new IOException("File too large.");

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length)
			throw new IOException("Could not completely read file.");

		tree = bytes;
		is.close();
	}

	public static Successor move(int state, int player, double skill) {
		int temp;
		if (Math.random() < skill && state != 0) {
			// Use Tree
			Successor tempsucc = null;
			// Initialize Most extreme value observed (min/max)
			// to Negative/Positive "Infinity"
			int extreme = player == 1 ? -3 : 3;
			// Loop through successors
			for (Successor s : BrainstonzState.successors(state, player)) {
				// Maximize or Minimize?
				if (player == 1) {
					// Get Value of Successor
					int value = 0;
					if(tree == null) {
						value = fromFile(s.state);
					} else {
						value = tree[s.state];
					}
					temp = BrainstonzState.get(value, 3);
					temp = temp == 2 ? -1 : temp;
					// If Greater than Max, update extreme value
					// and point tempsucc to successor
					if (temp > extreme) {
						extreme = temp;
						tempsucc = s;
					}
				} else {
					// Get Value of Successor
					int value = 0;
					if(tree == null) {
						value = fromFile(s.state);
					} else {
						value = tree[s.state];
					}
					temp = BrainstonzState.get(value, 1);
					temp = temp == 2 ? -1 : temp;
					// If Less than Min, update extreme value
					// and point tempsucc to successor
					if (temp < extreme) {
						extreme = temp;
						tempsucc = s;
					}
				}
			}
			// Show moves and return successor state
			return tempsucc;
		} else {
			// Pick Random Successor
			List<Successor> succs = BrainstonzState.successors(state, player);
			int rand = (int) (succs.size() * Math.random());
			return succs.get(rand);
		}
	}

	private static int fromFile(int index) {
		int result = 0;
		try {
			is.reset();
			for(int i=0; i<index; i++){
				is.read();
			}
			result = is.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
