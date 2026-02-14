#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const SKILL_PATH = path.join(__dirname, '..', 'skills', 'aem-junit', 'SKILL.md');
const EXAMPLES_PATH = path.join(__dirname, '..', 'examples');

const command = process.argv[2] || 'help';

const commands = {
  help: () => {
    console.log(`
AEM JUnit Testing Skill v1.0.0

Usage: npx aem-junit-skill <command>

Commands:
  help              Show this help message
  guide             Show the full testing guide
  quickstart        Create a quick start test project
  examples          List available examples
  deps              Show required Maven dependencies
  template <type>   Generate test template (model|service|component|servlet)
  config            Show pom.xml configuration

Examples:
  npx aem-junit-skill guide
  npx aem-junit-skill template model MyModel
  npx aem-junit-skill deps
  npx aem-junit-skill quickstart

For full documentation, visit: https://github.com/narendragandhi/aem-junit-skill
`);
  },

  guide: () => {
    if (fs.existsSync(SKILL_PATH)) {
      console.log(fs.readFileSync(SKILL_PATH, 'utf8'));
    } else {
      console.error('Error: SKILL.md not found');
      process.exit(1);
    }
  },

  deps: () => {
    console.log(`
# AEM JUnit Testing - Maven Dependencies

Add these to your pom.xml:

<properties>
    <aem.sdk.api>2025.11.23482.20251120T200914Z-251200</aem.sdk.api>
    <aem-mock.version>5.6.4</aem-mock.version>
    <junit.version>5.11.0</junit.version>
    <mockito.version>5.14.0</mockito.version>
</properties>

<dependencies>
    <!-- wcm.io AEM Mock (MUST be before AEM SDK API) -->
    <dependency>
        <groupId>io.wcm</groupId>
        <artifactId>io.wcm.testing.aem-mock.junit5</artifactId>
        <version>\${aem-mock.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- AEM SDK API -->
    <dependency>
        <groupId>com.adobe.aem</groupId>
        <artifactId>aem-sdk-api</artifactId>
        <version>\${aem.sdk.api}</version>
        <scope>provided</scope>
    </dependency>

    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>\${junit.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>\${mockito.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- javax.inject for Sling Models -->
    <dependency>
        <groupId>javax.inject</groupId>
        <artifactId>javax.inject</artifactId>
        <version>1</version>
        <scope>provided</scope>
    </dependency>

    <!-- Sling Models Implementation -->
    <dependency>
        <groupId>org.apache.sling</groupId>
        <artifactId>org.apache.sling.models.impl</artifactId>
        <version>1.6.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
`);
  },

  examples: () => {
    if (fs.existsSync(EXAMPLES_PATH)) {
      const dirs = fs.readdirSync(EXAMPLES_PATH).filter(f => {
        return fs.statSync(path.join(EXAMPLES_PATH, f)).isDirectory();
      });
      console.log('Available examples:');
      dirs.forEach(dir => {
        console.log('  - ' + dir);
      });
    } else {
      console.log('No examples found');
    }
  },

  template: (type) => {
    const templates = {
      model: `package com.example.core.models;

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
        context.load().json("/com/example/core/models/MyModelTest.json", "/content/mysite");
        context.addModelsForClasses(MyModel.class);
    }

    @Test
    void testModelProperties() {
        Resource resource = context.resourceResolver().getResource("/content/mysite/page");
        assertNotNull(resource);

        ModelFactory modelFactory = context.getService(ModelFactory.class);
        MyModel model = modelFactory.createModel(resource, MyModel.class);
        
        assertNotNull(model);
        // Add your assertions here
    }
}
`,
      service: `package com.example.core.services;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Test
    void testServiceMethod() {
        // Add your test here
    }
}
`,
      component: `package com.example.core.components;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class MyComponentTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.create().page("/content/mysite", "mysite/components/page");
    }

    @Test
    void testComponent() {
        Resource resource = context.resourceResolver().getResource("/content/mysite");
        assertNotNull(resource);
    }
}
`,
      servlet: `package com.example.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class MyServletTest {

    @Test
    void testDoGet() throws Exception {
        AemContext context = new AemContext();
        
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);
        
        when(response.getWriter()).thenReturn(new java.io.PrintWriter(new java.io.StringWriter()));
        
        // Test your servlet
    }
}
`
    };

    const templateName = process.argv[3];
    if (!templateName || !templates[templateName]) {
      console.log('Available templates: model, service, component, servlet');
      console.log('Usage: npx aem-junit-skill template <type>');
      console.log('Example: npx aem-junit-skill template model');
      return;
    }

    console.log(templates[templateName]);
  },

  config: () => {
    console.log(`
# AEM JUnit Testing - Complete pom.xml Configuration

<project>
    <modelVersion>4.0.0</modelVersion>
    <properties>
        <aem.sdk.api>2025.11.23482.20251120T200914Z-251200</aem.sdk.api>
        <aem-mock.version>5.6.4</aem-mock.version>
        <junit.version>5.11.0</junit.version>
        <mockito.version>5.14.0</mockito.version>
    </properties>
    <dependencies>
        <!-- AEM Mocks (MUST come first) -->
        <dependency>
            <groupId>io.wcm</groupId>
            <artifactId>io.wcm.testing.aem-mock.junit5</artifactId>
            <version>\${aem-mock.version}</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>com.adobe.aem</groupId>
            <artifactId>aem-sdk-api</artifactId>
            <version>\${aem.sdk.api}</version>
            <scope>provided</scope>
        </dependency>
        
        <!-- JUnit 5 & Mockito -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>\${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>\${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>\${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        
        <!-- Sling Models -->
        <dependency>
            <groupId>javax.inject</groupId>
            <artifactId>javax.inject</artifactId>
            <version>1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.sling</groupId>
            <artifactId>org.apache.sling.models.impl</artifactId>
            <version>1.6.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.2</version>
            </plugin>
        </plugins>
    </build>
</project>
`);
  },

  quickstart: () => {
    console.log(`
# Quick Start: AEM JUnit Testing

## 1. Add Dependencies

Run: npx aem-junit-skill config > pom.xml

## 2. Create Test Class

Run: npx aem-junit-skill template model MyModel > src/test/java/.../MyModelTest.java

## 3. Create Test Content

Create: src/test/resources/com/example/core/models/MyModelTest.json

{
    "jcr:primaryType": "cq:Page",
    "page": {
        "jcr:primaryType": "cq:PageContent",
        "sling:resourceType": "mysite/components/page"
    }
}

## 4. Run Tests

mvn test

## Key Points

- Use @ExtendWith(AemContextExtension.class) for AEM context
- Use ModelFactory.createModel() for Sling Models with injection
- Target 80% code coverage for Cloud Manager
- Use Java 21 for AEM as a Cloud Service

For more: npx aem-junit-skill guide
`);
  }
};

if (commands[command]) {
  commands[command]();
} else {
  console.log('Unknown command:', command);
  console.log('Run "npx aem-junit-skill help" for usage information');
  process.exit(1);
}
