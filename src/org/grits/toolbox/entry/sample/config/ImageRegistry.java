/**
 * 
 */
package org.grits.toolbox.entry.sample.config;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.grits.toolbox.entry.sample.Activator;

/**
 * 
 *
 */
public class ImageRegistry
{
	private static Logger logger = Logger.getLogger(ImageRegistry.class);
	private static final String IMAGE_PATH = "icons" + File.separator;
	private static Map<SampleImage, ImageDescriptor> imageCache = new HashMap<SampleImage, ImageDescriptor>();

	public static ImageDescriptor getImageDescriptor(SampleImage sampleImage)
	{
		logger.info("Get image from sample plugin : " + sampleImage);

		ImageDescriptor imageDescriptor = null;
		if(sampleImage != null)
		{
			imageDescriptor = imageCache.get(sampleImage);
			if(imageDescriptor == null)
			{
				logger.info("ImageDescriptor not found in cache");
				URL fullPathString = FileLocator.find(
						Platform.getBundle(Activator.PLUGIN_ID), new Path(IMAGE_PATH + sampleImage.iconName), null);

				logger.info("Loading image from url : " + fullPathString);
				if(fullPathString != null)
				{
					imageDescriptor = ImageDescriptor.createFromURL(fullPathString);
					imageCache.put(sampleImage, imageDescriptor);
				}
			}
		}
		else
			logger.error("Cannot load image from sample plugin (image name is null)");

		return imageDescriptor;
	}


	/**
	 ***********************************
	 *			Icons
	 ***********************************
	 */
	public enum SampleImage
	{
		// GRITS own icons
		SAMPLES_ICON("sampleGroup.png"),
		SAMPLE_ICON_SMALL("sampleIconSmall.png"),
		COMPONENT_ICON("sample16.png"),
		ADD_COMPONENT_LAUNCHER_ICON("test-tube2.png"),

		// FatCow Web Hosting ​http://www.fatcow.com Creative Commons Attribution (by)
		// ​http://www.findicons.com/icon/163648/chart_bar_add?id=164690
		ADD_DESCRIPTOR_GROUP_ICON("chart_bar_add.png"),
		// Bdate Kaspar/Franziska Sponsel ​http://rrze-icon-set.berlios.de/index.html Creative Commons Attribution (by)
		// ​http://www.findicons.com/icon/84660/pen?id=84972
		EDIT_DESCRIPTOR_ICON("pencil_icon.png"),
		// Yusuke Kamiyamane ​http://www.p.yusukekamiyamane.com CC Attribution 4.0
		// ​http://www.iconarchive.com/show/fugue-icons-by-yusuke-kamiyamane/tick-circle-icon.html
		CHECKBOX_TICKED_ICON("checkbox_ticked_icon.png"),
		// Yusuke Kamiyamane ​http://www.p.yusukekamiyamane.com CC Attribution 4.0
		// ​http://www.iconarchive.com/show/fugue-icons-by-yusuke-kamiyamane/tick-white-icon.html
		CHECKBOX_OPTIONAL_TICKED_ICON("checkbox_optional_ticked_icon.png"),
		// Sergio Sánchez López ​http://www.kde-look.org/usermanager/search.php?username=Sephiroth6779 GNU GPL license
		// ​https://www.iconfinder.com/icons/7546/a_adobe_font_letter_type_icon
		EDIT_NAME_ICON("rename.png"),
		// Icon Archive ​http://www.iconarchive.com CC Attribution 3.0
		// ​http://www.iconarchive.com/show/red-orb-alphabet-icons-by-iconarchive/Letter-T-icon.html
		CREATE_TEMPLATE_ICON("template_icon.png");

		private String iconName = null;
		private SampleImage(String iconName)
		{
			this.iconName  = iconName;
		}
	}
}
