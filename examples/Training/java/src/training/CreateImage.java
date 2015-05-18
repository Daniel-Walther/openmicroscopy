/*
 * training.CreateImage 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2013 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package training;


//Java imports
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//Third-party libraries




//Application-internal dependencies
import omero.RLong;
import omero.api.IContainerPrx;
import omero.api.IPixelsPrx;
import omero.api.RawPixelsStorePrx;
import omero.model.DatasetI;
import omero.model.DatasetImageLink;
import omero.model.DatasetImageLinkI;
import omero.model.IObject;
import omero.model.Image;
import omero.model.ImageI;
import omero.model.LengthI;
import omero.model.PixelsType;
import ome.model.enums.UnitsLength;
import omero.sys.ParametersI;
import pojos.ImageData;
import pojos.PixelsData;

/** 
 * Sample code showing how to create new image.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @since Beta4.3.2
 */
public class CreateImage
{

	//The value used if the configuration file is not used. To edit*/
	/** The server address.*/
	private String hostName = "serverName";

	/** The username.*/
	private String userName = "userName";
	
	/** The password.*/
	private String password = "password";
	
	/** The id of an image.*/
	private long imageId = 1;
	
	/** Id of the dataset hosting the image of reference.*/
	private long datasetId = 1;
	//end edit
	
	
	/** The image.*/
	private ImageData image;
	
	/** Reference to the connector.*/
	private Connector connector;
	
	/**
	 * Loads the image.
	 * 
	 * @param imageID The id of the image to load.
	 * @return See above.
	 */
	private ImageData loadImage(long imageID)
		throws Exception
	{
		IContainerPrx proxy = connector.getContainerService();
		List<Image> results = proxy.getImages(Image.class.getName(),
				Arrays.asList(imageID), new ParametersI());
		//You can directly interact with the IObject or the Pojos object.
		//Follow interaction with the Pojos.
		if (results.size() == 0)
			throw new Exception("Image does not exist. Check ID.");
		return new ImageData(results.get(0));
	}

	/**
	 * Returns a linearize version of the plane.
	 * 
	 * @param z The selected z-section.
	 * @param t The selected timepoint.
	 * @param sizeZ The number of z-sections.
	 * @return
	 */
	private Integer linearize(int z, int c, int t,  int sizeZ, int sizeC)
	{
		if (z < 0 || sizeZ <= z) 
			throw new IllegalArgumentException(
					"z out of range [0, "+sizeZ+"]: "+z+".");
		if (c < 0 || sizeC <= c)
			throw new IllegalArgumentException(
					"c out of range [0, "+sizeC+"]: "+c+".");
		return Integer.valueOf(sizeZ*sizeC*t + sizeZ*c + z);
	}
	
	/**
	 * Creates a new image with one channel from a source image.
	 * 
	 * @param info The information about the data to handle.
	 */
	private void CreateNewImage(ConfigurationInfo info)
		throws Exception
	{
		PixelsData pixels = image.getDefaultPixels();
		int sizeZ = pixels.getSizeZ();
		int sizeT = pixels.getSizeT();
		int sizeC = pixels.getSizeC();
		int sizeX = pixels.getSizeX();
		int sizeY = pixels.getSizeY();
		long pixelsId = pixels.getId();

		//Sets the pixel size using units (new in 5.1.0)
		LengthI units = new LengthI(9.8, UnitsLength.ANGSTROM);
		pixels.setPixelSizeX(units);
		pixels.setPixelSizeY(units);

		if (sizeC <= 1)
			throw new Exception("The image must have at least 2 channels.");
		RawPixelsStorePrx store = null;
		//Create a new image.
		Map<Integer, byte[]> map = new LinkedHashMap<Integer, byte[]>();
		try {
			store = connector.getRawPixelsStore();
			store.setPixelsId(pixelsId, false);
			for (int c = 0; c < sizeC; c++) {
				for (int z = 0; z < sizeZ; z++) {
					for (int t = 0; t < sizeT; t++) {
						byte[] plane = store.getPlane(z, c, t);
						map.put(linearize(z, c, t, sizeZ, sizeC), plane);
					}
				}
			}
			
		} catch (Exception e) {
			throw new Exception("Cannot retrieve the plane", e);
		} finally {
			if (store != null) store.close();
		}

		//Now we are going to create the new image.
		IPixelsPrx proxy = connector.getPixelsService();
		List<IObject> l = proxy.getAllEnumerations(PixelsType.class.getName());
		Iterator<IObject> i = l.iterator();
		PixelsType type = null;
		String original = pixels.getPixelType();
		while (i.hasNext()) {
			PixelsType o =  (PixelsType) i.next();
			String value = o.getValue().getValue();
			if (value.equals(original)) {
				type = o;
				break;
			}
		}
		if (type == null)
			throw new Exception("Pixels Type not valid.");
	
		String name = "newImageFrom"+image.getId();
		List<Integer> channels = new ArrayList<Integer>();
		for (int c = 0; c < sizeC; c++) {
			channels.add(c);
		}
		RLong idNew = proxy.createImage(sizeX, sizeY, sizeZ, sizeT,
			channels, type, name, "From Image ID: "+image.getId());
		if (idNew == null)
			throw new Exception("New image could not be created.");
		ImageData newImage = loadImage(idNew.getValue());
		
		//link the new image and the dataset hosting the source image.
		DatasetImageLink link = new DatasetImageLinkI();
		link.setParent(new DatasetI(info.getDatasetId(), false));
		link.setChild(new ImageI(newImage.getId(), false));
		connector.getUpdateService().saveAndReturnObject(link);
		
		//Write the data.
		try {
			store = connector.getRawPixelsStore();
			store.setPixelsId(newImage.getDefaultPixels().getId(), false);
			int index = 0;
			for (int c = 0; c < sizeC; c++) {
				for (int z = 0; z < sizeZ; z++) {
					for (int t = 0; t < sizeT; t++) {
						index = linearize(z, c, t, sizeZ, sizeC);
						store.setPlane(map.get(index), z, c, t);
					}
				}
			}
			store.save();
			System.err.println("image created");
		} catch (Exception e) {
			throw new Exception("Cannot set the plane", e);
		} finally {
			if (store != null) store.close();
		}
	}
	
	/**
	 * Connects and invokes the various methods.
	 * 
	 * @param info The configuration information
	 */
	CreateImage(ConfigurationInfo info)
	{
		if (info == null) {
			info = new ConfigurationInfo();
			info.setHostName(hostName);
			info.setPassword(password);
			info.setUserName(userName);
			info.setDatasetId(datasetId);
			info.setImageId(imageId);
		}
		connector = new Connector(info);
		try {
			connector.connect();
			image = loadImage(info.getImageId());
			CreateNewImage(info);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connector.disconnect(); // Be sure to disconnect
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Runs the script without configuration options.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		new CreateImage(null);
		System.exit(0);
	}
}
