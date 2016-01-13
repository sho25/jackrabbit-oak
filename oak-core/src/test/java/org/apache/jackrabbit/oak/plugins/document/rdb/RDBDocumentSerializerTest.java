begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|plugins
operator|.
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|DocumentStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|DocumentStoreException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|DocumentStoreFixture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|NodeDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|RDBDocumentSerializerTest
block|{
specifier|private
name|DocumentStoreFixture
name|fixture
init|=
name|DocumentStoreFixture
operator|.
name|RDB_H2
decl_stmt|;
specifier|private
name|DocumentStore
name|store
decl_stmt|;
specifier|private
name|RDBDocumentSerializer
name|ser
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|=
name|fixture
operator|.
name|createDocumentStore
argument_list|()
expr_stmt|;
name|ser
operator|=
operator|new
name|RDBDocumentSerializer
argument_list|(
name|store
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|fixture
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleString
parameter_list|()
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"{}"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"_foo"
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|doc
operator|.
name|hasBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|doc
operator|.
name|getModCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleBlob
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"\"blob\""
argument_list|,
literal|"{}"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"_foo"
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|doc
operator|.
name|hasBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|doc
operator|.
name|getModCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleBlob2
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"\"blob\""
argument_list|,
literal|"{\"s\":\"string\", \"b\":true, \"i\":1}"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"_foo"
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|doc
operator|.
name|hasBinary
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|doc
operator|.
name|getModCount
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"i"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleBoth
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
try|try
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"{}"
argument_list|,
literal|"{}"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlobAndDiff
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"\"blob\", [[\"=\", \"foo\", \"bar\"],[\"M\", \"m1\", 1],[\"M\", \"m2\", 3]]"
argument_list|,
literal|"{\"m1\":2, \"m2\":2}"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"m1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"m2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlobAndDiffBorked
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
try|try
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"[[\"\", \"\", \"\"]]"
argument_list|,
literal|"{}"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrokenJSONTrailingComma
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
try|try
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"{ \"x\" : 1, }"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrokenJSONUnquotedIdentifier
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
try|try
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"{ x : 1, }"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleStringNonAscii
parameter_list|()
block|{
name|RDBRow
name|row
init|=
operator|new
name|RDBRow
argument_list|(
literal|"_foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|"{\"x\":\"\u20ac\uD834\uDD1E\"}"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|this
operator|.
name|ser
operator|.
name|fromRow
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|row
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"_foo"
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\u20ac\uD834\uDD1E"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidJsonSimple
parameter_list|()
block|{
name|RDBJSONSupport
name|json
init|=
operator|new
name|RDBJSONSupport
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|json
operator|.
name|parse
argument_list|(
literal|"null"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|Boolean
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|(
name|Boolean
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123.45
argument_list|,
operator|(
name|Number
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"123.45"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\r\n\t\u00e0"
argument_list|,
operator|(
name|String
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"\"\r\n\t\u00e0\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidJsonArray
parameter_list|()
block|{
name|RDBJSONSupport
name|json
init|=
operator|new
name|RDBJSONSupport
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
literal|""
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|}
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"[true]"
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
literal|""
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
literal|null
block|,
literal|123L
block|,
literal|"foobar"
block|}
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"[true, null, 123, \"foobar\"]"
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidJsonMap
parameter_list|()
block|{
name|RDBJSONSupport
name|json
init|=
operator|new
name|RDBJSONSupport
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|json
operator|.
name|parse
argument_list|(
literal|"{\"a\":true,\"b\":null,\"c\":123,\"d\":\"foobar\"}"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|Boolean
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123l
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidJson
parameter_list|()
block|{
name|RDBJSONSupport
name|json
init|=
operator|new
name|RDBJSONSupport
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|String
name|tests
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"x"
block|,
literal|"\""
block|,
literal|"{a:1}"
block|,
literal|"[false,]"
block|}
decl_stmt|;
for|for
control|(
name|String
name|test
range|:
name|tests
control|)
block|{
try|try
block|{
name|json
operator|.
name|parse
argument_list|(
name|test
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{             }
block|}
block|}
block|}
end_class

end_unit

