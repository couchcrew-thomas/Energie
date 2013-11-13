package de.melchernetz.energy.models;

import de.melchernetz.energy.simulation.SimulationStep;

/**
 * Ein abstrakter Energieereuger erzeugt nach einer bestimmten Vorbereitungszeit Energie.
 */
public abstract class EnergyProducer extends BaseSimulationObject {

    //jedes objekt bekommt eine einigartige ID (für schnellere Listenoperationen)
    private static int energy_producer_id_counter = 0;

    //Stati eines Erzeugers
    public static enum STATES {
        VOID, PREPARING, PRODUCING
    }

    private int id;
    private STATES currentState;

    /**
     * constructor: setze id und startstatus
     */
    protected EnergyProducer() {
        id = ++energy_producer_id_counter;
        currentState = STATES.VOID;
    }

    //////
    // Folgende methoden müssen von Subklassen implementiert werden.

    /**
     * @return die Vorbereitungszeit die der Erzeuger benötigt
     */
    abstract public int getPreparationTime();

    /**
     * @return die erzeugbare Energieleistung
     */
    abstract public int getEnergyProductionValue();

    /**
     * @return der Name des Erzeugers
     */
    abstract public String getDisplayName();
    //
    /////////////////////////////

    /**
     * gibt die erzeugte Energie zurück, nachdem die vorbereitungszeit verstrichen ist
     *
     * @return die derzeitig produzierte Energie
     */
    public final int getEnergyOutput() {
        return currentState == STATES.PRODUCING ? getEnergyProductionValue() : 0;
    }

    public final STATES getCurrentState() {
        return currentState;
    }

    /**
     * wenn der Erzeuger zur Simulation hinzugefügt wird, wird beginnt er mit seiner Vorbereitung
     */
    @Override
    public final void onAddedToSimulation(long initialSimulationTime) {
        super.onAddedToSimulation(initialSimulationTime);
        currentState = STATES.PREPARING;
    }

    /**
     * bei jedem Simulationsschritt wird überprüft, ob der Erzeuger seine Vorbereitungseit
     * überschritten hat
     */
    @Override
    public final void onSimulationStep(SimulationStep step) {
        super.onSimulationStep(step);
        if (currentState != STATES.VOID) {
            //ist der erzeuger fertig mit der Vorbereitung?
            if (currentState == STATES.PREPARING && lifetime > getPreparationTime()) {
                //starte mit der Energieerzeugung
                currentState = STATES.PRODUCING;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return new Integer(id).hashCode();
    }
}
