/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.grits.toolbox.entry.sample.config.Config;

/**
 * 
 *
 */
public class UtilityNamespaceIdFile
{
    private static Logger logger = Logger.getLogger(UtilityNamespaceIdFile.class);

    public static String getId(String namespaceIdFileName, String value)
    {
        try
        {
            String id = null;
            URL resourceFileUrl = FileLocator.toFileURL(Config.NAMESPACE_RESOURCE_URL);
            String namespaceFilePath = resourceFileUrl.getPath() + namespaceIdFileName;
            File namespaceIdFile = new File(namespaceFilePath);
            FileReader fileReader = new FileReader(namespaceIdFile);
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine();
            String[] values = null;
            while (line != null) 
            {
                values = line.split("\t");
                if(values[0].equals(value))
                {
                    id = values[1];
                    break;
                }
                line = br.readLine();
            }
            br.close();
            fileReader.close();
            return id;
        } catch (IOException ex)
        {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

}
