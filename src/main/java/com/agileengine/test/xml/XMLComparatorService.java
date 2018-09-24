package com.agileengine.test.xml;

import com.agileengine.test.NodeElement;

import java.io.InputStream;
import java.util.Optional;

/**
 * Public service which provide capabilities to seek for some element in one file and find similar elements in other XML files.
 */
public class XMLComparatorService {
    private XMLElementByIdSeekerServiceService xmlElementByIdSeekerService;
    private XMLSimilarElementSeekerService xmlSimilarElementSeekerService;

    // yet we skip question of bean initialization it order to simplify solution.
    // consider this class and bean config
    public XMLComparatorService() {
        this.xmlElementByIdSeekerService = new XMLElementByIdSeekerServiceService();
        this.xmlSimilarElementSeekerService = new XMLSimilarElementSeekerService();
    }

    // theoretically this method may be extracted to interface, but so we won't overkill current solution it's just located in separate class
    public Optional<String> seek(InputStream originalFile, String elementId, InputStream comparedFile) {
        Optional<NodeElement> seek = xmlElementByIdSeekerService.seek(originalFile, elementId);
        if (!seek.isPresent()) {
            throw new RuntimeException("Element " + elementId + " wasn't found in original file. Comparison is impossible to be continued!");
        }
        return xmlSimilarElementSeekerService.seek(seek.get(), comparedFile);
    }
}
