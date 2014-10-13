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
name|lucene
operator|.
name|util
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
name|ImmutableSet
operator|.
name|of
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_BINARY
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_STRING
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
name|JcrConstants
operator|.
name|JCR_UUID
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
name|api
operator|.
name|Type
operator|.
name|NAME
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
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
name|ASYNC_PROPERTY_NAME
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|EXCLUDE_PROPERTY_NAMES
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|EXPERIMENTAL_STORAGE
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|INCLUDE_PROPERTY_TYPES
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_FILE
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_NAME
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|PERSISTENCE_PATH
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
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
name|PropertyStates
operator|.
name|createProperty
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConstants
operator|.
name|GROUP_PROPERTY_NAMES
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConstants
operator|.
name|USER_PROPERTY_NAMES
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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

begin_class
specifier|public
class|class
name|LuceneIndexHelper
block|{
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|JR_PROPERTY_INCLUDES
init|=
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|,
name|TYPENAME_BINARY
argument_list|)
decl_stmt|;
comment|/**      * Nodes that represent content that shold not be tokenized (like UUIDs,      * etc)      *       */
specifier|private
specifier|final
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|NOT_TOKENIZED
init|=
name|newHashSet
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
static|static
block|{
name|NOT_TOKENIZED
operator|.
name|addAll
argument_list|(
name|USER_PROPERTY_NAMES
argument_list|)
expr_stmt|;
name|NOT_TOKENIZED
operator|.
name|addAll
argument_list|(
name|GROUP_PROPERTY_NAMES
argument_list|)
expr_stmt|;
block|}
specifier|private
name|LuceneIndexHelper
parameter_list|()
block|{     }
specifier|public
specifier|static
name|NodeBuilder
name|newLuceneIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|)
block|{
return|return
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
name|name
argument_list|,
name|propertyTypes
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|newLuceneIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|,
annotation|@
name|Nullable
name|String
name|async
parameter_list|)
block|{
return|return
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
name|name
argument_list|,
name|propertyTypes
argument_list|,
name|excludes
argument_list|,
name|async
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|newLuceneIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|,
annotation|@
name|Nullable
name|String
name|async
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|stored
parameter_list|)
block|{
if|if
condition|(
name|index
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|index
operator|.
name|child
argument_list|(
name|name
argument_list|)
return|;
block|}
name|index
operator|=
name|index
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|TYPE_LUCENE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|async
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|propertyTypes
operator|!=
literal|null
operator|&&
operator|!
name|propertyTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_TYPES
argument_list|,
name|propertyTypes
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|excludes
operator|!=
literal|null
operator|&&
operator|!
name|excludes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|EXCLUDE_PROPERTY_NAMES
argument_list|,
name|excludes
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stored
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|EXPERIMENTAL_STORAGE
argument_list|,
name|stored
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|newLuceneFileIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
return|return
name|newLuceneFileIndexDefinition
argument_list|(
name|index
argument_list|,
name|name
argument_list|,
name|propertyTypes
argument_list|,
literal|null
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|newLuceneFileIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|String
name|async
parameter_list|)
block|{
if|if
condition|(
name|index
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|index
operator|.
name|child
argument_list|(
name|name
argument_list|)
return|;
block|}
name|index
operator|=
name|index
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|TYPE_LUCENE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PERSISTENCE_NAME
argument_list|,
name|PERSISTENCE_FILE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PERSISTENCE_PATH
argument_list|,
name|path
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|async
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|propertyTypes
operator|!=
literal|null
operator|&&
operator|!
name|propertyTypes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_TYPES
argument_list|,
name|propertyTypes
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|excludes
operator|!=
literal|null
operator|&&
operator|!
name|excludes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|index
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|EXCLUDE_PROPERTY_NAMES
argument_list|,
name|excludes
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
comment|/**      * Nodes that represent UUIDs and shold not be tokenized      *       */
specifier|public
specifier|static
name|boolean
name|skipTokenization
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|NOT_TOKENIZED
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isLuceneIndexNode
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
return|return
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

