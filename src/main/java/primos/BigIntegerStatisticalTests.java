package primos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Uma classe para realizar testes estatísticos em números BigInteger para avaliar
 * propriedades relacionadas à aleatoriedade, o que é crucial para aplicações
 * criptográficas.
 *
 * Estes testes analisam a representação binária do número.
 */
public class BigIntegerStatisticalTests {

    /**
     * Teste 1: Teste de Frequência (Monobit).
     *
     * Este teste verifica se o número de zeros e uns na sequência de bits é
     * aproximadamente igual. Para uma sequência verdadeiramente aleatória, o número
     * de uns deve ser cerca de 50% do comprimento total.
     *
     * @param number O BigInteger a ser testado.
     * @return Uma String contendo os resultados do teste.
     */
    public static String frequencyTest(BigInteger number) {
        String binaryString = number.toString(2);
        long countOnes = binaryString.chars().filter(ch -> ch == '1').count();
        long countZeros = binaryString.length() - countOnes;
        int totalBits = binaryString.length();
        double percentOnes = (double) countOnes / totalBits * 100.0;
        double percentZeros = (double) countZeros / totalBits * 100.0;

        StringBuilder result = new StringBuilder();
        result.append("--- Teste de Frequência (Monobit) ---\n");
        result.append(String.format("Total de Bits: %d\n", totalBits));
        result.append(String.format("Número de 0s: %d (%.2f%%)\n", countZeros, percentZeros));
        result.append(String.format("Número de 1s: %d (%.2f%%)\n", countOnes, percentOnes));
        result.append("Conclusão: Para uma sequência aleatória, a proporção de 0s e 1s deve ser próxima de 50%.\n");

        return result.toString();
    }

    /**
     * Teste 2: Teste de Runs.
     *
     * Um "run" é uma sequência contígua de bits idênticos. Este teste verifica
     * se o número de runs, tanto de zeros quanto de uns, está conforme o esperado
     * para uma sequência aleatória. Um número muito alto ou muito baixo de runs
     * pode indicar falta de aleatoriedade.
     *
     * @param number O BigInteger a ser testado.
     * @return Uma String contendo os resultados do teste.
     */
    public static String runsTest(BigInteger number) {
        String binaryString = number.toString(2);
        int n = binaryString.length();
        if (n < 2) {
            return "--- Teste de Runs ---\nNúmero muito pequeno para o teste de runs.\n";
        }

        int runs = 1;
        for (int i = 1; i < n; i++) {
            if (binaryString.charAt(i) != binaryString.charAt(i - 1)) {
                runs++;
            }
        }

        // Para uma sequência aleatória, o número esperado de runs é aproximadamente n/2.

        StringBuilder result = new StringBuilder();
        result.append("--- Teste de Runs ---\n");
        result.append(String.format("Total de Bits: %d\n", n));
        result.append(String.format("Número de Runs (sequências de bits idênticos): %d\n", runs));
        result.append(String.format("Número esperado de runs para uma sequência aleatória: ~%d\n", n / 2));
        result.append("Conclusão: Um número de runs muito distante do esperado pode indicar que a sequência não é aleatória.\n");

        return result.toString();
    }

    /**
     * Teste 3: Teste de Pôquer.
     *
     * Este teste divide a sequência de bits em blocos não sobrepostos de tamanho fixo
     * e verifica se todos os padrões de blocos possíveis ocorrem com frequências
     * semelhantes. Ele utiliza o teste estatístico Qui-quadrado para este fim.
     *
     * @param number O BigInteger a ser testado.
     * @param blockSize O tamanho de cada bloco (ex: 4 bits). Um valor comum.
     * @return Uma String contendo os resultados do teste.
     */
    public static String pokerTest(BigInteger number, int blockSize) {
        String binaryString = number.toString(2);
        int n = binaryString.length();
        if (blockSize <= 0 || blockSize > 16) {
            return "--- Teste de Pôquer ---\nTamanho de bloco inválido. Use um valor entre 1 e 16.\n";
        }

        int numBlocks = n / blockSize;
        if (numBlocks < 1) {
            return "--- Teste de Pôquer ---\nNúmero muito pequeno para o tamanho de bloco especificado.\n";
        }

        // Mapa para armazenar as frequências de cada padrão de bloco
        Map<String, Integer> frequencies = new HashMap<>();
        for (int i = 0; i <= n - blockSize; i += blockSize) {
            String block = binaryString.substring(i, i + blockSize);
            frequencies.put(block, frequencies.getOrDefault(block, 0) + 1);
        }

        // Cálculo do Qui-quadrado: X² = (2^m / k) * SUM(ni²) - k
        // onde m = blockSize, k = numBlocks, ni = frequência do i-ésimo bloco
        double sumOfSquares = 0;
        for (int freq : frequencies.values()) {
            sumOfSquares += (double) freq * freq;
        }

        double chiSquaredValue = (Math.pow(2, blockSize) / numBlocks) * sumOfSquares - numBlocks;

        StringBuilder result = new StringBuilder();
        result.append(String.format("--- Teste de Pôquer (blocos de %d bits) ---\n", blockSize));
        result.append(String.format("Total de Blocos: %d\n", numBlocks));
        result.append(String.format("Valor de Chi-quadrado (X²): %.4f\n", chiSquaredValue));
        // Para referência, valores críticos para m=4 (15 graus de liberdade) são ~25.0 (p=0.05) e ~30.6 (p=0.01)
        result.append("Conclusão: Este valor mede a uniformidade da distribuição dos blocos.\n");
        result.append("Valores baixos de Chi-quadrado indicam uma distribuição uniforme, como esperado em uma sequência aleatória.\n");

        return result.toString();
    }
}