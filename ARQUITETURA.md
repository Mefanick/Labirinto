# ARQUITETURA DO PROJETO - Simulador de Robô em Labirinto

## Visão Geral do Projeto

### Objetivo
Desenvolver um simulador de navegação de robô em labirinto 10x10 que demonstra diferentes algoritmos de busca e otimização, desde movimento aleatório básico até algoritmos de menor custo (Dijkstra).

### Contexto Acadêmico
Projeto desenvolvido para a disciplina de Algoritmos de Busca e Otimização da FURB (Universidade Regional de Blumenau), implementando progressivamente 4 etapas de complexidade crescente.

### Características Principais
- **Linguagem**: Java 11+
- **Interface**: Dual (JavaFX GUI + Terminal ASCII)
- **Grid**: 10x10 células
- **Etapas**: 4 níveis progressivos
- **Platform**: Windows (scripts .bat para execução)

---

## Arquitetura Geral

O projeto segue uma arquitetura modular baseada em camadas, separando responsabilidades de forma clara:

```
┌─────────────────────────────────────────────────────┐
│             CAMADA DE APRESENTAÇÃO                  │
│  ┌──────────────────┐    ┌──────────────────┐      │
│  │   Terminal CLI   │    │   JavaFX GUI     │      │
│  │   (Main.java)    │    │  (MainGUI.java)  │      │
│  └──────────────────┘    └──────────────────┘      │
└─────────────────────────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────┐
│            CAMADA DE VISUALIZAÇÃO                   │
│  ┌──────────────────────────────────────────┐      │
│  │  Views (HomeView, Etapa1-4View)          │      │
│  └──────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────┐
│              CAMADA DE SERVIÇOS                     │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐   │
│  │ Movimento  │  │   Busca    │  │  Dijkstra  │   │
│  │  Services  │  │  Services  │  │  Services  │   │
│  └────────────┘  └────────────┘  └────────────┘   │
└─────────────────────────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────┐
│              CAMADA DE DOMÍNIO                      │
│  ┌────────────────────────────────────────┐        │
│  │  Models (Campo, Casa, Robo, Posicao)   │        │
│  │  Enums (TipoCelula)                    │        │
│  │  Utils (Validador, Gerador)            │        │
│  └────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────┘
```

---

## Estrutura de Pastas Detalhada

```
Etapa 1/                                    # Diretório raiz do projeto
│
├── 📁 src/                                 # Código fonte
│   ├── 📁 main/
│   │   ├── 📁 java/com/projetorobo/
│   │   │   │
│   │   │   ├── 📁 core/                   # ⭐ COMPONENTES COMPARTILHADOS
│   │   │   │   │
│   │   │   │   ├── 📁 model/              # Modelos de dados
│   │   │   │   │   ├── Campo.java         # Grid 10x10 (matriz de casas)
│   │   │   │   │   ├── Casa.java          # Célula individual (tipo, peso, visitada)
│   │   │   │   │   ├── Robo.java          # Entidade robô (posição x, y)
│   │   │   │   │   └── Posicao.java       # Coordenadas imutáveis (x, y)
│   │   │   │   │
│   │   │   │   ├── 📁 enums/              # Enumerações
│   │   │   │   │   └── TipoCelula.java    # VAZIA | OCUPADA | PAREDE | FINAL
│   │   │   │   │
│   │   │   │   ├── 📁 util/               # Utilitários
│   │   │   │   │   ├── GeradorCampo.java  # Factory para criar campos
│   │   │   │   │   └── ValidadorMovimento.java # Validações de movimento
│   │   │   │   │
│   │   │   │   └── 📁 service/            # Serviços base
│   │   │   │       └── MovimentoBaseService.java # Classe abstrata base
│   │   │   │
│   │   │   ├── 📁 etapa1/                 # 🎯 ETAPA 1: Movimento Básico
│   │   │   │   ├── 📁 service/
│   │   │   │   │   ├── MovimentoAleatorioService.java    # Random Walk
│   │   │   │   │   └── MovimentoLinhaRetaService.java    # Linha Reta até bater
│   │   │   │   └── MainEtapa1.java        # Entry point terminal Etapa 1
│   │   │   │
│   │   │   ├── 📁 etapa2/                 # 🧠 ETAPA 2: Movimento Inteligente
│   │   │   │   ├── 📁 service/
│   │   │   │   │   ├── MovimentoInteligenteService.java  # Greedy (evita visitadas)
│   │   │   │   │   └── MovimentoFeromonioService.java    # ACO (contador de visitas)
│   │   │   │   └── MainEtapa2.java        # Entry point terminal Etapa 2
│   │   │   │
│   │   │   ├── 📁 etapa3/                 # 🔍 ETAPA 3: Busca em Largura (BFS)
│   │   │   │   ├── 📁 model/
│   │   │   │   │   └── ResultadoBusca.java         # Resultado da busca
│   │   │   │   ├── 📁 service/
│   │   │   │   │   ├── BuscaService.java           # BFS core
│   │   │   │   │   ├── BuscaVisualService.java     # BFS com visualização
│   │   │   │   │   └── ExecutorCaminhoService.java # Executa caminho encontrado
│   │   │   │   └── MainEtapa3.java        # Entry point terminal Etapa 3
│   │   │   │
│   │   │   ├── 📁 etapa4/                 # ⚖️ ETAPA 4: Dijkstra (Menor Custo)
│   │   │   │   ├── 📁 model/
│   │   │   │   │   ├── NoCaminho.java              # Nó com custo (Comparable)
│   │   │   │   │   └── ResultadoDijkstra.java      # Resultado com custo total
│   │   │   │   ├── 📁 service/
│   │   │   │   │   ├── DijkstraService.java        # Dijkstra core
│   │   │   │   │   └── DijkstraVisualService.java  # Dijkstra com visualização
│   │   │   │   └── MainEtapa4.java        # Entry point terminal Etapa 4
│   │   │   │
│   │   │   ├── 📁 gui/                    # 🎨 INTERFACE GRÁFICA
│   │   │   │   ├── MainGUI.java           # Entry point JavaFX
│   │   │   │   └── 📁 view/
│   │   │   │       ├── HomeView.java      # Tela inicial (menu)
│   │   │   │       ├── Etapa1View.java    # Interface Etapa 1
│   │   │   │       ├── Etapa2View.java    # Interface Etapa 2
│   │   │   │       ├── Etapa3View.java    # Interface Etapa 3
│   │   │   │       └── Etapa4View.java    # Interface Etapa 4
│   │   │   │
│   │   │   ├── Main.java                  # Entry point modo terminal
│   │   │   └── TestRunner.java            # Testes automatizados
│   │   │
│   │   └── 📁 resources/                  # Recursos estáticos
│   │       └── 📁 css/
│   │           └── styles.css             # Estilos JavaFX
│   │
│   └── 📁 test/                           # (Placeholder para testes)
│
├── 📁 bin/                                 # Bytecode compilado (.class)
│   └── com/projetorobo/...                # Estrutura espelhada de src/
│
├── 📁 javafx-sdk-21.0.1/                  # JavaFX SDK
│   ├── 📁 lib/                            # Bibliotecas .jar
│   │   ├── javafx.base.jar
│   │   ├── javafx.controls.jar
│   │   ├── javafx.graphics.jar
│   │   └── ...
│   └── 📁 bin/                            # Binários nativos (Windows DLLs)
│
├── 📁 .vscode/                            # Configuração VS Code/Cursor
│   └── settings.json                      # Classpath do JavaFX
│
├── 📄 .classpath                          # Configuração Eclipse
├── 📄 .project                            # Projeto Eclipse
│
├── 📄 ABRIR_GUI.bat                       # 🚀 Launcher interface gráfica
├── 📄 TERMINAL.bat                        # 🖥️ Launcher modo console
├── 📄 RESOLVER_ERROS_IDE.bat              # Script auxiliar
│
├── 📄 README.md                           # Documentação básica
├── 📄 COMO_RESOLVER_ERROS_IDE.md          # Guia de troubleshooting IDE
└── 📄 ARQUITETURA.md                      # Este documento
```

---

## Separação de Responsabilidades

### 1. Core (Componentes Compartilhados)

**Propósito**: Fornecer fundação sólida e reutilizável para todas as etapas.

**Princípios**:
- Encapsulamento total (matrizes nunca expostas diretamente)
- Validação de limites em todos os acessos
- Imutabilidade onde apropriado (Posicao)
- Single Responsibility Principle

**Componentes**:
- **Models**: Representam entidades do domínio
- **Enums**: Definem constantes tipadas
- **Utils**: Funções auxiliares e validações
- **Services**: Lógica de negócio reutilizável

### 2. Etapas (Packages Isolados)

Cada etapa é **autocontida** e **independente**:

**Estrutura padrão**:
```
etapaX/
├── model/           # Modelos específicos da etapa (se necessário)
├── service/         # Implementações de algoritmos
└── MainEtapaX.java  # Entry point para modo terminal
```

**Vantagens**:
- Fácil navegação e compreensão
- Modificações isoladas (não afetam outras etapas)
- Possibilidade de executar cada etapa independentemente
- Facilita avaliação progressiva do trabalho

### 3. GUI (Interface Gráfica)

**Propósito**: Fornecer interface visual moderna e intuitiva.

**Arquitetura**:
- `MainGUI.java`: Configuração da aplicação JavaFX
- `view/`: Uma classe por tela (HomeView + 4 EtapaViews)
- `resources/css/`: Estilos centralizados

**Pattern**: Cada View é um componente independente que recebe `Stage` e gerencia sua própria navegação.

---

## Fluxo de Dados

### Modo Terminal

```
Main.java (menu)
    ↓
MainEtapaX.executar()
    ↓
Cria: Campo + Robo + Service
    ↓
Loop de movimento:
    service.mover(campo, robo)
    ↓
Atualiza Campo
    ↓
campo.exibirCampo() (ASCII)
```

### Modo GUI

```
MainGUI.start()
    ↓
HomeView (menu gráfico)
    ↓ (usuário clica etapa)
EtapaXView
    ↓
Cria: Campo + Robo + GridPane
    ↓
Usuário clica INICIAR
    ↓
Timeline com KeyFrame
    ↓ (a cada N ms)
service.mover(campo, robo)
    ↓
atualizarGrid() (JavaFX)
    ↓
Se chegou ao FINAL → pausa
```

---

## Decisões Arquiteturais

### 1. Por que separar por etapas?

**Decisão**: Criar packages `etapa1`, `etapa2`, `etapa3`, `etapa4` separados.

**Razões**:
- Cada etapa representa um nível de complexidade algorítmica diferente
- Facilita avaliação progressiva (professor pode testar etapa por etapa)
- Isola modificações (bug em uma etapa não afeta outras)
- Clareza conceitual (cada pasta = uma funcionalidade distinta)

**Alternativa rejeitada**: Colocar tudo em `core/service` com switches/condicionais.
- Motivo: Violaria SRP e dificultaria manutenção.

### 2. Por que Core separado?

**Decisão**: Criar package `core` para componentes compartilhados.

**Razões**:
- Evita duplicação de código (Campo, Robo usados em todas as etapas)
- Ponto único de validação (ValidadorMovimento)
- Facilita mudanças globais (alterar Campo afeta todas as etapas uniformemente)
- Segue DRY (Don't Repeat Yourself)

### 3. Por que dois modos de execução (GUI + Terminal)?

**Decisão**: Manter ambas as interfaces.

**Razões**:
- **Terminal**: Útil para debugging e demonstração de algoritmos passo a passo
- **GUI**: Melhor UX, visualização clara, interatividade
- **Flexibilidade**: Professor/avaliador pode escolher modo preferido
- **Custo**: Baixo (services são compartilhados, só muda apresentação)

### 4. Por que JavaFX e não Swing?

**Decisão**: Usar JavaFX 21.0.1 para interface gráfica.

**Razões**:
- **Moderno**: JavaFX é a tecnologia GUI atual do Java
- **CSS**: Estilização separada da lógica
- **Animações**: Timeline e Transitions integradas
- **Scene Graph**: Hierarquia clara de componentes
- **Cross-platform**: Funciona em Windows, Mac, Linux

**Alternativa rejeitada**: Swing
- Motivo: Tecnologia legada, limitações de estilização, ausência de animações modernas.

### 5. Por que não Maven/Gradle?

**Decisão**: Usar scripts `.bat` diretos com JavaFX SDK.

**Razões**:
- **Simplicidade**: Projeto universitário simples, sem dependências externas complexas
- **Portabilidade**: Basta clonar e executar (JavaFX SDK incluído)
- **Controle**: Scripts claros e editáveis (não há "mágica" de build tool)
- **Windows-only**: Projeto desenvolvido especificamente para Windows

**Contexto**: Tentativa inicial de usar Maven falhou por problemas de instalação, então optou-se pela abordagem direta.

---

## Padrões de Design Implementados

### 1. Factory Method
**Classe**: `GeradorCampo`
**Propósito**: Centralizar criação de campos com configurações diferentes por etapa.

### 2. Service Layer
**Pattern**: Lógica de negócio isolada em services.
**Benefício**: Views e Mains apenas orquestram, não implementam algoritmos.

### 3. Template Method
**Classe**: `MovimentoBaseService`
**Propósito**: Define estrutura comum `atualizarCampo()` que subclasses herdam.

### 4. Strategy
**Implementação**: Diferentes services implementam diferentes estratégias de movimento.
**Benefício**: Fácil troca de algoritmo em runtime (combo box na GUI).

### 5. Immutable Object
**Classe**: `Posicao`
**Propósito**: Coordenadas que não mudam (segurança em coleções como HashMap).

---

## Convenções de Nomenclatura

### Packages
- `core`: Lowercase, sem plural
- `etapaX`: Numeração explícita (1, 2, 3, 4)
- `gui`: Abreviação comum

### Classes
- **Models**: Substantivos simples (`Campo`, `Casa`, `Robo`)
- **Services**: `<Funcionalidade>Service` (`MovimentoAleatorioService`)
- **Enums**: `Tipo<Nome>` (`TipoCelula`)
- **Utils**: Função no nome (`ValidadorMovimento`, `GeradorCampo`)

### Métodos
- **Ações**: Verbos (`mover`, `gerar`, `criar`)
- **Consultas**: `get`, `is`, `pode` (`getCasa`, `isVisitada`, `podeAcessar`)
- **Modificadores**: `set`, `marcar`, `resetar` (`setTipo`, `marcarComoVisitada`)

### Variáveis
- **camelCase**: `contadorMovimentos`, `velocidadeMultiplicador`
- **Descritivas**: Nomes claros sobre abreviações (`campo` não `c`)
- **Booleanos**: Prefixo `is`, `eh`, `pode` quando apropriado

---

## Dependências

### Internas
- Todas as etapas dependem de `core`
- GUI depende de `core` e services das etapas
- Services dependem de models e utils

### Externas
- **JavaFX 21.0.1**: Único dependency externo
- **Java Standard Library**: Collections, I/O, Random

### Diagrama de Dependências

```
gui/view → etapaX/service → core/service
                         ↓
                    core/model ← core/util
                         ↓
                    core/enums
```

**Princípio**: Dependências sempre apontam "para baixo" (nunca circular).

---

## Extensibilidade

### Como adicionar uma nova etapa?

1. Criar package `etapa5/`
2. Adicionar `service/` com implementação do algoritmo
3. Criar `MainEtapa5.java` para modo terminal
4. Criar `Etapa5View.java` em `gui/view/`
5. Adicionar botão em `HomeView.java`
6. Atualizar `Main.java` com nova opção

**Tempo estimado**: 2-3 horas (com algoritmo complexo)

### Como adicionar novo tipo de célula?

1. Adicionar em `TipoCelula.java`
2. Atualizar `Casa.podeSerAcessada()` se necessário
3. Atualizar `Campo.exibirCampo()` com novo símbolo
4. Adicionar estilo CSS para nova célula
5. Atualizar método `criarCelula()` nas Views

---

## Compilação e Build

### Processo de Compilação

**Para GUI (`ABRIR_GUI.bat`)**:
```batch
1. Remove pasta bin/ (clean)
2. Cria estrutura de diretórios
3. Lista todos os .java recursivamente
4. Compila com javac:
   --module-path "javafx-sdk-21.0.1\lib"
   --add-modules javafx.controls,javafx.fxml
   -d bin
5. Executa com java:
   --module-path "javafx-sdk-21.0.1\lib"
   --add-modules javafx.controls,javafx.fxml
   -cp bin com.projetorobo.gui.MainGUI
```

**Para Terminal (`TERMINAL.bat`)**:
```batch
1. Remove pasta bin/ (clean)
2. Lista apenas .java não-GUI (exclui \gui\)
3. Compila para bin/
4. Executa com: java -cp bin com.projetorobo.Main
```

### Por que PowerShell dentro do .bat?

**Problema**: CMD tem limitações com paths que contêm espaços e wildcards.

**Solução**: Usar `powershell -Command "..."` para comandos complexos.

**Exemplo**:
```batch
powershell -Command "Get-ChildItem -Path 'src\main\java' -Filter *.java -Recurse | ForEach-Object { $_.FullName }"
```

---

## Segurança e Validação

### Prevenção de Erros

1. **IndexOutOfBoundsException**: Todas as posições validadas antes de acesso
2. **NullPointerException**: Matriz de casas inicializada no construtor
3. **ConcurrentModificationException**: Timeline parada antes de modificações

### Validações Implementadas

- `Campo.posicaoValida(x, y)`: Verifica limites 0-9
- `Casa.podeSerAcessada()`: Verifica se não é PAREDE nem OCUPADA
- `ValidadorMovimento.podeAcessar()`: Combina ambas validações

---

## Conclusão

A arquitetura do projeto foi desenhada para:
- **Modularidade**: Etapas isoladas e independentes
- **Reutilização**: Core compartilhado evita duplicação
- **Clareza**: Estrutura de pastas reflete estrutura conceitual
- **Extensibilidade**: Fácil adicionar novas etapas ou funcionalidades
- **Manutenibilidade**: Separação de responsabilidades bem definida

Esta arquitetura permite que o projeto sirva tanto como ferramenta educacional (demonstrando algoritmos progressivamente) quanto como base para expansões futuras.
