---
name: aem-junit-testing
description: Comprehensive guide for JUnit testing in Adobe Experience Manager (AEM) as a Cloud Service. Use this skill when creating unit tests, integration tests, or mocking AEM components, services, Sling Models, servlets, or any testing-related tasks in AEM Cloud Service. Based on wcm.io AEM Mocks, Apache Sling Mocks, and Adobe AEM Testing Clients.
---

# AEM JUnit Testing

Guide for implementing comprehensive JUnit tests in AEM as a Cloud Service using wcm.io AEM Mocks, Apache Sling Mocks, and Adobe Testing Clients.

## Core Principles

### Testing Pyramid in AEM

1. **Unit Tests** (Base) - Test individual classes using AEM Mocks
2. **Integration Tests** - Test component interactions using AEM Testing Clients
3. **UI Tests** - End-to-end testing using Cypress/Playwright

**Focus on Unit Tests**: The majority of your tests should be unit tests using AEM Mocks. They are fast, reliable, and provide quick feedback.

### Adobe Cloud Manager Integration

> **Required**: Adobe Cloud Manager integrates unit test execution and code coverage reporting into its CI/CD pipeline. Unit tests are mandatory for production deployments.

- Cloud Manager **requires** unit tests to pass for deployment
- Target **minimum 80% code coverage** for production code
- Coverage reports are available in Cloud Manager's Quality Metrics

### Testing Frameworks Stack

| Framework | Purpose | Scope |
|-----------|---------|-------|
| **wcm.io AEM Mock** | Unit testing AEM components, Sling Models | In-memory |
| **Apache Sling Mock** | Unit testing Sling resources, services | In-memory |
| **Adobe AEM Testing Clients** | Integration testing against running AEM | HTTP-based |
| **Mockito** | Mocking dependencies | All levels |
| **JUnit 5** | Test execution framework | All levels |

## Maven Dependencies

### Unit Testing Dependencies (AEM Mocks)

> **Important**: AEM Mocks dependency must be declared BEFORE AEM SDK API in pom.xml to ensure proper classpath resolution.

```xml
<properties>
    <aem.sdk.api>2024.4.16758.20240422T083412Z-240400</aem.sdk.api>
    <aem-mock.version>5.7.0</aem-mock.version>
    <junit.version>5.10.2</junit.version>
    <mockito.version>5.12.0</mockito.version>
</properties>

<dependencies>
    <!-- wcm.io AEM Mock (MUST be before AEM SDK API) -->
    <dependency>
        <groupId>io.wcm</groupId>
        <artifactId>io.wcm.testing.aem-mock.junit5</artifactId>
        <version>${aem-mock.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- AEM SDK API -->
    <dependency>
        <groupId>com.adobe.aem</groupId>
        <artifactId>aem-sdk-api</artifactId>
        <version>${aem.sdk.api}</version>
        <scope>provided</scope>
    </dependency>

    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- javax.inject for Sling Models -->
    <dependency>
        <groupId>javax.inject</groupId>
        <artifactId>javax.inject</artifactId>
        <version>1</version>
        <scope>provided</scope>
    </dependency>

    <!-- Sling Models Implementation (required for testing) -->
    <dependency>
        <groupId>org.apache.sling</groupId>
        <artifactId>org.apache.sling.models.impl</artifactId>
        <version>1.7.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Integration Testing Dependencies (AEM Testing Clients)

```xml
<dependency>
    <groupId>com.adobe.cq</groupId>
    <artifactId>aem-cloud-testing-clients</artifactId>
    <version>1.3.0</version>
    <scope>test</scope>
</dependency>
```

### Java Version Requirements

> **Important**: As of February 2026, AEM as a Cloud Service **requires Java 21** for both build and runtime. Java 11 support was officially removed.

- **AEM Cloud Service (2025.x - 2026.x)**: Java 21 (LTS) - Required
- **Build Environment**: Java 21 (recommended for 50% faster test execution)
- **JUnit 5**: Required for all new tests
- **Mockito**: Version 5.x required for Java 21 compatibility

## Unit Testing with AEM Mock

### Basic AemContext Setup (JUnit 5)

```java
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
class MyModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Load test content from JSON
        context.load().json("/com/example/core/models/MyModelTest.json", "/content/mysite");

        // Register Sling Models
        context.addModelsForClasses(MyModel.class);
    }

    @Test
    void testModelProperties() {
        // Get resource from mock content
        Resource resource = context.resourceResolver().getResource("/content/mysite/page");
        assertNotNull(resource);

        // For Sling Models with complex injection, use ModelFactory
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        MyModel model = modelFactory.createModel(resource, MyModel.class);
        assertNotNull(model);

        // Assert model properties
        assertEquals("Expected Title", model.getTitle());
    }
}
```

> **Note**: For simple Sling Models without complex `@Inject` dependencies, you can use `resource.adaptTo(MyModel.class)`. However, for models with injected services or resources, use `ModelFactory.createModel()` to ensure proper dependency injection.

### Test Content JSON Format

Create test content in `src/test/resources`:

**File: `src/test/resources/com/example/core/models/MyModelTest.json`**

```json
{
    "jcr:primaryType": "cq:Page",
    "page": {
        "jcr:primaryType": "cq:PageContent",
        "jcr:title": "Test Page",
        "sling:resourceType": "mysite/components/page",
        "component": {
            "jcr:primaryType": "nt:unstructured",
            "sling:resourceType": "mysite/components/mycomponent",
            "title": "Expected Title",
            "description": "Test Description"
        }
    }
}
```

### AemContext Setup Options

#### Resource Resolver Types

```java
// No resource resolver - fastest, for pure Java logic tests (no AEM resources)
private final AemContext context = new AemContext();

// With RESOURCERESOLVER_MOCK (fastest, in-memory) - default
private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

// With JCR Mock (full JCR capabilities)
private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

// With JCR Oak (real Jackrabbit Oak, slowest but most accurate)
private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);
```

| Type | Speed | JCR Support | Use Case |
|------|-------|-------------|----------|
| Default (RESOURCERESOLVER_MOCK) | Fastest | No | Simple resource tests |
| `RESOURCEPROVIDER_MOCK` | Fast | No | Multiple providers |
| `JCR_MOCK` | Medium | Full | JCR-specific tests |
| `JCR_OAK` | Slow | Full + Oak | Query tests, complex JCR |

> **Performance Tip**: Use the default `RESOURCERESOLVER_MOCK` for most tests - it's the fastest option. Use `JCR_OAK` only when you need JCR query support. For mixed tests, consider injecting AemContext as a test method parameter for tests that need JCR, keeping faster tests unaffected.

#### Reusable Context Configuration (AppAemContext Pattern)

```java
public final class MyProjectAemContext {

    public static AemContext newContext() {
        return new AemContextBuilder()
            .resourceResolverType(ResourceResolverType.JCR_MOCK)
            .beforeSetUp(context -> {
                // Register common services
                context.registerService(ExternalService.class, new MockExternalService());
            })
            .afterSetUp(context -> {
                // Load common content
                context.load().json("/common-content.json", "/content");
                context.addModelsForPackage("com.example.core.models");
            })
            .build();
    }
}

// Usage in tests
@ExtendWith(AemContextExtension.class)
class MyTest {
    private final AemContext context = MyProjectAemContext.newContext();
}
```

> **Best Practice**: Use non-static AemContext with `@BeforeEach`/`@AfterEach`. Static fields with `@BeforeAll`/`@AfterAll` require ensuring no side effects between tests since all tests share the same context.

#### Context Plugins for Lifecycle Hooks

Context Plugins hook into test lifecycle for custom setup/teardown:

```java
public class MyContextPlugin extends AbstractContextPlugin<AemContextImpl> {

    @Override
    public void beforeSetUp(AemContextImpl context) {
        // Execute before default setup - register services, load common content
        context.registerService(CustomService.class, new MockCustomService());
    }

    @Override
    public void afterSetUp(AemContextImpl context) {
        // Execute after default setup - additional configuration
    }

    @Override
    public void beforeTearDown(AemContextImpl context) {
        // Execute before teardown - cleanup resources
    }
}

// Usage
private final AemContext context = new AemContextBuilder()
    .plugin(new MyContextPlugin())
    .build();
```

#### Parameter Injection for Performance Optimization

Inject AemContext as method parameter for tests requiring JCR, allowing faster resolver types for other tests:

```java
@ExtendWith(AemContextExtension.class)
class MixedPerformanceTest {

    // Fast tests use default resolver
    @Test
    void testPureJavaLogic() {
        // No AEM resources needed - uses fastest resolver
        assertEquals(2, Calculator.add(1, 1));
    }

    // Slower tests request JCR_OAK explicitly
    @Test
    void testWithJcrQuery(AemContext context) {
        // This test explicitly needs JCR - can use different resolver
        context.create().resource("/content/test", "sling:resourceType", "test");
        // Run query tests here
    }
}
```

## Testing Sling Models

### Basic Sling Model Test

```java
@ExtendWith(AemContextExtension.class)
class HeaderModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(HeaderModel.class);
        context.load().json("/header-content.json", "/content/header");
    }

    @Test
    void testGetTitle() {
        context.currentResource("/content/header/component");

        HeaderModel model = context.request().adaptTo(HeaderModel.class);

        assertNotNull(model);
        assertEquals("Welcome", model.getTitle());
    }

    @Test
    void testGetNavigationItems() {
        context.currentResource("/content/header/component");

        HeaderModel model = context.request().adaptTo(HeaderModel.class);

        assertNotNull(model.getNavigationItems());
        assertEquals(3, model.getNavigationItems().size());
    }
}
```

### Testing Models with Injected Services

```java
@Model(adaptables = Resource.class, adapters = ProductModel.class)
public class ProductModelImpl implements ProductModel {

    @OSGiService
    private ProductService productService;

    @ValueMapValue
    private String productId;

    @Override
    public Product getProduct() {
        return productService.getProduct(productId);
    }
}

// Test class
@ExtendWith(AemContextExtension.class)
class ProductModelTest {

    private final AemContext context = new AemContext();

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Register mock service BEFORE loading models
        context.registerService(ProductService.class, productService);
        context.addModelsForClasses(ProductModelImpl.class);

        // Setup mock behavior
        when(productService.getProduct("SKU123"))
            .thenReturn(new Product("SKU123", "Test Product", 99.99));

        // Load test content
        context.create().resource("/content/product",
            "sling:resourceType", "mysite/components/product",
            "productId", "SKU123");
    }

    @Test
    void testGetProduct() {
        Resource resource = context.resourceResolver().getResource("/content/product");
        ProductModel model = resource.adaptTo(ProductModel.class);

        assertNotNull(model);
        Product product = model.getProduct();
        assertEquals("Test Product", product.getName());
        assertEquals(99.99, product.getPrice());
    }
}
```

> **Note on Mockito integration**: The `AemContextExtension` from `io.wcm.testing.mock.aem.junit5` already includes support for Mockito, so you do not need to add `@ExtendWith(MockitoExtension.class)` to your test class. You can directly use the `@Mock` annotation.
```

### Testing Models with Request Attributes

```java
@ExtendWith(AemContextExtension.class)
class SearchResultsModelTest {

    private final AemContext context = new AemContext();

    @Test
    void testSearchQuery() {
        context.addModelsForClasses(SearchResultsModel.class);

        // Set up request with query parameter
        context.request().setQueryString("q=aem+testing");

        // Set current resource
        context.currentResource(context.create().resource("/content/search",
            "sling:resourceType", "mysite/components/search"));

        SearchResultsModel model = context.request().adaptTo(SearchResultsModel.class);

        assertEquals("aem testing", model.getSearchQuery());
    }
}
```

### Testing Models with Advanced Annotations

#### Model with @ChildResource

```java
package com.example.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationModel {

    @ChildResource
    private List<NavigationItem> items;

    public List<NavigationItem> getItems() {
        return items;
    }
}

@Model(adaptables = Resource.class)
class NavigationItem {
    @ValueMapValue
    private String title;
    
    @ValueMapValue
    private String href;

    public String getTitle() { return title; }
    public String getHref() { return href; }
}
```

```java
// Test for @ChildResource
@ExtendWith(AemContextExtension.class)
class NavigationModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(NavigationModel.class, NavigationItem.class);
        
        Resource nav = context.create().resource("/content/navigation",
            "sling:resourceType", "mysite/components/navigation");
        
        context.create().resource(nav, "items",
            "item1", Map.of("title", "Home", "href", "/"),
            "item2", Map.of("title", "About", "href", "/about"));
    }

    @Test
    void testNavigationWithChildResources() {
        Resource resource = context.resourceResolver().getResource("/content/navigation");
        
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        NavigationModel model = modelFactory.createModel(resource, NavigationModel.class);
        
        assertNotNull(model);
        assertNotNull(model.getItems());
        assertEquals(2, model.getItems().size());
        assertEquals("Home", model.getItems().get(0).getTitle());
    }
}
```

#### Model with @Self Injection

```java
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, 
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SelfInjectModel {

    @Self
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    public String getPath() {
        return resource.getPath();
    }

    public String getURL() {
        return request.getRequestURL().toString();
    }
}
```

```java
// Test for @Self injection
@Test
void testSelfInjection() {
    context.addModelsForClasses(SelfInjectModel.class);
    context.currentResource("/content/test");
    
    SelfInjectModel model = context.request().adaptTo(SelfInjectModel.class);
    
    assertNotNull(model);
    assertEquals("/content/test", model.getPath());
}
```

#### Model with @Named and @Optional

```java
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NamedInjectModel {

    @Named("customTitle")
    @ValueMapValue
    private String title;

    @ValueMapValue @Optional
    private String description;

    @ValueMapValue @Optional
    private String missingField;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean hasDescription() { return description != null; }
}
```

```java
// Test for @Named and @Optional
@Test
void testNamedAndOptionalInjection() {
    context.addModelsForClasses(NamedInjectModel.class);
    
    context.create().resource("/content/test",
        "customTitle", "Custom Title",
        "description", "Test Description");
    
    Resource resource = context.resourceResolver().getResource("/content/test");
    NamedInjectModel model = resource.adaptTo(NamedInjectModel.class);
    
    assertNotNull(model);
    assertEquals("Custom Title", model.getTitle());
    assertTrue(model.hasDescription());
    assertNull(model.getMissingField()); // @Optional prevents failure
}
```

#### Model with Default Values

```java
@Model(adaptables = Resource.class)
public class DefaultValueModel {

    @ValueMapValue(defaultValue = "default-title")
    private String title;

    @ValueMapValue(defaultValue = "0")
    private int count;

    @ValueMapValue(defaultValue = "false")
    private boolean enabled;

    public String getTitle() { return title; }
    public int getCount() { return count; }
    public boolean isEnabled() { return enabled; }
}
```

```java
// Test for default values
@Test
void testDefaultValues() {
    context.addModelsForClasses(DefaultValueModel.class);
    
    // Resource without any properties
    context.create().resource("/content/defaults",
        "sling:resourceType", "mysite/components/test");
    
    Resource resource = context.resourceResolver().getResource("/content/defaults");
    DefaultValueModel model = resource.adaptTo(DefaultValueModel.class);
    
    assertEquals("default-title", model.getTitle());
    assertEquals(0, model.getCount());
    assertFalse(model.isEnabled());
}
```

## Testing OSGi Services

### Basic Service Test

```java
@ExtendWith(AemContextExtension.class)
class EmailServiceImplTest {

    private final AemContext context = new AemContext();

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        // Register and activate the service
        emailService = context.registerInjectActivateService(new EmailServiceImpl());
    }

    @Test
    void testSendEmail() {
        boolean result = emailService.sendEmail("test@example.com", "Subject", "Body");
        assertTrue(result);
    }
}
```

### Testing Services with Configuration

```java
@Component(service = CacheService.class)
@Designate(ocd = CacheServiceImpl.Config.class)
public class CacheServiceImpl implements CacheService {

    @ObjectClassDefinition(name = "Cache Service Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Cache TTL (seconds)")
        int cacheTtl() default 3600;

        @AttributeDefinition(name = "Max Cache Size")
        int maxSize() default 1000;
    }

    private int cacheTtl;
    private int maxSize;

    @Activate
    @Modified
    protected void activate(Config config) {
        this.cacheTtl = config.cacheTtl();
        this.maxSize = config.maxSize();
    }
}

// Test class
@ExtendWith(AemContextExtension.class)
class CacheServiceImplTest {

    private final AemContext context = new AemContext();

    @Test
    void testDefaultConfiguration() {
        CacheServiceImpl service = context.registerInjectActivateService(
            new CacheServiceImpl());

        assertEquals(3600, service.getCacheTtl());
        assertEquals(1000, service.getMaxSize());
    }

    @Test
    void testCustomConfiguration() {
        CacheServiceImpl service = context.registerInjectActivateService(
            new CacheServiceImpl(),
            "cacheTtl", 7200,
            "maxSize", 500);

        assertEquals(7200, service.getCacheTtl());
        assertEquals(500, service.getMaxSize());
    }
}
```

### Testing Services with Dependencies

```java
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ContentSyncServiceImplTest {

    private final AemContext context = new AemContext();

    @Mock
    private Replicator replicator;

    @Mock
    private Externalizer externalizer;

    private ContentSyncServiceImpl contentSyncService;

    @BeforeEach
    void setUp() {
        // Register mock dependencies
        context.registerService(Replicator.class, replicator);
        context.registerService(Externalizer.class, externalizer);

        // Register and activate the service under test
        contentSyncService = context.registerInjectActivateService(
            new ContentSyncServiceImpl());
    }

    @Test
    void testSyncContent() throws ReplicationException {
        String contentPath = "/content/mysite/page";

        contentSyncService.syncContent(contentPath);

        verify(replicator).replicate(any(), eq(ReplicationActionType.ACTIVATE), eq(contentPath));
    }
}
```

## Testing Pages and Components

### Creating Mock Pages

```java
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class PageHelperTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Create page structure using PageManager
        context.create().page("/content/mysite", "mysite/templates/homepage", "My Site");
        context.create().page("/content/mysite/en", "mysite/templates/page", "English");
        context.create().page("/content/mysite/en/about", "mysite/templates/page", "About Us");
    }

    @Test
    void testGetChildPages() {
        Page rootPage = context.pageManager().getPage("/content/mysite/en");

        Iterator<Page> children = rootPage.listChildren();
        List<Page> childList = new ArrayList<>();
        children.forEachRemaining(childList::add);

        assertEquals(1, childList.size());
        assertEquals("About Us", childList.get(0).getTitle());
    }

    @Test
    void testPageProperties() {
        Page page = context.pageManager().getPage("/content/mysite/en/about");

        assertNotNull(page);
        assertEquals("About Us", page.getTitle());
        assertEquals("mysite/templates/page", page.getTemplate().getPath());
    }
}
```

### Testing Component Dialogs and Content Policies

```java
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class HeroComponentTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(HeroModel.class);

        // Define content policy
        context.contentPolicyMapping("mysite/components/hero",
            "titleType", "h1",
            "showSubtitle", true,
            "maxImageWidth", 1920);

        // Create component resource
        context.create().resource("/content/page/jcr:content/hero",
            "sling:resourceType", "mysite/components/hero",
            "title", "Welcome to AEM",
            "subtitle", "Learn testing best practices");
    }

    @Test
    void testHeroWithContentPolicy() {
        context.currentResource("/content/page/jcr:content/hero");
        HeroModel model = context.request().adaptTo(HeroModel.class);

        assertEquals("h1", model.getTitleType());
        assertTrue(model.isShowSubtitle());
        assertEquals(1920, model.getMaxImageWidth());
    }
}
```

## Testing DAM Assets

### Creating Mock Assets

```java
@ExtendWith(AemContextExtension.class)
class AssetHelperTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Create asset with metadata
        context.create().asset("/content/dam/images/hero.jpg", 1920, 1080, "image/jpeg",
            "dc:title", "Hero Image",
            "dc:description", "Main hero image for homepage",
            "dam:Fileformat", "JPEG");
    }

    @Test
    void testAssetMetadata() {
        Resource assetResource = context.resourceResolver()
            .getResource("/content/dam/images/hero.jpg");
        Asset asset = assetResource.adaptTo(Asset.class);

        assertNotNull(asset);
        assertEquals("Hero Image", asset.getMetadataValue("dc:title"));
        assertEquals("image/jpeg", asset.getMimeType());
    }

    @Test
    void testAssetRenditions() {
        Asset asset = context.resourceResolver()
            .getResource("/content/dam/images/hero.jpg")
            .adaptTo(Asset.class);

        Rendition original = asset.getOriginal();
        assertNotNull(original);
        assertTrue(original.getSize() > 0);
    }
}
```

### Testing Asset-Related Models

```java
@ExtendWith(AemContextExtension.class)
class ImageModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(ImageModel.class);

        // Create asset
        context.create().asset("/content/dam/images/test.png", 800, 600, "image/png",
            "dc:title", "Test Image");

        // Create image component referencing asset
        context.create().resource("/content/page/jcr:content/image",
            "sling:resourceType", "mysite/components/image",
            "fileReference", "/content/dam/images/test.png",
            "alt", "Test image alt text");
    }

    @Test
    void testImageModel() {
        context.currentResource("/content/page/jcr:content/image");
        ImageModel model = context.request().adaptTo(ImageModel.class);

        assertNotNull(model);
        assertEquals("/content/dam/images/test.png", model.getFileReference());
        assertEquals("Test image alt text", model.getAlt());
        assertEquals("Test Image", model.getAssetTitle());
    }
}
```

## Testing Servlets

### Testing SlingAllMethodsServlet

```java
@Component(service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/mysite/search",
        "sling.servlet.methods=GET"
    })
public class SearchServlet extends SlingAllMethodsServlet {

    @Reference
    private SearchService searchService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");
        if (StringUtils.isBlank(query)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query parameter required");
            return;
        }

        List<SearchResult> results = searchService.search(query);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(results));
    }
}

// Test class
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class SearchServletTest {

    private final AemContext context = new AemContext();

    @Mock
    private SearchService searchService;

    private SearchServlet servlet;

    @BeforeEach
    void setUp() {
        context.registerService(SearchService.class, searchService);
        servlet = context.registerInjectActivateService(new SearchServlet());
    }

    @Test
    void testSuccessfulSearch() throws Exception {
        // Setup mock results
        List<SearchResult> mockResults = Arrays.asList(
            new SearchResult("Page 1", "/content/page1"),
            new SearchResult("Page 2", "/content/page2"));
        when(searchService.search("test")).thenReturn(mockResults);

        // Setup request
        context.request().setQueryString("q=test");

        // Execute servlet
        servlet.doGet(context.request(), context.response());

        // Verify response
        assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());
        assertEquals("application/json", context.response().getContentType());

        String responseBody = context.response().getOutputAsString();
        assertTrue(responseBody.contains("Page 1"));
    }

    @Test
    void testMissingQueryParameter() throws Exception {
        servlet.doGet(context.request(), context.response());

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());
    }
}
```

### Testing POST Servlets with Form Data

```java
@ExtendWith(AemContextExtension.class)
class FormSubmitServletTest {

    private final AemContext context = new AemContext();

    private FormSubmitServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = context.registerInjectActivateService(new FormSubmitServlet());
    }

    @Test
    void testFormSubmission() throws Exception {
        // Setup form parameters
        context.request().setParameterMap(Map.of(
            "name", "John Doe",
            "email", "john@example.com",
            "message", "Hello World"
        ));
        context.request().setMethod("POST");

        servlet.doPost(context.request(), context.response());

        assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());
    }
}
```

## Testing Workflow Processes

### Testing Custom Workflow Process

```java
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class AssetApprovalWorkflowTest {

    private final AemContext context = new AemContext();

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private Workflow workflow;

    @Mock
    private MetaDataMap workflowMetaData;

    private AssetApprovalProcess process;

    @BeforeEach
    void setUp() {
        // Create test asset
        context.create().asset("/content/dam/pending/image.jpg", 100, 100, "image/jpeg");

        // Setup workflow mocks
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn("/content/dam/pending/image.jpg");
        when(workflowData.getPayloadType()).thenReturn("JCR_PATH");
        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getMetaDataMap()).thenReturn(workflowMetaData);
        when(workflowSession.adaptTo(ResourceResolver.class))
            .thenReturn(context.resourceResolver());

        process = new AssetApprovalProcess();
    }

    @Test
    void testApproveAsset() throws WorkflowException {
        MetaDataMap processArgs = new SimpleMetaDataMap();
        processArgs.put("PROCESS_ARGS", "action=approve");

        process.execute(workItem, workflowSession, processArgs);

        // Verify asset was moved to approved folder
        Resource approvedAsset = context.resourceResolver()
            .getResource("/content/dam/approved/image.jpg");
        assertNotNull(approvedAsset);
    }
}
```

## Testing Schedulers

### Testing Sling Scheduler Jobs

```java
@Component(service = Runnable.class,
    property = {
        "scheduler.expression=0 0 2 * * ?",
        "scheduler.concurrent=false"
    })
public class ContentCleanupScheduler implements Runnable {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private CleanupService cleanupService;

    @Override
    public void run() {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, "cleanup-service"))) {
            cleanupService.cleanupExpiredContent(resolver);
        } catch (LoginException e) {
            LOG.error("Failed to obtain service resolver", e);
        }
    }
}

// Test class
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ContentCleanupSchedulerTest {

    private final AemContext context = new AemContext();

    @Mock
    private CleanupService cleanupService;

    @BeforeEach
    void setUp() {
        // Register service user mapping
        context.registerService(CleanupService.class, cleanupService);
    }

    @Test
    void testSchedulerExecution() {
        // Register scheduler with injected dependencies
        ContentCleanupScheduler scheduler = context.registerInjectActivateService(
            new ContentCleanupScheduler());

        // Execute the scheduler manually
        scheduler.run();

        // Verify cleanup was called
        verify(cleanupService).cleanupExpiredContent(any(ResourceResolver.class));
    }
}
```

## Testing Event Handlers

### Testing Resource Event Handler

```java
@Component(service = ResourceChangeListener.class,
    property = {
        ResourceChangeListener.PATHS + "=/content/mysite",
        ResourceChangeListener.CHANGES + "=ADDED",
        ResourceChangeListener.CHANGES + "=CHANGED"
    })
public class ContentChangeHandler implements ResourceChangeListener {

    @Reference
    private NotificationService notificationService;

    @Override
    public void onChange(List<ResourceChange> changes) {
        for (ResourceChange change : changes) {
            if (change.getPath().contains("/jcr:content")) {
                notificationService.notifyContentChange(change.getPath());
            }
        }
    }
}

// Test class - simulate the handler directly
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ContentChangeHandlerTest {

    private final AemContext context = new AemContext();

    @Mock
    private NotificationService notificationService;

    private ContentChangeHandler handler;

    @BeforeEach
    void setUp() {
        context.registerService(NotificationService.class, notificationService);
        handler = context.registerInjectActivateService(new ContentChangeHandler());
    }

    @Test
    void testContentChangeNotification() {
        // Simulate resource change
        ResourceChange change = mock(ResourceChange.class);
        when(change.getPath()).thenReturn("/content/mysite/page/jcr:content");
        when(change.getType()).thenReturn(ChangeType.CHANGED);

        handler.onChange(List.of(change));

        verify(notificationService).notifyContentChange("/content/mysite/page/jcr:content");
    }

    @Test
    void testNonContentChangeIgnored() {
        ResourceChange change = mock(ResourceChange.class);
        when(change.getPath()).thenReturn("/content/mysite/dam/image.jpg");

        handler.onChange(List.of(change));

        verify(notificationService, never()).notifyContentChange(any());
    }
}
```

## Integration Testing with AEM Testing Clients

### Setting Up Integration Tests Module

**Directory Structure:**
```
it.tests/
├── pom.xml
└── src/test/java/
    └── com/example/it/
        ├── AuthorPublishTestBase.java
        ├── PageCreationIT.java
        └── ContentReplicationIT.java
```

### Base Integration Test Class (JUnit 5)

```java
package com.example.it;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.adobe.cq.testing.junit.rules.CQPublishClassRule;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

public abstract class AuthorPublishTestBase {

    @RegisterExtension
    public static final CQAuthorClassRule author = new CQAuthorClassRule();

    @RegisterExtension
    public static final CQPublishClassRule publish = new CQPublishClassRule();

    protected static CQClient authorClient;
    protected static CQClient publishClient;

    @BeforeAll
    static void beforeAll() {
        authorClient = author.getAdminClient(CQClient.class);
        publishClient = publish.getAdminClient(CQClient.class);
    }
}
```

### Page Creation Integration Test

```java
package com.example.it;

import com.adobe.cq.testing.client.CQClient;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageCreationIT extends AuthorPublishTestBase {

    private String testPagePath;

    @BeforeEach
    public void setup() throws ClientException {
        String pageName = "test-page-" + UUID.randomUUID();
        testPagePath = authorClient.createPage(pageName, "Test Page", "/content/mysite", "/conf/mysite/settings/wcm/templates/page").getSlingPath();
    }

    @AfterEach
    public void cleanup() throws ClientException {
        if (testPagePath != null) {
            authorClient.deletePageWithRetry(testPagePath, true, false, 2000, 500);
        }
    }

    @Test
    public void testCreatePage() throws ClientException {
        assertTrue(authorClient.exists(testPagePath));
    }

    @Test
    public void testPageReplication() throws ClientException {
        authorClient.replicate(testPagePath);
        publishClient.waitExists(testPagePath, 10000, 500);

        // Verify on publish
        assertTrue(publishClient.exists(testPagePath));

        // Cleanup
        authorClient.deactivate(testPagePath);
        publishClient.waitExists(testPagePath, false, 10000, 500);
    }
}
```
```

### Asset Upload Integration Test

```java
package com.example.it;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.assets.AssetClient;
import org.apache.sling.testing.clients.ClientException;
import org.junit.Test;

import java.io.InputStream;

public class AssetUploadIT extends AuthorPublishTestBase {

    @Test
    public void testAssetUpload() throws ClientException {
        AssetClient assetClient = getAuthorClient().adaptTo(AssetClient.class);

        // Upload asset from classpath
        InputStream imageStream = getClass().getResourceAsStream("/test-image.jpg");
        assetClient.uploadAsset("/content/dam/test-uploads/test-image.jpg",
                                imageStream, "image/jpeg");

        // Verify asset exists
        assertTrue(assetClient.exists("/content/dam/test-uploads/test-image.jpg"));

        // Verify renditions were created
        assertTrue(assetClient.exists(
            "/content/dam/test-uploads/test-image.jpg/jcr:content/renditions/cq5dam.thumbnail.140.100.png"));

        // Cleanup
        assetClient.deletePath("/content/dam/test-uploads/test-image.jpg");
    }
}
```

## Testing Best Practices

### Coverage Target

> **Adobe Recommendation**: Target minimum **80% code coverage** for production code. Cloud Manager reports coverage metrics in the Quality Metrics dashboard.

### Test Organization

```
core/
└── src/
    ├── main/java/
    │   └── com/example/core/
    │       ├── models/
    │       │   └── HeaderModel.java
    │       ├── services/
    │       │   └── SearchService.java
    │       └── servlets/
    │           └── SearchServlet.java
    └── test/
        ├── java/
        │   └── com/example/core/
        │       ├── models/
        │       │   └── HeaderModelTest.java
        │       ├── services/
        │       │   └── SearchServiceTest.java
        │       ├── servlets/
        │       │   └── SearchServletTest.java
        │       └── testcontext/
        │           └── MyProjectAemContext.java
        └── resources/
            └── com/example/core/
                ├── models/
                │   └── HeaderModelTest.json
                └── services/
                    └── SearchServiceTest.json
```

### Naming Conventions

- Test classes: `{ClassName}Test.java`
- Integration tests: `{Feature}IT.java`
- Test methods: `test{MethodName}{Scenario}` or `should{ExpectedBehavior}When{Condition}`
- JSON test data: `{TestClassName}.json`

### Assertions Best Practices

```java
// Use descriptive assertions
assertEquals("Title should match the configured value",
    "Expected Title", model.getTitle());

// Use assertAll for multiple related assertions
assertAll("Hero component properties",
    () -> assertEquals("h1", model.getTitleType()),
    () -> assertTrue(model.isShowSubtitle()),
    () -> assertNotNull(model.getBackgroundImage())
);

// Use assertThrows for exception testing
assertThrows(IllegalArgumentException.class, () -> {
    service.processWithInvalidInput(null);
});
```

### Mockito Best Practices

```java
// Use @Mock annotation for clarity
@Mock
private ExternalService externalService;

// Use lenient() for optional stubbing
lenient().when(service.optionalMethod()).thenReturn("value");

// Verify method calls
verify(service, times(1)).processData(any());
verify(service, never()).dangerousMethod();

// Use ArgumentCaptor for complex verification
ArgumentCaptor<ContentEvent> captor = ArgumentCaptor.forClass(ContentEvent.class);
verify(eventService).publishEvent(captor.capture());
assertEquals("/content/page", captor.getValue().getPath());
```

### Test Data Management

```java
// Reuse common test data
@BeforeAll
static void setupContent(AemContext context) {
    context.load().json("/common/site-structure.json", "/content/mysite");
    context.load().json("/common/dam-assets.json", "/content/dam/mysite");
}

// Use builders for complex objects
Page testPage = context.create().page("/content/mysite/test")
    .title("Test Page")
    .template("/conf/mysite/templates/page")
    .property("hideInNav", true)
    .build();

// Clean up after tests when using JCR_OAK
@AfterEach
void cleanup() {
    context.resourceResolver().delete(context.resourceResolver()
        .getResource("/content/mysite/temp"));
    context.resourceResolver().commit();
}
```

## Common Testing Patterns

### Testing Externalizer URLs

```java
@ExtendWith(AemContextExtension.class)
class LinkHelperTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Mock Externalizer configuration
        MockExternalizer externalizer = context.getService(Externalizer.class);
        // Configure domain mappings in AemContext setup
    }

    @Test
    void testExternalizeLink() {
        context.runMode("publish");

        LinkHelper helper = context.registerInjectActivateService(new LinkHelper());

        String externalUrl = helper.getExternalUrl("/content/mysite/page");
        assertTrue(externalUrl.startsWith("https://"));
    }
}
```

### Testing i18n/Translations

```java
@ExtendWith(AemContextExtension.class)
class I18nHelperTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Load i18n dictionaries
        context.load().json("/i18n/en.json", "/apps/mysite/i18n/en");
        context.load().json("/i18n/de.json", "/apps/mysite/i18n/de");
    }

    @Test
    void testTranslation() {
        // Set request locale
        context.request().setAttribute("sling-request-locale", Locale.GERMAN);

        I18nHelper helper = context.request().adaptTo(I18nHelper.class);

        assertEquals("Willkommen", helper.get("welcome"));
    }
}
```

### Testing with WCM Mode

```java
@ExtendWith(AemContextExtension.class)
class EditableComponentTest {

    private final AemContext context = new AemContext();

    @Test
    void testAuthorMode() {
        context.request().setAttribute(WCMMode.class.getName(), WCMMode.EDIT);

        context.currentResource("/content/page/jcr:content/component");
        EditableModel model = context.request().adaptTo(EditableModel.class);

        assertTrue(model.isEditMode());
        assertTrue(model.showPlaceholder());
    }

    @Test
    void testPublishMode() {
        context.request().setAttribute(WCMMode.class.getName(), WCMMode.DISABLED);

        context.currentResource("/content/page/jcr:content/component");
        EditableModel model = context.request().adaptTo(EditableModel.class);

        assertFalse(model.isEditMode());
        assertFalse(model.showPlaceholder());
    }
}
```

## Troubleshooting

### Common Issues

**Issue: "No adapter found for type X"**
```java
// Solution: Register adapter factory or model class
context.addModelsForClasses(MyModel.class);
// OR
context.addModelsForPackage("com.example.core.models");
```

**Issue: "Service not found"**
```java
// Solution: Register service before accessing
context.registerService(MyService.class, mockService);
// OR use registerInjectActivateService for real implementations
context.registerInjectActivateService(new MyServiceImpl());
```

**Issue: "Resource resolver is closed"**
```java
// Solution: Use context.resourceResolver() within test methods
// Don't store resolver in @BeforeEach
@Test
void test() {
    ResourceResolver resolver = context.resourceResolver(); // Fresh instance
}
```

**Issue: "JCR query not working"**
```java
// Solution: Use JCR_OAK resource resolver type for query support
private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);
```

**Issue: "Content policy not applied"**
```java
// Solution: Register content policy mapping
context.contentPolicyMapping("mysite/components/hero",
    "property1", "value1",
    "property2", "value2");
```

### Debug Logging

```java
// Enable debug logging for tests
@BeforeAll
static void setupLogging() {
    ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("io.wcm"))
        .setLevel(Level.DEBUG);
}
```

## Running Tests

### Maven Commands

```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=HeaderModelTest

# Run tests with coverage
mvn test jacoco:report

# Run integration tests
mvn verify -Pit.test

# Run integration tests against Cloud Service
mvn verify -Pit.test -Daem.host=https://author-xxx-xxx.adobeaemcloud.com
```

### CI/CD Configuration (Cloud Manager)

```yaml
# .cloudmanager/pipeline.yml
version: 1
pipelines:
  - name: Build and Test
    type: CI_CD
    trigger: ON_COMMIT
    phases:
      - type: BUILD
        steps:
          - type: MAVEN
            goals: clean install
      - type: UNIT_TEST
        steps:
          - type: MAVEN
            goals: test
      - type: INTEGRATION_TEST
        steps:
           - type: MAVEN
            goals: verify -Pit.tests
```

## Testing QueryBuilder

### Testing QueryBuilder Queries

```java
package com.example.core.search;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class QueryBuilderTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.create().page("/content/mysite", "mysite/components/page");
        context.create().page("/content/mysite/page1", "mysite/components/page");
        context.create().page("/content/mysite/page2", "mysite/components/page");
    }

    @Test
    void testSimpleQuery() {
        QueryBuilder queryBuilder = context.getService(QueryBuilder.class);
        Session session = context.resourceResolver().adaptTo(Session.class);

        Map<String, String> predicates = new HashMap<>();
        predicates.put("path", "/content/mysite");
        predicates.put("type", "cq:Page");

        PredicateGroup predicateGroup = PredicateGroup.create(predicates);
        Query query = queryBuilder.createQuery(predicateGroup, session);
        SearchResult result = query.getResult();

        assertNotNull(result);
        assertEquals(2, result.getHits().size());
    }

    @Test
    void testFullTextSearch() {
        context.create().page("/content/search-test", "mysite/components/page");
        Resource page = context.resourceResolver().getResource("/content/search-test");
        context.create().resource(page, "jcr:content", "jcr:title", "Test Article");

        QueryBuilder queryBuilder = context.getService(QueryBuilder.class);
        Session session = context.resourceResolver().adaptTo(Session.class);

        Map<String, String> predicates = new HashMap<>();
        predicates.put("path", "/content");
        predicates.put("type", "cq:Page");
        predicates.put("fulltext", "Test");

        PredicateGroup predicateGroup = PredicateGroup.create(predicates);
        Query query = queryBuilder.createQuery(predicateGroup, session);
        SearchResult result = query.getResult();

        assertNotNull(result);
    }
}
```

> **Note**: While the QueryBuilder service can still accept a `Map`, using `PredicateGroup` is the recommended approach for building queries programmatically as it is more type-safe and less error-prone.

## Testing Content Fragments

### Testing Content Fragment Models

```java
package com.example.core.models;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class ContentFragmentModelTest {

    private final AemContext context = new AemContext();

    @Test
    void testContentFragmentWithElements() {
        // Create a content fragment resource with elements in the jcr:content node
        Resource fragment = context.create().resource(
            "/content/dam/fragments/article",
            "jcr:primaryType", "dam:Asset",
            "jcr:content", "jcr:primaryType=dam:AssetContent",
            "jcr:content/metadata", "jcr:primaryType=nt:unstructured",
            "jcr:content/data/master/headline", "Breaking News",
            "jcr:content/data/master/body", "Article body content"
        );

        ContentFragment cf = fragment.adaptTo(ContentFragment.class);
        assertNotNull(cf);

        // Assert elements from the content fragment
        assertEquals("Breaking News", cf.getElement("headline").getValue(String.class));
        assertEquals("Article body content", cf.getElement("body").getValue(String.class));
    }
}
```

## Testing Context-Aware Configurations

### Testing Configurations with AemContext

```java
package com.example.core.config;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class SiteConfigTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Define the context-aware configuration using AEM Mocks
        context.create().congaConfiguration("/conf/mysite/sling:configs/com.example.core.config.SiteConfig",
            "siteName", "My Site",
            "siteEmail", "info@mysite.com",
            "maintenanceMode", false);
            
        // Set the configuration path for a content tree
        context.contentPolicyMapping("com.example.core.config.SiteConfig", 
            "sling:configRef", "/conf/mysite");
            
        context.create().resource("/content/mysite");
    }

    @Test
    void testGetConfiguration() {
        // Get the resource for which the context-aware configuration is defined
        Resource resource = context.resourceResolver().getResource("/content/mysite");
        
        // Adapt the resource to the configuration interface
        SiteConfig config = resource.adaptTo(SiteConfig.class);
        
        assertNotNull(config);
        assertEquals("My Site", config.siteName());
        assertEquals("info@mysite.com", config.siteEmail());
        assertFalse(config.maintenanceMode());
    }
}
```

### SiteConfig Interface

```java
package com.example.core.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Site Configuration", description = "Global site settings")
public @interface SiteConfig {

    @Property(label = "Site Name")
    String siteName();

    @Property(label = "Site Email")
    String siteEmail();

    @Property(label = "Maintenance Mode", description = "Enable to show a maintenance page")
    boolean maintenanceMode() default false;
}
```
```

## Testing Users, Groups, and ACLs

### Testing User Management

```java
package com.example.core.security;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.RepositoryException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class UserManagementTest {

    private final AemContext context = new AemContext();

    private UserManager userManager;

    @BeforeEach
    void setUp() throws RepositoryException {
        userManager = context.resourceResolver().adaptTo(UserManager.class);
        
        Group group = (Group) userManager.createGroup("content-authors");
        group.setProperty("profile/description", "Content Authors Group");
        
        User testUser = (User) userManager.createUser("testuser", "password");
        group.addMember(testUser);
    }

    @Test
    void testCreateUser() throws RepositoryException {
        User newUser = (User) userManager.createUser("newuser", "testpassword");
        
        assertNotNull(newUser);
        assertEquals("newuser", newUser.getID());
        assertFalse(newUser.isAdmin());
    }

    @Test
    void testCreateGroup() throws RepositoryException {
        Group administrators = (Group) userManager.createGroup("administrators");
        
        assertNotNull(administrators);
        assertEquals("administrators", administrators.getID());
    }

    @Test
    void testGroupMembership() throws RepositoryException {
        Authorizable testUser = userManager.getAuthorizable("testuser");
        assertNotNull(testUser);
        
        Iterator<Group> groups = testUser.declaredGroups();
        assertTrue(groups.hasNext());
        
        Group group = groups.next();
        assertEquals("content-authors", group.getID());
    }

    @Test
    void testRemoveUser() throws RepositoryException {
        Authorizable user = userManager.getAuthorizable("testuser");
        assertNotNull(user);
        
        user.remove();
        
        Authorizable removedUser = userManager.getAuthorizable("testuser");
        assertNull(removedUser);
    }

    @Test
    void testAddMemberToGroup() throws RepositoryException {
        Group group = (Group) userManager.getAuthorizable("content-authors");
        User newUser = (User) userManager.createUser("newmember", "password");
        
        group.addMember(newUser);
        
        Iterator<Authorizable> members = group.getMembers();
        boolean found = false;
        while (members.hasNext()) {
            if ("newmember".equals(members.next().getID())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
```

### Testing ACLs and Permissions

```java
package com.example.core.security;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.api.security.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.jcr.MockJcr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import java.security.Principal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ACLTest {

    private final AemContext context = new AemContext();
    private UserManager userManager;
    private PrincipalManager principalManager;

    @BeforeEach
    void setUp() throws RepositoryException {
        userManager = context.resourceResolver().adaptTo(UserManager.class);
        principalManager = context.resourceResolver().adaptTo(PrincipalManager.class);

        // Create a test user
        userManager.createUser("testuser", "password");
    }

    @Test
    void testSetAndCheckPermission() throws RepositoryException, LoginException {
        // Create a resource to secure
        context.create().resource("/content/restricted");
        Session adminSession = context.resourceResolver().adaptTo(Session.class);
        AccessControlManager accessControlManager = adminSession.getAccessControlManager();

        // Get ACL for the path
        AccessControlList acl;
        AccessControlPolicy[] policies = accessControlManager.getPolicies("/content/restricted");
        if (policies.length > 0) {
            acl = (AccessControlList) policies[0];
        } else {
            acl = (AccessControlList) accessControlManager.getApplicablePolicies("/content/restricted").nextAccessControlPolicy();
        }

        // Add a deny entry for jcr:read
        Principal principal = principalManager.getPrincipal("testuser");
        Privilege[] privileges = { accessControlManager.privilegeFromName(Privilege.JCR_READ) };
        acl.addAccessControlEntry(principal, privileges, false);

        // Apply the ACL
        accessControlManager.setPolicy("/content/restricted", acl);
        adminSession.save();

        // Get a resource resolver for the test user
        ResourceResolverFactory rrf = context.getService(ResourceResolverFactory.class);
        try (ResourceResolver userResolver = rrf.getResourceResolver(Collections.singletonMap(ResourceResolverFactory.USER, "testuser"))) {
            // Check for the resource, it should be null as the user can't read it
            assertNull(userResolver.getResource("/content/restricted"));
        }
    }
}
```

### Testing Resource Resolver with User Context

```java
package com.example.core.security;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.RepositoryException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ImpersonationTest {

    private final AemContext context = new AemContext();
    private UserManager userManager;

    @BeforeEach
    void setUp() throws RepositoryException {
        userManager = context.resourceResolver().adaptTo(UserManager.class);
        userManager.createUser("testuser", "password");
    }

    @Test
    void testImpersonateAsUser() throws LoginException {
        ResourceResolverFactory rrf = context.getService(ResourceResolverFactory.class);
        
        try (ResourceResolver adminResolver = rrf.getResourceResolver(null);
             ResourceResolver userResolver = rrf.getResourceResolver(Collections.singletonMap(ResourceResolverFactory.USER, "testuser"))) {

            assertEquals("admin", adminResolver.getUserID());
            assertEquals("testuser", userResolver.getUserID());
        }
    }

    @Test
    void testServiceUserResolver() {
        // In a real test, you would have a service user mapping configured
        // For now, we just get the default service resolver from the context
        ResourceResolver serviceResolver = context.getServiceResourceResolver();

        assertNotNull(serviceResolver);
        assertTrue(serviceResolver.isLive());

        // The user id of the service resolver depends on the mock setup
        // and is typically null or a generated name
        assertNotNull(serviceResolver.getUserID());
    }
}
```
```

## Reference Documentation

For detailed API documentation, see [references/testing-api-reference.md](references/testing-api-reference.md)

For code examples and templates, see [scripts/](scripts/)

## Additional Resources

- **wcm.io AEM Mock**: https://wcm.io/testing/aem-mock/
- **Apache Sling Mock**: https://sling.apache.org/documentation/development/sling-mock.html
- **Adobe AEM Testing Clients**: https://github.com/adobe/aem-testing-clients
- **JUnit 5 User Guide**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/
- **AEM Cloud Service Testing**: https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/testing.html
