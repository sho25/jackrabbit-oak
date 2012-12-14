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
name|index
operator|.
name|property
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|concat
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|index
operator|.
name|property
operator|.
name|PropertyIndex
operator|.
name|TYPE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|ImmutableList
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
name|Type
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
name|index
operator|.
name|IndexHook
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
name|memory
operator|.
name|MemoryNodeState
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
name|NodeStateUtils
import|;
end_import

begin_comment
comment|/**  * {@link IndexHook} implementation that is responsible for keeping the  * {@link PropertyIndex} up to date.  *<p>  * There is a tree of PropertyIndexDiff objects, each object represents the  * changes at a given node.  *   * @see PropertyIndex  * @see PropertyIndexLookup  *   */
end_comment

begin_class
annotation|@
name|Deprecated
class|class
name|PropertyIndexDiff
implements|implements
name|IndexHook
block|{
comment|/**      * The parent (null if this is the root node).      */
specifier|private
specifier|final
name|PropertyIndexDiff
name|parent
decl_stmt|;
comment|/**      * The node (never null).      */
specifier|private
specifier|final
name|NodeBuilder
name|node
decl_stmt|;
comment|/**      * The node name (the path element). Null for the root node.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * The path of the changed node (built lazily).      */
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Key: the property name. Value: the list of indexes (it is possible to      * have multiple indexes for the same property name).      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
argument_list|>
name|updates
decl_stmt|;
specifier|private
name|PropertyIndexDiff
parameter_list|(
name|PropertyIndexDiff
name|parent
parameter_list|,
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
argument_list|>
name|updates
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|updates
operator|=
name|updates
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
condition|)
block|{
name|NodeBuilder
name|index
init|=
name|node
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|indexName
range|:
name|index
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeBuilder
name|child
init|=
name|index
operator|.
name|child
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|isIndexNode
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|update
argument_list|(
name|child
argument_list|,
name|indexName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|PropertyIndexDiff
parameter_list|(
name|PropertyIndexDiff
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
name|getChildNode
argument_list|(
name|parent
operator|.
name|node
argument_list|,
name|name
argument_list|)
argument_list|,
name|name
argument_list|,
literal|null
argument_list|,
name|parent
operator|.
name|updates
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyIndexDiff
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|root
argument_list|,
literal|null
argument_list|,
literal|"/"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|getChildNode
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|node
operator|.
name|child
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
comment|// build the path lazily
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/**      * Get all the indexes for the given property name.      *       * @param name the property name      * @return the indexes      */
specifier|private
name|Iterable
argument_list|<
name|PropertyIndexUpdate
argument_list|>
name|getIndexes
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
name|indexes
init|=
name|updates
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexes
operator|!=
literal|null
condition|)
block|{
return|return
name|indexes
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|update
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|builder
operator|.
name|getProperty
argument_list|(
literal|"propertyNames"
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|propertyNames
init|=
name|ps
operator|!=
literal|null
condition|?
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
else|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pname
range|:
name|propertyNames
control|)
block|{
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
name|list
init|=
name|this
operator|.
name|updates
operator|.
name|get
argument_list|(
name|pname
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|updates
operator|.
name|put
argument_list|(
name|pname
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PropertyIndexUpdate
name|piu
range|:
name|list
control|)
block|{
if|if
condition|(
name|piu
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|exists
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|exists
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|PropertyIndexUpdate
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isIndexNode
parameter_list|(
name|NodeBuilder
name|node
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
name|boolean
name|isNodeType
init|=
name|ps
operator|!=
literal|null
operator|&&
operator|!
name|ps
operator|.
name|isArray
argument_list|()
operator|&&
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|.
name|equals
argument_list|(
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isNodeType
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PropertyState
name|type
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|boolean
name|isIndexType
init|=
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|isArray
argument_list|()
operator|&&
name|type
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|.
name|equals
argument_list|(
name|TYPE
argument_list|)
decl_stmt|;
return|return
name|isIndexType
return|;
block|}
comment|//-----------------------------------------------------< NodeStateDiff>--
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
for|for
control|(
name|PropertyIndexUpdate
name|update
range|:
name|getIndexes
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
control|)
block|{
name|update
operator|.
name|insert
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|PropertyIndexUpdate
name|update
range|:
name|getIndexes
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
control|)
block|{
name|update
operator|.
name|remove
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|before
argument_list|)
expr_stmt|;
name|update
operator|.
name|insert
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|PropertyIndexUpdate
name|update
range|:
name|getIndexes
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
control|)
block|{
name|update
operator|.
name|remove
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
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
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|,
name|after
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
if|if
condition|(
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|child
argument_list|(
name|name
argument_list|)
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
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
comment|// -----------------------------------------------------< IndexHook>--
annotation|@
name|Override
specifier|public
name|void
name|apply
parameter_list|()
throws|throws
name|CommitFailedException
block|{
for|for
control|(
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
name|updateList
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|PropertyIndexUpdate
name|update
range|:
name|updateList
control|)
block|{
name|update
operator|.
name|apply
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reindex
parameter_list|(
name|NodeBuilder
name|state
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|boolean
name|reindex
init|=
literal|false
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|PropertyIndexUpdate
argument_list|>
name|updateList
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|PropertyIndexUpdate
name|update
range|:
name|updateList
control|)
block|{
if|if
condition|(
name|update
operator|.
name|getAndResetReindexFlag
argument_list|()
condition|)
block|{
name|reindex
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|reindex
condition|)
block|{
name|state
operator|.
name|getNodeState
argument_list|()
operator|.
name|compareAgainstBaseState
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|,
operator|new
name|PropertyIndexDiff
argument_list|(
literal|null
argument_list|,
name|state
argument_list|,
literal|null
argument_list|,
literal|"/"
argument_list|,
name|updates
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexHook
name|child
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|PropertyIndexDiff
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|updates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

