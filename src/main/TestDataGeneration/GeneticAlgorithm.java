package TestDataGeneration;

import Interfaces.IGeneticAlgorithm;
import Interfaces.IIndividual;
import Interfaces.IPopulation;
import utils.Pair;

import java.util.*;


public class GeneticAlgorithm implements IGeneticAlgorithm {


    private int populationSize;

    private double mutationRate;

    private double crossoverRate;

    private int elitismCount;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
    }

    @Override
    public IPopulation initPopulation(ArrayList<ArrayList<Integer>> T) {

        IPopulation population = new Population(populationSize, T);
        return population;
    }

    @Override
    public double calcFitness(IIndividual individual, int totalDistinctPairs, int totalRepetitvePairs) {

        Set<ArrayList<Integer>> distinctPairs = new HashSet<>();
        int repetitivePairs = 0;
        for (ArrayList<Integer> gene : individual.getChromosome()) {

            for (int i = 0; i < gene.size() - 1; i++) {
                for (int j = i + 1; j < gene.size(); j++) {
                    ArrayList<Integer> distinctPair = new ArrayList<>();
                    for (int k = 0; k < gene.size(); k++) {
                        distinctPair.add( k == i ? gene.get(i) : k == j ? gene.get(j) : -1);
                    }
                    if(distinctPairs.contains(distinctPair))
                        repetitivePairs++;
                    distinctPairs.add(distinctPair);
                }
            }

        }
        double factor1 =  distinctPairs.size() / (double) totalDistinctPairs;
        double factor2 =  1 - (repetitivePairs / (double) totalRepetitvePairs);

        return 0.70*factor1 + 0.30*factor2 ;
    }

    @Override
    public void evalPopulation(IPopulation population, int totalDistinctPairs, int totalRepetitivePairs) {

        double populationFitness = 0;

        // Loop over population evaluating individuals and suming population
        // fitness

        int IndividualIndex = 0;
        for (IIndividual individual : population.getIndividuals()) {

            double fitness = calcFitness(individual,totalDistinctPairs,totalRepetitivePairs);
            individual.setFitness(fitness);
            population.setIndividual(IndividualIndex,individual);
            populationFitness += fitness;
            IndividualIndex++;
        }

        population.setPopulationFitness(populationFitness);

    }

    @Override
    public boolean isTerminationConditionMet(IPopulation population,int size ) {

        return population.getFittest().getFitness() == 1;
//        population.getFittest().getChromosome().size() < 0.75 * size;
    }

    @Override
    public IIndividual selectParent(IPopulation population) {

        // Get individuals
        ArrayList<IIndividual> individuals = population.getIndividuals();

        // Spin roulette wheel
        double populationFitness = population.getPopulationFitness();
        double rouletteWheelPosition = Math.random() * populationFitness;

        // Find parent
        double spinWheel = 0;
        for (IIndividual individual : individuals) {
            spinWheel += individual.getFitness();
            if (spinWheel >= rouletteWheelPosition) {
                return individual;
            }
        }
        return individuals.get(population.size() - 1);
    }

    @Override
    public IPopulation crossoverPopulation(IPopulation population) {
        // Create new population
        IPopulation newPopulation = new Population(population);

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {

            //selecting parent based by roulette
            IIndividual parent1 = selectParent(population);

            // Apply crossover to this individual?
            if (this.crossoverRate > Math.random() && populationIndex >= this.elitismCount) {

                // Find second parent
                IIndividual parent2 = selectParent(population);

                if (parent1 == parent2) {
                    populationIndex--;
                    continue;
                }


                Random rand = new Random();

                int crossoverPoint = rand.nextInt(Math.min(parent1.getChromosomeLength(), parent2.getChromosomeLength()));

                IIndividual offspring1 = new Individual(parent2);

                IIndividual offspring2 = new Individual(parent1);

                for (int i = 0; i <= crossoverPoint; i++) {
                    offspring1.setGene(i, parent1.getGene(i));
                    offspring2.setGene(i, parent2.getGene(i));
                }

                IIndividual[] fitnessArray = {offspring1, offspring2, parent1, parent2};

                IIndividual fittest = null;
                double bestFitness = -1;

                for (IIndividual individual : fitnessArray) {
                    if (individual.getFitness() > bestFitness) {
                        bestFitness = individual.getFitness();
                        fittest = individual;
                    } else if (individual.getFitness() == bestFitness) {
                        if (individual.getChromosomeLength() < fittest.getChromosomeLength()) {
                            bestFitness = individual.getFitness();
                            fittest = individual;
                        }
                    }
                }

                // Add fittest of parents and off springs  to new population
                newPopulation.setIndividual(populationIndex, fittest);
            } else {
                // Add individual to new population without applying crossover
                newPopulation.setIndividual(populationIndex, parent1);
            }
        }

        return newPopulation;
    }

        /*
        attributes = [
                      parameter a=> [1,2],
                      parameter b=> [1,2],
                      parameter c=> [1,2]
                  ]

     */

    @Override
    public IPopulation mutatePopulation(IPopulation population, ArrayList<ArrayList<Integer>> attributes) {

        int individualIndex = 0;

        for (IIndividual individual : population.getIndividuals()) {

            int parameterIndex = 0;
            for (ArrayList<Integer> parameter : attributes) {

                ArrayList<Integer> missingValues = new ArrayList<>();

                for (Integer value : parameter) {
                    boolean found = false;
                    for (ArrayList<Integer> gene : individual.getChromosome()) {
                        if (gene.get(parameterIndex).equals(value)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missingValues.add(value);
                    }
                }

                for (Integer missingValue : missingValues) {

                    Map<Integer, Integer> OccurrencesOfValues = new HashMap<>();
                    for (ArrayList<Integer> gene : individual.getChromosome()) {

                        Integer value = gene.get(parameterIndex);
                        if (OccurrencesOfValues.containsKey(value)) {
                            OccurrencesOfValues.replace(value, OccurrencesOfValues.get(value) + 1);
                        } else {
                            OccurrencesOfValues.put(value, 1);
                        }
                    }

                    int maximumOccurrence = 0;
                    int maximumValue = missingValue;

                    for (Map.Entry<Integer, Integer> value : OccurrencesOfValues.entrySet()) {
                        if (value.getValue() > maximumOccurrence) {
                            maximumValue = value.getKey();
                        }
                    }

                    Map<Pair<Integer, Integer>, Integer> distinctPairs = new HashMap<>();

                    for (ArrayList<Integer> gene : individual.getChromosome()) {
                        if (gene.get(parameterIndex).equals(maximumValue)) {
                            for (int i = 0; i < gene.size(); i++) {
                                if (i != parameterIndex) {
                                    Pair<Integer, Integer> newPair = new Pair<>(maximumValue, gene.get(i));
                                    if (distinctPairs.containsKey(newPair))
                                        distinctPairs.put(newPair, distinctPairs.get(newPair) + 1);
                                    else {
                                        distinctPairs.put(newPair, 1);
                                    }
                                }
                            }
                        }
                    }

                    int minimumDistinctPairs = attributes.size() - 1;

                    int IndexOfGeneWithMinimumDistinctPairs = 0;

                    int geneIndex = 0;

                    for (ArrayList<Integer> gene : individual.getChromosome()) {
                        if (gene.get(parameterIndex).equals(maximumValue)) {
                            int distinctPairsOfGene = 0;
                            for (int i = 0; i < gene.size(); i++) {
                                if (i != parameterIndex) {
                                    Pair<Integer, Integer> newPair = new Pair<>(maximumValue, gene.get(i));
                                    if (distinctPairs.get(newPair) == 1) {
                                        distinctPairsOfGene++;
                                    }
                                }
                            }
                            if (distinctPairsOfGene < minimumDistinctPairs) {
                                minimumDistinctPairs = distinctPairsOfGene;
                                IndexOfGeneWithMinimumDistinctPairs = geneIndex;
                            }

                        }
                        geneIndex++;
                    }

                    individual.changeGene(IndexOfGeneWithMinimumDistinctPairs, parameterIndex, missingValue);
                    population.setIndividual(individualIndex, individual);

                }

                parameterIndex++;
            }

            individualIndex++;
        }

        return population;
    }
}
