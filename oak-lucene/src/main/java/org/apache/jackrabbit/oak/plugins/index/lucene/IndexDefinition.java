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
name|index
operator|.
name|lucene
package|;
end_package

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
name|CheckForNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
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
name|lucene
operator|.
name|util
operator|.
name|LuceneIndexHelper
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
name|lucene
operator|.
name|codecs
operator|.
name|Codec
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
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
name|ENTRY_COUNT_PROPERTY_NAME
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
name|REINDEX_COUNT
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
name|BLOB_SIZE
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
name|FULL_TEXT_ENABLED
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
name|INCLUDE_PROPERTY_NAMES
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
name|ORDERED_PROP_NAMES
import|;
end_import

begin_class
class|class
name|IndexDefinition
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexDefinition
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Blob size to use by default. To avoid issues in OAK-2105 the size should not      * be power of 2.      */
specifier|static
specifier|final
name|int
name|DEFAULT_BLOB_SIZE
init|=
name|OakDirectory
operator|.
name|DEFAULT_BLOB_SIZE
operator|-
literal|300
decl_stmt|;
comment|/**      * Default entry count to keep estimated entry count low.      */
specifier|static
specifier|final
name|long
name|DEFAULT_ENTRY_COUNT
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|int
name|propertyTypes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|orderedProps
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|declaringNodeTypes
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|fullTextEnabled
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|storageEnabled
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definition
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyDefinition
argument_list|>
name|propDefns
decl_stmt|;
specifier|private
specifier|final
name|String
name|funcName
decl_stmt|;
specifier|private
specifier|final
name|int
name|blobSize
decl_stmt|;
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
comment|/**      * Defines the maximum estimated entry count configured.      * Defaults to {#DEFAULT_ENTRY_COUNT}      */
specifier|private
specifier|final
name|long
name|entryCount
decl_stmt|;
specifier|public
name|IndexDefinition
parameter_list|(
name|NodeBuilder
name|defn
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|defn
expr_stmt|;
name|PropertyState
name|pst
init|=
name|defn
operator|.
name|getProperty
argument_list|(
name|INCLUDE_PROPERTY_TYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|pst
operator|!=
literal|null
condition|)
block|{
name|int
name|types
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|inc
range|:
name|pst
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
try|try
block|{
name|types
operator||=
literal|1
operator|<<
name|PropertyType
operator|.
name|valueFromName
argument_list|(
name|inc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown property type: "
operator|+
name|inc
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|propertyTypes
operator|=
name|types
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|propertyTypes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|this
operator|.
name|excludes
operator|=
name|toLowerCase
argument_list|(
name|getMultiProperty
argument_list|(
name|defn
argument_list|,
name|EXCLUDE_PROPERTY_NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|includes
operator|=
name|getMultiProperty
argument_list|(
name|defn
argument_list|,
name|INCLUDE_PROPERTY_NAMES
argument_list|)
expr_stmt|;
name|this
operator|.
name|orderedProps
operator|=
name|getMultiProperty
argument_list|(
name|defn
argument_list|,
name|ORDERED_PROP_NAMES
argument_list|)
expr_stmt|;
name|this
operator|.
name|declaringNodeTypes
operator|=
name|getMultiProperty
argument_list|(
name|defn
argument_list|,
name|DECLARING_NODE_TYPES
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobSize
operator|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|BLOB_SIZE
argument_list|,
name|DEFAULT_BLOB_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|fullTextEnabled
operator|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|FULL_TEXT_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Storage is disabled for non full text indexes
name|this
operator|.
name|storageEnabled
operator|=
name|this
operator|.
name|fullTextEnabled
operator|&&
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|EXPERIMENTAL_STORAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyDefinition
argument_list|>
name|propDefns
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|NodeBuilder
name|propNode
init|=
name|defn
operator|.
name|getChildNode
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|propName
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|includes
argument_list|,
name|orderedProps
argument_list|)
control|)
block|{
if|if
condition|(
name|propNode
operator|.
name|hasChildNode
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|propDefns
operator|.
name|put
argument_list|(
name|propName
argument_list|,
operator|new
name|PropertyDefinition
argument_list|(
name|this
argument_list|,
name|propName
argument_list|,
name|propNode
operator|.
name|child
argument_list|(
name|propName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|propDefns
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|propDefns
argument_list|)
expr_stmt|;
name|String
name|functionName
init|=
name|getOptionalValue
argument_list|(
name|defn
argument_list|,
name|LuceneIndexConstants
operator|.
name|FUNC_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|this
operator|.
name|funcName
operator|=
name|functionName
operator|!=
literal|null
condition|?
literal|"native*"
operator|+
name|functionName
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|createCodec
argument_list|()
expr_stmt|;
if|if
condition|(
name|defn
operator|.
name|hasProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|)
condition|)
block|{
name|this
operator|.
name|entryCount
operator|=
name|defn
operator|.
name|getProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|entryCount
operator|=
name|DEFAULT_ENTRY_COUNT
expr_stmt|;
block|}
block|}
name|boolean
name|includeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|includes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|includes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
operator|!
name|excludes
operator|.
name|contains
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
name|boolean
name|includePropertyType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|propertyTypes
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
name|propertyTypes
operator|&
operator|(
literal|1
operator|<<
name|type
operator|)
operator|)
operator|!=
literal|0
return|;
block|}
name|boolean
name|isOrdered
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|orderedProps
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|NodeBuilder
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
specifier|public
name|boolean
name|isFullTextEnabled
parameter_list|()
block|{
return|return
name|fullTextEnabled
return|;
block|}
specifier|public
name|int
name|getPropertyTypes
parameter_list|()
block|{
return|return
name|propertyTypes
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getDeclaringNodeTypes
parameter_list|()
block|{
return|return
name|declaringNodeTypes
return|;
block|}
specifier|public
name|boolean
name|hasDeclaredNodeTypes
parameter_list|()
block|{
return|return
operator|!
name|declaringNodeTypes
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Checks if a given property should be stored in the lucene index or not      */
specifier|public
name|boolean
name|isStored
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|storageEnabled
return|;
block|}
specifier|public
name|boolean
name|skipTokenization
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
comment|//If fulltext is not enabled then we never tokenize
comment|//irrespective of property name
if|if
condition|(
operator|!
name|isFullTextEnabled
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|LuceneIndexHelper
operator|.
name|skipTokenization
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|PropertyDefinition
name|getPropDefn
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
return|return
name|propDefns
operator|.
name|get
argument_list|(
name|propName
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasPropertyDefinition
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
return|return
name|propDefns
operator|.
name|containsKey
argument_list|(
name|propName
argument_list|)
return|;
block|}
specifier|public
name|String
name|getFunctionName
parameter_list|()
block|{
return|return
name|funcName
return|;
block|}
specifier|public
name|boolean
name|hasFunctionDefined
parameter_list|()
block|{
return|return
name|funcName
operator|!=
literal|null
return|;
block|}
comment|/**      * Size in bytes for the blobs created while storing the index content      * @return size in bytes      */
specifier|public
name|int
name|getBlobSize
parameter_list|()
block|{
return|return
name|blobSize
return|;
block|}
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
specifier|public
name|long
name|getReindexCount
parameter_list|()
block|{
if|if
condition|(
name|definition
operator|.
name|hasProperty
argument_list|(
name|REINDEX_COUNT
argument_list|)
condition|)
block|{
return|return
name|definition
operator|.
name|getProperty
argument_list|(
name|REINDEX_COUNT
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|getEntryCount
parameter_list|()
block|{
return|return
name|entryCount
return|;
block|}
comment|//~------------------------------------------< Internal>
specifier|private
name|Codec
name|createCodec
parameter_list|()
block|{
name|String
name|codecName
init|=
name|getOptionalValue
argument_list|(
name|definition
argument_list|,
name|LuceneIndexConstants
operator|.
name|CODEC_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Codec
name|codec
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|codecName
operator|!=
literal|null
condition|)
block|{
name|codec
operator|=
name|Codec
operator|.
name|forName
argument_list|(
name|codecName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fullTextEnabled
condition|)
block|{
name|codec
operator|=
operator|new
name|OakCodec
argument_list|()
expr_stmt|;
block|}
return|return
name|codec
return|;
block|}
specifier|private
specifier|static
name|boolean
name|getOptionalValue
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|propName
parameter_list|,
name|boolean
name|defaultVal
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
decl_stmt|;
return|return
name|ps
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|getOptionalValue
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|propName
parameter_list|,
name|int
name|defaultVal
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
decl_stmt|;
return|return
name|ps
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|Ints
operator|.
name|checkedCast
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getOptionalValue
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|propName
parameter_list|,
name|String
name|defaultVal
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
decl_stmt|;
return|return
name|ps
operator|==
literal|null
condition|?
name|defaultVal
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getMultiProperty
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
name|PropertyState
name|pse
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
decl_stmt|;
return|return
name|pse
operator|!=
literal|null
condition|?
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|pse
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|toLowerCase
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|val
range|:
name|values
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|val
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
end_class

end_unit

