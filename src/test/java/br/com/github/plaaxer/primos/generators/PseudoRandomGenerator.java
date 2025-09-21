package br.com.github.plaaxer.primos.generators;

import java.math.BigInteger;
import java.util.List;

public interface PseudoRandomGenerator {
    /**
     * Gera uma lista de números pseudo-aleatório após n recorrências.
     */
    List<BigInteger> generate(int n);
}