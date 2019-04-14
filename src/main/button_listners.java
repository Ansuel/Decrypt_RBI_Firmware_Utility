package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.zip.DataFormatException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import main.gui_construct;
import main.gui_inizializer;

public class button_listners {
	
	private gui_construct Scene;
	
	private EventHandler<ActionEvent> inputFile;
	private EventHandler<ActionEvent> decryptFile;
	private EventHandler<ActionEvent> checkOsckButton;
	
	button_listners(gui_construct scene) {
		this.Scene = scene;
		this.inputFile = new EventHandler<ActionEvent>() {
			@Override
	    	public void handle(ActionEvent event) {
	    		FileChooser File_Choser = new FileChooser();
	    		FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Rbi Firmware", "*.rbi");

	    		File_Choser.getExtensionFilters().add(fileExtensions);
	    		
				File file = File_Choser.showOpenDialog(gui_inizializer.stage);
				if (file != null) {
					LoadFile(file);
				}
				
				event.consume();
	    	}
		};

		this.checkOsckButton = new EventHandler<ActionEvent>() {
			@Override
	    	public void handle(ActionEvent event) {
				Scene.setState("IDLE");
				RadioButton selectedRadioButton = (RadioButton) Scene.OsckGroup.getSelectedToggle();
				String model = selectedRadioButton.getText();
				SelectOsckFromName(model);
				event.consume();
	    	}
		};
		
		this.decryptFile = new EventHandler<ActionEvent>() {
			@Override
	    	public void handle(ActionEvent event) {
				ByteArrayOutputStream outputStream = Scene.getRbiConstructor().getOutputStream();
				RadioButton selectedRadioButton = (RadioButton) Scene.OsckGroup.getSelectedToggle();
				
				if ( selectedRadioButton != null ) {
					byte[] osck;
		    		if ( selectedRadioButton.getText()  == "Manual" ) 
		    			osck = string_util.toByteArray(Scene.OsckInput.getText());
		    		else
		    			osck = Scene.getOsck();
		    		
		    		if ( Scene.getState() == gui_construct.State.IDLE ) {
		    			Scene.getRbiConstructor().RegenerateOutputStream();
		    			LoadFile(Scene.getRbiConstructor().getFile());
		    		}
		    		
		    		if ( Scene.getState() == gui_construct.State.LOADED ) {
						try {
							file_util.processFile(outputStream, osck,Scene);
							Scene.setState("DECRYPTED");
						} catch (InvalidKeyException e) {
							ShowErrorMessage("Error in decrypting file! Wrong osck key provided ?");
							Scene.log.appendText(e.getMessage());
						} catch (NoSuchAlgorithmException e) {
							Scene.log.appendText(e.getMessage());
						} catch (NoSuchPaddingException e) {
							Scene.log.appendText(e.getMessage());
						} catch (InvalidAlgorithmParameterException e) {
							Scene.log.appendText(e.getMessage());
						} catch (IllegalBlockSizeException e) {
							ShowErrorMessage("Error in decrypting file!");
							Scene.log.appendText(e.getMessage());
						} catch (BadPaddingException e) {
							ShowErrorMessage("Error in decrypting file!");
							Scene.log.appendText(e.getMessage());
						} catch (IOException e) {
							ShowErrorMessage("Error in loading file!");
							Scene.log.appendText(e.getMessage());
						} catch (DataFormatException e) {
							Scene.log.appendText(e.getMessage());
						}
		    		}
		    		if ( Scene.getState() == gui_construct.State.DECRYPTED ) {
						File outputFile = OutputFileDialog();
						try {
							if (outputFile != null) {
								file_util.saveFile(outputStream, outputFile,Scene);
								Scene.setState("IDLE");
							}
						} catch (IOException e) {
							ShowErrorMessage("Error in saving file!");
							Scene.log.appendText(e.getMessage());
						}
		    		}
					event.consume();
		    	} else {
					ShowErrorMessage("Select a model or select Manual to enter a manual OSCK key!");
				}
			} 
		};
	}
	
	private void LoadFile(File file) {
		Scene.log.setText(null);
		Scene.FileInputText.setText(file.toString());
		Scene.setRbiConstructor(file);
		ProcessHeader();
		
		String boardname = Scene.getRbiConstructor().getHeaderTable().get("boardname");
		if ( osck_table.boardname_map.containsKey(boardname) ) {
			String model = osck_table.boardname_map.get(boardname);
			Scene.BoardButton.get(model).setSelected(true);
			SelectOsckFromName(model);
			Scene.log.appendText("AutoDetected model: "+model+"\n");
		}
		Scene.decryptFile.setDisable(false);
		Scene.setState("LOADED");
	}
	
	private void ProcessHeader() {
		File file = Scene.getRbiConstructor().getFile();
		ByteArrayOutputStream outputStream = Scene.getRbiConstructor().getOutputStream();
		Map<String,String> header_table = Scene.getRbiConstructor().getHeaderTable();
		try {
			InizializeInfoFile(file,outputStream,header_table);
			Scene.log.appendText("File loaded!\n");
			Scene.updateHeaderSubPanel();
			Scene.log.appendText("Encrypted data starts at 0x"+(Integer.parseInt(header_table.get("data_offset"))+1)+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void SelectOsckFromName(String model) {
		if ( model  == "Manual" ) {
			Scene.OsckInputPanel.setVisible(true);
			Scene.OsckManualinfotext.setVisible(true);
			Scene.OsckInput.setDisable(false);
			Scene.OsckInput.setText(null);
		} else {
			Scene.OsckManualinfotext.setVisible(false);
			Scene.OsckInputPanel.setVisible(true);
			Scene.OsckInput.setDisable(true);
			String osck = osck_table.osck_table.get(model);
			Scene.OsckInput.setText(osck);
			Scene.setOsck(string_util.toByteArray(osck));
		}
	}
	
	private File OutputFileDialog() {
		FileChooser File_Choser = new FileChooser();
		File_Choser.setInitialDirectory(Scene.getRbiConstructor().getFile().getParentFile());
		FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Firmware file", "*.bin");
		File_Choser.getExtensionFilters().add(fileExtensions);
		File_Choser.setInitialFileName(Scene.getRbiConstructor().getFile().getName().replaceFirst(".rbi", ""));
		File file = File_Choser.showSaveDialog(gui_inizializer.stage);
		
		return file;
	}
	
	private void ShowErrorMessage(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(error);
		alert.showAndWait();
	}
	
	private void InizializeInfoFile(File file,ByteArrayOutputStream outputStream,Map<String,String> header_table) throws IOException {
		file_util.loadFile(file, outputStream,header_table);
	}
	
	public EventHandler<ActionEvent> getInputFileEventHandler() {
		return inputFile;
	}
	
	public EventHandler<ActionEvent> getDecryptFileEventHandler() {
		return decryptFile;
	}
	
	public EventHandler<ActionEvent> getcheckOsckButtonEventHandler() {
		return checkOsckButton;
	}
}
