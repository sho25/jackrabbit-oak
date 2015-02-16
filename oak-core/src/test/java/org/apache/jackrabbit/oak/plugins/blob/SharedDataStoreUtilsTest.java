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
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
name|IOException
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
name|FileUtils
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Test for SharedDataUtils to test addition, retrieval and deletion of root records.  */
end_comment

begin_class
specifier|public
class|class
name|SharedDataStoreUtilsTest
block|{
name|SharedDataStore
name|dataStore
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Assume
operator|.
name|assumeThat
argument_list|(
name|DataStoreUtils
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|SharedDataStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2525"
argument_list|)
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|dataStore
operator|=
name|DataStoreUtils
operator|.
name|getBlobStore
argument_list|()
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
name|Assert
operator|.
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
name|Assert
operator|.
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
name|Assert
operator|.
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
name|Assert
operator|.
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
comment|// Earliest should be the 1st reference record
name|Assert
operator|.
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
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
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
comment|// Repository ids should still be available
name|Assert
operator|.
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
name|After
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|DataStoreUtils
operator|.
name|getHomeDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

