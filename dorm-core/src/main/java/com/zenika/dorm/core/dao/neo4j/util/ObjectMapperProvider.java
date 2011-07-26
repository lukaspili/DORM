package com.zenika.dorm.core.dao.neo4j.util;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * @author Antoine ROUAZE <antoine.rouaze AT zenika.com>
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper defautltObjectMapper;

    public ObjectMapperProvider() {
        defautltObjectMapper = createDefaultMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return defautltObjectMapper;
    }

    private static ObjectMapper createDefaultMapper() {
        AnnotationIntrospector.Pair combinedIntrospector = createJaxbJacksonAnnotationIntrospector();
        ObjectMapper result = new ObjectMapper();
        result.getDeserializationConfig().setAnnotationIntrospector(combinedIntrospector);
        result.getSerializationConfig().setAnnotationIntrospector(combinedIntrospector);
        return result;
    }

    private static AnnotationIntrospector.Pair createJaxbJacksonAnnotationIntrospector() {

        AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector();
        AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

        return new AnnotationIntrospector.Pair(jaxbIntrospector, jacksonIntrospector);
    }
}