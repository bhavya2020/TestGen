package Interfaces;

import TestDataGeneration.Population;

public interface IGeneticAlgorithm<populationType extends IPopulation<? extends IIndividual<?,?>>, individualType extends IIndividual<?,?>> {


    populationType initPopulation();

    double calcFitness(individualType individual);

    void evalPopulation(populationType population);

    boolean isTerminationConditionMet(populationType population);

    individualType selectParent(populationType population);

    populationType crossoverPopulation(populationType population);

    populationType mutatePopulation(populationType population);

}