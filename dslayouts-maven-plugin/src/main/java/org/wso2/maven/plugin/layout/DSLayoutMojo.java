/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.maven.plugin.layout;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.wso2.maven.dashboards.FileManagementUtils;

import java.io.File;

@Mojo(name = "buildDSLayout")
public class DSLayoutMojo extends FileManagementUtils {

    private static String LAYOUT_CONTENT_DIR = "layout";
    @Parameter(defaultValue = "${project.basedir}")
    private File path;
    @Parameter(defaultValue = "zip")
    private String type;
    @Parameter(defaultValue = "${project}")
    private MavenProject mavenProject;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File project = path;
        File layoutContentDir = new File(project, LAYOUT_CONTENT_DIR);
        processMavenProject(layoutContentDir, type, mavenProject, path);
    }
}
