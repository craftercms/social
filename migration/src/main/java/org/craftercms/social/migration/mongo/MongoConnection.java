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

package org.craftercms.social.migration.mongo;

import com.mongodb.MongoClient;

import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.craftercms.social.migration.util.MigrationException;
import org.craftercms.social.migration.util.MigrationMessenger;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class MongoConnection {

    private final MongoClient sourceClient;
    private final MongoClient destinationClient;
    private  Jongo source;
    private  Jongo destination;
    private static MongoConnection instance;
    private Logger log = LoggerFactory.getLogger(MongoConnection.class);

    private MongoConnection(final MongoClient source, final String srcDb, final MongoClient destination, final String
        dtsDb) {
        this.source = new Jongo(source.getDB(srcDb));
        this.destination =  new Jongo(destination.getDB(dtsDb));
        this.sourceClient=source;
        this.destinationClient=destination;
    }

    public static MongoConnection getInstance(){
        if (instance==null){
            throw new IllegalStateException("Mongo connection has not been initialize ");
        }
        return instance;
    }

    public static void init(final String srcHost, final String srcPort, final String srcDb, final String dstHost,
                            final String dtsPort, final String dtsDb) throws MigrationException {
        LoggerFactory.getLogger(MongoConnection.class).info("Starting DB connection");
        if (instance == null) {

            int iSrcPort = NumberUtils.toInt(srcPort);
            if (iSrcPort == 0) {

                throw new MigrationException("Source Port is not valid",true);
            }
            int iDstPort = NumberUtils.toInt(dtsPort);
            if (iDstPort == 0) {

                throw new MigrationException("Destination Port is not valid",true);
            }
            if (StringUtils.isBlank(srcHost)) {

                throw new MigrationException("Source Host is not valid",true);
            }
            if (StringUtils.isBlank(dstHost)) {

                throw new MigrationException("Destination Host is not valid",true);
            }

            if (StringUtils.isBlank(srcDb)) {
                throw new MigrationException("Source Db is not valid",true);
            }
            if (StringUtils.isBlank(dtsDb)) {
                throw new MigrationException("Destination Db is not valid",true);
            }

            try {
                instance = new MongoConnection(new MongoClient(srcHost, iSrcPort),srcDb,new MongoClient(dstHost,
                    iDstPort),dtsDb);
            } catch (UnknownHostException e) {
                throw new MigrationException(e, true);
            }
        }
    }

    public Jongo getSource() {
        return source;
    }

    public Jongo getDestination() {
        return destination;
    }

    public void close() {

    }
}
