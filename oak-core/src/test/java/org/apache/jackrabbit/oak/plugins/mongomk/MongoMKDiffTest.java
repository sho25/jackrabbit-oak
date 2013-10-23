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
name|plugins
operator|.
name|mongomk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|assertNotNull
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

begin_comment
comment|/**  * Tests for MicroKernel#diff  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKDiffTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|oak596
parameter_list|()
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
literal|"+\"node1\":{\"node2\":{\"prop1\":\"val1\",\"prop2\":\"val2\"}}"
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
literal|"^\"node1/node2/prop1\":\"val1 new\" ^\"node1/node2/prop2\":null"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|diff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev2
argument_list|,
literal|"/node1/node2"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|diff
operator|.
name|contains
argument_list|(
literal|"^\"prop2\":null"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|diff
operator|.
name|contains
argument_list|(
literal|"^\"prop1\":\"val1 new\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addPathOneLevel
parameter_list|()
block|{
name|String
name|rev0
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1\":{}"
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
literal|"/level1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1"
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
name|addPathTwoLevels
parameter_list|()
block|{
name|String
name|rev0
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1/level2\":{}"
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1"
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
literal|"/level1/level2"
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
name|addPathTwoSameLevels
parameter_list|()
block|{
name|String
name|rev0
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1a\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1b\":{}"
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
literal|"/level1a"
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
literal|"/level1b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1a"
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
literal|"/level1b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"New MongoMK only supports depth 0"
argument_list|)
specifier|public
name|void
name|removePath
parameter_list|()
block|{
comment|// Add level1& level1/level2
name|String
name|rev0
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1\":{}"
operator|+
literal|"+\"level1/level2\":{}"
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove level1/level2
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"level1/level2\""
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Generate reverseDiff from rev1 to rev0
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|"/level1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Commit the reverseDiff and check rev0 state is restored
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove level1
name|String
name|rev2
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"level1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Generate reverseDiff from rev2 to rev0
name|reverseDiff
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev2
argument_list|,
name|rev0
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Commit the reverseDiff and check rev0 state is restored
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"New MongoMK only supports depth 0"
argument_list|)
specifier|public
name|void
name|movePath
parameter_list|()
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
literal|"+\"level1\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1/level2\":{}"
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev2
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|">\"level1\" : \"level1new\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/level1"
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
literal|"/level1new"
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
literal|"/level1new/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev2
argument_list|,
name|rev1
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1"
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
literal|"/level1/level2"
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
literal|"/level1new"
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
literal|"/level1new/level2"
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
name|copyPath
parameter_list|()
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
literal|"+\"level1\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1/level2\":{}"
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
literal|"/level1"
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
literal|"/level1/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev2
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"*\"level1\" : \"level1new\""
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
literal|"/level1"
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
literal|"/level1new"
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
literal|"/level1new/level2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev2
argument_list|,
name|rev1
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|""
argument_list|,
name|reverseDiff
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
literal|"/level1"
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
literal|"/level1/level2"
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
literal|"/level1new"
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
literal|"/level1new/level2"
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
name|setProperty
parameter_list|()
block|{
name|String
name|rev0
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1\":{}"
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
literal|"/level1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add property.
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"level1/prop1\": \"value1\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertPropertyExists
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|)
expr_stmt|;
comment|// Generate reverseDiff from rev1 to rev0
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|"/level1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Commit the reverseDiff and check property is gone.
name|mk
operator|.
name|commit
argument_list|(
literal|"/level1"
argument_list|,
name|reverseDiff
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
literal|"/level1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeProperty
parameter_list|()
block|{
name|String
name|rev0
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1\":{ \"prop1\" : \"value\"}"
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
literal|"/level1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertPropertyExists
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|)
expr_stmt|;
comment|// Remove property
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"level1/prop1\" : null"
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
literal|"/level1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertPropertyNotExists
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|)
expr_stmt|;
comment|// Generate reverseDiff from rev1 to rev0
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|"/level1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Commit the reverseDiff and check property is added back.
name|mk
operator|.
name|commit
argument_list|(
literal|"/level1"
argument_list|,
name|reverseDiff
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertPropertyExists
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeProperty
parameter_list|()
block|{
name|String
name|rev0
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"level1\":{ \"prop1\" : \"value1\"}"
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
literal|"/level1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
comment|// Change property
name|String
name|rev1
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"level1/prop1\" : \"value2\""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
comment|// Generate reverseDiff from rev1 to rev0
name|String
name|reverseDiff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|rev1
argument_list|,
name|rev0
argument_list|,
literal|"/level1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reverseDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reverseDiff
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Commit the reverseDiff and check property is set back.
name|mk
operator|.
name|commit
argument_list|(
literal|"/level1"
argument_list|,
name|reverseDiff
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/level1"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|obj
argument_list|,
literal|"prop1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|diffForChangeBelowManyChildren
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|MongoMK
operator|.
name|MANY_CHILDREN_THRESHOLD
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"+\"node-"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":{}"
argument_list|)
expr_stmt|;
block|}
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// wait a while, _modified has 5 seconds granularity
name|Thread
operator|.
name|sleep
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// create a base commit for the diff
name|String
name|base
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// this is the commit we want to get the diff for
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/node-0"
argument_list|,
literal|"+\"foo\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|diff
init|=
name|mk
operator|.
name|diff
argument_list|(
name|base
argument_list|,
name|rev
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|diff
argument_list|,
name|diff
operator|.
name|contains
argument_list|(
literal|"^\"/node-0\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|diffManyChildren
parameter_list|()
block|{
name|diffManyChildren
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|diffManyChildrenOnBranch
parameter_list|()
block|{
name|diffManyChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|diffManyChildren
parameter_list|(
name|boolean
name|onBranch
parameter_list|)
block|{
name|String
name|baseRev
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|MongoMK
operator|.
name|MANY_CHILDREN_THRESHOLD
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"+\"node-"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":{}"
argument_list|)
expr_stmt|;
block|}
name|String
name|rev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|jsop
init|=
name|mk
operator|.
name|diff
argument_list|(
name|baseRev
argument_list|,
name|rev
argument_list|,
literal|"/"
argument_list|,
literal|0
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
name|MongoMK
operator|.
name|MANY_CHILDREN_THRESHOLD
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"+\"/node-"
operator|+
name|i
operator|+
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev
argument_list|,
name|baseRev
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MongoMK
operator|.
name|MANY_CHILDREN_THRESHOLD
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"-\"/node-"
operator|+
name|i
operator|+
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|onBranch
condition|)
block|{
name|rev
operator|=
name|mk
operator|.
name|branch
argument_list|(
name|rev
argument_list|)
expr_stmt|;
block|}
name|String
name|rev2
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"node-new\":{}"
argument_list|,
name|rev
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|jsop
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev
argument_list|,
name|rev2
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"+\"/node-new\""
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev3
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"node-new/prop\":\"value\""
argument_list|,
name|rev2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|jsop
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev2
argument_list|,
name|rev3
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"^\"/node-new\""
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev4
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"node-new/foo\":{}"
argument_list|,
name|rev3
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|jsop
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev3
argument_list|,
name|rev4
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"^\"/node-new\""
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev5
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"^\"node-new/foo/prop\":\"value\""
argument_list|,
name|rev4
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|jsop
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev4
argument_list|,
name|rev5
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"^\"/node-new\""
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|rev6
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"-\"node-new/foo\""
argument_list|,
name|rev5
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|jsop
operator|=
name|mk
operator|.
name|diff
argument_list|(
name|rev5
argument_list|,
name|rev6
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|jsop
argument_list|,
name|jsop
operator|.
name|contains
argument_list|(
literal|"^\"/node-new\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

