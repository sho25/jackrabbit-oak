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
operator|.
name|property
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|property
operator|.
name|ValuePattern
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
name|property
operator|.
name|strategy
operator|.
name|ContentMirrorStoreStrategy
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
name|property
operator|.
name|strategy
operator|.
name|UniqueEntryStoreStrategy
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
name|search
operator|.
name|PropertyDefinition
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
name|search
operator|.
name|PropertyUpdateCallback
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|base
operator|.
name|Suppliers
operator|.
name|ofInstance
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
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
name|INDEX_CONTENT_NODE_NAME
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROPERTY_INDEX
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROP_CREATED
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROP_HEAD_BUCKET
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROP_STORAGE_TYPE
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|STORAGE_TYPE_CONTENT_MIRROR
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|STORAGE_TYPE_UNIQUE
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
name|PropertyIndexUtil
operator|.
name|encode
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyIndexUpdateCallback
implements|implements
name|PropertyUpdateCallback
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
name|PropertyIndexUpdateCallback
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HEAD_BUCKET
init|=
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|UniquenessConstraintValidator
name|uniquenessConstraintValidator
decl_stmt|;
specifier|private
specifier|final
name|long
name|updateTime
decl_stmt|;
specifier|public
name|PropertyIndexUpdateCallback
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
name|this
argument_list|(
name|indexPath
argument_list|,
name|builder
argument_list|,
name|rootState
argument_list|,
name|Clock
operator|.
name|SIMPLE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyIndexUpdateCallback
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|NodeState
name|rootState
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|updateTime
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|uniquenessConstraintValidator
operator|=
operator|new
name|UniquenessConstraintValidator
argument_list|(
name|indexPath
argument_list|,
name|builder
argument_list|,
name|rootState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyUpdated
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyRelativePath
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|before
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|pd
operator|.
name|sync
condition|)
block|{
return|return;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|beforeKeys
init|=
name|getValueKeys
argument_list|(
name|before
argument_list|,
name|pd
operator|.
name|valuePattern
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|afterKeys
init|=
name|getValueKeys
argument_list|(
name|after
argument_list|,
name|pd
operator|.
name|valuePattern
argument_list|)
decl_stmt|;
comment|//Remove duplicates
name|Set
argument_list|<
name|String
argument_list|>
name|sharedKeys
init|=
name|newHashSet
argument_list|(
name|beforeKeys
argument_list|)
decl_stmt|;
name|sharedKeys
operator|.
name|retainAll
argument_list|(
name|afterKeys
argument_list|)
expr_stmt|;
name|beforeKeys
operator|.
name|removeAll
argument_list|(
name|sharedKeys
argument_list|)
expr_stmt|;
name|afterKeys
operator|.
name|removeAll
argument_list|(
name|sharedKeys
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|beforeKeys
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|afterKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|NodeBuilder
name|indexNode
init|=
name|getIndexNode
argument_list|(
name|propertyRelativePath
argument_list|,
name|pd
operator|.
name|unique
argument_list|)
decl_stmt|;
if|if
condition|(
name|pd
operator|.
name|unique
condition|)
block|{
name|UniqueEntryStoreStrategy
name|s
init|=
operator|new
name|UniqueEntryStoreStrategy
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|,
parameter_list|(
name|nb
parameter_list|)
lambda|->
name|nb
operator|.
name|setProperty
argument_list|(
name|PROP_CREATED
argument_list|,
name|updateTime
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|update
argument_list|(
name|ofInstance
argument_list|(
name|indexNode
argument_list|)
argument_list|,
name|nodePath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|beforeKeys
argument_list|,
name|afterKeys
argument_list|)
expr_stmt|;
name|uniquenessConstraintValidator
operator|.
name|add
argument_list|(
name|propertyRelativePath
argument_list|,
name|afterKeys
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ContentMirrorStoreStrategy
name|s
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
name|s
operator|.
name|update
argument_list|(
name|ofInstance
argument_list|(
name|indexNode
argument_list|)
argument_list|,
name|nodePath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|emptySet
argument_list|()
argument_list|,
comment|//Disable pruning with empty before keys
name|afterKeys
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"[{}] Property index updated for [{}/@{}] with values {}"
argument_list|,
name|indexPath
argument_list|,
name|nodePath
argument_list|,
name|propertyRelativePath
argument_list|,
name|afterKeys
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|done
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|uniquenessConstraintValidator
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|UniquenessConstraintValidator
name|getUniquenessConstraintValidator
parameter_list|()
block|{
return|return
name|uniquenessConstraintValidator
return|;
block|}
specifier|private
name|NodeBuilder
name|getIndexNode
parameter_list|(
name|String
name|propertyRelativePath
parameter_list|,
name|boolean
name|unique
parameter_list|)
block|{
name|NodeBuilder
name|propertyIndex
init|=
name|builder
operator|.
name|child
argument_list|(
name|PROPERTY_INDEX
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyIndex
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|propertyIndex
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_RETAIN
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|nodeName
init|=
name|HybridPropertyIndexUtil
operator|.
name|getNodeName
argument_list|(
name|propertyRelativePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|unique
condition|)
block|{
return|return
name|getUniqueIndexBuilder
argument_list|(
name|propertyIndex
argument_list|,
name|nodeName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getSimpleIndexBuilder
argument_list|(
name|propertyIndex
argument_list|,
name|nodeName
argument_list|)
return|;
block|}
block|}
specifier|private
name|NodeBuilder
name|getSimpleIndexBuilder
parameter_list|(
name|NodeBuilder
name|propertyIndex
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|NodeBuilder
name|idx
init|=
name|propertyIndex
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|idx
operator|.
name|setProperty
argument_list|(
name|PROP_HEAD_BUCKET
argument_list|,
name|DEFAULT_HEAD_BUCKET
argument_list|)
expr_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
name|PROP_STORAGE_TYPE
argument_list|,
name|STORAGE_TYPE_CONTENT_MIRROR
argument_list|)
expr_stmt|;
block|}
name|String
name|headBucketName
init|=
name|idx
operator|.
name|getString
argument_list|(
name|PROP_HEAD_BUCKET
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|headBucketName
argument_list|,
literal|"[%s] property not found in [%s] for index [%s]"
argument_list|,
name|PROP_HEAD_BUCKET
argument_list|,
name|idx
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
return|return
name|idx
operator|.
name|child
argument_list|(
name|headBucketName
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|getUniqueIndexBuilder
parameter_list|(
name|NodeBuilder
name|propertyIndex
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
name|NodeBuilder
name|idx
init|=
name|propertyIndex
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|idx
operator|.
name|setProperty
argument_list|(
name|PROP_STORAGE_TYPE
argument_list|,
name|STORAGE_TYPE_UNIQUE
argument_list|)
expr_stmt|;
block|}
return|return
name|idx
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getValueKeys
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|ValuePattern
name|pattern
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
operator|&&
name|property
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
operator|&&
name|property
operator|.
name|count
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|keys
operator|.
name|addAll
argument_list|(
name|encode
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|property
argument_list|)
argument_list|,
name|pattern
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
block|}
end_class

end_unit

