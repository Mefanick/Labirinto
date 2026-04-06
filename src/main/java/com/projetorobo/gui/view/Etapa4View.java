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
import javafx.scene.input.MouseButton;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import com.projetorobo.core.model.Campo;
import com.projetorobo.core.model.Robo;
import com.projetorobo.core.model.Casa;
import com.projetorobo.core.model.Posicao;
import com.projetorobo.core.enums.TipoCelula;
import com.projetorobo.core.util.GeradorCampo;
import com.projetorobo.etapa4.service.DijkstraService;
import com.projetorobo.etapa4.model.ResultadoDijkstra;

import java.util.List;

public class Etapa4View {
    private final Stage stage;
    private final BorderPane root;
    private final GridPane gridLabirinto;
    private final Label lblMovimentos;
    private final Label lblPosicao;
    private final Label lblStatus;
    private final Label lblCusto;
    private final Label lblModoEdicao;
    private final Button btnIniciar;
    private final Button btnPausar;
    private final Button btnReset;
    private final Button btnSair;
    private final Button btnFinalAleatorio;
    private final Button btnPesosAleatorios;
    private final ComboBox<String> cmbVelocidade;
    private final ComboBox<String> cmbModoEdicao;
    
    private Campo campo;
    private Robo robo;
    private DijkstraService dijkstraService;
    private Timeline timeline;
    private int contadorMovimentos;
    private boolean executando;
    private double velocidadeMultiplicador;
    private String modoEdicaoAtual;
    
    private List<Posicao> caminhoAtual;
    private int indiceCaminhoAtual;
    private int custoTotalCaminho;
    
    private static final int TAMANHO_CELULA = 38;
    private static final int GRID_SIZE = 10;
    
    public Etapa4View(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.gridLabirinto = new GridPane();
        this.lblMovimentos = new Label("Movimentos: 0");
        this.lblPosicao = new Label("Posição: (0, 5)");
        this.lblStatus = new Label("Status: Aguardando");
        this.lblCusto = new Label("Custo Total: 0");
        this.lblModoEdicao = new Label("Edição: Normal");
        this.btnIniciar = new Button("INICIAR");
        this.btnPausar = new Button("PAUSAR");
        this.btnReset = new Button("🔄 RESETAR");
        this.btnSair = new Button("✕ SAIR");
        this.btnFinalAleatorio = new Button("🎯 FINAL ALEATÓRIO");
        this.btnPesosAleatorios = new Button("⚖️ PESOS ALEATÓRIOS");
        this.cmbVelocidade = new ComboBox<>();
        this.cmbModoEdicao = new ComboBox<>();
        
        this.contadorMovimentos = 0;
        this.executando = false;
        this.velocidadeMultiplicador = 1.0;
        this.modoEdicaoAtual = "Normal";
        this.custoTotalCaminho = 0;
        
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
        cmbModoEdicao.getItems().addAll("Normal", "Adicionar Paredes", "Definir Final", "Definir Peso");
        cmbModoEdicao.setValue("Normal");
        cmbModoEdicao.getStyleClass().add("combo-modo");
        cmbModoEdicao.setOnAction(e -> {
            modoEdicaoAtual = cmbModoEdicao.getValue();
            lblModoEdicao.setText("Edição: " + modoEdicaoAtual);
        });
    }
    
    private void inicializarModelo() {
        this.campo = GeradorCampo.criarCampoEtapa4();
        this.robo = new Robo(0, 5);  // Posição inicial: linha 0, coluna 5 (onde está 'i')
        this.dijkstraService = new DijkstraService();
        this.campo.getCasa(0, 5).setTipo(TipoCelula.OCUPADA);
        
        // Definir posição final fixa em (9, 5) - onde está 'f'
        this.campo.getCasa(9, 5).setTipo(TipoCelula.FINAL);
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
        
        Label titulo = new Label("ETAPA 4 - ALGORITMO DE DIJKSTRA");
        titulo.getStyleClass().add("etapa-titulo");
        
        Label subtitulo = new Label("Encontra o caminho com menor custo considerando pesos nas células");
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
        lblCusto.getStyleClass().add("info-label");
        lblStatus.getStyleClass().add("info-label");
        lblModoEdicao.getStyleClass().add("info-label");
        
        infoBox.getChildren().addAll(lblMovimentos, lblPosicao, lblCusto, lblStatus, lblModoEdicao);
        
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
        Casa casa = campo.getCasa(linha, coluna);
        Posicao posAtual = new Posicao(linha, coluna);
        
        celula.setOnMouseClicked(event -> {
            if (!executando) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    handleCelulaClick(linha, coluna, true);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    handleCelulaClick(linha, coluna, false);
                }
            }
        });
        
        celula.setStyle("-fx-cursor: hand;");
        
        boolean ehRobo = (linha == robo.getPosicaoX() && coluna == robo.getPosicaoY());
        boolean ehCaminho = caminhoAtual != null && caminhoAtual.contains(posAtual);
        
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
        } else if (ehCaminho) {
            rect.getStyleClass().add("celula-caminho");
            Label caminhoLabel = new Label("★");
            caminhoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFB700;");
            celula.getChildren().addAll(rect, caminhoLabel);
        } else if (casa.getValor() > 0) {
            aplicarCorPorPeso(rect, casa.getValor());
            Label pesoLabel = new Label(String.valueOf(casa.getValor()));
            pesoLabel.getStyleClass().add("peso-label");
            celula.getChildren().addAll(rect, pesoLabel);
        } else if (casa.isVisitada()) {
            rect.getStyleClass().add("celula-visitada");
            celula.getChildren().add(rect);
        } else {
            rect.getStyleClass().add("celula-vazia");
            celula.getChildren().add(rect);
        }
        
        return celula;
    }
    
    private void aplicarCorPorPeso(Rectangle rect, int peso) {
        double intensidade = Math.min(peso / 10.0, 1.0);
        
        int r = 255;
        int g = (int)(255 * (1 - intensidade * 0.7));
        int b = (int)(200 * (1 - intensidade));
        
        Color corFill = Color.rgb(r, g, b, 0.5 + intensidade * 0.3);
        Color corStroke = Color.rgb(r, g, b, 0.9);
        
        rect.setFill(corFill);
        rect.setStroke(corStroke);
        rect.setStrokeWidth(2);
        rect.setArcWidth(6);
        rect.setArcHeight(6);
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
        btnPesosAleatorios.getStyleClass().addAll("btn", "btn-final-aleatorio");
        
        linha2.getChildren().addAll(lblEdicaoLabel, cmbModoEdicao, btnFinalAleatorio, btnPesosAleatorios);
        
        Label lblDica = new Label("💡 Clique Esquerdo: +1 peso | Clique Direito: -1 peso");
        lblDica.getStyleClass().add("label-dica");
        lblDica.setStyle("-fx-font-size: 11px; -fx-text-fill: #6C757D; -fx-padding: 5 0 0 0;");
        
        controlesBox.getChildren().addAll(linha1, linha2, lblDica);
        
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
    
    private void handleCelulaClick(int linha, int coluna, boolean adicionar) {
        Casa casa = campo.getCasa(linha, coluna);
        
        switch (modoEdicaoAtual) {
            case "Adicionar Paredes":
                if (adicionar) {
                    if (casa.getTipo() == TipoCelula.VAZIA || casa.getTipo() == TipoCelula.PAREDE) {
                        if (casa.getTipo() == TipoCelula.VAZIA) {
                            casa.setTipo(TipoCelula.PAREDE);
                        } else {
                            casa.setTipo(TipoCelula.VAZIA);
                        }
                        atualizarGrid();
                    }
                }
                break;
                
            case "Definir Final":
                if (adicionar) {
                    if (casa.getTipo() == TipoCelula.VAZIA || casa.getTipo() == TipoCelula.FINAL) {
                        removerFinalExistente();
                        
                        if (casa.getTipo() != TipoCelula.FINAL) {
                            casa.setTipo(TipoCelula.FINAL);
                        }
                        atualizarGrid();
                    }
                }
                break;
                
            case "Definir Peso":
                if (casa.getTipo() == TipoCelula.VAZIA) {
                    int pesoAtual = casa.getValor();
                    
                    if (adicionar) {
                        if (pesoAtual < 99) {
                            casa.setValor(pesoAtual + 1);
                        }
                    } else {
                        if (pesoAtual > 0) {
                            casa.setValor(pesoAtual - 1);
                        }
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
    
    private void gerarPesosAleatorios() {
        if (!executando) {
            java.util.Random random = new java.util.Random();
            
            for (int i = 0; i < campo.getLinhas(); i++) {
                for (int j = 0; j < campo.getColunas(); j++) {
                    Casa casa = campo.getCasa(i, j);
                    
                    if (casa.getTipo() == TipoCelula.VAZIA) {
                        int peso = random.nextInt(10) + 1;
                        casa.setValor(peso);
                    }
                }
            }
            
            atualizarGrid();
        }
    }
    
    private void configurarEventos() {
        btnIniciar.setOnAction(e -> iniciarSimulacao());
        btnPausar.setOnAction(e -> pausarSimulacao());
        btnSair.setOnAction(e -> voltarParaMenu());
        btnReset.setOnAction(e -> resetarSimulacao());
        btnFinalAleatorio.setOnAction(e -> gerarFinalAleatorio());
        btnPesosAleatorios.setOnAction(e -> gerarPesosAleatorios());
    }
    
    private void iniciarSimulacao() {
        if (executando) return;
        
        Posicao inicio = new Posicao(robo.getPosicaoX(), robo.getPosicaoY());
        Posicao fim = encontrarPosicaoFinal();
        
        if (fim == null) {
            lblStatus.setText("❌ Erro: Final não definido!");
            return;
        }
        
        lblStatus.setText("🔍 Calculando melhor caminho...");
        
        executando = true;
        btnIniciar.setDisable(true);
        btnPausar.setDisable(false);
        btnReset.setDisable(true);
        cmbModoEdicao.setDisable(true);
        btnFinalAleatorio.setDisable(true);
        btnPesosAleatorios.setDisable(true);
        
        ResultadoDijkstra resultado = dijkstraService.calcularMenorCusto(campo, inicio, fim);
        
        if (!resultado.isCaminhoEncontrado()) {
            lblStatus.setText("❌ Nenhum caminho encontrado!");
            pausarSimulacao();
            return;
        }
        
        caminhoAtual = resultado.getCaminho();
        custoTotalCaminho = resultado.getCustoTotal();
        indiceCaminhoAtual = 1;
        
        lblCusto.setText("Custo Total: " + custoTotalCaminho);
        lblStatus.setText("🤖 Executando caminho ótimo...");
        
        atualizarGrid();
        executarAnimacaoCaminho();
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
        btnPesosAleatorios.setDisable(false);
    }
    
    private void resetarSimulacao() {
        pausarSimulacao();
        
        this.campo = GeradorCampo.criarCampoEtapa4();
        this.robo = new Robo(0, 5);  // Posição inicial: linha 0, coluna 5
        this.campo.getCasa(0, 5).setTipo(TipoCelula.OCUPADA);
        this.contadorMovimentos = 0;
        this.caminhoAtual = null;
        this.custoTotalCaminho = 0;
        
        // Definir posição final fixa em (9, 5)
        this.campo.getCasa(9, 5).setTipo(TipoCelula.FINAL);
        
        lblMovimentos.setText("Movimentos: 0");
        lblPosicao.setText("Posição: (0, 5)");
        lblCusto.setText("Custo Total: 0");
        lblStatus.setText("Status: Aguardando");
        
        atualizarGrid();
        
        btnIniciar.setDisable(false);
        btnPausar.setDisable(true);
        btnReset.setDisable(false);
        cmbModoEdicao.setDisable(false);
        btnFinalAleatorio.setDisable(false);
        btnPesosAleatorios.setDisable(false);
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
