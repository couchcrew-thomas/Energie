package de.melchernetz.energy.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Komponente für die Simulationssteuerungen
 */
public class SimulationControls implements ActionListener {

    private JPanel root;
    private JButton btnToggleSimulationState;
    private JRadioButton simSpeed0;
    private JRadioButton simSpeed2;
    private JRadioButton simSpeed60;
    private JRadioButton simSpeed1800;
    private JRadioButton simSpeed3600;
    private JLabel lblSimSteps;
    private JRadioButton simSpeed1;
    private OnSimulationSpeedChangeListener onSimulationSpeedChangeListener;
    private OnSimulationStateChangeListener onSimulationStateChangeListener;

    public SimulationControls() {
        //setze Event-Empfänger für Geschwindigkeit-Buttons
        simSpeed0.addActionListener(this);
        simSpeed1.addActionListener(this);
        simSpeed2.addActionListener(this);
        simSpeed60.addActionListener(this);
        simSpeed1800.addActionListener(this);
        simSpeed3600.addActionListener(this);

        //Event-Empfänger für Start/Stopp
        btnToggleSimulationState.addActionListener(this);
    }

    public JPanel getRoot() {
        return root;
    }

    /**
     * Eventfunktion die ausgeführt wird, wenn irgendeine Schaltfläche der SimulationControls
     * geklickt wird
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JRadioButton) {
            //die geschwindigkeit wurde geändert
            int selectedSimSpeed = Integer.valueOf(e.getActionCommand());
            if (onSimulationSpeedChangeListener != null) {
                onSimulationSpeedChangeListener.onSimulationSpeedChanged(selectedSimSpeed);
            }
        } else if (e.getSource() == btnToggleSimulationState) {
            //die sinulation wurde angehalten oder gestartet
            if (onSimulationStateChangeListener != null) {
                onSimulationStateChangeListener.onSimulationStateChanged();
            }
        }
    }

    /**
     * findet heraus, welche Simulationsgeschwindigkeit zur Zeit eingestellt ist
     *
     * @return Die Geschwindigkeit
     */
    public int getSimulationTimeMultiplier() {
        JRadioButton[] buttons = {simSpeed0, simSpeed1, simSpeed2, simSpeed60, simSpeed1800,
                simSpeed3600};
        int result = 1;
        for (JRadioButton button : buttons) {
            if (button.isSelected()) {
                result = Integer.parseInt(button.getActionCommand());
                break;
            }
        }
        return result;
    }

    public void setOnSimulationSpeedChangeListener(
            OnSimulationSpeedChangeListener onSimulationSpeedChangeListener) {
        this.onSimulationSpeedChangeListener = onSimulationSpeedChangeListener;
    }

    public JLabel getLblSimSteps() {
        return lblSimSteps;
    }

    public void setSimulationRunningState(boolean is_running) {
        btnToggleSimulationState
                .setText("Simulation " + (is_running ? "stoppen" : "starten"));
    }

    public void setOnSimulationStateChangeListener(
            OnSimulationStateChangeListener onSimulationStateChangeListener) {
        this.onSimulationStateChangeListener = onSimulationStateChangeListener;
    }

    public static interface OnSimulationSpeedChangeListener {

        void onSimulationSpeedChanged(int selectedSimSpeed);
    }

    public static interface OnSimulationStateChangeListener {

        void onSimulationStateChanged();
    }
}
