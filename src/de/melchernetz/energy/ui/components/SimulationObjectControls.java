package de.melchernetz.energy.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Komponent für's erzeugen neuer Elemente
 */
public class SimulationObjectControls implements ActionListener {

    private JPanel root;
    private JButton btnAddSolarPowerplant;
    private JButton btnAddWindTurbine;
    private JButton btnAddNuclearPowerplant;
    private OnAddSimulationObjectListener onAddSimulationObjectListener;

    public SimulationObjectControls() {
        btnAddSolarPowerplant.addActionListener(this);
        btnAddNuclearPowerplant.addActionListener(this);
        btnAddWindTurbine.addActionListener(this);
    }

    public JPanel getRoot() {
        return root;
    }

    /**
     * wenn auf einen Button geklickt wurde, wird der ActionCommand weitergegeben. Dieser String
     * wird dann in der Energy Klasse interpretiert.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (onAddSimulationObjectListener != null) {
            onAddSimulationObjectListener.onAddSimulationObject(e.getActionCommand());
        }
    }

    public void setOnAddSimulationObjectListener(
            OnAddSimulationObjectListener onAddSimulationObjectListener) {
        this.onAddSimulationObjectListener = onAddSimulationObjectListener;
    }

    public interface OnAddSimulationObjectListener {
        void onAddSimulationObject(String obj);
    }
}
