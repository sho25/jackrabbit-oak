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
name|version
operator|.
name|LabelExistsVersionException
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|ReadOnlyNodeTypeManager
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
name|version
operator|.
name|ReadOnlyVersionManager
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
name|TreeUtil
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|ISO8601
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
name|checkArgument
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
name|JcrConstants
operator|.
name|JCR_CREATED
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
name|JCR_VERSIONLABELS
import|;
end_import

begin_comment
comment|/**  * {@code ReadWriteVersionManager}...  */
end_comment

begin_class
specifier|public
class|class
name|ReadWriteVersionManager
extends|extends
name|ReadOnlyVersionManager
block|{
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|VersionStorage
name|versionStorage
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
init|=
name|Clock
operator|.
name|ACCURATE
decl_stmt|;
specifier|public
name|ReadWriteVersionManager
parameter_list|(
annotation|@
name|NotNull
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|versionStorage
operator|=
operator|new
name|VersionStorage
argument_list|(
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called by the write methods to refresh the state of the possible      * session associated with this instance. The default implementation      * of this method does nothing, but a subclass can use this callback      * to keep a session in sync with the persisted version changes.      *      * @throws RepositoryException if the session could not be refreshed      */
specifier|protected
name|void
name|refresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|sessionDelegate
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|protected
name|Tree
name|getVersionStorage
parameter_list|()
block|{
return|return
name|versionStorage
operator|.
name|getTree
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|protected
name|Root
name|getWorkspaceRoot
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|protected
name|ReadOnlyNodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
return|return
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|sessionDelegate
operator|.
name|getRoot
argument_list|()
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
comment|/**      * Performs a checkin on a versionable tree and returns the tree that      * represents the created version.      *      * @param versionable the versionable node to check in.      * @return the created version.      * @throws InvalidItemStateException if the current root has pending      *                                   changes.      * @throws UnsupportedRepositoryOperationException      *                                   if the versionable tree isn't actually      *                                   versionable.      * @throws RepositoryException       if an error occurs while checking the      *                                   node type of the tree.      */
annotation|@
name|NotNull
specifier|public
name|Tree
name|checkin
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|versionable
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|InvalidItemStateException
throws|,
name|UnsupportedRepositoryOperationException
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
literal|"Unable to perform checkin. "
operator|+
literal|"Session has pending changes."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isVersionable
argument_list|(
name|versionable
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
name|versionable
operator|.
name|getPath
argument_list|()
operator|+
literal|" is not versionable"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isCheckedOut
argument_list|(
name|versionable
argument_list|)
condition|)
block|{
name|Tree
name|baseVersion
init|=
name|getExistingBaseVersion
argument_list|(
name|versionable
argument_list|)
decl_stmt|;
name|versionable
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|PropertyState
name|created
init|=
name|baseVersion
operator|.
name|getProperty
argument_list|(
name|JCR_CREATED
argument_list|)
decl_stmt|;
name|long
name|c
init|=
name|created
operator|==
literal|null
condition|?
literal|0
else|:
name|ISO8601
operator|.
name|parse
argument_list|(
name|created
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|last
init|=
name|Math
operator|.
name|max
argument_list|(
name|c
argument_list|,
name|clock
operator|.
name|getTimeIncreasing
argument_list|()
argument_list|)
decl_stmt|;
comment|// wait for clock to change so that the new version has a distinct
comment|// timestamp from the last checkin performed by this VersionManager
comment|// see https://issues.apache.org/jira/browse/OAK-7512
name|clock
operator|.
name|waitUntil
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
try|try
block|{
name|sessionDelegate
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|sessionDelegate
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
block|}
return|return
name|getExistingBaseVersion
argument_list|(
name|getWorkspaceRoot
argument_list|()
operator|.
name|getTree
argument_list|(
name|versionable
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Performs a checkout on a versionable tree.      *      * @param workspaceRoot a fresh workspace root without pending changes.      * @param versionablePath the absolute path to the versionable node to check out.      * @throws UnsupportedRepositoryOperationException      *                             if the versionable tree isn't actually      *                             versionable.      * @throws RepositoryException if an error occurs while checking the      *                             node type of the tree.      * @throws IllegalStateException if the workspaceRoot has pending changes.      * @throws IllegalArgumentException if the {@code versionablePath} is      *                             not absolute.      */
specifier|public
name|void
name|checkout
parameter_list|(
annotation|@
name|NotNull
name|Root
name|workspaceRoot
parameter_list|,
annotation|@
name|NotNull
name|String
name|versionablePath
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|InvalidItemStateException
throws|,
name|RepositoryException
block|{
name|checkState
argument_list|(
operator|!
name|workspaceRoot
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|versionablePath
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|versionable
init|=
name|workspaceRoot
operator|.
name|getTree
argument_list|(
name|versionablePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isVersionable
argument_list|(
name|versionable
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
name|versionable
operator|.
name|getPath
argument_list|()
operator|+
literal|" is not versionable"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isCheckedOut
argument_list|(
name|versionable
argument_list|)
condition|)
block|{
name|versionable
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
try|try
block|{
name|workspaceRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|workspaceRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|addVersionLabel
parameter_list|(
annotation|@
name|NotNull
name|VersionStorage
name|versionStorage
parameter_list|,
annotation|@
name|NotNull
name|String
name|versionHistoryOakRelPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|versionIdentifier
parameter_list|,
annotation|@
name|NotNull
name|String
name|oakVersionLabel
parameter_list|,
name|boolean
name|moveLabel
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|versionHistory
init|=
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|checkNotNull
argument_list|(
name|versionStorage
operator|.
name|getTree
argument_list|()
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|versionHistoryOakRelPath
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|labels
init|=
name|checkNotNull
argument_list|(
name|versionHistory
argument_list|)
operator|.
name|getChild
argument_list|(
name|JCR_VERSIONLABELS
argument_list|)
decl_stmt|;
name|PropertyState
name|existing
init|=
name|labels
operator|.
name|getProperty
argument_list|(
name|checkNotNull
argument_list|(
name|oakVersionLabel
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|moveLabel
condition|)
block|{
name|labels
operator|.
name|removeProperty
argument_list|(
name|existing
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|LabelExistsVersionException
argument_list|(
literal|"Version label '"
operator|+
name|oakVersionLabel
operator|+
literal|"' already exists on this version history"
argument_list|)
throw|;
block|}
block|}
name|labels
operator|.
name|setProperty
argument_list|(
name|oakVersionLabel
argument_list|,
name|versionIdentifier
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
try|try
block|{
name|sessionDelegate
operator|.
name|commit
argument_list|(
name|versionStorage
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|versionStorage
operator|.
name|refresh
argument_list|()
expr_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
block|}
specifier|public
name|void
name|removeVersionLabel
parameter_list|(
annotation|@
name|NotNull
name|VersionStorage
name|versionStorage
parameter_list|,
annotation|@
name|NotNull
name|String
name|versionHistoryOakRelPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|oakVersionLabel
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|versionHistory
init|=
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|checkNotNull
argument_list|(
name|versionStorage
operator|.
name|getTree
argument_list|()
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|versionHistoryOakRelPath
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|labels
init|=
name|checkNotNull
argument_list|(
name|versionHistory
argument_list|)
operator|.
name|getChild
argument_list|(
name|JCR_VERSIONLABELS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|labels
operator|.
name|hasProperty
argument_list|(
name|oakVersionLabel
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Version label "
operator|+
name|oakVersionLabel
operator|+
literal|" does not exist on this version history"
argument_list|)
throw|;
block|}
name|labels
operator|.
name|removeProperty
argument_list|(
name|oakVersionLabel
argument_list|)
expr_stmt|;
try|try
block|{
name|sessionDelegate
operator|.
name|commit
argument_list|(
name|versionStorage
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|versionStorage
operator|.
name|refresh
argument_list|()
expr_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
block|}
specifier|public
name|void
name|removeVersion
parameter_list|(
annotation|@
name|NotNull
name|VersionStorage
name|versionStorage
parameter_list|,
annotation|@
name|NotNull
name|String
name|versionHistoryOakRelPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|oakVersionName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|versionHistory
init|=
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|versionStorage
operator|.
name|getTree
argument_list|()
argument_list|,
name|versionHistoryOakRelPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|versionHistory
operator|==
literal|null
operator|||
operator|!
name|versionHistory
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Version history "
operator|+
name|versionHistoryOakRelPath
operator|+
literal|" does not exist on this version storage"
argument_list|)
throw|;
block|}
name|Tree
name|version
init|=
name|versionHistory
operator|.
name|getChild
argument_list|(
name|oakVersionName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|version
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|VersionException
argument_list|(
literal|"Version "
operator|+
name|oakVersionName
operator|+
literal|" does not exist on this version history"
argument_list|)
throw|;
block|}
name|version
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|sessionDelegate
operator|.
name|commit
argument_list|(
name|versionStorage
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|versionStorage
operator|.
name|refresh
argument_list|()
expr_stmt|;
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
block|}
comment|// TODO: more methods that modify versions
comment|//------------------------------------------------------------< private>---
annotation|@
name|NotNull
specifier|private
name|Tree
name|getExistingBaseVersion
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|versionableTree
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|baseVersion
init|=
name|getBaseVersion
argument_list|(
name|versionableTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Base version does not exist for "
operator|+
name|versionableTree
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|baseVersion
return|;
block|}
block|}
end_class

end_unit

