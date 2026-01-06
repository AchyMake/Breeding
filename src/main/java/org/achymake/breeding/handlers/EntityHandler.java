package org.achymake.breeding.handlers;

import org.achymake.breeding.Breeding;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;

import java.util.*;
import java.util.stream.Collectors;

public class EntityHandler {
    private Breeding getInstance() {
        return Breeding.getInstance();
    }
    private FileConfiguration getConfig() {
        return getInstance().getConfig();
    }
    private RandomHandler getRandomHandler() {
        return getInstance().getRandomHandler();
    }
    public EntityType get(String entityType) {
        return EntityType.fromName(entityType.toUpperCase());
    }
    public boolean isHorseType(LivingEntity livingEntity) {
        if (livingEntity.getType().equals(get("horse"))) {
            return true;
        } else if (livingEntity.getType().equals(get("donkey"))) {
            return true;
        } else return livingEntity.getType().equals(get("mule"));
    }
    public double getAttributeValue(LivingEntity livingEntity, Attribute attribute) {
        var value = livingEntity.getAttribute(attribute);
        if (value != null) {
            return value.getBaseValue();
        } else return 0.0;
    }
    public AttributeInstance getAttribute(LivingEntity livingEntity, Attribute attribute) {
        return livingEntity.getAttribute(attribute);
    }
    public void copyAttackDamage(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
        var babyAttribute = getAttribute(baby, Attribute.ATTACK_DAMAGE);
        if (babyAttribute != null) {
            var dadValue = getAttributeValue(dad, Attribute.ATTACK_DAMAGE);
            var momValue = getAttributeValue(mom, Attribute.ATTACK_DAMAGE);
            if (dadValue > momValue) {
                babyAttribute.setBaseValue(getRandomHandler().nextDouble(momValue, dadValue));
            } else if (momValue > dadValue) {
                babyAttribute.setBaseValue(getRandomHandler().nextDouble(dadValue, momValue));
            }
        }
    }
    public void copyHealth(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
        var babyAttribute = getAttribute(baby, Attribute.MAX_HEALTH);
        if (babyAttribute != null) {
            var dadValue = getAttributeValue(dad, Attribute.MAX_HEALTH);
            var momValue = getAttributeValue(mom, Attribute.MAX_HEALTH);
            if (dadValue > momValue) {
                babyAttribute.setBaseValue(getRandomHandler().nextDouble(momValue, dadValue));
            } else if (momValue > dadValue) {
                babyAttribute.setBaseValue(getRandomHandler().nextDouble(dadValue, momValue));
            }
        }
    }
    public void copyScale(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
        var babyAttribute = getAttribute(baby, Attribute.SCALE);
        if (babyAttribute != null) {
            var dadValue = getAttributeValue(dad, Attribute.SCALE);
            var momValue = getAttributeValue(mom, Attribute.SCALE);
            if (dadValue > momValue) {
                babyAttribute.setBaseValue(getRandomHandler().nextDouble(momValue, dadValue));
            } else if (momValue > dadValue) {
                babyAttribute.setBaseValue(getRandomHandler().nextDouble(dadValue, momValue));
            }
        }
    }
    public void copyStats(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
        copyAttackDamage(baby, dad, mom);
        copyScale(baby, dad, mom);
        if (!isHorseType(baby)) {
            copyHealth(baby, dad, mom);
        }
    }
    private Set<Map.Entry<String, Double>> getChances(LivingEntity livingEntity) {
        var levels = new HashMap<String, Double>();
        var entityType = livingEntity.getType();
        getConfig().getConfigurationSection("entity." + entityType + ".chances").getKeys(false).forEach(key -> levels.put(key, getConfig().getDouble("entity." + entityType + ".chances." + key + ".chance")));
        var list = new ArrayList<>(levels.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        var result = new LinkedHashMap<String, Double>();
        result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for (var entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result.entrySet();
    }
    public boolean isEnable(LivingEntity livingEntity) {
        return getConfig().getBoolean("entity." + livingEntity.getType() + ".enable");
    }
    public boolean setStats(LivingEntity livingEntity) {
        var entityType = livingEntity.getType();
        if (isEnable(livingEntity)) {
            var chances = getChances(livingEntity);
            if (!chances.isEmpty()) {
                chances.forEach(listed -> {
                    var chance = listed.getValue();
                    if (getRandomHandler().isTrue(chance)) {
                        var key = listed.getKey();
                        var section = "entity." + entityType + ".chances." + key;
                        if (getConfig().isDouble(section + ".attack_damage")) {
                            livingEntity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(getConfig().getDouble(section + ".attack_damage"));
                        } else if (getConfig().isDouble(section + ".attack_damage.min") && getConfig().isDouble(section + ".attack_damage.max")) {
                            livingEntity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(getRandomHandler().nextDouble(getConfig().getDouble(section + ".attack_damage.min"), getConfig().getDouble(section + ".attack_damage.max")));
                        }
                        if (getConfig().isDouble(section + ".health")) {
                            var health = getConfig().getDouble(section + ".health");
                            livingEntity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
                            livingEntity.setHealth(health);
                        } else if (getConfig().isDouble(section + ".health.min") && getConfig().isDouble(section + ".health.max")) {
                            var healthMin = getConfig().getDouble(section + ".health.min");
                            var healthMax = getConfig().getDouble(section + ".health.max");
                            var result = getRandomHandler().nextDouble(healthMin, healthMax);
                            livingEntity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(result);
                            livingEntity.setHealth(result);
                        }
                        if (getConfig().isDouble(section + ".scale")) {
                            livingEntity.getAttribute(Attribute.SCALE).setBaseValue(getConfig().getDouble(section + ".scale"));
                        } else if (getConfig().isDouble(section + ".scale.min") && getConfig().isDouble(section + ".scale.max")) {
                            livingEntity.getAttribute(Attribute.SCALE).setBaseValue(getRandomHandler().nextDouble(getConfig().getDouble(section + ".scale.min"), getConfig().getDouble(section + ".scale.max")));
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }
}