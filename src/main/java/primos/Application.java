package primos;

import primos.generators.BbsGenerator;
import primos.generators.LcgGenerator;
import primos.primality.FermatTester;
import primos.primality.FermatWeakTester;
import primos.primality.MillerRabinTester;
import primos.primality.PrimalityTester;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import static primos.ExperimentRunner.*;

public class Application {

    public static void main(String[] args) {

        List<Integer> bitLengths = List.of(40, 56, 80, 128, 256, 512, 1024, 2048, 4096);

        int numbersToGenerate = 1000;

        int certainty = 200;

        //generateLcg(bitLengths, numbersToGenerate);

        //generateBbs(bitLengths, numbersToGenerate);

        //verifyMillerRabin(bitLengths, certainty);

        carmichaelTesting();

    }

    private static void carmichaelTesting() {
        BigInteger carmichaelNumber = BigInteger.valueOf(1729);
        int certainty = 100;

        PrimalityTester millerRabin = new MillerRabinTester();
        PrimalityTester fermat = new FermatWeakTester();

        boolean millerRabinResult = millerRabin.isPrime(carmichaelNumber, certainty);
        System.out.println("Miller-Rabin diz que é primo? " + millerRabinResult); // Esperado: false

        boolean fermatResult = fermat.isPrime(carmichaelNumber, certainty);
        System.out.println("Fermat diz que é primo? " + fermatResult);

        System.out.println("r:  " + fermat.isPrime(BigInteger.probablePrime(256, new Random()), 200));
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

}