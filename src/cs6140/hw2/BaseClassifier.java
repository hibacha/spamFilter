package cs6140.hw2;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class BaseClassifier {

	/**
	 * @param args
	 */
	protected List<Double> tau = new ArrayList<Double>();
	protected KCrossValidation kcrossValidation = new KCrossValidation(10);
	protected List<Point> plotPoints = new ArrayList<Point>();
	
	
	protected boolean isFN(boolean isPredictSpam) {
		return !isPredictSpam;
	}
	
	protected  boolean isSpam(Vector<Double> mail) {
		return mail.get(57) == 1;
	}
	
	protected double AUC(){
		double sum=0;
		for(int i=1;i<plotPoints.size();i++){
			sum+=(plotPoints.get(i).getX()-plotPoints.get(i-1).getX())*(plotPoints.get(i).getY()+plotPoints.get(i-1).getY());
		}
		return sum*0.5;
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
	public void ROC(IClassifier classifier){
		classifier.beginToTrainData(0);
		overallErrorRate(classifier, 0, false);
		Collections.sort(tau);
		Collections.reverse(tau);
		plotPoints.clear();
		for(double threshhold:tau){
			overallErrorRate(classifier,threshhold, true);
		}
		
		for(Point p: plotPoints){
//			System.out.println("["+ p.getX()+"],["+p.getY()+"]");
			System.out.println(p.getX()+","+p.getY());
		}
//		for(double d:tau){
//			System.out.println("ROC"+d);
//		}
	}
	public double[] overallErrorRate(IClassifier classifier, double threshHold,boolean drawROC) {
		double errorNum = 0;
		int fnNum = 0;
		int fpNum = 0;
		int tnNum = 0;
		int tpNum = 0;
		ArrayList<Vector<Double>> testingSet = kcrossValidation.getTestingData();
		if (!drawROC) {
			tau.clear();
		}
		for (Vector<Double> mail : testingSet) {
			double log = classifier.predictIsSpam(mail);
			if (!drawROC) {
				tau.add(log);
			}
			boolean isPredictSpam = log >= threshHold;
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
		
		if (drawROC) {
			System.out.print(fnNum+":"+fpNum+":"+tnNum+":"+tpNum+"\n");
			double tpr = tpr(fnNum, fpNum, tnNum, tpNum);
			double fpr = fpr(fnNum, fpNum, tnNum, tpNum);
			Point point = new Point(fpr, tpr);

			plotPoints.add(point);
		}
		return result;
	}
	
	public double tpr(int fnNum, int fpNum, int tnNum, int tpNum){
		
		return (double)(tpNum)/(double)(tpNum+fnNum);
	}
	
	public double fpr(int fnNum, int fpNum, int tnNum, int tpNum){
		return (double)(fpNum)/(double)(fpNum+tnNum);
	}
}
