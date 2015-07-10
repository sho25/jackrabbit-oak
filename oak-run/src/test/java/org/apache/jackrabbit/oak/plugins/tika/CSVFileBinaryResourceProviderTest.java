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
name|tika
package|;
end_package

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
name|util
operator|.
name|Map
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
name|Charsets
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
name|io
operator|.
name|Files
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
name|csv
operator|.
name|CSVPrinter
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
name|blob
operator|.
name|MemoryBlobStore
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

begin_class
specifier|public
class|class
name|CSVFileBinaryResourceProviderTest
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
name|testGetBinaries
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|CSVPrinter
name|p
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sb
argument_list|,
name|CSVFileBinaryResourceProvider
operator|.
name|FORMAT
argument_list|)
decl_stmt|;
comment|// BLOB_ID, LENGTH, JCR_MIMETYPE, JCR_ENCODING, JCR_PATH
name|p
operator|.
name|printRecord
argument_list|(
literal|"a"
argument_list|,
literal|123
argument_list|,
literal|"text/plain"
argument_list|,
literal|null
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|p
operator|.
name|printRecord
argument_list|(
literal|"a2"
argument_list|,
literal|123
argument_list|,
literal|"text/plain"
argument_list|,
literal|null
argument_list|,
literal|"/a/c"
argument_list|)
expr_stmt|;
name|p
operator|.
name|printRecord
argument_list|(
literal|"b"
argument_list|,
literal|null
argument_list|,
literal|"text/plain"
argument_list|,
literal|null
argument_list|,
literal|"/b"
argument_list|)
expr_stmt|;
name|p
operator|.
name|printRecord
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"text/plain"
argument_list|,
literal|null
argument_list|,
literal|"/c"
argument_list|)
expr_stmt|;
name|File
name|dataFile
init|=
name|temporaryFolder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|sb
argument_list|,
name|dataFile
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|CSVFileBinaryResourceProvider
name|provider
init|=
operator|new
name|CSVFileBinaryResourceProvider
argument_list|(
name|dataFile
argument_list|,
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|BinaryResource
argument_list|>
name|binaries
init|=
name|provider
operator|.
name|getBinaries
argument_list|(
literal|"/"
argument_list|)
operator|.
name|uniqueIndex
argument_list|(
name|BinarySourceMapper
operator|.
name|BY_BLOBID
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|binaries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|binaries
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getBlobId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a"
argument_list|,
name|binaries
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|binaries
operator|=
name|provider
operator|.
name|getBinaries
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|uniqueIndex
argument_list|(
name|BinarySourceMapper
operator|.
name|BY_BLOBID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|binaries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|provider
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

