package cs6140.hw2;

import java.util.Vector;

public interface IClassifier {
	/**
	 * 
	 * @param mail
	 * @return log-odds for expression of p(spam|data)/p(nonspam|data)
	 */
  public double predictIsSpam(Vector<Double> mail);
}
