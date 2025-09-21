package primos.primality;

import java.math.BigInteger;

/**
 * Interface que define um contrato para algoritmos de teste de primalidade.
 * <p>
 * O objetivo é permitir que diferentes métodos (como Miller-Rabin, Fermat, etc.)
 * sejam implementados e utilizados de forma intercambiável.
 */
public interface PrimalityTester {

    /**
     * Verifica se um determinado número é provavelmente primo.
     * <p>
     * A maioria dos testes de primalidade para números grandes são probabilísticos.
     * A chance de um número composto ser incorretamente identificado como primo
     * diminui exponencialmente com o aumento do parâmetro de certeza.
     *
     * @param n O número a ser testado. Deve ser um inteiro positivo.
     * @param certainty O número de iterações do teste (geralmente chamado de 'k').
     * Um valor maior aumenta a confiança no resultado.
     * @return {@code true} se o número for provavelmente primo, {@code false} se for composto.
     */
    boolean isPrime(BigInteger n, int certainty);
}