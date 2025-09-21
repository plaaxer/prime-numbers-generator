package br.com.github.plaaxer.primos.generators;

import java.math.BigInteger;
import java.util.List;

public class BbsGenerator implements PseudoRandomGenerator {
    // Parâmetros do BBS (M, seed)
    private BigInteger M, seed;

    public BbsGenerator(/* parâmetros aqui */) {
        // Inicializa os parâmetros
    }

    @Override
    public List<BigInteger> generate(int bitLength) {
        // Lógica para gerar N bits usando BBS (lembre-se que é 1 bit por iteração)
        // ...
        return null; // Retorna o número montado
    }
}
