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

package org.craftercms.social.migration.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class MigrationMessenger {

    private final ProgressBar progressBar;
    private Logger log = LoggerFactory.getLogger(MigrationMessenger.class);
    private TableView logTable;

    public enum Level {
        INFO(""), WARNING("warning"), ERROR("danger"), TASK_START("info"), TASK_END("success");
        private String cssClass;

        Level(final String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }
    }

    public MigrationMessenger(final TableView logTable,final ProgressBar progressBar) {
        this.logTable = logTable;
        this.progressBar=progressBar;
    }

    public void log(final String level, final String message,final String msgSource) {
        log(Level.valueOf(level), message,msgSource);
    }

    public void clear() {
        logTable.getItems().clear();
    }

    public void log(final Level level, final String message, final String msgSource) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                logTable.getItems().add(0, new UserLogEntry(level, msgSource, message, new Date()));
            }
        });
    }

    public void resetProgress(){
        progressBar.setProgress(0);
    }
    public void setProgress(double progress){
        progressBar.setProgress(progress);
    }
}