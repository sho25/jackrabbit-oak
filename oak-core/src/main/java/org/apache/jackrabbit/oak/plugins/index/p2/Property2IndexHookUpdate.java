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
name|p2
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|p2
operator|.
name|Property2Index
operator|.
name|encode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|p2
operator|.
name|strategy
operator|.
name|IndexStoreStrategy
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
name|query
operator|.
name|PropertyValues
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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

begin_comment
comment|/**  * Takes care of applying the updates to the index content.  */
end_comment

begin_class
class|class
name|Property2IndexHookUpdate
block|{
specifier|private
specifier|final
name|IndexStoreStrategy
name|store
decl_stmt|;
comment|/**      * The path of the index definition (where the index data is stored).      */
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
comment|/**      * The node types that this index applies to. If<code>null</code> or      *<code>empty</code> then the node type of the indexed node is ignored      *       */
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nodeTypeNames
decl_stmt|;
comment|/**      * The node where the index definition is stored.      */
specifier|private
specifier|final
name|NodeBuilder
name|node
decl_stmt|;
comment|/**      * The node where the index content is stored.      */
specifier|private
specifier|final
name|NodeBuilder
name|index
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|unique
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|modifiedKeys
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|public
name|Property2IndexHookUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|node
parameter_list|,
name|IndexStoreStrategy
name|store
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|nodeTypeNames
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|nodeTypeNames
operator|=
name|nodeTypeNames
expr_stmt|;
name|index
operator|=
name|this
operator|.
name|node
operator|.
name|child
argument_list|(
literal|":index"
argument_list|)
expr_stmt|;
name|PropertyState
name|uniquePS
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"unique"
argument_list|)
decl_stmt|;
name|unique
operator|=
name|uniquePS
operator|!=
literal|null
operator|&&
operator|!
name|uniquePS
operator|.
name|isArray
argument_list|()
operator|&&
name|uniquePS
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
block|}
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|getNodeTypeNames
parameter_list|()
block|{
return|return
name|nodeTypeNames
return|;
block|}
comment|/**      * A property value was added at the given path.      *       * @param path      *            the path      * @param value      *            the value      */
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|,
name|PropertyState
name|value
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|key
range|:
name|encode
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|value
argument_list|)
argument_list|)
control|)
block|{
name|store
operator|.
name|insert
argument_list|(
name|index
argument_list|,
name|key
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|trimm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|modifiedKeys
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * A property value was removed at the given path.      *       * @param path      *            the path      * @param value      *            the value      */
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|,
name|PropertyState
name|value
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|key
range|:
name|encode
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|value
argument_list|)
argument_list|)
control|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|index
argument_list|,
name|key
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|trimm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|modifiedKeys
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|checkUniqueKeys
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|unique
operator|&&
operator|!
name|modifiedKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NodeState
name|state
init|=
name|index
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|modifiedKeys
control|)
block|{
if|if
condition|(
name|store
operator|.
name|count
argument_list|(
name|state
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|key
argument_list|)
argument_list|,
literal|2
argument_list|)
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Constraint"
argument_list|,
literal|30
argument_list|,
literal|"Uniqueness constraint violated for key "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|String
name|trimm
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|"/"
return|;
block|}
return|return
name|path
return|;
block|}
name|boolean
name|getAndResetReindexFlag
parameter_list|()
block|{
name|PropertyState
name|reindexPS
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|boolean
name|reindex
init|=
name|reindexPS
operator|==
literal|null
operator|||
operator|(
name|reindexPS
operator|!=
literal|null
operator|&&
name|reindexPS
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
operator|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|reindex
return|;
block|}
block|}
end_class

end_unit

