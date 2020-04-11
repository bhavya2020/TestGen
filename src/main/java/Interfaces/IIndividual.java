package Interfaces;

import java.util.ArrayList;

public interface IIndividual {

    Object chromosome = null;

    double fitness = -1;

    ArrayList<ArrayList<Integer>> getChromosome();

    int getChromosomeLength();

    void setGene(int offset, ArrayList<Integer> gene);

    ArrayList<Integer> getGene(int offset);

    void setFitness(double fitness);

    double getFitness();

    String toString();

    void changeGene(int indexOfGeneWithMinimumDistinctPairs, int parameterIndex, Integer missingValue);
}

