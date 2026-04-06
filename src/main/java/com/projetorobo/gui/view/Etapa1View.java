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
import javafx.util.Duration;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa1.service.MovimentoAleatorioService;
import com.projetorobo.etapa1.service.MovimentoLinhaRetaService;

public class Etapa1View {
    private final Stage stage;
    private final BorderPane root;
    private final GridPane gridLabirinto;
    private final Label lblMovimentos;
    private final Label lblPosicao;
    private final Label lblModo;
    private final Button btnIniciar;
    private final Button btnPausar;
    private final Button btnVoltar;
    private final Button btnReset;
    private final Button btnSair;
    private final ComboBox<String> cmbVelocidade;
    private final ComboBox<String> cmbModo;
    
    private Campo campo;
    private Robo robo;
    private MovimentoAleatorioService movimentoAleatorioService;
    private MovimentoLinhaRetaService movimentoLinhaRetaService;
    private Timeline timeline;
    private int contadorMovimentos;
    private boolean executando;
    private double velocidadeMultiplicador;
    private String modoAtual;
    
    private static final int TAMANHO_CELULA = 38;
    private static final int GRID_SIZE = 10;
    
    public Etapa1View(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.gridLabirinto = new GridPane();
        this.lblMovimentos = new Label("Movimentos: 0");
        this.lblPosicao = new Label("Posição: (0, 0)");
        this.lblModo = new Label("Modo: Aleatório");
        this.btnIniciar = new Button("INICIAR");
        this.btnPausar = new Button("PAUSAR");
        this.btnVoltar = new Button("← VOLTAR");
        this.btnReset = new Button("🔄 RESETAR");
        this.btnSair = new Button("✕ SAIR");
        this.cmbVelocidade = new ComboBox<>();
        this.cmbModo = new ComboBox<>();
        
        this.contadorMovimentos = 0;
        this.executando = false;
        this.velocidadeMultiplicador = 1.0;
        this.modoAtual = "Aleatório";
        
        configurarComboVelocidade();
        configurarComboModo();
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
    
    private void configurarComboModo() {
        cmbModo.getItems().addAll("Aleatório", "Linha Reta");
        cmbModo.setValue("Aleatório");
        cmbModo.getStyleClass().add("combo-modo");
        cmbModo.setOnAction(e -> {
            modoAtual = cmbModo.getValue();
            lblModo.setText("Modo: " + modoAtual);
            
            if (executando) {
                pausarSimulacao();
            }
        });
    }
    
    private void inicializarModelo() {
        this.campo = GeradorCampo.criarCampoEtapa1();
        this.robo = new Robo(0, 0);
        this.movimentoAleatorioService = new MovimentoAleatorioService();
        this.movimentoLinhaRetaService = new MovimentoLinhaRetaService();
        this.campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
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
        
        Label titulo = new Label("ETAPA 1 - MOVIMENTO ALEATÓRIO");
        titulo.getStyleClass().add("etapa-titulo");
        
        Label subtitulo = new Label("O robô se move aleatoriamente pelo campo vazio");
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
        HBox controlesBox = criarControlesBox();
        
        centerContent.getChildren().addAll(infoBox, labirintoContainer, controlesBox);
        
        return centerContent;
    }
    
    private HBox criarInfoBox() {
        HBox infoBox = new HBox(25);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getStyleClass().add("info-box");
        infoBox.setPadding(new Insets(8, 15, 8, 15));
        
        lblMovimentos.getStyleClass().add("info-label");
        lblPosicao.getStyleClass().add("info-label");
        lblModo.getStyleClass().add("info-label");
        
        infoBox.getChildren().addAll(lblMovimentos, lblPosicao, lblModo);
        
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
    
    private HBox criarControlesBox() {
        HBox controlesBox = new HBox(12);
        controlesBox.setAlignment(Pos.CENTER);
        controlesBox.setPadding(new Insets(8, 15, 8, 15));
        
        Label lblModoLabel = new Label("Modo:");
        lblModoLabel.getStyleClass().add("label-controle");
        
        Label lblVelocidade = new Label("Velocidade:");
        lblVelocidade.getStyleClass().add("label-controle");
        
        btnReset.getStyleClass().addAll("btn", "btn-reset");
        
        controlesBox.getChildren().addAll(lblModoLabel, cmbModo, lblVelocidade, cmbVelocidade, btnReset);
        
        return controlesBox;
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
        Casa casa = campo.getCasa(linha, coluna);
        
        switch (casa.getTipo()) {
            case OCUPADA:
                rect.getStyleClass().add("celula-robo");
                Label roboLabel = new Label("🤖");
                roboLabel.setStyle("-fx-font-size: 20px;");
                celula.getChildren().addAll(rect, roboLabel);
                break;
            case PAREDE:
                rect.getStyleClass().add("celula-parede");
                celula.getChildren().add(rect);
                break;
            case FINAL:
                rect.getStyleClass().add("celula-final");
                Label finalLabel = new Label("🎯");
                finalLabel.setStyle("-fx-font-size: 18px;");
                celula.getChildren().addAll(rect, finalLabel);
                break;
            default:
                if (casa.isVisitada()) {
                    rect.getStyleClass().add("celula-visitada");
                } else {
                    rect.getStyleClass().add("celula-vazia");
                }
                celula.getChildren().add(rect);
        }
        
        return celula;
    }
    
    private HBox criarBottomBar() {
        HBox bottomBar = new HBox(12);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(8));
        bottomBar.getStyleClass().add("etapa-bottombar");
        
        btnIniciar.getStyleClass().addAll("btn", "btn-primary");
        btnPausar.getStyleClass().addAll("btn", "btn-secondary");
        btnVoltar.getStyleClass().addAll("btn", "btn-voltar");
        
        btnPausar.setDisable(true);
        
        bottomBar.getChildren().addAll(btnIniciar, btnPausar);
        
        return bottomBar;
    }
    
    private void configurarEventos() {
        btnIniciar.setOnAction(e -> iniciarSimulacao());
        btnPausar.setOnAction(e -> pausarSimulacao());
        btnVoltar.setOnAction(e -> voltarParaMenu());
        btnSair.setOnAction(e -> voltarParaMenu());
        btnReset.setOnAction(e -> resetarSimulacao());
    }
    
    private void iniciarSimulacao() {
        if (executando) return;
        
        lblModo.setText("Modo: " + modoAtual);
        
        if (modoAtual.equals("Linha Reta")) {
            movimentoLinhaRetaService.resetarDirecao();
        }
        
        executando = true;
        btnIniciar.setDisable(true);
        btnPausar.setDisable(false);
        btnReset.setDisable(true);
        cmbModo.setDisable(true);
        
        double velocidadeBase = 500;
        double velocidadeAjustada = velocidadeBase / velocidadeMultiplicador;
        
        timeline = new Timeline(new KeyFrame(Duration.millis(velocidadeAjustada), e -> {
            boolean moveu = false;
            
            if (modoAtual.equals("Aleatório")) {
                moveu = movimentoAleatorioService.moverAleatorio(campo, robo);
            } else {
                moveu = movimentoLinhaRetaService.moverLinhaReta(campo, robo);
            }
            
            if (moveu) {
                contadorMovimentos++;
                lblMovimentos.setText("Movimentos: " + contadorMovimentos);
                lblPosicao.setText("Posição: (" + robo.getPosicaoX() + ", " + robo.getPosicaoY() + ")");
                atualizarGrid();
                
                if (campo.getCasa(robo.getPosicaoX(), robo.getPosicaoY()).getTipo() == TipoCelula.FINAL) {
                    pausarSimulacao();
                }
            } else {
                pausarSimulacao();
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void pausarSimulacao() {
        if (timeline != null) {
            timeline.stop();
        }
        executando = false;
        btnIniciar.setDisable(false);
        btnPausar.setDisable(true);
        btnReset.setDisable(false);
        cmbModo.setDisable(false);
    }
    
    private void resetarSimulacao() {
        pausarSimulacao();
        
        this.campo = GeradorCampo.criarCampoEtapa1();
        this.robo = new Robo(0, 0);
        this.campo.getCasa(0, 0).setTipo(TipoCelula.OCUPADA);
        this.contadorMovimentos = 0;
        
        movimentoLinhaRetaService.resetarDirecao();
        
        lblMovimentos.setText("Movimentos: 0");
        lblPosicao.setText("Posição: (0, 0)");
        lblModo.setText("Modo: " + modoAtual);
        
        atualizarGrid();
        
        btnIniciar.setDisable(false);
        btnPausar.setDisable(true);
        btnReset.setDisable(false);
        cmbModo.setDisable(false);
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
