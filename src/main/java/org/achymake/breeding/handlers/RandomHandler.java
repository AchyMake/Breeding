package org.achymake.breeding.handlers;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomHandler {
    public Random getRandom() {
        return new Random();
    }
    private double getRandomDouble() {
        return nextDouble(0, 1);
    }
    public boolean isTrue(double chance) {
        return chance >= getRandomDouble();
    }
    public double nextDouble(double origin, double bound) {
        return getRandom().nextDouble(origin, bound);
    }
    public String format(double value) {
        return new DecimalFormat("#,##0.00").format(value).replace(",", ".");
    }
    public double makeRandom(double value) {
        return Double.parseDouble(format(nextDouble(value - value / 4, value + value / 6)));
    }
    public double makeRandomBig(double value) {
        return Double.parseDouble(format(nextDouble(value - value / 2, value + value / 2)));
    }
}