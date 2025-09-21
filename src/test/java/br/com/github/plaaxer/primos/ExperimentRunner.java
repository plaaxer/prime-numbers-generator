package br.com.github.plaaxer.primos;

import br.com.github.plaaxer.primos.generators.LcgGenerator;
import br.com.github.plaaxer.primos.generators.PseudoRandomGenerator;

import java.math.BigInteger;
import java.util.List;

public class ExperimentRunner {

    /**
     * Executa o teste para um gerador específico, mede o tempo e imprime os resultados.
     * @param generator A instância do gerador a ser testado.
     * @param algorithmName O nome do algoritmo para exibição.
     * @param bitLength O tamanho em bits dos números gerados.
     * @param count A quantidade de números a gerar.
     */
    public static void runFor(PseudoRandomGenerator generator, String algorithmName, int bitLength, int count) {
        // Medição de tempo
        long startTime = System.nanoTime();
        List<BigInteger> result = generator.generate(count);
        long endTime = System.nanoTime();

        long totalTimeNs = endTime - startTime;
        double totalTimeMs = totalTimeNs / 1_000_000.0; // Convertendo para milissegundos
        long averageTimeNs = totalTimeNs / count;

        System.out.println("Primeiro gerado: " + result.get(0));
        System.out.println("Último gerado: " + result.get(result.size() - 1));

        System.out.printf("%-25s | %-12d | %-20.4f | %-20d%n",
                algorithmName, bitLength, totalTimeMs, averageTimeNs);
    }

/**
 * Executa uma série de gerações de números para "aquecer" a JVM (Máquina Virtual Java).
 * Este processo força o compilador Just-In-Time (JIT) a otimizar os métodos
 * críticos do gerador antes que as medições de performance reais comecem.
 * Ao fazer isso, evita-se que os custos de compilação iniciais contaminem os
 * resultados do benchmark, levando a medições mais precisas e consistentes.
 * @param bitLengths Lista de tamanhos em bits a serem utilizados no aquecimento
 * @param count A quantidade de números a serem gerados em cada iteração do aquecimento.
 */
// TODO: classe deve ser parametrizável aqui.
    public static void prepareJit(List<Integer> bitLengths, int count) {
        for (int length : bitLengths) {
            PseudoRandomGenerator generator = new LcgGenerator(length);
            generator.generate(count);
        }
    }
}