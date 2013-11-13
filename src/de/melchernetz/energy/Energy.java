package de.melchernetz.energy;

import javax.swing.*;

import de.melchernetz.energy.models.EnergyProducer;
import de.melchernetz.energy.models.NuclearPowerplant;
import de.melchernetz.energy.models.SolarPowerplant;
import de.melchernetz.energy.models.WindTurbine;
import de.melchernetz.energy.simulation.Simulation;
import de.melchernetz.energy.simulation.SimulationObject;
import de.melchernetz.energy.simulation.SimulationStep;
import de.melchernetz.energy.simulation.SimulationStepListener;
import de.melchernetz.energy.ui.components.SimulationControls;
import de.melchernetz.energy.ui.components.SimulationDisplay;
import de.melchernetz.energy.ui.components.SimulationEnergyOutput;
import de.melchernetz.energy.ui.components.SimulationObjectControls;
import de.melchernetz.energy.ui.frames.MainFrame;
import de.melchernetz.energy.ui.logic.UpdatingUIElement;

/**
 * Die Hauptklasse des Programms. Hier werden die Simulationsergebnisse mit dem UI zusammengeführt.
 * Denn die Simulation und das UI laufen in unterschiedlichen Thread(s) um eine gute Performance zu
 * erreichen und damit die Simulation nicht logisch vom UI anhängig ist (zur besseren
 * Wiederverwendbarkeit)
 */
public class Energy implements SimulationStepListener, MainFrame.MainFrameOnCloseListener,
        SimulationControls.OnSimulationStateChangeListener,
        SimulationControls.OnSimulationSpeedChangeListener,
        SimulationObjectControls.OnAddSimulationObjectListener,
        SimulationEnergyOutput.EnergyOutputUpdateListener,
        SimulationDisplay.SimulationObjectDeletionIntentListener {

    //Türsteher, weil die globale Energieleistung in mehreren Threads benutt wird
    private static final Object GLOBAL_ENERGY_OUTPUT_LOCK = new Object();

    //Der Titel für's Programmfenster
    private static final String APPLICATION_NAME = "Energy Simulation";

    //Felder für Simulation und Fenster
    private MainFrame applicationWindow;
    private Simulation simulation;

    //hier drin wird die global erzeugte Energie gespeichert
    private int globalEnergyOutput = 0;

    /**
     * Programmstart methode
     *
     * @param args externe Argumente
     */
    public static void main(String[] args) {
        new Energy();
    }

    /**
     * default Constructur
     */
    public Energy() {
        //erstelle die Simulation und horche auf Simulationsschritte
        simulation = Simulation.getInstance();
        simulation.setSimulationStepListener(this);
        //erstelle Benutzeroberfläche
        create_ui();
    }

    /**
     * erstelle Benutzeroberfläche
     */
    private void create_ui() {

        //nutze einen Betriebssystem ähnlichen style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
            //normaler style wird benutzt
        }

        //erstelle das Hauptfenster
        applicationWindow = new MainFrame(APPLICATION_NAME, this);

        //horche auf UI Events
        SimulationControls simulationControls = applicationWindow.getSimulationControls();
        simulationControls.setOnSimulationStateChangeListener(this);
        simulationControls.setOnSimulationSpeedChangeListener(this);
        applicationWindow.getSimulationObjectControls().setOnAddSimulationObjectListener(this);
        applicationWindow.getSimulationEnergyOutputPanel().setEnergyOutputUpdateListener(this);
        applicationWindow.setMainFrameOnCloseListener(this);
    }

    /**
     * started die simulation
     */
    private void startSimulation() {
        //starte die UI update Threadschleife
        UpdatingUIElement.startUpdating();
        //starte die simulation mit der derzeitig eingestellten Geschwindigkeit
        int simulationTimeMultiplier = applicationWindow.getSimulationControls()
                .getSimulationTimeMultiplier();
        simulation.startSimulation(simulationTimeMultiplier);
    }

    /**
     * beendet die simulation, UI updates und löscht die vorhandenen simulationsobjekte
     */
    private void stopSimulation() {
        UpdatingUIElement.stopUpdating();
        applicationWindow.getSimulationDisplay().clearContent();
        simulation.stopSimulation();
    }

    /**
     * Wird nach jedem Simulationsschritt ausgeführt
     *
     * @param step der aktuelle Simulationsschritt
     */
    @Override
    public void onSimulationStep(SimulationStep step) {
        //berechne die globale energieleistung durch addition aller vorhandenen Energieerzeuger
        int energyOutput = 0;
        synchronized (Simulation.SIMULATION_OBJECTS_LOCK) {
            for (SimulationObject simulationObject : simulation.getSimulationObjects()) {
                if (simulationObject instanceof EnergyProducer) {
                    //erhöhe den Energieouput um die vom Erzeuger produzierte Energie
                    energyOutput += ((EnergyProducer) simulationObject).getEnergyOutput();
                }
            }
        }
        synchronized (GLOBAL_ENERGY_OUTPUT_LOCK) {
            globalEnergyOutput = energyOutput;
        }

        //aktualisiere die simulationsschritt anzeige
        //TODO: das sollte eigentlich im UI update thread gemacht werden. aber das hier ist ja nur zum testen
        applicationWindow.getSimulationControls().getLblSimSteps().setText(
                String.valueOf(simulation.getSimulationSteps()));
    }

    /**
     * beende die simulation wenn das fenster geschlossen wird
     */
    @Override
    public void onMainFrameClosed() {
        stopSimulation();
    }

    /**
     * wird ausgeführt, wenn im UI der Start/Stopp-Button gedrückt wird
     */
    @Override
    public void onSimulationStateChanged() {
        boolean sim_running = simulation.isRunning();
        if (sim_running) {
            stopSimulation();
        } else {
            startSimulation();
        }
        applicationWindow.getSimulationControls().setSimulationRunningState(!sim_running);
    }

    /**
     * setze die simulationsgeschwindigkeit, wenn sie im UI geändert wird
     * @param selectedSimSpeed die eingestellte geschwindigkeit
     */
    @Override
    public void onSimulationSpeedChanged(int selectedSimSpeed) {
        simulation.setSimulationTimeMultiplier(selectedSimSpeed);
    }

    /**
     * Wird ausgeführt, wenn im UI ein Energieerzeuger hinzugefügt werden soll
     * @param obj String name des hinzuzufügenden Objektes
     */
    @Override
    public void onAddSimulationObject(String obj) {
        if (!simulation.isRunning()) {
            return;
        }

        EnergyProducer energyProducer = null;
        switch (obj) {
            case "SolarPowerplant":
                energyProducer = new SolarPowerplant();
                break;
            case "NuclearPowerplant":
                energyProducer = new NuclearPowerplant();
                break;
            case "WindTurbine":
                energyProducer = new WindTurbine();
        }

        //das Objekt muss zur Simulation UND zum UI hinzugefügt werden
        simulation.addSimulationObject(energyProducer);
        applicationWindow.getSimulationDisplay().addSimulationObject(energyProducer);
    }

    /**
     * gibt die globale Energieleistung zurück. (muss synchronisiert werden)
     * @return globale Energieleistung
     */
    public int getGlobalEnergyOutput() {
        int out;
        synchronized (GLOBAL_ENERGY_OUTPUT_LOCK) {
            out = globalEnergyOutput;
        }
        return out;
    }

    /**
     * der UI Thread möchte das Feld der globalen Energieleistung aktualisieren
     */
    @Override
    public void onEnergyOutputPanelUpdateRequest() {
        applicationWindow.getSimulationEnergyOutputPanel().setEnergyOutput(getGlobalEnergyOutput());
    }

    /**
     * vom UI wird die Löschung eines Simulationsobjektes angefordert
     * @param simulationObject das zu löschende Objekt
     */
    @Override
    public void onSimulationObjectDeletionIntent(SimulationObject simulationObject) {
        simulation.removeSimulationObject(simulationObject);
    }
}
