/*
 * pojos.ImageData
 *
 *   Copyright 2006 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package pojos;




//Java imports
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import static omero.rtypes.rstring;
import omero.RTime;
import omero.model.Annotation;
import omero.model.CommentAnnotation;
import omero.model.DatasetImageLink;
import omero.model.Image;
import omero.model.ImageAnnotationLink;
import omero.model.ImageI;
import omero.model.Instrument;
import omero.model.LongAnnotation;
import omero.model.Pixels;

/**
 * The data that makes up an <i>OME</i> Image along with links to its Pixels,
 * enclosing Datasets, and the Experimenter that owns this Image.
 * 
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author <br>
 *         Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:a.falconi@dundee.ac.uk"> a.falconi@dundee.ac.uk</a>
 * @version 2.2 <small> (<b>Internal version:</b> $Revision$ $Date: 2005/05/09
 *          19:50:41 $) </small>
 * @since OME2.2
 */
public class ImageData extends DataObject {

    /** Identifies the {@link Image#NAME} field. */
    public final static String NAME = ImageI.NAME;

    /** Identifies the {@link Image#DESCRIPTION} field. */
    public final static String DESCRIPTION = ImageI.DESCRIPTION;

    /** Identifies the {@link Image#PIXELS} field. */
    public final static String PIXELS = ImageI.PIXELS;

    /** Identifies the {@link Image#ANNOTATIONLINKS} field. */
    public final static String ANNOTATIONS = ImageI.ANNOTATIONLINKS;

    /** Identifies the {@link Image#DATASETLINKS} field. */
    public final static String DATASET_LINKS = ImageI.DATASETLINKS;

    /**
     * All the Pixels that belong to this Image. The elements of this set are
     * {@link PixelsData} objects. This field may not be <code>null</code> nor
     * empty. As a minimum, it will contain the {@link #defaultPixels default}
     * Pixels.
     * 
     * An <i>OME</i> Image can be associated to more than one 5D pixels set
     * (that is, the raw image data) if all those sets are derived from an
     * initial image file. An example is a deconvolved image and the original
     * file: those two pixels sets would be represented by the same <i>OME</i>
     * Image. In the case there's more than one pixels set, the first pixels
     * identifies the pixels that are used by default for analysis and
     * visualization.
     */
    private List<PixelsData> allPixels;

    /**
     * All the Datasets that contain this Image. The elements of this set are
     * {@link DatasetData} objects. If this Image is not contained in any
     * Dataset, then this set will be empty &#151; but never <code>null</code>.
     */
    private Set<DatasetData> datasets;

    /**
     * All the annotations related to this Image. The elements of the set are
     * {@link AnnotationData} objetcs. If this Image hasn't been annotated, then
     * this set will be empty &#151; but never <code>null</code>.
     */
    private Set<AnnotationData> annotations;

    /**
     * The number of annotations attached to this Image. This field may be
     * <code>null</code> meaning no count retrieved, and it may be less than
     * the actual number if filtered by user.
     */
    private Long annotationCount;

    /**
     * The number of categories attached to this Imaget. This field may be
     * <code>null</code> meaning no count retrieved, and it may be less than
     * the actual number if filtered by user.
     */
    private Long classificationCount;

    /** Creates a new instance. */
    public ImageData() {
        setDirty(true);
        setValue(new ImageI());
    }

    /**
     * Creates a new instance.
     * 
     * @param image
     *            Back pointer to the {@link Image} model object. Mustn't be
     *            <code>null</code>.
     * @throws IllegalArgumentException
     *             If the object is <code>null</code>.
     */
    public ImageData(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Object cannot null.");
        }
        setValue(image);
    }

    // Immutables

    /**
     * Sets the name of the image.
     * 
     * @param name
     *            The name of the image. Mustn't be <code>null</code>.
     * @throws IllegalArgumentException
     *             If the name is <code>null</code>.
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The name cannot be null.");
        }
        setDirty(true);
        asImage().setName(rstring(name));
    }

    /**
     * Returns the name of the image.
     * 
     * @return See above.
     */
    public String getName() {
        omero.RString n = asImage().getName();
        if (n == null || n.getValue() == null) {
            throw new IllegalStateException(
                    "The name should never have been null");
        }
        return n.getValue();
    }

    /**
     * Sets the description of the image.
     * 
     * @param description
     *            The description of the image.
     */
    public void setDescription(String description) {
        setDirty(true);
        asImage().setDescription(
                description == null ? null : rstring(description));
    }

    /**
     * Returns the description of the image.
     * 
     * @return See above.
     */
    public String getDescription() {
        omero.RString d = asImage().getDescription();
        return d == null ? null : d.getValue();
    }

    /**
     * Returns <code>true</code> if the image has been archived,
     * <code>false</code> otherwise.
     * 
     * @return See above.
     */
    public boolean isArchived() {
    	omero.RBool value = asImage().getArchived();
    	if (value == null) return false;
    	return value.getValue();
    }
    
    /**
     * Returns the number of annotations linked to the object, key: id of the
     * user, value: number of annotation. The map may be <code>null</code> if
     * no annotation.
     * 
     * @return See above.
     */
    public Map<Long, Long> getAnnotationsCounts() {
        return asImage().getAnnotationLinksCountPerOwner();
    }

    /**
     * Returns the insertion time of the image.
     * 
     * @return See above.
     */
    public Timestamp getInserted() {
        return timeOfEvent(asImage().getDetails().getUpdateEvent());
    }

    /**
     * Returns the acquistion date.
     * 
     * @return See above.
     */
    public Timestamp getAcquisitionDate()
    {
    	RTime time = asImage().getAcquisitionDate();
    	if (time == null) return null;
    	return new Timestamp(time.getValue());
    }
    
    // Single-valued objects.

    /**
     * Returns the default set of pixels.
     * 
     * @return See above.
     */
    public PixelsData getDefaultPixels() {
        return getAllPixels().get(0);
    }

    /**
     * Sets the default set of pixels.
     * 
     * @param defaultPixels
     *            The default set of pixels.
     */
    public void setDefaultPixels(PixelsData defaultPixels) {
        if (getDefaultPixels() == defaultPixels) {
            return;
        }
        setDirty(true);
        allPixels = null; // Invalidated
        asImage().setPrimaryPixels(defaultPixels.asPixels());
    }

    // Sets

    /**
     * Returns all the sets of pixels related to this image.
     * 
     * @return See above.
     */
    @SuppressWarnings("unchecked")
    public List<PixelsData> getAllPixels() {
        if (allPixels == null && asImage().sizeOfPixels() >= 0) {
            allPixels = new ArrayList<PixelsData>();
            List<Pixels> pixels = asImage().copyPixels();
            for (Pixels p : pixels) {
                allPixels.add(new PixelsData(p));
            }
        }
        return allPixels == null ? null : new ArrayList<PixelsData>(allPixels);
    }

    /**
     * Sets the set of pixels related to this image.
     * 
     * @param newValue
     *            The set of pixels' set.
     */
    public void setAllPixels(List<PixelsData> newValue) {
        List<PixelsData> currentValue = getAllPixels();
        SetMutator<PixelsData> m = new SetMutator<PixelsData>(currentValue,
                newValue);

        while (m.moreDeletions()) {
            setDirty(true);
            asImage().removePixels(m.nextDeletion().asPixels());
        }

        while (m.moreAdditions()) {
            setDirty(true);
            asImage().addPixels(m.nextAddition().asPixels());
        }

        allPixels = m.result();
    }

    /**
     * Returns the datasets containing this image.
     * 
     * @return See above.
     */
    public Set getDatasets() {
        if (datasets == null && asImage().sizeOfDatasetLinks() >= 0) {
            datasets = new HashSet<DatasetData>();
            List<DatasetImageLink> links = asImage().copyDatasetLinks();
            for (DatasetImageLink link : links) {
                datasets.add(new DatasetData(link.getParent()));
            }
        }
        return datasets == null ? null : new HashSet(datasets);
    }

    /**
     * Sets the datasets containing the image.
     * 
     * @param newValue
     *            The set of datasets.
     */
    public void setDatasets(Set newValue) {
        Set<DatasetData> currentValue = getDatasets();
        SetMutator<DatasetData> m = new SetMutator<DatasetData>(currentValue,
                newValue);

        while (m.moreDeletions()) {
            setDirty(true);
            asImage().unlinkDataset(m.nextDeletion().asDataset());
        }

        while (m.moreAdditions()) {
            setDirty(true);
            asImage().linkDataset(m.nextAddition().asDataset());
        }

        datasets = new HashSet<DatasetData>(m.result());
    }

    /**
     * Returns the annotations
     * 
     * @return See above.
     */
    public Set getAnnotations() {
        if (annotations == null) {
            int size = asImage().sizeOfAnnotationLinks();
            if (size >= 0) {
                annotations = new HashSet<AnnotationData>(size);
                List<ImageAnnotationLink> links = asImage()
                        .copyAnnotationLinks();
                for (ImageAnnotationLink link : links) {
                    Annotation a = link.getChild();
                    if (a instanceof CommentAnnotation) {
                        annotations.add(new TextualAnnotationData(
                                (CommentAnnotation) a));
                    } else if (a instanceof LongAnnotation) {
                        annotations.add(new RatingAnnotationData(
                                (LongAnnotation) a));
                    }
                }
            }
        }
        return annotations == null ? null : new HashSet<AnnotationData>(
                annotations);
    }

    /**
     * Sets the image's annotations.
     * 
     * @param newValue
     *            The set of annotations.
     */
    public void setAnnotations(Set newValue) {
        Set<AnnotationData> currentValue = getAnnotations();
        SetMutator<AnnotationData> m = new SetMutator<AnnotationData>(
                currentValue, newValue);

        while (m.moreDeletions()) {
            setDirty(true);
            asImage().unlinkAnnotation(m.nextDeletion().asAnnotation());
            annotationCount = annotationCount == null ? null : new Long(
                    annotationCount.longValue() - 1);
        }

        while (m.moreAdditions()) {
            setDirty(true);
            asImage().linkAnnotation(m.nextAddition().asAnnotation());
            annotationCount = annotationCount == null ? null : new Long(
                    annotationCount.longValue() + 1);
        }

        annotations = new HashSet<AnnotationData>(m.result());
    }

	/**
	 * Returns <code>true</code> if the image is a lifetime image,
	 * <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
    public boolean isLifetime()
    {
    	String name = getName();
    	return (name != null && name.endsWith(".sdt"));
    }
    
    /**
     * Returns the id of the instrument if any.
     * 
     * @return See above.
     */
    public long getInstrumentId()
    {
    	Instrument instrument = asImage().getInstrument();
    	if (instrument == null) return -1;
    	return instrument.getId().getValue();
    }
    
}
