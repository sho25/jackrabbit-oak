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
name|Maps
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
comment|/**  * Takes care of applying the updates to the index content.  *<p>  * The changes are temporarily added to an in-memory structure, and then applied  * to the node.  */
end_comment

begin_class
class|class
name|Property2IndexUpdate
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
comment|/**      * The set of added values / paths. The key of the map is the property value      * (encoded as a string), the value of the map is a set of paths that where      * added.      */
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
name|insert
decl_stmt|;
comment|/**      * The set of removed values / paths. The key of the map is the property      * value (encoded as a string), the value of the map is a set of paths that      * were removed.      */
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
name|remove
decl_stmt|;
specifier|public
name|Property2IndexUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|node
parameter_list|,
name|IndexStoreStrategy
name|store
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|node
argument_list|,
name|store
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Property2IndexUpdate
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
name|insert
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|remove
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeTypeNames
operator|=
name|nodeTypeNames
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
specifier|public
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
comment|/**      * A property value was added at the given path.      *       * @param path the path      * @param value the value      */
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|,
name|PropertyState
name|value
parameter_list|)
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
name|putValues
argument_list|(
name|insert
argument_list|,
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
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * A property value was removed at the given path.      *       * @param path the path      * @param value the value      */
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
name|putValues
argument_list|(
name|remove
argument_list|,
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
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|putValues
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
parameter_list|,
name|String
name|path
parameter_list|,
name|PropertyState
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|!=
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|Property2Index
operator|.
name|encode
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
block|{
name|paths
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
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
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|boolean
name|getAndResetReindexFlag
parameter_list|()
block|{
name|boolean
name|reindex
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
operator|!=
literal|null
operator|&&
name|node
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
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
comment|/**      * Try to apply the changes to the index content (to the ":index" node.      *       * @throws CommitFailedException if a unique index was violated      */
specifier|public
name|void
name|apply
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|boolean
name|unique
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"unique"
argument_list|)
operator|!=
literal|null
operator|&&
name|node
operator|.
name|getProperty
argument_list|(
literal|"unique"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|node
operator|.
name|child
argument_list|(
literal|":index"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|remove
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|index
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|insert
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|store
operator|.
name|insert
argument_list|(
name|index
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|unique
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

