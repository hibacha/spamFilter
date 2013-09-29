package cs6140.hw2;

import java.util.ArrayList;
import java.util.Vector;

public class GaussianClassifier extends BaseClassifier{

	/**
	 * @param args
	 */
	private KCrossValidation kcrossValidation = new KCrossValidation(10);
	private double trainingSetSpamTotalNum = 0;
	private double trainingSetNonSpamTotalNum = 0;
	
	double[] spamConditionalMui = new double[57];
	double[] nonSpamConditionalMui = new double[57];
	double[] overallTrainingMui = new double[57];
	
	double[] varianceSpam = new double[57];
	double[] varianceNonSpam = new double[57];
	double[] varianceOverall = new double[57];
	
	double proriSpam = 0;
	double proriNonSpam = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GaussianClassifier g= new GaussianClassifier();
		for(int testGroupId = 0; testGroupId < 10; testGroupId++){
			g.beginToTrainData(testGroupId);
			double[] result = g.overallErrorRate();
			System.out.println("Gaussian@@FN:" + result[0] + " FT:" + result[1]
					+ " OVERALL ERROR RATE:" + result[2]);
		}
	}
	
	public void beginToTrainData(int k) {
		trainingSetNonSpamTotalNum = 0;
		trainingSetSpamTotalNum = 0;
		kcrossValidation.extractTestingSetByIndex(k);
		
		double[] spamSumFreq = new double[57];
		double[] nonSpamSumFreq = new double[57];
		
		ArrayList<Vector<Double>> trainingSet = kcrossValidation
				.getTrainingData();
		
		for (Vector<Double> mail : trainingSet) {
			staticsSpamAndNonSpamNumber(mail);
			calConditionalMean(spamSumFreq, nonSpamSumFreq, mail);
		}
		
		for (int i = 0; i < 57; i++) {
			spamConditionalMui[i]=spamSumFreq[i]/trainingSetSpamTotalNum;
			nonSpamConditionalMui[i]=nonSpamSumFreq[i]/trainingSetNonSpamTotalNum;
			overallTrainingMui[i]=(spamSumFreq[i]+nonSpamConditionalMui[i])/(trainingSetSpamTotalNum+trainingSetNonSpamTotalNum);
		}
		calculateVariance(k);
		proriSpam = trainingSetSpamTotalNum
				/ (trainingSetSpamTotalNum + trainingSetNonSpamTotalNum);
		proriNonSpam = trainingSetNonSpamTotalNum
				/ (trainingSetSpamTotalNum + trainingSetNonSpamTotalNum);
	}
	
	private void calConditionalMean(double[] spamSumFreq, double[] nonSpamSumFreq, Vector<Double> oneMail) {
		// TODO Auto-generated method stub
		for(int featureIndex=0;featureIndex<57; featureIndex++){
			if(isSpam(oneMail)){
				spamSumFreq[featureIndex]+=oneMail.get(featureIndex);
			}else{
				nonSpamSumFreq[featureIndex]+=oneMail.get(featureIndex);
			}
		}
	}
	private void calculateVariance(int k) {
		// TODO Auto-generated method stub
		double[] sumSquareSpam= new double[57];
		double[] sumSquareNonSpam = new double[57];
		double[] sumSquareOverall = new double[57];
		
		ArrayList<Vector<Double>> trainingSet = kcrossValidation.getTrainingData();
		
				for(Vector<Double> mail:trainingSet){
					for(int featureIndex=0;featureIndex<57;featureIndex++){
						sumSquareOverall[featureIndex]+=Math.pow(mail.get(featureIndex)-overallTrainingMui[featureIndex], 2);
						if(isSpam(mail)){
							sumSquareSpam[featureIndex]+=Math.pow(mail.get(featureIndex)-spamConditionalMui[featureIndex], 2);
						}else{
							sumSquareNonSpam[featureIndex]+=Math.pow(mail.get(featureIndex)-nonSpamConditionalMui[featureIndex], 2);
						}
					}
				}
		
		smoothingVariance(sumSquareSpam, sumSquareNonSpam, sumSquareOverall);
	}
	private void smoothingVariance(double[] sumSquareSpam, double[] sumSquareNonSpam,double[] sumSquareOverall ){
		for(int i=0;i<57;i++){
		  varianceOverall[i]=sumSquareOverall[i]/(trainingSetNonSpamTotalNum+trainingSetSpamTotalNum-1);
		}
		double lamda=trainingSetSpamTotalNum/(trainingSetSpamTotalNum+2);
		for(int i=0;i<57;i++){
		  varianceSpam[i]=(sumSquareSpam[i]/(trainingSetSpamTotalNum-1))*lamda+(1-lamda)*varianceOverall[i];
		}
		lamda=trainingSetNonSpamTotalNum/(trainingSetNonSpamTotalNum+2);
		for(int i=0;i<57;i++){
		  varianceNonSpam[i]=(sumSquareNonSpam[i]/(trainingSetNonSpamTotalNum-1))*lamda+(1-lamda)*varianceOverall[i];
		}
	}
	private void staticsSpamAndNonSpamNumber(Vector<Double> mail) {
		// TODO Auto-generated method stub
		if(isSpam(mail)){
			trainingSetSpamTotalNum++;
		}else{
			trainingSetNonSpamTotalNum++;
		}
	}

	public double[] overallErrorRate() {
		double errorNum = 0;
		int fnNum = 0;
		int fpNum = 0;
		for (Vector<Double> mail : kcrossValidation.getTestingData()) {
			boolean isPredictSpam = predictIsSpamByGaussian(mail);
			boolean isActualSpam = isSpam(mail);
			if (isActualSpam != isPredictSpam) {
				errorNum++;
				if (isFN(isPredictSpam)) {
					fnNum++;
				} else {
					fpNum++;
				}
			}
		}
		double[] result = { fnNum, fpNum, errorNum /  kcrossValidation.getTestingData().size() };
		return result;
	}
	public boolean predictIsSpamByGaussian(Vector<Double> mail){
		double express=0;
		double spamEle=0;
		double nonSpamEle=0;
		for(int index=0;index<57;index++){
			spamEle=pdf(mail.get(index),spamConditionalMui[index],varianceSpam[index]);
			nonSpamEle=pdf(mail.get(index), nonSpamConditionalMui[index], varianceNonSpam[index]);
			express+=Math.log(spamEle/nonSpamEle);
			
		}
		express+=Math.log(proriSpam/proriNonSpam);
		return express>0;
	}
	public double pdf(double x, double mui, double var){
		double PI=3.14;
		return  (1/Math.sqrt(2*PI*var))*Math.exp(-1*Math.pow(x-mui,2)/(2*var));
	}
	
}
