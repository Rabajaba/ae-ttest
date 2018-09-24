package com.agileengine.test;

import java.io.InputStream;
import java.util.Optional;

/**
 * Provides ability to seek for some {@link NodeElement} by given ID at some file/network data.
 */
public interface ElementByIdSeekerService {
    Optional<NodeElement> seek(InputStream inputStream, String idToSeek);
}
