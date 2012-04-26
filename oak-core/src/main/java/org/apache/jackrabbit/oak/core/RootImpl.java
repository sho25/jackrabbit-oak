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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|mk
operator|.
name|util
operator|.
name|PathUtils
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
name|kernel
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
name|kernel
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
name|kernel
operator|.
name|NodeStore
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
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
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
name|mk
operator|.
name|util
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
name|mk
operator|.
name|util
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
name|mk
operator|.
name|util
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
name|applyChanges
parameter_list|)
block|{
name|TreeListener
name|changes
init|=
name|treeListener
decl_stmt|;
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
name|applyChanges
condition|)
block|{
name|apply
argument_list|(
name|changes
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|apply
parameter_list|(
name|TreeListener
name|changes
parameter_list|)
block|{
for|for
control|(
name|Operation
name|operation
range|:
name|changes
operator|.
name|getChanges
argument_list|()
control|)
block|{
try|try
block|{
switch|switch
condition|(
name|operation
operator|.
name|type
condition|)
block|{
case|case
name|ADD_NODE
case|:
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|getChild
argument_list|(
name|parentPath
argument_list|)
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|REMOVE_NODE
case|:
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|getChild
argument_list|(
name|parentPath
argument_list|)
operator|.
name|removeChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|SET_PROPERTY
case|:
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|operation
operator|.
name|isMultiple
condition|)
block|{
name|getChild
argument_list|(
name|parentPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|operation
operator|.
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getChild
argument_list|(
name|parentPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|operation
operator|.
name|values
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
name|REMOVE_PROPERTY
case|:
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|operation
operator|.
name|targetPath
argument_list|)
decl_stmt|;
name|getChild
argument_list|(
name|parentPath
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|MOVE
case|:
block|{
name|move
argument_list|(
name|operation
operator|.
name|sourcePath
argument_list|,
name|operation
operator|.
name|targetPath
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|COPY
case|:
block|{
name|copy
argument_list|(
name|operation
operator|.
name|sourcePath
argument_list|,
name|operation
operator|.
name|targetPath
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping failed operation on refresh:"
operator|+
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|TreeListener
implements|implements
name|Listener
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Operation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<
name|Operation
argument_list|>
argument_list|()
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
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|addNode
argument_list|(
name|targetPath
argument_list|)
argument_list|)
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
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|removeNode
argument_list|(
name|targetPath
argument_list|)
argument_list|)
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
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|setProperty
argument_list|(
name|targetPath
argument_list|,
name|value
argument_list|)
argument_list|)
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
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|setProperty
argument_list|(
name|targetPath
argument_list|,
name|values
argument_list|)
argument_list|)
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
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|removeProperty
argument_list|(
name|targetPath
argument_list|)
argument_list|)
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
name|String
name|sourcePath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|sourceParent
operator|.
name|getPath
argument_list|()
argument_list|,
name|sourceName
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|move
argument_list|(
name|sourcePath
argument_list|,
name|moved
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
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
name|String
name|sourcePath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|sourceParent
operator|.
name|getPath
argument_list|()
argument_list|,
name|sourceName
argument_list|)
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|Operation
operator|.
name|copy
argument_list|(
name|sourcePath
argument_list|,
name|copied
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|hasChanges
parameter_list|()
block|{
return|return
operator|!
name|operations
operator|.
name|isEmpty
argument_list|()
return|;
block|}
name|List
argument_list|<
name|Operation
argument_list|>
name|getChanges
parameter_list|()
block|{
return|return
name|operations
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Operation
block|{
specifier|final
name|Type
name|type
decl_stmt|;
specifier|final
name|String
name|targetPath
decl_stmt|;
specifier|final
name|String
name|sourcePath
decl_stmt|;
specifier|final
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
decl_stmt|;
specifier|final
name|boolean
name|isMultiple
decl_stmt|;
enum|enum
name|Type
block|{
name|ADD_NODE
block|,
name|REMOVE_NODE
block|,
name|SET_PROPERTY
block|,
name|REMOVE_PROPERTY
block|,
name|MOVE
block|,
name|COPY
block|}
specifier|private
name|Operation
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|targetPath
parameter_list|,
name|String
name|sourcePath
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|,
name|boolean
name|isMultiple
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|targetPath
operator|=
name|targetPath
expr_stmt|;
name|this
operator|.
name|sourcePath
operator|=
name|sourcePath
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|isMultiple
operator|=
name|isMultiple
expr_stmt|;
block|}
specifier|static
name|Operation
name|addNode
parameter_list|(
name|String
name|targetPath
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|ADD_NODE
argument_list|,
name|targetPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|static
name|Operation
name|removeNode
parameter_list|(
name|String
name|targetPath
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|REMOVE_NODE
argument_list|,
name|targetPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|static
name|Operation
name|setProperty
parameter_list|(
name|String
name|targetPath
parameter_list|,
name|CoreValue
name|value
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|SET_PROPERTY
argument_list|,
name|targetPath
argument_list|,
literal|null
argument_list|,
name|singletonList
argument_list|(
name|value
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|static
name|Operation
name|setProperty
parameter_list|(
name|String
name|targetPath
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|SET_PROPERTY
argument_list|,
name|targetPath
argument_list|,
literal|null
argument_list|,
name|values
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|static
name|Operation
name|removeProperty
parameter_list|(
name|String
name|targetPath
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|REMOVE_PROPERTY
argument_list|,
name|targetPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|static
name|Operation
name|move
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|targetPath
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|MOVE
argument_list|,
name|targetPath
argument_list|,
name|sourcePath
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|static
name|Operation
name|copy
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|targetPath
parameter_list|)
block|{
return|return
operator|new
name|Operation
argument_list|(
name|Type
operator|.
name|COPY
argument_list|,
name|targetPath
argument_list|,
name|sourcePath
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|ADD_NODE
case|:
return|return
literal|'+'
operator|+
name|targetPath
operator|+
literal|":{}"
return|;
case|case
name|REMOVE_NODE
case|:
return|return
literal|'-'
operator|+
name|targetPath
return|;
case|case
name|SET_PROPERTY
case|:
return|return
literal|'^'
operator|+
name|targetPath
operator|+
literal|':'
operator|+
operator|(
name|isMultiple
condition|?
name|values
else|:
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
return|;
case|case
name|REMOVE_PROPERTY
case|:
return|return
literal|'^'
operator|+
name|targetPath
operator|+
literal|":null"
return|;
case|case
name|MOVE
case|:
return|return
literal|'>'
operator|+
name|sourcePath
operator|+
literal|':'
operator|+
name|targetPath
return|;
case|case
name|COPY
case|:
return|return
literal|'*'
operator|+
name|sourcePath
operator|+
literal|':'
operator|+
name|targetPath
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"We should never get here"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

