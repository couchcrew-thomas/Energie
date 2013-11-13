package de.melchernetz.energy.simulation;

/**
 * Interface eines Simulationsobjektes
 */
public interface SimulationObject {

    /**
     * wird ausgeführt, sobald ein Simulationsschritt berechnet wurde
     * @param step der aktuelle Simulationsschritt
     */
    public void onSimulationStep(SimulationStep step);

    /**
     * wird ausgeführt, wenn das Objekt zur Simulation hinzugefügt wurde
     * @param initialSimulationTime die Simulationszeit, zu der das Objekt hinugefügt wurde
     */
    public void onAddedToSimulation(long initialSimulationTime);
}
