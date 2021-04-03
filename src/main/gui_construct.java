/** *****************************************************************************
 * Copyright (C) 2019, Christian Marangi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***************************************************************************** */
package main;

import java.io.File;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class gui_construct {

    private final Scene scene;
    private rbi_info info;

    public static enum State {
        LOADED,
        STREAMING,
        IDLE
    };
    private State state = State.IDLE;

    TextField FileInputText;
    TextArea log;

    StackPane buttonPanel;
    Button decryptFile;
    Button encryptFile;
    Button loadFile;

    TitledPane BoardInputPanel, OsKeysInputPanel;
    ComboBox<String> BoardSelect;
    Label ModelLabel;
    Label OsKeysInfotext;
    ComboBox<os_key_couple> OsKeysInput;

    GridPane HeaderSubPanel;
    GridPane InfoBlockSubPanel;
    ToggleGroup OsckGroup;

    public gui_construct() {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 10, 10, 10));

        this.scene = new Scene(root);

        scene.getStylesheets().add(Main.class.getResource("/modena_dark.css").toExternalForm());

        root.setTop(Header());
        root.setCenter(Content());
        root.setBottom(Footer());
    }

    button_listners Listners = new button_listners(this);

    private Node Header() {

        final StackPane headPanel = new StackPane();
        headPanel.setPadding(new Insets(0, 0, 20, 0));

        final Label head = new Label("Utility to decrypt Technicolor RBI firmware files");
        head.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        headPanel.getChildren().add(head);

        return headPanel;
    }

    private Node Content() {
        ColumnConstraints defaultColSpace = new ColumnConstraints();
        ColumnConstraints halfColumnsSpace = new ColumnConstraints();
        halfColumnsSpace.setPercentWidth(50);
        ColumnConstraints fillColumnsSpace = new ColumnConstraints();
        fillColumnsSpace.setHgrow(Priority.ALWAYS);

        RowConstraints defaultRowSpace = new RowConstraints();
        RowConstraints halfRowsSpace = new RowConstraints();
        halfRowsSpace.setPercentHeight(50);
        RowConstraints fillRowsSpace = new RowConstraints();
        fillRowsSpace.setVgrow(Priority.ALWAYS);
        
        GridPane content = new GridPane();
        content.getColumnConstraints().addAll(halfColumnsSpace, halfColumnsSpace);
        content.getRowConstraints().addAll(defaultRowSpace, defaultRowSpace, defaultRowSpace, fillRowsSpace);

        content.add(InputFilePanel(), 0, 0, 2, 1);
        content.add(InputBoardPanel(), 0, 1, 2, 1);
        content.add(InputOsKeysPanel(), 0, 2, 2, 1);
        content.add(InfoLogPanel(), 0, 3, 2, 1);

        return content;
    }

    private Node Footer() {
        VBox footer = new VBox();

        footer.getChildren().add(Separator());
        footer.getChildren().add(ButtonPanel());

        return footer;
    }

    private Node InputFilePanel() {

        final HBox fileSubPanel = new HBox();

        FileInputText = new TextField();
        FileInputText.setEditable(false);
        Button input = new Button("Select File");
        input.setOnAction(Listners.getInputFileEventHandler());

        fileSubPanel.getChildren().add(FileInputText);
        fileSubPanel.getChildren().add(input);

        HBox.setHgrow(FileInputText, Priority.ALWAYS);

        TitledPane filePanel = new TitledPane("RBI firmware File", fileSubPanel);
        filePanel.setCollapsible(false);
        //filePanel.setPadding(new Insets(0,0,10,0));

        return filePanel;
    }

    private Node InputBoardPanel() {
        final HBox InputBoardSubPanel = new HBox();
        InputBoardSubPanel.setAlignment(Pos.CENTER_LEFT);
        InputBoardSubPanel.setSpacing(10);

        BoardSelect = new ComboBox<>();
        BoardSelect.getItems().addAll(board.getMap().keySet());
        BoardSelect.getItems().add(0, "Manual");
        BoardSelect.setOnAction(Listners.getCheckBoardSelectHandler());

        ModelLabel = new Label("Please select");

        InputBoardSubPanel.getChildren().add(BoardSelect);
        InputBoardSubPanel.getChildren().add(ModelLabel);

        BoardInputPanel = new TitledPane("Model Select", InputBoardSubPanel);
        BoardInputPanel.setCollapsible(false);

        return BoardInputPanel;
    }

    private Node InputOsKeysPanel() {

        VBox InputOsKeysSubPanel = new VBox();
        InputOsKeysSubPanel.setAlignment(Pos.CENTER_LEFT);
        InputOsKeysSubPanel.setSpacing(5);

        OsKeysInfotext = new Label("Insert the extracted OSCK key (64 char long)");
        OsKeysInfotext.setVisible(false);
        OsKeysInfotext.managedProperty().bind(OsKeysInfotext.visibleProperty());

        OsKeysInput = new ComboBox();

        Pattern pattern = Pattern.compile(".{0,64}");
        TextFormatter<TextFormatter.Change> formatter = new TextFormatter<>((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        OsKeysInput.setEditable(true);
        OsKeysInput.getEditor().setTextFormatter(formatter);

        InputOsKeysSubPanel.getChildren().add(OsKeysInfotext);
        InputOsKeysSubPanel.getChildren().add(OsKeysInput);

        OsKeysInputPanel = new TitledPane("OSCKey", InputOsKeysSubPanel);
        OsKeysInputPanel.setCollapsible(false);
        OsKeysInputPanel.setVisible(false);
        OsKeysInputPanel.managedProperty().bind(OsKeysInputPanel.visibleProperty());

        return OsKeysInputPanel;
    }

    public void updateHeaderSubPanel() {

        Map<String, String> header = info.getHeaderTable();

        HeaderSubPanel.getChildren().clear();

        int r = 0;
        for (Map.Entry<String, String> entry : header.entrySet()) {
            HeaderSubPanel.addRow(r++, new Label(entry.getKey().concat(": ")), new Label(entry.getValue()));
        }
    }

    public void updateInfoBlockSubPanel() {

        Map<String, String> infoblock = info.getInfoBlockTable();

        InfoBlockSubPanel.getChildren().clear();

        int r = 0;
        for (Map.Entry<String, String> entry : infoblock.entrySet()) {
            InfoBlockSubPanel.addRow(r++, new Label(entry.getKey().concat(": ")), new Label(entry.getValue()));
        }
    }

    private Node InfoLogPanel() {
        ColumnConstraints defaultColSpace = new ColumnConstraints();
        ColumnConstraints halfColumnsSpace = new ColumnConstraints();
        halfColumnsSpace.setPercentWidth(50);
        ColumnConstraints fillColumnsSpace = new ColumnConstraints();
        fillColumnsSpace.setHgrow(Priority.ALWAYS);

        RowConstraints halfRowsSpace = new RowConstraints();
        halfRowsSpace.setPercentHeight(50);
        halfRowsSpace.setValignment(VPos.TOP);

        HeaderSubPanel = new GridPane();
        HeaderSubPanel.getColumnConstraints().addAll(defaultColSpace, fillColumnsSpace);

        InfoBlockSubPanel = new GridPane();
        InfoBlockSubPanel.getColumnConstraints().addAll(defaultColSpace, fillColumnsSpace);
        //InfoBlockSubPanel.setPrefHeight(90);

        log = new TextArea();
        log.setEditable(false);

        GridPane infoPanel = new GridPane();
        infoPanel.getColumnConstraints().addAll(halfColumnsSpace, halfColumnsSpace);
        infoPanel.getRowConstraints().addAll(halfRowsSpace, halfRowsSpace);

        TitledPane logPanel = new TitledPane("Log", log);
        logPanel.setCollapsible(false);

        TitledPane infoBlockPanel = new TitledPane("Info Block", new ScrollPane(InfoBlockSubPanel));
        infoBlockPanel.setCollapsible(false);

        TitledPane HeaderPanel = new TitledPane("Header Info", new ScrollPane(HeaderSubPanel));
        HeaderPanel.setCollapsible(false);

        infoPanel.add(logPanel, 0, 0);
        infoPanel.add(infoBlockPanel, 0, 1);
        infoPanel.add(HeaderPanel, 1, 0, 1, 2);

        return infoPanel;
    }

    private Node ButtonPanel() {
        buttonPanel = new StackPane();

        decryptFile = new Button("Decrypt");
        decryptFile.setDisable(true);
        decryptFile.setOnAction(Listners.getDecryptFileEventHandler());

        buttonPanel.getChildren().add(decryptFile);

        return buttonPanel;
    }

    private Node Separator() {

        final HBox SeparatorPanel = new HBox();
        //SeparatorPanel.setPadding(new Insets(20, 0, 10, 0));

        final Separator LineSeparator = new Separator();
        LineSeparator.setOrientation(Orientation.HORIZONTAL);
        final Label Version = new Label("Version " + gui_inizializer.Version);
        //Version.setPadding(new Insets(-10, 0, 0, 0));
        Version.setTextFill(Color.GRAY);

        SeparatorPanel.getChildren().add(LineSeparator);
        SeparatorPanel.getChildren().add(Version);

        HBox.setHgrow(LineSeparator, Priority.ALWAYS);

        return SeparatorPanel;
    }

    public rbi_info newRbiInfo(File file) {
        info = new rbi_info(file);
        return info;
    }

    public rbi_info getRbiInfo() {
        return info;
    }

    public void setState(String state) {
        this.state = State.valueOf(state);
    }

    public Scene getScene() {
        return scene;
    }

    public State getState() {
        return state;
    }

}
