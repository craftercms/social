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
import java.util.List;



import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import javafx.scene.control.TableView;
import org.craftercms.social.migration.migrators.MigrationPipe;
import org.craftercms.social.migration.mongo.MongoConnection;
import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MigrationPipeService extends Task<Object> {
    private TableView logTable;
    private ProgressBar progressBar;
    private List<File> scriptsToRun;
    private Logger log = LoggerFactory.getLogger(MigrationPipeService.class);
    private String srcHost;
    private String srcPort;
    private String srcDb;
    private String dstHost;
    private String dstPort;
    private String dstDb;
    private MigrationMessenger messenger;

    public MigrationPipeService(final TableView logTable, final ProgressBar progressBar, final String srcHost, final
    String srcPort, final String srcDb, final String dstHost, final String dstPort, final String dstDb, List<File>
        scriptsToRun) {
        this.logTable = logTable;
        this.progressBar = progressBar;
        this.srcHost = srcHost;
        this.srcPort = srcPort;
        this.srcDb = srcDb;
        this.dstHost = dstHost;
        this.dstPort = dstPort;
        this.dstDb = dstDb;
        this.scriptsToRun = scriptsToRun;
        messenger=new MigrationMessenger(logTable,progressBar);
    }


    @Override
    protected Object call() throws Exception {
        try {
            messenger.clear();
            try {
                MongoConnection.getInstance();
                log.info("DB already init");
            } catch (IllegalStateException ex) {
                MongoConnection.init(srcHost, srcPort, srcDb, dstHost, dstPort, dstDb);
            }
            final MigrationPipe pipe = new MigrationPipe(scriptsToRun, messenger);
            pipe.start();
        } catch (MigrationException e) {
            messenger.log(MigrationMessenger.Level.ERROR, "Unable to Continue with migration " + e.toString(),
                "Migration Task");
            log.error("Unable to continue with Migration ", e);
        } catch (Throwable ex) {
            log.error("Unable to execute Migration Service ", ex);
        }
        MongoConnection.getInstance().close();
        return null;
    }
}



