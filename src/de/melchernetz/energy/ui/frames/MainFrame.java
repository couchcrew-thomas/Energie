package de.melchernetz.energy.ui.frames;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import de.melchernetz.energy.ui.components.SimulationControls;
import de.melchernetz.energy.ui.components.SimulationDisplay;
import de.melchernetz.energy.ui.components.SimulationEnergyOutput;
import de.melchernetz.energy.ui.components.SimulationObjectControls;

/**
 * UI Klasse des Hauptfensters
 */
public class MainFrame implements WindowListener {

    private JPanel root;
    private JPanel leftSideBarContainer;
    private JPanel contentContainer;
    private JPanel rightSideBarContainer;
    private JPanel bottomBarContainer;
    private SimulationControls simulationControls;

    private MainFrameOnCloseListener mainFrameOnCloseListener;
    private SimulationDisplay simulationDisplay;
    private SimulationObjectControls simulationObjectControls;
    private SimulationEnergyOutput simulationEnergyOutputPanel;
    private SimulationDisplay.SimulationObjectDeletionIntentListener
            simulationObjectDeletionIntentListener;

    /**
     * Erstelle das Fenster
     *
     * @param applicationName der Fenster Titel
     * @param simulationObjectDeletionIntentListener
     *                        Der Event Empfänger für den Fall, dass ein Simulationsobjekt gelöscht
     *                        werden soll
     */
    public MainFrame(String applicationName,
            SimulationDisplay.SimulationObjectDeletionIntentListener simulationObjectDeletionIntentListener) {
        this.simulationObjectDeletionIntentListener = simulationObjectDeletionIntentListener;
        JFrame frame = new JFrame(applicationName);
        frame.setContentPane(this.getRoot());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);

        Dimension dimension = new Dimension();
        dimension.setSize(800, 600);
        frame.setMinimumSize(dimension);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param mainFrameOnCloseListener Event-Empfänger für's schließen vom Fenster
     */
    public void setMainFrameOnCloseListener(
            MainFrameOnCloseListener mainFrameOnCloseListener) {
        this.mainFrameOnCloseListener = mainFrameOnCloseListener;
    }

    /**
     * @return gibt das grundelement des UI-Baums zurück
     */
    public JPanel getRoot() {
        return root;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        //interessiert keinen
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //interessiert keinen
    }

    /**
     * gibt das Fenster-Schließen Event weiter
     *
     * @param e das Event
     */
    @Override
    public void windowClosed(WindowEvent e) {
        if (mainFrameOnCloseListener != null) {
            mainFrameOnCloseListener.onMainFrameClosed();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //interessiert keinen
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //interessiert keinen
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //interessiert keinen
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //interessiert keinen
    }

    //folgende Methoden geben die einelnen Komponenten zurück
    public SimulationControls getSimulationControls() {
        return simulationControls;
    }

    public SimulationDisplay getSimulationDisplay() {
        return simulationDisplay;
    }

    public SimulationObjectControls getSimulationObjectControls() {
        return simulationObjectControls;
    }

    public SimulationEnergyOutput getSimulationEnergyOutputPanel() {
        return simulationEnergyOutputPanel;
    }

    /**
     * Instanziere die einzelnen Komponenten und integriere sie im Fenster
     */
    private void createUIComponents() {
        //Die Simulations Steuerung (Start, Stop, Geschwindigkeit)
        simulationControls = new SimulationControls();
        leftSideBarContainer = simulationControls.getRoot();

        //Die Anzeige der Simulationselemente
        simulationDisplay = new SimulationDisplay(simulationObjectDeletionIntentListener);
        contentContainer = simulationDisplay.getRoot();

        //Das Panel zum erstellen von neuen Energieerzeugern
        simulationObjectControls = new SimulationObjectControls();
        rightSideBarContainer = simulationObjectControls.getRoot();

        //Die Anzeige der global erzeugten Energie
        simulationEnergyOutputPanel = new SimulationEnergyOutput();
        bottomBarContainer = simulationEnergyOutputPanel.getRoot();
    }

    /**
     * Wrapper Klasse für das Fenster-Schließen-Event, weil die restlichen Fenster Events unwichtig
     * sind
     */
    public static interface MainFrameOnCloseListener {

        public void onMainFrameClosed();
    }
}
