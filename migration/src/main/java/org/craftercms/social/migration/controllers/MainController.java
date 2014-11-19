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

package org.craftercms.social.migration.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.io.IOUtils;
import org.craftercms.social.migration.MigrationTool;
import org.craftercms.social.migration.migrators.MigrationPipe;
import org.craftercms.social.migration.mongo.MongoConnection;
import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.craftercms.social.migration.util.OverrideEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 */
public class MainController implements Initializable {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private MenuItem saveLog;
    @FXML
    private WebView logView;

    @FXML
    private MenuItem mnuStart;

    @FXML
    private TextField dstHost;
    @FXML
    private TextField dstPort;
    @FXML
    private TextField dstDb;

    @FXML
    private TextField srcHost;
    @FXML
    private TextField srcPort;
    @FXML
    private TextField srcDb;
    @FXML
    private RadioButton rbtMigrateProfile;
    @FXML
    private RadioButton rbtMigrateSocial;
    @FXML
    private MenuItem mnuQuit;
    @FXML
    private ListView lstProfileScripts;
    @FXML
    private ListView lstSocialScripts;

    private Scene scene;


    private boolean inProgress = false;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        logView.getEngine().loadContent(loadTemplateHtml(), "text/html");
        logView.setEventDispatcher(new OverrideEventDispatcher(logView.getEventDispatcher()));
        mnuQuit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                Platform.exit();
            }
        });
        lstProfileScripts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lstSocialScripts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lstProfileScripts.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(final ListView listView) {
                return new FileListCell();
            }
        });
        lstSocialScripts.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(final ListView listView) {
                return new FileListCell();
            }
        });

        loadScripts();
        saveLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Migration Log");
                fileChooser.setInitialFileName("Crafter-Migration-" + new SimpleDateFormat("yyyy-MM-dd@HH_mm").format
                    (new Date()) + ".html");
                final File savedFile = fileChooser.showSaveDialog(scene.getWindow());
                if (savedFile == null) {
                    return;
                }
                try {
                    getHtml(logView.getEngine().getDocument(), new FileWriter(savedFile));
                    MigrationMessenger.getInstance().log(MigrationMessenger.Level.TASK_END, "Saved Log File", "");
                } catch (IOException | TransformerException ex) {
                    log.error("Unable to save file", ex);
                }
            }
        });
        mnuStart.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        mnuStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                if (!inProgress) {
                    try {
                        MigrationMessenger.getInstance().clear();
                        inProgress = true;
                        MigrationMessenger.getInstance().log(MigrationMessenger.Level.TASK_START, "Starting " +
                            "Migration" + " " + "of " + (rbtMigrateProfile.isSelected()? " Profile": "Social"), " " +
                            "System");
                        MongoConnection.init(srcHost.getText(), srcPort.getText(), srcDb.getText(), dstHost.getText()
                            , dstPort.getText(), dstDb.getText());
                        MigrationPipe pipe;
                        if (rbtMigrateProfile.isSelected()) {
                            pipe = new MigrationPipe(lstProfileScripts.getSelectionModel().getSelectedItems());
                        } else {
                            pipe = new MigrationPipe(lstSocialScripts.getSelectionModel().getSelectedItems());
                        }
                        pipe.start();
                    } catch (MigrationException e) {
                        MigrationMessenger.getInstance().log(MigrationMessenger.Level.ERROR, e.getMessage(),
                            "Configuration");

                    }
                    inProgress = false;
                } else {
                    MigrationMessenger.getInstance().log(MigrationMessenger.Level.WARNING, "Migration Process " +
                        "already" + " " + "running", "");
                }
            }
        });
    }

    private void loadScripts() {
        internalLoadScripts(MigrationTool.systemProperties.getProperty("crafter.migration.profile.scripts"),
            lstProfileScripts);
        internalLoadScripts(MigrationTool.systemProperties.getProperty("crafter.migration.social.scripts"),
            lstSocialScripts);
    }

    protected void internalLoadScripts(final String path, final ListView whereToAdd) {

        log.info("Loading migration Scripts from {}", path);
        if (path != null) {
            File profileScriptPath = new File(path);
            if (!profileScriptPath.isDirectory()) {
                log.error("Profile Loading Path {} is not a directory");
                return;
            }
            final File[] scripts = profileScriptPath.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.endsWith(".js");
                }
            });
            whereToAdd.setItems(FXCollections.observableArrayList(scripts));
        } else {
            log.info("Property is not set using Default Scripts");
            return;
        }
    }


    private String loadTemplateHtml() {
        URL templateUrl = getClass().getResource(MigrationTool.systemProperties.getProperty("crafter.migration" + ""
            + ".loggerTemplate"));
        if (templateUrl != null) {
            try {
                final InputStream io = getClass().getResourceAsStream(MigrationTool.systemProperties.getProperty
                    ("crafter.migration" + "" + ".loggerTemplate"));
                if (io == null) {
                    return "<h1>Logging template can't be loaded</h1><br/> ";
                }
                return IOUtils.toString(io, "UTF-8");
            } catch (IOException e) {
                return "<h1>Logging template can't be loaded</h1><br/> " + e.toString();
            }
        }
        return "<h1>Logging template can't be loaded</h1>";
    }

    protected void getHtml(final Document document, final FileWriter writer) throws TransformerException, IOException {

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        writer.flush();
        writer.close();

    }

    public void setScene(final Scene scene) {
        this.scene = scene;
    }

    public WebView getLogView() {
        return logView;
    }

    class FileListCell extends ListCell<File> {
        @Override
        protected void updateItem(final File file, final boolean b) {
            super.updateItem(file, b);
            if (file != null) {
                setText(file.getName());
            }
        }
    }
}
