package org.achymake.breeding.listeners;

import org.achymake.breeding.Breeding;
import org.achymake.breeding.handlers.EntityHandler;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.plugin.PluginManager;

public class EntityTransform implements Listener {
    private Breeding getInstance() {
        return Breeding.getInstance();
    }
    private EntityHandler getEntityHandler() {
        return getInstance().getEntityHandler();
    }
    private PluginManager getPluginManager() {
        return getInstance().getPluginManager();
    }
    public EntityTransform() {
        getPluginManager().registerEvents(this, getInstance());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTransform(EntityTransformEvent event) {
        var transformReason = event.getTransformReason();
        var before = event.getEntity();
        var after = event.getTransformedEntity();
        if (transformReason.equals(EntityTransformEvent.TransformReason.INFECTION)) {
            if (before instanceof Villager villager && after instanceof ZombieVillager zombieVillager) {
                getEntityHandler().copyStats(villager, zombieVillager);
            } else if (before instanceof Villager villager && after instanceof Zombie zombie) {
                getEntityHandler().copyStats(villager, zombie);
            }
        } else if (transformReason.equals(EntityTransformEvent.TransformReason.CURED)) {
            if (before instanceof ZombieVillager zombieVillager && after instanceof Villager villager) {
                getEntityHandler().copyStats(zombieVillager, villager);
            } else if (before instanceof Zombie zombie && after instanceof Villager villager) {
                getEntityHandler().copyStats(zombie, villager);
            }
        }
    }
}