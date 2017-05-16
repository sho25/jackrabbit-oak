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
name|plugins
operator|.
name|tree
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

begin_comment
comment|/**  * {@code ReadOnlyVersionManager} provides implementations for read-only  * version operations modeled after the ones available in {@link javax.jcr.version.VersionManager}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReadOnlyVersionManager
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
name|ReadOnlyVersionManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @return the read-only {@link Tree} for the jcr:versionStorage node. The      *         returned {@code Tree} instance must be up-to-date with the      *         {@code Root} returned by {@link #getWorkspaceRoot()}.      */
annotation|@
name|Nonnull
specifier|protected
specifier|abstract
name|Tree
name|getVersionStorage
parameter_list|()
function_decl|;
comment|/**      /**      * @return the {@code Root} of the workspace.      */
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
comment|//--------------------------------------------------------------------------
comment|/**      * Return a new instance of {@code ReadOnlyVersionManager} that reads version      * information from the tree at {@link VersionConstants#VERSION_STORE_PATH}.      *      * @param root The root to read version information from.      * @param namePathMapper The {@code NamePathMapper} to use.      * @return a new instance of {@code ReadOnlyVersionManager}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|ReadOnlyVersionManager
name|getInstance
parameter_list|(
specifier|final
name|Root
name|root
parameter_list|,
specifier|final
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|ReadOnlyVersionManager
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Tree
name|getVersionStorage
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|VersionConstants
operator|.
name|VERSION_STORE_PATH
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Root
name|getWorkspaceRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
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
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
block|}
return|;
block|}
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
comment|/**      * Returns the version tree with the given uuid.      *      * @param uuid the uuid of the version tree.      * @return the version tree or {@code null} if there is none.      */
annotation|@
name|CheckForNull
specifier|public
name|Tree
name|getVersion
parameter_list|(
annotation|@
name|Nonnull
name|String
name|uuid
parameter_list|)
block|{
return|return
name|getIdentifierManager
argument_list|()
operator|.
name|getTree
argument_list|(
name|uuid
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
comment|/**      * Returns {@code true} if the specified tree has {@link VersionConstants#REP_VERSIONSTORAGE}      * defines as primary node type i.e. is part of the intermediate version storage      * structure that contains the version histories and the versions.      *      * @param tree The tree to be tested.      * @return {@code true} if the target node has {@link VersionConstants#REP_VERSIONSTORAGE}      * defines as primary node type; {@code false} otherwise.      */
specifier|public
specifier|static
name|boolean
name|isVersionStoreTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|VersionConstants
operator|.
name|REP_VERSIONSTORAGE
operator|.
name|equals
argument_list|(
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Tries to retrieve the tree corresponding to specified {@code versionTree}      * outside of the version storage based on versionable path information      * stored with the version history. The following cases are distinguished:      *      *<ul>      *<li>Version History: If the given tree is a version history the      *     associated versionable tree in the specified workspace is being returned      *     based on the information stored in the versionable path property. If      *     no versionable path property is present {@code null} is returned.</li>      *<li>Version: Same as for version history.</li>      *<li>Version Labels: Same as for version history.</li>      *<li>Frozen Node: If the given tree forms part of a frozen node the      *     path of the target node is computed from the versionable path and      *     the relative path of the frozen node.</li>      *<li>Other Nodes: If the specified tree is not part of the tree structure      *     defined by a version history, {@code null} will be returned.</li>      *</ul>      *      * Please note that this method will not verify if the tree at the versionable      * path or the computed subtree actually exists. This must be asserted by      * the caller before operating on the tree.      *      * @param versionTree The tree from within the version storage for which      *                    that versionable correspondent should be retrieved.      * @param workspaceName The name of the workspace for which the target should be retrieved.      * @return A existing or non-existing tree pointing to the location of the      * correspondent tree outside of the version storage or {@code null} if the      * versionable path property for the specified workspace is missing or if      * the given tree is not located within the tree structure defined by a version history.      *      * @see VersionablePathHook      * @see VersionConstants#MIX_REP_VERSIONABLE_PATHS      */
annotation|@
name|CheckForNull
specifier|public
name|Tree
name|getVersionable
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|versionTree
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|)
block|{
name|Root
name|root
init|=
name|getWorkspaceRoot
argument_list|()
decl_stmt|;
name|String
name|relPath
init|=
literal|""
decl_stmt|;
name|Tree
name|t
init|=
name|versionTree
decl_stmt|;
while|while
condition|(
name|t
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|isVersionStoreTree
argument_list|(
name|t
argument_list|)
operator|&&
operator|!
name|t
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|VersionConstants
operator|.
name|NT_FROZENNODE
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
name|relPath
operator|=
name|PathUtils
operator|.
name|relativize
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|versionTree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|JcrConstants
operator|.
name|NT_VERSIONHISTORY
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
name|PropertyState
name|prop
init|=
name|t
operator|.
name|getProperty
argument_list|(
name|workspaceName
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
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|prop
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|PATH
argument_list|)
argument_list|,
name|relPath
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// version history is missing the versionable path property for the given workspace name
name|log
operator|.
name|warn
argument_list|(
literal|"Missing versionable path property for {} at {}"
argument_list|,
name|workspaceName
argument_list|,
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|t
operator|=
name|t
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
comment|// intermediate node in the version storage that matches none of the special
comment|// conditions checked above and cannot be resolve to a versionable tree.
return|return
literal|null
return|;
block|}
comment|//----------------------------< internal>----------------------------------
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
comment|/**      * Returns {@code true} if the given {@code tree} is of type      * {@code mix:versionable}; {@code false} otherwise.      *      * @param tree the tree to check.      * @return whether the {@code tree} is versionable.      */
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
comment|/**      * Returns {@code true} if the given {@code versionableCandidate} is of type      * {@code mix:versionable}; {@code false} otherwise.      *      * @param versionableCandidate node state to check.      * @return whether the {@code versionableCandidate} is versionable.      */
name|boolean
name|isVersionable
parameter_list|(
name|NodeState
name|versionableCandidate
parameter_list|)
block|{
comment|// this is not 100% correct, because t.getPath() will
comment|// not return the correct path for node after, but is
comment|// sufficient to check if it is versionable
return|return
name|isVersionable
argument_list|(
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|versionableCandidate
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

