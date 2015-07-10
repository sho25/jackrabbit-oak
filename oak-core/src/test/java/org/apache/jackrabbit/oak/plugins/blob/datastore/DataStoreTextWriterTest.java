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
operator|.
name|datastore
package|;
end_package

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
name|FileDataStore
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
name|DataStoreTextWriter
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
name|TextWriter
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
name|index
operator|.
name|fulltext
operator|.
name|ExtractedText
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
name|index
operator|.
name|fulltext
operator|.
name|ExtractedText
operator|.
name|ExtractionResult
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
name|memory
operator|.
name|ArrayBasedBlob
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

begin_class
specifier|public
class|class
name|DataStoreTextWriterTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basicOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|fdsDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|FileDataStore
name|fds
init|=
name|createFDS
argument_list|(
name|fdsDir
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|DataRecord
name|dr
init|=
name|fds
operator|.
name|addRecord
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|File
name|writerDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|TextWriter
name|writer
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|writerDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|dr
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|FileDataStore
name|fds2
init|=
name|createFDS
argument_list|(
name|writerDir
argument_list|)
decl_stmt|;
name|DataRecord
name|dr2
init|=
name|fds2
operator|.
name|getRecordIfStored
argument_list|(
name|dr
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|is
argument_list|,
name|dr2
operator|.
name|getStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noLoadingInReadOnlyMode
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreTextWriter
name|w
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|w
operator|.
name|getEmptyBlobsHolder
argument_list|()
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|w
operator|.
name|getErrorBlobsHolder
argument_list|()
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
name|DataStoreTextWriter
name|w1
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|w1
operator|.
name|getEmptyBlobsHolder
argument_list|()
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|w1
operator|.
name|getErrorBlobsHolder
argument_list|()
operator|.
name|getLoadCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkEmptyAndErrorBlobs
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreTextWriter
name|w
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|w
operator|.
name|markEmpty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|w
operator|.
name|markError
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DataStoreTextWriter
name|w2
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ExtractionResult
operator|.
name|EMPTY
argument_list|,
name|w2
operator|.
name|getText
argument_list|(
literal|"/a"
argument_list|,
operator|new
name|IdBlob
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
operator|.
name|getExtractionResult
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ExtractionResult
operator|.
name|ERROR
argument_list|,
name|w2
operator|.
name|getText
argument_list|(
literal|"/a"
argument_list|,
operator|new
name|IdBlob
argument_list|(
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
operator|.
name|getExtractionResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonExistingEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|fdsDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|FileDataStore
name|fds
init|=
name|createFDS
argument_list|(
name|fdsDir
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|DataRecord
name|dr
init|=
name|fds
operator|.
name|addRecord
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|File
name|writerDir
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|DataStoreTextWriter
name|w
init|=
operator|new
name|DataStoreTextWriter
argument_list|(
name|writerDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|dr
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|w
operator|.
name|isProcessed
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|w
operator|.
name|getText
argument_list|(
literal|"/a"
argument_list|,
operator|new
name|IdBlob
argument_list|(
literal|"foo"
argument_list|,
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|id
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|w
operator|.
name|isProcessed
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|ExtractedText
name|et
init|=
name|w
operator|.
name|getText
argument_list|(
literal|"/a"
argument_list|,
operator|new
name|IdBlob
argument_list|(
literal|"foo"
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|et
operator|.
name|getExtractedText
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
name|et
operator|.
name|getExtractionResult
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|markEmpty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|w
operator|.
name|isProcessed
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|FileDataStore
name|createFDS
parameter_list|(
name|File
name|root
parameter_list|)
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setPath
argument_list|(
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setMinRecordLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|fds
return|;
block|}
specifier|private
specifier|static
class|class
name|IdBlob
extends|extends
name|ArrayBasedBlob
block|{
specifier|final
name|String
name|id
decl_stmt|;
specifier|public
name|IdBlob
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
block|}
end_class

end_unit

