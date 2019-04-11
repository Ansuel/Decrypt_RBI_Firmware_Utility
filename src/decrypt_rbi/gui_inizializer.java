package decrypt_rbi;

import decrypt_rbi.gui_construct;
import decrypt_rbi.gui_inizializer;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class gui_inizializer extends Application {

	static Stage stage;
	
    @Override
    public void start(Stage stage) {
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

}