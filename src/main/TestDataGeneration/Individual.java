package TestDataGeneration;

import Interfaces.IIndividual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Individual implements IIndividual {

    private ArrayList<ArrayList<Integer>> chromosome;
    private double fitness=-1;

    Random rand = new Random();

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


    public Individual(IIndividual individual){
        this.chromosome = individual.getChromosome();
        this.fitness = individual.getFitness();
    }

    // T is set of all possible configurations
    public Individual(ArrayList<ArrayList<Integer>> T) {

        this.chromosome = new ArrayList<ArrayList<Integer>>();

        int chromosomeLength = rand.nextInt(T.size())+1;

        Set<Integer> SelectedIndices = new HashSet<>();

        for(int i=0;i<chromosomeLength;i++){
            int randomIndex = rand.nextInt(T.size());
            if(SelectedIndices.contains(randomIndex)){
                i--;
                continue;
            }
            SelectedIndices.add(randomIndex);
        }

        for(int selectedIndex : SelectedIndices){
            this.chromosome.add(T.get(selectedIndex));
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
        this.chromosome.set(offset,gene);
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

    @Override
    public void changeGene(int indexOfGeneWithMinimumDistinctPairs, int parameterIndex, Integer missingValue) {
        ArrayList<Integer> gene = getGene(indexOfGeneWithMinimumDistinctPairs);
        gene.set(parameterIndex,missingValue);
    }
}
