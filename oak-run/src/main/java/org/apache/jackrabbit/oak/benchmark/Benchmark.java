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
name|benchmark
package|;
end_package

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
name|fixture
operator|.
name|JackrabbitRepositoryFixture
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
specifier|public
class|class
name|Benchmark
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RepositoryFixture
argument_list|>
name|FIXTURES
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|RepositoryFixture
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"Jackrabbit"
argument_list|,
operator|new
name|JackrabbitRepositoryFixture
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"Oak-Memory"
argument_list|,
name|OakRepositoryFixture
operator|.
name|getMemory
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"Oak-Default"
argument_list|,
name|OakRepositoryFixture
operator|.
name|getDefault
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"Oak-Mongo"
argument_list|,
name|OakRepositoryFixture
operator|.
name|getMongo
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"Oak-NewMongo"
argument_list|,
name|OakRepositoryFixture
operator|.
name|getNewMongo
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"Oak-Segment"
argument_list|,
name|OakRepositoryFixture
operator|.
name|getSegment
argument_list|()
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AbstractTest
argument_list|>
name|TESTS
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|AbstractTest
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"Login"
argument_list|,
operator|new
name|LoginTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"LoginLogout"
argument_list|,
operator|new
name|LoginLogoutTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"GetProperty"
argument_list|,
operator|new
name|ReadPropertyTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"SetProperty"
argument_list|,
operator|new
name|SetPropertyTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"SmallRead"
argument_list|,
operator|new
name|SmallFileReadTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"SmallWrite"
argument_list|,
operator|new
name|SmallFileWriteTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"ConcurrentRead"
argument_list|,
operator|new
name|ConcurrentReadTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"ConcurrentWrite"
argument_list|,
operator|new
name|ConcurrentReadWriteTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"SimpleSearch"
argument_list|,
operator|new
name|SimpleSearchTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"SQL2"
argument_list|,
operator|new
name|SQL2SearchTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"Descendant"
argument_list|,
operator|new
name|DescendantSearchTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"SQL2Descendant"
argument_list|,
operator|new
name|SQL2DescendantSearchTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"CreateFlatNode"
argument_list|,
operator|new
name|CreateManyChildNodesTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"UpdateFlatNode"
argument_list|,
operator|new
name|UpdateManyChildNodesTest
argument_list|()
argument_list|)
decl|.
name|put
argument_list|(
literal|"TransientSpace"
argument_list|,
operator|new
name|TransientManyChildNodesTest
argument_list|()
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|RepositoryFixture
argument_list|>
name|fixtures
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AbstractTest
argument_list|>
name|tests
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|args
control|)
block|{
if|if
condition|(
name|FIXTURES
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|fixtures
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|FIXTURES
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TESTS
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|tests
operator|.
name|add
argument_list|(
name|TESTS
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown argument: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|fixtures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fixtures
operator|.
name|putAll
argument_list|(
name|FIXTURES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|tests
operator|.
name|addAll
argument_list|(
name|TESTS
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AbstractTest
name|test
range|:
name|tests
control|)
block|{
name|test
operator|.
name|run
argument_list|(
name|fixtures
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

