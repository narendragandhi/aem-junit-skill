# AEM JUnit Testing - Agent Instructions

This repository contains comprehensive JUnit testing guidance for Adobe Experience Manager (AEM) as a Cloud Service.

## Quick Reference

- **Framework**: wcm.io AEM Mock 5.6.4 + JUnit 5.11.0
- **Java**: 21 (required for AEM Cloud Service)
- **Coverage Target**: 80% minimum for Cloud Manager

## Key Patterns

### Basic AemContext
```java
@ExtendWith(AemContextExtension.class)
class MyTest {
    private final AemContext context = new AemContext();
}
```

### Sling Models (use ModelFactory)
```java
ModelFactory modelFactory = context.getService(ModelFactory.class);
MyModel model = modelFactory.createModel(resource, MyModel.class);
```

### OSGi Services
```java
MyService service = context.registerInjectActivateService(new MyService());
```

## Important Notes

1. **Dependency Order**: AEM Mock MUST be declared before AEM SDK API in pom.xml
2. **javax.inject**: Required for Sling Models
3. **Sling Models Impl**: Add to test scope for model injection to work

## Coverage

- Sling Models (with/without injection)
- OSGi Services
- Pages & Components
- DAM Assets
- Servlets
- Workflows
- Schedulers
- Event Handlers
- QueryBuilder
- Content Fragments
- Context-Aware Configurations
- Users, Groups, ACLs

## Full Documentation

See `skills/aem-junit/SKILL.md` for complete guide.
