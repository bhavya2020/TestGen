package TestDataGeneration;

import Interfaces.IIndividual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Individual implements IIndividual<ArrayList<ArrayList<Integer>>, ArrayList<Integer>> {

    private ArrayList<ArrayList<Integer>> chromosome;

    private double fitness = -1;

    public Individual(Individual individual) {
        this.chromosome = new ArrayList<>(individual.getChromosome());
        this.fitness = individual.getFitness();
    }

    // T is set of all possible configurations
    public Individual(ArrayList<ArrayList<Integer>> T, int lowerBound, int upperBound) {

        this.chromosome = new ArrayList<>();

        Random rand = new Random();
        int chromosomeLength = rand.nextInt(upperBound - lowerBound) + lowerBound ;

        Set<Integer> SelectedIndices = new HashSet<>();

        for (int i = 0; i < chromosomeLength; i++) {
            int randomIndex = rand.nextInt(T.size());
            if (SelectedIndices.contains(randomIndex)) {
                i--;
                continue;
            }
            SelectedIndices.add(randomIndex);
        }

        for (int selectedIndex : SelectedIndices) {
            this.chromosome.add(new ArrayList<>(T.get(selectedIndex)));
        }
    }

    @Override
    public ArrayList<ArrayList<Integer>> getChromosome() {
        return this.chromosome;
    }

    @Override
    public int getChromosomeLength() {
        return this.chromosome.size();
    }


    @Override
    public void setGene(int offset, ArrayList<Integer> gene) {
        this.chromosome.set(offset, gene);
    }

    @Override
    public ArrayList<Integer> getGene(int offset) {
        return this.chromosome.get(offset);
    }

    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public double getFitness() {
        return this.fitness;
    }

    public void changeGene(int indexOfGeneWithMinimumDistinctPairs, int parameterIndex, Integer missingValue) {
        ArrayList<Integer> gene = getGene(indexOfGeneWithMinimumDistinctPairs);
        gene.set(parameterIndex, missingValue);
    }
}
