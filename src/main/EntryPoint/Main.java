package EntryPoint;

import Interfaces.IGeneticAlgorithm;
import Interfaces.IIndividual;
import Interfaces.IPopulation;
import TestDataGeneration.GeneticAlgorithm;
import utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {

    /*
        attributes = [
                      parameter a=> [1,2],
                      parameter b=> [1,2],
                      parameter c=> [1,2]
                  ]

     */

    /*
        T = [
                [1,1,1],
                [1,1,2],
                [1,2,1],
                [1,2,2],
                [2,1,1],
                [2,1,2],
                [2,2,1],
                [2,2,2]
            ]

       c1 = [                       o1 = [
               [1,2,2],                     [1,2,2],
               [1,1,2]                      [1,1,2],
                                            [1,2,1]
            ]                            ]
       c2 = [                       o2 = [
                [1,1,1],                    [1,1,1],
                [2,2,2],                    [2,2,2]
                [1,2,1]                ]
            ]
     */


    public static void main(String[] argv) {

        // Create GA object
        IGeneticAlgorithm ga = new GeneticAlgorithm(500, 0.05, 0.95, 50);


        ArrayList<ArrayList<Integer>> T = new ArrayList<>();

        ArrayList<ArrayList<Integer>> attributes = new ArrayList<>();
        attributes.add(new ArrayList<>(Arrays.asList(1, 2)));
        attributes.add(new ArrayList<>(Arrays.asList(1, 2, 3)));
        attributes.add(new ArrayList<>(Arrays.asList(1, 2, 3, 4)));

        ArrayList<Integer> TestSet = new ArrayList<>();
        T = createAllTestCases(attributes, T, 0, TestSet);
        // Initialize population
        IPopulation population = ga.initPopulation(T);

        Pair<Integer, Integer> P = getAllDistinctPairs(T);
        // Evaluate population
        int distinctPairs = P.getKey();
        int repetitivePairs = P.getValue();
        ga.evalPopulation(population, distinctPairs, repetitivePairs);

        // Keep track of current generation
        int generation = 1;

        /**
         * Start the evolution loop
         *
         * Every genetic algorithm problem has different criteria for finishing.
         * In this case, we know what a perfect solution looks like (we don't
         * always!), so our isTerminationConditionMet method is very
         * straightforward: if there's a member of the population whose
         * chromosome is all ones, we're done!
         */
        while (generation < 10000) {

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population, attributes);

            // Evaluate population
            ga.evalPopulation(population, distinctPairs, repetitivePairs);

            // Increment the current generation
            generation++;
        }

        /**
         * We're out of the loop now, which means we have a perfect solution on
         * our hands. Let's print it out to confirm that it is actually all
         * ones, as promised.
         */
        System.out.println("Found solution in " + generation + " generations");
        IIndividual fittest = population.getFittest();
        for (ArrayList<Integer> gene : fittest.getChromosome()) {
            System.out.println(gene);
        }
        System.out.println("Best solution Fitness: " + fittest.getFitness());
    }

    private static ArrayList<ArrayList<Integer>> createAllTestCases(ArrayList<ArrayList<Integer>> attributes, ArrayList<ArrayList<Integer>> T, int index, ArrayList<Integer> TestSet) {

        if (index == attributes.size()) {
            T.add(new ArrayList<>(TestSet));
            return T;
        }

        for (int i = 0; i < attributes.get(index).size(); i++) {
            if (TestSet.size() >= index + 1) {
                TestSet.set(index, attributes.get(index).get(i));
            } else {
                TestSet.add(attributes.get(index).get(i));
            }
            T = createAllTestCases(attributes, T, index + 1, TestSet);

        }

        return T;
    }

    private static Pair<Integer, Integer> getAllDistinctPairs(ArrayList<ArrayList<Integer>> T) {
        Set<ArrayList<Integer>> distinctPairs = new HashSet<>();

        int repetitivePairs = 0;
        for (ArrayList<Integer> gene : T) {

            for (int i = 0; i < gene.size() - 1; i++) {
                for (int j = i + 1; j < gene.size(); j++) {
                    ArrayList<Integer> distinctPair = new ArrayList<>();
                    for (int k = 0; k < gene.size(); k++) {
                        distinctPair.add(k == i ? gene.get(i) : k == j ? gene.get(j) : -1);
                    }
                    if (distinctPairs.contains(distinctPair))
                        repetitivePairs++;
                    distinctPairs.add(distinctPair);
                }
            }

        }

        return new Pair<>(distinctPairs.size(), repetitivePairs);
    }

}

