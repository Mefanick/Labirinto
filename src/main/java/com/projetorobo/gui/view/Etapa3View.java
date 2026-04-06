package com.projetorobo.gui.view;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa3.service.BuscaService;
import com.projetorobo.etapa3.model.ResultadoBusca;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Etapa3View {
    private final Stage stage;
    private final BorderPane root;
    private final GridPane gridLabirinto;
    private final Label lblMovimentos;
    private final Label lblPosicao;
    private final Label lblStatus;
    private final Label lblModoEdicao;
    private final Button btnIniciar;
    private final Button btnPausar;
    private final Button btnReset;
    private final Button btnSair;
    private final Button btnFinalAleatorio;
    private final ComboBox<String> cmbVelocidade;
    private final ComboBox<String> cmbModoEdicao;
    
    private Campo campo;
    private Robo robo;
    private BuscaService buscaService;
    private Timeline timeline;
    private int contadorMovimentos;
    private boolean executando;
    private double velocidadeMultiplicador;
    private String modoEdicaoAtual;
    
    private List<Posicao> caminhoAtual;
    private Set<Posicao> celulasExploradas;
    private int indiceCaminhoAtual;
    private boolean mostrandoBusca;
    private int indiceBuscaAtual;
    private List<Posicao> sequenciaBusca;
    private Map<Posicao, Integer> profundidadeNos;
    private List<List<Posicao>> ondasBFS;
    private int ondaAtual;
    
    private static final int TAMANHO_CELULA = 38;
    private static final int GRID_SIZE = 10;
    
    public Etapa3View(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.gridLabirinto = new GridPane();
        this.lblMovimentos = new Label("Movimentos: 0");
        this.lblPosicao = new Label("Posição: (0, 0)");
        this.lblStatus = new Label("Status: Aguardando");
        this.lblModoEdicao = new Label("Edição: Normal");
        this.btnIniciar = new Button("INICIAR");
        this.btnPausar = new Button("PAUSAR");
        this.btnReset = new Button("🔄 RESETAR");
        this.btnSair = new Button("✕ SAIR");
        this.btnFinalAleatorio = new Button("🎯 FINAL ALEATÓRIO");
        this.cmbVelocidade = new ComboBox<>();
        this.cmbModoEdicao = new ComboBox<>();
        
        this.contadorMovimentos = 0;
        this.executando = false;
        this.velocidadeMultiplicador = 1.0;
        this.modoEdicaoAtual = "Normal";
        this.celulasExploradas = new HashSet<>();
        this.mostrandoBusca = false;
        this.indiceBuscaAtual = 0;
        this.profundidadeNos = new HashMap<>();
        this.ondasBFS = new ArrayList<>();
        this.ondaAtual = 0;
        
        configurarComboVelocidade();
        configurarComboModoEdicao();
        inicializarModelo();
        criarInterface();
        configurarEventos();
    }
    
    private void configurarComboVelocidade() {
        cmbVelocidade.getItems().addAll("1x", "2x", "5x", "10x");
        cmbVelocidade.setValue("1x");
        cmbVelocidade.getStyleClass().add("combo-velocidade");
        cmbVelocidade.setOnAction(e -> {
            String valor = cmbVelocidade.getValue();
            velocidadeMultiplicador = Double.parseDouble(valor.replace("x", ""));
            
            if (executando) {
                pausarSimulacao();
                iniciarSimulacao();
            }
        });
    }
    
    private void configurarComboModoEdicao() {
        cmbModoEdicao.getItems().addAll("Normal", "Adicionar Paredes", "Definir Final");
        cmbModoEdicao.setValue("Normal");
        cmbModoEdicao.getStyleClass().add("combo-modo");
        cmbModoEdicao.setOnAction(e -> {
            modoEdicaoAtual = cmbModoEdicao.getValue();
            lblModoEdicao.setText("Edição: " + modoEdicaoAtual);
        });
    }
    
    private void inicializarModelo() {
        this.campo = GeradorCampo.criarCampoEtapa3();
        this.robo = new Robo(0, 0);
        this.buscaService = new BuscaService();
        this.campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
        
        GeradorCampo.definirPosicaoFinalAleatoria(campo);
    }
    
    private void criarInterface() {
        root.getStyleClass().add("etapa-container");
        
        VBox topBar = criarTopBar();
        ScrollPane scrollPane = criarScrollPane();
        HBox bottomBar = criarBottomBar();
        
        root.setTop(topBar);
        root.setCenter(scrollPane);
        root.setBottom(bottomBar);
    }
    
    private ScrollPane criarScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        VBox centerContent = criarCenterContent();
        scrollPane.setContent(centerContent);
        
        return scrollPane;
    }
    
    private VBox criarTopBar() {
        VBox topBar = new VBox(5);
        topBar.getStyleClass().add("etapa-topbar");
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(8));
        
        HBox headerContainer = new HBox();
        headerContainer.setAlignment(Pos.CENTER);
        
        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, javafx.scene.layout.Priority.ALWAYS);
        
        VBox centerBox = new VBox(3);
        centerBox.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("ETAPA 3 - BUSCA EM LARGURA (BFS)");
        titulo.getStyleClass().add("etapa-titulo");
        
        Label subtitulo = new Label("Encontra o caminho mais curto explorando em ondas concêntricas");
        subtitulo.getStyleClass().add("etapa-subtitulo");
        
        centerBox.getChildren().addAll(titulo, subtitulo);
        
        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, javafx.scene.layout.Priority.ALWAYS);
        
        btnSair.getStyleClass().addAll("btn", "btn-sair-etapa");
        
        headerContainer.getChildren().addAll(spacerLeft, centerBox, spacerRight, btnSair);
        
        topBar.getChildren().add(headerContainer);
        
        return topBar;
    }
    
    private VBox criarCenterContent() {
        VBox centerContent = new VBox(8);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(5, 8, 5, 8));
        
        HBox infoBox = criarInfoBox();
        StackPane labirintoContainer = criarLabirinto();
        VBox controlesBox = criarControlesBox();
        
        centerContent.getChildren().addAll(infoBox, labirintoContainer, controlesBox);
        
        return centerContent;
    }
    
    private HBox criarInfoBox() {
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getStyleClass().add("info-box");
        infoBox.setPadding(new Insets(8, 15, 8, 15));
        
        lblMovimentos.getStyleClass().add("info-label");
        lblPosicao.getStyleClass().add("info-label");
        lblStatus.getStyleClass().add("info-label");
        lblModoEdicao.getStyleClass().add("info-label");
        
        infoBox.getChildren().addAll(lblMovimentos, lblPosicao, lblStatus, lblModoEdicao);
        
        return infoBox;
    }
    
    private StackPane criarLabirinto() {
        StackPane container = new StackPane();
        container.getStyleClass().add("labirinto-container");
        container.setPadding(new Insets(10));
        
        gridLabirinto.setAlignment(Pos.CENTER);
        gridLabirinto.setHgap(1);
        gridLabirinto.setVgap(1);
        
        atualizarGrid();
        
        container.getChildren().add(gridLabirinto);
        
        return container;
    }
    
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
        celula.setPrefSize(TAMANHO_CELULA, TAMANHO_CELULA);
        celula.setMinSize(TAMANHO_CELULA, TAMANHO_CELULA);
        celula.setMaxSize(TAMANHO_CELULA, TAMANHO_CELULA);
        
        Rectangle rect = new Rectangle(TAMANHO_CELULA, TAMANHO_CELULA);
        rect.setArcWidth(4);
        rect.setArcHeight(4);
        
        Casa casa = campo.getCasa(linha, coluna);
        Posicao posAtual = new Posicao(linha, coluna);
        
        celula.setOnMouseClicked(event -> {
            if (!executando) {
                handleCelulaClick(linha, coluna);
            }
        });
        
        celula.setStyle("-fx-cursor: hand;");
        
        boolean ehRobo = (linha == robo.getPosicaoX() && coluna == robo.getPosicaoY());
        boolean ehCaminho = caminhoAtual != null && caminhoAtual.contains(posAtual);
        boolean ehExplorado = celulasExploradas.contains(posAtual);
        
        if (ehRobo) {
            rect.getStyleClass().add("celula-robo");
            Label roboLabel = new Label("🤖");
            roboLabel.setStyle("-fx-font-size: 20px;");
            celula.getChildren().addAll(rect, roboLabel);
        } else if (casa.getTipo() == TipoCelula.PAREDE) {
            rect.getStyleClass().add("celula-parede");
            celula.getChildren().add(rect);
        } else if (casa.getTipo() == TipoCelula.FINAL) {
            rect.getStyleClass().add("celula-final");
            Label finalLabel = new Label("🎯");
            finalLabel.setStyle("-fx-font-size: 18px;");
            celula.getChildren().addAll(rect, finalLabel);
        } else if (ehCaminho && !mostrandoBusca) {
            // Caminho final com gradiente dourado e animação pulsante
            Color corCaminho = Color.web("#FFB700");
            rect.setFill(Color.web("#FFF3CD"));
            rect.setStroke(corCaminho);
            rect.setStrokeWidth(2.5);
            
            Label caminhoLabel = new Label("★");
            caminhoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FFB700; -fx-font-weight: bold;");
            celula.getChildren().addAll(rect, caminhoLabel);
            
            // Animação pulsante suave para o caminho
            ScaleTransition pulse = new ScaleTransition(Duration.millis(800), celula);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.1);
            pulse.setToY(1.1);
            pulse.setCycleCount(Timeline.INDEFINITE);
            pulse.setAutoReverse(true);
            pulse.play();
        } else if (ehExplorado) {
            // Gradiente de azul baseado na profundidade
            Integer profundidade = profundidadeNos.get(posAtual);
            Color corExplorada = calcularCorPorProfundidade(profundidade);
            rect.setFill(corExplorada);
            rect.setStroke(corExplorada.darker());
            rect.setStrokeWidth(1);
            celula.getChildren().add(rect);
        } else if (casa.isVisitada()) {
            rect.getStyleClass().add("celula-visitada");
            celula.getChildren().add(rect);
        } else {
            rect.getStyleClass().add("celula-vazia");
            celula.getChildren().add(rect);
        }
        
        return celula;
    }
    
    private Color calcularCorPorProfundidade(Integer profundidade) {
        if (profundidade == null) {
            return Color.web("#90CAF9"); // Azul claro padrão
        }
        
        // Gradiente de azul claro para azul escuro baseado na profundidade
        // Profundidade 0 (início) = azul muito claro
        // Profundidades maiores = azul progressivamente mais escuro
        double intensidade = Math.min(profundidade / 15.0, 1.0); // Normalizar até profundidade 15
        
        int r = (int) (144 - intensidade * 144); // 144 -> 0
        int g = (int) (202 - intensidade * 126); // 202 -> 76
        int b = (int) (249 - intensidade * 96);  // 249 -> 153
        
        return Color.rgb(r, g, b, 0.7 + intensidade * 0.3);
    }
    
    private VBox criarControlesBox() {
        VBox controlesBox = new VBox(8);
        controlesBox.setAlignment(Pos.CENTER);
        controlesBox.setPadding(new Insets(8, 15, 8, 15));
        
        HBox linha1 = new HBox(12);
        linha1.setAlignment(Pos.CENTER);
        
        Label lblVelocidade = new Label("Velocidade:");
        lblVelocidade.getStyleClass().add("label-controle");
        
        btnReset.getStyleClass().addAll("btn", "btn-reset");
        
        linha1.getChildren().addAll(lblVelocidade, cmbVelocidade, btnReset);
        
        HBox linha2 = new HBox(12);
        linha2.setAlignment(Pos.CENTER);
        
        Label lblEdicaoLabel = new Label("Edição:");
        lblEdicaoLabel.getStyleClass().add("label-controle");
        
        btnFinalAleatorio.getStyleClass().addAll("btn", "btn-final-aleatorio");
        
        linha2.getChildren().addAll(lblEdicaoLabel, cmbModoEdicao, btnFinalAleatorio);
        
        controlesBox.getChildren().addAll(linha1, linha2);
        
        return controlesBox;
    }
    
    private HBox criarBottomBar() {
        HBox bottomBar = new HBox(12);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(8));
        bottomBar.getStyleClass().add("etapa-bottombar");
        
        btnIniciar.getStyleClass().addAll("btn", "btn-primary");
        btnPausar.getStyleClass().addAll("btn", "btn-secondary");
        
        btnPausar.setDisable(true);
        
        bottomBar.getChildren().addAll(btnIniciar, btnPausar);
        
        return bottomBar;
    }
    
    private void handleCelulaClick(int linha, int coluna) {
        Casa casa = campo.getCasa(linha, coluna);
        
        switch (modoEdicaoAtual) {
            case "Adicionar Paredes":
                if (casa.getTipo() == TipoCelula.VAZIA || casa.getTipo() == TipoCelula.PAREDE) {
                    if (casa.getTipo() == TipoCelula.VAZIA) {
                        casa.setTipo(TipoCelula.PAREDE);
                    } else {
                        casa.setTipo(TipoCelula.VAZIA);
                    }
                    atualizarGrid();
                }
                break;
                
            case "Definir Final":
                if (casa.getTipo() == TipoCelula.VAZIA || casa.getTipo() == TipoCelula.FINAL) {
                    removerFinalExistente();
                    
                    if (casa.getTipo() != TipoCelula.FINAL) {
                        casa.setTipo(TipoCelula.FINAL);
                    }
                    atualizarGrid();
                }
                break;
        }
    }
    
    private void removerFinalExistente() {
        for (int i = 0; i < campo.getLinhas(); i++) {
            for (int j = 0; j < campo.getColunas(); j++) {
                if (campo.getCasa(i, j).getTipo() == TipoCelula.FINAL) {
                    campo.getCasa(i, j).setTipo(TipoCelula.VAZIA);
                }
            }
        }
    }
    
    private void gerarFinalAleatorio() {
        if (!executando) {
            removerFinalExistente();
            GeradorCampo.definirPosicaoFinalAleatoria(campo);
            atualizarGrid();
        }
    }
    
    private void configurarEventos() {
        btnIniciar.setOnAction(e -> iniciarSimulacao());
        btnPausar.setOnAction(e -> pausarSimulacao());
        btnSair.setOnAction(e -> voltarParaMenu());
        btnReset.setOnAction(e -> resetarSimulacao());
        btnFinalAleatorio.setOnAction(e -> gerarFinalAleatorio());
    }
    
    private void iniciarSimulacao() {
        if (executando) return;
        
        Posicao inicio = new Posicao(robo.getPosicaoX(), robo.getPosicaoY());
        Posicao fim = encontrarPosicaoFinal();
        
        if (fim == null) {
            lblStatus.setText("❌ Erro: Final não definido!");
            return;
        }
        
        lblStatus.setText("🔍 Buscando caminho...");
        celulasExploradas.clear();
        profundidadeNos.clear();
        ondasBFS.clear();
        
        executando = true;
        btnIniciar.setDisable(true);
        btnPausar.setDisable(false);
        btnReset.setDisable(true);
        cmbModoEdicao.setDisable(true);
        btnFinalAleatorio.setDisable(true);
        
        // Executar BFS e calcular profundidades
        calcularBFSComProfundidade(inicio, fim);
        
        ResultadoBusca resultado = buscaService.buscarCaminhoBFS(campo, inicio, fim);
        
        if (!resultado.isCaminhoEncontrado()) {
            lblStatus.setText("❌ Nenhum caminho encontrado!");
            pausarSimulacao();
            return;
        }
        
        caminhoAtual = resultado.getCaminho();
        celulasExploradas = resultado.getNosExplorados();
        sequenciaBusca = new ArrayList<>(celulasExploradas);
        indiceBuscaAtual = 0;
        mostrandoBusca = true;
        ondaAtual = 0;
        
        lblStatus.setText("📊 Visualizando busca em ondas...");
        
        executarAnimacaoBuscaPorOndas();
    }
    
    private void calcularBFSComProfundidade(Posicao inicio, Posicao fim) {
        Queue<Posicao> fila = new LinkedList<>();
        Set<Posicao> visitados = new HashSet<>();
        
        fila.add(inicio);
        visitados.add(inicio);
        profundidadeNos.put(inicio, 0);
        
        List<Posicao> ondaAtualList = new ArrayList<>();
        ondaAtualList.add(inicio);
        ondasBFS.add(ondaAtualList);
        
        int profundidadeAtual = 0;
        
        while (!fila.isEmpty()) {
            int tamanhoNivel = fila.size();
            List<Posicao> proximaOnda = new ArrayList<>();
            
            for (int i = 0; i < tamanhoNivel; i++) {
                Posicao atual = fila.poll();
                
                if (atual.equals(fim)) {
                    continue;
                }
                
                List<Posicao> vizinhos = atual.getVizinhos();
                for (Posicao vizinho : vizinhos) {
                    if (campo.posicaoValida(vizinho) && 
                        !visitados.contains(vizinho) && 
                        campo.getCasa(vizinho).podeSerAcessada()) {
                        
                        visitados.add(vizinho);
                        fila.add(vizinho);
                        profundidadeNos.put(vizinho, profundidadeAtual + 1);
                        proximaOnda.add(vizinho);
                    }
                }
            }
            
            if (!proximaOnda.isEmpty()) {
                ondasBFS.add(proximaOnda);
            }
            profundidadeAtual++;
        }
    }
    
    private void executarAnimacaoBuscaPorOndas() {
        if (ondaAtual >= ondasBFS.size()) {
            mostrandoBusca = false;
            indiceCaminhoAtual = 1;
            lblStatus.setText("🤖 Executando caminho ótimo...");
            executarAnimacaoCaminho();
            return;
        }
        
        List<Posicao> ondaAtualNos = ondasBFS.get(ondaAtual);
        
        // Adicionar nós da onda atual às células exploradas
        for (Posicao pos : ondaAtualNos) {
            celulasExploradas.add(pos);
        }
        
        atualizarGrid();
        
        // Animar células da onda atual com efeito de aparecimento
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Posicao pos = new Posicao(i, j);
                if (ondaAtualNos.contains(pos)) {
                    StackPane celula = getCelulaDoGrid(i, j);
                    if (celula != null) {
                        animarCelulaExplorada(celula);
                    }
                }
            }
        }
        
        ondaAtual++;
        
        double velocidadeBase = 200; // Mais lento para apreciar as ondas
        double velocidadeAjustada = velocidadeBase / velocidadeMultiplicador;
        
        Timeline delayTimeline = new Timeline(new KeyFrame(
            Duration.millis(velocidadeAjustada),
            e -> executarAnimacaoBuscaPorOndas()
        ));
        delayTimeline.setCycleCount(1);
        delayTimeline.play();
    }
    
    private StackPane getCelulaDoGrid(int linha, int coluna) {
        for (javafx.scene.Node node : gridLabirinto.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            
            int row = (rowIndex == null) ? 0 : rowIndex;
            int col = (colIndex == null) ? 0 : colIndex;
            
            if (row == linha && col == coluna && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null;
    }
    
    private void animarCelulaExplorada(StackPane celula) {
        // Fade in suave
        FadeTransition fade = new FadeTransition(Duration.millis(300), celula);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        
        // Efeito de pulso crescente
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), celula);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        ParallelTransition parallel = new ParallelTransition(fade, scale);
        parallel.play();
    }
    
    private void executarAnimacaoCaminho() {
        double velocidadeBase = 300;
        double velocidadeAjustada = velocidadeBase / velocidadeMultiplicador;
        
        timeline = new Timeline(new KeyFrame(Duration.millis(velocidadeAjustada), e -> {
            if (indiceCaminhoAtual < caminhoAtual.size()) {
                Posicao proxima = caminhoAtual.get(indiceCaminhoAtual);
                
                campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY()).setTipo(TipoCelula.VAZIA);
                campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY()).marcarComoVisitada();
                
                robo.setPosicao(proxima.getX(), proxima.getY());
                
                if (campo.getCasa(proxima.getX(), proxima.getY()).getTipo() != TipoCelula.FINAL) {
                    campo.getCasa(proxima.getX(), proxima.getY()).setTipo(TipoCelula.OCUPADA);
                }
                
                contadorMovimentos++;
                lblMovimentos.setText("Movimentos: " + contadorMovimentos);
                lblPosicao.setText("Posição: (" + robo.getPosicaoX() + ", " + robo.getPosicaoY() + ")");
                
                atualizarGrid();
                
                if (campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY()).getTipo() == TipoCelula.FINAL) {
                    pausarSimulacao();
                    lblStatus.setText("🎯 OBJETIVO ALCANÇADO!");
                }
                
                indiceCaminhoAtual++;
            } else {
                pausarSimulacao();
                lblStatus.setText("✅ Caminho concluído!");
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private Posicao encontrarPosicaoFinal() {
        for (int i = 0; i < campo.getLinhas(); i++) {
            for (int j = 0; j < campo.getColunas(); j++) {
                if (campo.getCasa(i, j).getTipo() == TipoCelula.FINAL) {
                    return new Posicao(i, j);
                }
            }
        }
        return null;
    }
    
    private void pausarSimulacao() {
        if (timeline != null) {
            timeline.stop();
        }
        executando = false;
        btnIniciar.setDisable(false);
        btnPausar.setDisable(true);
        btnReset.setDisable(false);
        cmbModoEdicao.setDisable(false);
        btnFinalAleatorio.setDisable(false);
    }
    
    private void resetarSimulacao() {
        pausarSimulacao();
        
        this.campo = GeradorCampo.criarCampoEtapa3();
        this.robo = new Robo(0, 0);
        this.campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
        this.contadorMovimentos = 0;
        this.caminhoAtual = null;
        this.celulasExploradas.clear();
        this.mostrandoBusca = false;
        this.indiceBuscaAtual = 0;
        this.profundidadeNos.clear();
        this.ondasBFS.clear();
        this.ondaAtual = 0;
        
        GeradorCampo.definirPosicaoFinalAleatoria(campo);
        
        lblMovimentos.setText("Movimentos: 0");
        lblPosicao.setText("Posição: (0, 0)");
        lblStatus.setText("Status: Aguardando");
        
        atualizarGrid();
        
        btnIniciar.setDisable(false);
        btnPausar.setDisable(true);
        btnReset.setDisable(false);
        cmbModoEdicao.setDisable(false);
        btnFinalAleatorio.setDisable(false);
    }
    
    private void voltarParaMenu() {
        pausarSimulacao();
        HomeView homeView = new HomeView(stage);
        Scene scene = new Scene(homeView.getRoot(), stage.getWidth(), stage.getHeight());
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }
    
    public Parent getRoot() {
        return root;
    }
}
