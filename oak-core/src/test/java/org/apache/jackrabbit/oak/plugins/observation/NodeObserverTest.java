begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|observation
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|assertTrue
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
name|ImmutableSet
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
name|NodeState
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
name|NodeObserverTest
block|{
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"m"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"m"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"extra"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"m"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"o"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"q"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|before
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
specifier|private
name|TestNodeObserver
name|nodeObserver
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|nodeObserver
operator|=
operator|new
name|TestNodeObserver
argument_list|(
literal|"/m/n"
argument_list|,
literal|"extra"
argument_list|)
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
name|before
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNode
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"m"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"new"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
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
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/m/n/new"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"p"
argument_list|)
argument_list|)
argument_list|,
name|nodeObserver
operator|.
name|added
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|deleted
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|changed
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteNode
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"m"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"n"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"o"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
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
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|added
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/m/n/o"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"q"
argument_list|)
argument_list|)
argument_list|,
name|nodeObserver
operator|.
name|deleted
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|changed
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeNode
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"m"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"n"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
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
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|added
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|deleted
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/m/n"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"p"
argument_list|)
argument_list|)
argument_list|,
name|nodeObserver
operator|.
name|changed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"/m/n"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"extra"
argument_list|,
literal|"42"
argument_list|)
argument_list|)
argument_list|,
name|nodeObserver
operator|.
name|properties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ignoreAdd
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"new"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
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
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|added
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|deleted
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|changed
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ignoreDelete
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
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
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|added
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|deleted
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|changed
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ignoreChange
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|before
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|nodeObserver
operator|.
name|contentChanged
argument_list|(
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
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|added
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|deleted
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|changed
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeObserver
operator|.
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< TestNodeObserver>---
specifier|private
specifier|static
class|class
name|TestNodeObserver
extends|extends
name|NodeObserver
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|added
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|deleted
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|changed
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|properties
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|TestNodeObserver
parameter_list|(
name|String
name|path
parameter_list|,
name|String
modifier|...
name|propertyNames
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|propertyNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|added
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|changed
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
name|this
operator|.
name|added
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|newHashSet
argument_list|(
name|added
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|newHashMap
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|deleted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|changed
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
name|this
operator|.
name|deleted
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|newHashSet
argument_list|(
name|deleted
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|newHashMap
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|changed
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|changed
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
name|this
operator|.
name|changed
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|newHashSet
argument_list|(
name|changed
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|newHashMap
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

