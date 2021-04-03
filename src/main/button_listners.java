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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javafx.collections.FXCollections;

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
    private final EventHandler<ActionEvent> checkBoardSelect;
    private EventHandler<ActionEvent> setManualOsck;

    button_listners(gui_construct scene) {
        this.Scene = scene;
        this.inputFile = (ActionEvent event) -> {
            FileChooser File_Choser = new FileChooser();
            FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("BLI/RBI Firmware", "*.rbi", "*.bli");

            File_Choser.getExtensionFilters().add(fileExtensions);

            File file = File_Choser.showOpenDialog(gui_inizializer.stage);
            if (file != null) {
                try {
                    LoadFile(file);
                } catch (IOException ex) {
                    Logger.getLogger(button_listners.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            event.consume();
        };

        this.checkBoardSelect = (ActionEvent event) -> {
            Scene.setState("IDLE");
            String selection = Scene.BoardSelect.getSelectionModel().getSelectedItem();
            selectBoard(selection);
            event.consume();
        };

        this.decryptFile = (ActionEvent event) -> {
            String model = Scene.BoardSelect.getSelectionModel().getSelectedItem();

            if (model != null) {
                //LoadFile(Scene.getRbiConstructor().getFile());

                if (Scene.getState() == gui_construct.State.LOADED) {
                    if (!Scene.OsKeysInput.getEditor().getText().equals("")) {
                        Scene.OsKeysInput.getItems().add(new os_key_couple("FFFF", Scene.OsKeysInput.getEditor().getText()));
                    }

                    for (os_key_couple keys : Scene.OsKeysInput.getItems()) {
                        byte[] osck = string_util.hexStringToByteArray(keys.Osck);
                        byte[] osik = string_util.hexStringToByteArray(keys.Osik);

                        try {
                            Scene.getRbiInfo().getReader().processPayload(osck, osik, Scene);
                            Scene.setState("STREAMING");

                            File outputFile = OutputFileDialog();
                            if (outputFile != null) {
                                Scene.getRbiInfo().getReader().saveFile(outputFile, Scene);
                                Scene.setState("IDLE");
                            }
                            break;
                        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                            //ShowErrorMessage("Error in decrypting file!");
                            Scene.log.appendText(e.getMessage().concat("\n"));
                        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | DataFormatException e) {
                            Scene.log.appendText(e.getMessage());
                        } catch (IOException e) {
                            ShowErrorMessage("Error accessing file!");
                            Scene.log.appendText(e.getMessage().concat("\n"));
                            break;
                        }
                    }
                }
                event.consume();

            } else {
                ShowErrorMessage("Select a board or choose Manual to manually provide keys!");
            }
        };
    }

    private void LoadFile(File file) throws IOException {
        Scene.log.setText(null);
        Scene.FileInputText.setText(file.toString());
        Scene.newRbiInfo(file).getReader().openFile();
        
        Scene.log.appendText("BLI file opened\n");

        Scene.updateHeaderSubPanel();
        Scene.log.appendText(String.format("Encrypted data starts at 0x%x\n",
                Integer.parseInt(Scene.getRbiInfo().getHeaderTable().get("data_offset")) + 1));

        String boardname = Scene.getRbiInfo().getHeaderTable().get("boardname");
        if (board.getMap().containsKey(boardname)) {
            board b = board.getByMnemonic(boardname);
            Scene.BoardSelect.getSelectionModel().select(boardname);
            Scene.log.appendText("Detected board name: " + boardname + "\nKnown as: " + b.getFriendly() + "\n");
        } else {
            Scene.BoardSelect.getSelectionModel().select("Manual");
        }
        Scene.decryptFile.setDisable(false);
        Scene.setState("LOADED");
    }

    private void selectBoard(String model) {
        if ("Manual".equals(model)) {
            Scene.OsKeysInputPanel.setVisible(true);
            Scene.OsKeysInfotext.setVisible(true);
            Scene.OsKeysInput.setItems(FXCollections.observableArrayList());
            Scene.OsKeysInput.setEditable(true);
            Scene.OsKeysInput.getEditor().selectAll();
            Scene.ModelLabel.setText("");
        } else {
            board b = board.getByMnemonic(model);
            Scene.ModelLabel.setText(b.getFriendly());
            if (b.getAllOsKeys().size() > 0) {
                Scene.OsKeysInputPanel.setVisible(true);
                Scene.OsKeysInfotext.setVisible(false);
                Scene.OsKeysInput.setEditable(false);
                Scene.OsKeysInput.setItems(b.getAllOsKeys());
                Scene.OsKeysInput.getSelectionModel().selectFirst();
            } else {
                Scene.OsKeysInputPanel.setVisible(false);
            }
        }
    }

    private File OutputFileDialog() {
        FileChooser File_Choser = new FileChooser();
        File_Choser.setInitialDirectory(Scene.getRbiInfo().getFile().getParentFile());
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Firmware Bank file", "*.bin");
        File_Choser.getExtensionFilters().add(fileExtensions);
        File_Choser.setInitialFileName(Scene.getRbiInfo().getFile().getName().replaceFirst(".rbi", ""));
        File file = File_Choser.showSaveDialog(gui_inizializer.stage);

        return file;
    }

    private void ShowErrorMessage(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }

    public EventHandler<ActionEvent> getInputFileEventHandler() {
        return inputFile;
    }

    public EventHandler<ActionEvent> getDecryptFileEventHandler() {
        return decryptFile;
    }

    public EventHandler<ActionEvent> getCheckBoardSelectHandler() {
        return checkBoardSelect;
    }

    public EventHandler<ActionEvent> getManualOsckHandler() {
        return setManualOsck;
    }
}
