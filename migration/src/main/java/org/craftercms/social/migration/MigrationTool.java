/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.migration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.craftercms.social.migration.controllers.MainController;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class MigrationTool extends Application {

    private static Logger log = LoggerFactory.getLogger(MigrationTool.class);
    public static final File propertiesHome = Paths.get(System.getProperty("user.home"), ".crafter",
        "socialProfileMigration.properties").toFile();
    public static PropertiesConfiguration systemProperties;


    public static void main(String[] args) throws IOException {
        //debug();
        Application.launch(MigrationTool.class, args);

    }


    @Override
    public void start(final Stage primaryStage) throws Exception {
        loadProperties();
        log.debug("Loading Fxml file");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/Main.fxml"));
        log.debug("Fxml file Loaded, Starting scene");
        Scene scene = new Scene((VBox)loader.load());
        final MainController controller = loader.getController();
        controller.setScene(scene);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent windowEvent) {
                controller.stopTasks();
            }
        });
        primaryStage.setTitle("Crafter Social/Profile Migration Tool (2.3->2.5)");
        log.debug("Showing UI. {}", new Date());
        primaryStage.show();

    }

    private static void loadProperties() throws ConfigurationException, FileNotFoundException {

        systemProperties = new PropertiesConfiguration(MigrationTool.class.getResource("/migration.properties"));

        if (propertiesHome.exists()) {
            systemProperties.load(new FileReader(propertiesHome));
        }
        try {
            if (!propertiesHome.exists()) {
                propertiesHome.getParentFile().mkdirs();
                propertiesHome.createNewFile();
            }
            File profileScripts = new File(systemProperties.getString("crafter.migration.profile.scripts"));
            if (!profileScripts.exists()) {
                profileScripts.mkdirs();
            }
            File socialScript = new File(systemProperties.getString("crafter.migration.social.scripts"));
            if (!socialScript.exists()) {
                socialScript.mkdirs();
            }
        } catch (IOException e) {
            log.error("Unable to load properties from " + propertiesHome.getAbsolutePath());
        }

    }
}
