package com.example.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class HeroComponentTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.load().json("/com/example/core/models/HeroComponentTest.json", "/content/mysite/home");
        context.addModelsForClasses(HeroComponent.class);
    }

    @Test
    void testHeroComponentWithAllProperties() {
        Resource resource = context.resourceResolver().getResource("/content/mysite/home/hero");
        assertNotNull(resource);

        ModelFactory modelFactory = context.getService(ModelFactory.class);
        HeroComponent hero = modelFactory.createModel(resource, HeroComponent.class);
        
        assertNotNull(hero);
        assertEquals("Welcome to Our Site", hero.getTitle());
        assertEquals("Discover amazing content and services", hero.getDescription());
        assertEquals("Learn More", hero.getLinkText());
        assertEquals("/content/mysite/about.html", hero.getLinkURL());
        assertTrue(hero.hasContent());
    }

    @Test
    void testHeroComponentWithEmptyTitle() {
        context.load().json("/com/example/core/models/HeroComponentEmpty.json", "/content/mysite/empty");
        Resource resource = context.resourceResolver().getResource("/content/mysite/empty/hero");
        assertNotNull(resource);

        ModelFactory modelFactory = context.getService(ModelFactory.class);
        HeroComponent hero = modelFactory.createModel(resource, HeroComponent.class);
        
        assertNotNull(hero);
        assertEquals("", hero.getTitle());
        assertFalse(hero.hasContent());
    }
}
