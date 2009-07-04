/*
 * pojos.ChannelAcquisitionData 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package pojos;



//Java imports

//Third-party libraries

//Application-internal dependencies
import omero.RDouble;
import omero.RInt;
import omero.RString;
import omero.model.Binning;
import omero.model.DetectorSettings;
import omero.model.DetectorSettingsI;
import omero.model.Filter;
import omero.model.FilterSet;
import omero.model.LightSettings;
import omero.model.LightSettingsI;
import omero.model.LightSource;
import omero.model.LogicalChannel;

/** 
 * Object hosting the acquisition related to a logical channel.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class ChannelAcquisitionData 
	extends DataObject
{

	/** The settings of the detector. */
	private DetectorSettings 	detectorSettings;
	
	/** The settings of the light source. */
	private LightSettings 		lightSettings;
	
	/** The filter used. */
	private FilterSet			filterSet;
	
	/** The filter used for the emission wavelength. */
	private FilterData			secondaryEmFilter;
	
	/** The filter used for the excitation wavelength. */
	private FilterData			secondaryExFilter;
	
	/** The light source. */
	private LightSourceData		ligthSource;
	
	/** Flag indicating if the detector settings is dirty. */
	private boolean				detectorSettingsDirty;
	
	/** Flag indicating if the detector settings is dirty. */
	private boolean				ligthSourceSettingsDirty;

	/** The detector used. */
	private DetectorData		detector;
	
	/** The binning. */
	private Binning 			binning;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param channel The image the acquisition data is related to. 
	 * 				Mustn't be <code>null</code>.
	 */
	public ChannelAcquisitionData(LogicalChannel channel)
	{
        if (channel == null)
            throw new IllegalArgumentException("Object cannot null.");
        setValue(channel);
        detectorSettings = channel.getDetectorSettings();
        lightSettings = channel.getLightSourceSettings();
        filterSet = channel.getFilterSet();
        Filter f = channel.getSecondaryEmissionFilter();
        if (f != null) secondaryEmFilter = new FilterData(f);
        f = channel.getSecondaryExcitationFilter();
        if (f != null) secondaryExFilter = new FilterData(f);
	}
	
	/**
	 * Returns the detector used for that channel.
	 * 
	 * @return See above.
	 */
	public DetectorData getDetector()
	{
		if (detectorSettings == null) return null;
		if (detector == null) 
			detector = new DetectorData(detectorSettings.getDetector());
		return detector;
	}
	
	/**
	 * Returns the offset set on the detector.
	 * 
	 * @return See above.
	 */
	public double getDetectorSettingsOffset()
	{
		if (detectorSettings == null) return -1;
		RDouble value = detectorSettings.getOffsetValue();
		if (value == null) return -1;
		return value.getValue();
	}
	
	/**
	 * Returns the gain set on the detector.
	 * 
	 * @return See above.
	 */
	public double getDetectorSettingsGain()
	{
		if (detectorSettings == null) return -1;
		RDouble value = detectorSettings.getGain();
		if (value == null) return -1;
		return value.getValue();
	}
	
	/**
	 * Returns the voltage set on the detector.
	 * 
	 * @return See above.
	 */
	public double getDetectorSettingsVoltage()
	{
		if (detectorSettings == null) return -1;
		RDouble value = detectorSettings.getVoltage();
		if (value == null) return -1;
		return value.getValue();
	}
	
	/**
	 * Returns the Read out rate set on the detector.
	 * 
	 * @return See above.
	 */
	public double getDetectorSettingsReadOutRate()
	{
		if (detectorSettings == null) return -1;
		RDouble value = detectorSettings.getReadOutRate();
		if (value == null) return -1;
		return value.getValue();
	}
	
	/**
	 * Returns the binning.
	 * 
	 * @return See above.
	 */
	public String getDetectorSettingsBinning()
	{
		if (detectorSettings == null) return "";
		Binning value = detectorSettings.getBinning();
		if (value == null) return "";
		return value.getValue().getValue();
	}
	
	
	
	
	/**
	 * Returns the attenuation of the ligth source, percent value 
	 * between 0 and 1.
	 * 
	 * @return See above.
	 */
	public double getLigthSettingsAttenuation()
	{
		if (lightSettings == null) return -1;
		RDouble value = lightSettings.getAttenuation();
		if (value == null) return -1;
		return value.getValue();
	}
	
	/**
	 * Returns the wavelength of the ligth source.
	 * 
	 * @return See above.
	 */
	public int getLigthSettingsWavelength()
	{
		if (lightSettings == null) return -1;
		RInt value = lightSettings.getWavelength();
		if (value == null) return -1;
		return value.getValue();
	}

	/**
	 * Returns the secondary emission filter.
	 * 
	 * @return See above.
	 */
	public FilterData getSecondaryEmissionFilter() { return secondaryEmFilter; }
	
	/**
	 * Returns the secondary excitation filter.
	 * 
	 * @return See above.
	 */
	public FilterData getSecondaryExcitationFilter()
	{
		return secondaryExFilter;
	}
	
	
	
	
	/**
	 * Returns <code>true</code> if there is an filter for that channel, 
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean hasFilter()
	{
		return filterSet != null;
	}
	
	/**
	 * Returns <code>true</code> if there is a detector for that channel,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean hasDectector() { return getDetector() != null; }
	
	/**
	 * Sets the attenuation of the light settings.
	 * 
	 * @param value The value to set.
	 */
	public void setLigthSettingsAttenuation(double value)
	{
		ligthSourceSettingsDirty = true;
		if (lightSettings == null) lightSettings = new LightSettingsI();
		lightSettings.setAttenuation(omero.rtypes.rdouble(value));
	}
	
	/**
	 * Returns the wavelength of the ligth source.
	 * 
	 * @param value The value to set.
	 */
	public void setLigthSettingsWavelength(int value)
	{
		ligthSourceSettingsDirty = true;
		if (lightSettings == null) lightSettings = new LightSettingsI();
		lightSettings.setWavelength(omero.rtypes.rint(value));
	}
	
	
	/**
	 * Sets the detector's setting offset.
	 * 
	 * @param value The value to set.
	 */
	public void setDetectorSettingOffset(double value)
	{
		detectorSettingsDirty = true;
		if (detectorSettings == null) 
			detectorSettings = new DetectorSettingsI();
		detectorSettings.setOffsetValue(omero.rtypes.rdouble(value));
	}
	
	/**
	 * Sets the detector setting's gain.
	 * 
	 * @param value The value to set.
	 */
	public void setDetectorSettingsGain(double value)
	{
		detectorSettingsDirty = true;
		if (detectorSettings == null) 
			detectorSettings = new DetectorSettingsI();
		detectorSettings.setGain(omero.rtypes.rdouble(value));
	}
	
	/**
	 * Sets the detector setting's read out rate.
	 * 
	 * @param value The value to set.
	 */
	public void setDetectorSettingsReadOutRate(double value)
	{
		detectorSettingsDirty = true;
		if (detectorSettings == null) 
			detectorSettings = new DetectorSettingsI();
		detectorSettings.setReadOutRate(omero.rtypes.rdouble(value));
	}
	
	/**
	 * Sets the detector setting's voltage.
	 * 
	 * @param value The value to set.
	 */
	public void setDetectorSettingsVoltage(double value)
	{
		detectorSettingsDirty = true;
		if (detectorSettings == null) 
			detectorSettings = new DetectorSettingsI();
		detectorSettings.setVoltage(omero.rtypes.rdouble(value));
	}
	
	/**
	 * Sets the detector's binning.
	 * 
	 * @param binning The value to set.
	 */
	public void setDetectorSettingBinning(Binning binning)
	{
		this.binning = binning;
	}
	
	/**
	 * Returns the binning enumeration value.
	 * 
	 * @return See above.
	 */
	public Binning getDetectorBinningAsEnum() { return binning; }
	
	/**
	 * Returns <code>true</code> if the detector settings has been updated,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean isDetectorSettingsDirty() { return detectorSettingsDirty; }
	
	/**
	 * Returns <code>true</code> if the light source settings has been updated,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	public boolean isLightSourceSettingsDirty()
	{ 
		return ligthSourceSettingsDirty; 
	}
	
	/**
	 * Returns the source of light.
	 * 
	 * @return See above.
	 */
	public LightSourceData getLightSource()
	{
		if (lightSettings == null) return null;
		if (ligthSource != null) return ligthSource;
		LightSource src = lightSettings.getLightSource();
		if (src != null) ligthSource = new LightSourceData(src);
		return ligthSource;
	}
	
}
