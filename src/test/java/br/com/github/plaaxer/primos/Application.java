package br.com.github.plaaxer.primos;

import br.com.github.plaaxer.primos.generators.BbsGenerator;
import br.com.github.plaaxer.primos.generators.LcgGenerator;
import br.com.github.plaaxer.primos.generators.PseudoRandomGenerator;

import java.math.BigInteger;
import java.util.List;

import static br.com.github.plaaxer.primos.ExperimentRunner.prepareJit;
import static br.com.github.plaaxer.primos.ExperimentRunner.runFor;

public class Application {

    public static void main(String[] args) {

        List<Integer> bitLengths = List.of(40, 56, 80, 128, 256, 512, 1024, 2048, 4096);

        int numbersToGenerate = 1000;

        prepareJit(bitLengths, numbersToGenerate);

        for (int bitLength : bitLengths) {
            runFor(new LcgGenerator(bitLength), "LCG", bitLength, numbersToGenerate);
        }

    }
}