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
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinition
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
name|OnParentVersionAction
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
name|tree
operator|.
name|factories
operator|.
name|TreeFactory
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
name|CommitInfo
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
name|Editor
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
name|lock
operator|.
name|LockConstants
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
name|version
operator|.
name|VersionConstants
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
name|NotNull
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
name|JcrConstants
operator|.
name|JCR_BASEVERSION
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
name|JCR_ISCHECKEDOUT
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
name|spi
operator|.
name|version
operator|.
name|VersionConstants
operator|.
name|RESTORE_PREFIX
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
class|class
name|VersionEditor
implements|implements
name|Editor
block|{
specifier|private
specifier|final
name|VersionEditor
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
name|node
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|Boolean
name|isVersionable
init|=
literal|null
decl_stmt|;
specifier|private
name|NodeState
name|before
decl_stmt|;
specifier|private
name|NodeState
name|after
decl_stmt|;
specifier|private
name|boolean
name|isReadOnly
decl_stmt|;
specifier|private
name|CommitInfo
name|commitInfo
decl_stmt|;
specifier|public
name|VersionEditor
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|versionStore
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|workspaceRoot
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|commitInfo
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
operator|new
name|ReadWriteVersionManager
argument_list|(
name|checkNotNull
argument_list|(
name|versionStore
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|workspaceRoot
argument_list|)
argument_list|)
argument_list|,
name|workspaceRoot
argument_list|,
literal|""
argument_list|,
name|commitInfo
argument_list|)
expr_stmt|;
block|}
name|VersionEditor
parameter_list|(
annotation|@
name|Nullable
name|VersionEditor
name|parent
parameter_list|,
annotation|@
name|NotNull
name|ReadWriteVersionManager
name|vMgr
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|node
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|commitInfo
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
name|node
operator|=
name|checkNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitInfo
operator|=
name|commitInfo
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
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
argument_list|,
name|commitInfo
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// calculate isReadOnly state
if|if
condition|(
name|after
operator|.
name|exists
argument_list|()
operator|||
name|isVersionable
argument_list|()
condition|)
block|{
comment|// deleted or versionable -> check if it was checked in
comment|// a node cannot be modified if it was checked in
comment|// unless it has a new identifier
name|isReadOnly
operator|=
name|wasCheckedIn
argument_list|()
operator|&&
operator|!
name|hasNewIdentifier
argument_list|()
operator|&&
operator|!
name|isIgnoreOnOPV
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise inherit from parent
name|isReadOnly
operator|=
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|isReadOnly
operator|&&
operator|!
name|isIgnoreOnOPV
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|after
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|JCR_BASEVERSION
argument_list|)
operator|&&
name|this
operator|.
name|after
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_VERSIONHISTORY
argument_list|)
operator|&&
operator|!
name|this
operator|.
name|after
operator|.
name|hasProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|)
operator|&&
operator|!
name|this
operator|.
name|before
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// sentinel node for restore
name|vMgr
operator|.
name|restore
argument_list|(
name|node
argument_list|,
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|isReadOnly
operator|||
name|getOPV
argument_list|(
name|after
argument_list|)
operator|==
name|OnParentVersionAction
operator|.
name|IGNORE
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
throws|throws
name|CommitFailedException
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
name|isReadOnly
operator|&&
name|getOPV
argument_list|(
name|after
argument_list|)
operator|!=
name|OnParentVersionAction
operator|.
name|IGNORE
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
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vMgr
operator|.
name|checkin
argument_list|(
name|node
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
name|JCR_BASEVERSION
argument_list|)
condition|)
block|{
name|String
name|baseVersion
init|=
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|REFERENCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseVersion
operator|.
name|startsWith
argument_list|(
name|RESTORE_PREFIX
argument_list|)
condition|)
block|{
name|baseVersion
operator|=
name|baseVersion
operator|.
name|substring
argument_list|(
name|RESTORE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_BASEVERSION
argument_list|,
name|baseVersion
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
block|}
name|vMgr
operator|.
name|restore
argument_list|(
name|node
argument_list|,
name|baseVersion
argument_list|,
literal|null
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
name|isReadOnly
operator|&&
name|getOPV
argument_list|(
name|after
argument_list|)
operator|!=
name|OnParentVersionAction
operator|.
name|IGNORE
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
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|isReadOnly
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
operator|!
name|isLockProperty
argument_list|(
name|before
argument_list|)
operator|&&
name|getOPV
argument_list|(
name|before
argument_list|)
operator|!=
name|OnParentVersionAction
operator|.
name|IGNORE
condition|)
block|{
name|throwCheckedIn
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
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|MISSING_NODE
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
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
return|return
operator|new
name|VersionEditor
argument_list|(
name|this
argument_list|,
name|vMgr
argument_list|,
name|node
operator|.
name|child
argument_list|(
name|name
argument_list|)
argument_list|,
name|name
argument_list|,
name|commitInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
operator|new
name|VersionEditor
argument_list|(
name|this
argument_list|,
name|vMgr
argument_list|,
name|MISSING_NODE
operator|.
name|builder
argument_list|()
argument_list|,
name|name
argument_list|,
name|commitInfo
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if the node of this VersionDiff is versionable;      * {@code false} otherwise.      *      * @return whether the node is versionable.      */
specifier|private
name|boolean
name|isVersionable
parameter_list|()
block|{
if|if
condition|(
name|isVersionable
operator|==
literal|null
condition|)
block|{
name|isVersionable
operator|=
name|vMgr
operator|.
name|isVersionable
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
return|return
name|isVersionable
return|;
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
specifier|private
name|boolean
name|isLockProperty
parameter_list|(
name|PropertyState
name|state
parameter_list|)
block|{
return|return
name|LockConstants
operator|.
name|LOCK_PROPERTY_NAMES
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
comment|/**      * @return {@code true} if this node<b>was</b> checked in. That is,      *         this method checks the before state for the jcr:isCheckedOut      *         property.      */
specifier|private
name|boolean
name|wasCheckedIn
parameter_list|()
block|{
name|PropertyState
name|prop
init|=
name|before
operator|.
name|getProperty
argument_list|(
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
name|boolean
name|hasNewIdentifier
parameter_list|()
block|{
name|String
name|beforeId
init|=
name|buildBeforeIdentifier
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|afterId
init|=
name|buildAfterIdentifier
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|!
name|beforeId
operator|.
name|equals
argument_list|(
name|afterId
argument_list|)
return|;
block|}
specifier|private
name|StringBuilder
name|buildBeforeIdentifier
parameter_list|(
name|StringBuilder
name|identifier
parameter_list|)
block|{
name|String
name|uuid
init|=
name|before
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|!=
literal|null
condition|)
block|{
name|identifier
operator|.
name|append
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildBeforeIdentifier
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|identifier
return|;
block|}
specifier|private
name|StringBuilder
name|buildAfterIdentifier
parameter_list|(
name|StringBuilder
name|identifier
parameter_list|)
block|{
name|String
name|uuid
init|=
name|after
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|!=
literal|null
condition|)
block|{
name|identifier
operator|.
name|append
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildAfterIdentifier
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|identifier
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
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|VERSION
argument_list|,
name|VersionExceptionCode
operator|.
name|NODE_CHECKED_IN
operator|.
name|ordinal
argument_list|()
argument_list|,
name|msg
argument_list|)
throw|;
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
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|100
argument_list|,
literal|"Property is protected: "
operator|+
name|name
argument_list|)
throw|;
block|}
specifier|private
name|boolean
name|isIgnoreOnOPV
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|this
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|NodeDefinition
name|definition
init|=
name|this
operator|.
name|vMgr
operator|.
name|getNodeTypeManager
argument_list|()
operator|.
name|getDefinition
argument_list|(
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|parent
operator|.
name|node
argument_list|)
argument_list|,
name|this
operator|.
name|name
argument_list|)
decl_stmt|;
return|return
name|definition
operator|.
name|getOnParentVersion
argument_list|()
operator|==
name|OnParentVersionAction
operator|.
name|IGNORE
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|VERSION
argument_list|,
name|VersionExceptionCode
operator|.
name|UNEXPECTED_REPOSITORY_EXCEPTION
operator|.
name|ordinal
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|int
name|getOPV
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|CommitFailedException
block|{
try|try
block|{
return|return
name|this
operator|.
name|vMgr
operator|.
name|getNodeTypeManager
argument_list|()
operator|.
name|getDefinition
argument_list|(
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|this
operator|.
name|node
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|,
name|property
argument_list|,
literal|false
argument_list|)
operator|.
name|getOnParentVersion
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|VERSION
argument_list|,
name|VersionExceptionCode
operator|.
name|UNEXPECTED_REPOSITORY_EXCEPTION
operator|.
name|ordinal
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

