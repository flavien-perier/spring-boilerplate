# Gradle Optimizations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reduce build redundancy, fix silent misconfiguration, and improve incremental build correctness across all Gradle modules.

**Architecture:** Changes are purely to the Gradle build files — no source code is touched. Each task is independently verifiable by running `./gradlew build` after applying the change.

**Tech Stack:** Gradle Kotlin DSL, Version Catalog (`libs.versions.toml`), Spring Boot 4, Kotlin Multiplatform, Node Gradle Plugin.

---

### Task 1: Centralize Node version in `libs.versions.toml`

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `frontend/build.gradle.kts`
- Modify: `component-library/build.gradle.kts`

- [ ] **Step 1: Add `node` version to the catalog**

In `gradle/libs.versions.toml`, add after `graalvm-native`:
```toml
node-version = "24.14.0"
```

- [ ] **Step 2: Use the catalog version in `frontend/build.gradle.kts`**

Replace:
```kotlin
version.set("24.14.0")
```
With:
```kotlin
version.set(libs.versions.node.version.get())
```
(Only the `node { }` block line — leave `node-gradle` plugin version untouched.)

- [ ] **Step 3: Same change in `component-library/build.gradle.kts`**

Replace:
```kotlin
version.set("24.14.0")
```
With:
```kotlin
version.set(libs.versions.node.version.get())
```

---

### Task 2: Fix silent Spring plugin application to `component-library`

**Files:**
- Modify: `build.gradle.kts` (root)

- [ ] **Step 1: Add `component-library` to the exclusion condition**

Replace:
```kotlin
if (project.name != "utils" && project.name != "frontend" && project.name != "openapi") {
```
With:
```kotlin
if (project.name != "utils" && project.name != "frontend" && project.name != "openapi" && project.name != "component-library") {
```

---

### Task 3: Remove redundant plugin declarations from `domain`

**Files:**
- Modify: `domain/build.gradle.kts`

The root `subprojects {}` block already calls `apply(plugin = "io.spring.dependency-management")` and `apply(plugin = "org.jetbrains.kotlin.jvm")` for `domain`. Declaring them again in domain's `plugins {}` is redundant.

- [ ] **Step 1: Remove the two redundant plugin lines**

Replace the `plugins {}` block:
```kotlin
plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}
```
With:
```kotlin
plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}
```

---

### Task 4: Fix over-broad `api()` scope in `domain`

**Files:**
- Modify: `domain/build.gradle.kts`

`api()` leaks transitive dependencies to all consumers. Only what appears in `domain`'s public API surface (JPA entities, repository interfaces, utils types) should use `api`. Infrastructure concerns (Redis, mail, Thymeleaf, Liquibase, web starter) are implementation details.

- [ ] **Step 1: Replace the dependencies block**

Replace:
```kotlin
dependencies {
    api(project(":utils"))

    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.data.redis)
    api(libs.spring.boot.starter.mail)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.thymeleaf)
    api(libs.spring.boot.starter.liquibase)

    api(libs.kotlin.reflect)
    api(libs.kotlin.stdlib)

    runtimeOnly(libs.postgresql)
    api(libs.jedis)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.subethasmtp)
}
```
With:
```kotlin
dependencies {
    api(project(":utils"))

    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.liquibase)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)

    runtimeOnly(libs.postgresql)
    implementation(libs.jedis)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.subethasmtp)
}
```

---

### Task 5: Remove duplicate Kotlin deps from `api`

**Files:**
- Modify: `api/build.gradle.kts`

`kotlin-reflect` and `kotlin-stdlib` are already managed by the Spring BOM and arrive transitively via `:domain`. Declaring them explicitly is noise.

- [ ] **Step 1: Remove the two lines**

Remove:
```kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
```

---

### Task 6: Add `inputs`/`outputs` to `patchApiSource` task

**Files:**
- Modify: `frontend/build.gradle.kts`

Without declared inputs/outputs, Gradle cannot determine if `patchApiSource` is up-to-date and re-runs it every build. Declaring the four files it reads and modifies allows Gradle to skip it on unchanged inputs.

- [ ] **Step 1: Add inputs and outputs declarations**

Replace:
```kotlin
val patchApiSource = tasks.register("patchApiSource") {
    dependsOn("openApiGenerate")
    val tsConfigFile = file("${projectDir}/src/main/typescript/generated/api-source/tsconfig.json")
        val commonFile = file("${projectDir}/src/main/typescript/generated/api-source/common.ts")
        val apiFile = file("${projectDir}/src/main/typescript/generated/api-source/api.ts")
    doLast {
```
With:
```kotlin
val patchApiSource = tasks.register("patchApiSource") {
    dependsOn("openApiGenerate")
    val tsConfigFile = file("${projectDir}/src/main/typescript/generated/api-source/tsconfig.json")
    val commonFile = file("${projectDir}/src/main/typescript/generated/api-source/common.ts")
    val apiFile = file("${projectDir}/src/main/typescript/generated/api-source/api.ts")
    val packageJsonFileForInputs = file("${projectDir}/src/main/typescript/generated/api-source/package.json")
    inputs.files(tsConfigFile, commonFile, apiFile, packageJsonFileForInputs)
    outputs.files(tsConfigFile, commonFile, apiFile, packageJsonFileForInputs)
    doLast {
```

---

### Task 7: Build and verify

- [ ] **Step 1: Run a full build**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 2: Run a second build to verify incremental correctness**

```bash
./gradlew build
```
Expected: `BUILD SUCCESSFUL` with most tasks showing `UP-TO-DATE`.
