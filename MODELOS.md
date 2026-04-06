# MODELOS DE DADOS - Core Models

## Visão Geral

Os modelos de dados formam a fundação do simulador, representando as entidades do domínio e suas interações. Todos os modelos estão localizados em `src/main/java/com/projetorobo/core/` e seguem princípios de Clean Code, encapsulamento e imutabilidade quando apropriado.

---

## 1. Campo.java - Grid do Labirinto

### Responsabilidade
Representa o campo de jogo 10x10, encapsulando a matriz de células e fornecendo métodos seguros para acesso e manipulação.

### Estrutura

```java
public class Campo {
    private final int linhas;              // Altura do grid (final = imutável)
    private final int colunas;             // Largura do grid (final = imutável)
    private final int[][] campoPosicao;    // Matriz de IDs (uso legado)
    private final Casa[][] matrizCasas;    // Matriz de objetos Casa
}
```

### Características

**1. Encapsulamento Total**
- Matrizes são `private final` e nunca expostas diretamente
- Acesso controlado via métodos `getCasa(x, y)` e `setCasa(x, y, Casa)`
- Previne modificações não autorizadas

**2. Validação de Limites**
- Todos os métodos de acesso validam coordenadas antes de acessar a matriz
- Lança `IndexOutOfBoundsException` com mensagem clara se posição inválida
- Método `posicaoValida(x, y)` retorna `boolean` para validação preventiva

**3. Inicialização Segura**
- Construtor inicializa TODAS as células com `Casa` padrão (VAZIA)
- Previne `NullPointerException` ao acessar células

### Métodos Principais

#### Construtores

```java
public Campo(int linhas, int colunas)
```
- Cria campo com dimensões especificadas
- Inicializa todas as casas como VAZIA
- Chamado tipicamente com `new Campo(10, 10)`

#### Validação

```java
public boolean posicaoValida(int x, int y)
```
- Verifica se coordenadas estão dentro dos limites [0-9]
- Usado antes de qualquer acesso à matriz
- **Exemplo**: `if (campo.posicaoValida(5, 3)) { ... }`

```java
public boolean posicaoValida(Posicao posicao)
```
- Sobrecarga que aceita objeto `Posicao`
- Extrai x e y internamente

#### Acesso a Células

```java
public Casa getCasa(int linha, int coluna)
```
- Retorna objeto `Casa` na posição especificada
- Lança exceção se posição inválida
- **Uso**: `Casa casa = campo.getCasa(2, 5);`

```java
public Casa getCasa(Posicao posicao)
```
- Sobrecarga que aceita `Posicao`
- **Uso**: `Casa casa = campo.getCasa(robo.getPosicao());`

```java
public void setCasa(int linha, int coluna, Casa casa)
```
- Substitui `Casa` em uma posição
- Valida limites antes de atribuir
- **Uso**: `campo.setCasa(3, 4, new Casa(TipoCelula.PAREDE, 0, false));`

#### Operações em Lote

```java
public void resetarTodasVisitacoes()
```
- Marca todas as células como não visitadas
- Usado ao resetar simulação
- **Complexidade**: O(n²) onde n = tamanho do lado

#### Visualização (Terminal)

```java
public void exibirCampo()
```
- Renderiza campo ASCII no terminal com bordas desenhadas
- Símbolos:
  - `   ` (espaço triplo): Casa VAZIA não visitada
  - ` . ` (ponto): Casa VAZIA visitada
  - ` O ` (letra O): Casa OCUPADA (robô)
  - `███` (blocos): PAREDE
  - ` F ` (letra F): FINAL (objetivo)

**Exemplo de saída**:

```
╔════════════════════════════════════════════════════╗
║         CAMPO DE MOVIMENTAÇÃO 10x10              ║
╚════════════════════════════════════════════════════╝

      0   1   2   3   4   5   6   7   8   9  
    ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐
 0  │ O │ . │   │   │   │   │   │   │   │   │
    ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
 1  │ . │   │   │   │   │   │   │   │   │   │
    ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
 2  │   │   │███│███│███│   │   │   │   │   │
    ...
```

### Design Decisions

**Por que `int[][] campoPosicao` e `Casa[][] matrizCasas`?**
- `campoPosicao`: Mantido para compatibilidade legada, mas pouco usado
- `matrizCasas`: Matriz principal, contém objetos ricos com tipo, peso, visitada

**Por que métodos de validação?**
- Previne erros comuns de `ArrayIndexOutOfBoundsException`
- Fornece feedback claro ao desenvolvedor (mensagem de erro)
- Permite validação preventiva em algoritmos de busca

---

## 2. Casa.java - Célula Individual

### Responsabilidade
Representa uma célula única do grid, com tipo, peso, estado de visitação e contador de visitas para feromônios.

### Estrutura

```java
public class Casa {
    private TipoCelula tipo;           // VAZIA, OCUPADA, PAREDE, FINAL
    private int valor;                 // Peso para Dijkstra
    private boolean visitada;          // Flag de visitação
    private int contadorVisitas;       // Para sistema de feromônios
}
```

### Atributos Detalhados

#### 1. `TipoCelula tipo`
- **Valores**: `VAZIA`, `OCUPADA`, `PAREDE`, `FINAL`
- **Mutável**: Sim (células mudam de tipo durante simulação)
- **Uso**: Determina renderização visual e se célula pode ser acessada

#### 2. `int valor`
- **Propósito**: Peso/custo da célula para algoritmo Dijkstra
- **Range**: Tipicamente 1-10 (mas sem limite hard-coded)
- **Default**: 0 (interpretado como 1 em algoritmos)
- **Uso**: `int custoTotal = caminhoPercorrido.stream().mapToInt(p -> campo.getCasa(p).getValor()).sum();`

#### 3. `boolean visitada`
- **Propósito**: Rastreamento de exploração durante movimento
- **Uso**: Algoritmos inteligentes priorizam células não visitadas
- **Reset**: Chamado em `resetarVisitacao()` ou `resetarTodasVisitacoes()` do Campo

#### 4. `int contadorVisitas`
- **Propósito**: Sistema de feromônios (Ant Colony Optimization)
- **Lógica**: Quanto maior o contador, menos atrativa a célula
- **Incremento**: `incrementarVisitas()` chamado a cada passagem do robô
- **Reset**: `resetarContadorVisitas()` ao reiniciar simulação

### Construtores

```java
public Casa()
```
- Construtor padrão: VAZIA, valor=0, não visitada, contador=0

```java
public Casa(TipoCelula tipo, int valor, boolean visitada)
```
- Construtor parametrizado para inicializações específicas

### Métodos Principais

#### Validação de Acesso

```java
public boolean podeSerAcessada()
```
- **Retorna**: `true` se tipo != PAREDE e tipo != OCUPADA
- **Lógica**: `return tipo != TipoCelula.PAREDE && tipo != TipoCelula.OCUPADA;`
- **Uso crítico**: Validação antes de mover robô

**Por que não FINAL?**
- FINAL pode ser acessado (é o objetivo!)
- PAREDE bloqueia completamente
- OCUPADA representa posição atual do robô (não pode ter 2 robôs na mesma célula)

#### Controle de Visitação

```java
public void marcarComoVisitada()
```
- Define `visitada = true`
- Chamado quando robô pisa na célula
- **Não retorna ao estado false automaticamente**

```java
public void resetarVisitacao()
```
- Define `visitada = false`
- Usado ao recomeçar simulação

#### Sistema de Feromônios

```java
public void incrementarVisitas()
```
- Incrementa `contadorVisitas++`
- Usado em `MovimentoFeromonioService` (Etapa 2)
- Acumula ao longo de toda a simulação

```java
public int getContadorVisitas()
```
- Retorna valor atual do contador
- Usado para calcular probabilidade de escolha

```java
public void resetarContadorVisitas()
```
- `contadorVisitas = 0`
- Resetar entre simulações

### Exemplo de Uso Completo

```java
// Configurar parede
Casa casa = campo.getCasa(2, 3);
casa.setTipo(TipoCelula.PAREDE);

// Verificar se pode acessar
if (casa.podeSerAcessada()) {  // false
    robo.setPosicao(2, 3);
}

// Configurar célula com peso alto
Casa casaDificil = campo.getCasa(5, 5);
casaDificil.setValor(9);  // Custo alto

// Marcar como visitada
casaDificil.marcarComoVisitada();

// Sistema de feromônio
casaDificil.incrementarVisitas();  // contadorVisitas = 1
casaDificil.incrementarVisitas();  // contadorVisitas = 2
int feromonios = casaDificil.getContadorVisitas();  // 2
```

### Design Decisions

**Por que `visitada` E `contadorVisitas`?**
- `visitada`: Flag binária simples para algoritmos básicos (Etapa 1)
- `contadorVisitas`: Contador acumulativo para ACO (Etapa 2)
- Separados porque têm propósitos e reset behaviors diferentes

**Por que `valor` pode ser 0?**
- 0 = "sem peso especificado", interpretado como 1 (custo mínimo)
- Evita necessidade de inicializar todos os 100 valores no grid

---

## 3. Robo.java - Entidade Robô

### Responsabilidade
Representa o agente que navega pelo labirinto, armazenando sua posição atual.

### Estrutura

```java
public class Robo {
    private int posicaoX;  // Coordenada X (linha)
    private int posicaoY;  // Coordenada Y (coluna)
}
```

### Características

**Simplicidade Intencional**
- Classe deliberadamente simples (apenas posição)
- Não contém lógica de movimento (delegada aos Services)
- Segue Single Responsibility Principle

**Mutabilidade**
- Posição muda ao longo da simulação
- Setters permitem atualização controlada

### Métodos

#### Construtor

```java
public Robo(int posicaoX, int posicaoY)
```
- Inicializa robô em posição específica
- **Comum**: `Robo robo = new Robo(0, 0);` (canto superior esquerdo)

#### Getters

```java
public int getPosicaoX()
public int getPosicaoY()
```
- Acesso individual às coordenadas
- **Uso**: `int x = robo.getPosicaoX();`

```java
public Posicao getPosicao()
```
- Retorna objeto `Posicao` imutável representando posição atual
- **Útil para**: Passar posição para algoritmos de busca
- **Exemplo**: `List<Posicao> vizinhos = robo.getPosicao().getVizinhos();`

#### Setters

```java
public void setPosicao(int x, int y)
```
- Atualiza posição do robô
- **Uso direto**: `robo.setPosicao(5, 7);`

```java
public void setPosicao(Posicao posicao)
```
- Sobrecarga que aceita objeto `Posicao`
- **Uso**: `robo.setPosicao(proximaPosicao);`

#### Utilitários

```java
public String toString()
```
- Retorna representação textual: `"Robô na posição (x, y)"`
- **Útil para**: Debugging e logs

### Fluxo de Movimento Típico

```java
// 1. Obter posição atual
Posicao atual = robo.getPosicao();

// 2. Calcular nova posição (lógica no Service)
Posicao proxima = new Posicao(atual.getX() + 1, atual.getY());

// 3. Validar movimento
if (ValidadorMovimento.podeAcessar(campo, proxima)) {
    // 4. Atualizar campo (marcar antiga como VAZIA, nova como OCUPADA)
    campo.getCasa(atual).setTipo(TipoCelula.VAZIA);
    campo.getCasa(proxima).setTipo(TipoCelula.OCUPADA);
    
    // 5. Atualizar robô
    robo.setPosicao(proxima);
}
```

### Design Decisions

**Por que não `Posicao posicao` como atributo?**
- `int x, y` são primitivos (sem overhead de objeto)
- Método `getPosicao()` cria objeto `Posicao` sob demanda
- Evita mutabilidade indesejada (Posicao é imutável)

**Por que não métodos `moverCima()`, `moverBaixo()`?**
- Lógica de movimento pertence aos Services (não ao modelo)
- Robô é apenas um container de estado (posição)
- Segue Tell, Don't Ask principle

---

## 4. Posicao.java - Coordenadas Imutáveis

### Responsabilidade
Representa um par de coordenadas (x, y) de forma imutável e hashable, ideal para uso em coleções (HashMap, HashSet).

### Estrutura

```java
public class Posicao {
    private final int x;  // Imutável
    private final int y;  // Imutável
}
```

### Características

**1. Imutabilidade**
- Atributos `final` = não podem ser alterados após construção
- Sem setters
- Instâncias são thread-safe por natureza

**2. Hashable**
- Implementa `equals()` e `hashCode()`
- Pode ser usada como chave em `HashMap<Posicao, Integer>`
- Pode ser adicionada a `HashSet<Posicao>`

**3. Value Object**
- Igualdade baseada em valor, não identidade
- `new Posicao(2, 3).equals(new Posicao(2, 3))` → `true`

### Métodos

#### Construtor

```java
public Posicao(int x, int y)
```
- Único construtor (obrigatório fornecer x e y)

#### Getters

```java
public int getX()
public int getY()
```
- Acesso somente leitura às coordenadas

#### Vizinhos

```java
public List<Posicao> getVizinhos()
```
- **Retorna**: Lista de 4 posições adjacentes (cima, baixo, esquerda, direita)
- **Ordem**: 
  1. `(x-1, y)` - Cima
  2. `(x+1, y)` - Baixo
  3. `(x, y-1)` - Esquerda
  4. `(x, y+1)` - Direita

**IMPORTANTE**: Não valida se vizinhos estão dentro dos limites!
- Pode retornar `Posicao(-1, 5)` ou `Posicao(10, 3)`
- Validação deve ser feita externamente com `Campo.posicaoValida()`

**Uso típico**:
```java
Posicao atual = new Posicao(5, 5);
List<Posicao> vizinhos = atual.getVizinhos();

for (Posicao vizinho : vizinhos) {
    if (campo.posicaoValida(vizinho) && campo.getCasa(vizinho).podeSerAcessada()) {
        // vizinho válido!
    }
}
```

#### Comparação de Objetos

```java
@Override
public boolean equals(Object o)
```
- Compara baseado em valores de `x` e `y`
- **Uso**: `if (posicao1.equals(posicao2)) { ... }`

```java
@Override
public int hashCode()
```
- Gera hash baseado em `x` e `y` usando `Objects.hash()`
- Garante que posições iguais têm mesmo hash code

#### Representação Textual

```java
@Override
public String toString()
```
- Retorna: `"(x, y)"`
- **Exemplo**: `"(5, 7)"`

### Casos de Uso

#### 1. BFS (Busca em Largura)

```java
Queue<Posicao> fila = new LinkedList<>();
Set<Posicao> visitados = new HashSet<>();

fila.add(posicaoInicial);
visitados.add(posicaoInicial);

while (!fila.isEmpty()) {
    Posicao atual = fila.poll();
    
    for (Posicao vizinho : atual.getVizinhos()) {
        if (!visitados.contains(vizinho) && campo.posicaoValida(vizinho)) {
            fila.add(vizinho);
            visitados.add(vizinho);  // HashSet usa equals() e hashCode()
        }
    }
}
```

#### 2. Dijkstra (Menor Custo)

```java
Map<Posicao, Integer> distancias = new HashMap<>();
distancias.put(posicaoInicial, 0);

int distanciaAtual = distancias.get(posicaoAtual);  // HashMap usa hashCode()
```

#### 3. Rastreamento de Caminho

```java
Map<Posicao, Posicao> pais = new HashMap<>();
pais.put(vizinho, atual);  // vizinho veio de atual

// Reconstruir caminho
List<Posicao> caminho = new ArrayList<>();
Posicao passo = posicaoFinal;
while (passo != null) {
    caminho.add(passo);
    passo = pais.get(passo);
}
Collections.reverse(caminho);
```

### Design Decisions

**Por que imutável?**
- Seguro para uso em coleções (HashMap não "perde" chaves)
- Evita bugs de modificação acidental
- Facilita raciocínio sobre código (posição não muda inesperadamente)

**Por que `getVizinhos()` não valida limites?**
- Separação de responsabilidades (Posicao não conhece Campo)
- Permite uso genérico em diferentes contextos
- Validação explícita no código chamador é mais clara

---

## 5. TipoCelula.java - Enum de Tipos

### Responsabilidade
Define os tipos possíveis de células no grid.

### Valores

```java
public enum TipoCelula {
    VAZIA,     // Célula livre para navegação
    OCUPADA,   // Posição atual do robô
    PAREDE,    // Obstáculo intransponível
    FINAL      // Objetivo a ser alcançado
}
```

### Uso

```java
Casa casa = campo.getCasa(3, 4);
if (casa.getTipo() == TipoCelula.PAREDE) {
    System.out.println("Bloqueado!");
}

casa.setTipo(TipoCelula.FINAL);
```

### Mapeamento Visual

| Tipo     | Símbolo Terminal | Cor GUI              | Descrição                      |
|----------|-----------------|----------------------|-------------------------------|
| VAZIA    | `   ` ou ` . `  | Branco/cinza claro   | Célula livre                  |
| OCUPADA  | ` O `           | Azul (robô)          | Posição do robô               |
| PAREDE   | `███`           | Cinza escuro         | Obstáculo                     |
| FINAL    | ` F `           | Amarelo/dourado      | Objetivo                      |

### Design Decisions

**Por que enum e não constantes int?**
- Type safety (compilador detecta erros)
- Autocompletar no IDE
- Switch exhaustiveness checking

---

## 6. ValidadorMovimento.java - Utility Class

### Responsabilidade
Centraliza lógicas de validação de movimento, evitando duplicação de código.

### Métodos

#### Validação de Posição

```java
public static boolean posicaoValida(Campo campo, int x, int y)
```
- Delega para `campo.posicaoValida()`
- **Uso**: `if (ValidadorMovimento.posicaoValida(campo, 5, 3)) { ... }`

#### Validação de Acesso

```java
public static boolean podeAcessar(Campo campo, int x, int y)
```
- **Lógica**: posição válida **E** célula pode ser acessada
- Combina `posicaoValida()` + `podeSerAcessada()`

```java
// Implementação
public static boolean podeAcessar(Campo campo, int x, int y) {
    if (!posicaoValida(campo, x, y)) {
        return false;
    }
    Casa casa = campo.getCasa(x, y);
    return casa.podeSerAcessada();
}
```

#### Obter Vizinhos Válidos

```java
public static List<Posicao> obterVizinhosValidos(Campo campo, Posicao posicao)
```
- Retorna apenas vizinhos que estão dentro do grid **E** podem ser acessados
- **Uso comum em algoritmos de busca**

```java
List<Posicao> vizinhosValidos = ValidadorMovimento.obterVizinhosValidos(campo, posicaoAtual);
for (Posicao vizinho : vizinhosValidos) {
    // Todos já validados, pode processar diretamente
}
```

### Por que Utility Class?

- Métodos `static` (não precisa instanciar)
- Reutilizável em todos os serviços
- Evita lógica duplicada em 10+ classes

---

## 7. GeradorCampo.java - Factory

### Responsabilidade
Factory methods para criar configurações de campo específicas para cada etapa.

### Métodos

#### `criarCampoEtapa1()`
- Retorna campo 10x10 completamente vazio
- Sem paredes, sem pesos

#### `criarCampoEtapa2()`
- Campo 10x10 com paredes predefinidas
- **Paredes**: 
  - Bloco em (2,2), (2,3), (2,4), (3,2)
  - Bloco em (5,5), (5,6), (6,5), (6,6)
  - Linha em (7,0), (7,1), (7,2), (7,3)
  - Bloco em (8,8), (8,9), (9,8), (9,9)

#### `criarCampoEtapa3()` e `criarCampoEtapa4()`
- Reutilizam `criarCampoEtapa2()`
- Paredes iguais para manter consistência

#### `criarCampoEtapa4ComPesos()`
- Cria campo da etapa 2
- Adiciona pesos aleatórios (1-10) em todas as células VAZIA

#### `criarCampoAleatorio(linhas, colunas, percentualParedes)`
- Gera campo com paredes aleatórias
- **Uso**: Testes ou variações

#### `definirPosicaoFinalAleatoria(Campo campo)`
- Escolhe célula VAZIA aleatória
- Define como `TipoCelula.FINAL`
- **Retorna**: `Posicao` da célula escolhida

### Por que Factory?

- Evita duplicação de código de inicialização
- Centraliza configurações de cada etapa
- Facilita testes (criar campos específicos para casos de teste)

---

## Diagrama de Relacionamentos

```
┌────────────────┐
│     Campo      │◄──── (valida posições)
│  - matrizCasas │
└────────┬───────┘
         │ contém
         │ 0..100
         ▼
    ┌─────────┐
    │  Casa   │
    │ - tipo  │──► TipoCelula (enum)
    └─────────┘

┌──────────┐
│   Robo   │
│ - x, y   │──► (usa) Posicao
└──────────┘

┌────────────────┐           ┌──────────────────┐
│ ValidadorMov.  │◄────uses──│  GeradorCampo    │
│  (utility)     │           │   (factory)      │
└────────────────┘           └──────────────────┘
         │                            │
         └────► Campo ◄───────────────┘
```

---

## Princípios de Design Implementados

### 1. Encapsulation
- Matrizes nunca expostas diretamente
- Acesso controlado via métodos públicos

### 2. Immutability
- `Posicao`: Completamente imutável
- `Campo`: Dimensões imutáveis (`final int linhas, colunas`)

### 3. Fail-Fast
- Validações lançam exceções claras
- Previne estados inconsistentes

### 4. Single Responsibility
- `Campo`: Gerencia grid
- `Casa`: Estado de célula
- `Robo`: Apenas posição
- `ValidadorMovimento`: Apenas validações

### 5. Open/Closed
- Fácil adicionar novos `TipoCelula` sem modificar código existente
- Basta adicionar ao enum e atualizar switch em `exibirCampo()`

---

## Conclusão

Os modelos de dados do projeto são projetados para:
- **Segurança**: Validações em todos os acessos
- **Clareza**: Nomes descritivos, responsabilidades bem definidas
- **Reutilização**: Utilizados por todas as 4 etapas sem modificação
- **Manutenibilidade**: Princípios de Clean Code aplicados consistentemente

Esta base sólida permite que os algoritmos (services) se concentrem na lógica de busca e movimento, sem se preocupar com detalhes de implementação da estrutura de dados.
