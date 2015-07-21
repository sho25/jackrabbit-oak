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
name|Comparator
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
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|AbstractIterator
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
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
name|commons
operator|.
name|json
operator|.
name|JsopReader
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
name|JsopTokenizer
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
name|sort
operator|.
name|StringSort
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
name|util
operator|.
name|Utils
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
name|document
operator|.
name|Collection
operator|.
name|JOURNAL
import|;
end_import

begin_comment
comment|/**  * Keeps track of changes performed between two consecutive background updates.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JournalEntry
extends|extends
name|Document
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
name|JournalEntry
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The revision format for external changes:      *&lt;clusterId>-&lt;timestamp>-&lt;counter>. The string is prefixed with      * "b" if it denotes a branch revision.      */
specifier|private
specifier|static
specifier|final
name|String
name|REVISION_FORMAT
init|=
literal|"%d-%0"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|length
argument_list|()
operator|+
literal|"x-%0"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|length
argument_list|()
operator|+
literal|"x"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CHANGES
init|=
literal|"_c"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BRANCH_COMMITS
init|=
literal|"_bc"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MODIFIED
init|=
literal|"_modified"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|READ_CHUNK_SIZE
init|=
literal|100
decl_stmt|;
comment|/**      * switch to disk after 1MB      */
specifier|private
specifier|static
specifier|final
name|int
name|STRING_SORT_OVERFLOW_TO_DISK_THRESHOLD
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|private
specifier|volatile
name|TreeNode
name|changes
init|=
literal|null
decl_stmt|;
name|JournalEntry
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|static
name|StringSort
name|newSorter
parameter_list|()
block|{
return|return
operator|new
name|StringSort
argument_list|(
name|STRING_SORT_OVERFLOW_TO_DISK_THRESHOLD
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|compareTo
argument_list|(
name|arg1
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|static
name|void
name|applyTo
parameter_list|(
annotation|@
name|Nonnull
name|StringSort
name|externalSort
parameter_list|,
annotation|@
name|Nonnull
name|DiffCache
name|diffCache
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"applyTo: starting for {} to {}"
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
comment|// note that it is not de-duplicated yet
name|LOG
operator|.
name|debug
argument_list|(
literal|"applyTo: sorting done."
argument_list|)
expr_stmt|;
specifier|final
name|DiffCache
operator|.
name|Entry
name|entry
init|=
name|checkNotNull
argument_list|(
name|diffCache
argument_list|)
operator|.
name|newEntry
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|externalSort
operator|.
name|getIds
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// nothing at all? that's quite unusual..
comment|// we apply this diff as one '/' to the entry then
name|entry
operator|.
name|append
argument_list|(
literal|"/"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|entry
operator|.
name|done
argument_list|()
expr_stmt|;
return|return;
block|}
name|String
name|previousPath
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|TreeNode
name|node
init|=
operator|new
name|TreeNode
argument_list|()
decl_stmt|;
name|node
operator|=
name|node
operator|.
name|getOrCreatePath
argument_list|(
name|previousPath
argument_list|)
expr_stmt|;
name|int
name|totalCnt
init|=
literal|0
decl_stmt|;
name|int
name|deDuplicatedCnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|totalCnt
operator|++
expr_stmt|;
specifier|final
name|String
name|currentPath
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|previousPath
operator|.
name|equals
argument_list|(
name|currentPath
argument_list|)
condition|)
block|{
comment|// de-duplication
continue|continue;
block|}
specifier|final
name|TreeNode
name|currentNode
init|=
name|node
operator|.
name|getOrCreatePath
argument_list|(
name|currentPath
argument_list|)
decl_stmt|;
comment|// 'node' contains one hierarchy line, eg /a, /a/b, /a/b/c, /a/b/c/d
comment|// including the children on each level.
comment|// these children have not yet been appended to the diffCache entry
comment|// and have to be added as soon as the 'currentPath' is not
comment|// part of that hierarchy anymore and we 'move elsewhere'.
comment|// eg if 'currentPath' is /a/b/e, then we must flush /a/b/c/d and /a/b/c
while|while
condition|(
name|node
operator|!=
literal|null
operator|&&
operator|!
name|node
operator|.
name|isAncestorOf
argument_list|(
name|currentNode
argument_list|)
condition|)
block|{
comment|// add parent to the diff entry
name|entry
operator|.
name|append
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|getChanges
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|deDuplicatedCnt
operator|++
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|parent
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
comment|// we should never go 'passed' the root, hence node should
comment|// never be null - if it becomes null anyway, start with
comment|// a fresh root:
name|node
operator|=
operator|new
name|TreeNode
argument_list|()
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getOrCreatePath
argument_list|(
name|currentPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// this is the normal route: we add a direct or grand-child
comment|// node to the current node:
name|node
operator|=
name|currentNode
expr_stmt|;
block|}
name|previousPath
operator|=
name|currentPath
expr_stmt|;
block|}
comment|// once we're done we still have the last hierarchy line contained in 'node',
comment|// eg /x, /x/y, /x/y/z
comment|// and that one we must now append to the diff cache entry:
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|append
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|getChanges
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|deDuplicatedCnt
operator|++
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|parent
expr_stmt|;
block|}
comment|// and finally: mark the diff cache entry as 'done':
name|entry
operator|.
name|done
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"applyTo: done. totalCnt: {}, deDuplicatedCnt: {}"
argument_list|,
name|totalCnt
argument_list|,
name|deDuplicatedCnt
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reads all external changes between the two given revisions (with the same      * clusterId) from the journal and appends the paths therein to the provided      * sorter.      *      * @param sorter the StringSort to which all externally changed paths      *               between the provided revisions will be added      * @param from   the lower bound of the revision range (exclusive).      * @param to     the upper bound of the revision range (inclusive).      * @param store  the document store to query.      * @throws IOException      */
specifier|static
name|void
name|fillExternalChanges
parameter_list|(
annotation|@
name|Nonnull
name|StringSort
name|sorter
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|from
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|to
parameter_list|,
annotation|@
name|Nonnull
name|DocumentStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|checkArgument
argument_list|(
name|checkNotNull
argument_list|(
name|from
argument_list|)
operator|.
name|getClusterId
argument_list|()
operator|==
name|checkNotNull
argument_list|(
name|to
argument_list|)
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
comment|// to is inclusive, but DocumentStore.query() toKey is exclusive
specifier|final
name|String
name|inclusiveToId
init|=
name|asId
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|to
operator|=
operator|new
name|Revision
argument_list|(
name|to
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|to
operator|.
name|getCounter
argument_list|()
operator|+
literal|1
argument_list|,
name|to
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|to
operator|.
name|isBranch
argument_list|()
argument_list|)
expr_stmt|;
comment|// read in chunks to support very large sets of changes between
comment|// subsequent background reads to do this, provide a (TODO eventually configurable)
comment|// limit for the number of entries to be returned per query if the
comment|// number of elements returned by the query is exactly the provided
comment|// limit, then loop and do subsequent queries
specifier|final
name|String
name|toId
init|=
name|asId
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|String
name|fromId
init|=
name|asId
argument_list|(
name|from
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|fromId
operator|.
name|equals
argument_list|(
name|inclusiveToId
argument_list|)
condition|)
block|{
comment|// avoid query if from and to are off by just 1 counter (which
comment|// we do due to exclusiveness of query borders) as in this case
comment|// the query will always be empty anyway - so avoid doing the
comment|// query in the first place
break|break;
block|}
name|List
argument_list|<
name|JournalEntry
argument_list|>
name|partialResult
init|=
name|store
operator|.
name|query
argument_list|(
name|JOURNAL
argument_list|,
name|fromId
argument_list|,
name|toId
argument_list|,
name|READ_CHUNK_SIZE
argument_list|)
decl_stmt|;
for|for
control|(
name|JournalEntry
name|d
range|:
name|partialResult
control|)
block|{
name|d
operator|.
name|addTo
argument_list|(
name|sorter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|partialResult
operator|.
name|size
argument_list|()
operator|<
name|READ_CHUNK_SIZE
condition|)
block|{
break|break;
block|}
comment|// otherwise set 'fromId' to the last entry just processed
comment|// that works fine as the query is non-inclusive (ie does not
comment|// include the from which we'd otherwise double-process)
name|fromId
operator|=
name|partialResult
operator|.
name|get
argument_list|(
name|partialResult
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|getRevisionTimestamp
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|parts
init|=
name|getId
argument_list|()
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|,
literal|16
argument_list|)
return|;
block|}
name|void
name|modified
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeNode
name|node
init|=
name|getChanges
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getOrCreate
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|modified
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|modified
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|branchCommit
parameter_list|(
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|Revision
argument_list|>
name|revisions
parameter_list|)
block|{
name|String
name|branchCommits
init|=
operator|(
name|String
operator|)
name|get
argument_list|(
name|BRANCH_COMMITS
argument_list|)
decl_stmt|;
if|if
condition|(
name|branchCommits
operator|==
literal|null
condition|)
block|{
name|branchCommits
operator|=
literal|""
expr_stmt|;
block|}
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
name|branchCommits
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|branchCommits
operator|+=
literal|","
expr_stmt|;
block|}
name|branchCommits
operator|+=
name|asId
argument_list|(
name|r
operator|.
name|asBranchRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|put
argument_list|(
name|BRANCH_COMMITS
argument_list|,
name|branchCommits
argument_list|)
expr_stmt|;
block|}
name|String
name|getChanges
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeNode
name|node
init|=
name|getNode
argument_list|(
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
return|return
literal|""
return|;
block|}
return|return
name|getChanges
argument_list|(
name|node
argument_list|)
return|;
block|}
name|UpdateOp
name|asUpdateOp
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|String
name|id
init|=
name|asId
argument_list|(
name|revision
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|op
operator|.
name|set
argument_list|(
name|ID
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|op
operator|.
name|set
argument_list|(
name|CHANGES
argument_list|,
name|getChanges
argument_list|()
operator|.
name|serialize
argument_list|()
argument_list|)
expr_stmt|;
comment|// OAK-3085 : introduce a timestamp property
comment|// for later being used by OAK-3001
name|op
operator|.
name|set
argument_list|(
name|MODIFIED
argument_list|,
name|revision
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|bc
init|=
operator|(
name|String
operator|)
name|get
argument_list|(
name|BRANCH_COMMITS
argument_list|)
decl_stmt|;
if|if
condition|(
name|bc
operator|!=
literal|null
condition|)
block|{
name|op
operator|.
name|set
argument_list|(
name|BRANCH_COMMITS
argument_list|,
name|bc
argument_list|)
expr_stmt|;
block|}
return|return
name|op
return|;
block|}
name|void
name|addTo
parameter_list|(
specifier|final
name|StringSort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeNode
name|n
init|=
name|getChanges
argument_list|()
decl_stmt|;
name|TraversingVisitor
name|v
init|=
operator|new
name|TraversingVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|node
parameter_list|(
name|TreeNode
name|node
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|sort
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|n
operator|.
name|accept
argument_list|(
name|v
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|JournalEntry
name|e
range|:
name|getBranchCommits
argument_list|()
control|)
block|{
name|e
operator|.
name|getChanges
argument_list|()
operator|.
name|accept
argument_list|(
name|v
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the branch commits that are related to this journal entry.      *      * @return the branch commits.      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|JournalEntry
argument_list|>
name|getBranchCommits
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|String
name|bc
init|=
operator|(
name|String
operator|)
name|get
argument_list|(
name|BRANCH_COMMITS
argument_list|)
decl_stmt|;
if|if
condition|(
name|bc
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|bc
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Iterable
argument_list|<
name|JournalEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|JournalEntry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|AbstractIterator
argument_list|<
name|JournalEntry
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|ids
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|JournalEntry
name|computeNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
name|String
name|id
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|JournalEntry
name|d
init|=
name|store
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing external change for branch revision: "
operator|+
name|id
argument_list|)
throw|;
block|}
return|return
name|d
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|//-----------------------------< internal>---------------------------------
specifier|private
specifier|static
name|String
name|getChanges
parameter_list|(
name|TreeNode
name|node
parameter_list|)
block|{
name|JsopBuilder
name|builder
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|node
operator|.
name|keySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|key
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|object
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
name|String
name|asId
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|String
operator|.
name|format
argument_list|(
name|REVISION_FORMAT
argument_list|,
name|revision
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|revision
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|revision
operator|.
name|getCounter
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|revision
operator|.
name|isBranch
argument_list|()
condition|)
block|{
name|s
operator|=
literal|"b"
operator|+
name|s
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|TreeNode
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeNode
name|node
init|=
name|getChanges
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|TreeNode
name|getChanges
parameter_list|()
block|{
if|if
condition|(
name|changes
operator|==
literal|null
condition|)
block|{
name|TreeNode
name|node
init|=
operator|new
name|TreeNode
argument_list|()
decl_stmt|;
name|String
name|c
init|=
operator|(
name|String
operator|)
name|get
argument_list|(
name|CHANGES
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|parse
argument_list|(
operator|new
name|JsopTokenizer
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|changes
operator|=
name|node
expr_stmt|;
block|}
return|return
name|changes
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TreeNode
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TreeNode
argument_list|>
name|NO_CHILDREN
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|TreeNode
argument_list|>
name|children
init|=
name|NO_CHILDREN
decl_stmt|;
specifier|private
specifier|final
name|TreeNode
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|TreeNode
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|TreeNode
parameter_list|(
name|TreeNode
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|name
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"name must not contain '/': {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
name|TreeNode
name|getOrCreatePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|TreeNode
name|n
init|=
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|n
operator|=
name|n
operator|.
name|getOrCreate
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
name|boolean
name|isAncestorOf
parameter_list|(
name|TreeNode
name|other
parameter_list|)
block|{
name|TreeNode
name|n
init|=
name|other
decl_stmt|;
while|while
condition|(
name|n
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|==
name|n
operator|.
name|parent
condition|)
block|{
return|return
literal|true
return|;
block|}
name|n
operator|=
name|n
operator|.
name|parent
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|TreeNode
name|getRoot
parameter_list|()
block|{
name|TreeNode
name|n
init|=
name|this
decl_stmt|;
while|while
condition|(
name|n
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
specifier|private
name|String
name|getPath
parameter_list|()
block|{
return|return
name|buildPath
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|StringBuilder
name|buildPath
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
comment|// only add slash if parent is not the root
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// this is the root
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|sb
return|;
block|}
name|void
name|parse
parameter_list|(
name|JsopReader
name|reader
parameter_list|)
block|{
name|reader
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|reader
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|String
name|name
init|=
name|Utils
operator|.
name|unescapePropertyName
argument_list|(
name|reader
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|getOrCreate
argument_list|(
name|name
argument_list|)
operator|.
name|parse
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|serialize
parameter_list|()
block|{
name|JsopBuilder
name|builder
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|object
argument_list|()
expr_stmt|;
name|toJson
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|children
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
name|TreeNode
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|children
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
name|void
name|accept
parameter_list|(
name|TraversingVisitor
name|visitor
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|node
argument_list|(
name|this
argument_list|,
name|path
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|TreeNode
argument_list|>
name|entry
range|:
name|children
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|accept
argument_list|(
name|visitor
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|toJson
parameter_list|(
name|JsopBuilder
name|builder
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|TreeNode
argument_list|>
name|entry
range|:
name|children
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|key
argument_list|(
name|Utils
operator|.
name|escapePropertyName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|object
argument_list|()
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toJson
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|TreeNode
name|getOrCreate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|children
operator|==
name|NO_CHILDREN
condition|)
block|{
name|children
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|TreeNode
name|c
init|=
name|children
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
operator|new
name|TreeNode
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|children
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
specifier|private
interface|interface
name|TraversingVisitor
block|{
name|void
name|node
parameter_list|(
name|TreeNode
name|node
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

