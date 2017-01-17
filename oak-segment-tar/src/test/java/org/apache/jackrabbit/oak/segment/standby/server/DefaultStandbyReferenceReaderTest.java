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
name|segment
operator|.
name|standby
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|UUID
operator|.
name|randomUUID
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
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
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
name|Iterator
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
name|SegmentWriter
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
name|FileStore
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
name|FileStoreBuilder
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
name|DefaultStandbyReferenceReaderTest
block|{
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
specifier|private
name|FileStore
name|newFileStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldReturnNullWhenSegmentDoesNotExist
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|DefaultStandbyReferencesReader
name|reader
init|=
operator|new
name|DefaultStandbyReferencesReader
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|reader
operator|.
name|readReferences
argument_list|(
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldReturnEmptyReferences
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|SegmentWriter
name|writer
init|=
name|segmentWriterBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|RecordId
name|id
init|=
name|writer
operator|.
name|writeString
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|DefaultStandbyReferencesReader
name|reader
init|=
operator|new
name|DefaultStandbyReferencesReader
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|references
init|=
name|reader
operator|.
name|readReferences
argument_list|(
name|id
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|references
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldReturnReferences
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|FileStore
name|store
init|=
name|newFileStore
argument_list|()
init|)
block|{
name|SegmentWriter
name|writer
init|=
name|segmentWriterBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|RecordId
name|a
init|=
name|writer
operator|.
name|writeString
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|RecordId
name|b
init|=
name|writer
operator|.
name|writeList
argument_list|(
name|asList
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|DefaultStandbyReferencesReader
name|reader
init|=
operator|new
name|DefaultStandbyReferencesReader
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|reader
operator|.
name|readReferences
argument_list|(
name|b
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|getSegmentId
argument_list|()
operator|.
name|asUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

