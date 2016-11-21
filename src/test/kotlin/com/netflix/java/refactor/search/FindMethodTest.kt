/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.java.refactor.search

import com.netflix.java.refactor.parse.OracleJdkParser
import com.netflix.java.refactor.parse.Parser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

abstract class FindMethodTest(p: Parser): Parser by p {

    @Test
    fun findMethodCalls() {
        val a = parse("""
            |import java.util.Collections;
            |public class A {
            |   Object o = Collections.emptyList();
            |}
        """)
        
        val m = a.classes[0].findMethodCalls("java.util.Collections emptyList()").first()
        
        assertEquals("emptyList", m.name.name)
        assertEquals("Collections.emptyList()", m.printTrimmed())
    }

    @Test
    fun matchVarargs() {
        val a = """
            |public class A {
            |    public void foo(String s, Object... o) {}
            |}
        """

        val b = """
            |public class B {
            |   public void test() {
            |       new A().foo("s", "a", 1);
            |   }
            |}
        """

        assertTrue(parse(b, a).classes[0].findMethodCalls("A foo(String, Object...)").isNotEmpty())
    }

    @Test
    fun matchOnInnerClass() {
        val b = """
            |public class B {
            |   public static class C {
            |       public void foo() {}
            |   }
            |}
        """

        val a = """
            |public class A {
            |   void test() {
            |       new B.C().foo();
            |   }
            |}
        """

        assertEquals(1, parse(a, b).classes[0].findMethodCalls("B.C foo()").size)
    }
}

class OracleJdkFindMethodTest: FindMethodTest(OracleJdkParser())