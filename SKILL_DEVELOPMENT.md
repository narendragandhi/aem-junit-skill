# Skill Development Playbook

This document describes how we create, validate, and share a Codex skill. It is
for skill maintainers; it is not part of the runtime skill instructions.

## 1. Define the job

Start with a concrete job statement:

> When should the skill activate, and what should it help the agent produce?

Write down:

- The users and tasks the skill serves.
- Example prompts that should trigger it.
- Nearby tasks that should not trigger it.
- The expected output and quality bar.

Keep the scope narrow enough that the skill has a clear point of view. A skill
may cover several related workflows, but it should not become a general manual
for the entire domain.

## 2. Choose the skill name and layout

Use a short lowercase hyphenated name, under 64 characters. The directory name
and the frontmatter `name` must match.

Minimum layout:

```text
skill-name/
├── SKILL.md
└── agents/
    └── openai.yaml
```

Add `scripts/`, `references/`, or `assets/` only when they directly support the
skill. Keep maintainer documentation, tests, and release files outside the
runtime skill directory.

## 3. Write metadata first

The YAML frontmatter controls discovery, so make the description specific about
both capabilities and trigger conditions:

```yaml
---
name: example-skill
description: Use this skill when ... It helps with ... and produces ...
---
```

Then add `agents/openai.yaml` with a concise display name, a 25–64 character
short description, and a default prompt that explicitly mentions `$skill-name`.

## 4. Design for progressive disclosure

Keep `SKILL.md` focused on the operating workflow:

1. Decide which path applies.
2. Inspect the project or inputs.
3. Perform the work using the preferred patterns.
4. Validate the result.
5. Report assumptions, limitations, and next steps.

Move detailed API tables, variant-specific examples, and long reference material
into one-level-deep `references/` files. Link every reference directly from
`SKILL.md`, and tell the agent when to read it. Aim for fewer than 500 lines in
the main skill file.

## 5. Add deterministic resources where useful

Use a script when the same logic would otherwise be rewritten or when exact,
repeatable behavior matters. Make scripts:

- Safe by default and explicit about inputs and outputs.
- Portable from the skill directory.
- Easy to run without loading their full source into context.
- Covered by a focused test or a documented verification command.

Use examples to demonstrate realistic outputs, not to duplicate the guide.

## 6. Create an evaluation set

Build prompts from real user tasks. Include:

- A common happy path.
- An advanced or ambiguous case.
- A failure or missing-input case.
- A task that should not activate the skill.

For each prompt, define observable criteria: correctness, completeness,
compilability or executability, safety, and adherence to the skill’s preferred
patterns. Record expected behavior and make the evaluation executable where
practical. A list of prompts alone is a test catalog, not an evaluation result.

## 7. Validate in layers

Run checks in this order:

```bash
# Validate metadata and frontmatter
python3 /path/to/skill-creator/scripts/quick_validate.py skills/example

# Run the skill’s unit or integration checks
npm test                 # or the project-specific test command

# Exercise realistic prompt cases
# Record pass/fail evidence against the evaluation criteria.

# Verify the publishable package
npm pack --dry-run --json
git diff --check
```

Also check that Markdown fences are balanced, linked files exist, scripts do
not depend on the maintainer’s machine, and generated artifacts are excluded
from the package.

If a check cannot run, record the exact environmental blocker instead of
claiming it passed.

## 8. Review for shareability

Before sharing, confirm:

- The trigger description is neither too broad nor too narrow.
- Instructions are actionable and do not assume local paths, credentials, or
  unavailable tools.
- Versions and commands agree across the skill, examples, CLI, and README.
- Examples pass from a clean checkout.
- The package contains only intended files.
- Claims in quality reports match current test output.
- Licensing and attribution are present.

For domain-specific skills, have a second reviewer look specifically for stale
APIs, unsafe commands, and guidance that is technically plausible but not
actually compilable.

## 9. Release and maintain

When the checks pass:

1. Update the version and changelog together.
2. Regenerate or verify UI metadata.
3. Run the complete CI-equivalent command locally.
4. Inspect the final package contents.
5. Share the repository or package with installation instructions.

After release, treat user feedback and failed evaluations as inputs to the next
iteration. Re-run the evaluation set after substantive changes, especially
changes to trigger metadata, dependencies, or core examples.

## AEM JUnit example

For this repository, the process is:

1. Define the AEM testing scenarios and supported versions.
2. Put concise operating guidance in `skills/aem-junit/SKILL.md`.
3. Keep API details and examples discoverable through linked references.
4. Verify representative AEM Mock tests with Maven.
5. Exercise the CLI templates and commands.
6. Run the prompt cases in `PROMPT_TESTS.md` against explicit quality criteria.
7. Reconcile `QUALITY_METRICS.md` with actual test and package output.
8. Inspect `npm pack --dry-run` before publishing.
