# AEM JUnit Testing Skill — Evaluation Results

**Run:** 2026-11-12 (skill v1.1.1)
**Method:** 4 prompts sampled from `PROMPT_TESTS.md` (cases 1, 2, 4, 6) were given to independent
coding agents with the skill loaded (SKILL.md + full-testing-guide.md + verified examples).
Generated code was compiled and executed in a bare Maven project mirroring the verified
example environment (aem-sdk-api 2026.10.29251, aem-mock 5.7.4, JUnit 5.14.4, Mockito 5.24.0,
Java 21) with one realism addition: `bnd-maven-plugin` for SCR metadata generation, as every
real AEM core module has.

| # | Case | Tests run | Compile | Runtime | Verdict |
|---|------|-----------|---------|---------|---------|
| 1 | Sling Model (ArticleModel) | 6 | ✅ | 6/6 pass | **PASS** |
| 2 | OSGi service (PageService) | 8 | ✅ | 3/8 pass | **PARTIAL** |
| 3 | Servlet (DataServlet) | 5 | ✅ | 1/5 pass | **PARTIAL** |
| 4 | QueryBuilder (PageSearchService) | 4 | ✅ | 4/4 pass | **PASS** |

Aggregate: 14/23 tests green across 4 scenarios; 2/4 scenarios fully green.
Raw generated code preserved under `aem-junit-eval/` scratch (cases 1–4).

## Per-case evidence

### Case 1 — Sling Model: PASS (6/6)
ModelFactory.createModel() + adaptTo(), explicit model registration, JSON fixtures with
timezone-stable Calendar assertions, null/empty/scalar-to-array edge cases. Exemplary skill
adherence; agent even verified fixture date-parsing behavior against the bundled mock jars.

### Case 2 — OSGi service: PARTIAL (3/8)
Compiled cleanly. Runtime failures expose a skill gap, not bad assertions: the test faked
`ResourceResolverFactory` via `context.registerService(...)`, but **aem-mock silently ignores
external ResourceResolverFactory registrations** — the context always supplies its own
`MockResourceResolverFactory` (verified by identity hash: injected field ≠ registered mock).
Service behavior was correct through the context factory (the 4 "errors" are strict-stub
UnnecessaryStubbingException from the never-used mock; 1 test fails `verify` with zero
interactions). Skill guidance "registerInjectActivateService() and registered dependency
fakes" does not hold for ResourceResolverFactory specifically.

### Case 3 — Servlet: PARTIAL (1/5)
Compiled cleanly. 4/5 tests fail with `JsonException: ...JsonProviderImpl not found` /
ClassCastException: the servlet used `javax.json.Json.createObjectBuilder()` while the
cloud-dependencies BOM puts Johnzon 2.x (jakarta JSONP) on the test classpath — the ServiceLoader
finds Johnzon's provider but cannot cast it to `javax.json.spi.JsonProvider`. Classic JSONP 1.1
vs 2.x conflict (wcm.io documents a JSONP 1.1 mixin for this). Skill has no warning about JSON
provider conflicts in tests; hand-built JSON or the mixin avoids it.

### Case 4 — QueryBuilder: PASS (4/4)
Correctly chose to mock the QueryBuilder boundary (in-memory resolver only — matches skill
guidance), `PredicateGroup` with type=cq:Page + path, zero-result and invalid-input cases,
strict stubs. Passed as generated.

## Findings → skill improvements

1. **SCR metadata requirement (blocked 3/4 scenarios initially).** `registerInjectActivateService()`
   throws `NoScrMetadataException` for DS-annotated classes unless bnd/maven-bundle-plugin has
   generated `OSGI-INF/*.xml` onto the test classpath. Verified examples avoid the API entirely;
   the skill never mentions the requirement. → Add a note + harness guidance (bnd-maven-plugin
   `bnd-process` goal generates DS XML into `target/classes`).
2. **ResourceResolverFactory cannot be faked in aem-mock.** `context.registerService()` for it is
   silently ignored; tests must exercise the context resolver or inject manually. → Add a gotcha.
3. **JSONP provider conflict under the cloud BOM.** `javax.json.Json` API + Johnzon 2.x classpath
   = ClassCastException at `Json.createObjectBuilder()`. → Add a gotcha: prefer plain JSON
   building or import the JSONP 1.1 mixin.

## What was NOT evaluated
Prompts 3, 5, 7–10 (component, DAM, workflow, scheduler, CF, ACL) remain unscored; the workflow
and DAM rows are partially covered by the verified examples but not by model-output eval.
