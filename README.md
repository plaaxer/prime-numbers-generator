
# Prime Numbers Generator

Implementação de algoritmos de geração de números pseudo-aleatórios e testes de primalidade em Java, desenvolvida para a disciplina de Segurança em Computação (INE5429) da UFSC.
Este projeto é capaz de gerar números (provavelmente) primos de até 4096 bits e analisar suas propriedades estatísticas.

## Pré-requisitos

- Java Development Kit (JDK) versão 8 ou superior.

## Compilação

Navegue até a pasta raiz do projeto (`prime-numbers-generator/src/main/java`) e execute o seguinte comando para compilar todos os arquivos Java:

```bash
javac primos/*.java primos/generators/*.java primos/primality/*.java
```

## Execução

O programa é executado através da linha de comando, especificando o gerador, o testador e os parâmetros desejados. Deve ser executado no diretório `java/`.

### Sintaxe

```bash
java Main [opções] <gerador> <testador> <bits> <certeza>
```

**Argumentos:**

-   `[opções]` (opcional):
    -   `-nt`: Não truncar a saída do número primo encontrado.
    - `-t`: Executa uma série de benchmarks.
    - `-stat-freq:` Executa o Teste de Frequência (Monobit) no primo gerado.
    - `-stat-runs:` Executa o Teste de Runs no primo gerado.
    - `-stat-poker:` Executa o Teste de Pôquer no primo gerado.
    - `-stat-all:` Executa todos os testes estatísticos disponíveis.
-   `<gerador>`: O algoritmo gerador a ser usado.
    -   Opções: `LCG`, `BBS`
-   `<testador>`: O algoritmo de teste de primalidade.
    -   Opções: `MillerRabin`, `Fermat`
-   `<bits>`: O tamanho em bits do número primo a ser encontrado (ex: `256`).
-   `<certeza>`: O número de iterações do teste de primalidade (ex: `100`).

### Exemplos de Uso

**1. Encontrar um primo de 256 bits usando BBS e Miller-Rabin e realizar todos os testes estatísticos sobre ele::**

```bash
java primos.Main -stat-all BBS MillerRabin 256 100
```

**2. Encontrar um primo de 512 bits usando LCG e Fermat, mostrando o número completo:**

```bash
java primos.Main -nt LCG Fermat 512 100
```

### Testes

Com a flag -t adicionada, uma série de testes e benchmarks dos geradores e testadores são executados, inclusive os testes com números de Carmichael comparando Miller-Rabin com Fermat.