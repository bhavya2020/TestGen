package TestDataPrioritization;

import java.util.*;

public class PrioritizeTestData {


    //Incremental-interaction-coverage-based-prioritization
    public static ArrayList<ArrayList<Integer>> ApplyIICBP(ArrayList<ArrayList<Integer>> testData) {

        ArrayList<ArrayList<Integer>> prioritizedTestData = new ArrayList<>();
        int strength = 1;
        int size = testData.size();

        while (prioritizedTestData.size() != size) {
            if (getCombinatorialSetForTestSuite(prioritizedTestData, strength).size() == getCombinatorialSetForTestSuite(testData, strength).size()) {
                strength++;
            }
            int bestTestElementIndex = getBestTestElementIndex(prioritizedTestData, testData, strength);
            prioritizedTestData.add(testData.get(bestTestElementIndex));
            testData.remove(bestTestElementIndex);
        }

        return prioritizedTestData;

    }

    private static int getBestTestElementIndex(ArrayList<ArrayList<Integer>> prioritizedTestData, ArrayList<ArrayList<Integer>> testData, int strength) {

        int bestDistance = -1;
        ArrayList<Integer> equalSet = new ArrayList<>();
        int testCaseIndex = 0;

        for (ArrayList<Integer> testCase : testData) {

            int UVCD = getUncoveredTWiseValueCombinationDistance(testCase, prioritizedTestData, strength);
            if (UVCD > bestDistance) {
                equalSet.clear();
                bestDistance = UVCD;
                equalSet.add(testCaseIndex);
            } else if (UVCD == bestDistance) {
                equalSet.add(testCaseIndex);
            }
            testCaseIndex++;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(equalSet.size());
        return equalSet.get(randomIndex);

    }

    private static int getUncoveredTWiseValueCombinationDistance(ArrayList<Integer> testCase, ArrayList<ArrayList<Integer>> testSuite, int strength) {

        Set<ArrayList<Integer>> testSuiteCombinatorialSet = getCombinatorialSetForTestSuite(testSuite, strength);
        Set<ArrayList<Integer>> testCaseCombinatorialSet = new HashSet<>(getCombinatorialSetForTestCase(testCase, strength));

        testCaseCombinatorialSet.removeAll(testSuiteCombinatorialSet);

        return testCaseCombinatorialSet.size();

    }

    private static Set<ArrayList<Integer>> getCombinatorialSetForTestCase(ArrayList<Integer> testCase, int strength) {

        Set<ArrayList<Integer>> combinatorialSet = new HashSet<>();
        ArrayList<Integer> element = new ArrayList<>();
        return getTWiseValueCombinations(testCase, strength, combinatorialSet, element, 0, 0);

    }

    private static Set<ArrayList<Integer>> getCombinatorialSetForTestSuite(ArrayList<ArrayList<Integer>> testSuite, int strength) {

        Set<ArrayList<Integer>> combinatorialSet = new HashSet<>();
        for (ArrayList<Integer> testCase : testSuite) {
            combinatorialSet.addAll(getCombinatorialSetForTestCase(testCase, strength));
        }
        return combinatorialSet;
    }

    private static Set<ArrayList<Integer>> getTWiseValueCombinations(ArrayList<Integer> testCase, int strength, Set<ArrayList<Integer>> combinatorialSet, ArrayList<Integer> element, int count, int start) {

        if (start == testCase.size()) {
            if (strength == count)
                combinatorialSet.add(new ArrayList<>(element));
            return combinatorialSet;
        }

        for (int i = start; i < testCase.size(); i++) {
            if (count < strength) {
                if (element.size() > i) {
                    element.set(i, testCase.get(i));
                } else {
                    element.add(testCase.get(i));
                }
                combinatorialSet = getTWiseValueCombinations(testCase, strength, combinatorialSet, element, count + 1, i + 1);
            }
            if (element.size() > i) {
                element.set(i, -1);
            } else {
                element.add(-1);
            }
            combinatorialSet = getTWiseValueCombinations(testCase, strength, combinatorialSet, element, count, i + 1);
        }

        return combinatorialSet;

    }

//    public static void main(String[] args) {
//        Set<ArrayList<Integer>> combinatorialSet = new HashSet<>();
//        ArrayList<ArrayList<Integer>> testData = new ArrayList<>();
//        ArrayList<Integer> testCase = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
//        testData.add(testCase);
//        ArrayList<Integer> testCase2 = new ArrayList<>(Arrays.asList(2, 4, 5, 6));
//        testData.add(testCase2);
//        ArrayList<Integer> testCase3 = new ArrayList<>(Arrays.asList(2, 1, 5, 8));
//        testData.add(testCase3);
//
//        combinatorialSet = getCombinatorialSetForTestSuite(testData, 2);
//        return;
//    }

}
