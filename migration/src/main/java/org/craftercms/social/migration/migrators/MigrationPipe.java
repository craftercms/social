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
package org.craftercms.social.migration.migrators;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MigrationPipe {

    private List<File> scripts;
    private MigrationMessenger messenger;
    private ScriptingEngine scriptEngine;
    private Logger log = LoggerFactory.getLogger(MigrationPipe.class);


    public MigrationPipe(List<File> scriptToExec) {
        this.scripts = scriptToExec;
        messenger = MigrationMessenger.getInstance();
        scriptEngine = ScriptingEngine.getInstance();
    }

    public void start() throws MigrationException {
        if (scripts.isEmpty()) {
            log.info("No script where selected nothing to do");
        }
        for (File script : scripts) {
            try {
                messenger.log(MigrationMessenger.Level.TASK_START, "Start  " + script.getName() + "@ " + new Date(), script.getName());
                scriptEngine.eval(script);
                messenger.log(MigrationMessenger.Level.TASK_START, "Finish " + script.getName() + "@ " + new Date(), script.getName());
            }catch (MigrationException e) {
                if(e.isBlocker()){
                    throw e;
                }else{
                    messenger.log(MigrationMessenger.Level.WARNING,"Error executing "+script.getName()+"<br/>"+e
                        .toString(),script.getName());
                }
            }
        }
    }
}
