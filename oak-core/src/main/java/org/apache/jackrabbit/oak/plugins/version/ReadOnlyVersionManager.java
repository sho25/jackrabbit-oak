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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
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
name|util
operator|.
name|TreeUtil
import|;
end_import

begin_comment
comment|/**  * {@code ReadOnlyVersionManager} provides implementations for read-only  * version operations modeled after the ones available in {@link javax.jcr.version.VersionManager}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReadOnlyVersionManager
block|{
comment|/**      * @return the read-only {@link Tree} for the jcr:versionStorage node. The      *         returned {@code Tree} instance must be up-to-date with the      *         {@code Root} returned by {@link #getWorkspaceRoot()}.      */
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|Tree
name|getVersionStorage
parameter_list|()
function_decl|;
comment|/**     /**      * @return the {@code Root} of the workspace.      */
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|Root
name|getWorkspaceRoot
parameter_list|()
function_decl|;
comment|/**      * @return the node type manager of this repository.      */
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|ReadOnlyNodeTypeManager
name|getNodeTypeManager
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if the tree is checked out; otherwise      * {@code false}. The root node is always considered checked out.      *      * @param tree the tree to check.      * @return whether the tree is checked out or not.      */
specifier|public
name|boolean
name|isCheckedOut
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|checkNotNull
argument_list|(
name|tree
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|p
init|=
name|tree
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
name|p
operator|!=
literal|null
condition|)
block|{
return|return
name|p
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
else|else
block|{
comment|// FIXME: this actually means access to the tree is restricted
comment|// and may result in wrong isCheckedOut value. This should never
comment|// be the case in a commit hook because it operates on non-access-
comment|// controlled NodeStates. This means consistency is not at risk
comment|// but it may mean oak-jcr sees a node as checked out even though
comment|// it is in fact read-only because of a checked-in ancestor.
block|}
if|if
condition|(
name|tree
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// otherwise return checkedOut status of parent
return|return
name|isCheckedOut
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns {@code true} if the tree at the given absolute Oak path is      * checked out; otherwise {@code false}.      *      * @param absOakPath an absolute path.      * @return whether the tree at the given path is checked out or not.      */
specifier|public
name|boolean
name|isCheckedOut
parameter_list|(
annotation|@
name|Nonnull
name|String
name|absOakPath
parameter_list|)
block|{
return|return
name|isCheckedOut
argument_list|(
name|getWorkspaceRoot
argument_list|()
operator|.
name|getTree
argument_list|(
name|checkNotNull
argument_list|(
name|absOakPath
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the tree representing the version history of the given      * versionable tree or {@code null} if none exists yet.      *      * @param versionable the versionable tree.      * @return the version history or {@code null} if none exists yet.      * @throws UnsupportedRepositoryOperationException      *                             if the versionable tree is not actually      *                             versionable.      * @throws RepositoryException if an error occurs while checking the node      *                             type of the tree.      */
annotation|@
name|CheckForNull
specifier|public
name|Tree
name|getVersionHistory
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|versionable
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
name|checkVersionable
argument_list|(
name|versionable
argument_list|)
expr_stmt|;
name|String
name|uuid
init|=
name|versionable
operator|.
name|getProperty
argument_list|(
name|VersionConstants
operator|.
name|JCR_UUID
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
return|return
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|getVersionStorage
argument_list|()
argument_list|,
name|getVersionHistoryPath
argument_list|(
name|uuid
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the path of the version history for the given {@code uuid}.      * The returned path is relative to the version storage tree as returned      * by {@link #getVersionStorage()}.      *      * @param uuid the uuid of the versionable node      * @return the relative path of the version history for the given uuid.      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getVersionHistoryPath
parameter_list|(
annotation|@
name|Nonnull
name|String
name|uuid
parameter_list|)
block|{
name|String
name|relPath
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|uuid
operator|.
name|substring
argument_list|(
name|i
operator|*
literal|2
argument_list|,
name|i
operator|*
literal|2
operator|+
literal|2
argument_list|)
decl_stmt|;
name|relPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|relPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|relPath
argument_list|,
name|uuid
argument_list|)
return|;
block|}
comment|/**      * Returns the tree representing the base version of the given versionable      * tree or {@code null} if none exists yet. This is the case when a      * versionable node is created, but is not yet saved.      *      * @param versionable the versionable tree.      * @return the tree representing the base version or {@code null}.      * @throws UnsupportedRepositoryOperationException      *                             if the versionable tree is not actually      *                             versionable.      * @throws RepositoryException if an error occurs while checking the node      *                             type of the tree.      */
annotation|@
name|CheckForNull
specifier|public
name|Tree
name|getBaseVersion
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|versionable
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
name|checkVersionable
argument_list|(
name|versionable
argument_list|)
expr_stmt|;
name|PropertyState
name|p
init|=
name|versionable
operator|.
name|getProperty
argument_list|(
name|VersionConstants
operator|.
name|JCR_BASEVERSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
comment|// version history does not yet exist
return|return
literal|null
return|;
block|}
return|return
name|getIdentifierManager
argument_list|()
operator|.
name|getTree
argument_list|(
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
return|;
block|}
comment|//----------------------------< internal>----------------------------------
annotation|@
name|Nonnull
specifier|private
specifier|static
name|String
name|getRelativePath
parameter_list|(
annotation|@
name|Nonnull
name|String
name|absJcrPath
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|checkNotNull
argument_list|(
name|absJcrPath
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"Path is not absolute: "
operator|+
name|absJcrPath
argument_list|)
expr_stmt|;
name|String
name|relPath
init|=
name|absJcrPath
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
operator|!
name|relPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"Invalid path: "
operator|+
name|absJcrPath
argument_list|)
expr_stmt|;
return|return
name|relPath
return|;
block|}
comment|/**      * @return an identifier manager that is able to resolve identifiers of      *         nodes in the version storage.      */
specifier|protected
name|IdentifierManager
name|getIdentifierManager
parameter_list|()
block|{
comment|// FIXME: may need to revise this, because getVersionStorageTree()
comment|// is not the same Root as getWorkspaceRoot()
return|return
operator|new
name|IdentifierManager
argument_list|(
name|getWorkspaceRoot
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Checks if the given {@code tree} is versionable and throws a {@link      * UnsupportedRepositoryOperationException} if it is not.      *      * @param tree the tree to check.      * @return the passed tree.      * @throws UnsupportedRepositoryOperationException      *                             if the tree is not versionable.      * @throws RepositoryException if an error occurs while checking the node      *                             type of the tree.      */
annotation|@
name|Nonnull
specifier|protected
name|Tree
name|checkVersionable
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isVersionable
argument_list|(
name|checkNotNull
argument_list|(
name|tree
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Node at "
operator|+
name|tree
operator|.
name|getPath
argument_list|()
operator|+
literal|" is not versionable"
argument_list|)
throw|;
block|}
return|return
name|tree
return|;
block|}
comment|/**      * Returns {@code true} if the given {@code tree} is of type      * {@code mix:versionable}; {@code false} otherwise.      *      * @param tree the tree to check.      * @return whether the {@code tree} is versionable.      * @throws RepositoryException if an error occurs while checking the node      *                             type of the tree.      */
specifier|protected
name|boolean
name|isVersionable
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|getNodeTypeManager
argument_list|()
operator|.
name|isNodeType
argument_list|(
name|checkNotNull
argument_list|(
name|tree
argument_list|)
argument_list|,
name|VersionConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

