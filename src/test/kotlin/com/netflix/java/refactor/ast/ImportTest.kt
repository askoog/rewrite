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
package com.netflix.java.refactor.ast

import com.netflix.java.refactor.parse.Parser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

abstract class ImportTest(p: Parser): Parser by p {
    
    @Test
    fun matchImport() {
        val a = parse("""
            import java.util.List;
            public class A {}
        """)

        assertTrue(a.imports.first().matches("java.util.List"))
    }

    @Test
    fun matchStarImport() {
        val a = parse("""
            import java.util.*;
            public class A {}
        """)

        assertTrue(a.imports.first().matches("java.util.List"))
    }

    @Test
    fun hasStarImportOnInnerClass() {
        val a = """
            package a;
            public class A {
               public static class B { }
            }
        """

        val c = """
            import a.*;
            public class C {
                A.B b = new A.B();
            }
        """

        val cu = parse(c, whichDependsOn = a)
        val import = cu.imports.first()
        assertTrue(import.matches("a.A.B"))
        assertTrue(import.matches("a.A"))
    }
    
    @Test
    fun format() {
        val a = parse("""
            |import java.util.List;
            |import static java.util.Collections.*;
            |public class A {}
        """)
        
        assertEquals("import java.util.List", a.imports[0].printTrimmed())
        assertEquals("import static java.util.Collections.*", a.imports[1].printTrimmed())
    }
}