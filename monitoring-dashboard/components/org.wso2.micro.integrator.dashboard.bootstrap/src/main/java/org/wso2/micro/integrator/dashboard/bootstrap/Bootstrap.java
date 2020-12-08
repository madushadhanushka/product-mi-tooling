/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.micro.integrator.dashboard.bootstrap;

import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class starting up the jetty server on the port as defined in deployment.toml file.
 * React application serve in root context and all the war files serve in "/api" context.
 */
public class Bootstrap {
    private static final String CONF_DIR = "conf";
    private static final String DEPLOYMENT_TOML = "deployment.toml";
    private static final String TOML_CONF_PORT = "server_config.port";
    private static final String SERVER_DIR = "server";
    private static final String WEBAPPS_DIR = "webapps";
    private static final String WWW_DIR = "www";

    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        String dashboardHome = System.getenv("DASHBOARD_HOME");
        int serverPort = 9743;
        String tomlFile = dashboardHome + File.separator + CONF_DIR + DEPLOYMENT_TOML;
        try {
            TomlParseResult parseResult = Toml.parse(Paths.get(tomlFile));
            serverPort = Integer.parseInt(parseResult.getString(TOML_CONF_PORT));
        } catch (IOException e) {
            logger.warn("Error while reading TOML file in " + tomlFile + ". Using default port " + serverPort, e);
        }
        Server server = new Server(serverPort);
        HandlerCollection handlers = new HandlerCollection();
        String webAppsPath = dashboardHome + File.separator + SERVER_DIR + File.separator + WEBAPPS_DIR;
        File f = new File(webAppsPath);
        String[] pathnames = f.list();
        for (String pathname : pathnames) {
            WebAppContext webApp = new WebAppContext();
            webApp.setContextPath("/api/");
            File warFile = new File(webAppsPath + File.separator + pathname);
            webApp.setExtractWAR(true);
            webApp.setWar(warFile.getAbsolutePath());
            handlers.addHandler(webApp);
        }
        WebAppContext wwwApp = new WebAppContext();
        wwwApp.setContextPath("/");
        wwwApp.setResourceBase(dashboardHome + File.separator + SERVER_DIR + File.separator + WWW_DIR);
        wwwApp.setParentLoaderPriority(true);
        handlers.addHandler(wwwApp);
        server.setHandler(handlers);
        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            logger.error("Error while starting up the server", ex);
        }

        logger.info("Stopping the server");
    }
}
