# Changelog

All notable changes to this project will be documented in this file.

## [1.2.0] - 2026-11-12

### Added
- **Eval results**: `EVALS.md` records the first model-output eval run (4 prompts from
  `PROMPT_TESTS.md` compiled and executed; findings are the basis of the new gotchas below).
- **Verified environment gotchas** (SKILL.md + full-testing-guide.md):
  - SCR metadata requirement for `registerInjectActivateService()` (`bnd-maven-plugin` guidance)
  - `ResourceResolverFactory` cannot be faked via `context.registerService()` in aem-mock
  - JSONP 1.1 vs Johnzon 2.x provider conflict under the cloud dependencies BOM

### Changed
- **Freshness**: Dependency pins updated in all snippets (pom.xml, README, `deps`/`config` CLI commands, full-testing-guide):
  - wcm.io AEM Mock 5.6.4 → 5.7.4
  - JUnit 5.11.0 → 5.14.4
  - Mockito 5.14.0 → 5.24.0 (byte-buddy 1.17.7)
- **BOM pattern**: Snippets now import `io.wcm.maven.aem-cloud-dependencies` (wcm.io-recommended) to manage `aem-sdk-api` plus Sling-internal dependencies required for AEM Mocks.
- Verified with `examples/aem-test-verified`: 21/21 tests pass on the new versions (aem-sdk-api 2026.10.29251).

## [1.1.0] - 2026-02-24

### Added
- **Headless Support**: Patterns for testing Sling Model Exporters (Jackson JSON output).
- **AEM Forms**: Specific mocking patterns for Adaptive Forms submission handlers.
- **Cloud Service Secrets**: Patterns for mocking environment variables and $[secret:...] variables.
- **QueryBuilder Utility**: Reusable `QueryBuilderMockHelper` to simplify complex JCR search testing.
- New example tests in `examples/aem-test-verified` for Search and Headless patterns.

## [1.0.0] - 2026-02-14

### Added
- Initial release of AEM JUnit Testing Skill
- 2,230 lines of comprehensive documentation
- 22 testing scenario sections
- 9 CLI commands for quick reference
- Working Maven example project
- Platform integrations:
  - Claude / OpenCode support
  - Cursor (.cursorrules)
  - GitHub Copilot instructions
  - Codex / general agents
- GitHub Actions CI/CD workflow
- npm package configuration

### Documentation Coverage
- Sling Models (basic & advanced annotations)
- OSGi Services
- Pages & Components
- DAM Assets
- Servlets
- Workflows
- Schedulers
- Event Handlers
- QueryBuilder (AEM Cloud compatible)
- Content Fragments
- Context-Aware Configurations
- Users, Groups & ACLs

### Advanced Annotations Covered
- @ValueMapValue
- @ChildResource
- @Self
- @Named
- @Optional
- defaultValue
- DefaultInjectionStrategy
- @OSGiService
