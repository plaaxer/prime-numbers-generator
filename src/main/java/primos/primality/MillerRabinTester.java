package primos.primality;

import java.math.BigInteger;
import java.util.Random;

/**
 * Implementação do teste de primalidade de Miller-Rabin.
 * <p>
 * Este é um teste probabilístico eficiente para determinar se um número grande é
 * provavelmente primo. É o algoritmo mais utilizado na prática para essa finalidade.
 * <p>
 */
public class MillerRabinTester implements PrimalityTester {

    // Constantes BigInteger para evitar recriação
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);

    private final Random rand;

    /**
     * Construtor que inicializa a fonte de aleatoriedade.
     */
    public MillerRabinTester() {
        // Usa o tempo do sistema como semente para consistência com os geradores.
        this.rand = new Random(System.nanoTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrime(BigInteger n, int certainty) {

        if (n.compareTo(ONE) <= 0) return false;    // Números <= 1 não são primos.
        if (n.compareTo(THREE) <= 0) return true;   // 2 e 3 são primos.
        if (!n.testBit(0)) return false;            // Números pares (exceto 2) não são primos.

        // Decompor n-1 em d * 2^s, onde d é ímpar ---
        BigInteger nMinusOne = n.subtract(ONE);
        int s = nMinusOne.getLowestSetBit();
        BigInteger d = nMinusOne.shiftRight(s);

        // Executar o teste 'certainty' vezes ---
        for (int i = 0; i < certainty; i++) {
            // Escolher uma testemunha aleatória 'a' no intervalo [2, n-2].
            BigInteger a = getRandomBase(n);
            // Calcular x = a^d mod n.
            BigInteger x = a.modPow(d, n);

            // Se a primeira condição for satisfeita, 'a' não é testemunha.
            // Pulamos para a próxima testemunha.
            if (passesFirstCheck(x, nMinusOne)) {
                continue;
            }

            // Se a segunda condição não for satisfeita, o número é definitivamente composto.
            if (!passesSecondCheck(x, s, n)) {
                return false;
            }
        }

        // Se n passou em todos os testes, é provavelmente primo.
        return true;
    }

    /**
     * Verifica a primeira condição do teste de Miller-Rabin.
     * <p>
     * Esta condição verifica se a^d ≡ 1 (mod n) ou a^d ≡ -1 (mod n).
     * Se uma delas for verdadeira, 'a' não é uma testemunha da compostura de n.
     *
     * @param x O resultado de a^d mod n.
     * @param nMinusOne O valor de n-1.
     * @return {@code true} se a condição for satisfeita, {@code false} caso contrário.
     */
    private boolean passesFirstCheck(BigInteger x, BigInteger nMinusOne) {
        /* --- a^d = 1 (mod n) --- */
        // Se x == 1 ou x == n-1, 'a' não é uma testemunha da "compositeness" de n;
        // isto é, n ainda pode ser primo.
        return x.equals(ONE) || x.equals(nMinusOne);
    }

    /**
     * Verifica a segunda condição do teste de Miller-Rabin através de um loop de exponenciação.
     * <p>
     * Esta condição verifica se a^(d*2^r) ≡ -1 (mod n) para algum 0 ≤ r < s.
     * Se encontrarmos tal 'r', 'a' não é testemunha. Se o loop terminar sem
     * encontrar, ou se encontrarmos uma raiz quadrada não trivial de 1, o número é composto.
     *
     * @param x O valor inicial de x (a^d mod n).
     * @param s O expoente da potência de 2 na decomposição de n-1.
     * @param n O número sendo testado.
     * @return {@code true} se a condição for satisfeita (n pode ser primo), {@code false} se n é certamente composto.
     */
    private boolean passesSecondCheck(BigInteger x, int s, BigInteger n) {
        /* --- a^(2^r)d = -1 mod n --- */
        BigInteger nMinusOne = n.subtract(ONE);

        // Repetir o squaring s-1 vezes.
        for (int r = 1; r < s; r++) {
            x = x.modPow(TWO, n);
            // Se x se tornar 1, n é composto (raiz quadrada não trivial de 1).
            if (x.equals(ONE)) {
                return false;
            }
            // Se x se tornar n-1, 'a' não é testemunha. Pode ser primo.
            if (x.equals(nMinusOne)) {
                return true; // Sai do loop e vai para a próxima testemunha.
            }
        }

        // Se o loop terminou sem que x se tornasse n-1, então n é composto.
        return false;
    }

    /**
     * Gera uma base aleatória 'a' no intervalo [2, n-2].
     *
     * @param n O limite superior (exclusivo) para a base.
     * @return um BigInteger aleatório 'a' tal que 2 <= a <= n-2.
     */
    private BigInteger getRandomBase(BigInteger n) {
        BigInteger nMinusTwo = n.subtract(TWO);
        BigInteger a;
        do {
            a = new BigInteger(n.bitLength(), rand);
        } while (a.compareTo(TWO) < 0 || a.compareTo(nMinusTwo) > 0);
        return a;
    }
}