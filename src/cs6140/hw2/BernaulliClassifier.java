package cs6140.hw2;

import java.util.ArrayList;
import java.util.Vector;





public class BernaulliClassifier extends BaseClassifier implements IClassifier{

	/**
	 * @param args
	 */

	double trainingSetSpamTotalNum = 0;
	double trainingSetNonSpamTotalNum = 0;

	double[] probLessEqMuiSpamArray = new double[57];
	double[] probGreatMuiSpamArray = new double[57];
	double[] probLessEqMuiNonSpamArray = new double[57];
	double[] probGreatMuiNonSpamArray = new double[57];

	double proriSpam = 0;
	double proriNonSpam = 0;

	public static void main(String[] args) {
		BernaulliClassifier b= new BernaulliClassifier();
		for(int testGroupId = 0; testGroupId < 10; testGroupId++){
			b.beginToTrainData(testGroupId);
			double[] result = b.overallErrorRate(b,0,false);
			System.out.println("B@@FN:" + result[0] + " FP:" + result[1]
					+ " OVERALL ERROR RATE:" + result[2]);
		}
	   
//		b.beginToTrainData(0);
//		//first time run on fold1 to get a list of tau
//		b.overallErrorRate(b, 0, false);
		b.ROC(b);
		System.out.println("AUC="+b.AUC());
	}
	
	
	public double predictIsSpam(Vector<Double> mail) {
		double likelihoodSpam = 1;
		double likelihoodNonSpam = 1;
		for (int featureIndex = 0; featureIndex < 57; featureIndex++) {
			if (mail.get(featureIndex) <= MyConstant.OVERALLMEANARRAYLIST
					.get(featureIndex)) {
				likelihoodSpam *= probLessEqMuiSpamArray[featureIndex];
				likelihoodNonSpam *= probLessEqMuiNonSpamArray[featureIndex];
			} else {
				likelihoodSpam *= probGreatMuiSpamArray[featureIndex];
				likelihoodNonSpam *= probGreatMuiNonSpamArray[featureIndex];
			}
		}
		return Math.log((likelihoodSpam * proriSpam)
				/ (likelihoodNonSpam * proriNonSpam)) ;
	}
	
	public void beginToTrainData(int k) {
		trainingSetNonSpamTotalNum = 0;
		trainingSetSpamTotalNum = 0;
		kcrossValidation.extractTestingSetByIndex(k);
		
		double[] countLessEqMuiSpam = new double[57];
		double[] countGreatMuiSpam = new double[57];
		double[] countLessEqMuiNonSpam = new double[57];
		double[] countGreatMuiNonSpam = new double[57];

		ArrayList<Vector<Double>> trainingSet = kcrossValidation
				.getTrainingData();
		for (Vector<Double> mail : trainingSet) {
			countTimes(countLessEqMuiSpam, countGreatMuiSpam,
					countLessEqMuiNonSpam, countGreatMuiNonSpam, mail);
		}

		for (int i = 0; i < 57; i++) {
			probLessEqMuiSpamArray[i] = (countLessEqMuiSpam[i] + 1)
					/ (trainingSetSpamTotalNum + 2);
			probGreatMuiSpamArray[i] = (countGreatMuiSpam[i] + 1)
					/ (trainingSetSpamTotalNum + 2);
			probLessEqMuiNonSpamArray[i] = (countLessEqMuiNonSpam[i] + 1)
					/ (trainingSetNonSpamTotalNum + 2);
			probGreatMuiNonSpamArray[i] = (countGreatMuiNonSpam[i] + 1)
					/ (trainingSetNonSpamTotalNum + 2);

		}
		proriSpam = trainingSetSpamTotalNum
				/ (trainingSetSpamTotalNum + trainingSetNonSpamTotalNum);
		proriNonSpam = trainingSetNonSpamTotalNum
				/ (trainingSetSpamTotalNum + trainingSetNonSpamTotalNum);

	}

	private void countTimes(double[] countLessEqMuiSpam,
			double[] countGreatMuiSpam, double[] countLessEqMuiNonSpam,
			double[] countGreatMuiNonSpam, Vector<Double> oneMail) {
		if (isSpam(oneMail)) {
			for (int featureIndex = 0; featureIndex < 57; featureIndex++) {
				if (oneMail.get(featureIndex) <= MyConstant.OVERALLMEANARRAYLIST
						.get(featureIndex)) {
					countLessEqMuiSpam[featureIndex]++;
				} else {
					countGreatMuiSpam[featureIndex]++;
				}
			}
			trainingSetSpamTotalNum++;
		} else {
			for (int featureIndex = 0; featureIndex < 57; featureIndex++) {
				if (oneMail.get(featureIndex) <= MyConstant.OVERALLMEANARRAYLIST
						.get(featureIndex)) {
					countLessEqMuiNonSpam[featureIndex]++;
				} else {
					countGreatMuiNonSpam[featureIndex]++;
				}
			}
			trainingSetNonSpamTotalNum++;
		}
	}

	

}
