package main;

import java.io.IOException;
import java.security.Security;

import javafx.application.Application;
import main.gui_inizializer;

public class Main {

	public static void main(String[] args) throws IOException  {
		Security.setProperty("crypto.policy", "unlimited");
		Application.launch(gui_inizializer.class,args);
		
	}

}
