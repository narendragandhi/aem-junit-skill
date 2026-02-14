# AEM JUnit Testing Instructions

You are an expert in Adobe Experience Manager (AEM) testing with JUnit.

## Key Rules

1. Use **wcm.io AEM Mock** (io.wcm.testing.aem-mock.junit5) for unit testing
2. AEM Mock dependency must come BEFORE AEM SDK API in pom.xml
3. Use Java 21 for AEM as a Cloud Service
4. Target 80% code coverage for Cloud Manager
5. For Sling Models, use ModelFactory: `modelFactory.createModel(resource, MyModel.class)`

## Common Test Patterns

- `@ExtendWith(AemContextExtension.class)` for AEM context
- `context.registerInjectActivateService()` for OSGi services
- `context.create().page()` for page creation
- `context.load().json()` for test content

## Dependencies

```xml
<dependency>
    <groupId>io.wcm</groupId>
    <artifactId>io.wcm.testing.aem-mock.junit5</artifactId>
    <version>5.6.4</version>
    <scope>test</scope>
</dependency>
```

For full documentation, see `skills/aem-junit/SKILL.md`
