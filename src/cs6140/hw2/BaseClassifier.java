package cs6140.hw2;

import java.util.Vector;

public class BaseClassifier {

	/**
	 * @param args
	 */
	
	protected KCrossValidation kcrossValidation = new KCrossValidation(10);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	protected boolean isFN(boolean isPredictSpam) {
		return !isPredictSpam;
	}
	protected  boolean isSpam(Vector<Double> mail) {
		return mail.get(57) == 1;
	}

	protected double average(double[] elements){
		if(elements.length==0){
			return 0;
		}
		double sum=0;
		for(int i=0;i<elements.length;i++){
			sum+=elements[i];
		}
		return sum/elements.length;
	}
	
	public double[] overallErrorRate(IClassifier classifier) {
		double errorNum = 0;
		int fnNum = 0;
		int fpNum = 0;
		int tnNum = 0;
		int tpNum = 0;
		
		for (Vector<Double> mail : kcrossValidation.getTestingData()) {
			double log = classifier.predictIsSpam(mail);
			boolean isPredictSpam = log>0;
			boolean isActualSpam = isSpam(mail);
			if (isActualSpam != isPredictSpam) {
				errorNum++;
				if (isFN(isPredictSpam)) {
					fnNum++;
				} else {
					fpNum++;
				}
			}else{
				if(isPredictSpam){
					tpNum++;
				}else{
					tnNum++;
				}
			}
		}
		double[] result = { fnNum, fpNum,
				errorNum / kcrossValidation.getTestingData().size() };
		System.out.println(tpr(fnNum, fpNum, tnNum, tpNum));
		System.out.println(fpr(fnNum, fpNum, tnNum, tpNum));
		return result;
	}
	
	public double tpr(int fnNum, int fpNum, int tnNum, int tpNum){
		
		return (double)tpNum/(tpNum+fnNum);
	}
	
	public double fpr(int fnNum, int fpNum, int tnNum, int tpNum){
		return (double)fpNum/(fpNum+tnNum);
	}
}
