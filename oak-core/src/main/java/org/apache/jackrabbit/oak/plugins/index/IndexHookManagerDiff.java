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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|commons
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
name|NodeStateUtils
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
name|IndexConstants
operator|.
name|TYPE_UNKNOWN
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

begin_comment
comment|/**  * Acts as a composite NodeStateDiff, it delegates all the diff's events to the  * existing IndexHooks.  *   * This allows for a simultaneous update of all the indexes via a single  * traversal of the changes.  */
end_comment

begin_class
class|class
name|IndexHookManagerDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexHookManagerDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexHookProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|IndexHookManagerDiff
name|parent
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|node
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * The map of known indexes.      *       * Key: index type name ("p2"). Value: a map from path to index hook.      */
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
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|>
name|indexMap
decl_stmt|;
specifier|public
name|IndexHookManagerDiff
parameter_list|(
name|IndexHookProvider
name|provider
parameter_list|,
name|NodeBuilder
name|root
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|>
name|updates
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|this
argument_list|(
name|provider
argument_list|,
literal|null
argument_list|,
name|root
argument_list|,
literal|null
argument_list|,
literal|"/"
argument_list|,
name|updates
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexHookManagerDiff
parameter_list|(
name|IndexHookProvider
name|provider
parameter_list|,
name|IndexHookManagerDiff
name|parent
parameter_list|,
name|String
name|nodeName
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|this
argument_list|(
name|provider
argument_list|,
name|parent
argument_list|,
name|getChildNode
argument_list|(
name|parent
operator|.
name|node
argument_list|,
name|nodeName
argument_list|)
argument_list|,
name|nodeName
argument_list|,
literal|null
argument_list|,
name|parent
operator|.
name|indexMap
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexHookManagerDiff
parameter_list|(
name|IndexHookProvider
name|provider
parameter_list|,
name|IndexHookManagerDiff
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
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|>
name|indexMap
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
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
name|nodeName
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
name|indexMap
operator|=
name|indexMap
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|isIndexNodeType
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
condition|)
block|{
comment|// to prevent double-reindex we only call reindex if:
comment|// - the flag exists and is set to true
comment|// OR
comment|// - the flag does not exist
name|boolean
name|reindex
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
operator|==
literal|null
operator|||
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
if|if
condition|(
name|reindex
condition|)
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|type
init|=
name|TYPE_UNKNOWN
decl_stmt|;
name|PropertyState
name|typePS
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|typePS
operator|!=
literal|null
operator|&&
operator|!
name|typePS
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|type
operator|=
name|typePS
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
comment|// TODO this is kinda fragile
name|NodeBuilder
name|rebuildBuilder
init|=
name|parent
operator|.
name|parent
operator|.
name|node
decl_stmt|;
name|String
name|relativePath
init|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|getPath
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// find parent index by type and trigger a full reindex
name|List
argument_list|<
name|IndexHook
argument_list|>
name|indexes
init|=
name|getIndexesWithRelativePaths
argument_list|(
name|relativePath
argument_list|,
name|getIndexes
argument_list|(
name|type
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexHook
name|ih
range|:
name|indexes
control|)
block|{
name|ih
operator|.
name|reindex
argument_list|(
name|rebuildBuilder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|Set
argument_list|<
name|String
argument_list|>
name|existingTypes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|reindexTypes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
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
name|indexChild
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
name|isIndexNodeType
argument_list|(
name|indexChild
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
condition|)
block|{
comment|// this reindex should only happen when the flag is set
comment|// before the index impl is online
name|boolean
name|reindex
init|=
name|indexChild
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|)
operator|!=
literal|null
operator|&&
name|indexChild
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
name|String
name|type
init|=
name|TYPE_UNKNOWN
decl_stmt|;
name|PropertyState
name|typePS
init|=
name|indexChild
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|typePS
operator|!=
literal|null
operator|&&
operator|!
name|typePS
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|type
operator|=
name|typePS
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reindex
condition|)
block|{
name|reindexTypes
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|existingTypes
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
name|existingTypes
operator|.
name|remove
argument_list|(
name|TYPE_UNKNOWN
argument_list|)
expr_stmt|;
name|reindexTypes
operator|.
name|remove
argument_list|(
name|TYPE_UNKNOWN
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|existingTypes
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|byType
init|=
name|this
operator|.
name|indexMap
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|byType
operator|==
literal|null
condition|)
block|{
name|byType
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexMap
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|byType
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|IndexHook
argument_list|>
name|hooks
init|=
name|byType
operator|.
name|get
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|hooks
operator|==
literal|null
condition|)
block|{
name|hooks
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|byType
operator|.
name|put
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|hooks
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reindexTypes
operator|.
name|contains
argument_list|(
name|type
argument_list|)
condition|)
block|{
for|for
control|(
name|IndexHook
name|ih
range|:
name|provider
operator|.
name|getIndexHooks
argument_list|(
name|type
argument_list|,
name|node
argument_list|)
control|)
block|{
name|ih
operator|.
name|reindex
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// TODO proper cleanup of resources in the case of an
comment|// exception?
name|hooks
operator|.
name|add
argument_list|(
name|ih
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|hooks
operator|.
name|addAll
argument_list|(
name|provider
operator|.
name|getIndexHooks
argument_list|(
name|type
argument_list|,
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|nodeName
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
name|nodeName
argument_list|)
condition|)
block|{
return|return
name|node
operator|.
name|child
argument_list|(
name|nodeName
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
specifier|private
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
comment|// => parent != null
name|path
operator|=
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/**      * Returns IndexHooks of all types that have the best match (are situated      * the closest on the hierarchy) for the given path.      */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|getIndexes
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|hooks
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|this
operator|.
name|indexMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|newIndexes
init|=
name|getIndexes
argument_list|(
name|type
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|newIndexes
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|hooks
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|hooks
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|addAll
argument_list|(
name|newIndexes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hooks
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|newIndexes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|hooks
return|;
block|}
comment|/**      * Returns IndexHooks of the given type that have the best match (are      * situated the closest on the hierarchy) for the current path.      *       * @param type the index type ("p2")      */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|getIndexes
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|hooks
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|indexes
init|=
name|this
operator|.
name|indexMap
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexes
operator|!=
literal|null
operator|&&
operator|!
name|indexes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|indexes
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|bestMatch
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|key
argument_list|,
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|bestMatch
operator|=
name|key
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|List
argument_list|<
name|IndexHook
argument_list|>
name|existing
init|=
name|hooks
operator|.
name|get
argument_list|(
name|bestMatch
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|==
literal|null
condition|)
block|{
name|existing
operator|=
operator|new
name|ArrayList
argument_list|<
name|IndexHook
argument_list|>
argument_list|()
expr_stmt|;
name|hooks
operator|.
name|put
argument_list|(
name|bestMatch
argument_list|,
name|existing
argument_list|)
expr_stmt|;
block|}
name|existing
operator|.
name|addAll
argument_list|(
name|indexes
operator|.
name|get
argument_list|(
name|bestMatch
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|hooks
return|;
block|}
comment|/**      * Fixes the relative paths on the best matching indexes so updates apply      * properly      */
specifier|private
specifier|static
name|List
argument_list|<
name|IndexHook
argument_list|>
name|getIndexesWithRelativePaths
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
name|bestMatches
parameter_list|)
block|{
name|List
argument_list|<
name|IndexHook
argument_list|>
name|hooks
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexHook
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|relativePath
range|:
name|bestMatches
operator|.
name|keySet
argument_list|()
control|)
block|{
for|for
control|(
name|IndexHook
name|update
range|:
name|bestMatches
operator|.
name|get
argument_list|(
name|relativePath
argument_list|)
control|)
block|{
name|IndexHook
name|u
init|=
name|update
decl_stmt|;
name|String
name|downPath
init|=
name|path
operator|.
name|substring
argument_list|(
name|relativePath
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|downPath
argument_list|)
control|)
block|{
name|u
operator|=
name|u
operator|.
name|child
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|hooks
operator|.
name|add
argument_list|(
name|u
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|hooks
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isIndexNodeType
parameter_list|(
name|PropertyState
name|ps
parameter_list|)
block|{
return|return
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
return|;
block|}
comment|// -----------------------------------------------------< NodeStateDiff>---
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
name|IndexHook
name|update
range|:
name|getIndexesWithRelativePaths
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|getIndexes
argument_list|()
argument_list|)
control|)
block|{
name|update
operator|.
name|propertyAdded
argument_list|(
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
name|IndexHook
name|update
range|:
name|getIndexesWithRelativePaths
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|getIndexes
argument_list|()
argument_list|)
control|)
block|{
name|update
operator|.
name|propertyChanged
argument_list|(
name|before
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
name|IndexHook
name|update
range|:
name|getIndexesWithRelativePaths
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|getIndexes
argument_list|()
argument_list|)
control|)
block|{
name|update
operator|.
name|propertyDeleted
argument_list|(
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
name|nodeName
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|childNodeChanged
argument_list|(
name|nodeName
argument_list|,
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
name|nodeName
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
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
return|return;
block|}
name|getIndexesWithRelativePaths
argument_list|(
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|nodeName
argument_list|)
argument_list|,
name|getIndexes
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|IndexHookManagerDiff
argument_list|(
name|provider
argument_list|,
name|this
argument_list|,
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// TODO ignore exception - is this a hack?
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|nodeName
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|childNodeChanged
argument_list|(
name|nodeName
argument_list|,
name|before
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

