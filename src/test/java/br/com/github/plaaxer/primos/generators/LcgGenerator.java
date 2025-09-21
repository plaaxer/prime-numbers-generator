package br.com.github.plaaxer.primos.generators;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LcgGenerator implements PseudoRandomGenerator {

    // paramêtros do algoritmo
    private final BigInteger a;
    private final BigInteger c;
    private final BigInteger m;
    private BigInteger seed;

    public LcgGenerator(int bitLength) {

        // pré-definidos
        this.a = new BigInteger("6364136223846793005");
        this.c = new BigInteger("1442695040888963407");

        // é o limitante
        this.m = BigInteger.ONE.shiftLeft(bitLength);

        // utilizando o tempo atual como seed
        this.seed = new BigInteger(String.valueOf(System.nanoTime()));
    }

    private BigInteger next() {
        // Aplica a fórmula: seed = (a * seed + c) % m
        this.seed = a.multiply(seed).add(c).mod(m);
        return this.seed;
    }

    public List<BigInteger> generate(int count) {
        List<BigInteger> numbers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            numbers.add(next());
        }
        return numbers;
    }
}