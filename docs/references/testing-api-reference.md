# AEM JUnit Testing API Reference

Comprehensive reference for AEM testing frameworks and APIs.

## Package Structure

### wcm.io AEM Mock Packages
- `io.wcm.testing.mock.aem` - Core AEM mock implementations
- `io.wcm.testing.mock.aem.junit5` - JUnit 5 extensions
- `io.wcm.testing.mock.aem.junit4` - JUnit 4 rules

### Apache Sling Mock Packages
- `org.apache.sling.testing.mock.sling` - Core Sling mock implementations
- `org.apache.sling.testing.mock.sling.junit5` - JUnit 5 extensions
- `org.apache.sling.testing.mock.osgi` - OSGi mock implementations
- `org.apache.sling.testing.mock.jcr` - JCR mock implementations

### Adobe AEM Testing Clients Packages
- `com.adobe.cq.testing.client` - HTTP testing clients
- `com.adobe.cq.testing.junit.rules` - JUnit rules for IT tests

## AemContext API

### Class: `io.wcm.testing.mock.aem.junit5.AemContext`

The main context for AEM unit testing, extending SlingContext with AEM-specific capabilities.

**Constructors:**

```java
// Default with RESOURCERESOLVER_MOCK
AemContext()

// With specific resource resolver type
AemContext(ResourceResolverType type)

// From builder
new AemContextBuilder()
    .resourceResolverType(ResourceResolverType.JCR_MOCK)
    .build()
```

**Key Methods:**

```java
// Resource Resolver
ResourceResolver resourceResolver()

// Page Manager
PageManager pageManager()

// Asset Manager
AssetManager assetManager()

// Tag Manager
TagManager tagManager()

// Content Policy
ContentPolicyMapping contentPolicyMapping(String resourceType, Object... properties)

// Content Loader
ContentLoader load()

// Content Builder
ContentBuilder create()

// Request/Response
MockSlingHttpServletRequest request()
MockSlingHttpServletResponse response()

// Current Resource
void currentResource(String path)
void currentResource(Resource resource)
Resource currentResource()

// Current Page
void currentPage(String path)
void currentPage(Page page)
Page currentPage()

// Run Modes
void runMode(String... runModes)

// Service Registration
<T> T registerService(Class<T> serviceClass, T service)
<T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties)
<T> T registerInjectActivateService(T service, Object... properties)

// Service Retrieval
<T> T getService(Class<T> serviceClass)

// Sling Models
void addModelsForPackage(String... packages)
void addModelsForClasses(Class<?>... classes)

// Bundle Context
BundleContext bundleContext()
```

## ContentLoader API

### Class: `org.apache.sling.testing.mock.sling.loader.ContentLoader`

Loads test content from various sources into the mock repository.

**Methods:**

```java
// Load from JSON
void json(String classpathResource, String destinationPath)

// Load from JSON with options
void json(String classpathResource, String destinationPath, ContentTypeOptions options)

// Load from binary file
void binaryFile(String classpathResource, String destinationPath)
void binaryFile(String classpathResource, String destinationPath, String mimeType)

// Load from InputStream
void binaryFile(InputStream inputStream, String destinationPath, String mimeType)
```

**JSON Format:**

```json
{
    "jcr:primaryType": "nt:unstructured",
    "propertyString": "value",
    "propertyLong": 123,
    "propertyBoolean": true,
    "propertyDouble": 1.23,
    "propertyArray": ["value1", "value2"],
    "childNode": {
        "jcr:primaryType": "nt:unstructured",
        "childProperty": "childValue"
    }
}
```

## ContentBuilder API

### Class: `org.apache.sling.testing.mock.sling.builder.ContentBuilder`

Programmatically creates test content.

**Methods:**

```java
// Create resource
Resource resource(String path, Object... properties)
Resource resource(String path, Map<String, Object> properties)

// Create page (AEM specific)
Page page(String path)
Page page(String path, String template)
Page page(String path, String template, String title)
Page page(String path, String template, String title, Map<String, Object> properties)

// Create asset (AEM specific)
Asset asset(String path, int width, int height, String mimeType)
Asset asset(String path, int width, int height, String mimeType, Map<String, Object> properties)
Asset asset(String path, InputStream inputStream, String mimeType)

// Create tag (AEM specific)
Tag tag(String tagId)
Tag tag(String tagId, Map<String, Object> properties)
```

## MockSlingHttpServletRequest API

### Class: `org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest`

Mock implementation of SlingHttpServletRequest.

**Key Methods:**

```java
// URL and Path Configuration
void setQueryString(String queryString)
void setServerName(String serverName)
void setServerPort(int port)
void setContextPath(String contextPath)
void setScheme(String scheme)
void setMethod(String method)

// Parameters
void setParameterMap(Map<String, Object> parameters)
void addRequestParameter(String name, String value)

// Headers
void addHeader(String name, String value)
void setHeader(String name, String value)

// Cookies
void addCookie(Cookie cookie)

// Attributes
void setAttribute(String name, Object value)

// Resource
void setResource(Resource resource)

// Request Path Info
MockRequestPathInfo getRequestPathInfo()

// Locale
void setLocale(Locale locale)
```

## MockSlingHttpServletResponse API

### Class: `org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse`

Mock implementation of SlingHttpServletResponse.

**Key Methods:**

```java
// Status
int getStatus()

// Content Type
String getContentType()

// Character Encoding
String getCharacterEncoding()

// Output
String getOutputAsString()
byte[] getOutput()

// Headers
String getHeader(String name)
Collection<String> getHeaders(String name)
Collection<String> getHeaderNames()

// Cookies
Cookie[] getCookies()
```

## MockRequestPathInfo API

### Class: `org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo`

Mock implementation of RequestPathInfo.

**Methods:**

```java
void setResourcePath(String resourcePath)
void setSelectorString(String selectorString)
void setExtension(String extension)
void setSuffix(String suffix)
void setSuffixResource(Resource suffixResource)
```

## ResourceResolverType Enum

### Enum: `org.apache.sling.testing.mock.sling.ResourceResolverType`

Defines the type of mock resource resolver to use.

**Values:**

| Type | Description | JCR Support | Speed |
|------|-------------|-------------|-------|
| `RESOURCERESOLVER_MOCK` | Simple in-memory mock | No | Fastest |
| `RESOURCEPROVIDER_MOCK` | Multiple provider support | No | Fast |
| `JCR_MOCK` | Full JCR mock (Jackrabbit) | Yes | Medium |
| `JCR_OAK` | Real Oak implementation | Yes + Queries | Slow |
| `NONE` | No mock, requires custom setup | N/A | N/A |

## CQClient API (Integration Testing)

### Class: `com.adobe.cq.testing.client.CQClient`

HTTP client for integration testing against running AEM instances.

**Key Methods:**

```java
// Page Operations
SlingHttpResponse createPage(String name, String title, String parentPath, String template)
SlingHttpResponse deletePath(String path)
boolean exists(String path)

// Content Operations
SlingHttpResponse setPageProperty(String path, String property, String value)
String getPageProperty(String path, String property)

// Replication
SlingHttpResponse replicate(String path)
SlingHttpResponse deactivate(String path)

// User Management
SlingHttpResponse createUser(String userId, String password, String givenName, String familyName)
SlingHttpResponse createGroup(String groupId)
SlingHttpResponse addUserToGroup(String userId, String groupId)

// Workflow
SlingHttpResponse startWorkflow(String modelId, String payloadPath)

// Adapt to specialized client
<T extends SlingClient> T adaptTo(Class<T> type)
```

### Class: `com.adobe.cq.testing.client.assets.AssetClient`

Specialized client for DAM operations.

**Key Methods:**

```java
SlingHttpResponse uploadAsset(String assetPath, InputStream inputStream, String mimeType)
SlingHttpResponse createFolder(String folderPath, String title)
Asset getAsset(String assetPath)
boolean assetExists(String assetPath)
```

## JUnit Rules for Integration Testing

### Class: `com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule`

JUnit class rule that provides author and publish instance configurations.

**Usage:**

```java
@ClassRule
public static CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();
```

**Properties:**

```java
CQRule authorRule  // Access author instance
CQRule publishRule // Access publish instance
```

### Class: `com.adobe.cq.testing.junit.rules.CQRule`

JUnit rule for individual test methods.

**Usage:**

```java
@Rule
public CQRule cqRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);
```

**Methods:**

```java
<T extends SlingClient> T getAdminClient(Class<T> clientClass)
<T extends SlingClient> T getClient(Class<T> clientClass, String user, String password)
```

## OSGi Mock API

### Class: `org.apache.sling.testing.mock.osgi.context.OsgiContextImpl`

Provides OSGi context for testing.

**Key Methods:**

```java
// Service Registration
<T> T registerService(Class<T> serviceClass, T service)
<T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties)

// Service with Injection and Activation
<T> T registerInjectActivateService(T service, Object... configurationProperties)

// Service Retrieval
<T> T getService(Class<T> serviceClass)
<T> T[] getServices(Class<T> serviceClass, String filter)

// Bundle Context
BundleContext bundleContext()
```

## Common Test Annotations

### JUnit 5 Annotations

```java
@ExtendWith(AemContextExtension.class)  // Enable AEM Mock
@ExtendWith(MockitoExtension.class)      // Enable Mockito

@BeforeAll    // Run once before all tests
@AfterAll     // Run once after all tests
@BeforeEach   // Run before each test
@AfterEach    // Run after each test

@Test         // Mark test method
@Disabled     // Skip test
@DisplayName  // Custom test name

@ParameterizedTest
@ValueSource(strings = {"a", "b", "c"})
@CsvSource({"a,1", "b,2"})
```

### Mockito Annotations

```java
@Mock           // Create mock object
@Spy            // Create spy (partial mock)
@InjectMocks    // Inject mocks into tested class
@Captor         // Argument captor
```

## Assertion Methods

### JUnit 5 Assertions

```java
// Basic
assertEquals(expected, actual)
assertNotEquals(unexpected, actual)
assertTrue(condition)
assertFalse(condition)
assertNull(object)
assertNotNull(object)
assertSame(expected, actual)
assertNotSame(unexpected, actual)

// Arrays
assertArrayEquals(expected, actual)

// Exceptions
assertThrows(ExceptionClass.class, () -> { /* code */ })
assertDoesNotThrow(() -> { /* code */ })

// Timeout
assertTimeout(Duration.ofSeconds(5), () -> { /* code */ })

// Multiple Assertions
assertAll("group name",
    () -> assertEquals(1, a),
    () -> assertEquals(2, b),
    () -> assertNotNull(c)
)
```

### Mockito Verification

```java
// Verify method called
verify(mock).method()
verify(mock, times(2)).method()
verify(mock, never()).method()
verify(mock, atLeast(1)).method()
verify(mock, atMost(3)).method()

// Verify with argument matchers
verify(mock).method(any())
verify(mock).method(eq("value"))
verify(mock).method(argThat(arg -> arg.length() > 5))

// Verify order
InOrder inOrder = inOrder(mock1, mock2);
inOrder.verify(mock1).method1();
inOrder.verify(mock2).method2();

// Capture arguments
ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
verify(mock).method(captor.capture());
assertEquals("expected", captor.getValue());
```

## System Properties for Integration Tests

```properties
# AEM Author Instance
sling.it.author.url=http://localhost:4502
sling.it.author.user=admin
sling.it.author.password=admin

# AEM Publish Instance
sling.it.publish.url=http://localhost:4503
sling.it.publish.user=admin
sling.it.publish.password=admin

# Timeouts
sling.it.timeout=30000
sling.it.http.timeout=60000
```

## External Resources

- **wcm.io AEM Mock JavaDoc**: https://wcm.io/testing/aem-mock/apidocs/
- **Apache Sling Mock JavaDoc**: https://sling.apache.org/apidocs/sling-mock/
- **JUnit 5 API**: https://junit.org/junit5/docs/current/api/
- **Mockito JavaDoc**: https://javadoc.io/doc/org.mockito/mockito-core/latest/
- **AEM Testing Clients**: https://github.com/adobe/aem-testing-clients
