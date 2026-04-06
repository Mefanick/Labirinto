package com.projetorobo.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.projetorobo.gui.view.HomeView;

public class MainGUI extends Application {
    
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 820;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulador de Robô - Labirinto");
        
        HomeView homeView = new HomeView(primaryStage);
        Scene scene = new Scene(homeView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(javafx.scene.paint.Color.web("#F8F9FA"));
        
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.setResizable(true);
        
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
        
        primaryStage.setFullScreenExitHint("Pressione F11 ou ESC para sair do modo tela cheia");
        
        primaryStage.show();
        primaryStage.setMaximized(true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
