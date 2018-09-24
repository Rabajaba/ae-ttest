package com.agileengine.test.xml;

import com.agileengine.test.NodeElement;
import com.agileengine.test.SimilarElementSeekerService;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Seeks for the best occlusion of similar element to given arguments.
 * By similar element consider some element with most attributes are the same. So whatever elemnts has more of same attributes - win.
 * This means this service always scan all the file.
 *
 * Issue in implementation is that to keep XPath and seek for multiple similar nodes we keep all the structure of {@link Node} elements in memory.
 * For very large files this will become a problem.
 */
@Slf4j
class XMLSimilarElementSeekerService implements SimilarElementSeekerService {
    @Override
    public Optional<String> seek(NodeElement attributesToSeek, InputStream comparedFile) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        AttributesSimilarHandler attributesSimilarHandler = new AttributesSimilarHandler(attributesToSeek);
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(comparedFile, attributesSimilarHandler);
            return buildXPath(attributesSimilarHandler.currentBestNode);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<String> buildXPath(Node currentPath) {
        if (currentPath == null) {
            return Optional.empty();
        }
        List<Node> nodes = new ArrayList<>();
        Node currentNode = currentPath;
        do {
            nodes.add(currentNode);
            // let's fix indexNumber, representing the currentXPAth index, because elements should be the same to be accounted as index

            Node nextValue = currentNode.parent;
            if (nextValue != null) {
                int xPathIndex = 0;
                for (int i = 0; i < currentNode.myIndex; i++) {
                    if (nextValue.children.get(i).name.equals(currentNode.name)) {
                        xPathIndex++;
                    }
                }
                currentNode.myIndex = xPathIndex;
            }
            currentNode = nextValue;
        } while (currentNode != null);

        // we have ordered list with path, let's build it
        Collections.reverse(nodes);
        return Optional.of(nodes.stream().map(node -> node.name + "[" + node.myIndex + "]").collect(Collectors.joining(">")));
    }

    private static final class AttributesSimilarHandler extends DefaultHandler {
        // this is what we need to fine
        private final NodeElement originalElement;
        // this is current inc value to seek best option
        private int similarCount = 0;
        // to keep track of current position in tree
        private Stack<Node> pathTracker = new Stack<>();
        // best result
        private int currentBestMatch = 0;
        // result itself
        private Node currentBestNode;

        private AttributesSimilarHandler(NodeElement originalElement) {
            this.originalElement = originalElement;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            Node thisNode = new Node(qName);
            if (!pathTracker.isEmpty()) {
                Node parent = pathTracker.peek();
                parent.children.add(thisNode);
                thisNode.myIndex = parent.children.size() - 1;
                thisNode.parent = parent;
            }
            pathTracker.push(thisNode);
            similarCount = 0;
            // let's compare attributes
            for (int i = 0; i < attributes.getLength(); i++) {
                String comparedValue = originalElement.getAttributes().get(attributes.getQName(i));
                if (comparedValue != null && comparedValue.equals(attributes.getValue(i))) {
                    similarCount++;
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (similarCount == 0) {
                return; // no attributes matched
            }
            StringBuilder res = new StringBuilder();
            for (int i = start; i < length; i++) {
                res.append(ch[i]);
            }
            if (res.toString().trim().equals(originalElement.getBody())) {
                similarCount++;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (similarCount >= 2) {
                if (currentBestMatch < similarCount) {
                    currentBestMatch = similarCount;
                    currentBestNode = pathTracker.pop();
                }
            } else {
                // no winner here, just move on
                pathTracker.pop();
            }
            // need to drop counter when closing tags are incoming
            similarCount = 0;

            // TODO here we may add step to clean up Node tree after element being closed, if nothing was found there, to clean up some RAM
        }
    }

    /**
     * Helper class to represent XML tree to be able to build XPath later on
     */
    private static final class Node {
        private String name;
        private int myIndex = 0;
        private List<Node> children = new ArrayList<>();
        private Node parent;

        private Node(String name) {
            this.name = name;
        }
    }
}
