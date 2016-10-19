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
name|document
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
name|json
operator|.
name|JsopBuilder
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
name|json
operator|.
name|BlobSerializer
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
name|json
operator|.
name|JsonSerializer
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
name|bundlor
operator|.
name|BundlingHandler
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
name|bundlor
operator|.
name|DocumentBundlor
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
name|MISSING_NODE
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

begin_comment
comment|/**  * Implementation of a {@link NodeStateDiff}, which translates the diffs into  * {@link UpdateOp}s of a commit.  */
end_comment

begin_class
class|class
name|CommitDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Commit
name|commit
decl_stmt|;
specifier|private
specifier|final
name|JsopBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|BlobSerializer
name|blobs
decl_stmt|;
specifier|private
specifier|final
name|BundlingHandler
name|bundlingHandler
decl_stmt|;
name|CommitDiff
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|Commit
name|commit
parameter_list|,
annotation|@
name|Nonnull
name|BlobSerializer
name|blobs
parameter_list|)
block|{
name|this
argument_list|(
name|checkNotNull
argument_list|(
name|store
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|commit
argument_list|)
argument_list|,
name|store
operator|.
name|getBundlingHandler
argument_list|()
argument_list|,
operator|new
name|JsopBuilder
argument_list|()
argument_list|,
name|checkNotNull
argument_list|(
name|blobs
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CommitDiff
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|,
name|Commit
name|commit
parameter_list|,
name|BundlingHandler
name|bundlingHandler
parameter_list|,
name|JsopBuilder
name|builder
parameter_list|,
name|BlobSerializer
name|blobs
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
name|commit
operator|=
name|commit
expr_stmt|;
name|this
operator|.
name|bundlingHandler
operator|=
name|bundlingHandler
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|blobs
operator|=
name|blobs
expr_stmt|;
name|performBundlingRelatedOperations
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|commit
operator|.
name|updateProperty
argument_list|(
name|bundlingHandler
operator|.
name|getRootBundlePath
argument_list|()
argument_list|,
name|bundlingHandler
operator|.
name|getPropertyPath
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|BundlingHandler
name|child
init|=
name|bundlingHandler
operator|.
name|childAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isBundlingRoot
argument_list|()
condition|)
block|{
name|commit
operator|.
name|addNode
argument_list|(
operator|new
name|DocumentNodeState
argument_list|(
name|store
argument_list|,
name|child
operator|.
name|getRootBundlePath
argument_list|()
argument_list|,
operator|new
name|RevisionVector
argument_list|(
name|commit
operator|.
name|getRevision
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setChildrenFlagOnAdd
argument_list|(
name|child
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|CommitDiff
argument_list|(
name|store
argument_list|,
name|commit
argument_list|,
name|child
argument_list|,
name|builder
argument_list|,
name|blobs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
comment|//TODO [bundling] Handle change of primaryType
name|BundlingHandler
name|child
init|=
name|bundlingHandler
operator|.
name|childChanged
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|CommitDiff
argument_list|(
name|store
argument_list|,
name|commit
argument_list|,
name|child
argument_list|,
name|builder
argument_list|,
name|blobs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|BundlingHandler
name|child
init|=
name|bundlingHandler
operator|.
name|childDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isBundlingRoot
argument_list|()
condition|)
block|{
comment|//TODO [bundling] Handle delete
name|commit
operator|.
name|removeNode
argument_list|(
name|child
operator|.
name|getRootBundlePath
argument_list|()
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
return|return
name|MISSING_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|CommitDiff
argument_list|(
name|store
argument_list|,
name|commit
argument_list|,
name|child
argument_list|,
name|builder
argument_list|,
name|blobs
argument_list|)
argument_list|)
return|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
name|void
name|performBundlingRelatedOperations
parameter_list|()
block|{
name|setMetaProperties
argument_list|()
expr_stmt|;
name|informCommitAboutBundledNodes
argument_list|()
expr_stmt|;
name|removeRemovedProps
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setMetaProperties
parameter_list|()
block|{
for|for
control|(
name|PropertyState
name|ps
range|:
name|bundlingHandler
operator|.
name|getMetaProps
argument_list|()
control|)
block|{
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|informCommitAboutBundledNodes
parameter_list|()
block|{
if|if
condition|(
name|bundlingHandler
operator|.
name|isBundledNode
argument_list|()
condition|)
block|{
name|commit
operator|.
name|addBundledNode
argument_list|(
name|bundlingHandler
operator|.
name|getNodeFullPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeRemovedProps
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
name|bundlingHandler
operator|.
name|getRemovedProps
argument_list|()
control|)
block|{
name|commit
operator|.
name|updateProperty
argument_list|(
name|bundlingHandler
operator|.
name|getRootBundlePath
argument_list|()
argument_list|,
name|bundlingHandler
operator|.
name|getPropertyPath
argument_list|(
name|propName
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setChildrenFlagOnAdd
parameter_list|(
name|BundlingHandler
name|child
parameter_list|)
block|{
comment|//Add hasChildren marker for bundling case
name|String
name|propName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|isBundledNode
argument_list|()
condition|)
block|{
comment|//1. Child is a bundled node. In that case current node would be part
comment|//   same NodeDocument in which the child would be saved
name|propName
operator|=
name|DocumentBundlor
operator|.
name|META_PROP_BUNDLED_CHILD
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bundlingHandler
operator|.
name|isBundledNode
argument_list|()
condition|)
block|{
comment|//2. Child is a non bundled node but current node was bundled. This would
comment|//   be the case where child node is not covered by bundling pattern. In
comment|//   that case also add marker to current node
comment|//   For case when current node is bundled  but is bundling root
comment|//   this info is already captured in _hasChildren flag
name|propName
operator|=
name|DocumentBundlor
operator|.
name|META_PROP_NON_BUNDLED_CHILD
expr_stmt|;
block|}
comment|//Retouch the property if already present to enable
comment|//hierarchy conflict detection
if|if
condition|(
name|propName
operator|!=
literal|null
condition|)
block|{
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|propName
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
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
parameter_list|)
block|{
name|builder
operator|.
name|resetWriter
argument_list|()
expr_stmt|;
name|JsonSerializer
name|serializer
init|=
operator|new
name|JsonSerializer
argument_list|(
name|builder
argument_list|,
name|blobs
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|commit
operator|.
name|updateProperty
argument_list|(
name|bundlingHandler
operator|.
name|getRootBundlePath
argument_list|()
argument_list|,
name|bundlingHandler
operator|.
name|getPropertyPath
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|serializer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|property
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARY
operator|)
operator|||
operator|(
name|property
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARIES
operator|)
condition|)
block|{
name|this
operator|.
name|commit
operator|.
name|markNodeHavingBinary
argument_list|(
name|bundlingHandler
operator|.
name|getRootBundlePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

