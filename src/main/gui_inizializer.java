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

import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

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
        stage.setTitle(titolo);
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
        stage.setScene(new gui_construct().getScene());

        //set Stage boundaries to visible bounds of the main screen
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //stage.setX(primaryScreenBounds.getMinX());
        //stage.setY(primaryScreenBounds.getMinY());
        stage.setMaxWidth(primaryScreenBounds.getWidth());
        stage.setMaxHeight(primaryScreenBounds.getHeight());
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
