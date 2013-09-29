package cs6140.hw2;

import java.util.ArrayList;
import java.util.Vector;

public class HistogramClassifier extends BaseClassifier {

	/**
	 * @param args
	 */
	private KCrossValidation kcrossValidation = new KCrossValidation(10);
	private ArrayList<Interval[]> knownBucketList = new ArrayList<Interval[]>();
    private double[][] spamProb=new double[57][4];
    private double[][] nonSpamProb=new double[57][4];
	
	private double trainingSetSpamTotalNum = 0;
	private double trainingSetNonSpamTotalNum = 0;

	double[] spamConditionalMui = new double[57];
	double[] nonSpamConditionalMui = new double[57];
	double[] overallTrainingMui = new double[57];

	double proriSpam = 0;
	double proriNonSpam = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void beginToTrainData(int k) {
		kcrossValidation.extractTestingSetByIndex(k);

		ArrayList<Vector<Double>> trainingSet = kcrossValidation
				.getTrainingData();
		trainingSetNonSpamTotalNum = 0;
		trainingSetSpamTotalNum = 0;
		double[] maxValue = new double[57];
		double[] minValue = new double[57];
		double[] spamConditionalSum = new double[57];
		double[] nonSpamConditionalSum = new double[57];

		for (Vector<Double> mail : trainingSet) {

			if (isSpam(mail)) {
				trainingSetSpamTotalNum++;
				for (int featureIndex = 0; featureIndex < 57; featureIndex++) {
					spamConditionalSum[featureIndex] += mail.get(featureIndex);
				}
			} else {
				trainingSetNonSpamTotalNum++;
				for (int featureIndex = 0; featureIndex < 57; featureIndex++) {
					nonSpamConditionalSum[featureIndex] += mail
							.get(featureIndex);
				}
			}
			for(int featureIndex = 0; featureIndex < 57; featureIndex++){
				if(mail.get(featureIndex)<minValue[featureIndex]){
					minValue[featureIndex] = mail.get(featureIndex);
				}
				if(mail.get(featureIndex)>maxValue[featureIndex]){
					maxValue[featureIndex] = mail.get(featureIndex);
				}
			}
		}
		
		for(int i=0; i<57;i++){
			spamConditionalMui[i]=spamConditionalSum[i]/trainingSetSpamTotalNum;
			nonSpamConditionalMui[i]=nonSpamConditionalSum[i]/trainingSetNonSpamTotalNum;
			overallTrainingMui[i]=(spamConditionalSum[i]+nonSpamConditionalSum[i])/(trainingSetNonSpamTotalNum+trainingSetSpamTotalNum);
			
		}
		
		proriSpam = trainingSetSpamTotalNum
				/ (trainingSetSpamTotalNum + trainingSetNonSpamTotalNum);
		proriNonSpam = trainingSetNonSpamTotalNum
				/ (trainingSetSpamTotalNum + trainingSetNonSpamTotalNum);
		
		knownBucketList.clear();
		for(int i=0; i<57;i++){
			double lowerMean = spamConditionalMui[i]<nonSpamConditionalMui[i]?spamConditionalMui[i]:nonSpamConditionalMui[i];
			double higherMean = spamConditionalMui[i]>=nonSpamConditionalMui[i]?spamConditionalMui[i]:nonSpamConditionalMui[i];
		    Interval in1= new Interval(Double.NEGATIVE_INFINITY, lowerMean , true, true);	
		    Interval in2= new Interval(lowerMean, overallTrainingMui[i], false, true);
		    Interval in3= new Interval(overallTrainingMui[i], higherMean, false, true);
		    Interval in4= new Interval(higherMean, Double.POSITIVE_INFINITY, false, true);
		    Interval[] intervals={in1,in2,in3,in4};
		    knownBucketList.add(intervals);
		}
		
		for(int i=0;i<57;i++){
			spamProb[i]=new double[4];
		}
		for (Vector<Double> mail : trainingSet) {
			if(isSpam(mail)){
				for(int i=0; i<57; i++){
					double[] probForOneFeature=this.spamProb[i];
					putIntoBucket(probForOneFeature,mail.get(i),knownBucketList.get(i), trainingSetSpamTotalNum);
				}
				
			}
			
		}
		
	}
	
	public void putIntoBucket(double[] probForOneFeature,double featureValue, Interval[] knownIntervals,double denominator){
		int resultIndexOfInterval=0;
		for(int i=0; i<4 ;i++){
			if(knownIntervals[i].isInInterval(featureValue)){
			   resultIndexOfInterval=i;
			   break;
			}
			
		}
		probForOneFeature[resultIndexOfInterval]+=1/denominator;
	}
}