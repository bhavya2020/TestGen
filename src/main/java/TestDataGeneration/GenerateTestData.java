package TestDataGeneration;
import java.util.ArrayList;

public class GenerateTestData {


    public static ArrayList<ArrayList<Integer>> generateTestData(ArrayList<ArrayList<Integer>> attributes) {

        GeneticAlgorithm ga = new GeneticAlgorithm(200, 0.05, 0.95, 50, attributes );

        // Initialize population
        Population population = ga.initPopulation();


        ga.evalPopulation(population);

        // Keep track of current generation
        int generation = 1;

        while (generation < 10000) {

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
        }

        return  population.getFittest().getChromosome();

    }

}


