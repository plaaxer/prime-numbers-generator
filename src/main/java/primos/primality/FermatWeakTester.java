package primos.primality;

import java.math.BigInteger;

/**
 * Uma implementação "fraca" do Teste de Fermat que herda de FermatTester.
 * <p>
 * O objetivo desta classe é demonstrar a falha do algoritmo com os números de
 * Carmichael. Para isso, ela sobrescreve o método isPrime() para ignorar
 * bases 'a' que não são coprimas com 'n', forçando o teste a usar apenas as
 * bases que falham em detectar um número de Carmichael.
 */
public class FermatWeakTester extends FermatTester {

    /**
     * Construtor que chama o construtor da classe pai (FermatTester).
     * Isso garante que a fonte de aleatoriedade 'rand' seja inicializada.
     */
    public FermatWeakTester() {
        super();
    }

    /**
     * Sobrescreve o método isPrime da classe pai para introduzir a "fraqueza".
     */
    @Override
    public boolean isPrime(BigInteger n, int certainty) {
        // Reutiliza os mesmos casos base da implementação original
        if (n.compareTo(ONE) <= 0) return false;
        if (n.compareTo(THREE) <= 0) return true;
        if (!n.testBit(0)) return false;

        BigInteger nMinusOne = n.subtract(ONE);

        for (int i = 0; i < certainty; i++) {
            BigInteger a = getRandomBase(n);

            // Se a base 'a' compartilha um fator com 'n', ela poderia revelar
            // que 'n' é composto. Para demonstrar a fraqueza, nós ignoramos
            // essa base e pulamos para a próxima tentativa.
            if (!a.gcd(n).equals(ONE)) {
                continue;
            }

            // Continua com o teste de Fermat padrão
            BigInteger result = a.modPow(nMinusOne, n);

            if (!result.equals(ONE)) {
                return false;
            }
        }

        // Se passou em todos os testes (usando apenas bases coprimas),
        // ele será enganado por um número de Carmichael.
        return true;
    }
}