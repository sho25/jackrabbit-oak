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
name|blob
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
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|ByteArrayInputStream
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
name|FileInputStream
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
name|Map
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
name|UUID
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
name|TimeUnit
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
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
name|core
operator|.
name|data
operator|.
name|DataIdentifier
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
name|core
operator|.
name|data
operator|.
name|DataRecord
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
name|core
operator|.
name|data
operator|.
name|DataStoreException
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
name|FileIOUtils
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|blob
operator|.
name|datastore
operator|.
name|SharedDataStoreUtils
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
name|blob
operator|.
name|datastore
operator|.
name|SharedDataStoreUtils
operator|.
name|SharedStoreRecordType
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
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

begin_comment
comment|/**  * Test for SharedDataUtils to test addition, retrieval and deletion of root records.  */
end_comment

begin_class
specifier|public
class|class
name|SharedDataStoreUtilsTest
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
name|SharedDataStoreUtilsTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
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
name|DataStoreBlobStore
name|dataStore
decl_stmt|;
specifier|protected
name|DataStoreBlobStore
name|getBlobStore
parameter_list|(
name|File
name|root
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|DataStoreUtils
operator|.
name|getBlobStore
argument_list|(
name|root
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|rootFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|rootFolder
argument_list|)
expr_stmt|;
name|String
name|repoId1
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|repoId2
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Add repository records
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
expr_stmt|;
name|DataRecord
name|repo1
init|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
decl_stmt|;
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId2
argument_list|)
argument_list|)
expr_stmt|;
name|DataRecord
name|repo2
init|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getNameFromId
argument_list|(
name|repoId2
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add reference marker record for repo1
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|SharedStoreRecordType
operator|.
name|MARKED_START_MARKER
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
expr_stmt|;
name|DataRecord
name|markerRec1
init|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|MARKED_START_MARKER
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SharedStoreRecordType
operator|.
name|MARKED_START_MARKER
operator|.
name|getIdFromName
argument_list|(
name|markerRec1
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|repoId1
argument_list|)
expr_stmt|;
name|long
name|lastModifiedMarkerRec1
init|=
name|markerRec1
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// Add reference records
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
expr_stmt|;
name|DataRecord
name|rec1
init|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId1
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|lastModifiedRec1
init|=
name|rec1
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|25
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId2
argument_list|)
argument_list|)
expr_stmt|;
name|DataRecord
name|rec2
init|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId2
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|lastModifiedRec2
init|=
name|rec2
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getIdFromName
argument_list|(
name|repo1
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|repoId1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getIdFromName
argument_list|(
name|repo2
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|repoId2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getIdFromName
argument_list|(
name|rec1
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|repoId1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getIdFromName
argument_list|(
name|rec2
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|repoId2
argument_list|)
expr_stmt|;
comment|// All the references from registered repositories are available
name|Assert
operator|.
name|assertTrue
argument_list|(
name|SharedDataStoreUtils
operator|.
name|refsNotAvailableFromRepos
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Since, we don't care about which file specifically but only the earliest timestamped record
comment|// Earliest time should be the min timestamp from the 2 reference files
name|long
name|minRefTime
init|=
operator|(
name|lastModifiedRec1
operator|<=
name|lastModifiedRec2
condition|?
name|lastModifiedRec1
else|:
name|lastModifiedRec2
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SharedDataStoreUtils
operator|.
name|getEarliestRecord
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getLastModified
argument_list|()
argument_list|,
name|minRefTime
argument_list|)
expr_stmt|;
comment|// the marker timestamp should be the minimum
name|long
name|minMarkerTime
init|=
name|SharedDataStoreUtils
operator|.
name|getEarliestRecord
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|MARKED_START_MARKER
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|minRefTime
operator|>=
name|minMarkerTime
argument_list|)
expr_stmt|;
comment|// Delete references and check back if deleted
name|dataStore
operator|.
name|deleteAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Delete markers and check back if deleted
name|dataStore
operator|.
name|deleteAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|MARKED_START_MARKER
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|MARKED_START_MARKER
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Repository ids should still be available
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dataStore
operator|.
name|getAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REPOSITORY
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|rootFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|rootFolder
argument_list|)
expr_stmt|;
name|String
name|repoId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|refs
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"1_1"
argument_list|,
literal|"1_2"
argument_list|)
decl_stmt|;
name|File
name|f
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|FileIOUtils
operator|.
name|writeStrings
argument_list|(
name|refs
operator|.
name|iterator
argument_list|()
argument_list|,
name|f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|,
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId
argument_list|)
argument_list|)
expr_stmt|;
name|DataRecord
name|rec
init|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|refsReturned
init|=
name|FileIOUtils
operator|.
name|readStringsAsSet
argument_list|(
name|rec
operator|.
name|getStream
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|refs
argument_list|,
name|refsReturned
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|deleteAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|addMetadataRecord
argument_list|(
name|f
argument_list|,
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId
argument_list|)
argument_list|)
expr_stmt|;
name|rec
operator|=
name|dataStore
operator|.
name|getMetadataRecord
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getNameFromId
argument_list|(
name|repoId
argument_list|)
argument_list|)
expr_stmt|;
name|refsReturned
operator|=
name|FileIOUtils
operator|.
name|readStringsAsSet
argument_list|(
name|rec
operator|.
name|getStream
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|refs
argument_list|,
name|refsReturned
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getIdFromName
argument_list|(
name|rec
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|repoId
argument_list|)
expr_stmt|;
name|dataStore
operator|.
name|deleteAllMetadataRecords
argument_list|(
name|SharedStoreRecordType
operator|.
name|REFERENCES
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAllChunkIds
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|rootFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|rootFolder
argument_list|)
expr_stmt|;
name|int
name|number
init|=
literal|10
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
name|newHashSet
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|String
name|rec
init|=
name|dataStore
operator|.
name|writeBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|16516
argument_list|)
argument_list|)
decl_stmt|;
name|added
operator|.
name|add
argument_list|(
name|rec
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|newHashSet
argument_list|(
name|dataStore
operator|.
name|getAllChunkIds
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|added
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetAllRecords
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|rootFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|rootFolder
argument_list|)
expr_stmt|;
name|int
name|number
init|=
literal|10
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
name|newHashSet
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|String
name|rec
init|=
name|dataStore
operator|.
name|addRecord
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|16516
argument_list|)
argument_list|)
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|added
operator|.
name|add
argument_list|(
name|rec
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|retrieved
init|=
name|newHashSet
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|newHashSet
argument_list|(
name|dataStore
operator|.
name|getAllRecords
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|DataRecord
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
name|DataRecord
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|added
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamFromGetAllRecords
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|rootFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|rootFolder
argument_list|)
expr_stmt|;
name|int
name|number
init|=
literal|10
decl_stmt|;
name|Set
argument_list|<
name|DataRecord
argument_list|>
name|added
init|=
name|newHashSet
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|added
operator|.
name|add
argument_list|(
name|dataStore
operator|.
name|addRecord
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|16516
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|DataRecord
argument_list|>
name|retrieved
init|=
name|newHashSet
argument_list|(
operator|(
name|dataStore
operator|.
name|getAllRecords
argument_list|()
operator|)
argument_list|)
decl_stmt|;
name|assertRecords
argument_list|(
name|added
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRecordForId
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|rootFolder
init|=
name|folder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|dataStore
operator|=
name|getBlobStore
argument_list|(
name|rootFolder
argument_list|)
expr_stmt|;
name|int
name|number
init|=
literal|10
decl_stmt|;
name|Set
argument_list|<
name|DataRecord
argument_list|>
name|added
init|=
name|newHashSet
argument_list|()
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|added
operator|.
name|add
argument_list|(
name|dataStore
operator|.
name|addRecord
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|16516
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|DataRecord
argument_list|>
name|retrieved
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|DataRecord
name|rec
range|:
name|added
control|)
block|{
name|retrieved
operator|.
name|add
argument_list|(
name|dataStore
operator|.
name|getRecordForId
argument_list|(
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertRecords
argument_list|(
name|added
argument_list|,
name|retrieved
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertRecords
parameter_list|(
name|Set
argument_list|<
name|DataRecord
argument_list|>
name|expected
parameter_list|,
name|Set
argument_list|<
name|DataRecord
argument_list|>
name|retrieved
parameter_list|)
throws|throws
name|DataStoreException
throws|,
name|IOException
block|{
comment|//assert streams
name|Map
argument_list|<
name|DataIdentifier
argument_list|,
name|DataRecord
argument_list|>
name|retMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|DataRecord
name|ret
range|:
name|retrieved
control|)
block|{
name|retMap
operator|.
name|put
argument_list|(
name|ret
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DataRecord
name|rec
range|:
name|expected
control|)
block|{
name|assertEquals
argument_list|(
literal|"Record id different for "
operator|+
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|retMap
operator|.
name|get
argument_list|(
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Record length different for "
operator|+
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|rec
operator|.
name|getLength
argument_list|()
argument_list|,
name|retMap
operator|.
name|get
argument_list|(
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Record lastModified different for "
operator|+
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|rec
operator|.
name|getLastModified
argument_list|()
argument_list|,
name|retMap
operator|.
name|get
argument_list|(
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|getLastModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Record steam different for "
operator|+
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|rec
operator|.
name|getStream
argument_list|()
argument_list|,
name|retMap
operator|.
name|get
argument_list|(
name|rec
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|InputStream
name|randomStream
parameter_list|(
name|int
name|seed
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
block|}
end_class

end_unit

