package main;

import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.gui_construct;
import main.gui_inizializer;

public class gui_inizializer extends Application {

	static Stage stage;
	public static String Version;
	
    @Override
    public void start(Stage stage) throws IOException {
    	setVersion(getProperties());
    	setUserAgentStylesheet(STYLESHEET_MODENA);
    	gui_inizializer.stage = stage;
    	initUI(stage);
    }
    
    private void initUI(Stage stage) {
    	final String titolo = "Technicolor Rbi Frimware decrypt utiliy";
    	stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
    	
    	stage.setMinWidth(800);
    	stage.setMinHeight(880);
        stage.setTitle(titolo);
        stage.setScene(new gui_construct().getScene());
        stage.show();
    }
    
    private Properties getProperties() throws IOException {
    	
    	Properties properties = new Properties();
    	properties.load(Main.class.getResourceAsStream("/project.properties"));
    	
		return properties;
    	
    }
    
    private void setVersion(Properties properties) {
    	Version = properties.getProperty("version");
    }

}