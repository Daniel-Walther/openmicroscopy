/*
 *   $Id$
 *
 *   Copyright 2009 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.formats.importer;

import omero.model.IObject;

/**
 * Utility class which configures the Import.
 * 
 * @since Beta4.1
 */
public class ImportEvent {

    public String toLog() {
        return "Event: " + getClass().getSimpleName();
    }

    
    // Base classes

    public static class COUNT_EVENT extends ImportEvent {
        public final String shortName;
        public final Integer index;
        public final Integer numDone;
        public final Integer total;

        COUNT_EVENT(String shortName, Integer index, Integer numDone,
                Integer total) {
            this.shortName = shortName;
            this.index = index;
            this.numDone = numDone;
            this.total = total;
        }
        
    }

    public static class PROGRESS_EVENT extends ImportEvent {
        public final int index;
        public final String filename;
        public final IObject target;
        public final Long pixId;
        public final int series;
        public final ImportSize size;

        PROGRESS_EVENT(int index, String filename, IObject target, Long pixId, int series, ImportSize size) {
            this.index = index;
            this.filename = filename;
            this.target = target;
            this.pixId = pixId;
            this.series = series;
            this.size = size;
        }
    }

    public static class FILE_UPLOAD_EVENT extends ImportEvent {
        public final String filename;
        public final int fileIndex;
        public final int fileTotal;
        public final Long uploadedBytes;
        public final Long contentLength;
        public final Exception exception;

        FILE_UPLOAD_EVENT(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            this.filename = filename;
            this.fileIndex = fileIndex;
            this.fileTotal = fileTotal;
            this.uploadedBytes = uploadedBytes;
            this.contentLength = contentLength;
            this.exception = exception;
        }
    }

    // Data-less events

    public static class ADD extends ImportEvent {
    }

    public static class ERRORS_PENDING extends ImportEvent {
    }

    public static class ERRORS_SEND extends ImportEvent {

    }
    
    public static class ERRORS_COMPLETE extends ImportEvent {
        
    }
    
    public static class ERRORS_UPLOAD_CANCELLED extends ImportEvent {
        
    }

    public static class REIMPORT extends ImportEvent {
        
    }
    
    public static class LOGGED_IN extends ImportEvent {
        
    }
    
    // file-upload events

    public static class FILE_UPLOAD_STARTED extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_STARTED(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }
    
    public static class FILE_UPLOAD_BYTES extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_BYTES(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }
    
    public static class FILE_UPLOAD_COMPLETE extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_COMPLETE(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }
    
    public static class FILE_UPLOAD_FAILED extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_FAILED(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }
    
    public static class FILE_UPLOAD_ERROR extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_ERROR(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }
    
    public static class FILE_UPLOAD_FINISHED extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_FINISHED(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }
    
    public static class FILE_UPLOAD_CANCELLED extends FILE_UPLOAD_EVENT {
        public FILE_UPLOAD_CANCELLED(String filename, int fileIndex, int fileTotal,
                Long uploadedBytes, Long contentLength, Exception exception) {
            super(filename, fileIndex, fileTotal, uploadedBytes, contentLength,
                    exception);
        }
    }

    // misc-events

    public static class EXCEPTION_EVENT extends ImportEvent {
        public final String filename;
        public final Exception exception;
        public EXCEPTION_EVENT(String filename, Exception exception) {
            this.filename = filename;
            this.exception = exception;
        }
    }
    
    public static class IO_EXCEPTION extends EXCEPTION_EVENT {
        public IO_EXCEPTION(String filename, Exception exception) {
            super(filename, exception);
        }
    }
    
    public static class DEBUG_SEND extends ImportEvent {
        public final boolean sendFiles;

        public DEBUG_SEND(boolean sendFiles) {
            this.sendFiles = sendFiles;
        }
    }

    public static class IMPORT_STEP extends ImportEvent {
        public final int step;
        public final int series;
        public final int seriesCount;

        public IMPORT_STEP(int step, int series, int seriesCount) {
            this.step = step;
            this.series = series;
            this.seriesCount = seriesCount;
        }
        
        @Override
        public String toLog() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toLog());
            sb.append(" ");
            sb.append(String.format(
                    "Image: %d Series: %d Total Series: %d",
                    step,
                    series,
                    seriesCount));
            return sb.toString();
        }
    }

    // count-events

    public static class LOADING_IMAGE extends COUNT_EVENT {
        public LOADING_IMAGE(String shortName, Integer index, Integer numDone,
                Integer total) {
            super(shortName, index, numDone, total);
        }
    }

    public static class LOADED_IMAGE extends COUNT_EVENT {
        public LOADED_IMAGE(String shortName, Integer index, Integer numDone,
                Integer total) {
            super(shortName, index, numDone, total);
        }
    }

    //
    // Progress-based events: these are used by the FileQueueTable (and others)
    // to know which file index is currently in which state. They should possibly
    // be moved closer to the classes using them.
    //

    public static class DATASET_STORED extends PROGRESS_EVENT {
        public DATASET_STORED(int index, String filename, IObject target, Long pixId, int series,
                ImportSize size) {
            super(index, filename, target, pixId, series, size);
        }
    }

    public static class DATA_STORED extends PROGRESS_EVENT {
        public DATA_STORED(int index, String filename, IObject target, Long pixId, int series,
                ImportSize size) {
            super(index, filename, target, pixId, series, size);
        }
    }

    public static class IMPORT_ARCHIVING extends PROGRESS_EVENT {
        public IMPORT_ARCHIVING(int index, String filename, IObject target, Long pixId, int series,
                ImportSize size) {
            super(index, filename, target, pixId, series, size);
        }
    }

    public static class IMPORT_THUMBNAILING extends PROGRESS_EVENT {
        public IMPORT_THUMBNAILING(int index, String filename, IObject target, Long pixId, int series,
                ImportSize size) {
            super(index, filename, target, pixId, series, size);
        }
    }

    public static class IMPORT_DONE extends PROGRESS_EVENT {
        public IMPORT_DONE(int index, String filename, IObject target, Long pixId, int series,
                ImportSize size) {
            super(index, filename, target, pixId, series, size);
        }
    }

    //
    // Events which should be housed elsewhere
    //

    public static class QUICKBAR_UPDATE extends ImportEvent {

    }
    
    
}
