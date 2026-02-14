# Contributing to AEM JUnit Testing Skill

Thank you for your interest in contributing!

## Ways to Contribute

1. **Report Issues** - Found a bug or have a suggestion? [Open an issue](https://github.com/narendragandhi/aem-junit-skill/issues)
2. **Submit Pull Requests** - Fix bugs, add examples, or improve documentation
3. **Share Feedback** - Let us know how the skill works for your use case

## Getting Started

### Prerequisites
- Node.js 14+
- Git
- npm or yarn

### Development Setup
```bash
# Clone the repository
git clone https://github.com/narendragandhi/aem-junit-skill
cd aem-junit-skill

# Test CLI
node bin/run.js help
node bin/run.js test

# Test example project
cd examples/aem-test-verified
mvn test
```

### Adding New Examples
1. Add code examples to `skills/aem-junit/SKILL.md`
2. Add working example to `examples/`
3. Update CLI templates in `bin/run.js`
4. Update documentation

## Code Style

- Use 2 spaces for indentation
- Keep lines under 100 characters where possible
- Add comments for complex logic
- Include imports in code examples

## Commit Messages

Use clear, descriptive commit messages:
- `Add QueryBuilder test example`
- `Fix pom.xml dependencies order`
- `Update Sling Model annotations section`

## Testing

Before submitting:
```bash
# Test CLI commands
node bin/run.js test

# Verify example builds
cd examples/aem-test-verified
mvn test
```

## License

By contributing, you agree that your contributions will be licensed under the Apache 2.0 License.
