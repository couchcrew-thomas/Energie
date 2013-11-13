package de.melchernetz.energy.models;

/**
 * Ein Solarkraftwerk
 */
public final class SolarPowerplant extends EnergyProducer {

    private static final int PREPARATION_TIME = 1000 * 60 * 60 * 1; //1h
    private static final int ENERGY_PRODUCTION = 150; //150 MW
    private static final String NAME = "Solarkraftwerk";

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
