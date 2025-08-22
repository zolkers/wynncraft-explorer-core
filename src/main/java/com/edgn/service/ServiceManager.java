package com.edgn.service;

import com.edgn.service.services.ChatService;
import com.edgn.service.services.RaidEventService;
import com.edgn.service.services.SpellEventService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceManager {
    private static ServiceManager instance;
    private final List<IService> services = new ArrayList<>();

    private ServiceManager() {
    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public List<IService> all() { return Collections.unmodifiableList(services); }

    public void add(IService service, boolean alreadyBooted) {
        services.add(service);
        if (alreadyBooted) service.init();
    }

    @SuppressWarnings("unchecked")
    public <T extends IService> T getService(Class<T> serviceClass) {
        for (IService service : services) {
            if( serviceClass.isInstance(service)) return (T) service;
        }
        return null;
    }

    public void register() {
        services.add(new RaidEventService());
        services.add(new SpellEventService());
        services.add(new ChatService());

        for(IService service : services) service.init();
    }
}
