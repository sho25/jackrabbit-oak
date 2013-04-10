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
comment|/**  * Tests for {@code MongoMicroKernel#getHeadRevision()}.  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKGetNodesTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|nonExistingRevision
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|"123"
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
name|invalidRevision
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
literal|"invalid"
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
annotation|@
name|Ignore
specifier|public
name|void
name|afterDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|JSONObject
name|root
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
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
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|root
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|JSONObject
name|a
init|=
name|resolveObjectValue
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|a
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|scenario
operator|.
name|delete_A
argument_list|()
expr_stmt|;
name|root
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
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
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|root
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|depthNegative
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|JSONObject
name|root
init|=
name|parseJSONObject
argument_list|(
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
name|root
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|depthZero
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|JSONObject
name|root
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
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
name|root
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|JSONObject
name|a
init|=
name|resolveObjectValue
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertPropertyNotExists
argument_list|(
name|a
argument_list|,
literal|"int"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|depthOne
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|JSONObject
name|root
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
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
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|root
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|JSONObject
name|a
init|=
name|resolveObjectValue
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|a
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|a
argument_list|,
literal|"int"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|JSONObject
name|b
init|=
name|resolveObjectValue
argument_list|(
name|a
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|assertPropertyNotExists
argument_list|(
name|b
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|JSONObject
name|c
init|=
name|resolveObjectValue
argument_list|(
name|a
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|assertPropertyNotExists
argument_list|(
name|c
argument_list|,
literal|"bool"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|depthLimitless
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|scenario
operator|.
name|create
argument_list|()
expr_stmt|;
name|JSONObject
name|root
init|=
name|parseJSONObject
argument_list|(
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
name|root
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|JSONObject
name|a
init|=
name|resolveObjectValue
argument_list|(
name|root
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|a
argument_list|,
literal|":childNodeCount"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|a
argument_list|,
literal|"int"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|JSONObject
name|b
init|=
name|resolveObjectValue
argument_list|(
name|a
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|b
argument_list|,
literal|"string"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|JSONObject
name|c
init|=
name|resolveObjectValue
argument_list|(
name|a
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|assertPropertyValue
argument_list|(
name|c
argument_list|,
literal|"bool"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

