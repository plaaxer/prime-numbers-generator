package primos.generators;

import java.math.BigInteger;
import java.util.List;

public interface PseudoRandomGenerator {
    /**
     * Gera uma lista de números pseudo-aleatórios.
     *
     * @param n A quantidade de números a serem gerados.
     * @return Uma lista de BigInteger contendo os números gerados.
     */
    List<BigInteger> generate(int n);

    /**
     * Retorna o nome abreviado do gerador para print.
     * @return O nome
     */
    String name();
}