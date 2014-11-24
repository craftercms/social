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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.transform.TransformerException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.io.IOUtils;
import org.craftercms.social.migration.MigrationTool;
import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.UserLogEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class MainController implements Initializable {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private VBox main;

    @FXML
    private MenuItem saveLog;
    @FXML
    private TableView logTable;

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
    @FXML
    private MenuItem ctxClearLog;
    @FXML
    private MenuItem ctxReloadProfileScrp;
    @FXML
    private MenuItem ctxReloadSocialScrp;

    @FXML
    private MenuItem ctxClearSocialSelection;

    @FXML
    private MenuItem ctxClearProfileSelection;
    @FXML
    private ProgressBar pgbTaskProgress;



    private MigrationPipeService currentTask;

    private Scene scene;


    private boolean inProgress = false;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        configTable();
        mnuQuit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                stopTasks();
                Platform.exit();
            }
        });
        ctxClearLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                logTable.getItems().clear();
            }
        });
        ctxClearProfileSelection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
               lstProfileScripts.getSelectionModel().clearSelection();
            }
        });
        ctxClearSocialSelection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                lstSocialScripts.getSelectionModel().clearSelection();
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
        ctxReloadProfileScrp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                lstProfileScripts.getItems().clear();
                try {
                    extractBuildInScripts("profile", lstProfileScripts);
                } catch (MigrationException e) {
                    log.error("Unable to extract BuildIn scripts");
                }
                loadScripts(MigrationTool.systemProperties.getString("crafter.migration.profile.scripts"),
                    lstProfileScripts);
            }
        });
        ctxReloadSocialScrp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                lstSocialScripts.getItems().clear();
                try {
                    extractBuildInScripts("profile", lstSocialScripts);
                } catch (MigrationException e) {
                    log.error("Unable to extract BuildIn scripts");
                }
                loadScripts(MigrationTool.systemProperties.getString("crafter.migration.social.scripts"),
                    lstSocialScripts);
            }
        });
        final MigrationSelectionAction selectionEventHandler = new MigrationSelectionAction();
        rbtMigrateProfile.setOnAction(selectionEventHandler);
        rbtMigrateSocial.setOnAction(selectionEventHandler);
        loadScripts();
        loadDefaultValues();
        saveLog.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
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
                    getHtml(new FileWriter(savedFile));
                    log.info("Saved Html log file");
                } catch (IOException | TransformerException ex) {
                    log.error("Unable to save file", ex);
                }
            }
        });
        mnuStart.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        mnuStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {

                if (currentTask == null || !currentTask.isRunning()) {
                    ObservableList scriptsToRun;
                    if (rbtMigrateProfile.isSelected()) {
                        scriptsToRun = lstProfileScripts.getSelectionModel().getSelectedItems();
                    } else {
                        scriptsToRun = lstSocialScripts.getSelectionModel().getSelectedItems();
                    }
                    currentTask = new MigrationPipeService(logTable, pgbTaskProgress, srcHost.getText(), srcPort
                        .getText(), srcDb.getText(), dstHost.getText(), dstPort.getText(), dstDb.getText(),
                        scriptsToRun);
                }
                if (!currentTask.isRunning()) {
                    final Thread t = new Thread(currentTask, "Migration Task");
                    t.start();
                }
            }
        });
    }

    public void stopTasks() {
        if (currentTask != null && currentTask.isRunning()) {
            currentTask.cancel();
        }
    }

    private void configTable() {

        TableColumn dateCol = new TableColumn("Date");
        TableColumn messageCol = new TableColumn("Message");
        TableColumn sourceCol = new TableColumn("Source");
        dateCol.setMaxWidth(200);
        dateCol.setPrefWidth(200);
        dateCol.setCellValueFactory(new PropertyValueFactory<UserLogEntry, String>("date"));
        messageCol.setMaxWidth(675);
        messageCol.setPrefWidth(675);
        messageCol.setCellValueFactory(new PropertyValueFactory<UserLogEntry, String>("message"));
        messageCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sourceCol.setMaxWidth(150);
        sourceCol.setPrefWidth(150);
        sourceCol.setCellValueFactory(new PropertyValueFactory<UserLogEntry, String>("source"));
        logTable.getColumns().addAll(dateCol, messageCol, sourceCol);

    }

    private void loadScripts() {
        try {
            extractBuildInScripts("profile", lstProfileScripts);
            extractBuildInScripts("social", lstSocialScripts);
        } catch (MigrationException ex) {
            log.error("Unable to extract Migration Scripts", ex);
        }
        loadScripts(MigrationTool.systemProperties.getString("crafter.migration.profile.scripts"), lstProfileScripts);
        loadScripts(MigrationTool.systemProperties.getString("crafter.migration.social.scripts"), lstSocialScripts);
    }

    private void loadBuildInScripts(final String application, final ListView listToAdd) {
        URL profileInternalScriptPath = getClass().getResource("/migration/scripts/" + application);
        if (profileInternalScriptPath != null) {
            loadScripts(profileInternalScriptPath.getFile(), listToAdd);
        } else {
            log.error("Unable to find build in profile scripts");
        }
    }

    protected void loadDefaultValues() {
        dstHost.setText(MigrationTool.systemProperties.getString("crafter.migration.defaultDstHost"));
        dstPort.setText(MigrationTool.systemProperties.getString("crafter.migration.defaultDstPort"));
        /** SRC **/
        srcHost.setText(MigrationTool.systemProperties.getString("crafter.migration.defaultSrcHost"));
        srcPort.setText(MigrationTool.systemProperties.getString("crafter.migration.defaultSrcPort"));
        new MigrationSelectionAction().handle(null);
    }

    protected void extractBuildInScripts(String application, ListView lstToAdd) throws MigrationException {
        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        List<String> list = new ArrayList<String>();
        byte[] buffer = new byte[1024];
        if (src != null) {
            try {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry ze = zip.getNextEntry();
                if (ze == null) {
                    //Running from IDE or Exploded Jar no need to extract!
                    log.debug("Loading files from FS ");
                    loadScripts(getClass().getResource("/" + application).getFile(), lstToAdd);
                } else {
                    while (ze != null) {
                        String entryName = ze.getName();
                        if (entryName.startsWith(application) && entryName.endsWith(".js")) {
                            log.debug("Extracting {} ", entryName);
                            final File extractFile = Paths.get(Paths.get(MigrationTool.systemProperties.getString
                                ("crafter" +
                                "" + ".migration.profile.scripts")).toFile().getParent(), entryName).toFile();
                            if (!extractFile.exists()) {
                                extractFile.createNewFile();
                                FileOutputStream fos = new FileOutputStream(extractFile);
                                int len;
                                while ((len = zip.read(buffer)) > 0) {
                                    fos.write(buffer, 0, len);
                                }
                                fos.close();

                            }
                        }
                        ze = zip.getNextEntry();
                    }
                }
            } catch (IOException ex) {
                log.debug("Unable to load build in scripts", ex);
            }
        } else {
            loadBuildInScripts(application, lstToAdd);
        }
    }

    protected void loadScripts(final String path, final ListView whereToAdd) {

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
            whereToAdd.getItems().addAll(scripts);
        } else {
            log.info("Property is not set using Default Scripts");
            return;
        }
    }


    private String loadTemplateHtml() {
        URL templateUrl = getClass().getResource(MigrationTool.systemProperties.getString("crafter.migration" + "" +
            ".loggerTemplate"));
        if (templateUrl != null) {
            try {
                final InputStream io = getClass().getResourceAsStream(MigrationTool.systemProperties.getString
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

    protected void getHtml(final FileWriter writer) throws TransformerException, IOException {
        final URL in = getClass().getResource(MigrationTool.systemProperties.getString("crafter" + ".migration" + ""
            + ".loggerTemplate"));
        if (in == null) {
            log.error("Unable to find {} " + MigrationTool.systemProperties.getString("crafter" + ".migration" + "" +
                ".loggerTemplate"));
        }
        final Document loggingDoc = Jsoup.parse(IOUtils.toString(in));
        final Element logs = loggingDoc.getElementById("logs");
        for (Object o : logTable.getItems()) {
            if (o instanceof UserLogEntry) {
                UserLogEntry userLogEntry = (UserLogEntry)o;
                String dateFormat = new SimpleDateFormat("yyyy MM dd hh:mm:ss zzz").format(userLogEntry.getDate());
                final Element tr = loggingDoc.createElement("tr");
                tr.attr("class", userLogEntry.getLevel().getCssClass());
                final Element tmigrator = loggingDoc.createElement("td");
                final Element tdate = loggingDoc.createElement("td");
                final Element tmessage = loggingDoc.createElement("td");
                tmessage.attr("class", "text-center");
                tmessage.text(userLogEntry.getMessage());
                tdate.text(dateFormat);
                tmigrator.text(userLogEntry.getSource());
                tr.appendChild(tmigrator);
                tr.appendChild(tdate);
                tr.appendChild(tmessage);
                logs.appendChild(tr);
            }
        }
        IOUtils.write(loggingDoc.toString(), writer);
        //        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        //        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        //        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        //        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        //        transformer.transform(new DOMSource(loggingDoc), new StreamResult(writer));
        writer.flush();
        writer.close();
    }

    public void setScene(final Scene scene) {
        this.scene = scene;
    }

    class MigrationSelectionAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(final ActionEvent actionEvent) {
            if (rbtMigrateProfile.isSelected()) {
                srcDb.setText(MigrationTool.systemProperties.getString("crafter.migration.profile.defaultSrcDb"));
            } else {
                srcDb.setText(MigrationTool.systemProperties.getString("crafter.migration.social.defaultSrcDb"));
            }
            if (rbtMigrateProfile.isSelected()) {
                dstDb.setText(MigrationTool.systemProperties.getString("crafter.migration.profile.defaultDstDb"));
            } else {
                dstDb.setText(MigrationTool.systemProperties.getString("crafter.migration.social.defaultDstDb"));
            }
        }
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
