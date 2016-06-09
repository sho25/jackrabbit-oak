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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|write
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
name|file
operator|.
name|JournalReader
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
name|JournalReaderTest
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
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|JournalReader
name|journalReader
init|=
name|createJournalReader
argument_list|(
literal|""
argument_list|)
decl_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|journalReader
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleton
parameter_list|()
throws|throws
name|IOException
block|{
name|JournalReader
name|journalReader
init|=
name|createJournalReader
argument_list|(
literal|"one 1"
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|journal
init|=
name|journalReader
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiple
parameter_list|()
throws|throws
name|IOException
block|{
name|JournalReader
name|journalReader
init|=
name|createJournalReader
argument_list|(
literal|"one 1\ntwo 2\nthree 3 456"
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|journal
init|=
name|journalReader
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"three"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSpaces
parameter_list|()
throws|throws
name|IOException
block|{
name|JournalReader
name|journalReader
init|=
name|createJournalReader
argument_list|(
literal|"\n \n  \n   "
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|journal
init|=
name|journalReader
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIgnoreInvalid
parameter_list|()
throws|throws
name|IOException
block|{
name|JournalReader
name|journalReader
init|=
name|createJournalReader
argument_list|(
literal|"one 1\ntwo 2\ninvalid\nthree 3"
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|journal
init|=
name|journalReader
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"three"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|journal
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|journal
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|journalReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|JournalReader
name|createJournalReader
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|journalFile
init|=
name|folder
operator|.
name|newFile
argument_list|(
literal|"jrt"
argument_list|)
decl_stmt|;
name|write
argument_list|(
name|journalFile
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
operator|new
name|JournalReader
argument_list|(
name|journalFile
argument_list|)
return|;
block|}
block|}
end_class

end_unit

