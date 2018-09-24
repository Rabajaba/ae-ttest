package com.agileengine.test;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.Map;

/**
 * Core class to compare different attributes.
 */
@Getter
@Builder
@ToString
public final class NodeElement {
    @Singular
    private Map<String, String> attributes;
    private String body;
}
