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
name|jcr
operator|.
name|version
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
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
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
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockException
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
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|util
operator|.
name|TraversingItemVisitor
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
name|Version
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
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionHistory
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
name|VersionManager
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
name|commons
operator|.
name|iterator
operator|.
name|NodeIteratorAdapter
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
name|jcr
operator|.
name|SessionContext
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
name|jcr
operator|.
name|delegate
operator|.
name|NodeDelegate
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
name|jcr
operator|.
name|delegate
operator|.
name|SessionDelegate
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
name|jcr
operator|.
name|delegate
operator|.
name|VersionDelegate
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
name|jcr
operator|.
name|delegate
operator|.
name|VersionHistoryDelegate
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
name|jcr
operator|.
name|delegate
operator|.
name|VersionManagerDelegate
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
name|jcr
operator|.
name|operation
operator|.
name|SessionOperation
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
name|util
operator|.
name|TODO
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

begin_class
specifier|public
class|class
name|VersionManagerImpl
implements|implements
name|VersionManager
block|{
specifier|private
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
specifier|final
name|VersionManagerDelegate
name|versionManagerDelegate
decl_stmt|;
specifier|public
name|VersionManagerImpl
parameter_list|(
name|SessionContext
name|sessionContext
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|versionManagerDelegate
operator|=
name|VersionManagerDelegate
operator|.
name|create
argument_list|(
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|setActivity
parameter_list|(
name|Node
name|activity
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restoreByLabel
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|versionLabel
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|,
specifier|final
name|Version
name|version
parameter_list|,
specifier|final
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"VersionManager.restore(String, Version, boolean)"
operator|+
literal|" not allowed on existing nodes; use"
operator|+
literal|" VersionManager.restore(Version, boolean) instead: "
operator|+
name|absPath
argument_list|)
throw|;
block|}
comment|// check if parent exists
name|NodeDelegate
name|parent
init|=
name|ensureParentExists
argument_list|(
name|sessionDelegate
argument_list|,
name|absPath
argument_list|)
decl_stmt|;
comment|// check for pending changes
name|checkPendingChangesForRestore
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
comment|// check lock status
name|checkNotLocked
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// check for existing nodes
name|List
argument_list|<
name|NodeDelegate
argument_list|>
name|existing
init|=
name|getExisting
argument_list|(
name|version
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|existing
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|removeExisting
condition|)
block|{
name|removeExistingNodes
argument_list|(
name|existing
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeDelegate
name|nd
range|:
name|existing
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|nd
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|ItemExistsException
argument_list|(
literal|"Unable to restore with "
operator|+
literal|"removeExisting=false. Existing nodes in "
operator|+
literal|"workspace: "
operator|+
name|paths
argument_list|)
throw|;
block|}
block|}
comment|// ready for restore
name|VersionDelegate
name|vd
init|=
name|versionManagerDelegate
operator|.
name|getVersionByIdentifier
argument_list|(
name|version
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|versionManagerDelegate
operator|.
name|restore
argument_list|(
name|parent
argument_list|,
name|PathUtils
operator|.
name|getName
argument_list|(
name|oakPath
argument_list|)
argument_list|,
name|vd
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// refresh if one of the modifying operations fail
name|sessionDelegate
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|,
specifier|final
name|String
name|versionName
parameter_list|,
specifier|final
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|VersionHistory
name|history
init|=
name|getVersionHistory
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|restore
argument_list|(
operator|new
name|Version
index|[]
block|{
name|history
operator|.
name|getVersion
argument_list|(
name|versionName
argument_list|)
block|}
argument_list|,
name|removeExisting
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
name|Version
name|version
parameter_list|,
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|restore
argument_list|(
operator|new
name|Version
index|[]
block|{
name|version
block|}
argument_list|,
name|removeExisting
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restore
parameter_list|(
specifier|final
name|Version
index|[]
name|versions
parameter_list|,
specifier|final
name|boolean
name|removeExisting
parameter_list|)
throws|throws
name|ItemExistsException
throws|,
name|UnsupportedRepositoryOperationException
throws|,
name|VersionException
throws|,
name|LockException
throws|,
name|InvalidItemStateException
throws|,
name|RepositoryException
block|{
if|if
condition|(
name|versions
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// TODO: implement restore of multiple versions
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
comment|// TODO: RETURN
block|}
specifier|final
name|Version
name|version
init|=
name|versions
index|[
literal|0
index|]
decl_stmt|;
name|VersionHistory
name|history
init|=
operator|(
name|VersionHistory
operator|)
name|version
operator|.
name|getParent
argument_list|()
decl_stmt|;
specifier|final
name|String
name|versionableId
init|=
name|history
operator|.
name|getVersionableIdentifier
argument_list|()
decl_stmt|;
if|if
condition|(
name|history
operator|.
name|getRootVersion
argument_list|()
operator|.
name|isSame
argument_list|(
name|version
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Restore of root version not possible"
argument_list|)
throw|;
block|}
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// check for pending changes
name|checkPendingChangesForRestore
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
name|NodeDelegate
name|n
init|=
name|sessionDelegate
operator|.
name|getNodeByIdentifier
argument_list|(
name|versionableId
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Unable to restore version. "
operator|+
literal|"No versionable node with identifier: "
operator|+
name|versionableId
argument_list|)
throw|;
block|}
comment|// check lock status
name|checkNotLocked
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// check for existing nodes
name|List
argument_list|<
name|NodeDelegate
argument_list|>
name|existing
init|=
name|getExisting
argument_list|(
name|version
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|existing
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|removeExisting
condition|)
block|{
name|removeExistingNodes
argument_list|(
name|existing
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeDelegate
name|nd
range|:
name|existing
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|nd
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|ItemExistsException
argument_list|(
literal|"Unable to restore with "
operator|+
literal|"removeExisting=false. Existing nodes in "
operator|+
literal|"workspace: "
operator|+
name|paths
argument_list|)
throw|;
block|}
block|}
comment|// ready for restore
name|VersionDelegate
name|vd
init|=
name|versionManagerDelegate
operator|.
name|getVersionByIdentifier
argument_list|(
name|version
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|versionManagerDelegate
operator|.
name|restore
argument_list|(
name|n
operator|.
name|getParent
argument_list|()
argument_list|,
name|n
operator|.
name|getName
argument_list|()
argument_list|,
name|vd
argument_list|)
expr_stmt|;
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// refresh if one of the modifying operations fail
name|sessionDelegate
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeActivity
parameter_list|(
name|Node
name|activityNode
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|merge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|srcWorkspace
parameter_list|,
name|boolean
name|bestEffort
parameter_list|,
name|boolean
name|isShallow
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
name|NodeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|merge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|String
name|srcWorkspace
parameter_list|,
name|boolean
name|bestEffort
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
name|NodeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|merge
parameter_list|(
name|Node
name|activityNode
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
name|NodeIteratorAdapter
operator|.
name|EMPTY
argument_list|)
return|;
block|}
specifier|private
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
return|return
name|sessionContext
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCheckedOut
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
return|return
name|versionManagerDelegate
operator|.
name|isCheckedOut
argument_list|(
name|nodeDelegate
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|VersionHistory
name|getVersionHistory
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|VersionHistory
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|VersionHistory
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|VersionHistoryImpl
argument_list|(
name|internalGetVersionHistory
argument_list|(
name|absPath
argument_list|)
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|getBaseVersion
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Version
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Version
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
return|return
operator|new
name|VersionImpl
argument_list|(
name|versionManagerDelegate
operator|.
name|getBaseVersion
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getActivity
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
literal|null
return|;
comment|//TODO.unimplemented().returnValue(null);
block|}
annotation|@
name|Override
specifier|public
name|void
name|doneMerge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|createConfiguration
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|createActivity
parameter_list|(
name|String
name|title
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|returnValue
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|checkpoint
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// FIXME: atomic?
name|Version
name|v
init|=
name|checkin
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|checkout
argument_list|(
name|absPath
argument_list|)
expr_stmt|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkout
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
name|checkNotLocked
argument_list|(
name|absPath
argument_list|)
expr_stmt|;
name|versionManagerDelegate
operator|.
name|checkout
argument_list|(
name|nodeDelegate
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Version
name|checkin
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Version
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Version
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|absPath
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPath
argument_list|)
throw|;
block|}
name|checkNotLocked
argument_list|(
name|absPath
argument_list|)
expr_stmt|;
return|return
operator|new
name|VersionImpl
argument_list|(
name|versionManagerDelegate
operator|.
name|checkin
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cancelMerge
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
operator|.
name|doNothing
argument_list|()
expr_stmt|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
name|void
name|checkPendingChangesForRestore
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
if|if
condition|(
name|sessionDelegate
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Unable to restore. Session has pending changes."
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|checkNotLocked
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO: avoid nested calls
if|if
condition|(
name|sessionContext
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getLockManager
argument_list|()
operator|.
name|isLocked
argument_list|(
name|absPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LockException
argument_list|(
literal|"Node at "
operator|+
name|absPath
operator|+
literal|" is locked"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the parent for the given<code>absPath</code> or throws a      * {@link PathNotFoundException} if it doesn't exist.      *      * @param sessionDelegate session delegate.      * @param absPath an absolute path      * @return the parent for the given<code>absPath</code>.      * @throws PathNotFoundException if the node does not exist.      */
annotation|@
name|Nonnull
specifier|private
name|NodeDelegate
name|ensureParentExists
parameter_list|(
annotation|@
name|Nonnull
name|SessionDelegate
name|sessionDelegate
parameter_list|,
annotation|@
name|Nonnull
name|String
name|absPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
name|String
name|oakParentPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|checkNotNull
argument_list|(
name|absPath
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|NodeDelegate
name|parent
init|=
name|checkNotNull
argument_list|(
name|sessionDelegate
argument_list|)
operator|.
name|getNode
argument_list|(
name|oakParentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|absPath
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|parent
return|;
block|}
comment|/**      * Returns referenceable nodes outside of the versionable sub-graphs      * identified by<code>versionablePaths</code>, which are also present      * in the versionable state captured by<code>version</code>.      *      * @param version the version.      * @param versionablePaths identifies the starting points of the versionable      *                         sub-graphs.      * @return existing nodes in this workspace.      */
specifier|private
name|List
argument_list|<
name|NodeDelegate
argument_list|>
name|getExisting
parameter_list|(
annotation|@
name|Nonnull
name|Version
name|version
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|versionablePaths
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// collect uuids
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|uuids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|version
operator|.
name|getFrozenNode
argument_list|()
operator|.
name|accept
argument_list|(
operator|new
name|TraversingItemVisitor
operator|.
name|Default
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|entering
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|NodeType
operator|.
name|NT_FROZEN_NODE
argument_list|)
condition|)
block|{
name|uuids
operator|.
name|add
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|Property
operator|.
name|JCR_FROZEN_UUID
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|NodeType
operator|.
name|NT_VERSIONED_CHILD
argument_list|)
condition|)
block|{
comment|// TODO: handle?
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|SessionDelegate
name|delegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
if|if
condition|(
name|uuids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|NodeDelegate
argument_list|>
name|existing
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeDelegate
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|uuid
range|:
name|uuids
control|)
block|{
name|NodeDelegate
name|node
init|=
name|delegate
operator|.
name|getNodeByIdentifier
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|boolean
name|inSubGraph
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|versionablePath
range|:
name|versionablePaths
control|)
block|{
if|if
condition|(
name|node
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|versionablePath
argument_list|)
condition|)
block|{
name|inSubGraph
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|inSubGraph
condition|)
block|{
name|existing
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|existing
return|;
block|}
comment|/**      * Removes existing nodes and throws a {@link RepositoryException} if      * removing one of them fails.      *      * @param existing nodes to remove.      * @throws RepositoryException if the operation fails.      */
specifier|private
name|void
name|removeExistingNodes
parameter_list|(
name|List
argument_list|<
name|NodeDelegate
argument_list|>
name|existing
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|NodeDelegate
name|nd
range|:
name|existing
control|)
block|{
if|if
condition|(
operator|!
name|nd
operator|.
name|remove
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Unable to remove existing node: "
operator|+
name|nd
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Returns the version history for the versionable node at the given path.      *      * @param absPathVersionable path to a versionable node.      * @return the version history.      * @throws PathNotFoundException if the given path does not reference an      *                               existing node.      * @throws UnsupportedRepositoryOperationException      *                               if the node at the given path is not      *                               mix:versionable.      * @throws RepositoryException if some other error occurs.      */
annotation|@
name|Nonnull
specifier|private
name|VersionHistoryDelegate
name|internalGetVersionHistory
parameter_list|(
annotation|@
name|Nonnull
name|String
name|absPathVersionable
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|UnsupportedRepositoryOperationException
block|{
name|SessionDelegate
name|sessionDelegate
init|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
decl_stmt|;
name|String
name|oakPath
init|=
name|getOakPathOrThrowNotFound
argument_list|(
name|checkNotNull
argument_list|(
name|absPathVersionable
argument_list|)
argument_list|)
decl_stmt|;
name|NodeDelegate
name|nodeDelegate
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeDelegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PathNotFoundException
argument_list|(
name|absPathVersionable
argument_list|)
throw|;
block|}
return|return
name|versionManagerDelegate
operator|.
name|getVersionHistory
argument_list|(
name|nodeDelegate
argument_list|)
return|;
block|}
block|}
end_class

end_unit

