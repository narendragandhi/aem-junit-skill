# AEM JUnit Testing Skill

<p align="center">
  <a href="https://www.npmjs.com/package/aem-junit-skill">
    <img src="https://img.shields.io/npm/v/aem-junit-skill.svg" alt="npm version">
  </a>
  <a href="https://github.com/narendragandhi/aem-junit-skill/actions">
    <img src="https://github.com/narendragandhi/aem-junit-skill/workflows/CI/CD/badge.svg" alt="CI Status">
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License">
  </a>
</p>

> Comprehensive JUnit testing guide for Adobe Experience Manager (AEM) as a Cloud Service. Designed for AI assistants to generate accurate, production-ready test code.

## Overview

This skill provides comprehensive documentation and templates for writing JUnit tests in AEM projects using wcm.io AEM Mocks, Apache Sling Mocks, and Adobe Testing Clients.

### Features

- 📚 Comprehensive AEM JUnit testing documentation
- 🎯 **26 testing sections** covered
- ⚡ **7 CLI commands** for quick reference
- ✅ **Working example projects** with tests
- 🔧 **Platform integrations** (Claude, Cursor, Copilot, Codex)

## Quick Start

### Using npx (No Install)

```bash
# Show help
npx aem-junit-skill help

# Show full guide
npx aem-junit-skill guide

# Get Maven dependencies
npx aem-junit-skill deps

# Generate test template
npx aem-junit-skill template model
npx aem-junit-skill template service
npx aem-junit-skill template component
npx aem-junit-skill template servlet
```

### Clone & Install

```bash
git clone https://github.com/narendragandhi/aem-junit-skill
cd aem-junit-skill
npm install -g
aem-junit-skill help
```

## What's Covered

### Testing Scenarios

| Scenario | Status |
|----------|--------|
| Sling Models (basic & advanced) | ✅ |
| OSGi Services | ✅ |
| Pages & Components | ✅ |
| DAM Assets | ✅ |
| Servlets | ✅ |
| Workflows | ✅ |
| Schedulers | ✅ |
| Event Handlers | ✅ |
| QueryBuilder | ✅ |
| Content Fragments | ✅ |
| Context-Aware Configs | ✅ |
| Users, Groups & ACLs | ✅ |

### Advanced Sling Model Annotations

- `@ValueMapValue`
- `@ChildResource`
- `@Self`
- `@Named`
- `@Optional`
- `defaultValue`
- `@OSGiService`
- `DefaultInjectionStrategy`

## Documentation Structure

```
aem-junit-skill/
├── skills/
│   └── aem-junit/
│       ├── SKILL.md            # Concise runtime instructions
│       └── references/         # Detailed patterns loaded as needed
├── QUALITY_METRICS.md          # Quality metrics & coverage
├── SKILL_DEVELOPMENT.md        # Skill creation and release playbook
├── PROMPT_TESTS.md             # Test prompts for AI evaluation
├── bin/
│   └── run.js                  # CLI tool
├── scripts/
│   └── validate-prompt-tests.js # Prompt catalog evaluator
├── docs/
│   └── references/
│       └── testing-api-reference.md
├── examples/
│   └── aem-test-verified/     # Working Maven project
├── .github/
│   └── workflows/
│       └── ci.yml            # GitHub Actions
└── package.json
```

## How We Create Skills

See [SKILL_DEVELOPMENT.md](SKILL_DEVELOPMENT.md) for the maintainer workflow:
define the job and triggers, design progressive disclosure, add deterministic
resources, evaluate realistic prompts, validate the package, and release with
evidence-backed quality claims.

## Dependencies

```xml
<properties>
    <aem.sdk.api>2025.11.23482.20251120T200914Z-251200</aem.sdk.api>
    <aem-mock.version>5.6.4</aem-mock.version>
    <junit.version>5.11.0</junit.version>
    <mockito.version>5.14.0</mockito.version>
</properties>
```

## Installation by Platform

### Claude Code

**Option 1: Project-level (recommended)**
```bash
# In your AEM project root
mkdir -p .claude/skills
curl -o .claude/skills/aem-junit.md https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/skills/aem-junit/SKILL.md
```

**Option 2: Using npx**
```bash
npx aem-junit-skill guide > .claude/skills/aem-junit.md
```

**Option 3: Global installation**
```bash
mkdir -p ~/.claude/skills
curl -o ~/.claude/skills/aem-junit.md https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/skills/aem-junit/SKILL.md
```

### Cursor AI

**Option 1: Copy rules file**
```bash
curl -o .cursorrules https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/.cursorrules
```

**Option 2: Create .cursor/rules directory**
```bash
mkdir -p .cursor/rules
curl -o .cursor/rules/aem-junit.mdc https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/.cursorrules
```

### GitHub Copilot

```bash
mkdir -p .github
curl -o .github/copilot-instructions.md https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/.github/copilot-instructions.md
```

Or append to existing instructions:
```bash
curl https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/.github/copilot-instructions.md >> .github/copilot-instructions.md
```

### Google Gemini CLI

```bash
curl -o GEMINI.md https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/AGENTS.md
```

### Windsurf / Cascade

```bash
mkdir -p .windsurf/rules
curl -o .windsurf/rules/aem-junit.md https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/skills/aem-junit/SKILL.md
```

### OpenAI Codex / Other Agents

Use the `AGENTS.md` file which provides a condensed version of the skill:
```bash
curl -o AGENTS.md https://raw.githubusercontent.com/narendragandhi/aem-junit-skill/main/AGENTS.md
```

## Example Test

```java
@ExtendWith(AemContextExtension.class)
class ArticleModelTest {

    private final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.load().json("/test-content.json", "/content/mysite");
        context.addModelsForClasses(ArticleModel.class);
    }

    @Test
    void testArticleModel() {
        Resource resource = context.resourceResolver().getResource("/content/mysite/article");
        ModelFactory modelFactory = context.getService(ModelFactory.class);
        ArticleModel model = modelFactory.createModel(resource, ArticleModel.class);
        
        assertNotNull(model);
        assertEquals("Test Title", model.getTitle());
    }
}
```

## Testing the Examples

```bash
cd examples/aem-test-verified
mvn test
```

Expected output:
```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Example Tests Included

| Test Class | Tests | Patterns Covered |
|------------|-------|------------------|
| `HeroComponentTest` | 2 | Basic Sling Model |
| `HelloServiceTest` | 4 | OSGi Service, Mockito |
| `NavigationModelTest` | 4 | `@Self`, `@ChildResource` |
| `SiteConfigTest` | 4 | Context-Aware Config |
| `AssetApprovalProcessTest` | 6 | Workflow, WorkItem mocking |

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for:
- Development setup
- Adding new examples
- Code style guidelines
- Testing procedures

## License

Apache License 2.0 - See [LICENSE](LICENSE)

---

<p align="center">Built for the AEM Community ❤️</p>
