# AEM JUnit Testing Skill - Prompt Tests

This document contains sample prompts to test the skill's effectiveness.

## Test Prompts

### 1. Sling Model Test Creation
```
Create a JUnit test for a Sling Model called ArticleModel that has properties:
- title (String)
- author (String) 
- publishDate (Calendar)
- tags (String[])

The model adapts from Resource. Write the test using AEM Mocks with JUnit 5.
```

### 2. OSGi Service Test
```
Write a unit test for an OSGi service called PageService that:
- Has a method getPagePath(String pageName) returning String
- Uses ResourceResolver to resolve pages
- Should be tested with AEM Mocks and Mockito
```

### 3. Component Test
```
Create a JUnit test for an AEM component MyComponent that:
- Has a Sling Model MyComponentModel
- Uses HTL to display title and description
- Test with AEM Mocks including page creation
```

### 4. Servlet Test
```
Write a test for an AEM Servlet that:
- Handles GET requests at /bin/myapp/data
- Returns JSON with message and timestamp
- Use AEM Mocks to mock request/response
```

### 5. DAM Asset Test
```
Create a test for a DAM workflow that:
- Creates an asset programmatically
- Tests asset metadata extraction
- Uses AEM Mock asset creation
```

### 6. QueryBuilder Test
```
Write a test that:
- Creates 3 test pages under /content/mysite
- Uses QueryBuilder to search for pages of type cq:Page
- Verifies the correct number of results
```

### 7. Workflow Test
```
Create a test for a custom workflow process that:
- Gets triggered on asset creation
- Adds metadata to the asset
- Use workflow mocks with AEMContext
```

### 8. Scheduler Test
```
Write a test for a Sling Scheduler job that:
- Runs a cleanup task
- Can be tested with AEM Mocks
- Uses registerInjectActivateService
```

### 9. Content Fragment Test
```
Create a test for Content Fragment:
- Creates a content fragment with elements
- Tests element data retrieval
- Uses AEM Mock for DAM
```

### 10. ACL/User Test
```
Write a test that:
- Creates a test user
- Tests user group membership
- Verifies permission checks
```

## Expected Quality Metrics

For each prompt test, evaluate:
1. **Code Completeness** - Does it include imports, annotations, setup?
2. **Correctness** - Does it follow AEM best practices?
3. **Compilability** - Would the code compile with proper dependencies?
4. **Best Practices** - Does it use proper patterns (ModelFactory, etc.)?

## Usage

Test each prompt by:
1. Reading the skill documentation relevant to the scenario
2. Using the CLI template: `npx aem-junit-skill template <type>`
3. Generating the test code
4. Verifying code quality against metrics
