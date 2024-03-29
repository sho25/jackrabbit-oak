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
name|segment
operator|.
name|file
operator|.
name|tooling
package|;
end_package

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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|api
operator|.
name|Type
operator|.
name|BINARY
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
name|concat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Blob
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
name|segment
operator|.
name|SegmentBlob
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|SegmentNotFoundException
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
name|segment
operator|.
name|file
operator|.
name|JournalEntry
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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
name|NodeStateUtils
import|;
end_import

begin_class
specifier|public
class|class
name|ConsistencyChecker
block|{
specifier|private
specifier|static
name|NodeState
name|getDescendantOrNull
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeState
name|descendant
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|descendant
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|descendant
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
class|class
name|PathToCheck
block|{
specifier|final
name|String
name|path
decl_stmt|;
name|JournalEntry
name|journalEntry
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|corruptPaths
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|PathToCheck
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|onCheckRevision
parameter_list|(
name|String
name|revision
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckHead
parameter_list|()
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckChekpoints
parameter_list|()
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckCheckpoint
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckpointNotFoundInRevision
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckRevisionError
parameter_list|(
name|String
name|revision
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onConsistentPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onPathNotFound
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckTree
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|head
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckTreeEnd
parameter_list|(
name|boolean
name|head
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckProperty
parameter_list|()
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckPropertyEnd
parameter_list|(
name|String
name|path
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckNodeError
parameter_list|(
name|String
name|path
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|protected
name|void
name|onCheckTreeError
parameter_list|(
name|String
name|path
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
comment|// Do nothing.
block|}
specifier|public
specifier|static
class|class
name|Revision
block|{
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
specifier|private
name|Revision
parameter_list|(
name|String
name|revision
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
specifier|public
name|String
name|getRevision
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|ConsistencyCheckResult
block|{
specifier|private
name|ConsistencyCheckResult
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|headRevisions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|>
name|checkpointRevisions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Revision
name|overallRevision
decl_stmt|;
specifier|private
name|int
name|checkedRevisionsCount
decl_stmt|;
specifier|public
name|int
name|getCheckedRevisionsCount
parameter_list|()
block|{
return|return
name|checkedRevisionsCount
return|;
block|}
specifier|public
name|Revision
name|getOverallRevision
parameter_list|()
block|{
return|return
name|overallRevision
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|getHeadRevisions
parameter_list|()
block|{
return|return
name|headRevisions
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|>
name|getCheckpointRevisions
parameter_list|()
block|{
return|return
name|checkpointRevisions
return|;
block|}
block|}
specifier|private
name|boolean
name|isPathInvalid
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|getDescendantOrNull
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|onPathNotFound
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
name|checkNode
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
name|binaries
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|private
name|String
name|findFirstCorruptedPathInSet
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|corruptedPaths
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
for|for
control|(
name|String
name|corruptedPath
range|:
name|corruptedPaths
control|)
block|{
if|if
condition|(
name|isPathInvalid
argument_list|(
name|root
argument_list|,
name|corruptedPath
argument_list|,
name|binaries
argument_list|)
condition|)
block|{
return|return
name|corruptedPath
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|findFirstCorruptedPathInTree
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|getDescendantOrNull
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|onPathNotFound
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
return|return
name|checkNodeAndDescendants
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
name|binaries
argument_list|)
return|;
block|}
specifier|private
name|String
name|checkTreeConsistency
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|corruptedPaths
parameter_list|,
name|boolean
name|binaries
parameter_list|,
name|boolean
name|head
parameter_list|)
block|{
name|String
name|corruptedPath
init|=
name|findFirstCorruptedPathInSet
argument_list|(
name|root
argument_list|,
name|corruptedPaths
argument_list|,
name|binaries
argument_list|)
decl_stmt|;
if|if
condition|(
name|corruptedPath
operator|!=
literal|null
condition|)
block|{
return|return
name|corruptedPath
return|;
block|}
name|onCheckTree
argument_list|(
name|path
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|corruptedPath
operator|=
name|findFirstCorruptedPathInTree
argument_list|(
name|root
argument_list|,
name|path
argument_list|,
name|binaries
argument_list|)
expr_stmt|;
name|onCheckTreeEnd
argument_list|(
name|head
argument_list|)
expr_stmt|;
return|return
name|corruptedPath
return|;
block|}
specifier|private
name|boolean
name|checkPathConsistency
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|PathToCheck
name|ptc
parameter_list|,
name|JournalEntry
name|entry
parameter_list|,
name|boolean
name|binaries
parameter_list|,
name|boolean
name|head
parameter_list|)
block|{
if|if
condition|(
name|ptc
operator|.
name|journalEntry
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|corruptPath
init|=
name|checkTreeConsistency
argument_list|(
name|root
argument_list|,
name|ptc
operator|.
name|path
argument_list|,
name|ptc
operator|.
name|corruptPaths
argument_list|,
name|binaries
argument_list|,
name|head
argument_list|)
decl_stmt|;
if|if
condition|(
name|corruptPath
operator|!=
literal|null
condition|)
block|{
name|ptc
operator|.
name|corruptPaths
operator|.
name|add
argument_list|(
name|corruptPath
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|onConsistentPath
argument_list|(
name|ptc
operator|.
name|path
argument_list|)
expr_stmt|;
name|ptc
operator|.
name|journalEntry
operator|=
name|entry
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|checkAllPathsConsistency
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
name|paths
parameter_list|,
name|JournalEntry
name|entry
parameter_list|,
name|boolean
name|binaries
parameter_list|,
name|boolean
name|head
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|PathToCheck
name|ptc
range|:
name|paths
control|)
block|{
if|if
condition|(
operator|!
name|checkPathConsistency
argument_list|(
name|root
argument_list|,
name|ptc
argument_list|,
name|entry
argument_list|,
name|binaries
argument_list|,
name|head
argument_list|)
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|checkHeadConsistency
parameter_list|(
name|SegmentNodeStore
name|store
parameter_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
name|paths
parameter_list|,
name|JournalEntry
name|entry
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
name|boolean
name|allConsistent
init|=
name|paths
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|p
lambda|->
name|p
operator|.
name|journalEntry
operator|!=
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|allConsistent
condition|)
block|{
return|return
literal|true
return|;
block|}
name|onCheckHead
argument_list|()
expr_stmt|;
return|return
name|checkAllPathsConsistency
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|paths
argument_list|,
name|entry
argument_list|,
name|binaries
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|checkCheckpointConsistency
parameter_list|(
name|SegmentNodeStore
name|store
parameter_list|,
name|String
name|checkpoint
parameter_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
name|paths
parameter_list|,
name|JournalEntry
name|entry
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
name|boolean
name|allConsistent
init|=
name|paths
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|p
lambda|->
name|p
operator|.
name|journalEntry
operator|!=
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|allConsistent
condition|)
block|{
return|return
literal|true
return|;
block|}
name|onCheckCheckpoint
argument_list|(
name|checkpoint
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|retrieve
argument_list|(
name|checkpoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|onCheckpointNotFoundInRevision
argument_list|(
name|checkpoint
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|checkAllPathsConsistency
argument_list|(
name|root
argument_list|,
name|paths
argument_list|,
name|entry
argument_list|,
name|binaries
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|checkCheckpointsConsistency
parameter_list|(
name|SegmentNodeStore
name|store
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
argument_list|>
name|paths
parameter_list|,
name|JournalEntry
name|entry
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
argument_list|>
name|e
range|:
name|paths
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|checkCheckpointConsistency
argument_list|(
name|store
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
argument_list|,
name|binaries
argument_list|)
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|allPathsConsistent
parameter_list|(
name|List
argument_list|<
name|PathToCheck
argument_list|>
name|headPaths
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
argument_list|>
name|checkpointPaths
parameter_list|)
block|{
for|for
control|(
name|PathToCheck
name|path
range|:
name|headPaths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|journalEntry
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
argument_list|>
name|e
range|:
name|checkpointPaths
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|PathToCheck
name|path
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|path
operator|.
name|journalEntry
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|shouldCheckCheckpointsConsistency
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
argument_list|>
name|paths
parameter_list|)
block|{
return|return
name|paths
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|List
operator|::
name|stream
argument_list|)
operator|.
name|anyMatch
argument_list|(
name|p
lambda|->
name|p
operator|.
name|journalEntry
operator|==
literal|null
argument_list|)
return|;
block|}
comment|/**      * Check the consistency of a given subtree and returns the first      * inconsistent path. If provided, this method probes a set of inconsistent      * paths before performing a full traversal of the subtree.      *      * @param root           The root node of the subtree.      * @param corruptedPaths A set of possibly inconsistent paths.      * @param binaries       Whether to check binaries for consistency.      * @return The first inconsistent path or {@code null}. The path might be      * either one of the provided inconsistent paths or a new one discovered      * during a full traversal of the tree.      */
specifier|public
name|String
name|checkTreeConsistency
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|corruptedPaths
parameter_list|,
name|boolean
name|binaries
parameter_list|)
block|{
return|return
name|checkTreeConsistency
argument_list|(
name|root
argument_list|,
literal|"/"
argument_list|,
name|corruptedPaths
argument_list|,
name|binaries
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|ConsistencyCheckResult
name|checkConsistency
parameter_list|(
name|ReadOnlyFileStore
name|store
parameter_list|,
name|Iterator
argument_list|<
name|JournalEntry
argument_list|>
name|journal
parameter_list|,
name|boolean
name|head
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|checkpoints
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|boolean
name|binaries
parameter_list|,
name|Integer
name|revisionsCount
parameter_list|)
block|{
name|List
argument_list|<
name|PathToCheck
argument_list|>
name|headPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PathToCheck
argument_list|>
argument_list|>
name|checkpointPaths
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|checkedRevisionsCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|head
condition|)
block|{
name|headPaths
operator|.
name|add
argument_list|(
operator|new
name|PathToCheck
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|checkpoint
range|:
name|checkpoints
control|)
block|{
name|checkpointPaths
operator|.
name|computeIfAbsent
argument_list|(
name|checkpoint
argument_list|,
name|k
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|PathToCheck
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|JournalEntry
name|lastValidJournalEntry
init|=
literal|null
decl_stmt|;
name|SegmentNodeStore
name|sns
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
while|while
condition|(
name|journal
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|JournalEntry
name|journalEntry
init|=
name|journal
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|revision
init|=
name|journalEntry
operator|.
name|getRevision
argument_list|()
decl_stmt|;
try|try
block|{
name|checkedRevisionsCount
operator|++
expr_stmt|;
name|store
operator|.
name|setRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|onCheckRevision
argument_list|(
name|revision
argument_list|)
expr_stmt|;
comment|// Check the consistency of both the head and the checkpoints.
comment|// If both are consistent, the current journal entry is the
comment|// overall valid entry.
name|boolean
name|overall
init|=
name|checkHeadConsistency
argument_list|(
name|sns
argument_list|,
name|headPaths
argument_list|,
name|journalEntry
argument_list|,
name|binaries
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldCheckCheckpointsConsistency
argument_list|(
name|checkpointPaths
argument_list|)
condition|)
block|{
name|onCheckChekpoints
argument_list|()
expr_stmt|;
name|overall
operator|=
name|overall
operator|&&
name|checkCheckpointsConsistency
argument_list|(
name|sns
argument_list|,
name|checkpointPaths
argument_list|,
name|journalEntry
argument_list|,
name|binaries
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|overall
condition|)
block|{
name|lastValidJournalEntry
operator|=
name|journalEntry
expr_stmt|;
block|}
comment|// If every PathToCheck is assigned to a JournalEntry, stop
comment|// looping through the journal.
if|if
condition|(
name|allPathsConsistent
argument_list|(
name|headPaths
argument_list|,
name|checkpointPaths
argument_list|)
condition|)
block|{
break|break;
block|}
comment|// limit the number of revisions to be checked
if|if
condition|(
name|checkedRevisionsCount
operator|==
name|revisionsCount
condition|)
block|{
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|SegmentNotFoundException
name|e
parameter_list|)
block|{
name|onCheckRevisionError
argument_list|(
name|revision
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|ConsistencyCheckResult
name|result
init|=
operator|new
name|ConsistencyCheckResult
argument_list|()
decl_stmt|;
name|result
operator|.
name|checkedRevisionsCount
operator|=
name|checkedRevisionsCount
expr_stmt|;
name|result
operator|.
name|overallRevision
operator|=
name|newRevisionOrNull
argument_list|(
name|lastValidJournalEntry
argument_list|)
expr_stmt|;
for|for
control|(
name|PathToCheck
name|path
range|:
name|headPaths
control|)
block|{
name|result
operator|.
name|headRevisions
operator|.
name|put
argument_list|(
name|path
operator|.
name|path
argument_list|,
name|newRevisionOrNull
argument_list|(
name|path
operator|.
name|journalEntry
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|checkpoint
range|:
name|checkpoints
control|)
block|{
for|for
control|(
name|PathToCheck
name|path
range|:
name|checkpointPaths
operator|.
name|get
argument_list|(
name|checkpoint
argument_list|)
control|)
block|{
name|result
operator|.
name|checkpointRevisions
operator|.
name|computeIfAbsent
argument_list|(
name|checkpoint
argument_list|,
name|s
lambda|->
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|path
operator|.
name|path
argument_list|,
name|newRevisionOrNull
argument_list|(
name|path
operator|.
name|journalEntry
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|Revision
name|newRevisionOrNull
parameter_list|(
name|JournalEntry
name|entry
parameter_list|)
block|{
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Revision
argument_list|(
name|entry
operator|.
name|getRevision
argument_list|()
argument_list|,
name|entry
operator|.
name|getTimestamp
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Checks the consistency of a node and its properties at the given path.      *      * @param node          node to be checked      * @param path          path of the node      * @param checkBinaries if {@code true} full content of binary properties      *                      will be scanned      * @return {@code null}, if the node is consistent, or the path of the first      * inconsistency otherwise.      */
specifier|private
name|String
name|checkNode
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
block|{
try|try
block|{
name|onCheckNode
argument_list|(
name|path
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|propertyState
range|:
name|node
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|propertyState
operator|.
name|getType
argument_list|()
decl_stmt|;
name|boolean
name|checked
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|checked
operator|=
name|traverse
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|(
name|BINARY
argument_list|)
argument_list|,
name|checkBinaries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|BINARIES
condition|)
block|{
for|for
control|(
name|Blob
name|blob
range|:
name|propertyState
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|checked
operator|=
name|checked
operator||
name|traverse
argument_list|(
name|blob
argument_list|,
name|checkBinaries
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|propertyState
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|onCheckProperty
argument_list|()
expr_stmt|;
name|checked
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|checked
condition|)
block|{
name|onCheckPropertyEnd
argument_list|(
name|path
argument_list|,
name|propertyState
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|onCheckNodeError
argument_list|(
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
block|}
comment|/**      * Recursively checks the consistency of a node and its descendants at the      * given path.      *      * @param node          node to be checked      * @param path          path of the node      * @param checkBinaries if {@code true} full content of binary properties      *                      will be scanned      * @return {@code null}, if the node is consistent, or the path of the first      * inconsistency otherwise.      */
specifier|private
name|String
name|checkNodeAndDescendants
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
block|{
name|String
name|result
init|=
name|checkNode
argument_list|(
name|node
argument_list|,
name|path
argument_list|,
name|checkBinaries
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
try|try
block|{
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|String
name|childName
init|=
name|cne
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|child
init|=
name|cne
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|result
operator|=
name|checkNodeAndDescendants
argument_list|(
name|child
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
argument_list|,
name|checkBinaries
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|onCheckTreeError
argument_list|(
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
block|}
specifier|private
name|boolean
name|traverse
parameter_list|(
name|Blob
name|blob
parameter_list|,
name|boolean
name|checkBinaries
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|checkBinaries
operator|&&
operator|!
name|isExternal
argument_list|(
name|blob
argument_list|)
condition|)
block|{
try|try
init|(
name|InputStream
name|s
init|=
name|blob
operator|.
name|getNewStream
argument_list|()
init|)
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
name|int
name|l
init|=
name|s
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|l
operator|>=
literal|0
condition|)
block|{
name|l
operator|=
name|s
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
name|onCheckProperty
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isExternal
parameter_list|(
name|Blob
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|instanceof
name|SegmentBlob
condition|)
block|{
return|return
operator|(
operator|(
name|SegmentBlob
operator|)
name|b
operator|)
operator|.
name|isExternal
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

