package com.soywiz.korge.test

import com.soywiz.korio.test.assertEqualsJvmFileReference

// Use ./gradlew jvmTestFix to update these files
fun assertEqualsFileReference(path: String, content: String) {
    assertEqualsJvmFileReference(path, content)
}
