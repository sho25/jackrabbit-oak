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
name|upgrade
operator|.
name|blob
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|InputSupplier
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LengthCachingDataStoreTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|tempFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|mappingFileData
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|root
init|=
name|tempFolder
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|File
name|mappingFile
init|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
literal|"mapping.txt"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
literal|"1000|foo\n2000|bar"
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|text
argument_list|,
name|mappingFile
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
expr_stmt|;
name|LengthCachingDataStore
name|fds
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setMappingFilePath
argument_list|(
name|mappingFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|DataRecord
name|dr
init|=
name|fds
operator|.
name|getRecord
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|dr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|dr
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2000
argument_list|,
name|fds
operator|.
name|getRecord
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|configDelegate
parameter_list|()
throws|throws
name|Exception
block|{
comment|//1. Store the config in a file
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"minRecordLength"
argument_list|,
literal|"4972"
argument_list|)
expr_stmt|;
name|File
name|configFile
init|=
name|tempFolder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
name|p
operator|.
name|store
argument_list|(
name|fos
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//2. Configure the delegate and config file
name|LengthCachingDataStore
name|fds
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setDelegateConfigFilePath
argument_list|(
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4972
argument_list|,
name|fds
operator|.
name|getMinRecordLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|delegateRecordTest
parameter_list|()
throws|throws
name|Exception
block|{
name|FileDataStore
name|ds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|bytes
argument_list|(
name|ds
operator|.
name|getMinRecordLength
argument_list|()
operator|+
literal|10
argument_list|)
decl_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|DataRecord
name|dr
init|=
name|ds
operator|.
name|addRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|mappingFile
init|=
operator|new
name|File
argument_list|(
name|tempFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"mapping.txt"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s|%s"
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|dr
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|text
argument_list|,
name|mappingFile
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
expr_stmt|;
name|LengthCachingDataStore
name|fds
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setMappingFilePath
argument_list|(
name|mappingFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|DataRecord
name|dr2
init|=
name|fds
operator|.
name|getRecordIfStored
argument_list|(
name|dr
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dr
argument_list|,
name|dr2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dr
operator|.
name|getLength
argument_list|()
argument_list|,
name|dr2
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ByteStreams
operator|.
name|equal
argument_list|(
name|supplier
argument_list|(
name|dr
argument_list|)
argument_list|,
name|supplier
argument_list|(
name|dr2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeBackNewEntries
parameter_list|()
throws|throws
name|Exception
block|{
comment|//1. Add some entries to FDS
name|FileDataStore
name|fds1
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|File
name|fds1Dir
init|=
name|tempFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|int
name|minSize
init|=
name|fds1
operator|.
name|getMinRecordLength
argument_list|()
decl_stmt|;
name|fds1
operator|.
name|init
argument_list|(
name|fds1Dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|DataRecord
name|dr1
init|=
name|fds1
operator|.
name|addRecord
argument_list|(
name|byteStream
argument_list|(
name|minSize
operator|+
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|DataRecord
name|dr2
init|=
name|fds1
operator|.
name|addRecord
argument_list|(
name|byteStream
argument_list|(
name|minSize
operator|+
literal|100
argument_list|)
argument_list|)
decl_stmt|;
comment|//2. Try reading them so as to populate the new mappings
name|LengthCachingDataStore
name|fds2
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds2
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds2
operator|.
name|init
argument_list|(
name|fds1Dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds2
operator|.
name|getRecord
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
name|dr1
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fds2
operator|.
name|getRecord
argument_list|(
operator|new
name|DataIdentifier
argument_list|(
name|dr2
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|mappingFile
init|=
name|fds2
operator|.
name|getMappingFile
argument_list|()
decl_stmt|;
comment|//3. Get the mappings pushed to file
name|fds2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//4. Open a new FDS pointing to new directory. Read should still work fine
comment|//as they would be served by the mapping data
name|LengthCachingDataStore
name|fds3
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds3
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds3
operator|.
name|setMappingFilePath
argument_list|(
name|mappingFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds3
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds3
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dr1
operator|.
name|getLength
argument_list|()
argument_list|,
name|fds3
operator|.
name|getRecord
argument_list|(
name|dr1
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
name|dr2
operator|.
name|getLength
argument_list|()
argument_list|,
name|fds3
operator|.
name|getRecord
argument_list|(
name|dr2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|DataRecord
name|dr3
init|=
name|fds3
operator|.
name|addRecord
argument_list|(
name|byteStream
argument_list|(
name|minSize
operator|+
literal|200
argument_list|)
argument_list|)
decl_stmt|;
comment|//5. Close again so see if update of existing file works
name|fds3
operator|.
name|close
argument_list|()
expr_stmt|;
name|LengthCachingDataStore
name|fds4
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds4
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds4
operator|.
name|setMappingFilePath
argument_list|(
name|mappingFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds4
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dr3
operator|.
name|getLength
argument_list|()
argument_list|,
name|fds4
operator|.
name|getRecord
argument_list|(
name|dr3
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
name|dr2
operator|.
name|getLength
argument_list|()
argument_list|,
name|fds4
operator|.
name|getRecord
argument_list|(
name|dr2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|referenceHandling
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|minSize
init|=
operator|new
name|FileDataStore
argument_list|()
operator|.
name|getMinRecordLength
argument_list|()
decl_stmt|;
name|LengthCachingDataStore
name|fds
init|=
operator|new
name|LengthCachingDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setDelegateClass
argument_list|(
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
name|tempFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|DataRecord
name|dr1
init|=
name|fds
operator|.
name|addRecord
argument_list|(
name|byteStream
argument_list|(
name|minSize
operator|+
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fds
operator|.
name|getRecordFromReference
argument_list|(
name|dr1
operator|.
name|getReference
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dr1
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|fds
operator|.
name|getRecordFromReference
argument_list|(
name|dr1
operator|.
name|getReference
argument_list|()
argument_list|)
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|InputStream
name|byteStream
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|(
name|size
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|byte
index|[]
name|bytes
parameter_list|(
name|int
name|size
parameter_list|)
block|{
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
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|private
specifier|static
name|InputSupplier
argument_list|<
name|InputStream
argument_list|>
name|supplier
parameter_list|(
specifier|final
name|DataRecord
name|dr
parameter_list|)
block|{
return|return
operator|new
name|InputSupplier
argument_list|<
name|InputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|getInput
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|dr
operator|.
name|getStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|DataStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

