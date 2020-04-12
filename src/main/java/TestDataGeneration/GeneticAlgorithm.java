package TestDataGeneration;

import Interfaces.IGeneticAlgorithm;

import utils.Pair;

import java.util.*;


public class GeneticAlgorithm implements IGeneticAlgorithm<Population, Individual> {


    private int populationSize;

    private double mutationRate;

    private double crossoverRate;

    private int elitismCount;

    private ArrayList<ArrayList<Integer>> attributes;

    private ArrayList<ArrayList<Integer>> T;

    private int totalDistinctPairs;

    private int totalRepetitivePairs;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount, ArrayList<ArrayList<Integer>> attributes) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.attributes = attributes;
    }

    @Override
    public Population initPopulation() {

        ArrayList<ArrayList<Integer>> T = new ArrayList<>();

        ArrayList<Integer> TestSet = new ArrayList<>();

        T = createAllTestCases(attributes, T, 0, TestSet);

        Pair<Integer, Integer> P = getAllDistinctPairs(T);
        this.totalDistinctPairs = P.getKey();
        this.totalRepetitivePairs = P.getValue();

        return new Population(populationSize, T);
    }

    @Override
    public double calcFitness(Individual individual) {

        Set<ArrayList<Integer>> distinctPairs = new HashSet<>();
        int repetitivePairs = 0;
        for (ArrayList<Integer> gene : individual.getChromosome()) {

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
        double factor1;
        if (totalDistinctPairs == 0) {
            if(distinctPairs.size()  == 1)
                factor1 = 1;
            else
                factor1 = 0;
        } else {
            factor1 = distinctPairs.size() / (double) totalDistinctPairs;
        }
        double factor2;
        if (totalRepetitivePairs == 0) {
            if(repetitivePairs == 0)
                factor2 = 1;
            else
                factor2 = 0;
        } else {
            factor2 = 1 - (repetitivePairs / (double) totalRepetitivePairs);
        }

        return 0.70 * factor1 + 0.30 * factor2;
    }

    @Override
    public void evalPopulation(Population population) {

        double populationFitness = 0;

        int IndividualIndex = 0;
        for (Individual individual : population.getIndividuals()) {

            double fitness = calcFitness(individual);
            individual.setFitness(fitness);
            population.setIndividual(IndividualIndex, individual);
            populationFitness += fitness;
            IndividualIndex++;
        }

        population.setPopulationFitness(populationFitness);

    }

    @Override
    public boolean isTerminationConditionMet(Population population) {

        return population.getFittest().getFitness() >= 0.75;
    }

    @Override
    public Individual selectParent(Population population) {

        // Get individuals
        ArrayList<Individual> individuals = population.getIndividuals();

        // Spin roulette wheel
        double populationFitness = population.getPopulationFitness();
        double rouletteWheelPosition = Math.random() * populationFitness;

        // Find parent
        double spinWheel = 0;
        for (Individual individual : individuals) {
            spinWheel += individual.getFitness();
            if (spinWheel >= rouletteWheelPosition) {
                return individual;
            }
        }
        return individuals.get(population.size() - 1);
    }

    @Override
    public Population crossoverPopulation(Population population) {
        // Create new population
        Population newPopulation = new Population(population);

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {

            //selecting parent based by roulette
            Individual parent1 = selectParent(population);

            Random rand = new Random();

            // Apply crossover to this individual?
            if (this.crossoverRate > (double) rand.nextInt(100) / 100 && populationIndex >= this.elitismCount) {

                // Find second parent
                Individual parent2 = selectParent(population);

                if (parent1 == parent2) {
                    populationIndex--;
                    continue;
                }

                int crossoverPoint = rand.nextInt(Math.min(parent1.getChromosomeLength(), parent2.getChromosomeLength()));

                Individual offspring1 = new Individual(parent2);

                Individual offspring2 = new Individual(parent1);

                for (int i = 0; i <= crossoverPoint; i++) {
                    offspring1.setGene(i, parent1.getGene(i));
                    offspring2.setGene(i, parent2.getGene(i));
                }

                Individual[] fitnessArray = {offspring1, offspring2, parent1, parent2};

                Individual fittest = null;
                double bestFitness = -1;

                for (Individual individual : fitnessArray) {
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
                newPopulation.setIndividual(populationIndex, fittest);
            } else {
                newPopulation.setIndividual(populationIndex, parent1);
            }
        }

        return newPopulation;
    }

    @Override
    public Population mutatePopulation(Population population) {

        int individualIndex = 0;

        for (Individual individual : population.getIndividuals()) {

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
