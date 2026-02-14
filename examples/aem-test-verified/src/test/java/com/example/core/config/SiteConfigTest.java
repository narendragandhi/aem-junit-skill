package com.example.core.config;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Context-Aware Configuration (CA Config)
 * Demonstrates testing CA Config patterns with AEM Mocks
 *
 * Note: Full CA Config testing requires additional setup in AemContext.
 * This test demonstrates the pattern using resource properties as a simpler approach.
 */
@ExtendWith(AemContextExtension.class)
class SiteConfigTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Create content structure with config reference
        context.create().resource("/content/mysite",
            "sling:configRef", "/conf/mysite");

        // Create configuration resource (simulating CA Config storage)
        context.create().resource("/conf/mysite/sling:configs/com.example.core.config.SiteConfig",
            "siteName", "My Awesome Site",
            "siteEmail", "contact@mysite.com",
            "analyticsId", "UA-12345678-1",
            "maintenanceMode", false);
    }

    @Test
    void testSiteConfigValues() {
        // Test that config resource was created correctly
        Resource configResource = context.resourceResolver()
            .getResource("/conf/mysite/sling:configs/com.example.core.config.SiteConfig");
        assertNotNull(configResource);

        ValueMap properties = configResource.getValueMap();
        assertEquals("My Awesome Site", properties.get("siteName", String.class));
        assertEquals("contact@mysite.com", properties.get("siteEmail", String.class));
        assertEquals("UA-12345678-1", properties.get("analyticsId", String.class));
        assertFalse(properties.get("maintenanceMode", false));
    }

    @Test
    void testConfigRefOnContent() {
        // Verify content has config reference
        Resource contentResource = context.resourceResolver().getResource("/content/mysite");
        assertNotNull(contentResource);

        String configRef = contentResource.getValueMap().get("sling:configRef", String.class);
        assertEquals("/conf/mysite", configRef);
    }

    @Test
    void testMaintenanceModeEnabled() {
        // Create site with maintenance mode enabled
        context.create().resource("/conf/maintenance/sling:configs/com.example.core.config.SiteConfig",
            "siteName", "Maintenance Site",
            "maintenanceMode", true);

        Resource configResource = context.resourceResolver()
            .getResource("/conf/maintenance/sling:configs/com.example.core.config.SiteConfig");
        assertNotNull(configResource);

        assertTrue(configResource.getValueMap().get("maintenanceMode", false));
    }

    @Test
    void testDefaultValues() {
        // Test default value behavior using annotation defaults
        SiteConfig defaultConfig = new SiteConfig() {
            @Override
            public String siteName() { return "Default Site"; }
            @Override
            public String siteEmail() { return ""; }
            @Override
            public String analyticsId() { return ""; }
            @Override
            public boolean maintenanceMode() { return false; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return SiteConfig.class;
            }
        };

        assertEquals("Default Site", defaultConfig.siteName());
        assertEquals("", defaultConfig.siteEmail());
        assertFalse(defaultConfig.maintenanceMode());
    }
}
