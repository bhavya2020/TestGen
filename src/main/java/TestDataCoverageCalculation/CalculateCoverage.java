package TestDataCoverageCalculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CalculateCoverage {

    public static ArrayList<ArrayList<String>> getCoverage(ArrayList<ArrayList<Integer>> testData, ArrayList<ArrayList<Integer>> attributes, ArrayList<String> parameterNames) {

        ArrayList<ArrayList<String>> result = new ArrayList<>();


        ArrayList<Integer> totalDistinctPairs = getTotalDistinctPairs(attributes);

        int count = 0;
        double totalCoverage = 0;

        for (int i = 0; i < attributes.size(); i++) {
            for (int j = i + 1; j < attributes.size(); j++) {
                Set<ArrayList<Integer>> distinctPairs = new HashSet<>();
                ArrayList<String> resultRow = new ArrayList<>();
                resultRow.add(parameterNames.get(i) + " " + parameterNames.get(j));
                for (ArrayList<Integer> testDatum : testData) {
                    distinctPairs.add(new ArrayList<>(Arrays.asList(testDatum.get(i), testDatum.get(j))));
                }
                StringBuilder pairs = new StringBuilder();
                for(ArrayList<Integer> distinctPair : distinctPairs){
                    pairs.append(distinctPair.get(0));
                    pairs.append(",");
                    pairs.append(distinctPair.get(1));
                    pairs.append(" ");
                }
                resultRow.add(pairs.toString());
                double coverage = distinctPairs.size()/(double) totalDistinctPairs.get(count);
                count++;
                totalCoverage+=coverage;
                resultRow.add(String.valueOf(coverage));
                result.add(resultRow);
            }
        }

        totalCoverage/=totalDistinctPairs.size();
        ArrayList<String> resultRow = new ArrayList<>();
        resultRow.add(String.valueOf(totalCoverage));
        result.add(resultRow);

        return result;
    }

    private static ArrayList<Integer> getTotalDistinctPairs(ArrayList<ArrayList<Integer>> attributes) {

        ArrayList<Integer> totalDistinctPairs = new ArrayList<>();
        for (int i = 0; i < attributes.size(); i++)
            for (int j = i + 1; j < attributes.size(); j++)
                totalDistinctPairs.add(attributes.get(i).size() * attributes.get(j).size());

        return totalDistinctPairs;

    }

}
