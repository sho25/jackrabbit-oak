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
name|mongomk
operator|.
name|impl
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|BaseMongoMicroKernelTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests for {@link MongoMicroKernel#commit(String, String, String, String)}  * with emphasis on copy operations.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKCommitCopyTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|copyNode
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"a\" : \"b\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyNodeWithChild
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : {} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"a\" : \"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyNodeWithChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : {},  \"c\" : {}, \"d\" : {}}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"a\" : \"e\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyNodeWithNestedChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : { \"c\" : { \"d\" : {} } } }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"a\" : \"e\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"e/b\" : \"f\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/f"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/f/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/f/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyNodeWithProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"key1\" : \"value1\" }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"a\" : \"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
expr_stmt|;
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"c/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyFromNonExistentNode
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"b\" : \"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{}
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyToAnExistentNode
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : {} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"c\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"c\" : \"a/b\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/b\":{}\n"
operator|+
literal|"*\"a/b\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeAndCopy2
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/b\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"a/b\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeWithChildrenAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/b\":{ \"c\" : {}, \"d\" : {} }\n"
operator|+
literal|"*\"a/b\":\"e\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeWithNestedChildrenAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"b\" : { \"c\" : { } } }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/b/c/d\":{}\n"
operator|+
literal|"*\"a\":\"e\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeAndCopyParent
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/b\":{}\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"b\" : {} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a/b\"\n"
operator|+
literal|"*\"a/b\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeWithNestedChildrenAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"b\" : { \"c\" : { \"d\" : {} } } }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a/b/c/d\"\n"
operator|+
literal|"*\"a\" : \"e\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/e/b/c/d"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNodeAndCopyParent
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"b\" : {} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a/b\"\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/c/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setPropertyAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key1\": \"value1\"\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"c/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setNestedPropertyAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"b\" : {} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/b/key1\": \"value1\"\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|2
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/b/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"c/b/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|modifyParentAddPropertyAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\" : {}\n"
operator|+
literal|"^\"a/key1\": \"value1\"\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"c/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removePropertyAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"b\" : { \"key1\" : \"value1\" } }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/b/key1\": null\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"a/b/key1"
argument_list|)
expr_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"c/b/key1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNestedPropertyAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"key1\" : \"value1\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key1\" : null\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"a/key1"
argument_list|)
expr_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"c/key1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|modifyParentRemovePropertyAndCopy
parameter_list|()
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\":{ \"key1\" : \"value1\"}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\" : {}\n"
operator|+
literal|"^\"a/key1\" : null\n"
operator|+
literal|"*\"a\":\"c\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|nodes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|1
comment|/*depth*/
argument_list|,
literal|0
comment|/*offset*/
argument_list|,
operator|-
literal|1
comment|/*maxChildNodes*/
argument_list|,
literal|null
comment|/*filter*/
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"a/key1"
argument_list|)
expr_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"c/key1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

