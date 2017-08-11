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
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
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
name|nio
operator|.
name|file
operator|.
name|Files
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
annotation|@
name|Test
specifier|public
name|void
name|tarGcJournal
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|directory
init|=
name|segmentFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|GCJournal
name|gc
init|=
operator|new
name|GCJournal
argument_list|(
name|directory
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
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|GCJournal
operator|.
name|GC_JOURNAL
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|allLines
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
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
name|segmentFolder
operator|.
name|getRoot
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
name|segmentFolder
operator|.
name|getRoot
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
name|segmentFolder
operator|.
name|getRoot
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
name|segmentFolder
operator|.
name|getRoot
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
block|}
end_class

end_unit

