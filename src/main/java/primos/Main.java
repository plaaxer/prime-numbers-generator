package primos;

import primos.generators.BbsGenerator;
import primos.generators.LcgGenerator;
import primos.generators.PseudoRandomGenerator;
import primos.primality.FermatTester;
import primos.primality.FermatWeakTester;
import primos.primality.MillerRabinTester;
import primos.primality.PrimalityTester;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static primos.ExperimentRunner.*;

public class Main {

    public static void main(String[] args) {

        List<String> argList = new ArrayList<>(Arrays.asList(args));
        boolean truncateOutput = true;

        List<String> statisticalTestsToRun = new ArrayList<>();

        if (argList.remove("-stat-all")) {
            statisticalTestsToRun.add("freq");
            statisticalTestsToRun.add("runs");
            statisticalTestsToRun.add("poker");

        } else {

            if (argList.remove("-stat-freq")) {
                statisticalTestsToRun.add("freq");
            }
            if (argList.remove("-stat-runs")) {
                statisticalTestsToRun.add("runs");
            }
            if (argList.remove("-stat-poker")) {
                statisticalTestsToRun.add("poker");
            }
        }

        if (argList.contains("-t")) {
            runTests();
            return;
        }

        if (argList.remove("-nt")) {
            truncateOutput = false;
        }

        if (argList.size() != 4) {
            printUsage();
            return;
        }

        String generatorName = argList.get(0);
        String testerName = argList.get(1);
        String bitLengthStr = argList.get(2);
        String certaintyStr = argList.get(3);

        Class<? extends PseudoRandomGenerator> generatorClass = null;
        if ("lcg".equalsIgnoreCase(generatorName)) {
            generatorClass = LcgGenerator.class;
        } else if ("bbs".equalsIgnoreCase(generatorName)) {
            generatorClass = BbsGenerator.class;
        } else {
            System.err.println("ERRO: Gerador '" + generatorName + "' inválido.");
            printUsage();
            return;
        }

        Class<? extends PrimalityTester> testerClass = null;
        if ("millerrabin".equalsIgnoreCase(testerName)) {
            testerClass = MillerRabinTester.class;
        } else if ("fermat".equalsIgnoreCase(testerName)) {
            testerClass = FermatTester.class;
        } else {
            System.err.println("ERRO: Testador '" + testerName + "' inválido.");
            printUsage();
            return;
        }

        int bitLength;
        int certainty;
        try {
            bitLength = Integer.parseInt(bitLengthStr);
            certainty = Integer.parseInt(certaintyStr);
        } catch (NumberFormatException e) {
            System.err.println("ERRO: O tamanho em bits e a certeza devem ser números inteiros.");
            printUsage();
            return;
        }

        System.out.println("Buscando um primo de " + bitLength + " bits...");
        System.out.println(" -> Usando gerador: " + generatorClass.getSimpleName());
        System.out.println(" -> Usando testador: " + testerClass.getSimpleName());

        try {
            long startTime = System.nanoTime();

            BigInteger foundPrime = findPrime(
                    testerClass,
                    generatorClass,
                    bitLength,
                    certainty
            );

            long endTime = System.nanoTime();
            double totalTimeMs = (endTime - startTime) / 1_000_000.0;

            System.out.println("\nProcesso Concluído!");
            System.out.printf("Tempo total da busca: %.4f ms%n", totalTimeMs);

            if (truncateOutput && foundPrime.toString().length() > 70) {
                String primeString = foundPrime.toString();
                primeString = primeString.substring(0, 35) + "..." + primeString.substring(primeString.length() - 35);
                System.out.println("Primo encontrado: " + primeString);
            } else {
                System.out.println("Primo encontrado: " + foundPrime);
            }

            // testes estatísticos
            if (!statisticalTestsToRun.isEmpty()) {
                System.out.println("\n--- INICIANDO TESTES ESTATÍSTICOS ---");
                for (String test : statisticalTestsToRun) {
                    switch (test) {
                        case "freq":
                            System.out.println(BigIntegerStatisticalTests.frequencyTest(foundPrime));
                            break;
                        case "runs":
                            System.out.println(BigIntegerStatisticalTests.runsTest(foundPrime));
                            break;
                        case "poker":
                            // Roda o teste com um tamanho de bloco padrão de 4 bits
                            System.out.println(BigIntegerStatisticalTests.pokerTest(foundPrime, 4));
                            // Se o primo for grande, roda também com blocos de 8 bits para uma análise mais detalhada
                            if (bitLength >= 512) {
                                System.out.println(BigIntegerStatisticalTests.pokerTest(foundPrime, 8));
                            }
                            break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ocorreu um erro durante a execução:");
            e.printStackTrace();
        }
    }

    /**
     * Imprime as instruções de uso do programa no console.
     */
    private static void printUsage() {
        // ALTERAÇÃO: Instruções de uso atualizadas com as novas flags.
        System.err.println("\nUso: java Main [opções] <gerador> <testador> <bits> <certeza>");
        System.err.println("\nArgumentos Obrigatórios:");
        System.err.println("  <gerador>    LCG | BBS");
        System.err.println("  <testador>   MillerRabin | Fermat");
        System.err.println("  <bits>       O número de bits do primo (ex: 256)");
        System.err.println("  <certeza>    O número de iterações do teste (ex: 100)");
        System.err.println("\nOpções:");
        System.err.println("  -nt          Não truncar a saída do número primo encontrado.");
        System.err.println("  -t           Executa uma série de benchmarks pré-definidos e encerra.");
        System.err.println("  -stat-freq   Executa o Teste de Frequência.");
        System.err.println("  -stat-runs   Executa o Teste de Runs.");
        System.err.println("  -stat-poker  Executa o Teste de Pôquer.");
        System.err.println("  -stat-all    Executa todos os testes estatísticos.");
        System.err.println("\nExemplo de uso com testes estatísticos:");
        System.err.println("  java Main -stat-all BBS MillerRabin 256 100");
    }

    private static void runTests() {
        List<Integer> bitLengths = List.of(40, 56, 80, 128, 256, 512, 1024, 2048, 4096);

        int numbersToGenerate = 5000;

        int certainty = 200;

        generateLcg(bitLengths, numbersToGenerate);

        generateBbs(bitLengths, numbersToGenerate);

        verifyMillerRabin(bitLengths, certainty);

        verifyFermat(bitLengths, certainty);

        carmichaelTesting();

        benchmarkPrimalityTestLCG(bitLengths, certainty, FermatTester.class);

        benchmarkPrimalityTestLCG(bitLengths, certainty, MillerRabinTester.class);
    }

    private static void carmichaelTesting() {
        BigInteger carmichaelNumber = BigInteger.valueOf(1729);
        int certainty = 200;

        PrimalityTester millerRabin = new MillerRabinTester();
        PrimalityTester fermat = new FermatWeakTester();

        boolean millerRabinResult = millerRabin.isPrime(carmichaelNumber, certainty);
        System.out.println("Miller-Rabin diz que é primo? " + millerRabinResult);

        boolean fermatResult = fermat.isPrime(carmichaelNumber, certainty);
        System.out.println("Fermat diz que é primo? " + fermatResult);

    }

    /**
     * Busca gerar um número pseudo-aleatório, provavelmente primo, utilizando Lcg e Miller-Rabin.
     * Obs: a quantidade de números gerados pelo lcg está fixado em {@link primos.ExperimentRunner#BATCHSIZE}
     * @param bitLengths Os tamanhos de número desejados.
     * @param certainty Número de iterações no Miller-Rabin.
     */
    private static void verifyMillerRabin(List<Integer> bitLengths, int certainty) {
        List<Integer> reduced = new ArrayList<>(bitLengths.subList(0, bitLengths.size() - 2));
        prepareJit(LcgGenerator.class, bitLengths, BATCHSIZE);
        prepareJitForTester(MillerRabinTester.class, LcgGenerator.class, reduced, certainty);

        for (int bitLength : bitLengths) {
            runPrimalityTest(MillerRabinTester.class, LcgGenerator.class, bitLength, certainty);
        }
    }
    /**
     * Busca gerar um número pseudo-aleatório, provavelmente primo, utilizando Lcg e Fermat.
     * Obs: a quantidade de números gerados pelo lcg está fixado em {@link primos.ExperimentRunner#BATCHSIZE}
     * @param bitLengths Os tamanhos de número desejados.
     * @param certainty Número de iterações no teste de Fermat.
     */
    private static void verifyFermat(List<Integer> bitLengths, int certainty) {
        List<Integer> reduced = new ArrayList<>(bitLengths.subList(0, bitLengths.size() - 2));
        prepareJit(LcgGenerator.class, bitLengths, BATCHSIZE);
        prepareJitForTester(FermatTester.class, LcgGenerator.class, reduced, certainty);

        for (int bitLength : bitLengths) {
            runPrimalityTest(FermatTester.class, LcgGenerator.class, bitLength, certainty);
        }
    }

    private static void generateLcg(List<Integer> bitLengths, int numbersToGenerate) {
        prepareJit(LcgGenerator.class, bitLengths, numbersToGenerate);

        for (int bitLength : bitLengths) {
            runFor(new LcgGenerator(bitLength), bitLength, numbersToGenerate);
        }
    }

    private static void generateBbs(List<Integer> bitLengths, int numbersToGenerate) {
        List<Integer> reduced = new ArrayList<>(bitLengths.subList(0, bitLengths.size() - 2));
        prepareJit(BbsGenerator.class, reduced, numbersToGenerate);

        for (int bitLength : bitLengths) {
            runFor(new BbsGenerator(bitLength), bitLength, numbersToGenerate);
        }
    }

    private static <T extends PrimalityTester> void benchmarkPrimalityTestLCG(List<Integer> bitLengths, int certainty, Class<T> clazz) {
        List<Integer> reduced = new ArrayList<>(bitLengths.subList(0, bitLengths.size() - 2));

        prepareJitForTester(clazz, LcgGenerator.class, reduced, 500);
        for (int bitLength : bitLengths) {
            benchmarkPrimalityTester(clazz, LcgGenerator.class, bitLength, certainty, 100);
        }
    }

    private static <T extends PrimalityTester> void benchmarkPrimalityTestBBS(List<Integer> bitLengths, int certainty, Class<T> clazz) {
        List<Integer> reduced = new ArrayList<>(bitLengths.subList(0, bitLengths.size() - 2));

        prepareJitForTester(clazz, LcgGenerator.class, reduced, 500);
        for (int bitLength : bitLengths) {
            benchmarkPrimalityTester(clazz, BbsGenerator.class, bitLength, certainty, 100);
        }
    }

}