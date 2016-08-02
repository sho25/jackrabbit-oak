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
name|document
operator|.
name|secondary
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Predicate
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
name|document
operator|.
name|AbstractDocumentNodeState
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
name|document
operator|.
name|NodeStateDiffer
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
name|document
operator|.
name|RevisionVector
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
name|MemoryChildNodeEntry
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
name|MemoryNodeBuilder
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
name|Preconditions
operator|.
name|checkState
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
name|denotesRoot
import|;
end_import

begin_comment
comment|/**  * NodeState wrapper which wraps another NodeState (mostly SegmentNodeState)  * so as to expose it as an {@link AbstractDocumentNodeState} by extracting  * the meta properties which are stored as hidden properties  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingDocumentNodeState
extends|extends
name|AbstractDocumentNodeState
block|{
comment|//Hidden props holding DocumentNodeState meta properties
specifier|static
specifier|final
name|String
name|PROP_PATH
init|=
literal|":doc-path"
decl_stmt|;
specifier|static
specifier|final
name|String
name|PROP_REVISION
init|=
literal|":doc-rev"
decl_stmt|;
specifier|static
specifier|final
name|String
name|PROP_LAST_REV
init|=
literal|":doc-lastRev"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|META_PROP_COUNT
init|=
literal|3
decl_stmt|;
comment|//Count of above meta props
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
name|NOT_META_PROPS
init|=
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|PropertyState
name|input
parameter_list|)
block|{
return|return
operator|!
name|input
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|":doc-"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|NodeStateDiffer
name|differ
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|RevisionVector
name|rootRevision
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|fromExternalChange
decl_stmt|;
specifier|private
name|RevisionVector
name|lastRevision
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * Wraps a given NodeState as a {@link DelegatingDocumentNodeState} if      * it has required meta properties otherwise just returns the passed NodeState      *      * @param delegate nodeState to wrap      * @return wrapped state or original state      */
specifier|public
specifier|static
name|NodeState
name|wrapIfPossible
parameter_list|(
name|NodeState
name|delegate
parameter_list|,
name|NodeStateDiffer
name|differ
parameter_list|)
block|{
if|if
condition|(
name|hasMetaProps
argument_list|(
name|delegate
argument_list|)
condition|)
block|{
name|String
name|revVector
init|=
name|getRequiredProp
argument_list|(
name|delegate
argument_list|,
name|PROP_REVISION
argument_list|)
decl_stmt|;
return|return
operator|new
name|DelegatingDocumentNodeState
argument_list|(
name|delegate
argument_list|,
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|revVector
argument_list|)
argument_list|,
literal|false
argument_list|,
name|differ
argument_list|)
return|;
block|}
return|return
name|delegate
return|;
block|}
specifier|public
specifier|static
name|boolean
name|hasMetaProps
parameter_list|(
name|NodeState
name|delegate
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasProperty
argument_list|(
name|PROP_REVISION
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|AbstractDocumentNodeState
name|wrap
parameter_list|(
name|NodeState
name|delegate
parameter_list|,
name|NodeStateDiffer
name|differ
parameter_list|)
block|{
name|String
name|revVector
init|=
name|getRequiredProp
argument_list|(
name|delegate
argument_list|,
name|PROP_REVISION
argument_list|)
decl_stmt|;
return|return
operator|new
name|DelegatingDocumentNodeState
argument_list|(
name|delegate
argument_list|,
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|revVector
argument_list|)
argument_list|,
literal|false
argument_list|,
name|differ
argument_list|)
return|;
block|}
specifier|private
name|DelegatingDocumentNodeState
parameter_list|(
name|NodeState
name|delegate
parameter_list|,
name|RevisionVector
name|rootRevision
parameter_list|,
name|boolean
name|fromExternalChange
parameter_list|,
name|NodeStateDiffer
name|differ
parameter_list|)
block|{
name|this
operator|.
name|differ
operator|=
name|differ
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|rootRevision
operator|=
name|rootRevision
expr_stmt|;
name|this
operator|.
name|fromExternalChange
operator|=
name|fromExternalChange
expr_stmt|;
block|}
specifier|private
name|DelegatingDocumentNodeState
parameter_list|(
name|DelegatingDocumentNodeState
name|original
parameter_list|,
name|RevisionVector
name|rootRevision
parameter_list|,
name|boolean
name|fromExternalChange
parameter_list|)
block|{
name|this
operator|.
name|differ
operator|=
name|original
operator|.
name|differ
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|original
operator|.
name|delegate
expr_stmt|;
name|this
operator|.
name|rootRevision
operator|=
name|rootRevision
expr_stmt|;
name|this
operator|.
name|fromExternalChange
operator|=
name|fromExternalChange
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|original
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|lastRevision
operator|=
name|original
operator|.
name|lastRevision
expr_stmt|;
block|}
comment|//~----------------------------------< AbstractDocumentNodeState>
annotation|@
name|Override
specifier|public
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
name|this
operator|.
name|path
operator|=
name|getRequiredProp
argument_list|(
name|PROP_PATH
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getLastRevision
parameter_list|()
block|{
if|if
condition|(
name|lastRevision
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|lastRevision
operator|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|getRequiredProp
argument_list|(
name|PROP_LAST_REV
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|lastRevision
return|;
block|}
annotation|@
name|Override
specifier|public
name|RevisionVector
name|getRootRevision
parameter_list|()
block|{
return|return
name|rootRevision
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFromExternalChange
parameter_list|()
block|{
return|return
name|fromExternalChange
return|;
block|}
annotation|@
name|Override
specifier|public
name|AbstractDocumentNodeState
name|withRootRevision
parameter_list|(
annotation|@
name|Nonnull
name|RevisionVector
name|root
parameter_list|,
name|boolean
name|externalChange
parameter_list|)
block|{
if|if
condition|(
name|rootRevision
operator|.
name|equals
argument_list|(
name|root
argument_list|)
operator|&&
name|fromExternalChange
operator|==
name|externalChange
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|DelegatingDocumentNodeState
argument_list|(
name|this
argument_list|,
name|root
argument_list|,
name|externalChange
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNoChildren
parameter_list|()
block|{
comment|//Passing max as 1 so as to minimize any overhead.
return|return
name|delegate
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStateDiffer
name|getNodeStateDiffer
parameter_list|()
block|{
return|return
name|differ
return|;
block|}
comment|//~----------------------------------< NodeState>
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|delegate
operator|.
name|getProperties
argument_list|()
argument_list|,
name|NOT_META_PROPS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
name|decorate
argument_list|(
name|delegate
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|delegate
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|input
operator|.
name|getName
argument_list|()
argument_list|,
name|decorate
argument_list|(
name|input
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|builder
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|denotesRoot
argument_list|(
name|getPath
argument_list|()
argument_list|)
argument_list|,
literal|"Builder cannot be opened for root "
operator|+
literal|"path for state of type [%s]"
argument_list|,
name|delegate
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|//Following method should be overridden as default implementation in AbstractNodeState
comment|//is not optimized
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getPropertyCount
argument_list|()
operator|-
name|META_PROP_COUNT
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getBoolean
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getLong
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getString
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getStrings
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getName
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getNames
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getChildNodeNames
argument_list|()
return|;
block|}
comment|//~--------------------------------------------< internal>
specifier|private
name|NodeState
name|decorate
parameter_list|(
name|NodeState
name|childNode
parameter_list|)
block|{
if|if
condition|(
name|childNode
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|DelegatingDocumentNodeState
argument_list|(
name|childNode
argument_list|,
name|rootRevision
argument_list|,
name|fromExternalChange
argument_list|,
name|differ
argument_list|)
return|;
block|}
return|return
name|childNode
return|;
block|}
specifier|private
name|String
name|getRequiredProp
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getRequiredProp
argument_list|(
name|delegate
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getRequiredProp
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|checkNotNull
argument_list|(
name|state
operator|.
name|getString
argument_list|(
name|name
argument_list|)
argument_list|,
literal|"No property [%s] found in [%s]"
argument_list|,
name|name
argument_list|,
name|state
argument_list|)
return|;
block|}
block|}
end_class

end_unit

