/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * 
 *
 */
public class UtilityOntologyLocation
{
    private static Logger logger = Logger.getLogger(UtilityOntologyLocation.class);
    static final String BASE_URI_WITHOUT_HASH = SampleOntologyManager.baseURI.substring(
    		0, SampleOntologyManager.baseURI.length() -1);

    public static String getStandardOntologyLocation() throws Exception
    {
        try
        {
            String configFolderLocation = PropertyHandler.getVariable("configuration_location");
            File configFolder = new File(configFolderLocation);
            if(configFolder.isDirectory())
            {
                List<String> childFiles = Arrays.asList(configFolder.list());
                String sampleSubFolderName = configFolderLocation +
                        File.separator + "org.grits.toolbox.entry.sample";
                File sampleSubFolder = new File(sampleSubFolderName);
                boolean subFolderExists = sampleSubFolder.exists();
                if(!subFolderExists && !childFiles.contains(sampleSubFolderName))
                {
                    subFolderExists = sampleSubFolder.mkdir();
                }
                if(subFolderExists)
                {
                    String configOntologyLocation = sampleSubFolder.getAbsolutePath() +
                            File.separator + Config.STANDARD_ONTOLOGY_FILE_NAME;
                    childFiles = Arrays.asList(sampleSubFolder.list());

                    URL resourceFileUrl = FileLocator.toFileURL(Config.ONTOLOGY_RESOURCE_URL);
                    String originalJarFilePath = resourceFileUrl.getPath() + Config.STANDARD_ONTOLOGY_FILE_NAME;
                    File originalJarFile = new File(originalJarFilePath);
                    
                    boolean toBeCopied = false;
                    if(childFiles.contains(Config.STANDARD_ONTOLOGY_FILE_NAME))
                    {
                        InputStream existingInputOntology = new FileInputStream(configOntologyLocation);
                        OntModel existingOntologyModel = ModelFactory.createOntologyModel(
                                OntModelSpec.OWL_DL_MEM_RDFS_INF, null);
                        existingOntologyModel.read(existingInputOntology, SampleOntologyManager.baseURI);
                        String existingVersionInfo = existingOntologyModel.getOntology(
                        		BASE_URI_WITHOUT_HASH).getVersionInfo();

                        InputStream originalJarInputOntology = new FileInputStream(originalJarFile);
                        OntModel originalJarOntologyModel = ModelFactory.createOntologyModel(
                                OntModelSpec.OWL_DL_MEM_RDFS_INF, null);
                        originalJarOntologyModel.read(originalJarInputOntology, SampleOntologyManager.baseURI);
                        String newVersionInfo = originalJarOntologyModel.getOntology(
                                BASE_URI_WITHOUT_HASH).getVersionInfo();
                        
                        toBeCopied = !existingVersionInfo.equals(newVersionInfo);
                    }
                    else
                    {
                        toBeCopied = true;
                    }
                    if(toBeCopied)
                    {
                        FileOutputStream configFile = new FileOutputStream(configOntologyLocation);
                        Files.copy(originalJarFile.toPath(), configFile);
                        configFile.close();
                    }
                    return configOntologyLocation;
                }
                else
                {
                    throw new Exception("Error locating subfolder for the ontology. Please check at this location : "
                            + sampleSubFolderName);
                }
            }
        } catch (Exception ex)
        {
            logger.error(ex);
            throw ex;
        }
        return null;
    }

    public static String getLocalOntologyLocation() throws Exception
    {
        try
        {
            String configFolderLocation = PropertyHandler.getVariable("configuration_location");
            File configFolder = new File(configFolderLocation);
            if(configFolder.isDirectory())
            {
                List<String> childFiles = Arrays.asList(configFolder.list());
                String sampleSubFolderName = configFolderLocation +
                        File.separator + "org.grits.toolbox.entry.sample";
                File sampleSubFolder = new File(sampleSubFolderName);
                boolean subFolderExists = sampleSubFolder.exists();
                if(!subFolderExists && !childFiles.contains(sampleSubFolderName))
                {
                    subFolderExists = sampleSubFolder.mkdir();
                }
                if(subFolderExists)
                {
                    String configOntologyLocation = sampleSubFolder.getAbsolutePath() + 
                            File.separator + Config.LOCAL_ONTOLOGY_FILE_NAME;
                    childFiles = Arrays.asList(sampleSubFolder.list());
                    if(!childFiles.contains(Config.LOCAL_ONTOLOGY_FILE_NAME))
                    {
                        URL resourceFileUrl = FileLocator.toFileURL(Config.ONTOLOGY_RESOURCE_URL);
                        String originalJarFilePath = resourceFileUrl.getPath() + Config.LOCAL_ONTOLOGY_FILE_NAME;
                        File originalJarFile = new File(originalJarFilePath);
                        FileOutputStream configFile = new FileOutputStream(configOntologyLocation );
                        Files.copy(originalJarFile.toPath(), configFile);
                        configFile.close();
                    }
                    return configOntologyLocation;
                }
                else
                {
                    throw new Exception("Error locating subfolder for the ontology. Please check at this location : "
                            + sampleSubFolderName);
                }
            }
        } catch (Exception e)
        {
            throw e;
        }
        return null;
    }

}
