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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
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
name|commons
operator|.
name|FixturesHelper
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
name|DocumentMemoryFixture
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
name|DocumentMongoFixture
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
name|DocumentRdbFixture
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
name|MemoryFixture
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
name|NodeStoreFixture
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
name|multiplex
operator|.
name|MultiplexingMemoryFixture
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
name|multiplex
operator|.
name|MultiplexingSegmentFixture
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
name|segment
operator|.
name|fixture
operator|.
name|SegmentTarFixture
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStoreFixtures
block|{
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|MEMORY_NS
init|=
operator|new
name|MemoryFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|SEGMENT_TAR
init|=
operator|new
name|SegmentTarFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|DOCUMENT_NS
init|=
operator|new
name|DocumentMongoFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|DOCUMENT_RDB
init|=
operator|new
name|DocumentRdbFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|DOCUMENT_MEM
init|=
operator|new
name|DocumentMemoryFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|MULTIPLEXED_SEGMENT
init|=
operator|new
name|MultiplexingSegmentFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|NodeStoreFixture
name|MULTIPLEXED_MEM
init|=
operator|new
name|MultiplexingMemoryFixture
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|asJunitParameters
parameter_list|(
name|Set
argument_list|<
name|FixturesHelper
operator|.
name|Fixture
argument_list|>
name|fixtures
parameter_list|)
block|{
name|List
argument_list|<
name|NodeStoreFixture
argument_list|>
name|configuredFixtures
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeStoreFixture
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|DOCUMENT_NS
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|DOCUMENT_NS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|MEMORY_NS
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|MEMORY_NS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|DOCUMENT_RDB
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|DOCUMENT_RDB
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|DOCUMENT_MEM
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|DOCUMENT_MEM
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|SEGMENT_TAR
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|SEGMENT_TAR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|MULTIPLEXED_SEGMENT
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|MULTIPLEXED_SEGMENT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fixtures
operator|.
name|contains
argument_list|(
name|FixturesHelper
operator|.
name|Fixture
operator|.
name|MULTIPLEXED_MEM
argument_list|)
condition|)
block|{
name|configuredFixtures
operator|.
name|add
argument_list|(
name|MULTIPLEXED_MEM
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeStoreFixture
name|f
range|:
name|configuredFixtures
control|)
block|{
if|if
condition|(
name|f
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|f
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

