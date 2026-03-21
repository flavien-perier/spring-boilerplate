package org.springframework.data.util

// Stub for Spring Data 3.x compatibility — TypeInformation was moved to
// org.springframework.data.core in Spring Data 4.x.
// Required to allow AOT processing to introspect springdoc-openapi 2.x classes
// (QuerydslPredicateOperationCustomizer) without ClassNotFoundException.
// The QuerydslPredicateOperationCustomizer bean is @ConditionalOnClass(Querydsl)
// and is never instantiated at runtime since Querydsl is not on the classpath.
interface TypeInformation<S>
