package com.soywiz.korte

import com.soywiz.korte.dynamic.DynamicShape
import com.soywiz.korte.dynamic.DynamicType
import com.soywiz.korte.dynamic.DynamicTypeScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSerializationIssue {
    @Test
    fun test() = suspendTest {
        // println("testUser()=${testUser()}")
        assertEquals("test user", Template("""{{ user.key }}""")("user" to testUser()))
    }

    fun testUser(): User? {
        val x = """{
                "key": "test user",
                "firstName": "test",
                "lastName": "test"
            }"""

        return Json.decodeFromString(User.serializer(), x)
        //return User(key = "test user", firstName = "test", lastName = "test")
    }

    // Fails: we are getting a null pointer exception
    //@Serializable
    //data class User(val key: String, val firstName: String, val lastName: String) :
    //    DynamicType<User> by DynamicType({
    //        register(User::key)
    //        register(User::firstName)
    //        register(User::lastName)
    //    })

    @Serializable
    data class User(val key: String, val firstName: String, val lastName: String) : DynamicType<User> {
        companion object : DynamicType<User> by DynamicType({
            register(User::key)
            register(User::firstName)
            register(User::lastName)
        })
        override val DynamicTypeScope.__dynamicShape: DynamicShape<User> get() = User.run { DynamicTypeScope.__dynamicShape }
    }
}
