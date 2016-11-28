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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|Sets
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
name|memory
operator|.
name|MemoryDocumentStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests for {@link JournalEntry}.  */
end_comment

begin_class
specifier|public
class|class
name|JournalEntryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|applyTo
parameter_list|()
throws|throws
name|Exception
block|{
name|DiffCache
name|cache
init|=
operator|new
name|MemoryDiffCache
argument_list|(
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|addRandomPaths
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|sort
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|RevisionVector
name|from
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|to
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|sort
operator|.
name|sort
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|applyTo
argument_list|(
name|sort
argument_list|,
name|cache
argument_list|,
literal|"/"
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|String
name|changes
init|=
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"missing changes for "
operator|+
name|p
argument_list|,
name|changes
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|getChildren
argument_list|(
name|changes
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|p
argument_list|,
name|c
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|applyToWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|DiffCache
name|cache
init|=
operator|new
name|MemoryDiffCache
argument_list|(
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|)
decl_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/foo/a"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/foo/b"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/bar"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/bar/a"
argument_list|)
expr_stmt|;
name|sort
operator|.
name|add
argument_list|(
literal|"/bar/b"
argument_list|)
expr_stmt|;
name|RevisionVector
name|from
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|to
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|sort
operator|.
name|sort
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|applyTo
argument_list|(
name|sort
argument_list|,
name|cache
argument_list|,
literal|"/foo"
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"/foo"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"/foo/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"/foo/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"/bar"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"/bar/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"/bar/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//OAK-3494
annotation|@
name|Test
specifier|public
name|void
name|useParentDiff
parameter_list|()
throws|throws
name|Exception
block|{
name|DiffCache
name|cache
init|=
operator|new
name|MemoryDiffCache
argument_list|(
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|)
decl_stmt|;
name|RevisionVector
name|from
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|to
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|unjournalled
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|//Put one entry for (from, to, "/a/b")->["c1", "c2"] manually
name|DiffCache
operator|.
name|Entry
name|entry
init|=
name|cache
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
name|entry
operator|.
name|append
argument_list|(
literal|"/a/b"
argument_list|,
literal|"^\"c1\":{}^\"c2\":{}"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|done
argument_list|()
expr_stmt|;
comment|//NOTE: calling validateCacheUsage fills the cache with an empty diff for the path being validated.
comment|//So, we need to make sure that each validation is done on a separate path.
comment|//Cases that cache can answer (only c1 and c2 sub-trees are possibly changed)
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/a/b/c3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/a/b/c4/e/f/g"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Cases that cache can't answer
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/a/b/c1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//cached entry says that c1 sub-tree is changed
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/a/b/c2/d"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//cached entry says that c2 sub-tree is changed
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/c"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//there is no cache entry for the whole hierarchy
comment|//Fill cache using journal
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/content/changed"
argument_list|,
literal|"/content/changed1/child1"
argument_list|)
decl_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|sort
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|sort
operator|.
name|sort
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|applyTo
argument_list|(
name|sort
argument_list|,
name|cache
argument_list|,
literal|"/"
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/topUnchanged"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/content/changed/unchangedLeaf"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
literal|"/content/changed1/child2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//check against an unjournalled revision (essentially empty cache)
name|validateCacheUsage
argument_list|(
name|cache
argument_list|,
name|from
argument_list|,
name|unjournalled
argument_list|,
literal|"/unjournalledPath"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fillExternalChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|JournalEntry
name|entry
init|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|addRandomPaths
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|Revision
name|r1
init|=
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|r2
init|=
operator|new
name|Revision
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|r3
init|=
operator|new
name|Revision
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
name|entry
operator|.
name|asUpdateOp
argument_list|(
name|r2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|create
argument_list|(
name|JOURNAL
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r2
argument_list|,
name|r3
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r1
argument_list|,
name|r2
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r1
argument_list|,
name|r3
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fillExternalChanges2
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
name|r1
init|=
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|r2
init|=
operator|new
name|Revision
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|r3
init|=
operator|new
name|Revision
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|r4
init|=
operator|new
name|Revision
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|JournalEntry
name|entry
init|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
init|=
name|entry
operator|.
name|asUpdateOp
argument_list|(
name|r2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|create
argument_list|(
name|JOURNAL
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar"
argument_list|)
expr_stmt|;
name|op
operator|=
name|entry
operator|.
name|asUpdateOp
argument_list|(
name|r4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|create
argument_list|(
name|JOURNAL
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r1
argument_list|,
name|r1
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r1
argument_list|,
name|r2
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/foo"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r1
argument_list|,
name|r3
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/foo"
argument_list|,
literal|"/bar"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r1
argument_list|,
name|r4
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/foo"
argument_list|,
literal|"/bar"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r2
argument_list|,
name|r2
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r2
argument_list|,
name|r3
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/bar"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r2
argument_list|,
name|r4
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/bar"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r3
argument_list|,
name|r3
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r3
argument_list|,
name|r4
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/bar"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
name|sort
operator|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
expr_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
name|r4
argument_list|,
name|r4
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fillExternalChangesWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
name|r1
init|=
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|r2
init|=
operator|new
name|Revision
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|JournalEntry
name|entry
init|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo/a"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo/b"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo/c"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar/a"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar/b"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar/c"
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
init|=
name|entry
operator|.
name|asUpdateOp
argument_list|(
name|r2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|create
argument_list|(
name|JOURNAL
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|op
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|JournalEntry
operator|.
name|fillExternalChanges
argument_list|(
name|sort
argument_list|,
literal|"/foo"
argument_list|,
name|r1
argument_list|,
name|r2
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getRevisionTimestamp
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|JournalEntry
name|entry
init|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|.
name|create
argument_list|(
name|JOURNAL
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|entry
operator|.
name|asUpdateOp
argument_list|(
name|r
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|entry
operator|=
name|store
operator|.
name|find
argument_list|(
name|JOURNAL
argument_list|,
name|JournalEntry
operator|.
name|asId
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|entry
operator|.
name|getRevisionTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// OAK-4682
annotation|@
name|Test
specifier|public
name|void
name|concurrentModification
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|store
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|JournalEntry
name|entry
init|=
name|store
operator|.
name|getCurrentJournalEntry
argument_list|()
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|entry
operator|.
name|modified
argument_list|(
literal|"/node-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
try|try
block|{
name|entry
operator|.
name|addTo
argument_list|(
name|sort
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addToWithPath
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|JournalEntry
name|entry
init|=
name|JOURNAL
operator|.
name|newDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo/a"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo/b"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/foo/c"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar/a"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar/b"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|modified
argument_list|(
literal|"/bar/c"
argument_list|)
expr_stmt|;
name|StringSort
name|sort
init|=
name|JournalEntry
operator|.
name|newSorter
argument_list|()
decl_stmt|;
name|entry
operator|.
name|addTo
argument_list|(
name|sort
argument_list|,
literal|"/foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|sort
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addRandomPaths
parameter_list|(
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
literal|42
argument_list|)
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|int
name|depth
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|depth
condition|;
name|j
operator|++
control|)
block|{
name|char
name|name
init|=
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
argument_list|)
decl_stmt|;
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|StringSort
name|sort
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|sort
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getChildren
parameter_list|(
name|String
name|diff
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|diff
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|r
init|=
name|t
operator|.
name|read
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|r
condition|)
block|{
case|case
literal|'^'
case|:
block|{
name|children
operator|.
name|add
argument_list|(
name|t
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|JsopReader
operator|.
name|END
case|:
block|{
return|return
name|children
return|;
block|}
default|default:
name|fail
argument_list|(
literal|"Unexpected token: "
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|validateCacheUsage
parameter_list|(
name|DiffCache
name|cache
parameter_list|,
name|RevisionVector
name|from
parameter_list|,
name|RevisionVector
name|to
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|cacheExpected
parameter_list|)
block|{
name|String
name|nonLoaderDiff
init|=
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|loaderCalled
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|cache
operator|.
name|getChanges
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|path
argument_list|,
operator|new
name|DiffCache
operator|.
name|Loader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
block|{
name|loaderCalled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|cacheExpected
condition|)
block|{
name|assertNotNull
argument_list|(
name|nonLoaderDiff
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|loaderCalled
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|nonLoaderDiff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|loaderCalled
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

