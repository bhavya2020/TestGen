package Interfaces;

public interface IIndividual<ChromosomeType, GeneType> {

    ChromosomeType getChromosome();

    int getChromosomeLength();

    void setGene(int offset,GeneType gene);

    GeneType getGene(int offset);

    void setFitness(double fitness);

    double getFitness();

    String toString();

}

