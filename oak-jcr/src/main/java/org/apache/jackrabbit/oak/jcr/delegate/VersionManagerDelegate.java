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
name|delegate
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
name|version
operator|.
name|ReadWriteVersionManager
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
name|version
operator|.
name|VersionStorage
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
name|JCR_FROZENMIXINTYPES
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
name|JCR_FROZENPRIMARYTYPE
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
name|JCR_FROZENUUID
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
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|JCR_VERSIONHISTORY
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
name|version
operator|.
name|VersionConstants
operator|.
name|RESTORE_PREFIX
import|;
end_import

begin_comment
comment|/**  * {@code VersionManagerDelegate}...  */
end_comment

begin_class
specifier|public
class|class
name|VersionManagerDelegate
block|{
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteVersionManager
name|versionManager
decl_stmt|;
specifier|public
specifier|static
name|VersionManagerDelegate
name|create
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
return|return
operator|new
name|VersionManagerDelegate
argument_list|(
name|sessionDelegate
argument_list|)
return|;
block|}
specifier|private
name|VersionManagerDelegate
parameter_list|(
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
name|versionManager
operator|=
operator|new
name|ReadWriteVersionManager
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|checkin
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|versionManager
operator|.
name|checkin
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|checkout
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// perform the operation on a fresh root because
comment|// it must not save pending changes in the workspace
name|Root
name|fresh
init|=
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|versionManager
operator|.
name|checkout
argument_list|(
name|fresh
argument_list|,
name|nodeDelegate
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCheckedOut
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|versionManager
operator|.
name|isCheckedOut
argument_list|(
name|getTree
argument_list|(
name|nodeDelegate
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionHistoryDelegate
name|createVersionHistory
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|versionHistory
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|VersionHistoryDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|getTree
argument_list|(
name|versionHistory
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|createVersion
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|version
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|getTree
argument_list|(
name|version
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionHistoryDelegate
name|getVersionHistory
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|versionable
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|vh
init|=
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|getTree
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|vh
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Node does not"
operator|+
literal|" have a version history: "
operator|+
name|versionable
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|VersionHistoryDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|vh
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|getBaseVersion
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|versionable
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|v
init|=
name|versionManager
operator|.
name|getBaseVersion
argument_list|(
name|getTree
argument_list|(
name|versionable
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Node does not"
operator|+
literal|" have a base version: "
operator|+
name|versionable
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|v
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|VersionDelegate
name|getVersionByIdentifier
parameter_list|(
annotation|@
name|Nonnull
name|String
name|identifier
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|t
init|=
name|sessionDelegate
operator|.
name|getIdManager
argument_list|()
operator|.
name|getTree
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
operator|||
operator|!
name|t
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"No such Version with identifier: "
operator|+
name|identifier
argument_list|)
throw|;
block|}
return|return
name|VersionDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|t
argument_list|)
return|;
block|}
specifier|public
name|void
name|restore
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|,
annotation|@
name|Nonnull
name|VersionDelegate
name|vd
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeDelegate
name|frozen
init|=
name|vd
operator|.
name|getFrozenNode
argument_list|()
decl_stmt|;
name|PropertyState
name|primaryType
init|=
name|frozen
operator|.
name|getProperty
argument_list|(
name|JCR_FROZENPRIMARYTYPE
argument_list|)
operator|.
name|getPropertyState
argument_list|()
decl_stmt|;
name|PropertyState
name|uuid
init|=
name|frozen
operator|.
name|getProperty
argument_list|(
name|JCR_FROZENUUID
argument_list|)
operator|.
name|getPropertyState
argument_list|()
decl_stmt|;
name|PropertyDelegate
name|mixinTypes
init|=
name|frozen
operator|.
name|getPropertyOrNull
argument_list|(
name|JCR_FROZENMIXINTYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|.
name|getChild
argument_list|(
name|oakName
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// create a sentinel node with a jcr:baseVersion pointing
comment|// to the version to restore
name|Tree
name|t
init|=
name|parent
operator|.
name|getTree
argument_list|()
operator|.
name|addChild
argument_list|(
name|oakName
argument_list|)
decl_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|primaryType
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_UUID
argument_list|,
name|uuid
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
if|if
condition|(
name|mixinTypes
operator|!=
literal|null
operator|&&
name|mixinTypes
operator|.
name|getPropertyState
argument_list|()
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|mixinTypes
operator|.
name|getPropertyState
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_BASEVERSION
argument_list|,
name|vd
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_VERSIONHISTORY
argument_list|,
name|vd
operator|.
name|getParent
argument_list|()
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Tree
name|t
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|oakName
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JCR_BASEVERSION
argument_list|,
name|RESTORE_PREFIX
operator|+
name|vd
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Add a version label to the given version history.      *      * @param versionHistory the version history.      * @param version the version.      * @param oakVersionLabel the version label.      * @param moveLabel whether to move the label if it already exists.      * @throws InvalidItemStateException if any of the nodes is stale.      * @throws LabelExistsVersionException if moveLabel is false, and an attempt      * is made to add a label that already exists in this version history.      * @throws VersionException if the specified version does not exist in this      * version history or if the specified version is the root version (jcr:rootVersion).      * @throws RepositoryException if another error occurs.      */
specifier|public
name|void
name|addVersionLabel
parameter_list|(
annotation|@
name|Nonnull
name|VersionHistoryDelegate
name|versionHistory
parameter_list|,
annotation|@
name|Nonnull
name|VersionDelegate
name|version
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakVersionLabel
parameter_list|,
name|boolean
name|moveLabel
parameter_list|)
throws|throws
name|InvalidItemStateException
throws|,
name|LabelExistsVersionException
throws|,
name|VersionException
throws|,
name|RepositoryException
block|{
comment|// perform operation on fresh storage to not interfere
comment|// with pending changes in the workspace.
name|Root
name|fresh
init|=
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|VersionStorage
name|storage
init|=
operator|new
name|VersionStorage
argument_list|(
name|fresh
argument_list|)
decl_stmt|;
name|String
name|vhRelPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|VersionStorage
operator|.
name|VERSION_STORAGE_PATH
argument_list|,
name|checkNotNull
argument_list|(
name|versionHistory
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|versionManager
operator|.
name|addVersionLabel
argument_list|(
name|storage
argument_list|,
name|vhRelPath
argument_list|,
name|checkNotNull
argument_list|(
name|version
argument_list|)
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|checkNotNull
argument_list|(
name|oakVersionLabel
argument_list|)
argument_list|,
name|moveLabel
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a version label from the given history.      *      * @param versionHistory the version history.      * @param oakVersionLabel the version label.      * @throws InvalidItemStateException if any of the nodes is stale.      * @throws VersionException if the name label does not exist in this version history.      * @throws RepositoryException if another error occurs.      */
specifier|public
name|void
name|removeVersionLabel
parameter_list|(
annotation|@
name|Nonnull
name|VersionHistoryDelegate
name|versionHistory
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakVersionLabel
parameter_list|)
throws|throws
name|InvalidItemStateException
throws|,
name|VersionException
throws|,
name|RepositoryException
block|{
comment|// perform operation on fresh storage to not interfere
comment|// with pending changes in the workspace.
name|Root
name|fresh
init|=
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|VersionStorage
name|storage
init|=
operator|new
name|VersionStorage
argument_list|(
name|fresh
argument_list|)
decl_stmt|;
name|String
name|vhRelPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|VersionStorage
operator|.
name|VERSION_STORAGE_PATH
argument_list|,
name|checkNotNull
argument_list|(
name|versionHistory
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|versionManager
operator|.
name|removeVersionLabel
argument_list|(
name|storage
argument_list|,
name|vhRelPath
argument_list|,
name|checkNotNull
argument_list|(
name|oakVersionLabel
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a version from the given history.      *      * @param versionHistory the version history delegate.      * @param oakVersionName the version name      * @throws RepositoryException if an error occurs.      */
specifier|public
name|void
name|removeVersion
parameter_list|(
annotation|@
name|Nonnull
name|VersionHistoryDelegate
name|versionHistory
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakVersionName
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// perform operation on fresh storage to not interfere
comment|// with pending changes in the workspace.
name|Root
name|fresh
init|=
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|VersionStorage
name|storage
init|=
operator|new
name|VersionStorage
argument_list|(
name|fresh
argument_list|)
decl_stmt|;
name|String
name|vhRelPath
init|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|VersionStorage
operator|.
name|VERSION_STORAGE_PATH
argument_list|,
name|checkNotNull
argument_list|(
name|versionHistory
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|versionManager
operator|.
name|removeVersion
argument_list|(
name|storage
argument_list|,
name|vhRelPath
argument_list|,
name|oakVersionName
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------< internal>----------------------------------
comment|/**      * Returns the underlying tree.      *      * @param nodeDelegate the node delegate.      * @return the underlying tree.      * @throws InvalidItemStateException if the location points to a stale item.      */
annotation|@
name|Nonnull
specifier|private
specifier|static
name|Tree
name|getTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeDelegate
name|nodeDelegate
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
return|return
name|checkNotNull
argument_list|(
name|nodeDelegate
argument_list|)
operator|.
name|getTree
argument_list|()
return|;
block|}
block|}
end_class

end_unit

