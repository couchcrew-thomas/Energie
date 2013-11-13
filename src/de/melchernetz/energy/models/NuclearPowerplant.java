package de.melchernetz.energy.models;

/**
 * Ein Atomkraftwerk
 */
public final class NuclearPowerplant extends EnergyProducer {

    private static final int PREPARATION_TIME = 1000 * 60 * 60 * 3; //3h
    private static final int ENERGY_PRODUCTION = 1500; //1500 MW
    private static final String NAME = "Atomkraftwerk";

    @Override
    public int getPreparationTime() {
        return PREPARATION_TIME;
    }

    @Override
    public int getEnergyProductionValue() {
        return ENERGY_PRODUCTION;
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }
}
