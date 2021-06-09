/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/artipie/LICENSE.txt
 */
package com.artipie.docker.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Docker client support annotation. Enables {@link DockerClientExtension} for class.
 *
 * @since 0.10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DockerClientExtension.class)
@Inherited
public @interface DockerClientSupport {
}
