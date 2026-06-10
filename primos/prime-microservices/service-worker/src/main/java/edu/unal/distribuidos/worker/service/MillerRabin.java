package edu.unal.distribuidos.worker.service;

import java.math.BigInteger;
import java.security.SecureRandom;

public class MillerRabin {
    private static final SecureRandom random = new SecureRandom();

    /**
     * Miller-Rabin primality test
     * Returns true if n is probably prime, false if definitely composite
     */
    public static boolean isProbablePrime(BigInteger n, int certainty) {
        // Handle small cases
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        // Write n-1 as 2^r * d
        BigInteger d = n.subtract(BigInteger.ONE);
        int r = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            r++;
        }

        // Witness loop
        witness: for (int i = 0; i < certainty; i++) {
            BigInteger a = uniformRandom(BigInteger.TWO, n.subtract(BigInteger.TWO));
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue;
            }

            for (int j = 0; j < r - 1; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    continue witness;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Generate a random BigInteger uniformly distributed in [min, max]
     */
    private static BigInteger uniformRandom(BigInteger min, BigInteger max) {
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        int bitLength = range.bitLength();
        BigInteger result;
        do {
            result = new BigInteger(bitLength, random);
        } while (result.compareTo(range) >= 0);
        return result.add(min);
    }

    /**
     * Generate a random prime number with specified number of digits
     */
    public static BigInteger generatePrime(int digits) {
        BigInteger min = BigInteger.TEN.pow(digits - 1);
        BigInteger max = BigInteger.TEN.pow(digits).subtract(BigInteger.ONE);
        
        BigInteger candidate;
        do {
            candidate = uniformRandom(min, max);
            // Make it odd
            if (candidate.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                candidate = candidate.add(BigInteger.ONE);
            }
        } while (!isProbablePrime(candidate, 20)); // 20 rounds gives error < 2^-40
        
        return candidate;
    }
}
