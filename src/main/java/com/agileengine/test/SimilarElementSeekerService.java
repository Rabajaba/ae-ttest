package com.agileengine.test;

import java.io.InputStream;
import java.util.Optional;

/**
 * Provides ability to seek similar {@link NodeElement} at some file/network data.
 */
public interface SimilarElementSeekerService {
    Optional<String> seek(NodeElement attributesToSeek, InputStream comparedFile);
}
