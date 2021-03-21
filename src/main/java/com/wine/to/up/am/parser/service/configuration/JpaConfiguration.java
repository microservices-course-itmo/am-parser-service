package com.wine.to.up.am.parser.service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author : SSyrova
 * @since : 20.11.2020, пт
 **/
@Configuration
@EnableJpaRepositories("com.wine.to.up.am.parser.service.repository")
public class JpaConfiguration {

}
