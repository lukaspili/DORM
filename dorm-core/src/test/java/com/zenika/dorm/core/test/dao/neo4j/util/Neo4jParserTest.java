package com.zenika.dorm.core.test.dao.neo4j.util;

import com.zenika.dorm.core.dao.neo4j.util.Neo4jParser;
import com.zenika.dorm.core.model.DormMetadata;
import com.zenika.dorm.core.model.DormOrigin;
import com.zenika.dorm.core.model.impl.DefaultDormMetadata;
import com.zenika.dorm.core.model.impl.DefaultDormOrigin;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Antoine ROUAZE <antoine.rouaze AT zenika.com>
 */
public class Neo4jParserTest {

    private static Logger logger = Logger.getLogger(Neo4jParserTest.class.getName());

    private DormMetadata dormMetadata;
    private DormOrigin origin;

    @Before
    public void setUp(){
        origin = new DefaultDormOrigin("DEFAULT");
        dormMetadata = new DefaultDormMetadata("1.0.0", origin);

    }

    @Test
    public void testParseMetaDataProperty(){
            Map<String, String> map = Neo4jParser.parseMetaDataPropertyToMap(dormMetadata);
            assertTrue(map.containsKey("qualifier"));
            assertTrue(map.containsKey("version"));
            assertTrue(map.containsKey("fullQualifier"));

            assertEquals(dormMetadata.getFullQualifier(), map.get("fullQualifier"));
            assertEquals(dormMetadata.getQualifier(), map.get("qualifier"));
            assertEquals(dormMetadata.getVersion(), map.get("version"));
    }
}