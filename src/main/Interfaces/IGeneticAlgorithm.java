package Interfaces;

import java.util.ArrayList;

public interface IGeneticAlgorithm {

    int populationSize = 0;

    double mutationRate = 0;

    double crossoverRate = 0;

    int elitismCount = 0;

    IPopulation initPopulation(ArrayList<ArrayList<Integer>> T);

    double calcFitness(IIndividual individual, int totalDistinctPairs,int totalRepetitivePairs);

    void evalPopulation(IPopulation population, int totalDistinctPairs,int totalRepetitivePairs);

    boolean isTerminationConditionMet(IPopulation population, int size);

    IIndividual selectParent(IPopulation population);

    IPopulation crossoverPopulation(IPopulation population);

    IPopulation mutatePopulation(IPopulation population, ArrayList<ArrayList<Integer>> attributes);

}