begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|http
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|AcceptHeaderTest
block|{
comment|//    @Test
comment|//    public void testRfcExample1() {
comment|//        AcceptHeader accept = new AcceptHeader(
comment|//                "text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c");
comment|//
comment|//        assertEquals("text/plain", accept.resolve("text/plain"));
comment|//        assertEquals("text/html", accept.resolve("text/html"));
comment|//        assertEquals("text/x-dvi", accept.resolve("text/x-dvi"));
comment|//        assertEquals("text/x-c", accept.resolve("text/x-c"));
comment|//
comment|//        assertEquals(
comment|//                "application/octet-stream",
comment|//                accept.resolve("application/octet-stream"));
comment|//        assertEquals(
comment|//                "application/octet-stream",
comment|//                accept.resolve("application/pdf"));
comment|//
comment|//        assertEquals("text/html", accept.resolve("text/plain", "text/html"));
comment|//        assertEquals("text/x-c", accept.resolve("text/x-dvi", "text/x-c"));
comment|//        assertEquals("text/x-dvi", accept.resolve("text/x-dvi", "text/plain"));
comment|//
comment|//        assertEquals("text/html", accept.resolve("text/html", "text/x-c"));
comment|//        assertEquals("text/x-c", accept.resolve("text/x-c", "text/html"));
comment|//    }
block|}
end_class

end_unit

