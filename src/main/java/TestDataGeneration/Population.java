package TestDataGeneration;

import Interfaces.IPopulation;

import java.util.ArrayList;
import java.util.Random;

public class Population implements IPopulation<Individual> {


    private ArrayList<Individual> population;

    private double populationFitness = -1;

    public Population(Population population) {
        // Initial population
        this.population = population.getIndividuals();
        this.populationFitness = population.getPopulationFitness();
    }

    public Population(int populationSize, ArrayList<ArrayList<Integer>> T) {
        // Initialize the population as an array of individuals
        this.population = new ArrayList<>();

        // Create each individual in turn
        for (int individualCount = 0; individualCount < populationSize; individualCount++) {
            // Create an individual, initializing its chromosome to the given
            // length
            Individual individual = new Individual(T);
            // Add individual to population
            this.population.add(individual);
        }
    }

    @Override
    public ArrayList<Individual> getIndividuals() {
        return this.population;
    }

    @Override
    public Individual getFittest() {

        Individual fittest = null;
        double bestFitness = -1;

        for (Individual individual : this.population) {
            if (individual.getFitness() > bestFitness) {
                bestFitness = individual.getFitness();
                fittest = individual;
            } else if (individual.getFitness() == bestFitness) {
                assert fittest != null;
                if (individual.getChromosomeLength() < fittest.getChromosomeLength()) {
                    bestFitness = individual.getFitness();
                    fittest = individual;
                }
            }
        }

        return fittest;

    }

    @Override
    public void setPopulationFitness(double fitness) {
        this.populationFitness = fitness;
    }

    @Override
    public double getPopulationFitness() {
        return this.populationFitness;
    }

    @Override
    public int size() {
        return this.population.size();
    }

    @Override
    public void setIndividual(int offset, Individual individual) {
        this.population.set(offset, individual);

    }

    @Override
    public Individual getIndividual(int offset) {
        return this.population.get(offset);
    }

    @Override
    public void shuffle() {
        Random rnd = new Random();
        for (int i = population.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Individual temp = this.population.get(index);
            this.population.set(index, this.population.get(i));
            this.population.set(i, temp);
        }
    }
}
