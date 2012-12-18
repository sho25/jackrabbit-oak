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
name|version
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionException
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
name|JcrConstants
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
name|core
operator|.
name|ReadOnlyTree
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
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
name|commit
operator|.
name|CommitHook
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
name|version
operator|.
name|VersionConstants
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

begin_comment
comment|/**  *<code>VersionHook</code>...  */
end_comment

begin_class
specifier|public
class|class
name|VersionHook
implements|implements
name|CommitHook
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
specifier|final
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// FIXME? depends on InitialContent creating the nodes and setting
comment|// the jcr:primaryType accordingly
name|NodeBuilder
name|vsRoot
init|=
name|builder
operator|.
name|child
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|NodeTypeConstants
operator|.
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
name|ReadWriteVersionManager
name|vMgr
init|=
operator|new
name|ReadWriteVersionManager
argument_list|(
name|vsRoot
argument_list|,
name|builder
argument_list|)
decl_stmt|;
try|try
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|VersionDiff
argument_list|(
literal|null
argument_list|,
name|vMgr
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UncheckedRepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|VersionDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|VersionDiff
name|parent
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteVersionManager
name|vMgr
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|nodeAfter
decl_stmt|;
specifier|private
name|Boolean
name|isVersionable
init|=
literal|null
decl_stmt|;
name|VersionDiff
parameter_list|(
annotation|@
name|Nullable
name|VersionDiff
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|ReadWriteVersionManager
name|vMgr
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|node
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
name|vMgr
operator|=
name|checkNotNull
argument_list|(
name|vMgr
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeAfter
operator|=
name|checkNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|isVersionable
argument_list|()
condition|)
block|{
name|vMgr
operator|.
name|getOrCreateVersionHistory
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
operator|!
name|isVersionable
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// JCR allows to put a lock on a checked in node.
if|if
condition|(
name|after
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|JcrConstants
operator|.
name|JCR_LOCKOWNER
argument_list|)
operator|||
name|after
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|JcrConstants
operator|.
name|JCR_LOCKISDEEP
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|wasCheckedIn
argument_list|()
condition|)
block|{
name|throwCheckedIn
argument_list|(
literal|"Cannot add property "
operator|+
name|after
operator|.
name|getName
argument_list|()
operator|+
literal|" on checked in node"
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
if|if
condition|(
operator|!
name|isVersionable
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|isVersionProperty
argument_list|(
name|after
argument_list|)
operator|&&
name|wasCheckedIn
argument_list|()
condition|)
block|{
name|throwCheckedIn
argument_list|(
literal|"Cannot change property "
operator|+
name|after
operator|.
name|getName
argument_list|()
operator|+
literal|" on checked in node"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|String
name|propName
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
name|VersionConstants
operator|.
name|JCR_ISCHECKEDOUT
argument_list|)
condition|)
block|{
if|if
condition|(
name|wasCheckedIn
argument_list|()
condition|)
block|{
name|vMgr
operator|.
name|checkout
argument_list|(
name|nodeAfter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vMgr
operator|.
name|checkin
argument_list|(
name|nodeAfter
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
name|VersionConstants
operator|.
name|JCR_BASEVERSION
argument_list|)
condition|)
block|{
name|vMgr
operator|.
name|restore
argument_list|(
name|nodeAfter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isVersionProperty
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|throwProtected
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|wasCheckedIn
argument_list|()
condition|)
block|{
name|throwCheckedIn
argument_list|(
literal|"Cannot change property "
operator|+
name|after
operator|.
name|getName
argument_list|()
operator|+
literal|" on checked in node"
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
if|if
condition|(
operator|!
name|isVersionable
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|isVersionProperty
argument_list|(
name|before
argument_list|)
operator|&&
name|wasCheckedIn
argument_list|()
condition|)
block|{
name|throwProtected
argument_list|(
literal|"Cannot delete property on checked in node"
argument_list|)
expr_stmt|;
block|}
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
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|VersionDiff
argument_list|(
name|this
argument_list|,
name|vMgr
argument_list|,
name|nodeAfter
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|NodeState
name|after
init|=
name|MemoryNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|VersionDiff
argument_list|(
name|this
argument_list|,
name|vMgr
argument_list|,
name|after
operator|.
name|builder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**          * Returns<code>true</code> if the node of this VersionDiff          * is versionable;<code>false</code> otherwise.          *          * @return whether the node is versionable.          */
specifier|private
name|boolean
name|isVersionable
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|isVersionable
operator|==
literal|null
condition|)
block|{
comment|// this is not 100% correct, because t.getPath() will
comment|// not return the correct path for nodeAfter, but is
comment|// sufficient to check if it is versionable
name|Tree
name|t
init|=
operator|new
name|ReadOnlyTree
argument_list|(
name|nodeAfter
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|isVersionable
operator|=
name|vMgr
operator|.
name|isVersionable
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|isVersionable
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedRepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|isVersionProperty
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
return|return
name|VersionConstants
operator|.
name|VERSION_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * @return<code>true</code> if this node<b>was</b> checked in. That          *         is, this method checks the base state for the          *         jcr:isCheckedOut property.          */
specifier|private
name|boolean
name|wasCheckedIn
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|nodeAfter
operator|.
name|getBaseState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|PropertyState
name|prop
init|=
name|state
operator|.
name|getProperty
argument_list|(
name|VersionConstants
operator|.
name|JCR_ISCHECKEDOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
return|return
operator|!
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
block|}
comment|// new node or not versionable, check parent
return|return
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|wasCheckedIn
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|throwCheckedIn
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|UncheckedRepositoryException
block|{
name|throwUnchecked
argument_list|(
operator|new
name|VersionException
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|throwProtected
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|UncheckedRepositoryException
block|{
name|throwUnchecked
argument_list|(
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Property is protected: "
operator|+
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|throwUnchecked
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
throws|throws
name|UncheckedRepositoryException
block|{
throw|throw
operator|new
name|UncheckedRepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
specifier|private
specifier|static
class|class
name|UncheckedRepositoryException
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5220620245610340169L
decl_stmt|;
specifier|public
name|UncheckedRepositoryException
parameter_list|(
name|RepositoryException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RepositoryException
name|getCause
parameter_list|()
block|{
return|return
operator|(
name|RepositoryException
operator|)
name|super
operator|.
name|getCause
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

