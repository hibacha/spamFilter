package cs6140.hw2;

public class Interval {

	/**
	 * @param args
	 */
	private double lowerBound;
	private double upperBound;

	private boolean isLowerBoundIncluded;
	private boolean isUpperBoundIncluded;

	public Interval(double lowerBound, double upperBound,boolean isLowerBoundIncluded, boolean isUpperBoundIncluded) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.isLowerBoundIncluded = isLowerBoundIncluded;
		this.isUpperBoundIncluded = isUpperBoundIncluded;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean isInInterval(double givenValue) {
		 return (givenValue < upperBound && givenValue > lowerBound)||
				 (isLowerBoundIncluded&&(lowerBound==givenValue))||
				 (isUpperBoundIncluded&&(upperBound==givenValue));

		
	}

}
