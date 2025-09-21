package primos.generators;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LcgGenerator implements PseudoRandomGenerator {

    /**
     * Parâmetros do algoritmo, vide relatório.
     */
    private final BigInteger a;
    private final BigInteger c;
    private final BigInteger m;
    private BigInteger seed;

    /**
     * Construtor para o Linear Congruential Generator.
     * Trata-se basicamente de constantes pré-definidas, explicadas no relatório,
     * e na seleção de m conforme a quantidade de bits resultantes desejada.
     * @param bitLength
     */
    public LcgGenerator(int bitLength) {

        this.a = new BigInteger("6364136223846793005");
        this.c = new BigInteger("1442695040888963407");

        this.m = BigInteger.ONE.shiftLeft(bitLength);

        // utilizando o tempo atual como seed
        this.seed = new BigInteger(String.valueOf(System.nanoTime()));
    }

    /**
     * Aplica a fórmula (a * seed + c) % m.
     * @return Próximo número da sequência
     */
    private BigInteger next() {
        this.seed = a.multiply(seed).add(c).mod(m);
        return this.seed;
    }

    /**
     * {@inheritDoc}
     */
    public List<BigInteger> generate(int count) {
        List<BigInteger> numbers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            numbers.add(next());
        }
        return numbers;
    }

    @Override
    public String name() {
        return "LCG";
    }
}