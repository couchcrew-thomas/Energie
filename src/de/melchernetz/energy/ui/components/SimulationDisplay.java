package de.melchernetz.energy.ui.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import de.melchernetz.energy.models.EnergyProducer;
import de.melchernetz.energy.simulation.SimulationObject;
import de.melchernetz.energy.ui.components.model_components.EnergyProducerComponent;
import de.melchernetz.energy.ui.logic.UpdatingUIElement;

/**
 * Komponente für die erstellten Energieerzeuger (ist nicht an eine FORM Datei gebunden, weil der
 * GUI Editor das BoxLayout nicht unterstützt)
 */
public class SimulationDisplay extends UpdatingUIElement
        implements EnergyProducerComponent.EnergyProducerDeletionIntentListener {

    private JPanel root;
    private List<EnergyProducerComponent> childComponents = new ArrayList<>();
    private SimulationObjectDeletionIntentListener simulationObjectDeletionIntentListener;

    public SimulationDisplay(
            SimulationObjectDeletionIntentListener simulationObjectDeletionIntentListener) {
        this.simulationObjectDeletionIntentListener = simulationObjectDeletionIntentListener;
        //erstelle das Grundelement und weise das Layout zu
        root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
    }

    public JPanel getRoot() {
        return root;
    }

    /**
     * füge ein Simulationsobjekt hinzu
     */
    public void addSimulationObject(SimulationObject simulationObject) {
        //je nach typ des Simulationsobjektes kann hier unterschieden werden welche Komponente
        // hinzugefügt wird. EnergyProducer haben aber alle die gleiche Komponente
        if (simulationObject instanceof EnergyProducer) {
            EnergyProducerComponent
                    component = new EnergyProducerComponent((EnergyProducer) simulationObject);
            component.setEnergyProducerDeletionIntentListener(this);
            childComponents.add(component);
            root.add(component.getRoot());
        }
    }

    /**
     * lösche alle Kindelemente
     */
    public void clearContent() {
        childComponents.clear();
        JPanel root = getRoot();
        root.removeAll();
        root.revalidate();
        root.repaint();
    }

    /**
     * bei jedem UI Update Event wird der Inhalt der Kindelemente neu berechnet
     */
    @Override
    public void onUIUpdate() {
        for (EnergyProducerComponent component : childComponents) {
            component.updateContent();
        }
    }

    /**
     * Wenn vom UI die Anforderung kommt, ein EnergieProducer zu löschen, muss auch das zugehörige
     * Element gelöscht werden
     * @param producerComponent die zu Löschende Komponente
     */
    @Override
    public void onEnergyProducerDeletionIntent(EnergyProducerComponent producerComponent) {
        //entferne die Komponente
        root.remove(producerComponent.getRoot());
        childComponents.remove(producerComponent);
        //leite das Lösch-Event weiter (damit das Objekt auch aus der Simulation entfernt wird)
        simulationObjectDeletionIntentListener
                .onSimulationObjectDeletionIntent(producerComponent.getEnergyProducer());
        root.revalidate();
        root.repaint();
    }

    public interface SimulationObjectDeletionIntentListener {

        void onSimulationObjectDeletionIntent(SimulationObject simulationObject);
    }
}
