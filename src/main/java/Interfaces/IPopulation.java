package Interfaces;

import java.util.ArrayList;

public interface IPopulation {

    IIndividual[] population = new IIndividual[0];

    double populationFitness = -1;

    ArrayList<IIndividual> getIndividuals();

    IIndividual getFittest();

    void setPopulationFitness(double fitness);

    double getPopulationFitness();

    int size();

    IIndividual setIndividual(int offset, IIndividual individual);

    IIndividual getIndividual(int offset);

    void shuffle();

}
