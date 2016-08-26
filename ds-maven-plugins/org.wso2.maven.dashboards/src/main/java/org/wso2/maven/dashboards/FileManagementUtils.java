package org.wso2.maven.dashboards;/*
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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class FileManagementUtils extends AbstractMojo {

    private static final Logger logger = Logger.getLogger(FileManagementUtils.class);

    private static final String ERROR_CREATING_TEMP_DIRECTORY = "Error creating temporary directory - ";
    private static final String ERROR_CREATING_ZIP_DIRECTORY = "Error creating zip directory - ";
    private static final String ERROR_CREATING_ZIP_FILE = "Error creating zip file - ";
    private static String SEPERATOR = "-";
    private static String FILE_TYPE_SEPERATOR = ".";
    private static final String IN = "in";
    private static final String TARGET = "target";
    private static final String DSGADGET_TEMP = "temp";
    private static final String PROJECT_EXTENTION = ".project";

    public static void processMavenProject(File project, String artifactType, MavenProject mavenProject, File path)
            throws MojoExecutionException {
        try {
            String artifactName =
                    mavenProject.getArtifactId() + SEPERATOR + mavenProject.getVersion() + FILE_TYPE_SEPERATOR
                            + artifactType;
            File archive = createArchive(path, project, artifactName);
            if (archive != null && archive.exists()) {
                mavenProject.getArtifact().setFile(archive);
            }
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error while creating gadget archive " + mavenProject.getArtifactId() + FILE_TYPE_SEPERATOR
                            + artifactType, e);
        }
    }

    /**
     * create a archive file using given location,artifact name and artifact location
     *
     * @param location         path of artifact directory
     * @param artifactLocation artifact location
     * @param artifactName     name of the artifact
     * @return File
     * @throws Exception
     */
    public static File createArchive(File location, File artifactLocation, String artifactName) throws IOException {
        File targetFolder;
        targetFolder = new File(location.getPath(), TARGET);
        File gadgetDataFolder = new File(targetFolder, DSGADGET_TEMP);
        if (!gadgetDataFolder.mkdirs()) {
            logger.error(ERROR_CREATING_TEMP_DIRECTORY + DSGADGET_TEMP + IN + targetFolder.getAbsolutePath());
        }
        File zipDirectory = new File(gadgetDataFolder, artifactLocation.getName());
        if (!zipDirectory.mkdirs()) {
            logger.error(ERROR_CREATING_ZIP_DIRECTORY + artifactLocation.getName() + IN + gadgetDataFolder
                    .getAbsolutePath());
        }
        FileUtils.copyDirectory(artifactLocation, zipDirectory);
        File zipFile = new File(targetFolder, artifactName);
        zipDirectory(zipDirectory.getAbsolutePath(), zipFile.toString());
        FileUtils.deleteDirectory(gadgetDataFolder);
        return zipFile;
    }

    /**
     * Zipping given source folder into destination zip file
     *
     * @param srcFolder   source folder
     * @param destZipFile destination zip file
     */
    private static void zipDirectory(String srcFolder, String destZipFile) throws IOException {
        try (FileOutputStream fileWriter = new FileOutputStream(destZipFile);
                ZipOutputStream zip = new ZipOutputStream(fileWriter)) {
            addDirectoryContentsToZip(srcFolder, zip);
            zip.flush();
        }
    }

    /**
     * adding source file into zip file
     *
     * @param path    Path
     * @param srcFile source file
     * @param zip     destination zip file
     */
    private static void addToZip(String path, String srcFile, ZipOutputStream zip) {

        File file = new File(srcFile);

        if (file.isDirectory()) {
            addDirectoryToZip(path, srcFile, zip);
        } else {
            if (!srcFile.equals(PROJECT_EXTENTION)) {
                byte[] buf = new byte[1024];
                int len;
                try (FileInputStream in = new FileInputStream(srcFile)) {
                    String location = file.getName();
                    if (!path.isEmpty()) {
                        location = path + File.separator + file.getName();
                    }
                    zip.putNextEntry(new ZipEntry(location));
                    while ((len = in.read(buf)) > 0) {
                        zip.write(buf, 0, len);
                    }
                } catch (IOException e) {
                    logger.error(ERROR_CREATING_ZIP_FILE + file.getName() + IN + path, e);
                }
            }
        }
    }

    /**
     * adding folder contents to the zip file.
     *
     * @param srcFolder directory of content files
     * @param zip       destination zip file
     */
    private static void addDirectoryContentsToZip(String srcFolder, ZipOutputStream zip) {
        File folder = new File(srcFolder);
        String fileListArray[] = folder.list();
        int i = 0;
        if (fileListArray != null) {
            while (i < fileListArray.length) {
                addToZip("", srcFolder + File.separator + fileListArray[i], zip);
                i++;
            }
        }
    }

    /**
     * adding given folder to the Zip file
     *
     * @param path      Path
     * @param srcFolder source folder
     * @param zip       destination zip file
     */
    private static void addDirectoryToZip(String path, String srcFolder, ZipOutputStream zip) {
        File folder = new File(srcFolder);
        String fileListArray[] = folder.list();
        int i = 0;
        if (fileListArray != null) {
            while (i < fileListArray.length) {
                String newPath = folder.getName();
                if (!path.isEmpty()) {
                    newPath = path + File.separator + newPath;
                }
                addToZip(newPath, srcFolder + File.separator + fileListArray[i], zip);
                i++;
            }
        }
    }
}