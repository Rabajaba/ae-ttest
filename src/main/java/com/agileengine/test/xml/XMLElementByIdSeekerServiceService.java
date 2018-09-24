package com.agileengine.test.xml;

import com.agileengine.test.ElementByIdSeekerService;
import com.agileengine.test.NodeElement;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Allows to seek for some element by ID
 */
class XMLElementByIdSeekerServiceService implements ElementByIdSeekerService {
    /**
     * Seeks XML element by a given ID and responds with a set of attributes available and body value.
     * May not find what you seek for.
     */
    @Override
    public Optional<NodeElement> seek(InputStream inputStream, String idToSeek) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser saxParser;
        IdSeekerHandler idSeekerHandler = new IdSeekerHandler(idToSeek);
        try {
            saxParser = factory.newSAXParser();
            factory.setValidating(true);
            saxParser.parse(inputStream, idSeekerHandler);
            // if stop exception wasn't thrown, file is not ok!
            return Optional.empty();
        } catch (StopParsingException ex) {
            NodeElement.NodeElementBuilder builder = NodeElement.builder().body(idSeekerHandler.currentBody);
            for (int i = 0; i < idSeekerHandler.currentAttributes.getLength(); i++) {
                builder.attribute(idSeekerHandler.currentAttributes.getQName(i), idSeekerHandler.currentAttributes.getValue(i));
            }
            return Optional.of(builder.build());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this seeks element by given ID
     */
    @Slf4j
    private static final class IdSeekerHandler extends DefaultHandler {
        private final String idToSeek;

        private Attributes currentAttributes;
        private String currentBody;

        private IdSeekerHandler(String idToSeek) {
            this.idToSeek = idToSeek;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (attributes.getValue("id") != null) {
                if (attributes.getValue("id").equals(idToSeek)) {
                    currentAttributes = attributes;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (currentAttributes == null) {
                // no need to do anything yet, element wasn't found
                return;
            }
            // element was found by id, let's fill in body value
            StringBuilder res = new StringBuilder();
            for (int i = start; i < length; i++) {
                res.append(ch[i]);
            }
            currentBody = res.toString().trim();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (currentBody != null) {
                throw new StopParsingException(); // in order to stop file processing and free up resources faster
            }
        }
    }
}
