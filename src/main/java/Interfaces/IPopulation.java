package Interfaces;

import java.util.ArrayList;

public interface IPopulation<IndividualType extends IIndividual<?,?>> {

    ArrayList<IndividualType> getIndividuals();

    IndividualType getFittest();

    void setPopulationFitness(double fitness);

    double getPopulationFitness();

    int size();

    void setIndividual(int offset, IndividualType individual);

    IndividualType getIndividual(int offset);

    void shuffle();

}
