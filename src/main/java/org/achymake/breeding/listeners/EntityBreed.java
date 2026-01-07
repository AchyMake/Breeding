package org.achymake.breeding.listeners;

import org.achymake.breeding.Breeding;
import org.achymake.breeding.handlers.EntityHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.plugin.PluginManager;

public class EntityBreed implements Listener {
    private Breeding getInstance() {
        return Breeding.getInstance();
    }
    private EntityHandler getEntityHandler() {
        return getInstance().getEntityHandler();
    }
    private PluginManager getPluginManager() {
        return getInstance().getPluginManager();
    }
    public EntityBreed() {
        getPluginManager().registerEvents(this, getInstance());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBreed(EntityBreedEvent event) {
        if (getEntityHandler().setStats(event.getEntity()))return;
        getEntityHandler().randomizeStats(event.getEntity(), event.getFather(), event.getMother());
    }
}