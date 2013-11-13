package de.melchernetz.energy.models;

/**
 * Ein Windrad
 */
public final class WindTurbine extends EnergyProducer {

    private static final int PREPARATION_TIME = 1000 * 60 * 30; //30min
    private static final int ENERGY_PRODUCTION = 6; //6KW
    private static final String NAME = "Windrad";

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
