package de.melchernetz.energy.ui.components;

import javax.swing.*;

import de.melchernetz.energy.ui.logic.UpdatingUIElement;

/**
 * Komponente, die die global erzeugte Energie anzeigt
 */
public class SimulationEnergyOutput extends UpdatingUIElement {

    private JPanel root;
    private JLabel lblOutput;
    private EnergyOutputUpdateListener energyOutputUpdateListener;

    public SimulationEnergyOutput() {
        super();
    }

    public JPanel getRoot() {
        return root;
    }

    /**
     * wenn das UI aktualisiert werden soll, wird ein Event gesendet. Denn das UI weiß nichts von
     * der global erzeugten Energie. Die Berechnung muss also anderswo geschehen.
     */
    @Override
    public void onUIUpdate() {
        if (energyOutputUpdateListener != null) {
            energyOutputUpdateListener.onEnergyOutputPanelUpdateRequest();
        }
    }

    /**
     * formatiere die Energieleistung und zeige sie an
     *
     * @param energyOutput die anzuzeigende Energieleistung
     */
    public void setEnergyOutput(int energyOutput) {
        lblOutput.setText(String.format("%,d MW", energyOutput));
    }

    /**
     * setze den Event-Empfänger für's Aktualisieren der UI
     */
    public void setEnergyOutputUpdateListener(
            EnergyOutputUpdateListener energyOutputUpdateListener) {
        this.energyOutputUpdateListener = energyOutputUpdateListener;
    }

    public interface EnergyOutputUpdateListener {

        void onEnergyOutputPanelUpdateRequest();
    }
}
