package primos.primality;

import java.math.BigInteger;
import java.util.Random;

/**
 * Implementação do Teste de Primalidade de Fermat.
 * <p>
 * Baseado no Pequeno Teorema de Fermat, este é um teste probabilístico simples.
 * Ele é mais rápido que o Miller-Rabin, mas menos confiável devido à sua
 * vulnerabilidade a números compostos especiais chamados "números de Carmichael".
 * <p>
 */
public class FermatTester implements PrimalityTester {

    // Constantes BigInteger para evitar recriação
    protected static final BigInteger ONE = BigInteger.ONE;
    protected static final BigInteger TWO = BigInteger.valueOf(2);
    protected static final BigInteger THREE = BigInteger.valueOf(3);

    private final Random rand;

    /**
     * Construtor que inicializa a fonte de aleatoriedade.
     */
    public FermatTester() {
        // Usa o tempo do sistema como semente.
        this.rand = new Random(System.nanoTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrime(BigInteger n, int certainty) {

        // parte idêntica ao miller-rabin.
        if (n.compareTo(ONE) <= 0 || !n.testBit(0)) {
            return false;
        }

        if (n.compareTo(THREE) <= 0) {
            return true;
        }

        // Preparar o expoente para o teste (n-1).
        BigInteger nMinusOne = n.subtract(ONE);

        // Executar o teste 'certainty' vezes.
        for (int i = 0; i < certainty; i++) {
            // Escolher uma testemunha aleatória 'a' no intervalo [2, n-2].
            BigInteger a = getRandomBase(n);

            // Calcular a^(n-1) mod n.
            BigInteger result = a.modPow(nMinusOne, n);

            // Conforme o teorema, se n é primo, o resultado DEVE ser 1.
            // Se o resultado for diferente de 1, n é definitivamente composto.
            if (!result.equals(ONE)) {
                return false;
            }
        }

        // Se "n" passou em todos os testes, é provavelmente primo (ou um número de Carmichael).
        return true;
    }

    /**
     * Gera uma base aleatória 'a' no intervalo [2, n-2].
     * (Este método é idêntico ao usado no MillerRabinTester).
     *
     * @param n O limite superior (exclusivo) para a base.
     * @return um BigInteger aleatório 'a' tal que 2 <= a <= n-2.
     */
    protected BigInteger getRandomBase(BigInteger n) {
        BigInteger nMinusTwo = n.subtract(TWO);
        BigInteger a;
        do {
            a = new BigInteger(n.bitLength(), rand);
        } while (a.compareTo(TWO) < 0 || a.compareTo(nMinusTwo) > 0);
        return a;
    }
}