package main;

import java.io.File;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class gui_construct {
	private final Scene scene;
	private rbi_info rbi_constructor;
	public static enum State {
		LOADED,
		DECRYPTED,
		IDLE
	};
	private State state = State.IDLE;
	
	TextField FileInputText;
	TextArea log;

	StackPane buttonPanel;
	Button decryptFile;
	Button encryptFile;
	Button loadFile;
	
	TitledPane OsckInputPanel;
	ComboBox<String> OsckSelect;
	Label ModelLabel;
	Label OsckManualinfotext;
	TextField OsckInput;
	
	HBox HeaderSubPanel;
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
		headPanel.setPadding(new Insets(0 , 0 , 20 , 0 ));
		
		final Label head = new Label("Utility to decrypt Technicolor RBI firmware files");
		head.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		headPanel.getChildren().add(head);
		
		return headPanel;
	}
    
	private Node Content() {
		VBox content = new VBox();
		
		content.getChildren().add(InputFilePanel());
		content.getChildren().add(InputOsckPanel());
		content.getChildren().add(InputManualOsckPanel());
		content.getChildren().add(InfoLogPanel());
		
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
		filePanel.setPadding(new Insets(0,0,10,0));
		
		return filePanel;
	}
    
    private Node InputOsckPanel() {
    	final HBox InputOsckSubPanel = new HBox();
    	
    	OsckSelect = new ComboBox<>();
		OsckSelect.getItems().addAll(board.getMap().keySet());
		OsckSelect.getItems().add(0, "Manual");
    	OsckSelect.setOnAction(Listners.getcheckOsckSelectHandler());
    	
		ModelLabel = new Label("Please select");
    	ModelLabel.setPadding(new Insets(0,0,0,10));
		
    	InputOsckSubPanel.getChildren().add(OsckSelect);
		InputOsckSubPanel.getChildren().add(ModelLabel);
		
		TitledPane osckInputPanel = new TitledPane("Model Select", InputOsckSubPanel); 
		osckInputPanel.setCollapsible(false);
		osckInputPanel.setPadding(new Insets(0,0,10,0));
    	
    	return osckInputPanel;
    }
    
    private Node InputManualOsckPanel() {
		
		VBox InputManualOsckSubPanel = new VBox();
		
		OsckManualinfotext = new Label("Insert the extracted OSCK key (64 char long)");
		OsckManualinfotext.setVisible(false);
		OsckManualinfotext.managedProperty().bind(OsckManualinfotext.visibleProperty());
        
		OsckInput = new TextField();
		
		Pattern pattern = Pattern.compile(".{0,64}");
	    TextFormatter<TextFormatter.Change> formatter = new TextFormatter<>((UnaryOperator<TextFormatter.Change>) change -> {
	        return pattern.matcher(change.getControlNewText()).matches() ? change : null;
	    });
		
		OsckInput.setTextFormatter(formatter);
		
		InputManualOsckSubPanel.getChildren().add(OsckManualinfotext);
		InputManualOsckSubPanel.getChildren().add(OsckInput);
		
		OsckInputPanel = new TitledPane("OSCK Key", InputManualOsckSubPanel); 
		OsckInputPanel.setCollapsible(false);
		OsckInputPanel.setPadding(new Insets(0,0,10,0));
		OsckInputPanel.setVisible(false);
		OsckInputPanel.managedProperty().bind(OsckInputPanel.visibleProperty());
		
		return OsckInputPanel;
	}
    
    public void updateHeaderSubPanel() {
    	
    	Map<String,String> header = rbi_constructor.getHeaderTable();
    	
    	VBox HeaderLegend = new VBox();
    	VBox HeaderValue = new VBox();
    	
    	for (Map.Entry<String, String> entry : header.entrySet()) {
		    HeaderLegend.getChildren().add(new Label(entry.getKey()));
		    HeaderValue.getChildren().add(new Label(entry.getValue()));
		}
    	
    	HeaderSubPanel.getChildren().clear();
    	HeaderSubPanel.getChildren().add(HeaderLegend);
    	HeaderSubPanel.getChildren().add(HeaderValue);
    }
    
    private Node InfoLogPanel() {
		
    	HeaderSubPanel = new HBox();
    	
		log = new TextArea();
		log.setEditable(false);
		
		TitledPane logPanel = new TitledPane("Log", new ScrollPane(log)); 
		logPanel.setCollapsible(false);
		logPanel.setPadding(new Insets(0,5,0,0));
		
		TitledPane HeaderPanel = new TitledPane("Header Info", HeaderSubPanel); 
		HeaderPanel.setCollapsible(false);
		HeaderPanel.setPadding(new Insets(0,0,0,5));
		HeaderSubPanel.setPrefWidth(350);
		HeaderSubPanel.setPrefHeight(350);
		
		HBox infoPanel = new HBox();
		
		infoPanel.getChildren().add(logPanel);
		infoPanel.getChildren().add(HeaderPanel);
		
		
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
    	SeparatorPanel.setPadding(new Insets(20, 0, 10, 0));
    	
    	final Separator LineSeparator = new Separator();
    	LineSeparator.setOrientation(Orientation.HORIZONTAL);
    	final Label Version = new Label("Version "+gui_inizializer.Version);
    	Version.setPadding(new Insets(-10, 0, 0, 0));
    	Version.setTextFill(Color.GRAY);
    	
    	SeparatorPanel.getChildren().add(LineSeparator);
    	SeparatorPanel.getChildren().add(Version);
    	
    	HBox.setHgrow(LineSeparator, Priority.ALWAYS);
    	
    	return SeparatorPanel;
    }
    
    public void setRbiConstructor(File file) {
    	rbi_constructor = new rbi_info(file);
    }
    
    public void setState(String state) {
    	this.state = State.valueOf(state);
    }
    
    public rbi_info getRbiConstructor() {
    	return rbi_constructor;
    }
    
    public Scene getScene() {
		return scene;
	}
    
    public State getState() {
    	return state;
    }
    
}
