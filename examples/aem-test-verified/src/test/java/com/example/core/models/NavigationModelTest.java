package com.example.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for NavigationModel demonstrating:
 * - @Self injection
 * - @ChildResource injection
 * - @ValueMapValue with DefaultInjectionStrategy.OPTIONAL
 */
@ExtendWith(AemContextExtension.class)
class NavigationModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(NavigationModel.class, NavigationItem.class);
    }

    @Test
    void testSelfInjection() {
        // Create navigation resource
        context.create().resource("/content/navigation",
            "sling:resourceType", "mysite/components/navigation",
            "title", "Main Navigation");

        Resource resource = context.resourceResolver().getResource("/content/navigation");
        assertNotNull(resource);

        ModelFactory modelFactory = context.getService(ModelFactory.class);
        NavigationModel model = modelFactory.createModel(resource, NavigationModel.class);

        assertNotNull(model);
        // Test @Self injection - resource path should be available
        assertEquals("/content/navigation", model.getPath());
        assertEquals("Main Navigation", model.getTitle());
    }

    @Test
    void testChildResourceInjection() {
        // Create navigation with child items
        Resource nav = context.create().resource("/content/navigation",
            "sling:resourceType", "mysite/components/navigation",
            "title", "Site Navigation");

        // Create child resources under "items" node
        Resource items = context.create().resource(nav, "items");
        context.create().resource(items, "item1",
            "label", "Home",
            "url", "/content/home");
        context.create().resource(items, "item2",
            "label", "About",
            "url", "/content/about");
        context.create().resource(items, "item3",
            "label", "Contact",
            "url", "/content/contact");

        Resource resource = context.resourceResolver().getResource("/content/navigation");
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        NavigationModel model = modelFactory.createModel(resource, NavigationModel.class);

        assertNotNull(model);
        assertTrue(model.hasItems());
        assertEquals(3, model.getItems().size());

        // Verify child item properties
        NavigationItem firstItem = model.getItems().get(0);
        assertEquals("Home", firstItem.getLabel());
        assertEquals("/content/home", firstItem.getUrl());
    }

    @Test
    void testEmptyChildResource() {
        // Create navigation without items
        context.create().resource("/content/empty-nav",
            "sling:resourceType", "mysite/components/navigation",
            "title", "Empty Navigation");

        Resource resource = context.resourceResolver().getResource("/content/empty-nav");
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        NavigationModel model = modelFactory.createModel(resource, NavigationModel.class);

        assertNotNull(model);
        assertFalse(model.hasItems());
        // With DefaultInjectionStrategy.OPTIONAL, items should be null not throw exception
        assertNull(model.getItems());
    }

    @Test
    void testOptionalInjectionStrategy() {
        // Create minimal resource without title
        context.create().resource("/content/minimal-nav",
            "sling:resourceType", "mysite/components/navigation");

        Resource resource = context.resourceResolver().getResource("/content/minimal-nav");
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        NavigationModel model = modelFactory.createModel(resource, NavigationModel.class);

        // Model should be created even without optional properties
        assertNotNull(model);
        assertNull(model.getTitle());
        assertEquals("/content/minimal-nav", model.getPath());
    }
}
