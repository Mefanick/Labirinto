# INTERFACE GRÁFICA - JavaFX Design System

## Visão Geral

O projeto utiliza **JavaFX 21.0.1** para criar uma interface gráfica moderna, responsiva e intuitiva. A arquitetura GUI segue padrões MVC (Model-View-Controller), com separação clara entre lógica de negócio (services) e apresentação (views).

### Tecnologias Utilizadas
- **JavaFX 21.0.1**: Framework GUI
- **CSS3**: Estilização centralizada
- **JavaFX Animations**: Timeline, Transitions
- **Scene Graph**: Hierarquia de componentes

---

## Estrutura de Arquivos

```
gui/
├── MainGUI.java           # Entry point da aplicação
└── view/
    ├── HomeView.java      # Tela inicial (menu)
    ├── Etapa1View.java    # Interface Etapa 1
    ├── Etapa2View.java    # Interface Etapa 2
    ├── Etapa3View.java    # Interface Etapa 3
    └── Etapa4View.java    # Interface Etapa 4

resources/
└── css/
    └── styles.css         # Design system completo
```

---

## 1. MainGUI - Entry Point

### Responsabilidade
Configurar e lançar a aplicação JavaFX, inicializando a janela principal e carregando a HomeView.

### Código Completo

```java
public class MainGUI extends Application {
    
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 820;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulador de Robô - Labirinto");
        
        // 1. Criar view inicial
        HomeView homeView = new HomeView(primaryStage);
        Scene scene = new Scene(homeView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // 2. Aplicar background explícito
        scene.setFill(javafx.scene.paint.Color.web("#F8F9FA"));
        
        // 3. Carregar CSS
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        
        // 4. Configurações de janela
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.setResizable(true);
        
        // 5. Tela cheia com F11
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        
        primaryStage.setFullScreenExitHint(
            "Pressione F11 ou ESC para sair do modo tela cheia"
        );
        
        // 6. Mostrar janela e maximizar
        primaryStage.show();
        primaryStage.setMaximized(true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

### Decisões de Design

**Por que `show()` antes de `setMaximized()`?**
- JavaFX requer que janela seja visível antes de maximizar
- Ordem incorreta causava bug de maximização

**Por que `scene.setFill()`?**
- Garante background consistente mesmo se CSS falhar
- Evita "flashes" brancos durante transições

---

## 2. Design System - Paleta de Cores FURB

### Cores Oficiais

As cores seguem o Manual de Identidade Visual da FURB:

```css
/* Cores Primárias */
-furb-azul: #004C99;           /* CMYK: C100% M75% Y0% K10% */
-furb-amarelo: #FFB700;        /* CMYK: C0% M31% Y100% K0% */

/* Variações */
-furb-azul-dark: #003366;
-furb-azul-light: #1A66CC;
-furb-amarelo-dark: #E09900;
-furb-amarelo-light: #FFC233;

/* Neutros */
-color-bg-light: #F8F9FA;      /* Background principal */
-color-white: #FFFFFF;
-color-text: #495057;
-color-text-dark: #343A40;
```

### Espaçamentos

```css
-spacing-xs: 4px;
-spacing-sm: 8px;
-spacing-md: 16px;
-spacing-lg: 24px;
-spacing-xl: 32px;
-spacing-xxl: 48px;
```

### Bordas e Sombras

```css
/* Radius */
-border-radius-sm: 4px;
-border-radius-md: 8px;
-border-radius-lg: 12px;
-border-radius-xl: 16px;

/* Shadows */
-shadow-sm: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);
-shadow-md: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 10, 0, 0, 4);
-shadow-lg: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 15, 0, 0, 6);
```

---

## 3. HomeView - Tela Inicial

### Estrutura

```
StackPane (root)
└── VBox (contentBox)
    ├── VBox (header)
    │   ├── Label (SIMULADOR DE ROBÔ)
    │   └── Label (Navegação Inteligente...)
    ├── GridPane (menuGrid) 2x2
    │   ├── Button (Etapa 1)
    │   ├── Button (Etapa 2)
    │   ├── Button (Etapa 3)
    │   └── Button (Etapa 4)
    └── HBox (footer)
        ├── Label (info)
        └── Button (SAIR)
```

### Componentes Principais

#### Header

```java
private VBox createHeader() {
    VBox header = new VBox(8);
    header.setAlignment(Pos.CENTER);
    
    Label title = new Label("SIMULADOR DE ROBÔ • FURB");
    title.getStyleClass().add("home-title");
    
    Label subtitle = new Label("Navegação Inteligente em Labirinto");
    subtitle.getStyleClass().add("home-subtitle");
    
    header.getChildren().addAll(title, subtitle, spacer);
    return header;
}
```

**Estilos CSS**:
```css
.home-title {
    -fx-font-size: 48px;
    -fx-font-weight: bold;
    -fx-text-fill: #004C99 !important;  /* Azul FURB */
    -fx-letter-spacing: 2px;
}

.home-subtitle {
    -fx-font-size: 18px;
    -fx-text-fill: #6C757D !important;  /* Cinza médio */
    -fx-letter-spacing: 1px;
}
```

#### Cards de Etapa

```java
private Button createEtapaCard(String emoji, String numero, 
                               String titulo, String descricao) {
    Button card = new Button();
    card.getStyleClass().add("btn-card");
    
    VBox content = new VBox(16);
    content.setAlignment(Pos.TOP_LEFT);
    content.setPadding(new Insets(24));
    
    Label emojiLabel = new Label(emoji);
    emojiLabel.getStyleClass().add("card-icon");
    
    Label numeroLabel = new Label(numero);
    numeroLabel.getStyleClass().add("card-numero");
    
    Label tituloLabel = new Label(titulo);
    tituloLabel.getStyleClass().add("card-titulo");
    
    Label descLabel = new Label(descricao);
    descLabel.getStyleClass().add("card-descricao");
    descLabel.setWrapText(true);
    
    content.getChildren().addAll(
        emojiLabel, numeroLabel, tituloLabel, spacer, descLabel
    );
    card.setGraphic(content);
    
    return card;
}
```

**Estilos CSS (Estado Normal)**:
```css
.btn-card {
    -fx-background-color: #FFFFFF;
    -fx-background-radius: 12px;
    -fx-border-color: #FFB700;        /* Amarelo FURB */
    -fx-border-width: 3px;
    -fx-border-radius: 12px;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 20, 0.2, 0, 4);
    -fx-min-width: 280px;
    -fx-min-height: 240px;
}

.card-icon {
    -fx-font-size: 48px;
    -fx-text-fill: #FFB700 !important;  /* Amarelo */
}

.card-numero {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
    -fx-text-fill: #004C99 !important;  /* Azul */
}

.card-titulo {
    -fx-font-size: 20px;
    -fx-font-weight: bold;
    -fx-text-fill: #2C3E50;
}

.card-descricao {
    -fx-font-size: 13px;
    -fx-text-fill: #7F8C8D;
    -fx-line-spacing: 3px;
}
```

**Estilos CSS (Hover)**:
```css
.btn-card:hover {
    -fx-background-color: #F8F9FA;     /* Fundo claro */
    -fx-border-color: #FFB700;
    -fx-effect: dropshadow(gaussian, rgba(255, 183, 0, 0.4), 30, 0.3, 0, 8);
    -fx-scale-x: 1.03;
    -fx-scale-y: 1.03;
}

.btn-card:hover .card-icon {
    -fx-text-fill: #FFB700 !important;
}

.btn-card:hover .card-numero,
.btn-card:hover .card-titulo {
    -fx-text-fill: #004C99 !important;
}
```

#### Animação de Entrada

```java
private void animateEntrance() {
    VBox contentBox = (VBox) root.getChildren().get(0);
    
    for (int i = 0; i < contentBox.getChildren().size(); i++) {
        Node node = contentBox.getChildren().get(i);
        
        // Fade in
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        // Slide up
        TranslateTransition slide = new TranslateTransition(Duration.millis(800), node);
        slide.setFromY(40);
        slide.setToY(0);
        
        // Scale up
        ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        // Delay baseado no índice (efeito cascata)
        PauseTransition pause = new PauseTransition(Duration.millis(i * 150));
        
        ParallelTransition parallel = new ParallelTransition(fade, slide, scale);
        SequentialTransition seq = new SequentialTransition(pause, parallel);
        seq.play();
    }
}
```

**Efeito**: Elementos aparecem sequencialmente de baixo para cima com fade.

#### Navegação entre Telas

```java
private void abrirEtapa1() {
    Etapa1View etapa1View = new Etapa1View(stage);
    Scene scene = new Scene(etapa1View.getRoot(), 
                           stage.getWidth(), 
                           stage.getHeight());
    String css = getClass().getResource("/css/styles.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
}
```

**Fluxo**:
1. Criar nova View
2. Criar nova Scene com dimensões atuais
3. Aplicar CSS
4. Substituir Scene no Stage

---

## 4. EtapaViews - Estrutura Comum

### Layout Base (BorderPane)

Todas as EtapaViews seguem a mesma estrutura:

```
BorderPane (root)
├── Top: VBox (topBar)
│   ├── HBox (header com título e botão sair)
│   └── Label (subtítulo)
├── Center: ScrollPane
│   └── VBox (centerContent)
│       ├── HBox (infoBox - labels de status)
│       ├── StackPane (labirinto)
│       └── HBox/VBox (controles)
└── Bottom: HBox (bottomBar)
    ├── Button (VOLTAR)
    └── Buttons (INICIAR/PAUSAR)
```

### Constantes Comuns

```java
private static final int TAMANHO_CELULA = 38;  // Pixels por célula
private static final int GRID_SIZE = 10;       // 10x10 grid
```

### Atributos JavaFX Comuns

```java
private final Stage stage;
private final BorderPane root;
private final GridPane gridLabirinto;
private final Label lblMovimentos;
private final Label lblPosicao;
private final Button btnIniciar;
private final Button btnPausar;
private final ComboBox<String> cmbVelocidade;
```

### Atributos de Modelo

```java
private Campo campo;
private Robo robo;
private Timeline timeline;
private int contadorMovimentos;
private boolean executando;
private double velocidadeMultiplicador;
```

---

## 5. Renderização do Labirinto

### Criação do Grid

```java
private StackPane criarLabirinto() {
    gridLabirinto.setAlignment(Pos.CENTER);
    gridLabirinto.setHgap(2);
    gridLabirinto.setVgap(2);
    gridLabirinto.setPadding(new Insets(10));
    gridLabirinto.getStyleClass().add("grid-labirinto");
    
    atualizarGrid();
    
    StackPane container = new StackPane(gridLabirinto);
    container.setAlignment(Pos.CENTER);
    container.getStyleClass().add("labirinto-container");
    
    return container;
}
```

### Renderização de Células

```java
private void atualizarGrid() {
    gridLabirinto.getChildren().clear();
    
    for (int i = 0; i < GRID_SIZE; i++) {
        for (int j = 0; j < GRID_SIZE; j++) {
            StackPane celula = criarCelula(i, j);
            gridLabirinto.add(celula, j, i);
        }
    }
}

private StackPane criarCelula(int linha, int coluna) {
    StackPane celula = new StackPane();
    celula.setMinSize(TAMANHO_CELULA, TAMANHO_CELULA);
    celula.setMaxSize(TAMANHO_CELULA, TAMANHO_CELULA);
    
    Casa casa = campo.getCasa(linha, coluna);
    Rectangle rect = new Rectangle(TAMANHO_CELULA, TAMANHO_CELULA);
    
    // Determinar aparência baseado no tipo
    switch (casa.getTipo()) {
        case VAZIA:
            if (casa.isVisitada()) {
                rect.getStyleClass().add("celula-visitada");
            } else {
                rect.getStyleClass().add("celula-vazia");
            }
            break;
        
        case OCUPADA:  // Robô
            rect.getStyleClass().add("celula-robo");
            Label roboLabel = new Label("🤖");
            roboLabel.setStyle("-fx-font-size: 20px;");
            celula.getChildren().addAll(rect, roboLabel);
            return celula;
        
        case PAREDE:
            rect.getStyleClass().add("celula-parede");
            break;
        
        case FINAL:
            rect.getStyleClass().add("celula-final");
            Label finalLabel = new Label("🎯");
            finalLabel.setStyle("-fx-font-size: 20px;");
            celula.getChildren().addAll(rect, finalLabel);
            return celula;
    }
    
    celula.getChildren().add(rect);
    return celula;
}
```

### Estilos CSS das Células

```css
.celula-vazia {
    -fx-fill: #FFFFFF;
    -fx-stroke: #E0E0E0;
    -fx-stroke-width: 1;
}

.celula-visitada {
    -fx-fill: #E3F2FD;         /* Azul muito claro */
    -fx-stroke: #90CAF9;
    -fx-stroke-width: 1;
}

.celula-robo {
    -fx-fill: linear-gradient(to bottom, #42A5F5, #1E88E5);
    -fx-stroke: #1565C0;
    -fx-stroke-width: 2;
    -fx-arc-width: 6;
    -fx-arc-height: 6;
}

.celula-parede {
    -fx-fill: linear-gradient(to bottom, #616161, #424242);
    -fx-stroke: #212121;
    -fx-stroke-width: 1;
}

.celula-final {
    -fx-fill: linear-gradient(to bottom, #FFD54F, #FFC107);
    -fx-stroke: #FFA000;
    -fx-stroke-width: 2;
    -fx-arc-width: 6;
    -fx-arc-height: 6;
}

/* Etapa 3: Células exploradas pelo BFS */
.celula-explorada {
    -fx-fill: rgba(0, 76, 153, 0.2);   /* Azul FURB transparente */
    -fx-stroke: #90CAF9;
    -fx-stroke-width: 1;
}

/* Etapa 3: Caminho encontrado */
.celula-caminho {
    -fx-fill: #FFD54F;
    -fx-stroke: #FFA000;
    -fx-stroke-width: 2;
}
```

---

## 6. Sistema de Animação

### Timeline para Movimento Contínuo

```java
private void iniciarSimulacao() {
    if (executando) return;
    
    executando = true;
    btnIniciar.setDisable(true);
    btnPausar.setDisable(false);
    cmbVelocidade.setDisable(true);
    
    // Velocidade base: 200ms por frame
    double intervalo = 200.0 / velocidadeMultiplicador;
    
    timeline = new Timeline(new KeyFrame(
        Duration.millis(intervalo),
        event -> {
            // 1. Executar movimento
            boolean moveu = movimentoAleatorioService.moverAleatorio(campo, robo);
            
            if (moveu) {
                contadorMovimentos++;
                lblMovimentos.setText("Movimentos: " + contadorMovimentos);
                lblPosicao.setText(String.format("Posição: (%d, %d)", 
                                                 robo.getPosicaoX(), 
                                                 robo.getPosicaoY()));
                
                // 2. Atualizar visualização
                atualizarGrid();
            }
            
            // 3. Verificar chegada ao final (Etapa 2+)
            if (movimentoBaseService.verificarChegadaAoFinal(campo, robo)) {
                pausarSimulacao();
                lblModo.setText("🎯 OBJETIVO ALCANÇADO!");
            }
        }
    ));
    
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}
```

### Controle de Velocidade

```css
Multiplicador | Intervalo (ms) | FPS
--------------|----------------|-----
1x            | 200            | 5
2x            | 100            | 10
5x            | 40             | 25
10x           | 20             | 50
```

**Implementação**:
```java
double intervalo = 200.0 / velocidadeMultiplicador;
```

### Pausar e Retomar

```java
private void pausarSimulacao() {
    if (timeline != null) {
        timeline.stop();
    }
    executando = false;
    btnIniciar.setDisable(false);
    btnPausar.setDisable(true);
    cmbVelocidade.setDisable(false);
}
```

---

## 7. Etapa 2 - Recursos Adicionais

### Modo de Edição Interativa

```java
private ComboBox<String> cmbModoEdicao;

private void configurarComboModoEdicao() {
    cmbModoEdicao.getItems().addAll(
        "Normal",
        "Adicionar Paredes",
        "Definir Final"
    );
    cmbModoEdicao.setValue("Normal");
}
```

### Click Handler

```java
private void handleCelulaClick(int linha, int coluna, boolean adicionar) {
    if (executando) return;  // Não editar durante simulação
    
    Casa casa = campo.getCasa(linha, coluna);
    String modoEdicao = cmbModoEdicao.getValue();
    
    switch (modoEdicao) {
        case "Adicionar Paredes":
            if (casa.getTipo() == TipoCelula.VAZIA) {
                if (adicionar) {  // Click esquerdo
                    casa.setTipo(TipoCelula.PAREDE);
                }
            } else if (casa.getTipo() == TipoCelula.PAREDE) {
                if (!adicionar) {  // Click direito
                    casa.setTipo(TipoCelula.VAZIA);
                }
            }
            break;
        
        case "Definir Final":
            // Remover FINAL anterior
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (campo.getCasa(i, j).getTipo() == TipoCelula.FINAL) {
                        campo.getCasa(i, j).setTipo(TipoCelula.VAZIA);
                    }
                }
            }
            // Definir novo FINAL
            if (casa.getTipo() == TipoCelula.VAZIA && adicionar) {
                casa.setTipo(TipoCelula.FINAL);
            }
            break;
    }
    
    atualizarGrid();
}
```

### Heat Map de Feromônios

```java
private void aplicarHeatMapFeromonio(Rectangle rect, int contadorVisitas) {
    if (contadorVisitas == 0) {
        rect.setFill(Color.web("#E3F2FD"));  // Azul muito claro
        return;
    }
    
    // Normalizar contador (0-255)
    int maxVisitas = 20;  // Assumir 20 como máximo para escala
    double intensidade = Math.min(contadorVisitas / (double) maxVisitas, 1.0);
    
    // Gradiente: Branco → Amarelo → Laranja → Vermelho
    int r = 255;
    int g = (int) (255 * (1 - intensidade * 0.8));
    int b = (int) (200 * (1 - intensidade));
    
    Color cor = Color.rgb(r, g, b, 0.5 + intensidade * 0.5);
    rect.setFill(cor);
    rect.setStroke(Color.rgb(r, g, b));
}
```

---

## 8. Etapa 3 & 4 - Busca Visual

### Duas Fases de Animação

#### Fase 1: Exploração (BFS/Dijkstra)

```java
private void executarAnimacaoBusca() {
    mostrandoBusca = true;
    sequenciaBusca = 0;
    
    Timeline timelineBusca = new Timeline(new KeyFrame(
        Duration.millis(50),  // Rápido para mostrar exploração
        event -> {
            if (sequenciaBusca >= celulasExploradas.size()) {
                ((Timeline) event.getSource()).stop();
                mostrandoBusca = false;
                executarAnimacaoCaminho();  // Inicia fase 2
                return;
            }
            
            // Mostrar próxima célula explorada
            sequenciaBusca++;
            atualizarGrid();
        }
    ));
    
    timelineBusca.setCycleCount(celulasExploradas.size());
    timelineBusca.play();
}
```

#### Fase 2: Execução do Caminho

```java
private void executarAnimacaoCaminho() {
    int indiceCaminho = 0;
    
    Timeline timelineCaminho = new Timeline(new KeyFrame(
        Duration.millis(300),
        event -> {
            if (indiceCaminho >= caminhoAtual.size()) {
                ((Timeline) event.getSource()).stop();
                lblStatus.setText("Chegou ao destino!");
                return;
            }
            
            Posicao proxima = caminhoAtual.get(indiceCaminho);
            robo.setPosicao(proxima);
            contadorMovimentos++;
            
            lblMovimentos.setText("Passos: " + contadorMovimentos);
            lblPosicao.setText(String.format("Posição: (%d, %d)", 
                                             proxima.getX(), 
                                             proxima.getY()));
            atualizarGrid();
            indiceCaminho++;
        }
    ));
    
    timelineCaminho.setCycleCount(caminhoAtual.size());
    timelineCaminho.play();
}
```

### Renderização de Células Especiais

```java
// Etapa 3: Células exploradas
if (ehExplorado && mostrandoBusca) {
    int indice = listaExploradas.indexOf(posicao);
    if (indice < sequenciaBusca) {
        rect.getStyleClass().add("celula-explorada");
    }
}

// Etapa 3: Caminho final
if (ehCaminho && !mostrandoBusca) {
    rect.getStyleClass().add("celula-caminho");
    Label estrela = new Label("★");
    estrela.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFB700;");
    celula.getChildren().addAll(rect, estrela);
}
```

---

## 9. Etapa 4 - Pesos Dinâmicos

### Edição de Pesos

```java
case "Definir Peso":
    if (casa.getTipo() == TipoCelula.VAZIA) {
        int pesoAtual = casa.getValor();
        if (adicionar) {  // Click esquerdo: +1
            if (pesoAtual < 99) {
                casa.setValor(pesoAtual + 1);
            }
        } else {  // Click direito: -1
            if (pesoAtual > 0) {
                casa.setValor(pesoAtual - 1);
            }
        }
        atualizarGrid();
    }
    break;
```

### Heat Map de Pesos

```java
private void aplicarCorPorPeso(Rectangle rect, int peso) {
    if (peso == 0) {
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.web("#E0E0E0"));
        return;
    }
    
    // Normalizar peso (1-10)
    double intensidade = Math.min(peso / 10.0, 1.0);
    
    // Gradiente: Branco → Rosa → Laranja → Vermelho
    int r = 255;
    int g = (int) (255 * (1 - intensidade * 0.7));
    int b = (int) (200 * (1 - intensidade));
    
    Color corFill = Color.rgb(r, g, b, 0.5 + intensidade * 0.3);
    Color corStroke = Color.rgb(r, g, b, 0.9);
    
    rect.setFill(corFill);
    rect.setStroke(corStroke);
    rect.setStrokeWidth(1.5);
    rect.setArcWidth(4);
    rect.setArcHeight(4);
}
```

### Label de Peso

```java
if (peso > 0) {
    Label pesoLabel = new Label(String.valueOf(peso));
    pesoLabel.getStyleClass().add("peso-label");
    pesoLabel.setStyle(String.format(
        "-fx-font-size: 12px; -fx-font-weight: bold; " +
        "-fx-text-fill: rgba(0, 0, 0, %.2f);",
        0.5 + intensidade * 0.5
    ));
    celula.getChildren().addAll(rect, pesoLabel);
}
```

---

## 10. Responsividade

### ScrollPane

```java
ScrollPane scrollPane = new ScrollPane();
scrollPane.setFitToWidth(true);
scrollPane.setFitToHeight(true);
scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
```

**CSS**:
```css
.scroll-pane {
    -fx-background-color: transparent;
    -fx-background: transparent;
}

.scroll-pane > .viewport {
    -fx-background-color: transparent;
}

.scroll-pane .scroll-bar:vertical .thumb {
    -fx-background-color: -furb-amarelo;
    -fx-background-radius: 4px;
}
```

### Dimensionamento Automático

```java
// Labels com wrap
descLabel.setWrapText(true);
descLabel.setMaxWidth(220);

// Botões expansíveis
card.setMaxWidth(Double.MAX_VALUE);
card.setMaxHeight(Double.MAX_VALUE);

// Spacers flexíveis
Region spacer = new Region();
HBox.setHgrow(spacer, Priority.ALWAYS);
```

---

## 11. Navegação e Estado

### Voltar ao Menu

```java
btnVoltar.setOnAction(e -> {
    if (executando) {
        pausarSimulacao();
    }
    
    HomeView homeView = new HomeView(stage);
    Scene scene = new Scene(homeView.getRoot(), 
                           stage.getWidth(), 
                           stage.getHeight());
    String css = getClass().getResource("/css/styles.css").toExternalForm();
    scene.getStylesheets().add(css);
    stage.setScene(scene);
});
```

### Reset de Simulação

```java
private void resetarSimulacao() {
    if (executando) {
        pausarSimulacao();
    }
    
    // Reset modelo
    this.campo = GeradorCampo.criarCampoEtapa1();
    this.robo = new Robo(0, 0);
    this.campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
    
    // Reset contadores
    this.contadorMovimentos = 0;
    this.lblMovimentos.setText("Movimentos: 0");
    this.lblPosicao.setText("Posição: (0, 0)");
    
    // Reset visual
    atualizarGrid();
    
    // Reset controles
    cmbVelocidade.setValue("1x");
    cmbModo.setDisable(false);
}
```

---

## 12. Otimização e Performance

### Boas Práticas Implementadas

1. **Timeline Reutilização**: Uma timeline por simulação (não cria nova a cada frame)
2. **Grid Rendering**: `gridLabirinto.getChildren().clear()` antes de redesenhar
3. **CSS Caching**: Estilos aplicados via classes (não inline)
4. **Event Delegation**: Handlers configurados uma vez no construtor
5. **Lazy Loading**: Views criadas apenas quando necessário

### Evitar Vazamentos de Memória

```java
// Sempre parar timeline antes de trocar de cena
if (timeline != null) {
    timeline.stop();
    timeline = null;
}
```

---

## 13. Acessibilidade e UX

### Feedback Visual

- **Botões desabilitados**: Cinza claro, sem cursor
- **Hover states**: Escala 1.03x, sombra colorida
- **Pressed states**: Escala 0.97x
- **Loading**: Timeline animando células

### Atalhos de Teclado

```java
scene.setOnKeyPressed(event -> {
    switch (event.getCode()) {
        case SPACE:
            if (executando) pausarSimulacao();
            else iniciarSimulacao();
            break;
        case R:
            resetarSimulacao();
            break;
        case ESCAPE:
            voltarParaMenu();
            break;
    }
});
```

### Mensagens Claras

```java
lblStatus.setText("Buscando caminho...");
lblStatus.setText("Caminho encontrado! Executando...");
lblStatus.setText("Nenhum caminho disponível!");
lblStatus.setText("🎯 OBJETIVO ALCANÇADO!");
```

---

## Conclusão

A interface gráfica do projeto é construída com:
- **Modularidade**: Cada view é independente
- **Consistência**: Design system aplicado uniformemente
- **Interatividade**: Animações e controles responsivos
- **Clareza**: Feedback visual claro em todas as operações

O uso de JavaFX permite criar uma experiência visual rica que complementa perfeitamente os algoritmos implementados, tornando o projeto tanto funcional quanto esteticamente agradável.
