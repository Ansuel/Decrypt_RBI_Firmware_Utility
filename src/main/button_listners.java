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
import javafx.stage.FileChooser;

public class button_listners {
	
	private gui_construct Scene;
	
	private final EventHandler<ActionEvent> inputFile;
	private final EventHandler<ActionEvent> decryptFile;
	private final EventHandler<ActionEvent> checkOsckSelect;
	private EventHandler<ActionEvent> setManualOsck;
	
	button_listners(gui_construct scene) {
		this.Scene = scene;
		this.inputFile = (ActionEvent event) -> {
            FileChooser File_Choser = new FileChooser();
            FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Rbi Firmware", "*.rbi");
            
            File_Choser.getExtensionFilters().add(fileExtensions);
            
            File file = File_Choser.showOpenDialog(gui_inizializer.stage);
            if (file != null) {
                LoadFile(file);
            }
            
            event.consume();
        };

		this.checkOsckSelect = (ActionEvent event) -> {
            Scene.setState("IDLE");
            String selection = Scene.OsckSelect.getSelectionModel().getSelectedItem();
            selectBoard(selection);
            event.consume();
        };
		
		this.decryptFile = (ActionEvent event) -> {
            ByteArrayOutputStream outputStream = Scene.getRbiConstructor().getOutputStream();
            String model = Scene.OsckSelect.getSelectionModel().getSelectedItem();
            
            if ( model != null ) {
                byte[] osck = string_util.hexStringToByteArray(Scene.OsckInput.getText());
                byte[] osik = null;
                
                Scene.getRbiConstructor().RegenerateOutputStream();
                LoadFile(Scene.getRbiConstructor().getFile());
                
                if ( Scene.getState() == gui_construct.State.LOADED ) {
                    try {
                        file_util.processFile(outputStream, osck, osik, Scene);
                        Scene.setState("DECRYPTED");
                    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        ShowErrorMessage("Error in decrypting file!");
                        Scene.log.appendText(e.getMessage());
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | DataFormatException e) {
                        Scene.log.appendText(e.getMessage());
                    } catch (IOException e) {
                        ShowErrorMessage("Error in loading file!");
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
                ShowErrorMessage("Select a model or select Manual to manually enter keys!");
            }
        };
	}
	
	private void LoadFile(File file) {
		Scene.log.setText(null);
		Scene.FileInputText.setText(file.toString());
		Scene.setRbiConstructor(file);
		ProcessHeader();
		
		String boardname = Scene.getRbiConstructor().getHeaderTable().get("boardname");
		if ( board.getMap().containsKey(boardname) ) {
			board b = board.getByMnemonic(boardname);
			Scene.OsckSelect.getSelectionModel().select(boardname);
			Scene.log.appendText("Detected board name: " + boardname + "\nKnown as: " + b.getFriendly() + "\n");
		} else {
			Scene.OsckSelect.getSelectionModel().select("Manual");
		}
		Scene.decryptFile.setDisable(false);
		Scene.setState("LOADED");
	}
	
	private void ProcessHeader() {
		File file = Scene.getRbiConstructor().getFile();
		ByteArrayOutputStream outputStream = Scene.getRbiConstructor().getOutputStream();
		Map<String,String> header_table = Scene.getRbiConstructor().getHeaderTable();
		try {
			InizializeInfoFile(file, outputStream, header_table);
			Scene.log.appendText("File loaded!\n");
			Scene.updateHeaderSubPanel();
			Scene.log.appendText(String.format("Encrypted data starts at 0x%x\n", Integer.parseInt(header_table.get("data_offset")) + 1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void selectBoard(String model) {
		if ( "Manual".equals(model) ) {
			Scene.OsckInputPanel.setVisible(true);
			Scene.OsckManualinfotext.setVisible(true);
			Scene.OsckInput.setEditable(true);
            Scene.OsckInput.selectAll();
			Scene.ModelLabel.setText("");
		} else {
			board b = board.getByMnemonic(model);
			Scene.ModelLabel.setText(b.getFriendly());
			if ( b.getOsck() != null ) {
				Scene.OsckInputPanel.setVisible(true);
                Scene.OsckManualinfotext.setVisible(false);
				Scene.OsckInput.setEditable(false);
				Scene.OsckInput.setText(b.getOsck());
			} else {
				Scene.OsckInputPanel.setVisible(false);
			}
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
	
	public EventHandler<ActionEvent> getcheckOsckSelectHandler() {
		return checkOsckSelect;
	}
	
	public EventHandler<ActionEvent> getManualOsckHandler() {
		return setManualOsck;
	}
}
