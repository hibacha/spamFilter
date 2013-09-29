package cs6140.hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class KCrossValidation {

	/**
	 * @param args
	 */
	private int k;
	private ArrayList<ArrayList<String>> partitionedFolds = new ArrayList<ArrayList<String>>();
	private HashMap<Integer, ArrayList<Vector<Double>>> vectorFoldsMap = new HashMap<Integer, ArrayList<Vector<Double>>>();
	private ArrayList<Vector<Double>> trainingData;
	private ArrayList<Vector<Double>> testingData;

	public KCrossValidation(int k) {
		this.k = k;

	}

	private void readFromGivenURL(String url) throws IOException {
		File file = new File(url);
		initPartitionedFolds();
		parseFile(file);
		groupRawData();
	}

	private void groupRawData() {
		for (int i = 0; i < partitionedFolds.size(); i++) {
			vectorFoldsMap.put(i,
					transformToFeatureVec(partitionedFolds.get(i)));
		}
	}

	private ArrayList<Vector<Double>> transformToFeatureVec(
			ArrayList<String> fold) {
		ArrayList<Vector<Double>> foldWithVectorFeature = new ArrayList<Vector<Double>>();
		for (String str : fold) {
			String[] features = str.split(",");
			Vector<Double> vec_features = new Vector<Double>();
			for (String strFrequency : features) {
				vec_features.add(Double.parseDouble(strFrequency));
			}
			foldWithVectorFeature.add(vec_features);
		}
		return foldWithVectorFeature;
	}

	private void parseFile(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader bf = new BufferedReader(fr);
		String str = "";
		int count = 0;
		while ((str = bf.readLine()) != null) {
			int fold_index = count % k;
			partitionedFolds.get(fold_index).add(str);
			count++;
		}
	}

	private void initPartitionedFolds() {
		partitionedFolds.clear();
		for (int i = 0; i < k; i++) {
			partitionedFolds.add(new ArrayList<String>());
		}
	}

	public void extractTestingSetByIndex(int indexOfTestingData) {
		trainingData.clear();
		testingData.clear();
		Iterator<Integer> it = vectorFoldsMap.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			ArrayList<Vector<Double>> emailsInOneFold = vectorFoldsMap.get(key);
            if(key == indexOfTestingData)
            	testingData.addAll(emailsInOneFold);
            else
            	trainingData.addAll(emailsInOneFold);
            
		}
	}

	public static void main(String[] args) throws IOException {
		KCrossValidation kv = new KCrossValidation(10);
		kv.readFromGivenURL(Constant.DATA_PATH);
	}
}
