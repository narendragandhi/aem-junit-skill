#!/usr/bin/env node

const fs = require('fs');

const file = 'PROMPT_TESTS.md';
const content = fs.readFileSync(file, 'utf8');
const prompts = [...content.matchAll(/^### (\d+)\. .+$/gm)];
const fencedBlocks = [...content.matchAll(/^```\n[\s\S]*?^```$/gm)];

if (prompts.length !== 10) {
  throw new Error(`Expected 10 prompt cases, found ${prompts.length}`);
}

if (fencedBlocks.length !== 10) {
  throw new Error(`Expected 10 prompt blocks, found ${fencedBlocks.length}`);
}

for (const [index, match] of prompts.entries()) {
  if (Number(match[1]) !== index + 1) {
    throw new Error(`Prompt numbering is not sequential at case ${index + 1}`);
  }
}

for (const criterion of ['Code Completeness', 'Correctness', 'Compilability', 'Best Practices']) {
  if (!content.includes(`**${criterion}**`)) {
    throw new Error(`Missing evaluation criterion: ${criterion}`);
  }
}

console.log(`Prompt catalog valid: ${prompts.length} cases and ${fencedBlocks.length} prompt blocks.`);
console.log('This check validates the catalog structure; model-output scoring remains a manual evaluation step.');
