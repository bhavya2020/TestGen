package TestDataGeneration;

import utils.Pair;

import java.util.ArrayList;

public class GenerateTestData {

    public static Pair<ArrayList<ArrayList<Integer>>, Double> generateTestData(ArrayList<ArrayList<Integer>> attributes, double requiredFitness) {

        GeneticAlgorithm ga = new GeneticAlgorithm(200, 0.05, 0.95, 0, attributes, requiredFitness);

        // Initialize population
        Population population = ga.initPopulation();


        ga.evalPopulation(population);

        // Keep track of current generation
        int generation = 1;

        while (!ga.isTerminationConditionMet(population)) {

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
        }

        return new Pair<>(population.getFittest().getChromosome(), population.getFittest().getFitness());

    }

}


