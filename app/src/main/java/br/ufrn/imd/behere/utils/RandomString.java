package br.ufrn.imd.behere.utils;

import java.util.Locale;
import java.util.Random;

/**
 * Created by elton on 06/12/17.
 */

public class RandomString {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;

    private final int length;
    private final Random random;
    private final char[] symbols;

    public RandomString(int length, String symbols) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }
        if (symbols.length() < 2) {
            throw new IllegalArgumentException();
        }
        this.length = length;
        this.random = new Random();
        this.symbols = symbols.toCharArray();
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length) {
        this(length, ALPHANUM);
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < length; ++idx) {
            sb.append(symbols[random.nextInt(symbols.length)]);
        }
        return sb.toString();
    }
}
