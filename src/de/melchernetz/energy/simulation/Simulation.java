package de.melchernetz.energy.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Die Simulations Klasse
 *
 * Wenn eine Simulation läuft, werden hintereinander Simulationsschritte ausgeführt, bis die
 * Simulation wirder beendet wird. Diese Schritte enthalten die Simulationszeit, die je nach der
 * Simulationsgeschwindigkeit berechnet wird. Der Simulation zugewiesene Simulationsobjekte bekommen
 * dann pro Simulationsschritt den aktuellen Schritt zugesandt. Jeder Simulationsschritt wird in
 * einem extra Thread ausgeführt um eine hohe Performance zu erzielen. Sobald ein Schritt ausgeführt
 * wurde, wird nach einer kleinen Pause der nächste Schritt gestartet.
 */
public class Simulation {

    //die Simulation ist ein Singleton!
    private static Simulation instance = null;

    public static Simulation getInstance() {
        if (instance == null) {
            instance = new Simulation();
        }
        return instance;
    }

    //Türsteher für die Simulationsobjekte
    public final static Object SIMULATION_OBJECTS_LOCK = new Object();

    //Die Pause in Millisekunden, die zwischen zwei Schritten gewartet wird.
    //Je kleiner die Pause ist, umso genauer ist die Simulation bei steigender Rechenanforderung
    private final static int SIMULATION_STEP_DELAY = 10;

    //Zeitfaktor um für die Simulationsgeschwindigkeit
    private int simulationTimeMultiplier;

    //Simulations Startzeit
    private long simulationStartTimestamp;

    //derzeite Simulationszeit
    private long currentSimulationTime;

    //letzter Simulationsschritt
    private SimulationStep lastStep = null;

    //Event Empfänger für die Schrittänderung
    private SimulationStepListener simulationStepListener = null;

    //ob die simulation läuft
    private boolean running = false;

    //anzahl der bisher berechneten Schritte
    private int simulationSteps = 0;

    //die Liste der Simulationsobjekte
    private List<SimulationObject> simulationObjects = new ArrayList<>();

    //diesem Sercive werden die Schritt-Tasks gegeben
    private ExecutorService executorService;

    //der Timer für die Pause zwischen zwei Schritten
    private Timer simulationStepDelayTimer;

    /**
     * PRIVATE constructor (Simulation ist ein Singleton)
     */
    private Simulation() {
        simulationTimeMultiplier = 1;
    }

    /**
     * startet die simulation
     *
     * @param timeSpeed der initiale zeit multiplikator
     */
    public void startSimulation(int timeSpeed) {
        if (running) {
            return;
        }
        //Schritte werden sequentiell berechnet, nie parallel => singleThreadExecutor
        executorService = Executors.newSingleThreadExecutor();
        simulationStepDelayTimer = new Timer();
        running = true;
        simulationSteps = 0;
        this.simulationTimeMultiplier = timeSpeed;

        //erstelle den initialen Simulationsschritt
        simulationStartTimestamp = System.currentTimeMillis();
        currentSimulationTime = simulationStartTimestamp;
        SimulationStep startStep = new SimulationStep(simulationStartTimestamp,
                simulationStartTimestamp, 0);

        //starte die simulation mit dem simulationsschritt
        doSimulationStep(startStep);
    }

    /**
     * beende die simulation
     */
    public void stopSimulation() {
        if (!running) {
            return;
        }
        running = false;

        //beende threads
        simulationStepDelayTimer.cancel();
        executorService.shutdown();

        //lösche Simulationsobjekte
        synchronized (SIMULATION_OBJECTS_LOCK) {
            simulationObjects.clear();
        }
    }

    /**
     * setze die Simulationsgeschwindigkeit
     *
     * @param simulationTimeMultiplier die neue Geschwindigkeit
     */
    public void setSimulationTimeMultiplier(int simulationTimeMultiplier) {
        this.simulationTimeMultiplier = simulationTimeMultiplier;
    }

    public long getCurrentSimulationTime() {
        return currentSimulationTime;
    }

    public void setSimulationStepListener(SimulationStepListener simulationStepListener) {
        this.simulationStepListener = simulationStepListener;
    }

    /**
     * berechne einen neuen Simulationsschritt relativ zum letzten
     */
    private void doSimulationStep() {
        //die Zeitdifferenz zum letzten Schritt in Echtzeit
        int realTimeDifference = (int) (System.currentTimeMillis() - lastStep.getStepRealTime());

        //die Zeitdifferenz in simulierter Zeit ist die Echtzeit * Zeitfaktor
        int simulationTimeDifference = realTimeDifference * simulationTimeMultiplier;

        //die derzeitige Simulationszeit wird erhöht
        currentSimulationTime += simulationTimeDifference;

        //ein neuer simulationsschritt wird aus den berechneten daten erstellt
        SimulationStep step = new SimulationStep(currentSimulationTime, simulationStartTimestamp,
                simulationTimeDifference);

        //setzte die Simulation mit dem neuen Schritt fort
        doSimulationStep(step);
    }

    /**
     * sende einen Simulationsschritt und plane den nächsten
     *
     * @param step der aktuelle Schritt
     */
    private void doSimulationStep(final SimulationStep step) {
        //erhöhe Schrittanzahl und speichere den aktuellen Schritt
        ++simulationSteps;
        lastStep = step;
        //übergebe die neue Schrittberechnung dem ExecutorService, um den Schritt in einem neuen
        //Thread zu berechnen
        executorService.submit(new CallbackRunnable(new Runnable() {
            /**
             * Task des Schrittes
             */
            @Override
            public void run() {
                //sende den Simulationsschritt an alle Simulationsobjekte
                synchronized (SIMULATION_OBJECTS_LOCK) {
                    for (final SimulationObject object : simulationObjects) {
                        object.onSimulationStep(step);
                    }
                }
                //sende den Simulationsschritt an den Event-Empfänger
                if (simulationStepListener != null) {
                    simulationStepListener.onSimulationStep(step);
                }
            }
        }, new CallbackRunnable.Callback() {
            /**
             * der aktuelle Schritt wurde berechnet
             */
            @Override
            public void complete() {
                if (!running) {
                    return;
                }
                //starte den nächsten Schritt nach einer kurzen Pause
                simulationStepDelayTimer.schedule(new SimulationTimerTask(), SIMULATION_STEP_DELAY);
            }
        }
        ));
    }

    public int getSimulationSteps() {
        return simulationSteps;
    }

    public List<SimulationObject> getSimulationObjects() {
        return simulationObjects;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * füge ein neues Simulationsobjekt hinzu
     */
    public void addSimulationObject(SimulationObject simulationObject) {
        synchronized (SIMULATION_OBJECTS_LOCK) {
            simulationObjects.add(simulationObject);
            simulationObject.onAddedToSimulation(getCurrentSimulationTime());
        }
    }

    /**
     * entferne ein Simulationsobjekt
     */
    public void removeSimulationObject(SimulationObject simulationObject) {
        synchronized (SIMULATION_OBJECTS_LOCK) {
            simulationObjects.remove(simulationObject);
        }
    }

    /**
     * Der TimerTask der einen neuen Simulationsschritt nach der Timer Pause ausführt
     */
    private class SimulationTimerTask extends TimerTask {

        @Override
        public void run() {
            //calculate current simulation time
            doSimulationStep();
        }
    }
}
