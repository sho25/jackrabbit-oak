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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|spi
operator|.
name|commit
operator|.
name|EmptyHook
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
name|spi
operator|.
name|state
operator|.
name|ChildNodeEntry
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
comment|/**  * Checks that traversing over many child nodes requests them in batches with  * an upper limit.  */
end_comment

begin_class
specifier|public
class|class
name|ManyChildNodesTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|manyChildNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|TestStore
name|store
init|=
operator|new
name|TestStore
argument_list|()
decl_stmt|;
name|MongoMK
name|mk
init|=
operator|new
name|MongoMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|NodeStore
name|ns
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
name|MongoNodeState
operator|.
name|MAX_FETCH_SIZE
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"c-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|ns
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|.
name|queries
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// must fetch in batches
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|entry
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
comment|// maximum fetch size is MAX_FETCH_SIZE plus one because
comment|// MongoNodeStore will use this value to find out if there
comment|// are more child nodes than requested
name|int
name|maxFetchSize
init|=
name|MongoNodeState
operator|.
name|MAX_FETCH_SIZE
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|e
range|:
name|store
operator|.
name|queries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|+
literal|"> "
operator|+
name|maxFetchSize
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|<=
name|maxFetchSize
argument_list|)
expr_stmt|;
block|}
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|TestStore
extends|extends
name|MemoryDocumentStore
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|queries
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|queries
operator|.
name|put
argument_list|(
name|fromKey
argument_list|,
name|limit
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

