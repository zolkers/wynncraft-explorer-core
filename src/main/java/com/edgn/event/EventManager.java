package com.edgn.event;



import com.edgn.Main;
import com.edgn.exceptions.EventException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public final class EventManager {
    private final HashMap<Class<? extends Listener>, ArrayList<? extends Listener>> listenerMap =
            new HashMap<>();


    public static <L extends Listener, E extends Event<L>> void fire(E event) {
        EventManager eventManager = Main.EVENT_MANAGER;
        eventManager.fireImpl(event);
    }

    private <L extends Listener, E extends Event<L>> void fireImpl(E event) {
        try {
            Class<L> type = event.getListenerType();
            @SuppressWarnings("unchecked")
            ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

            if (listeners == null || listeners.isEmpty())
                return;

            ArrayList<L> listeners2 = new ArrayList<>(listeners);

            listeners2.removeIf(Objects::isNull);

            event.fire(listeners2);

        } catch (Throwable e) {

            CrashReport report = CrashReport.create(e, "Firing WYNNCRAFT EXPLORER event");
            CrashReportSection section = report.addElement("Affected event");
            section.add("Event class", () -> event.getClass().getName());

            throw new EventException(report.toString() + report.getCause());
        }
    }

    public <L extends Listener> void add(Class<L> type, L listener) {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

            if (listeners == null) {
                listeners = new ArrayList<>(Collections.singletonList(listener));
                listenerMap.put(type, listeners);
                return;
            }

            listeners.add(listener);

        } catch (Throwable e) {

            CrashReport report =
                    CrashReport.create(e, "Adding WYNNCRAFT EXPLORER event listener");
            CrashReportSection section = report.addElement("Affected listener");
            section.add("Listener type", type::getName);
            section.add("Listener class", () -> listener.getClass().getName());

            throw new EventException(report.toString());
        }
    }

    public <L extends Listener> void remove(Class<L> type, L listener) {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<L> listeners = (ArrayList<L>) listenerMap.get(type);

            if (listeners != null)
                listeners.remove(listener);

        } catch (Throwable e) {

            CrashReport report =
                    CrashReport.create(e, "Removing WYNNCRAFT EXPLORER event listener");
            CrashReportSection section = report.addElement("Affected listener");
            section.add("Listener type", type::getName);
            section.add("Listener class", () -> listener.getClass().getName());

            throw new EventException(report.toString());
        }
    }
}
