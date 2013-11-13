package de.melchernetz.energy.simulation;

/**
 * Event-Empfänger für das Simulationsschritt-Ereignis der Simulation
 */
public interface SimulationStepListener {

    /**
     * wird ausgeführt, sobald ein neuer Simulationsschritt berechnet wurde
     * @param step der neue Schritt
     */
    void onSimulationStep(SimulationStep step);
}
