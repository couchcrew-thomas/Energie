package de.melchernetz.energy.ui.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Diese Klasse ermöglicht es, dass UI Elemente in bestimmten Abständen ihren Inhalt aktualisieren.
 * Alle Kindklassen werden automatisch registriert damit nach dem Starten der Updateschleife deren
 * Update-Event Funktion aufgerufen werden kann.
 */
public abstract class UpdatingUIElement {

    //Türsteher für Registrierte Elemente
    private static final Object REGISTERED_ELEMENTS_LOCK = new Object();

    //Intervall in dem das UI erneuert wird.
    private static final int UI_UPDATE_INTERVAL = 100;

    private static boolean updating = false;
    private static Timer updateTimer;
    private static List<UpdatingUIElement> registered_elements = new ArrayList<>();

    /**
     * startet die updateschleife
     */
    public static void startUpdating() {
        if (updating) {
            return;
        }
        updating = true;
        //erzeuge einen neuen timer, der im Update-Interval den UpdateTask aufruft
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UiUpdateTask(), 0, UI_UPDATE_INTERVAL);
    }

    /**
     * beendet die updateschleife
     */
    public static void stopUpdating() {
        if (!updating) {
            return;
        }
        updateTimer.cancel();
        updating = false;
    }

    /**
     * default constructor
     */
    public UpdatingUIElement() {
        //registriere das erzeugte objekt
        synchronized (REGISTERED_ELEMENTS_LOCK) {
            registered_elements.add(this);
        }
    }

    /**
     * event funktion die auf den kindklassen implementiert werden muss
     */
    abstract public void onUIUpdate();

    /**
     * Dieser Task wird dem Timer übergeben und ruft die update methode bei jedem registrierten
     * objekt auf
     */
    private static class UiUpdateTask extends TimerTask {
        @Override
        public void run() {
            synchronized (REGISTERED_ELEMENTS_LOCK) {
                for (UpdatingUIElement element : registered_elements) {
                    element.onUIUpdate();
                }
            }
        }
    }
}
