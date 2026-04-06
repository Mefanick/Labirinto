package com.projetorobo.gui.view;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class HomeView {
    private final StackPane root;
    private final Stage stage;
    
    public HomeView(Stage stage) {
        this.stage = stage;
        this.root = createHomeScreen();
        animateEntrance();
    }
    
    private StackPane createHomeScreen() {
        StackPane mainContainer = new StackPane();
        mainContainer.getStyleClass().add("home-container");
        
        VBox contentBox = new VBox(30);
        contentBox.getStyleClass().add("home-content");
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(900);
        contentBox.setPadding(new Insets(60, 80, 60, 80));
        
        VBox headerBox = createHeader();
        GridPane menuGrid = createMenuGrid();
        HBox footerBox = createFooter();
        
        VBox.setVgrow(menuGrid, Priority.ALWAYS);
        
        contentBox.getChildren().addAll(headerBox, menuGrid, footerBox);
        mainContainer.getChildren().add(contentBox);
        
        return mainContainer;
    }
    
    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Label title = new Label("SIMULADOR DE ROBÔ • FURB");
        title.getStyleClass().add("home-title");
        
        Label subtitle = new Label("Navegação Inteligente em Labirinto");
        subtitle.getStyleClass().add("home-subtitle");
        
        Region spacer = new Region();
        spacer.setMinHeight(15);
        
        header.getChildren().addAll(title, subtitle, spacer);
        
        return header;
    }
    
    private GridPane createMenuGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        
        Button btnEtapa1 = createEtapaCard(
            "🎲",
            "ETAPA 1",
            "Movimento Aleatório",
            "Exploração básica do campo com movimentos aleatórios e sem persistência."
        );
        btnEtapa1.setOnAction(e -> {
            abrirEtapa1();
        });
        
        Button btnEtapa2 = createEtapaCard(
            "🧠",
            "ETAPA 2",
            "Movimento Inteligente",
            "Sistema de feromônios para evitar caminhos redundantes e otimizar a navegação."
        );
        btnEtapa2.setOnAction(e -> {
            abrirEtapa2();
        });
        
        Button btnEtapa3 = createEtapaCard(
            "🔍",
            "ETAPA 3",
            "Busca BFS",
            "Algoritmo de Busca em Largura para garantir o caminho mais curto."
        );
        btnEtapa3.setOnAction(e -> abrirEtapa3());
        
        Button btnEtapa4 = createEtapaCard(
            "⚖️",
            "ETAPA 4",
            "Dijkstra",
            "Algoritmo de Dijkstra para encontrar o caminho com menor custo usando pesos."
        );
        btnEtapa4.setOnAction(e -> abrirEtapa4());
        
        grid.add(btnEtapa1, 0, 0);
        grid.add(btnEtapa2, 1, 0);
        grid.add(btnEtapa3, 0, 1);
        grid.add(btnEtapa4, 1, 1);
        
        return grid;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));
        
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        
        Label info = new Label("Desenvolvido para Algoritmos de Busca e Otimização");
        info.getStyleClass().add("label-info");
        
        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);
        
        Button btnSair = new Button("SAIR");
        btnSair.getStyleClass().addAll("btn", "btn-exit");
        btnSair.setMinWidth(120);
        btnSair.setOnAction(e -> stage.close());
        
        footer.getChildren().addAll(leftSpacer, info, centerSpacer, btnSair);
        
        return footer;
    }
    
    private Button createEtapaCard(String emoji, String numero, String titulo, String descricao) {
        Button card = new Button();
        card.getStyleClass().addAll("btn-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        
        VBox content = new VBox(16);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(24));
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.getStyleClass().add("card-icon");
        
        Label numeroLabel = new Label(numero);
        numeroLabel.getStyleClass().add("card-numero");
        
        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("card-titulo");
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Label descLabel = new Label(descricao);
        descLabel.getStyleClass().add("card-descricao");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(220);
        
        content.getChildren().addAll(emojiLabel, numeroLabel, tituloLabel, spacer, descLabel);
        card.setGraphic(content);
        
        return card;
    }
    
    private void animateEntrance() {
        VBox contentBox = (VBox) root.getChildren().get(0);
        
        for (int i = 0; i < contentBox.getChildren().size(); i++) {
            javafx.scene.Node node = contentBox.getChildren().get(i);
            
            FadeTransition fade = new FadeTransition(Duration.millis(800), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(800), node);
            slide.setFromY(40);
            slide.setToY(0);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
            scale.setFromX(0.95);
            scale.setFromY(0.95);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            PauseTransition pause = new PauseTransition(Duration.millis(i * 150));
            
            ParallelTransition parallel = new ParallelTransition(fade, slide, scale);
            SequentialTransition seq = new SequentialTransition(pause, parallel);
            seq.play();
        }
    }
    
    private void abrirEtapa1() {
        Etapa1View etapa1View = new Etapa1View(stage);
        Scene scene = new Scene(etapa1View.getRoot(), stage.getWidth(), stage.getHeight());
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }
    
    private void abrirEtapa2() {
        Etapa2View etapa2View = new Etapa2View(stage);
        Scene scene = new Scene(etapa2View.getRoot(), stage.getWidth(), stage.getHeight());
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }
    
    private void abrirEtapa3() {
        Etapa3View etapa3View = new Etapa3View(stage);
        Scene scene = new Scene(etapa3View.getRoot(), stage.getWidth(), stage.getHeight());
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }
    
    private void abrirEtapa4() {
        Etapa4View etapa4View = new Etapa4View(stage);
        Scene scene = new Scene(etapa4View.getRoot(), stage.getWidth(), stage.getHeight());
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
    }
    
    public Parent getRoot() {
        return root;
    }
}
