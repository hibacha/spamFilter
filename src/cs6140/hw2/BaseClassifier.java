package cs6140.hw2;

import java.util.Vector;

public class BaseClassifier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	protected boolean isFN(boolean isPredictSpam) {
		return !isPredictSpam;
	}
	protected  boolean isSpam(Vector<Double> mail) {
		return mail.get(57) == 1;
	}

}
