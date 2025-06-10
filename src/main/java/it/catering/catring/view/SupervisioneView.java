// File: src/main/java/it/catering/catring/view/SupervisioneView.java
package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.CompitoController;
import it.catering.catring.model.entities.Compito;
import it.catering.catring.model.states.StatoCompito;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SupervisioneView {
    private CompitoController compitoController;
    private VBox mainContent;
    private TableView<Compito> compitiTable;
    private PieChart statusChart;
    private VBox statsCardsContainer;
    
    public SupervisioneView() {
        this.compitoController = ApplicationController.getInstance().getCompitoController();
        createView();
        loadDatiSupervisione();
    }
    
    public ScrollPane getView() {
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }
    
    private void createView() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: white;");
        
        // Header
        HBox header = createHeader();
        
        // Statistics section
        HBox statsSection = createStatsSection();
        
        // Table section
        VBox tableSection = createTableSection();
        
        mainContent.getChildren().addAll(header, new Separator(), statsSection, new Separator(), tableSection);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Supervisione Cucina");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadDatiSupervisione());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, refreshButton);
        return header;
    }
    
    private HBox createStatsSection() {
        HBox statsSection = new HBox(30);
        statsSection.setAlignment(javafx.geometry.Pos.CENTER);
        statsSection.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-background-radius: 10;");
        
        // Statistics cards
        statsCardsContainer = createStatsCards();
        
        // Chart
        statusChart = new PieChart();
        statusChart.setTitle("Distribuzione Stati Compiti");
        statusChart.setPrefSize(300, 250);
        
        statsSection.getChildren().addAll(statsCardsContainer, statusChart);
        return statsSection;
    }
    
    private VBox createStatsCards() {
        VBox cardsContainer = new VBox(15);
        
        // Total tasks card
        VBox totalCard = createStatCard("Compiti Totali", "0", "#3498db");
        totalCard.setId("totalCard");
        
        // Completed tasks card
        VBox completedCard = createStatCard("Completati", "0", "#27ae60");
        completedCard.setId("completedCard");
        
        // In progress tasks card
        VBox inProgressCard = createStatCard("In Corso", "0", "#f39c12");
        inProgressCard.setId("inProgressCard");
        
        // Problem tasks card
        VBox problemCard = createStatCard("Problemi", "0", "#e74c3c");
        problemCard.setId("problemCard");
        
        cardsContainer.getChildren().addAll(totalCard, completedCard, inProgressCard, problemCard);
        return cardsContainer;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-min-width: 120;");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        valueLabel.setId("valueLabel");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createTableSection() {
        VBox tableSection = new VBox(15);
        
        Label tableTitle = new Label("Dettaglio Compiti");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        compitiTable = createCompitiTable();
        
        tableSection.getChildren().addAll(tableTitle, compitiTable);
        return tableSection;
    }
    
    private TableView<Compito> createCompitiTable() {
        TableView<Compito> table = new TableView<>();
        table.setPrefHeight(300);
        
        // Colonne
        TableColumn<Compito, String> preparazioneCol = new TableColumn<>("Preparazione");
        preparazioneCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getPreparazione().getNome()));
        preparazioneCol.setPrefWidth(200);
        
        TableColumn<Compito, String> cuocoCol = new TableColumn<>("Cuoco");
        cuocoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCuoco().getNomeCompleto()));
        cuocoCol.setPrefWidth(150);
        
        TableColumn<Compito, String> tempoCol = new TableColumn<>("Tempo Stimato");
        tempoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTempoStimato() + " min"));
        tempoCol.setPrefWidth(120);
        
        TableColumn<Compito, String> quantitaCol = new TableColumn<>("Quantità");
        quantitaCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getQuantita())));
        quantitaCol.setPrefWidth(100);
        
        TableColumn<Compito, String> statoCol = new TableColumn<>("Stato");
        statoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStato().toString()));
        statoCol.setPrefWidth(120);
        
        // Custom cell factory per colorare le righe in base allo stato
        statoCol.setCellFactory(column -> new TableCell<Compito, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    TableRow<Compito> row = getTableRow();
                    if (row != null && row.getItem() != null) {
                        StatoCompito stato = row.getItem().getStato();
                        switch (stato) {
                            case COMPLETATO -> row.setStyle("-fx-background-color: #d4edda;");
                            case IN_CORSO -> row.setStyle("-fx-background-color: #fff3cd;");
                            case PROBLEMA -> row.setStyle("-fx-background-color: #f8d7da;");
                            default -> row.setStyle("");
                        }
                    }
                }
            }
        });
        
        TableColumn<Compito, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNote() != null ? data.getValue().getNote() : ""));
        noteCol.setPrefWidth(250);
        
        table.getColumns().addAll(preparazioneCol, cuocoCol, tempoCol, quantitaCol, statoCol, noteCol);
        
        return table;
    }
    
    private void loadDatiSupervisione() {
        try {
            List<Compito> tuttiCompiti = compitoController.getTuttiICompiti();
            
            // Update table
            ObservableList<Compito> compitiList = FXCollections.observableArrayList(tuttiCompiti);
            compitiTable.setItems(compitiList);
            
            // Update statistics
            updateStatistics(tuttiCompiti);
            
            // Update chart
            updateChart(tuttiCompiti);
            
        } catch (Exception e) {
            // Mostra l'errore ma continua a mostrare la UI
            System.err.println("Errore nel caricamento dati supervisione: " + e.getMessage());
            
            // Inizializza con dati vuoti
            compitiTable.setItems(FXCollections.observableArrayList());
            updateStatisticsEmpty();
            updateChartEmpty();
        }
    }
    
    private void updateStatistics(List<Compito> compiti) {
        int total = compiti.size();
        long completati = compiti.stream().filter(c -> c.getStato() == StatoCompito.COMPLETATO).count();
        long inCorso = compiti.stream().filter(c -> c.getStato() == StatoCompito.IN_CORSO).count();
        long problemi = compiti.stream().filter(c -> c.getStato() == StatoCompito.PROBLEMA).count();
        
        // Aggiorna le card usando gli ID
        updateStatCardById("totalCard", String.valueOf(total));
        updateStatCardById("completedCard", String.valueOf(completati));
        updateStatCardById("inProgressCard", String.valueOf(inCorso));
        updateStatCardById("problemCard", String.valueOf(problemi));
    }
    
    private void updateStatisticsEmpty() {
        updateStatCardById("totalCard", "0");
        updateStatCardById("completedCard", "0");
        updateStatCardById("inProgressCard", "0");
        updateStatCardById("problemCard", "0");
    }
    
    private void updateStatCardById(String cardId, String newValue) {
        try {
            VBox card = (VBox) statsCardsContainer.lookup("#" + cardId);
            if (card != null) {
                Label valueLabel = (Label) card.lookup("#valueLabel");
                if (valueLabel != null) {
                    valueLabel.setText(newValue);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nell'aggiornamento della card " + cardId + ": " + e.getMessage());
        }
    }
    
    private void updateChart(List<Compito> compiti) {
        try {
            Map<StatoCompito, Long> conteggiStati = compiti.stream()
                .collect(Collectors.groupingBy(Compito::getStato, Collectors.counting()));
            
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            for (Map.Entry<StatoCompito, Long> entry : conteggiStati.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
            }
            
            // Se non ci sono dati, aggiungi un segmento vuoto
            if (pieChartData.isEmpty()) {
                pieChartData.add(new PieChart.Data("Nessun compito", 1));
            }
            
            statusChart.setData(pieChartData);
            
            // Color the chart segments
            statusChart.getData().forEach(data -> {
                String stato = data.getName();
                String color = switch (stato) {
                    case "COMPLETATO" -> "-fx-pie-color: #27ae60;";
                    case "IN_CORSO" -> "-fx-pie-color: #f39c12;";
                    case "PROBLEMA" -> "-fx-pie-color: #e74c3c;";
                    case "ASSEGNATO" -> "-fx-pie-color: #3498db;";
                    default -> "-fx-pie-color: #95a5a6;";
                };
                
                if (data.getNode() != null) {
                    data.getNode().setStyle(color);
                }
            });
        } catch (Exception e) {
            System.err.println("Errore nell'aggiornamento del grafico: " + e.getMessage());
            updateChartEmpty();
        }
    }
    
    private void updateChartEmpty() {
        try {
            ObservableList<PieChart.Data> emptyData = FXCollections.observableArrayList();
            emptyData.add(new PieChart.Data("Nessun compito", 1));
            statusChart.setData(emptyData);
            
            // Colora il segmento vuoto
            statusChart.getData().forEach(data -> {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: #95a5a6;");
                }
            });
        } catch (Exception e) {
            System.err.println("Errore nella creazione del grafico vuoto: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}