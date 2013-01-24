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

begin_import
import|import
name|com
operator|.
name|jamonapi
operator|.
name|Monitor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jamonapi
operator|.
name|MonitorFactory
import|;
end_import

begin_comment
comment|/**  * Tests for {@link MongoMicroKernel#commit(String, String, String, String)}  * with emphasis on add node and property operations.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKCommitAddTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|addSingleNode
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
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
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
operator|-
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
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeWithChildren
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
literal|"+\"a\" : { \"b\": {}, \"c\": {}, \"d\" : {} }"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeWithNestedChildren
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
literal|"+\"a\" : { \"b\": { \"c\" : { \"d\" : {} } } }"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNodeWithNestedChildren2
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
name|mk
operator|.
name|commit
argument_list|(
literal|"/a"
argument_list|,
literal|"+\"b\" : {}"
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
literal|"/a/b"
argument_list|,
literal|"+\"c\" : {}"
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
literal|"/a"
argument_list|,
literal|"+\"d\" : {}"
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
literal|"/a/d"
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
name|addNodeWithParanthesis
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
literal|"+\"Test({0})\" : {}"
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
literal|"/Test({0})"
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
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
literal|":childNodeCount"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addIntermediataryNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"b\" : { \"c\": {} }}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|rev2
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/d\" : {} +\"a/b/e\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/c"
argument_list|,
name|rev1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/e"
argument_list|,
name|rev1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/d"
argument_list|,
name|rev1
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
name|rev2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/a/b/e"
argument_list|,
name|rev2
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
name|rev2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addDuplicateNode
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
try|try
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
name|setSingleProperty
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
literal|"+\"a\" : {} ^\"a/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
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
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setMultipleProperties
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
literal|"+\"a\" : {} ^\"a/key1\" : \"value1\""
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
literal|"^\"a/key2\" : 2"
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
literal|"^\"a/key3\" : false"
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
literal|"^\"a/key4\" : 0.25"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
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
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
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
literal|"a/key2"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/key3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"a/key4"
argument_list|,
literal|0.25
argument_list|)
expr_stmt|;
block|}
comment|// See http://www.mongodb.org/display/DOCS/Legal+Key+Names
annotation|@
name|Test
specifier|public
name|void
name|setPropertyIllegalKey
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
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/ke.y1\" : \"value\""
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
literal|"a/ke.y1"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/ke.y.1\" : \"value\""
argument_list|,
literal|null
argument_list|,
literal|null
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
literal|"a/ke.y.1"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/$key1\" : \"value\""
argument_list|,
literal|null
argument_list|,
literal|null
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
literal|"a/$key1"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setPropertyWithoutAddingNode
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key1\" : \"value1\""
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
name|setOverwritingProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {} ^\"a/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Commit with rev1
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key1\" : \"value2\""
argument_list|,
name|rev1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Commit with rev1 again (to overwrite rev2)
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key1\" : \"value3\""
argument_list|,
name|rev1
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
literal|"value3"
argument_list|)
expr_stmt|;
block|}
comment|// This is a test to make sure commit time stays the same as time goes on.
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-461"
argument_list|)
specifier|public
name|void
name|commitTime
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|debug
init|=
literal|false
decl_stmt|;
name|Monitor
name|commitMonitor
init|=
name|MonitorFactory
operator|.
name|getTimeMonitor
argument_list|(
literal|"commit"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|commitMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|diff
init|=
literal|"+\"a"
operator|+
name|i
operator|+
literal|"\" : {} +\"b"
operator|+
name|i
operator|+
literal|"\" : {} +\"c"
operator|+
name|i
operator|+
literal|"\" : {}"
decl_stmt|;
if|if
condition|(
name|debug
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Committing: "
operator|+
name|diff
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|diff
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|commitMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|debug
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Committed in "
operator|+
name|commitMonitor
operator|.
name|getLastValue
argument_list|()
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|debug
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Final Result:"
operator|+
name|commitMonitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|existingNodesMerged
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rev
init|=
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
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a/b\" : {}"
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
literal|"^\"a/key1\" : \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Commit to rev before key1 and b were added
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"a/key2\" : \"value2\""
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Check that key1 and b were merged
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
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
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
literal|"a/key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

