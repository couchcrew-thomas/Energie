package de.melchernetz.energy.simulation;

/**
 * Ein Simulationsschritt speichert lediglich verschiedene Zeitangaben
 */
public class SimulationStep {

    //Die Startzeit der Simulation in Echtzeit
    private long simulationStartTime;

    //Die Zeit des Schrittes in simulierter Zeit
    private long stepSimulationTime;

    //Die Zeit des Schrittes in Echteit
    private long stepRealTime;

    //Die simulierte Zeit die seit dem letzten Schritt vergangen ist
    private int simulationTimePassedSinceLastStep;

    public SimulationStep(long stepSimulationTime, long simulationStartTime,
            int simulationTimePassedSinceLastStep) {
        this.stepSimulationTime = stepSimulationTime;
        this.simulationStartTime = simulationStartTime;
        this.simulationTimePassedSinceLastStep = simulationTimePassedSinceLastStep;
        this.stepRealTime = System.currentTimeMillis();
    }

    public long getStepSimulationTime() {
        return stepSimulationTime;
    }

    public long getSimulationStartTime() {
        return simulationStartTime;
    }

    public int getSimulationTimePassedSinceLastStep() {
        return simulationTimePassedSinceLastStep;
    }

    public long getStepRealTime() {
        return stepRealTime;
    }
}