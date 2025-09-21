package primos.generators;

import java.math.BigInteger;
// Removido: import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random; // Importa a classe Random padrão

/**
 * Implementação do gerador de números pseudo-aleatórios Blum Blum Shub (BBS).
 * <p>
 * O BBS é um gerador criptograficamente seguro, baseado na dificuldade de
 * fatorar inteiros. Sua fórmula de geração é: X_n+1 = (X_n)^2 mod M.
 * <p>
 * Referência: Blum, L., Blum, M., & Shub, M. (1986). A Simple Unpredictable
 * Pseudo-Random Number Generator. SIAM Journal on Computing, 15(2), 364–383.
 */
public class BbsGenerator implements PseudoRandomGenerator {

    /**
     * O módulo M, que é o produto de dois grandes primos (p * q).
     */
    private final BigInteger m;

    /**
     * O estado atual do gerador (X_n).
     */
    private BigInteger currentState;

    /**
     * O tamanho em bits dos números que serão gerados.
     */
    private final int bitLength;

    // Constantes para facilitar os cálculos com BigInteger
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /**
     * Construtor para o gerador Blum Blum Shub.
     * <p>
     * A inicialização deste gerador é computacionalmente intensiva, pois requer
     * a busca por dois números primos grandes que satisfaçam as condições do algoritmo.
     *
     * @param bitLength O tamanho (em bits) de cada número aleatório a ser gerado.
     */
    public BbsGenerator(int bitLength) {
        if (bitLength < 2) {
            throw new IllegalArgumentException("O tamanho em bits deve ser pelo menos 2.");
        }
        this.bitLength = bitLength;

        Random rand = new Random(System.nanoTime());

        // Encontrar dois primos grandes, p e q, distintos.
        // Ambos devem ser congruentes a 3 (mod 4).
        // Para que M = p*q tenha 'bitLength' bits, p e q devem ter ~bitLength/2 bits.
        int primeBitLength = bitLength / 2;

        BigInteger p = findBlumPrime(primeBitLength, rand);
        BigInteger q;
        do {
            q = findBlumPrime(primeBitLength, rand);
        } while (p.equals(q)); // Garante que p e q são diferentes.

        // Calcular o módulo M = p * q.
        this.m = p.multiply(q);

        // Escolher uma semente 's' (ou x0).
        // A semente deve ser um inteiro aleatório e coprimo com M.
        // Uma forma segura é escolher um 'x' aleatório e fazer s = x^2 mod M.
        BigInteger x;
        do {
            x = new BigInteger(bitLength, rand);
        } while (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE));

        this.currentState = x.modPow(TWO, this.m);
    }

    /**
     * Método auxiliar para encontrar um número primo 'p' que satisfaça a
     * condição p ≡ 3 (mod 4). Vide relatório.
     *
     * @param bitLength O número de bits desejado para o primo.
     * @param rand      A fonte de aleatoriedade.
     * @return Um BigInteger que é um provável primo e satisfaz a condição.
     */
    private BigInteger findBlumPrime(int bitLength, Random rand) {
        BigInteger p;
        do {
            // Gera um número que é provavelmente primo.
            p = BigInteger.probablePrime(bitLength, rand);
            // Verifica se ele atende à condição de Blum: p % 4 == 3
        } while (!p.mod(FOUR).equals(THREE));
        return p;
    }

    /**
     * Gera o próximo número pseudo-aleatório da sequência.
     * <p>
     * Este método gera 'bitLength' bits, um por um, e os combina para formar
     * um único número BigInteger.
     *
     * @return O número pseudo-aleatório gerado.
     */
    private BigInteger next() {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < this.bitLength; i++) {
            // Passo de geração: X_n+1 = (X_n)^2 mod M
            this.currentState = this.currentState.modPow(TWO, this.m);

            // O bit pseudo-aleatório é o bit menos significativo (LSB) do estado atual.
            // Para construir o número final, deslocamos o resultado para a esquerda...
            result = result.shiftLeft(1);
            // ...e adicionamos o novo bit na posição 0.
            if (this.currentState.testBit(0)) { // testBit(0) retorna true se o LSB for 1.
                result = result.setBit(0);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BigInteger> generate(int count) {
        List<BigInteger> numbers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            numbers.add(next());
        }
        return numbers;
    }

    @Override
    public String name() {
        return "BBS";
    }
}