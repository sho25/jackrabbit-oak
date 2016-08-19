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
name|upgrade
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|Oak
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
name|plugins
operator|.
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|CompositeEditorProvider
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
name|EditorHook
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|NodeStore
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|LoggingCompositeHook
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
name|upgrade
operator|.
name|cli
operator|.
name|node
operator|.
name|TarNodeStore
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
name|upgrade
operator|.
name|nodestate
operator|.
name|NameFilteringNodeState
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
name|upgrade
operator|.
name|nodestate
operator|.
name|report
operator|.
name|LoggingReporter
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
name|upgrade
operator|.
name|nodestate
operator|.
name|report
operator|.
name|ReportingNodeState
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
name|upgrade
operator|.
name|nodestate
operator|.
name|NodeStateCopier
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
name|upgrade
operator|.
name|version
operator|.
name|VersionCopyConfiguration
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
name|upgrade
operator|.
name|version
operator|.
name|VersionableEditor
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
name|collect
operator|.
name|ImmutableSet
operator|.
name|copyOf
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
name|ImmutableSet
operator|.
name|of
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
name|Lists
operator|.
name|newArrayList
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
name|Lists
operator|.
name|transform
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
name|union
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
name|sort
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|DEFAULT_EXCLUDE_PATHS
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|DEFAULT_INCLUDE_PATHS
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|DEFAULT_MERGE_PATHS
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|calculateEffectiveIncludePaths
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|createIndexEditorProvider
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|createTypeEditorProvider
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
name|upgrade
operator|.
name|RepositoryUpgrade
operator|.
name|markIndexesToBeRebuilt
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
name|upgrade
operator|.
name|nodestate
operator|.
name|NodeStateCopier
operator|.
name|copyProperties
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
name|upgrade
operator|.
name|version
operator|.
name|VersionCopier
operator|.
name|copyVersionStorage
import|;
end_import

begin_class
specifier|public
class|class
name|RepositorySidegrade
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RepositorySidegrade
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Target node store.      */
specifier|private
specifier|final
name|NodeStore
name|target
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|source
decl_stmt|;
comment|/**      * Paths to include during the copy process. Defaults to the root path "/".      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|includePaths
init|=
name|DEFAULT_INCLUDE_PATHS
decl_stmt|;
comment|/**      * Paths to exclude during the copy process. Empty by default.      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|excludePaths
init|=
name|DEFAULT_EXCLUDE_PATHS
decl_stmt|;
comment|/**      * Paths to merge during the copy process. Empty by default.      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|mergePaths
init|=
name|DEFAULT_MERGE_PATHS
decl_stmt|;
specifier|private
name|boolean
name|includeIndex
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|filterLongNames
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|skipInitialization
init|=
literal|false
decl_stmt|;
specifier|private
name|List
argument_list|<
name|CommitHook
argument_list|>
name|customCommitHooks
init|=
literal|null
decl_stmt|;
name|VersionCopyConfiguration
name|versionCopyConfiguration
init|=
operator|new
name|VersionCopyConfiguration
argument_list|()
decl_stmt|;
comment|/**      * Configures the version storage copy. Be default all versions are copied.      * One may disable it completely by setting {@code null} here or limit it to      * a selected date range: {@code<minDate, now()>}.      *       * @param minDate      *            minimum date of the versions to copy or {@code null} to      *            disable the storage version copying completely. Default value:      *            {@code 1970-01-01 00:00:00}.      */
specifier|public
name|void
name|setCopyVersions
parameter_list|(
name|Calendar
name|minDate
parameter_list|)
block|{
name|versionCopyConfiguration
operator|.
name|setCopyVersions
argument_list|(
name|minDate
argument_list|)
expr_stmt|;
block|}
comment|/**      * Configures copying of the orphaned version histories (eg. ones that are      * not referenced by the existing nodes). By default all orphaned version      * histories are copied. One may disable it completely by setting      * {@code null} here or limit it to a selected date range:      * {@code<minDate, now()>}.<br>      *<br>      * Please notice, that this option is overriden by the      * {@link #setCopyVersions(Calendar)}. You can't copy orphaned versions      * older than set in {@link #setCopyVersions(Calendar)} and if you set      * {@code null} there, this option will be ignored.      *       * @param minDate      *            minimum date of the orphaned versions to copy or {@code null}      *            to not copy them at all. Default value:      *            {@code 1970-01-01 00:00:00}.      */
specifier|public
name|void
name|setCopyOrphanedVersions
parameter_list|(
name|Calendar
name|minDate
parameter_list|)
block|{
name|versionCopyConfiguration
operator|.
name|setCopyOrphanedVersions
argument_list|(
name|minDate
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a tool for copying the full contents of the source repository      * to the given target repository. Any existing content in the target      * repository will be overwritten.      *      * @param source source node store      * @param target target node store      */
specifier|public
name|RepositorySidegrade
parameter_list|(
name|NodeStore
name|source
parameter_list|,
name|NodeStore
name|target
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
comment|/**      * Returns the list of custom CommitHooks to be applied before the final      * type validation, reference and indexing hooks.      *      * @return the list of custom CommitHooks      */
specifier|public
name|List
argument_list|<
name|CommitHook
argument_list|>
name|getCustomCommitHooks
parameter_list|()
block|{
return|return
name|customCommitHooks
return|;
block|}
comment|/**      * Sets the list of custom CommitHooks to be applied before the final      * type validation, reference and indexing hooks.      *      * @param customCommitHooks the list of custom CommitHooks      */
specifier|public
name|void
name|setCustomCommitHooks
parameter_list|(
name|List
argument_list|<
name|CommitHook
argument_list|>
name|customCommitHooks
parameter_list|)
block|{
name|this
operator|.
name|customCommitHooks
operator|=
name|customCommitHooks
expr_stmt|;
block|}
comment|/**      * Sets the paths that should be included when the source repository      * is copied to the target repository.      *      * @param includes Paths to be included in the copy.      */
specifier|public
name|void
name|setIncludes
parameter_list|(
annotation|@
name|Nonnull
name|String
modifier|...
name|includes
parameter_list|)
block|{
name|this
operator|.
name|includePaths
operator|=
name|copyOf
argument_list|(
name|checkNotNull
argument_list|(
name|includes
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the paths that should be excluded when the source repository      * is copied to the target repository.      *      * @param excludes Paths to be excluded from the copy.      */
specifier|public
name|void
name|setExcludes
parameter_list|(
annotation|@
name|Nonnull
name|String
modifier|...
name|excludes
parameter_list|)
block|{
name|this
operator|.
name|excludePaths
operator|=
name|copyOf
argument_list|(
name|checkNotNull
argument_list|(
name|excludes
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setIncludeIndex
parameter_list|(
name|boolean
name|includeIndex
parameter_list|)
block|{
name|this
operator|.
name|includeIndex
operator|=
name|includeIndex
expr_stmt|;
block|}
comment|/**      * Sets the paths that should be merged when the source repository      * is copied to the target repository.      *      * @param merges Paths to be merged during copy.      */
specifier|public
name|void
name|setMerges
parameter_list|(
annotation|@
name|Nonnull
name|String
modifier|...
name|merges
parameter_list|)
block|{
name|this
operator|.
name|mergePaths
operator|=
name|copyOf
argument_list|(
name|checkNotNull
argument_list|(
name|merges
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFilterLongNames
parameter_list|()
block|{
return|return
name|filterLongNames
return|;
block|}
specifier|public
name|void
name|setFilterLongNames
parameter_list|(
name|boolean
name|filterLongNames
parameter_list|)
block|{
name|this
operator|.
name|filterLongNames
operator|=
name|filterLongNames
expr_stmt|;
block|}
comment|/**      * Skip the new repository initialization. Only copy content passed in the      * {@link #includePaths}.      *      * @param skipInitialization      */
specifier|public
name|void
name|setSkipInitialization
parameter_list|(
name|boolean
name|skipInitialization
parameter_list|)
block|{
name|this
operator|.
name|skipInitialization
operator|=
name|skipInitialization
expr_stmt|;
block|}
comment|/**      * Same as {@link #copy(RepositoryInitializer)}, but with no custom initializer.       */
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|copy
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Copies the full content from the source to the target repository.      *<p>      * The source repository<strong>must not be modified</strong> while      * the copy operation is running to avoid an inconsistent copy.      *<p>      * Note that both the source and the target repository must be closed      * during the copy operation as this method requires exclusive access      * to the repositories.      *       * @param initializer optional extra repository initializer to use      *      * @throws RepositoryException if the copy operation fails      */
specifier|public
name|void
name|copy
parameter_list|(
name|RepositoryInitializer
name|initializer
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|NodeBuilder
name|targetRoot
init|=
name|target
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|skipInitialization
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping the repository initialization"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|targetRoot
argument_list|)
expr_stmt|;
if|if
condition|(
name|initializer
operator|!=
literal|null
condition|)
block|{
name|initializer
operator|.
name|initialize
argument_list|(
name|targetRoot
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|NodeState
name|reportingSourceRoot
init|=
name|ReportingNodeState
operator|.
name|wrap
argument_list|(
name|source
operator|.
name|getRoot
argument_list|()
argument_list|,
operator|new
name|LoggingReporter
argument_list|(
name|LOG
argument_list|,
literal|"Copying"
argument_list|,
literal|10000
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NodeState
name|sourceRoot
decl_stmt|;
if|if
condition|(
name|filterLongNames
condition|)
block|{
name|sourceRoot
operator|=
name|NameFilteringNodeState
operator|.
name|wrap
argument_list|(
name|reportingSourceRoot
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sourceRoot
operator|=
name|reportingSourceRoot
expr_stmt|;
block|}
name|copyState
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Failed to copy content"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|removeCheckpointReferences
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// removing references to the checkpoints,
comment|// which don't exist in the new repository
name|builder
operator|.
name|setChildNode
argument_list|(
literal|":async"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|copyState
parameter_list|(
name|NodeState
name|sourceRoot
parameter_list|,
name|NodeBuilder
name|targetRoot
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|copyWorkspace
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeIndex
condition|)
block|{
name|IndexCopier
operator|.
name|copy
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|,
name|includePaths
argument_list|)
expr_stmt|;
block|}
name|boolean
name|isRemoveCheckpointReferences
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|copyCheckpoints
argument_list|(
name|targetRoot
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Copying checkpoints is not supported for this combination of node stores"
argument_list|)
expr_stmt|;
name|isRemoveCheckpointReferences
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|DEFAULT_INCLUDE_PATHS
operator|.
name|equals
argument_list|(
name|includePaths
argument_list|)
condition|)
block|{
name|isRemoveCheckpointReferences
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|isRemoveCheckpointReferences
condition|)
block|{
name|removeCheckpointReferences
argument_list|(
name|targetRoot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|versionCopyConfiguration
operator|.
name|skipOrphanedVersionsCopy
argument_list|()
condition|)
block|{
name|copyVersionStorage
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|,
name|versionCopyConfiguration
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|CommitHook
argument_list|>
name|hooks
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitHook
argument_list|>
argument_list|()
decl_stmt|;
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|EditorHook
argument_list|(
operator|new
name|VersionableEditor
operator|.
name|Provider
argument_list|(
name|sourceRoot
argument_list|,
name|Oak
operator|.
name|DEFAULT_WORKSPACE_NAME
argument_list|,
name|versionCopyConfiguration
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|customCommitHooks
operator|!=
literal|null
condition|)
block|{
name|hooks
operator|.
name|addAll
argument_list|(
name|customCommitHooks
argument_list|)
expr_stmt|;
block|}
name|markIndexesToBeRebuilt
argument_list|(
name|targetRoot
argument_list|)
expr_stmt|;
comment|// type validation, reference and indexing hooks
name|hooks
operator|.
name|add
argument_list|(
operator|new
name|EditorHook
argument_list|(
operator|new
name|CompositeEditorProvider
argument_list|(
name|createTypeEditorProvider
argument_list|()
argument_list|,
name|createIndexEditorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|target
operator|.
name|merge
argument_list|(
name|targetRoot
argument_list|,
operator|new
name|LoggingCompositeHook
argument_list|(
name|hooks
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|copyWorkspace
parameter_list|(
name|NodeState
name|sourceRoot
parameter_list|,
name|NodeBuilder
name|targetRoot
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|includes
init|=
name|calculateEffectiveIncludePaths
argument_list|(
name|includePaths
argument_list|,
name|sourceRoot
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
init|=
name|union
argument_list|(
name|copyOf
argument_list|(
name|this
operator|.
name|excludePaths
argument_list|)
argument_list|,
name|of
argument_list|(
literal|"/jcr:system/jcr:versionStorage"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|merges
init|=
name|union
argument_list|(
name|copyOf
argument_list|(
name|this
operator|.
name|mergePaths
argument_list|)
argument_list|,
name|of
argument_list|(
literal|"/jcr:system"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeStateCopier
operator|.
name|builder
argument_list|()
operator|.
name|include
argument_list|(
name|includes
argument_list|)
operator|.
name|exclude
argument_list|(
name|excludes
argument_list|)
operator|.
name|merge
argument_list|(
name|merges
argument_list|)
operator|.
name|copy
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|)
expr_stmt|;
if|if
condition|(
name|includePaths
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|copyProperties
argument_list|(
name|sourceRoot
argument_list|,
name|targetRoot
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|copyCheckpoints
parameter_list|(
name|NodeBuilder
name|targetRoot
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|source
operator|instanceof
name|TarNodeStore
operator|&&
name|target
operator|instanceof
name|TarNodeStore
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TarNodeStore
name|sourceTarNS
init|=
operator|(
name|TarNodeStore
operator|)
name|source
decl_stmt|;
name|TarNodeStore
name|targetTarNS
init|=
operator|(
name|TarNodeStore
operator|)
name|target
decl_stmt|;
name|NodeState
name|srcSuperRoot
init|=
name|sourceTarNS
operator|.
name|getSuperRoot
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|targetTarNS
operator|.
name|getSuperRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|String
name|previousRoot
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|checkpoint
range|:
name|getCheckpointPaths
argument_list|(
name|srcSuperRoot
argument_list|)
control|)
block|{
comment|// copy the checkpoint without the root
name|NodeStateCopier
operator|.
name|builder
argument_list|()
operator|.
name|include
argument_list|(
name|checkpoint
argument_list|)
operator|.
name|exclude
argument_list|(
name|checkpoint
operator|+
literal|"/root"
argument_list|)
operator|.
name|copy
argument_list|(
name|srcSuperRoot
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// reference the previousRoot or targetRoot as a new checkpoint root
name|NodeState
name|baseRoot
decl_stmt|;
if|if
condition|(
name|previousRoot
operator|==
literal|null
condition|)
block|{
name|baseRoot
operator|=
name|targetRoot
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseRoot
operator|=
name|getBuilder
argument_list|(
name|builder
argument_list|,
name|previousRoot
argument_list|)
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
name|NodeBuilder
name|targetParent
init|=
name|getBuilder
argument_list|(
name|builder
argument_list|,
name|checkpoint
argument_list|)
decl_stmt|;
name|targetParent
operator|.
name|setChildNode
argument_list|(
literal|"root"
argument_list|,
name|baseRoot
argument_list|)
expr_stmt|;
name|previousRoot
operator|=
name|checkpoint
operator|+
literal|"/root"
expr_stmt|;
comment|// apply diff changes
name|NodeStateCopier
operator|.
name|builder
argument_list|()
operator|.
name|include
argument_list|(
name|checkpoint
operator|+
literal|"/root"
argument_list|)
operator|.
name|copy
argument_list|(
name|srcSuperRoot
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|targetTarNS
operator|.
name|setSuperRoot
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Return all checkpoint paths, sorted by their "created" property, descending.      *      * @param superRoot      * @return      */
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getCheckpointPaths
parameter_list|(
name|NodeState
name|superRoot
parameter_list|)
block|{
name|List
argument_list|<
name|ChildNodeEntry
argument_list|>
name|checkpoints
init|=
name|newArrayList
argument_list|(
name|superRoot
operator|.
name|getChildNode
argument_list|(
literal|"checkpoints"
argument_list|)
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
name|sort
argument_list|(
name|checkpoints
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ChildNodeEntry
name|o1
parameter_list|,
name|ChildNodeEntry
name|o2
parameter_list|)
block|{
name|long
name|c1
init|=
name|o1
operator|.
name|getNodeState
argument_list|()
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
name|long
name|c2
init|=
name|o1
operator|.
name|getNodeState
argument_list|()
operator|.
name|getLong
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
return|return
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|c1
argument_list|,
name|c2
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|transform
argument_list|(
name|checkpoints
argument_list|,
operator|new
name|Function
argument_list|<
name|ChildNodeEntry
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|ChildNodeEntry
name|input
parameter_list|)
block|{
return|return
literal|"/checkpoints/"
operator|+
name|input
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|getBuilder
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

