---
name: aem-junit-testing
description: Use this skill when creating, reviewing, or troubleshooting JUnit 5 unit and integration tests for Adobe Experience Manager as a Cloud Service, including Sling Models, OSGi services, servlets, workflows, schedulers, QueryBuilder, DAM, Content Fragments, Forms, and AEM Mocks.
---

# AEM JUnit Testing

Use this workflow to create reliable AEM tests with wcm.io AEM Mocks, Apache Sling Mocks, Mockito, JUnit 5, and AEM testing clients.

## Workflow

1. Identify the test boundary: pure Java unit test, AEM Mock unit test, or HTTP integration test.
2. Inspect the target class, annotations, injected services, resource types, configuration, and existing project conventions.
3. Reuse the project’s dependency-management and Java version choices. Do not blindly replace an existing `pom.xml`.
4. Choose the smallest suitable resolver type. Use the default in-memory resolver for resource tests; use JCR mock or Oak only for JCR-specific behavior and queries.
5. Build isolated fixtures in `@BeforeEach`, register services before model adaptation, and cleanly control request, resource, configuration, and user context.
6. Use `AemContextExtension` for AEM context lifecycle. Add `MockitoExtension` separately when using Mockito annotations such as `@Mock`.
7. Prefer `ModelFactory.createModel()` when Sling Model injection is complex or failures must be explicit; use `adaptTo()` for simple models.
8. Assert observable behavior, including null/empty inputs, missing services, permissions, error responses, and resource cleanup where relevant.
9. Run the narrow test first, then the module suite. Report compilation, test, coverage, and environment limitations separately.

## Select the test style

| Target | Preferred approach |
|---|---|
| Plain service or utility | JUnit 5 plus Mockito or plain fakes |
| Sling Model | `AemContext`, test content, model registration, `ModelFactory` or `adaptTo()` |
| OSGi service | `registerInjectActivateService()` and registered dependency fakes |
| Servlet | Mock request/response, set selectors, suffix, parameters, and resource context |
| Page/component | `context.create().page()`, component resource, model or rendering contract |
| DAM/Workflow | Mock assets, metadata, workflow data, and process arguments |
| QueryBuilder | JCR-capable resolver when needed; mock the QueryBuilder boundary for pure service tests |
| Integration test | AEM testing client against a running author/publish environment |

## Non-negotiable practices

- Keep tests deterministic and isolated; do not depend on a shared repository or live AEM unless the test is explicitly an integration test.
- Match the project’s namespace (`javax` versus `jakarta`) and dependency versions.
- Register model classes and services explicitly in the mock context.
- Keep Mockito strictness enabled; remove unused stubs instead of making all stubs lenient.
- Never put credentials, tokens, or real customer content in fixtures.
- Do not claim coverage or compatibility without a recorded command and result.

## References and examples

- For detailed patterns and API examples, read [references/full-testing-guide.md](references/full-testing-guide.md).
- For the API summary, read [../../docs/references/testing-api-reference.md](../../docs/references/testing-api-reference.md) when needed.
- For runnable fixtures, see [../../examples/aem-test-verified/](../../examples/aem-test-verified/).
- For dependency snippets, use `node bin/run.js deps` from the repository.

## Validation checklist

```bash
mvn -B test
npm test
npm run test:cli
npm run test:prompts
git diff --check
npm pack --dry-run --json
```

If a command cannot run because dependencies, credentials, or a service are unavailable, state that limitation and preserve the exact failure evidence.
