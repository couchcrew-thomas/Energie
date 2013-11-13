package de.melchernetz.energy.ui.components.model_components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import de.melchernetz.energy.models.EnergyProducer;

/**
 * Komponente für einen Energieerzeuger
 */
public class EnergyProducerComponent implements ActionListener {

    private JPanel root;
    private JLabel name;
    private JLabel state;
    private JProgressBar state_progress;
    private JLabel lblEnergyOutput;
    private JButton deleteButton;
    private EnergyProducer energyProducer;
    private EnergyProducerDeletionIntentListener energyProducerDeletionIntentListener;

    public EnergyProducerComponent(de.melchernetz.energy.models.EnergyProducer simulationObject) {
        energyProducer = simulationObject;
        state_progress.setMinimum(0);
        state_progress.setMaximum(100);

        deleteButton.addActionListener(this);

        updateContent();
    }

    /**
     * setze den Inhalt der Komponente anhand des EnergyProducers
     */
    public void updateContent() {
        //setze namen, status, erzeugte energie
        name.setText(energyProducer.getDisplayName());
        lblEnergyOutput.setText(String.format("%,d MW", energyProducer.getEnergyOutput()));

        switch (energyProducer.getCurrentState()) {
            case PREPARING:
                state.setText("VORBEREITEN");
                break;
            case PRODUCING:
                state.setText("PRODUZIEREN");
        }

        //berechne die fortschrittsanzeige
        int progress;
        long currentTime = energyProducer.getLifetime();
        EnergyProducer.STATES lifeState = energyProducer
                .getCurrentState();
        if (currentTime == 0
                || lifeState == EnergyProducer.STATES.VOID) {
            progress = 0;
        } else if (lifeState == EnergyProducer.STATES.PRODUCING) {
            progress = 100;
        } else {
            long targetTime = energyProducer.getPreparationTime();
            progress = (int) ((double) currentTime / (double) targetTime * 100.0);
        }
        state_progress.setValue(progress);
    }

    public JPanel getRoot() {
        return root;
    }

    public EnergyProducer getEnergyProducer() {
        return energyProducer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (energyProducerDeletionIntentListener != null) {
            //sende das lösch event
            energyProducerDeletionIntentListener.onEnergyProducerDeletionIntent(this);
        }
    }

    public void setEnergyProducerDeletionIntentListener(
            EnergyProducerDeletionIntentListener energyProducerDeletionIntentListener) {
        this.energyProducerDeletionIntentListener = energyProducerDeletionIntentListener;
    }

    public interface EnergyProducerDeletionIntentListener {

        void onEnergyProducerDeletionIntent(EnergyProducerComponent producerComponent);
    }
}
