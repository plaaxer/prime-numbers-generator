package primos;

import primos.generators.LcgGenerator;
import primos.generators.PseudoRandomGenerator;
import primos.primality.PrimalityTester;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.List;

public class ExperimentRunner {

    public static final int BATCHSIZE = 10;

    /**
     * Executa o teste para um gerador específico, mede o tempo e imprime os resultados.
     * @param generator A instância do gerador a ser testado.
     * @param bitLength O tamanho em bits dos números gerados.
     * @param count A quantidade de números a gerar.
     */
    public static void runFor(PseudoRandomGenerator generator, int bitLength, int count) {

        long startTime = System.nanoTime();
        List<BigInteger> result = generator.generate(count);
        long endTime = System.nanoTime();

        long totalTimeNs = endTime - startTime;
        double totalTimeMs = totalTimeNs / 1_000_000.0; // ms
        long averageTimeNs = totalTimeNs / count;

        System.out.println("Primeiro gerado: " + result.get(0));
        System.out.println("Último gerado: " + result.get(result.size() - 1));

        System.out.printf("%-25s | %-12d | %-20.4f | %-20d%n",
                generator.name(), bitLength, totalTimeMs, averageTimeNs);
    }

/**
 * Executa uma série de gerações de números para "aquecer" a JVM (Máquina Virtual Java).
 * Este processo força o compilador Just-In-Time (JIT) a otimizar os métodos
 * críticos do gerador antes que as medições de performance reais comecem.
 * Ao fazer isso, evita-se que os custos de compilação iniciais contaminem os
 * resultados do benchmark, levando a medições mais precisas e consistentes.
 * @param clazz Classe referente ao gerador de números aleatórios que será "aquecido".
 * @param bitLengths Lista de tamanhos em bits a serem utilizados no aquecimento
 * @param count A quantidade de números a serem gerados em cada iteração do aquecimento.
 */
    public static <T extends PseudoRandomGenerator> void prepareJit(Class<T> clazz, List<Integer> bitLengths, int count) {
        for (int length : bitLengths) {
            try {
                PseudoRandomGenerator generator = clazz.getDeclaredConstructor(int.class).newInstance(length);
                generator.generate(count);
            } catch (Exception e) {
                System.out.println("Erro ao instanciar gerador: " + e.getMessage());
                return;
            }
        }
    }

    /**
     * Executa e cronometra a busca por um número primo de um determinado tamanho.
     * <p>
     * O processo consiste em:
     * 1. Gerar um número candidato usando o {@link PseudoRandomGenerator}.
     * 2. Garantir que o candidato seja ímpar.
     * 3. Testar sua primalidade com o {@link PrimalityTester}.
     * 4. Repetir até que um número provavelmente primo seja encontrado.
     * <p>
     * O tempo medido corresponde ao tempo total para encontrar o primo.
     *
     * @param tester    A instância do algoritmo de teste de primalidade (ex: Miller-Rabin).
     * @param generator A instância do gerador de números pseudo-aleatórios.
     * @param bitLength O tamanho em bits do número primo a ser encontrado.
     * @param certainty O parâmetro de certeza para o teste de primalidade (ex: 'k' do Miller-Rabin).
     */
    /**
     * Executa e cronometra a busca por um número primo, instanciando os objetos a partir de suas classes.
     *
     * @param testerClass    A CLASSE do testador de primalidade (ex: MillerRabinTester.class).
     * @param generatorClass A CLASSE do gerador de números (ex: LcgGenerator.class).
     * @param bitLength      O tamanho em bits do primo a ser encontrado.
     * @param certainty      A certeza para o teste de primalidade.
     * @param <T>            Um tipo que implementa PrimalityTester.
     * @param <R>            Um tipo que implementa PseudoRandomGenerator.
     */
    public static <T extends PrimalityTester, R extends PseudoRandomGenerator> void runPrimalityTest(
            Class<T> testerClass,
            Class<R> generatorClass,
            int bitLength,
            int certainty) {

        try {

            PrimalityTester tester = testerClass.getDeclaredConstructor().newInstance();

            PseudoRandomGenerator generator = generatorClass.getDeclaredConstructor(int.class).newInstance(bitLength);

            long startTime = System.nanoTime();
            BigInteger primeCandidate;
            int attempts = 0;

            while (true) {
                attempts++;
                // 1. Gera um lote de números candidatos.
                List<BigInteger> candidates = generator.generate(BATCHSIZE);

                // Pega o ÚLTIMO número do lote gerado.
                primeCandidate = candidates.get(candidates.size() - 1);

                // 2. Garante que o candidato seja ímpar.
                if (!primeCandidate.testBit(0)) {
                    primeCandidate = primeCandidate.setBit(0);
                }

                // 3. Testa a primalidade. Se for primo, o loop termina.
                if (tester.isPrime(primeCandidate, certainty)) {
                    break;
                }
            }

            long endTime = System.nanoTime();
            long totalTimeNs = endTime - startTime;
            double totalTimeMs = totalTimeNs / 1_000_000.0;

            //System.out.println("Primo de " + bitLength + " bits encontrado após " + attempts + " tentativas.");
            //System.out.println("Primo encontrado: " + primeCandidate.toString());

            System.out.printf("%-25s | %-12d | %-10d | %-20.4f%n",
                    tester.getClass().getSimpleName(),
                    bitLength,
                    attempts,
                    totalTimeMs);

            //versão truncada do primo encontrado
            String primeString = primeCandidate.toString();
            if (primeString.length() > 60) {
                primeString = primeString.substring(0, 30) + "..." + primeString.substring(primeString.length() - 30);
            }
            System.out.println("  \\-> Primo encontrado: " + primeString);


        } catch (Exception e) {
            System.err.println("ERRO: Falha ao instanciar gerador ou testador via reflexão.");
        }
    }

    /**
     * Executa uma série de testes de primalidade para "aquecer" a JVM.
     * <p>
     * Este processo força o compilador JIT a otimizar os métodos críticos do
     * testador de primalidade (ex: {@code isPrime}) antes do início das medições
     * de performance reais, garantindo benchmarks mais precisos.
     *
     * @param testerClazz      A classe do testador de primalidade a ser aquecido.
     * @param generatorClazz   A classe do gerador usado para fornecer números para o aquecimento.
     * @param bitLengths       Uma lista de tamanhos em bits para usar no aquecimento.
     * @param certainty        O parâmetro de certeza para os testes de aquecimento.
     */
    public static void prepareJitForTester(Class<? extends PrimalityTester> testerClazz,
                                           Class<? extends PseudoRandomGenerator> generatorClazz,
                                           List<Integer> bitLengths,
                                           int certainty) {
        System.out.println("Aquecendo JIT para o testador de primalidade: " + testerClazz.getSimpleName());
        try {
            PrimalityTester tester = testerClazz.getDeclaredConstructor().newInstance();
            for (int length : bitLengths) {
                PseudoRandomGenerator generator = generatorClazz.getDeclaredConstructor(int.class).newInstance(length);
                BigInteger candidate = generator.generate(1).get(0);
                tester.isPrime(candidate.setBit(0), certainty);
            }
        } catch (Exception e) {
            System.err.println("ERRO: Falha durante o aquecimento do JIT para o testador: " + e.getMessage());
        }
        System.out.println("Aquecimento do testador concluído.");
    }
}