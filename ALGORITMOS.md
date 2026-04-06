# ALGORITMOS - Implementações por Etapa

## Visão Geral

O projeto implementa 4 etapas progressivas, cada uma introduzindo complexidade algorítmica crescente:

1. **Etapa 1**: Movimento Básico (Random Walk + Linha Reta)
2. **Etapa 2**: Movimento Inteligente (Greedy + Ant Colony Optimization)
3. **Etapa 3**: Busca em Largura (BFS - Breadth-First Search)
4. **Etapa 4**: Menor Custo (Dijkstra's Algorithm)

Cada etapa reutiliza componentes core e herda comportamentos da classe base `MovimentoBaseService`.

---

## Classe Base: MovimentoBaseService

### Responsabilidade
Fornece métodos comuns de movimento e atualização de campo para todas as etapas.

### Métodos Principais

#### `mover(Campo, Robo, deltaX, deltaY)`
Movimenta robô aplicando deltas à posição atual.

```java
public boolean mover(Campo campo, Robo robo, int deltaX, int deltaY) {
    int novaX = robo.getPosicaoX() + deltaX;
    int novaY = robo.getPosicaoY() + deltaY;

    if (!ValidadorMovimento.podeAcessar(campo, novaX, novaY)) {
        return false;  // Movimento inválido
    }

    atualizarCampo(campo, robo, novaX, novaY);
    return true;  // Movimento bem-sucedido
}
```

**Parâmetros**:
- `deltaX`: -1 (cima), +1 (baixo), 0 (sem movimento vertical)
- `deltaY`: -1 (esquerda), +1 (direita), 0 (sem movimento horizontal)

**Retorno**: `true` se moveu, `false` se bloqueado.

#### `moverPara(Campo, Robo, Posicao)`
Movimenta robô para posição absoluta especificada.

```java
public boolean moverPara(Campo campo, Robo robo, Posicao destino) {
    int deltaX = destino.getX() - robo.getPosicaoX();
    int deltaY = destino.getY() - robo.getPosicaoY();
    return mover(campo, robo, deltaX, deltaY);
}
```

**Uso típico**: Executar caminho encontrado por algoritmos de busca.

#### `atualizarCampo(Campo, Robo, novaX, novaY)`
Atualiza estado do campo após movimento (método `protected`).

**Lógica**:
1. Marcar célula antiga como VAZIA e visitada
2. Marcar nova célula como OCUPADA (preserva FINAL se for o objetivo)
3. Atualizar posição do robô

```java
protected void atualizarCampo(Campo campo, Robo robo, int novaX, int novaY) {
    // 1. Limpar posição antiga
    Casa casaAntiga = campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY());
    casaAntiga.setTipo(TipoCelula.VAZIA);
    casaAntiga.marcarComoVisitada();
    
    // 2. Ocupar nova posição (preserva FINAL)
    Casa casaNova = campo.getCasa(novaX, novaY);
    if (casaNova.getTipo() != TipoCelula.FINAL) {
        casaNova.setTipo(TipoCelula.OCUPADA);
    }
    
    // 3. Atualizar robô
    robo.setPosicao(novaX, novaY);
}
```

**Decisão de design**: Preservar `TipoCelula.FINAL` permite detectar quando robô alcança objetivo.

#### `verificarChegadaAoFinal(Campo, Robo)`
Verifica se robô alcançou célula FINAL.

```java
public boolean verificarChegadaAoFinal(Campo campo, Robo robo) {
    Casa casaAtual = campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY());
    return casaAtual.getTipo() == TipoCelula.FINAL;
}
```

#### Métodos Direcionais
Atalhos para movimentos específicos:
- `moverParaCima()`: `mover(campo, robo, -1, 0)`
- `moverParaBaixo()`: `mover(campo, robo, 1, 0)`
- `moverParaEsquerda()`: `mover(campo, robo, 0, -1)`
- `moverParaDireita()`: `mover(campo, robo, 0, 1)`

---

## ETAPA 1: Movimento Básico

### Objetivo
Demonstrar navegação simples em campo vazio, sem obstáculos.

### Algoritmos Implementados

---

### 1.1 Movimento Aleatório (Random Walk)

**Classe**: `MovimentoAleatorioService`  
**Arquivo**: `etapa1/service/MovimentoAleatorioService.java`

#### Descrição
Escolhe direção aleatória entre vizinhos válidos a cada passo.

#### Algoritmo

```
FUNÇÃO moverAleatorio(campo, robo):
    1. vizinhos ← obter vizinhos válidos da posição atual
    2. SE vizinhos está vazio:
        RETORNAR false (robô preso)
    3. posição ← escolher aleatoriamente de vizinhos
    4. mover robô para posição
    5. RETORNAR true
```

#### Implementação

```java
public boolean moverAleatorio(Campo campo, Robo robo) {
    // 1. Obter vizinhos acessíveis
    List<Posicao> movimentosPossiveis = ValidadorMovimento.obterVizinhosValidos(
        campo, 
        robo.getPosicaoX(), 
        robo.getPosicaoY()
    );

    // 2. Verificar se há movimentos possíveis
    if (movimentosPossiveis.isEmpty()) {
        return false;
    }

    // 3. Escolher aleatoriamente
    Posicao posicaoEscolhida = movimentosPossiveis.get(
        random.nextInt(movimentosPossiveis.size())
    );
    
    // 4. Executar movimento
    return moverPara(campo, robo, posicaoEscolhida);
}
```

#### Características

**Complexidade**:
- **Tempo**: O(1) amortizado (validação de 4 vizinhos é constante)
- **Espaço**: O(1) (lista de vizinhos tem no máximo 4 elementos)

**Comportamento**:
- Completamente não-determinístico
- Pode visitar mesma célula múltiplas vezes
- Não garante encontrar objetivo (movimento infinito possível)
- Útil para exploração inicial ou simulações estocásticas

**Seed para Reprodutibilidade**:
```java
MovimentoAleatorioService service = new MovimentoAleatorioService(42L);
// Sequência de movimentos será sempre a mesma
```

---

### 1.2 Movimento em Linha Reta

**Classe**: `MovimentoLinhaRetaService`  
**Arquivo**: `etapa1/service/MovimentoLinhaRetaService.java`

#### Descrição
Mantém direção atual enquanto possível. Ao bater em obstáculo ou borda, escolhe nova direção aleatória.

#### Algoritmo

```
VARIÁVEIS DE ESTADO:
    direcaoAtual ← 0..3  // 0=cima, 1=direita, 2=baixo, 3=esquerda
    primeiroDirecao ← true

FUNÇÃO moverLinhaReta(campo, robo):
    1. SE primeiroDirecao:
        direcaoAtual ← aleatório(0..3)
        primeiroDirecao ← false
    
    2. tentativas ← 0
    3. ENQUANTO tentativas < 4 E não moveu:
        a. calcular nova posição na direcaoAtual
        b. SE posição válida e acessível:
            - mover robô
            - moveu ← true
        c. SENÃO:
            - direcaoAtual ← aleatório(0..3)
            - tentativas++
    
    4. RETORNAR moveu
```

#### Implementação

```java
public boolean moverLinhaReta(Campo campo, Robo robo) {
    // 1. Inicializar direção na primeira chamada
    if (primeiroDirecao) {
        direcaoAtual = random.nextInt(4);
        primeiroDirecao = false;
    }
    
    int tentativas = 0;
    boolean moveu = false;
    
    // 2. Tentar até 4 direções diferentes
    while (tentativas < 4 && !moveu) {
        int novoX = robo.getPosicaoX();
        int novoY = robo.getPosicaoY();
        
        // 3. Aplicar delta baseado na direção
        switch (direcaoAtual) {
            case 0: novoX--; break; // Cima
            case 1: novoY++; break; // Direita
            case 2: novoX++; break; // Baixo
            case 3: novoY--; break; // Esquerda
        }
        
        // 4. Validar e mover
        if (ValidadorMovimento.podeAcessar(campo, novoX, novoY)) {
            atualizarCampo(campo, robo, novoX, novoY);
            moveu = true;
        } else {
            // Obstáculo! Escolher nova direção
            direcaoAtual = random.nextInt(4);
            tentativas++;
        }
    }
    
    return moveu;
}
```

#### Método Auxiliar

```java
public void resetarDirecao() {
    primeiroDirecao = true;
}
```
**Uso**: Chamado ao reiniciar simulação para garantir nova direção inicial.

#### Características

**Complexidade**:
- **Tempo**: O(1) (máximo 4 tentativas)
- **Espaço**: O(1)

**Comportamento**:
- Movimento mais "natural" que random puro
- Tende a criar padrões lineares
- Menos mudanças de direção que movimento aleatório
- Em campo aberto, explora uma direção completamente antes de mudar

**Comparação com Random Walk**:
| Aspecto | Random Walk | Linha Reta |
|---------|-------------|------------|
| Mudança de direção | A cada passo | Apenas ao bater |
| Padrão de movimento | Errático | Linear com cantos |
| Cobertura | Uniforme | Faixas lineares |
| Eficiência | Baixa | Média |

---

## ETAPA 2: Movimento Inteligente

### Objetivo
Implementar estratégias que evitam revisitar células, utilizando memória de exploração.

### Algoritmos Implementados

---

### 2.1 Movimento Inteligente Greedy (Burro)

**Classe**: `MovimentoInteligenteService`  
**Arquivo**: `etapa2/service/MovimentoInteligenteService.java`

#### Descrição
Prioriza células não visitadas. Só revisita células já exploradas quando não há alternativa.

#### Algoritmo

```
FUNÇÃO moverInteligente(campo, robo):
    1. vizinhos ← obter vizinhos válidos
    2. SE vizinhos está vazio:
        RETORNAR false
    
    3. naoVisitadas ← filtrar vizinhos não visitados
    4. visitadas ← filtrar vizinhos visitados
    
    5. SE naoVisitadas não vazio:
        escolher aleatoriamente de naoVisitadas
        RETORNAR true
    6. SENÃO SE visitadas não vazio:
        escolher aleatoriamente de visitadas (backtrack)
        RETORNAR true
    7. SENÃO:
        RETORNAR false
```

#### Implementação

```java
public boolean moverInteligente(Campo campo, Robo robo) {
    // 1. Obter vizinhos acessíveis
    List<Posicao> vizinhosValidos = ValidadorMovimento.obterVizinhosValidos(
        campo, robo.getPosicaoX(), robo.getPosicaoY()
    );

    if (vizinhosValidos.isEmpty()) {
        return false;
    }

    // 2. Separar visitadas e não visitadas
    List<Posicao> naoVisitadas = filtrarNaoVisitadas(campo, vizinhosValidos);
    List<Posicao> visitadas = filtrarVisitadas(campo, vizinhosValidos);

    // 3. Priorizar não visitadas
    if (!naoVisitadas.isEmpty()) {
        Posicao posicaoEscolhida = naoVisitadas.get(random.nextInt(naoVisitadas.size()));
        return moverPara(campo, robo, posicaoEscolhida);
    } 
    // 4. Fallback: revisitar células
    else if (!visitadas.isEmpty()) {
        Posicao posicaoEscolhida = visitadas.get(random.nextInt(visitadas.size()));
        return moverPara(campo, robo, posicaoEscolhida);
    }

    return false;
}
```

#### Métodos Auxiliares

```java
private List<Posicao> filtrarNaoVisitadas(Campo campo, List<Posicao> posicoes) {
    List<Posicao> naoVisitadas = new ArrayList<>();
    for (Posicao pos : posicoes) {
        if (!campo.getCasa(pos.getX(), pos.getY()).isVisitada()) {
            naoVisitadas.add(pos);
        }
    }
    return naoVisitadas;
}
```

#### Características

**Complexidade**:
- **Tempo**: O(V) onde V = número de vizinhos (4 no máximo) → O(1)
- **Espaço**: O(V) → O(1)

**Vantagens**:
- Evita loops óbvios
- Maior cobertura do mapa em menos passos
- Backtracking natural em becos sem saída

**Desvantagens**:
- Não garante encontrar caminho mais curto
- Pode entrar em loops macro (ciclo de 10+ células)
- Sem memória de longo prazo (apenas visitada/não visitada)

**Por que "Burro"?**
- Nome coloquial escolhido pelo usuário
- Inteligência limitada: apenas evita imediato, sem planejamento
- Não aprende com tentativas anteriores

---

### 2.2 Movimento com Feromônio (ACO - Ant Colony Optimization)

**Classe**: `MovimentoFeromonioService`  
**Arquivo**: `etapa2/service/MovimentoFeromonioService.java`

#### Descrição
Inspirado em formigas: células acumulam "feromônio" (contador de visitas). Robô prefere células com menos feromônio.

#### Algoritmo

```
FUNÇÃO moverComFeromonio(campo, robo):
    1. vizinhos ← obter vizinhos válidos
    2. SE vizinhos vazio:
        RETORNAR false
    
    3. melhorPosicao ← escolherMelhorPosicao(vizinhos)
    4. incrementar feromônio da célula atual
    5. mover para melhorPosicao
    6. RETORNAR true

FUNÇÃO escolherMelhorPosicao(vizinhos):
    1. menorContador ← ∞
    2. PARA CADA vizinho EM vizinhos:
        SE vizinho.contadorVisitas < menorContador:
            menorContador ← vizinho.contadorVisitas
    
    3. melhoresOpcoes ← filtrar vizinhos com contador == menorContador
    4. RETORNAR aleatório de melhoresOpcoes
```

#### Implementação

```java
public boolean moverComFeromonio(Campo campo, Robo robo) {
    // 1. Obter vizinhos acessíveis
    List<Posicao> vizinhosValidos = ValidadorMovimento.obterVizinhosValidos(
        campo, robo.getPosicaoX(), robo.getPosicaoY()
    );
    
    if (vizinhosValidos.isEmpty()) {
        return false;
    }
    
    // 2. Escolher célula com menor contador (menos visitada)
    Posicao melhorPosicao = escolherMelhorPosicao(campo, vizinhosValidos);
    
    // 3. IMPORTANTE: Incrementar feromônio ANTES de sair
    Casa casaAtual = campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY());
    casaAtual.incrementarVisitas();
    
    // 4. Mover
    int deltaX = melhorPosicao.getX() - robo.getPosicaoX();
    int deltaY = melhorPosicao.getY() - robo.getPosicaoY();
    
    return mover(campo, robo, deltaX, deltaY);
}
```

#### Seleção da Melhor Posição

```java
private Posicao escolherMelhorPosicao(Campo campo, List<Posicao> vizinhos) {
    // 1. Encontrar menor contador entre vizinhos
    int menorContador = Integer.MAX_VALUE;
    
    for (Posicao pos : vizinhos) {
        Casa casa = campo.getCasa(pos);
        int contador = casa.getContadorVisitas();
        
        if (contador < menorContador) {
            menorContador = contador;
        }
    }
    
    // 2. Filtrar todas as células com menor contador (podem ser múltiplas!)
    final int contadorMinimo = menorContador;
    List<Posicao> melhoresOpcoes = vizinhos.stream()
        .filter(pos -> campo.getCasa(pos).getContadorVisitas() == contadorMinimo)
        .collect(Collectors.toList());
    
    // 3. Escolher aleatoriamente entre empates
    return melhoresOpcoes.get(random.nextInt(melhoresOpcoes.size()));
}
```

#### Características

**Complexidade**:
- **Tempo**: O(V) onde V = vizinhos → O(1)
- **Espaço**: O(V) → O(1)

**Lógica de Feromônio**:
1. **Acumulação**: Contador incrementa a cada visita (não decai)
2. **Repulsão**: Alto contador = célula "cheirosa" = evitar
3. **Exploração**: Contador 0 = célula virgem = atrativo
4. **Backtracking**: Em beco sem saída, volta por caminho com menor contador

**Exemplo Prático**:

```
Situação: Robô em beco sem saída
┌───┬───┬───┐
│ 1 │ 0 │ 0 │  Contadores de visita
├───┼───┼───┤
│ 2 │ R │███│  R = Robô, ███ = Parede
├───┼───┼───┤
│ 1 │███│███│
└───┴───┴───┘

Vizinhos válidos: (0,0)[1], (0,1)[0], (1,0)[2]
Menor contador: 0
Robô escolhe: (0,1)
```

**Diferença para Greedy**:
| Aspecto | Greedy | Feromônio |
|---------|--------|-----------|
| Memória | Binária (visitada/não) | Acumulativa (contador) |
| Decisão | Qualquer não visitada | Menos visitada |
| Backtrack | Qualquer visitada | Visitada com menos frequência |
| Loops | Possível | Menos provável |

**Por que funciona?**
- Becos sem saída acumulam alto contador (múltiplas tentativas)
- Próxima vez, robô evita beco (alto feromônio)
- Força exploração de caminhos alternativos
- Auto-balanceamento: caminho explorado demais perde atratividade

---

## ETAPA 3: Busca em Largura (BFS)

### Objetivo
Encontrar caminho mais curto (menor número de passos) entre posição inicial e final.

### Algoritmo Implementado

---

### 3.1 BFS (Breadth-First Search)

**Classe**: `BuscaService`  
**Arquivo**: `etapa3/service/BuscaService.java`

#### Descrição
Explora campo em ondas, garantindo caminho com menor número de passos.

#### Algoritmo Clássico

```
FUNÇÃO buscarCaminhoBFS(campo, inicio, fim):
    1. fila ← fila vazia
    2. visitadas ← conjunto vazio
    3. predecessores ← mapa vazio
    
    4. adicionar inicio à fila
    5. marcar inicio como visitada
    6. predecessores[inicio] ← null
    
    7. ENQUANTO fila não vazia:
        a. atual ← remover primeiro da fila
        
        b. SE atual == fim:
            caminho ← reconstruirCaminho(predecessores, inicio, fim)
            RETORNAR ResultadoBusca(caminho, true, visitadas)
        
        c. PARA CADA vizinho EM vizinhos válidos de atual:
            SE vizinho não visitado:
                - marcar vizinho como visitado
                - predecessores[vizinho] ← atual
                - adicionar vizinho à fila
    
    8. RETORNAR ResultadoBusca.semCaminho(visitadas)
```

#### Implementação

```java
public ResultadoBusca buscarCaminhoBFS(Campo campo, Posicao inicio, Posicao fim) {
    // 1. Estruturas de dados
    Queue<Posicao> fila = new LinkedList<>();
    Map<Posicao, Posicao> predecessores = new HashMap<>();
    Set<Posicao> visitadas = new HashSet<>();
    
    // 2. Inicialização
    fila.add(inicio);
    visitadas.add(inicio);
    predecessores.put(inicio, null);
    
    // 3. Exploração em largura
    while (!fila.isEmpty()) {
        Posicao atual = fila.poll();
        
        // 4. Objetivo encontrado?
        if (atual.equals(fim)) {
            List<Posicao> caminho = reconstruirCaminho(predecessores, inicio, fim);
            return new ResultadoBusca(caminho, true, visitadas);
        }
        
        // 5. Explorar vizinhos
        List<Posicao> vizinhos = ValidadorMovimento.obterVizinhosValidos(campo, atual);
        
        for (Posicao vizinho : vizinhos) {
            if (!visitadas.contains(vizinho)) {
                visitadas.add(vizinho);
                predecessores.put(vizinho, atual);
                fila.add(vizinho);
            }
        }
    }
    
    // 6. Caminho não encontrado
    return ResultadoBusca.semCaminho(visitadas);
}
```

#### Reconstrução de Caminho

```java
private List<Posicao> reconstruirCaminho(Map<Posicao, Posicao> predecessores, 
                                         Posicao inicio, Posicao fim) {
    List<Posicao> caminho = new ArrayList<>();
    Posicao atual = fim;
    
    // Percorrer de trás para frente
    while (atual != null) {
        caminho.add(0, atual);  // Adiciona no início
        atual = predecessores.get(atual);
    }
    
    return caminho;
}
```

#### Características

**Complexidade**:
- **Tempo**: O(V + E) onde V = vértices (células), E = arestas (conexões)
  - Grid 10x10: V = 100, E ≈ 360 → O(460) ≈ O(1) para este problema
- **Espaço**: O(V) para fila, visitadas e predecessores

**Propriedades**:
- ✓ **Completude**: Sempre encontra caminho se existir
- ✓ **Otimalidade**: Garante menor número de passos
- ✓ **Não ponderado**: Considera todas as arestas com peso 1

**Estruturas de Dados Utilizadas**:
- `Queue<Posicao>`: Fila FIFO para exploração por níveis
- `Set<Posicao>`: Marca células já visitadas (evita reprocessamento)
- `Map<Posicao, Posicao>`: Rastreia predecessor de cada célula (para reconstruir caminho)

**Exemplo de Exploração**:

```
Início: (0,0), Fim: (2,2)

Passo 1:
F  ·  ·     Fila: [(0,0)]
·  ·  ·     
·  ·  T     

Passo 2:
F  1  ·     Fila: [(1,0), (0,1)]
1  ·  ·     Explore (0,0) → adiciona vizinhos
·  ·  T     

Passo 3:
F  1  2     Fila: [(0,1), (2,0), (1,1)]
1  2  ·     Explore (1,0) → adiciona (2,0)
2  ·  T     

Passo 4:
F  1  2     Fila: [(2,0), (1,1), (0,2)]
1  2  3     Explore (0,1) → adiciona (0,2)
2  3  T     

Passo 5 (objetivo):
F  1  2     Encontrou (2,2)!
1  2  3     Reconstruir: (2,2)←(1,2)←(1,1)←(0,1)←(0,0)
2  3  T✓    Caminho: [(0,0), (0,1), (1,1), (1,2), (2,2)]
```

**Modelo ResultadoBusca**:
```java
public class ResultadoBusca {
    private final List<Posicao> caminho;
    private final boolean caminhoEncontrado;
    private final Set<Posicao> nosExplorados;
    private final int totalPassos;
}
```

**Visualização na GUI**:
- Células exploradas: Azul claro (ondas de BFS)
- Caminho final: Estrelas douradas (★)
- Animação: Mostra exploração antes de mover robô

---

## ETAPA 4: Algoritmo de Dijkstra

### Objetivo
Encontrar caminho com menor custo total, considerando pesos das células.

### Algoritmo Implementado

---

### 4.1 Dijkstra (Shortest Path)

**Classe**: `DijkstraService`  
**Arquivo**: `etapa4/service/DijkstraService.java`

#### Descrição
Variação de BFS que usa fila de prioridade, processando sempre o nó com menor custo acumulado.

#### Algoritmo

```
FUNÇÃO calcularMenorCusto(campo, inicio, fim):
    1. filaPrioridade ← min-heap vazia
    2. custos ← mapa {Posicao → Integer}
    3. predecessores ← mapa {Posicao → Posicao}
    4. visitados ← conjunto vazio
    
    5. adicionar (inicio, custo=0) à filaPrioridade
    6. custos[inicio] ← 0
    
    7. ENQUANTO filaPrioridade não vazia:
        a. noAtual ← extrair mínimo da fila
        b. SE noAtual já visitado:
            CONTINUAR (skip)
        c. marcar noAtual como visitado
        
        d. SE noAtual.posicao == fim:
            caminho ← reconstruirCaminho(predecessores, inicio, fim)
            custoTotal ← custos[fim]
            RETORNAR ResultadoDijkstra(caminho, true, custoTotal)
        
        e. PARA CADA vizinho EM vizinhos válidos:
            SE vizinho já visitado:
                CONTINUAR
            
            custoVizinho ← campo.getCasa(vizinho).getValor()
            SE custoVizinho == 0: custoVizinho ← 1
            
            novoCusto ← custos[noAtual] + custoVizinho
            
            SE novoCusto < custos[vizinho]:
                custos[vizinho] ← novoCusto
                predecessores[vizinho] ← noAtual
                adicionar (vizinho, novoCusto) à filaPrioridade
    
    8. RETORNAR ResultadoDijkstra.semCaminho()
```

#### Implementação

```java
public ResultadoDijkstra calcularMenorCusto(Campo campo, Posicao inicio, Posicao fim) {
    // 1. Estruturas de dados
    PriorityQueue<NoCaminho> filaPrioridade = new PriorityQueue<>();
    Map<Posicao, Integer> custos = new HashMap<>();
    Map<Posicao, Posicao> predecessores = new HashMap<>();
    Set<Posicao> visitados = new HashSet<>();
    
    // 2. Inicialização
    filaPrioridade.add(new NoCaminho(inicio, 0, null));
    custos.put(inicio, 0);
    
    // 3. Loop principal
    while (!filaPrioridade.isEmpty()) {
        NoCaminho noAtual = filaPrioridade.poll();
        Posicao posicaoAtual = noAtual.getPosicao();
        
        // 4. Skip se já visitado (pode haver duplicatas na fila)
        if (visitados.contains(posicaoAtual)) {
            continue;
        }
        
        visitados.add(posicaoAtual);
        
        // 5. Objetivo alcançado?
        if (posicaoAtual.equals(fim)) {
            List<Posicao> caminho = reconstruirCaminho(predecessores, inicio, fim);
            int custoTotal = custos.get(fim);
            return new ResultadoDijkstra(caminho, true, custoTotal);
        }
        
        // 6. Relaxamento de arestas
        List<Posicao> vizinhos = ValidadorMovimento.obterVizinhosValidos(campo, posicaoAtual);
        
        for (Posicao vizinho : vizinhos) {
            if (visitados.contains(vizinho)) {
                continue;
            }
            
            // Obter custo da célula (peso)
            Casa casaVizinha = campo.getCasa(vizinho.getX(), vizinho.getY());
            int custoVizinho = casaVizinha.getValor();
            if (custoVizinho == 0) custoVizinho = 1;  // Default
            
            int novoCusto = custos.get(posicaoAtual) + custoVizinho;
            
            // Se encontrou caminho mais barato, atualizar
            if (novoCusto < custos.getOrDefault(vizinho, Integer.MAX_VALUE)) {
                custos.put(vizinho, novoCusto);
                predecessores.put(vizinho, posicaoAtual);
                filaPrioridade.add(new NoCaminho(vizinho, novoCusto, noAtual));
            }
        }
    }
    
    return ResultadoDijkstra.semCaminho();
}
```

#### Modelo NoCaminho

```java
public class NoCaminho implements Comparable<NoCaminho> {
    private final Posicao posicao;
    private final int custoAcumulado;
    private final NoCaminho pai;
    
    @Override
    public int compareTo(NoCaminho outro) {
        return Integer.compare(this.custoAcumulado, outro.custoAcumulado);
    }
}
```

**Por que `Comparable`?**
- `PriorityQueue` precisa saber como ordenar nós
- Nós com menor custo têm prioridade (min-heap)

#### Características

**Complexidade**:
- **Tempo**: O((V + E) log V) com heap binário
  - Grid 10x10: O(460 log 100) ≈ O(920)
- **Espaço**: O(V) para mapas e fila

**Propriedades**:
- ✓ **Completude**: Encontra caminho se existir
- ✓ **Otimalidade**: Garante menor custo total
- ✓ **Ponderado**: Considera pesos das células
- ✗ **Não funciona com pesos negativos** (não há no nosso caso)

**Diferença para BFS**:
| Aspecto | BFS | Dijkstra |
|---------|-----|----------|
| Estrutura | `Queue` (FIFO) | `PriorityQueue` (min-heap) |
| Critério | Ordem de descoberta | Custo acumulado |
| Pesos | Ignora (assume 1) | Considera |
| Resultado | Menos passos | Menor custo total |

**Exemplo Comparativo**:

```
Campo com pesos:
┌───┬───┬───┐
│ I │ 1 │ 5 │
├───┼───┼───┤
│ 1 │ 9 │ 1 │
├───┼───┼───┤
│ 5 │ 1 │ F │
└───┴───┴───┘

BFS escolhe:
I → (1,0) → (2,0) → (2,1) → (2,2) = F
Passos: 4
Custo: 1 + 5 + 1 + 1 = 8

Dijkstra escolhe:
I → (0,1) → (1,0) → (1,2) → (2,2) = F
Passos: 4 (mesmo)
Custo: 1 + 1 + 1 + 1 = 4 ✓ MENOR
```

**Relaxamento de Arestas**:
Conceito central do Dijkstra:
```
SE custo_atual + peso_aresta < custo_conhecido_do_vizinho:
    atualizar custo_do_vizinho
    atualizar predecessor
    adicionar à fila
```

**Heat Map de Pesos (GUI)**:
- Peso 0-3: Branco/Rosa claro
- Peso 4-7: Laranja
- Peso 8-10: Vermelho intenso
- Opacidade aumenta com peso

---

## Resumo Comparativo

### Complexidade Assintótica

| Algoritmo | Tempo | Espaço | Garantia |
|-----------|-------|--------|----------|
| Random Walk | O(∞) | O(1) | Nenhuma |
| Linha Reta | O(∞) | O(1) | Nenhuma |
| Greedy | O(∞) | O(1) | Nenhuma |
| Feromônio | O(∞) | O(V) | Nenhuma |
| BFS | O(V+E) | O(V) | Menor passos |
| Dijkstra | O((V+E)logV) | O(V) | Menor custo |

### Quando Usar Cada Algoritmo?

**Etapa 1 (Random/Linha Reta)**:
- Sem objetivo definido
- Exploração livre
- Demonstração de movimentos básicos

**Etapa 2 (Greedy/Feromônio)**:
- Objetivo definido mas posição desconhecida
- Exploração inteligente
- Trade-off memória vs. eficiência

**Etapa 3 (BFS)**:
- Objetivo conhecido
- Todas células têm custo uniforme
- Quer menor número de passos

**Etapa 4 (Dijkstra)**:
- Objetivo conhecido
- Células têm custos variados
- Quer menor custo total (pode não ser menor número de passos)

---

## Visualização dos Algoritmos (GUI)

### Etapa 1 & 2: Movimento em Tempo Real
- Timeline JavaFX atualiza grid a cada N milissegundos
- Robô se move imediatamente (sem pré-computação)
- Células visitadas ficam cinza claro

### Etapa 3 & 4: Busca + Execução
1. **Fase de Busca**: 
   - Animação mostra células sendo exploradas (azul)
   - Rápido (100-200ms entre frames)
2. **Fase de Execução**:
   - Robô segue caminho encontrado
   - Marcação de estrelas (★) no caminho
   - Velocidade ajustável (1x-10x)

### Heat Maps
- **Feromônio (Etapa 2)**: Gradiente vermelho baseado em `contadorVisitas`
- **Pesos (Etapa 4)**: Gradiente RGB baseado em `valor` da célula

---

## Conclusão

O projeto implementa uma progressão didática de algoritmos:
1. **Aleatoriedade pura** → Movimento sem memória
2. **Memória local** → Evitar revisitas imediatas
3. **Busca global** → Planejamento ótimo (passos)
4. **Otimização ponderada** → Planejamento ótimo (custo)

Cada etapa reutiliza componentes anteriores (herança, composição) e introduz novos conceitos de forma incremental, tornando o projeto ideal para fins educacionais.
