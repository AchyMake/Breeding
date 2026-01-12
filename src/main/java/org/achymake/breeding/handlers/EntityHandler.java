package org.achymake.breeding.handlers;

import org.achymake.breeding.Breeding;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public boolean isEnable(LivingEntity livingEntity) {
        return getConfig().getBoolean("entity." + livingEntity.getType() + ".enable");
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
    public void randomizeStats(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
        randomizeAttackDamage(baby, dad, mom);
        randomizeScale(baby, dad, mom);
        if (!isHorseType(baby)) {
            randomizeHealth(baby, dad, mom);
        }
    }
    public void randomizeAttackDamage(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
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
    public void randomizeHealth(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
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
    public void randomizeScale(LivingEntity baby, LivingEntity dad, LivingEntity mom) {
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
        public void copyStats(LivingEntity before, LivingEntity after) {
            copyAttackDamage(before, after);
            copyScale(before, after);
            if (!isHorseType(after)) {
                copyHealth(before, after);
            }
        }
        public void copyAttackDamage(LivingEntity before, LivingEntity after) {
            var afterAttribute = getAttribute(after, Attribute.ATTACK_DAMAGE);
            if (afterAttribute != null) {
                var beforeValue = getAttributeValue(before, Attribute.ATTACK_DAMAGE);
                if (beforeValue > 0) {
                    afterAttribute.setBaseValue(beforeValue);
                }
            }
        }
        public void copyHealth(LivingEntity before, LivingEntity after) {
            var afterAttribute = getAttribute(after, Attribute.MAX_HEALTH);
            if (afterAttribute != null) {
                var beforeValue = getAttributeValue(before, Attribute.MAX_HEALTH);
                if (beforeValue > 0) {
                    afterAttribute.setBaseValue(beforeValue);
                }
            }
        }
        public void copyScale(LivingEntity before, LivingEntity after) {
            var afterAttribute = getAttribute(after, Attribute.SCALE);
            if (afterAttribute != null) {
                var beforeValue = getAttributeValue(before, Attribute.SCALE);
                if (beforeValue > 0) {
                    afterAttribute.setBaseValue(beforeValue);
                }
            }
        }
    private List<Map.Entry<String, Double>> getChances(LivingEntity livingEntity) {
        var levels = new HashMap<String, Double>();
        var entityType = livingEntity.getType();
        var section = getConfig().getConfigurationSection("entity." + entityType + ".chances");
        if (section != null) {
            for (var key : section.getKeys(false)) {
                var chance = section.getDouble(key + ".chance");
                if (chance > 0) {
                    levels.put(key, chance);
                }
            }
        }
        var listed = new ArrayList<>(levels.entrySet());
        listed.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        return listed.stream().toList();
    }
    public boolean setStats(LivingEntity livingEntity) {
        var entityType = livingEntity.getType();
        if (isEnable(livingEntity)) {
            var chances = getChances(livingEntity);
            if (!chances.isEmpty()) {
                for (var listed : chances) {
                    var chance = listed.getValue();
                    if (getRandomHandler().isTrue(chance)) {
                        var key = listed.getKey();
                        var section = "entity." + entityType + ".chances." + key;
                        if (getConfig().isDouble(section + ".attack_damage")) {
                            getAttribute(livingEntity, Attribute.ATTACK_DAMAGE).setBaseValue(getConfig().getDouble(section + ".attack_damage"));
                        } else if (getConfig().isDouble(section + ".attack_damage.min") && getConfig().isDouble(section + ".attack_damage.max")) {
                            getAttribute(livingEntity, Attribute.ATTACK_DAMAGE).setBaseValue(getRandomHandler().nextDouble(getConfig().getDouble(section + ".attack_damage.min"), getConfig().getDouble(section + ".attack_damage.max")));
                        }
                        if (getConfig().isDouble(section + ".health")) {
                            var health = getConfig().getDouble(section + ".health");
                            getAttribute(livingEntity, Attribute.MAX_HEALTH).setBaseValue(health);
                            livingEntity.setHealth(health);
                        } else if (getConfig().isDouble(section + ".health.min") && getConfig().isDouble(section + ".health.max")) {
                            var healthMin = getConfig().getDouble(section + ".health.min");
                            var healthMax = getConfig().getDouble(section + ".health.max");
                            var result = getRandomHandler().nextDouble(healthMin, healthMax);
                            getAttribute(livingEntity, Attribute.MAX_HEALTH).setBaseValue(result);
                            livingEntity.setHealth(result);
                        }
                        if (getConfig().isDouble(section + ".scale")) {
                            getAttribute(livingEntity, Attribute.SCALE).setBaseValue(getConfig().getDouble(section + ".scale"));
                        } else if (getConfig().isDouble(section + ".scale.min") && getConfig().isDouble(section + ".scale.max")) {
                            getAttribute(livingEntity, Attribute.SCALE).setBaseValue(getRandomHandler().nextDouble(getConfig().getDouble(section + ".scale.min"), getConfig().getDouble(section + ".scale.max")));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}