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
name|core
package|;
end_package

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
name|api
operator|.
name|CoreValue
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
name|PropertyState
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
name|Root
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
name|Tree
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
name|core
operator|.
name|TreeImpl
operator|.
name|Listener
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
name|NodeState
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
name|NodeStateBuilder
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
name|NodeStateDiff
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|Iterators
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
name|util
operator|.
name|PagedIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

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
name|Iterator
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
import|import static
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
name|PathUtils
operator|.
name|elements
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
name|commons
operator|.
name|PathUtils
operator|.
name|getName
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
name|commons
operator|.
name|PathUtils
operator|.
name|getParentPath
import|;
end_import

begin_comment
comment|/**  * This {@code Root} implementation listens on the root of the underlying  * {@link Tree} using a {@link Listener}. All changes are directly applied  * to the {@link NodeStateBuilder} for the relevant sub-tree.  */
end_comment

begin_class
specifier|public
class|class
name|RootImpl
implements|implements
name|Root
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RootImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** The underlying store to which this root belongs */
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
comment|/** The name of the workspace we are operating on */
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
comment|/** Listener for changes on the tree */
specifier|private
name|TreeListener
name|treeListener
init|=
operator|new
name|TreeListener
argument_list|()
decl_stmt|;
comment|/** Base node state of this tree */
specifier|private
name|NodeState
name|base
decl_stmt|;
comment|/** The builder for this root */
specifier|private
name|NodeStateBuilder
name|nodeStateBuilder
decl_stmt|;
comment|/** Root state of this tree */
specifier|private
name|TreeImpl
name|root
decl_stmt|;
comment|/**      * New instance bases on a given {@link NodeStore} and a workspace      * @param store  node store      * @param workspaceName  name of the workspace      */
specifier|public
name|RootImpl
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|workspaceName
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|workspaceName
argument_list|)
expr_stmt|;
name|nodeStateBuilder
operator|=
name|store
operator|.
name|getBuilder
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
operator|new
name|TreeImpl
argument_list|(
name|store
argument_list|,
name|nodeStateBuilder
argument_list|,
name|treeListener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|TreeImpl
name|source
init|=
name|getChild
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TreeImpl
name|destParent
init|=
name|getChild
argument_list|(
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|destName
init|=
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
return|return
name|destParent
operator|!=
literal|null
operator|&&
name|source
operator|.
name|move
argument_list|(
name|destParent
argument_list|,
name|destName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destPath
parameter_list|)
block|{
name|TreeImpl
name|sourceNode
init|=
name|getChild
argument_list|(
name|sourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceNode
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TreeImpl
name|destParent
init|=
name|getChild
argument_list|(
name|getParentPath
argument_list|(
name|destPath
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|destName
init|=
name|getName
argument_list|(
name|destPath
argument_list|)
decl_stmt|;
return|return
name|destParent
operator|!=
literal|null
operator|&&
name|sourceNode
operator|.
name|copy
argument_list|(
name|destParent
argument_list|,
name|destName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|getChild
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rebase
parameter_list|()
block|{
name|rebase
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|store
operator|.
name|apply
argument_list|(
name|nodeStateBuilder
argument_list|)
expr_stmt|;
name|rebase
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
return|return
name|treeListener
operator|.
name|hasChanges
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Get a tree for the child identified by {@code path}      * @param path  the path to the child      * @return  a {@link Tree} instance for the child      *          at {@code path} or {@code null} if no such item exits.      */
specifier|private
name|TreeImpl
name|getChild
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeImpl
name|state
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|state
operator|=
name|state
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|state
return|;
block|}
specifier|private
name|void
name|rebase
parameter_list|(
name|boolean
name|mergeChanges
parameter_list|)
block|{
name|NodeState
name|oldBase
decl_stmt|;
name|NodeState
name|oldHead
decl_stmt|;
if|if
condition|(
name|mergeChanges
condition|)
block|{
name|oldBase
operator|=
name|base
expr_stmt|;
name|oldHead
operator|=
name|nodeStateBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|oldBase
operator|=
literal|null
expr_stmt|;
name|oldHead
operator|=
literal|null
expr_stmt|;
block|}
name|treeListener
operator|=
operator|new
name|TreeListener
argument_list|()
expr_stmt|;
name|base
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|workspaceName
argument_list|)
expr_stmt|;
name|nodeStateBuilder
operator|=
name|store
operator|.
name|getBuilder
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|TreeImpl
argument_list|(
name|store
argument_list|,
name|nodeStateBuilder
argument_list|,
name|treeListener
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeChanges
condition|)
block|{
name|merge
argument_list|(
name|oldBase
argument_list|,
name|oldHead
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|merge
parameter_list|(
name|NodeState
name|fromState
parameter_list|,
name|NodeState
name|toState
parameter_list|,
specifier|final
name|Tree
name|target
parameter_list|)
block|{
name|store
operator|.
name|compare
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
operator|new
name|NodeStateDiff
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|setProperty
argument_list|(
name|after
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|setProperty
argument_list|(
name|after
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|target
operator|.
name|removeProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|addChild
argument_list|(
name|name
argument_list|,
name|after
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|Tree
name|child
init|=
name|target
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|merge
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|target
operator|.
name|removeChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addChild
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|state
parameter_list|,
name|Tree
name|target
parameter_list|)
block|{
name|Tree
name|child
init|=
name|target
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|setProperty
argument_list|(
name|property
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|childNodeEntries
argument_list|(
name|state
argument_list|)
control|)
block|{
name|addChild
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|Tree
name|target
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|target
operator|.
name|setProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|toList
argument_list|(
name|property
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|.
name|setProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|childNodeEntries
parameter_list|(
specifier|final
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|()
block|{
comment|// Java's type system is too weak to express the exact type here
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|flatten
argument_list|(
operator|new
name|PagedIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
literal|1024
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getPage
parameter_list|(
name|long
name|pos
parameter_list|,
name|int
name|size
parameter_list|)
block|{
return|return
name|nodeState
operator|.
name|getChildNodeEntries
argument_list|(
name|pos
argument_list|,
name|size
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|toList
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|T
name|value
range|:
name|values
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
specifier|private
specifier|static
class|class
name|TreeListener
implements|implements
name|Listener
block|{
specifier|private
name|boolean
name|hasChanges
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|addChild
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeChild
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
name|TreeImpl
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|TreeImpl
name|sourceParent
parameter_list|,
name|String
name|sourceName
parameter_list|,
name|TreeImpl
name|moved
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|TreeImpl
name|sourceParent
parameter_list|,
name|String
name|sourceName
parameter_list|,
name|TreeImpl
name|copied
parameter_list|)
block|{
name|hasChanges
operator|=
literal|true
expr_stmt|;
block|}
name|boolean
name|hasChanges
parameter_list|()
block|{
return|return
name|hasChanges
return|;
block|}
block|}
block|}
end_class

end_unit

