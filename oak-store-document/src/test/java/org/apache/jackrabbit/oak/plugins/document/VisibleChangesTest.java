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
name|document
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
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|api
operator|.
name|CommitFailedException
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
name|memory
operator|.
name|MemoryDocumentStore
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
name|version
operator|.
name|VersionablePathHook
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
name|CommitInfo
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
name|NodeBuilder
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
name|util
operator|.
name|Utils
operator|.
name|getIdFromPath
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  *<code>VisibleChangesTest</code>...  */
end_comment

begin_class
specifier|public
class|class
name|VisibleChangesTest
block|{
comment|// OAK-3019
annotation|@
name|Test
specifier|public
name|void
name|versionablePathHook
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|TestStore
name|store
init|=
operator|new
name|TestStore
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|ns
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
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
name|builder
operator|.
name|child
argument_list|(
literal|":hidden"
argument_list|)
expr_stmt|;
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
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|builder
operator|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|hidden
init|=
name|builder
operator|.
name|child
argument_list|(
literal|":hidden"
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
operator|<=
name|DocumentMK
operator|.
name|MANY_CHILDREN_THRESHOLD
condition|;
name|i
operator|++
control|)
block|{
name|hidden
operator|.
name|child
argument_list|(
literal|"child-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|// add more changes until a branch is created
name|NodeBuilder
name|foo
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|int
name|numRevs
init|=
name|getRevisionsSize
argument_list|(
name|store
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
while|while
condition|(
name|numRevs
operator|==
name|getRevisionsSize
argument_list|(
name|store
argument_list|,
literal|"/"
argument_list|)
condition|)
block|{
name|foo
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|foo
operator|.
name|removeProperty
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|paths
operator|.
name|clear
argument_list|()
expr_stmt|;
name|VersionablePathHook
name|hook
init|=
operator|new
name|VersionablePathHook
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|ns
operator|.
name|getRoot
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must not query for hidden paths: "
operator|+
name|store
operator|.
name|paths
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|store
operator|.
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|int
name|getRevisionsSize
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestStore
extends|extends
name|MemoryDocumentStore
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
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
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|indexedProperty
operator|!=
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|fromKey
argument_list|)
expr_stmt|;
block|}
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
name|indexedProperty
argument_list|,
name|startValue
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
