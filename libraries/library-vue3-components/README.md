# library-vue3-components

Standalone, reusable Vue 3 UI component library consumed by `frontend/`.

## Responsibilities

- Shared input components (email, password, password creation, markdown editor)
- Content components (`fio-markdown` markdown renderer)
- Icon wrapper for FontAwesome
- Global SCSS styles (Bootstrap + customisations)
- Shared utilities (e.g., `passwordUtil` for scrypt proof-of-work, `downloadUtil`, `markdownUtil` zero-dependency
  markdown parser)

## Key facts

- Source root: `libraries/library-vue3-components/src/main/typescript/src/`
- Component tag names use the `fio-` prefix (e.g., `fio-input-email`, `fio-icon`, `fio-markdown`, `fio-input-markdown`)
- Distributed as a Vue plugin; entry point: `src/index.ts`
- Self-contained: no Pinia, Vue Router, or application-specific imports allowed
- Input components support `v-model` via `modelValue` prop + `update:modelValue` emit
- `vue` and `vue-i18n` are external (not bundled)

## Build

```bash
./gradlew :libraries:library-vue3-components:yarnBuild
```
