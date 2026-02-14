package com.example.core.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Site Configuration", description = "Global site settings")
public @interface SiteConfig {

    @Property(label = "Site Name")
    String siteName() default "Default Site";

    @Property(label = "Site Email")
    String siteEmail() default "";

    @Property(label = "Analytics ID")
    String analyticsId() default "";

    @Property(label = "Maintenance Mode")
    boolean maintenanceMode() default false;
}
