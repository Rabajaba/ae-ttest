package com.agileengine.test.xml;

import org.xml.sax.SAXException;

/**
 * Used only as a marker exception, since there is no other way to stop SAX parser other than throw exception
 */
final class StopParsingException extends SAXException {
}
