# component-library

Standalone, reusable Vue 3 UI component library consumed by `frontend/`.

## Responsibilities

- Shared input components (email, password, password creation)
- Icon wrapper for FontAwesome
- Global SCSS styles (Bootstrap + customisations)
- Shared utilities (e.g., `passwordUtil` for scrypt proof-of-work)

## Key facts

- Source root: `component-library/src/main/typescript/src/`
- Component tag names use the `fio-` prefix (e.g., `fio-input-email`, `fio-icon`)
- Distributed as a Vue plugin; entry point: `src/index.ts`
- Self-contained: no Pinia, Vue Router, or application-specific imports allowed
- Input components support `v-model` via `modelValue` prop + `update:modelValue` emit
- `vue` and `vue-i18n` are external (not bundled)

## Build

```bash
./gradlew :component-library:npmBuild
```
