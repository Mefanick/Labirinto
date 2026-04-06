# Simulador de Robô em Labirinto

## 📋 Visão Geral

Projeto acadêmico desenvolvido em Java que implementa 4 etapas progressivas de algoritmos de navegação em labirinto 10x10, desde movimento aleatório até otimização por menor custo (Dijkstra). Inclui interface gráfica moderna em JavaFX e modo terminal ASCII.

**Instituição**: FURB (Universidade Regional de Blumenau)  
**Disciplina**: Algoritmos de Busca e Otimização  
**Linguagem**: Java 11+  
**Interface**: JavaFX 21.0.1

---

## 🚀 INÍCIO RÁPIDO

### Executar Interface Gráfica

```batch
ABRIR_GUI.bat
```

### Executar Modo Terminal

```batch
TERMINAL.bat
```

**Primeira execução**: Compilação automática (15-30 segundos)  
**Execuções seguintes**: Instantâneo

---

## 🎯 Etapas Implementadas

### Etapa 1 - Movimento Básico
- **Algoritmos**: Random Walk, Linha Reta até Bater
- **Campo**: Vazio (sem obstáculos)
- **Objetivo**: Exploração livre
- **Interface**: Seletor de modo, controle de velocidade

### Etapa 2 - Movimento Inteligente
- **Algoritmos**: Greedy (evita visitadas), Feromônio (ACO)
- **Campo**: Com paredes predefinidas
- **Objetivo**: Alcançar célula FINAL
- **Interface**: Heat map de visitas, edição interativa de paredes/final

### Etapa 3 - Busca em Largura (BFS)
- **Algoritmo**: Breadth-First Search
- **Campo**: Com paredes predefinidas
- **Objetivo**: Encontrar caminho mais curto (menos passos)
- **Interface**: Animação de exploração + execução do caminho

### Etapa 4 - Algoritmo de Dijkstra
- **Algoritmo**: Dijkstra (menor custo ponderado)
- **Campo**: Com paredes e pesos nas células
- **Objetivo**: Encontrar caminho de menor custo total
- **Interface**: Heat map de pesos, edição interativa de pesos

---

## 📚 Documentação Completa

A documentação técnica está organizada em 5 documentos detalhados:

### [ARQUITETURA.md](ARQUITETURA.md)
Visão geral do projeto, estrutura de pastas, decisões arquiteturais, padrões de design, separação de responsabilidades e fluxo de dados.

**Conteúdo**:
- Estrutura de pastas detalhada
- Separação por etapas (core + etapa1-4 + gui)
- Padrões: Factory, Service Layer, Template Method, Strategy
- Decisões arquiteturais justificadas
- Diagrama de dependências

### [MODELOS.md](MODELOS.md)
Documentação completa das classes de modelo (core), incluindo `Campo`, `Casa`, `Robo`, `Posicao`, `TipoCelula`, `ValidadorMovimento` e `GeradorCampo`.

**Conteúdo**:
- Estrutura de cada classe
- Métodos com exemplos de uso
- Princípios de encapsulamento e validação
- Diagramas de relacionamentos
- Casos de uso práticos

### [ALGORITMOS.md](ALGORITMOS.md)
Explicação detalhada de todos os algoritmos implementados, com pseudocódigo, análise de complexidade e comparações.

**Conteúdo**:
- **Etapa 1**: Random Walk, Linha Reta
- **Etapa 2**: Greedy, Feromônio (ACO)
- **Etapa 3**: BFS com reconstrução de caminho
- **Etapa 4**: Dijkstra com PriorityQueue
- Análise de complexidade (tempo/espaço)
- Comparativos entre algoritmos
- Exemplos visuais de execução

### [INTERFACE_GRAFICA.md](INTERFACE_GRAFICA.md)
Documentação completa do sistema de interface gráfica JavaFX, incluindo design system, componentes, animações e interatividade.

**Conteúdo**:
- Arquitetura JavaFX (MainGUI, Views)
- Design System (cores FURB, espaçamentos, shadows)
- Estrutura de cada View (HomeView, Etapa1-4View)
- Sistema de animação (Timeline, Transitions)
- Renderização do labirinto (Grid, células, heat maps)
- Recursos interativos (edição de paredes, pesos, final)
- CSS completo com exemplos

### [GUIA_EXECUCAO.md](GUIA_EXECUCAO.md)
Guia completo de compilação, execução e troubleshooting, incluindo requisitos, processos detalhados e resolução de erros.

**Conteúdo**:
- Requisitos do sistema
- Processo de compilação passo a passo
- Scripts `.bat` explicados
- Troubleshooting completo (7 erros comuns)
- Compilação manual
- Verificação de ambiente
- Testes de cada etapa
- Dicas de performance

---

## 🛠️ Tecnologias Utilizadas

### Core
- **Java 11+**: Linguagem base
- **Collections Framework**: ArrayList, HashMap, HashSet, LinkedList, PriorityQueue
- **Enums**: Definição de tipos

### Algoritmos
- **Estruturas de Dados**: Queue (BFS), PriorityQueue (Dijkstra), Map (rastreamento)
- **Padrões**: Random Walk, Greedy Search, BFS, Dijkstra, ACO

### Interface Gráfica
- **JavaFX 21.0.1**: Framework GUI
- **Scene Graph**: Hierarquia de componentes
- **CSS3**: Estilização centralizada
- **Animations**: Timeline, FadeTransition, ScaleTransition

### Build & Deployment
- **Batch Scripts**: Compilação e execução automatizadas
- **PowerShell**: Listagem de arquivos e compilação
- **JavaFX SDK**: Biblioteca externa incluída

---

## 📁 Estrutura do Projeto

```
Etapa 1/
├── 📁 src/main/java/com/projetorobo/
│   ├── 📁 core/                    # Componentes compartilhados
│   │   ├── model/                  # Campo, Casa, Robo, Posicao
│   │   ├── enums/                  # TipoCelula
│   │   ├── service/                # MovimentoBaseService
│   │   └── util/                   # ValidadorMovimento, GeradorCampo
│   ├── 📁 etapa1/                  # Movimento Básico
│   ├── 📁 etapa2/                  # Movimento Inteligente
│   ├── 📁 etapa3/                  # BFS
│   ├── 📁 etapa4/                  # Dijkstra
│   ├── 📁 gui/                     # Interface Gráfica
│   │   ├── MainGUI.java
│   │   └── view/                   # HomeView, Etapa1-4View
│   └── Main.java                   # Entry point terminal
├── 📁 src/main/resources/css/
│   └── styles.css                  # Design system
├── 📁 bin/                         # Bytecode compilado (auto)
├── 📁 javafx-sdk-21.0.1/           # JavaFX SDK
├── 📄 ABRIR_GUI.bat                # Launcher GUI
├── 📄 TERMINAL.bat                 # Launcher terminal
├── 📄 ARQUITETURA.md               # Documentação: Arquitetura
├── 📄 MODELOS.md                   # Documentação: Modelos
├── 📄 ALGORITMOS.md                # Documentação: Algoritmos
├── 📄 INTERFACE_GRAFICA.md         # Documentação: Interface
├── 📄 GUIA_EXECUCAO.md             # Documentação: Execução
├── 📄 COMO_RESOLVER_ERROS_IDE.md   # Troubleshooting IDE
└── 📄 README.md                    # Este arquivo
```

---

## 🎨 Recursos Visuais

### Interface Gráfica
- **Cores FURB**: Azul `#004C99`, Amarelo `#FFB700`
- **Design Moderno**: Glassmorphism, shadows, animações
- **Responsividade**: ScrollPane, controles adaptativos
- **Animações**: Entrada suave, transições de páginas, movimento do robô

### Visualizações
- **Etapa 1**: Células visitadas (cinza claro)
- **Etapa 2**: Heat map de feromônios (branco → amarelo → vermelho)
- **Etapa 3**: Células exploradas (azul claro), caminho com estrelas ★
- **Etapa 4**: Heat map de pesos (branco → rosa → laranja → vermelho)

---

## 🔧 Requisitos

### Obrigatórios
- **Windows 10/11** (64-bit)
- **JDK 11+** instalado e no PATH
- **JavaFX SDK 21.0.1** (incluído no projeto)

### Verificar Instalação

```batch
java -version
javac -version
```

**Esperado**: Versão 11 ou superior para ambos.

---

## 📖 Como Usar

### 1. Clonar/Baixar o Projeto

```bash
git clone <url-do-repositorio>
cd "Etapa 1"
```

### 2. Executar

**Interface Gráfica**:
```batch
ABRIR_GUI.bat
```

**Modo Terminal**:
```batch
TERMINAL.bat
```

### 3. Navegar

- **HOME**: Escolha uma das 4 etapas
- **Etapa View**: Use controles (INICIAR, PAUSAR, RESETAR)
- **Velocidade**: Ajuste no ComboBox (1x-10x)
- **Voltar**: Botão no canto inferior esquerdo

---

## 🎓 Para Avaliadores

### Execução Rápida

1. Duplo clique em `ABRIR_GUI.bat`
2. Aguardar compilação automática (primeira vez)
3. Testar as 4 etapas no menu HOME

### Verificação de Funcionamento

✅ **Compilação**: Console mostra "Compilacao concluida!"  
✅ **Interface**: Janela maximizada com menu HOME  
✅ **Etapa 1**: Robô se move (aleatório ou linha reta)  
✅ **Etapa 2**: Heat map visível, pausa ao chegar no final  
✅ **Etapa 3**: Animação BFS + robô segue caminho  
✅ **Etapa 4**: Pesos coloridos, Dijkstra calcula menor custo  

### Documentação Recomendada

1. **Primeiro**: [ARQUITETURA.md](ARQUITETURA.md) - Visão geral
2. **Algoritmos**: [ALGORITMOS.md](ALGORITMOS.md) - Lógica implementada
3. **Problemas**: [GUIA_EXECUCAO.md](GUIA_EXECUCAO.md) - Troubleshooting

---

## 🐛 Problemas Comuns

### "JavaFX SDK não encontrado"
**Solução**: Extrair JavaFX SDK 21.0.1 na pasta raiz do projeto.

### "javac não é reconhecido"
**Solução**: Instalar JDK e adicionar ao PATH do Windows.

### Erros no IDE (linhas vermelhas)
**Solução**: Ver [COMO_RESOLVER_ERROS_IDE.md](COMO_RESOLVER_ERROS_IDE.md)  
**Nota**: Erros visuais apenas! Código compila e executa via `.bat`.

### Janela abre em branco
**Solução**: Aguardar 2-3 segundos (carregamento CSS) ou recompilar.

**Mais detalhes**: [GUIA_EXECUCAO.md](GUIA_EXECUCAO.md#-troubleshooting)

---

## 📊 Estatísticas do Projeto

- **Linhas de Código**: ~3,500
- **Classes**: 30+
- **Arquivos**: 45+
- **Algoritmos**: 6 (Random, Linha Reta, Greedy, Feromônio, BFS, Dijkstra)
- **Etapas**: 4
- **Telas GUI**: 5 (Home + 4 Etapas)
- **Tempo de Desenvolvimento**: ~40 horas

---

## 📜 Licença

Projeto acadêmico desenvolvido para fins educacionais.  
**Autor**: Desenvolvido por estudante da FURB  
**Curso**: Ciência da Computação / Engenharia de Software  
**Disciplina**: Algoritmos de Busca e Otimização

---

## 🤝 Contribuições

Este é um projeto acadêmico fechado. Não aceita contribuições externas.

---

## 📞 Suporte

Para questões sobre execução, consulte:
- [GUIA_EXECUCAO.md](GUIA_EXECUCAO.md) - Compilação e execução
- [COMO_RESOLVER_ERROS_IDE.md](COMO_RESOLVER_ERROS_IDE.md) - Problemas no IDE

Para questões sobre código:
- [ARQUITETURA.md](ARQUITETURA.md) - Estrutura do projeto
- [MODELOS.md](MODELOS.md) - Classes core
- [ALGORITMOS.md](ALGORITMOS.md) - Lógica dos algoritmos
- [INTERFACE_GRAFICA.md](INTERFACE_GRAFICA.md) - Sistema GUI

---

## ✨ Destaques

- ✅ **4 Etapas Progressivas**: Do básico ao avançado
- ✅ **Interface Moderna**: JavaFX com design FURB
- ✅ **Documentação Completa**: 2000+ linhas de docs
- ✅ **Plug & Play**: Scripts `.bat` automáticos
- ✅ **Cross-Algorithm**: Comparação entre 6 algoritmos
- ✅ **Visualização Rica**: Animações, heat maps, cores
- ✅ **Código Limpo**: Clean Code, SOLID, padrões de design

---

## 🎯 Objetivos Alcançados

1. ✅ Implementar movimento aleatório básico
2. ✅ Implementar movimento inteligente com feromônios
3. ✅ Implementar busca em largura (BFS)
4. ✅ Implementar algoritmo de Dijkstra
5. ✅ Criar interface gráfica moderna e intuitiva
6. ✅ Documentar código e arquitetura completamente
7. ✅ Garantir execução plug-and-play

---

**Desenvolvido com ❤️ para FURB - 2026**
