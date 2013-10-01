package cs6140.hw2;

import java.util.Vector;

public interface IClassifier {
  public boolean predictIsSpam(Vector<Double> mail);
}
