package primos;

import primos.generators.BbsGenerator;
import primos.generators.LcgGenerator;
import primos.generators.PseudoRandomGenerator;
import primos.primality.FermatTester;
import primos.primality.FermatWeakTester;
import primos.primality.MillerRabinTester;
import primos.primality.PrimalityTester;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import static primos.ExperimentRunner.*;

public class Application {

    public static void main(String[] args) {


        List<String> argList = new ArrayList<>(Arrays.asList(args));
        boolean truncateOutput = true;

        if (argList.contains("-t")) {
            runTests();
            return;
        }


        if (argList.contains("-nt")) {
            truncateOutput = false;
            argList.remove("-nt");
        }

        if (argList.size() != 4) {
            printUsage();
            return;
        }

        Class<? extends PseudoRandomGenerator> generatorClass = null;
        String generatorName = args[0];
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
        String testerName = args[1];
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
            bitLength = Integer.parseInt(args[2]);
            certainty = Integer.parseInt(args[3]);
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

            // função que retorna o primo
            BigInteger foundPrime = ExperimentRunner.findPrime(
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
                // versão truncada do primo encontrado
                String primeString = foundPrime.toString();
                primeString = primeString.substring(0, 35) + "..." + primeString.substring(primeString.length() - 35);
                System.out.println("Primo encontrado: " + primeString);
            } else {
                System.out.println("Primo encontrado: " + foundPrime);
            }

        } catch (Exception e) {
            System.err.println("Ocorreu um erro fatal durante a execução:");
            e.printStackTrace();
        }
    }

    /**
     * Imprime as instruções de uso do programa no console.
     */
    private static void printUsage() {
        System.err.println("\nUso: java Main <gerador> <testador> <bits> <certeza>");
        System.err.println("  <gerador>: LCG | BBS");
        System.err.println("  <testador>: MillerRabin | Fermat");
        System.err.println("  <bits>: O número de bits do primo (ex: 128)");
        System.err.println("  <certeza>: O número de iterações do teste (ex: 100)");
        System.err.println("\nExemplo: java Main BBS MillerRabin 256 100");
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