//package com.zenika.dorm.core.dao.mongo;
//
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import com.zenika.dorm.core.exception.CoreException;
//import com.zenika.dorm.core.model.old.DormArtifact;
//import com.zenika.dorm.core.model.old.DormFile;
//import com.zenika.dorm.core.model.old.DormMetadata;
//import com.zenika.dorm.core.model.old.MetadataExtension;
//
//import java.lang.reflect.Field;
//
///**
// * @author Lukasz Piliszczuk <lukasz.piliszczuk AT zenika.com>
// */
//public class MongoMapping {
//
//    public static <T extends MetadataExtension> BasicDBObject getDocumentFromMetadata(DormMetadata<T> metadata) {
//
//        // map basic metadata
//        BasicDBObject document = new BasicDBObject();
//        document.put("name", metadata.getName());
//        document.put("version", metadata.getVersion());
//        document.put("origin", metadata.getOrigin());
//
//        // map specific metadata
//        try {
//            mapSpecificMetadata(metadata.getExtension(), document);
//        } catch (IllegalAccessException e) {
//            throw new CoreException("Cannot map specific metadata by reflection", e);
//        }
//
//        return document;
//    }
//
//    public static <T extends MetadataExtension> BasicDBObject getDocumentFromArtifact(DormArtifact<T> artifact) {
//
//        BasicDBObject document = getDocumentFromMetadata(artifact.getMetadata());
//
//        return document;
//    }
//
//    public static <T extends MetadataExtension> void mapSpecificMetadata(T metadata, BasicDBObject object) throws IllegalAccessException {
//
//        if (null == metadata) {
//            return;
//        }
//
//        Class<? extends MetadataExtension> reflect = metadata.getClass();
//
//        for (Field field : reflect.getDeclaredFields()) {
//
//            // include non public attributes
//            field.setAccessible(true);
//
//            // TODO: Add required field checking, by annotation for example ?
//
//            // get the field value
//            Object value = field.get(metadata);
//
//            // file metadata
//            if (Field.class.equals(field.getType()) && null != value) {
//
//                //TODO: Add file
//            } else {
//                object.put(field.getName(), value);
//            }
//        }
//    }
//
//    public static <T extends MetadataExtension> DormArtifact<T> getArtifactFromDocument(DBObject document,
//                                                                                        Class extensionClass) {
//
//        DormMetadata<T> metadata = mapDocumentToDormMetadata(document);
//
//        DormFile file = mapDocumentToDormFile(document);
//
//        // create and map metadata extension if exists
//        if (null != document.get("extension")) {
//            try {
//
//                // get the mapped metadata extension
//                T metadataExtension = (T) mapDocumentToMetadataExtension(document,
//                        extensionClass);
//
//                // and set it to the mapped metadata
//                metadata.setExtension(metadataExtension);
//
//            } catch (Exception e) {
//                throw new CoreException("Cannot map metadata extension");
//            }
//        }
//
//        DormArtifact<T> artifact = new DormArtifact<T>(metadata, file);
//
//        return artifact;
//    }
//
//    protected static <T extends MetadataExtension> DormMetadata<T> mapDocumentToDormMetadata(DBObject document) {
//
//        DormMetadata<T> mappedMetadata = null;
//
//        try {
//            String name = document.get("name").toString();
//            String version = document.get("version").toString();
//            String origin = document.get("origin").toString();
//
//            mappedMetadata = new DormMetadata<T>(name, version, origin);
//
//        } catch (NullPointerException e) {
//            throw new CoreException("Empty requiered informations in the document");
//        }
//
//        return mappedMetadata;
//    }
//
//    /**
//     * Get the DormFile from the document model dorm schema
//     */
//    protected static DormFile mapDocumentToDormFile(DBObject document) {
//
////        DormFile dormFile = new DormFile();
//
//        return null;
//    }
//
//    protected static <T extends MetadataExtension> T mapDocumentToMetadataExtension(DBObject document, Class<T> extensionClass)
//            throws IllegalArgumentException, IllegalAccessException, InstantiationException {
//
////        T metadataExtension;
////
////        try {
////            // TODO: Correct this if possible ?
////            @SuppressWarnings("unchecked")
////            Class<T> extensionClass = (Class<T>) metadata.getExtension()
////                    .getClass();
////
////            // create new instance of metadata extension by reflection
////            metadataExtension = extensionClass.newInstance();
////        } catch (Exception e) {
////            throw new CoreException(
////                    "Cannot create new instance of metadata extension");
////        }
////
////        Class<? extends MetadataExtension> reflect = extension.getClass();
//
//        T extension = extensionClass.newInstance();
//
//        for (Field field : extensionClass.getDeclaredFields()) {
//
//            // include non public attributes
//            field.setAccessible(true);
//
//            // get the property matching the field's name
//            Object property = document.get(field.getName());
//
//            // and set it in the metadata
//            field.set(extension, property);
//        }
//
//        return extension;
//    }
//}
