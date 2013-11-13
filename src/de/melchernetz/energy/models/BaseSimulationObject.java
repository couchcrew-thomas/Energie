package de.melchernetz.energy.models;

import de.melchernetz.energy.simulation.SimulationObject;
import de.melchernetz.energy.simulation.SimulationStep;

/**
 * Das Grundobjekt für Simulationsobjekte in der Energie-Anwendung
 */
public abstract class BaseSimulationObject implements SimulationObject {

    //die simulierte Lebenszeit des Objektes
    protected long lifetime;

    protected BaseSimulationObject() {
        lifetime = -1;
    }

    public long getLifetime() {
        return lifetime;
    }

    /**
     * bei jedem Simulationsschritt wird die Lebenszeit erhöht
     */
    public void onSimulationStep(SimulationStep step) {
        //increase lifetime
        lifetime += step.getSimulationTimePassedSinceLastStep();
    }

    /**
     * wenn das Objekt zur Simulation hinzugefügt wird, hat es eine Lebenzeit von 0
     */
    public void onAddedToSimulation(long initialSimulationTime) {
        lifetime = 0;
    }
}
