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
name|assertFalse
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
name|RandomAccessFile
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
name|segment
operator|.
name|SegmentNodeBuilder
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
name|segment
operator|.
name|SegmentNodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
specifier|public
class|class
name|FileStoreTest
block|{
specifier|private
name|File
name|directory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"FileStoreTest"
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecovery
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStore
name|store
init|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// first 1kB
name|SegmentNodeState
name|base
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"step"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// second 1kB
name|base
operator|=
name|store
operator|.
name|getHead
argument_list|()
expr_stmt|;
name|builder
operator|=
name|base
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"step"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// third 1kB
name|store
operator|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getString
argument_list|(
literal|"step"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|file
operator|.
name|setLength
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|getString
argument_list|(
literal|"step"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|file
operator|.
name|setLength
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|getHead
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"step"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRearrangeOldData
parameter_list|()
throws|throws
name|IOException
block|{
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00010a.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00030.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"bulk00002.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"bulk00005a.tar"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|?
argument_list|>
name|files
init|=
name|FileStore
operator|.
name|collectFiles
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|,
literal|32
argument_list|,
literal|33
argument_list|)
argument_list|,
name|newArrayList
argument_list|(
name|files
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00000a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00001a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00031a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00032a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
literal|"data00033a.tar"
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|files
operator|=
name|FileStore
operator|.
name|collectFiles
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newArrayList
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|31
argument_list|,
literal|32
argument_list|,
literal|33
argument_list|)
argument_list|,
name|newArrayList
argument_list|(
name|files
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

