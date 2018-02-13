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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
operator|.
name|newGCGeneration
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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|Collection
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
name|IOUtils
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
name|RecordId
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
name|SegmentNodeStorePersistence
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
name|GCJournal
operator|.
name|GCJournalEntry
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
name|tar
operator|.
name|TarPersistence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|GcJournalTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|segmentFolder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
name|SegmentNodeStorePersistence
name|getPersistence
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|TarPersistence
argument_list|(
name|segmentFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|tarGcJournal
parameter_list|()
throws|throws
name|Exception
block|{
name|GCJournal
name|gc
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|gc
operator|.
name|persist
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|,
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|50
argument_list|,
name|RecordId
operator|.
name|NULL
operator|.
name|toString10
argument_list|()
argument_list|)
expr_stmt|;
name|GCJournalEntry
name|e0
init|=
name|gc
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|e0
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e0
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|e0
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RecordId
operator|.
name|NULL
operator|.
name|toString10
argument_list|()
argument_list|,
name|e0
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|gc
operator|.
name|persist
argument_list|(
literal|0
argument_list|,
literal|250
argument_list|,
name|newGCGeneration
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|75
argument_list|,
name|RecordId
operator|.
name|NULL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|GCJournalEntry
name|e1
init|=
name|gc
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|250
argument_list|,
name|e1
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|e1
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|e1
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RecordId
operator|.
name|NULL
operator|.
name|toString
argument_list|()
argument_list|,
name|e1
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|gc
operator|.
name|persist
argument_list|(
literal|50
argument_list|,
literal|200
argument_list|,
name|newGCGeneration
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|90
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|GCJournalEntry
name|e2
init|=
name|gc
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|e2
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|e2
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|e2
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|e2
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
comment|// same gen
name|gc
operator|.
name|persist
argument_list|(
literal|75
argument_list|,
literal|300
argument_list|,
name|newGCGeneration
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|125
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|GCJournalEntry
name|e3
init|=
name|gc
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|e3
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|e3
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|e3
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|e2
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|GCJournalEntry
argument_list|>
name|all
init|=
name|gc
operator|.
name|readAll
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|all
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|SegmentNodeStorePersistence
operator|.
name|GCJournalFile
name|gcFile
init|=
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|allLines
init|=
name|gcFile
operator|.
name|readLines
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|allLines
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGCGeneration
parameter_list|()
throws|throws
name|Exception
block|{
name|GCJournal
name|out
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|persist
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|,
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|50
argument_list|,
name|RecordId
operator|.
name|NULL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|GCJournal
name|in
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
name|in
operator|.
name|read
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGCGenerationCompactedFlagCleared
parameter_list|()
throws|throws
name|Exception
block|{
name|GCJournal
name|out
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|persist
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|,
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|50
argument_list|,
name|RecordId
operator|.
name|NULL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|GCJournal
name|in
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
argument_list|,
name|in
operator|.
name|read
argument_list|()
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadOak16GCLog
parameter_list|()
throws|throws
name|Exception
block|{
name|createOak16GCLog
argument_list|()
expr_stmt|;
name|GCJournal
name|gcJournal
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|GCJournalEntry
name|entry
init|=
name|gcJournal
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|45919825920L
argument_list|,
name|entry
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|41394306048L
argument_list|,
name|entry
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1493819563098L
argument_list|,
name|entry
operator|.
name|getTs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|,
name|entry
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|entry
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RecordId
operator|.
name|NULL
operator|.
name|toString10
argument_list|()
argument_list|,
name|entry
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateOak16GCLog
parameter_list|()
throws|throws
name|Exception
block|{
name|createOak16GCLog
argument_list|()
expr_stmt|;
name|GCJournal
name|gcJournal
init|=
operator|new
name|GCJournal
argument_list|(
name|getPersistence
argument_list|()
operator|.
name|getGCJournalFile
argument_list|()
argument_list|)
decl_stmt|;
name|gcJournal
operator|.
name|persist
argument_list|(
literal|75
argument_list|,
literal|300
argument_list|,
name|newGCGeneration
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|125
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|GCJournalEntry
argument_list|>
name|entries
init|=
name|newArrayList
argument_list|(
name|gcJournal
operator|.
name|readAll
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|GCJournalEntry
name|entry
init|=
name|entries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|45919825920L
argument_list|,
name|entry
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|41394306048L
argument_list|,
name|entry
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1493819563098L
argument_list|,
name|entry
operator|.
name|getTs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|,
name|entry
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|entry
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RecordId
operator|.
name|NULL
operator|.
name|toString10
argument_list|()
argument_list|,
name|entry
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|entries
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|300
argument_list|,
name|entry
operator|.
name|getRepoSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|75
argument_list|,
name|entry
operator|.
name|getReclaimedSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newGCGeneration
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|,
name|entry
operator|.
name|getGcGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|125
argument_list|,
name|entry
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|entry
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createOak16GCLog
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStream
name|source
init|=
name|GcJournalTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"oak-1.6-gc.log"
argument_list|)
init|)
block|{
try|try
init|(
name|FileOutputStream
name|target
init|=
operator|new
name|FileOutputStream
argument_list|(
name|segmentFolder
operator|.
name|newFile
argument_list|(
literal|"gc.log"
argument_list|)
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

