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
name|index
operator|.
name|lucene
operator|.
name|directory
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
name|List
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
name|Strings
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|lucene
operator|.
name|IndexDefinition
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
name|lucene
operator|.
name|LuceneIndexEditorContext
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|plugins
operator|.
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|IndexRootDirectoryTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|temporaryFolder
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
name|IndexRootDirectory
name|dir
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
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
name|dir
operator|=
operator|new
name|IndexRootDirectory
argument_list|(
name|temporaryFolder
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
name|getIndexDirOldFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|f1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|LocalIndexDir
operator|.
name|isIndexDir
argument_list|(
name|f1
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_COUNT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|File
name|f2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|File
name|f3
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
comment|//Both should be same dir
name|assertEquals
argument_list|(
name|f2
argument_list|,
name|f3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|f2
operator|.
name|getParentFile
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|File
name|f1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|File
name|f2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reindexCaseWithSamePath
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|File
name|f1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|resetBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|f2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|dirs
init|=
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a/b"
argument_list|)
decl_stmt|;
comment|//First one should be F2 as it got created later
name|assertEquals
argument_list|(
name|f2
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFSPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a/b/c"
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|allLocalIndexes
parameter_list|()
throws|throws
name|Exception
block|{
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|File
name|fa1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|resetBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|fa2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|File
name|fb1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|resetBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|fb2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/b"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|dirs
init|=
name|dir
operator|.
name|getAllLocalIndexes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fb2
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|getDir
argument_list|(
literal|"/b"
argument_list|,
name|dirs
argument_list|)
operator|.
name|getFSPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fa2
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|getDir
argument_list|(
literal|"/a"
argument_list|,
name|dirs
argument_list|)
operator|.
name|getFSPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexFolderName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/abc12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xyabc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/xy:abc12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xyabc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/xy:abc#^&12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xyabc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/oak:index/xy:abc12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"content_xyabc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/content/oak:index/xy:abc12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sales_xyabc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/content/sales/oak:index/xy:abc12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"appsales_xyabc12"
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
literal|"/content/app:sales/oak:index/xy:abc12"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|longFileName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|longName
init|=
name|Strings
operator|.
name|repeat
argument_list|(
literal|"x"
argument_list|,
name|IndexRootDirectory
operator|.
name|MAX_NAME_LENGTH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|longName
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
name|longName
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|longName2
init|=
name|Strings
operator|.
name|repeat
argument_list|(
literal|"x"
argument_list|,
name|IndexRootDirectory
operator|.
name|MAX_NAME_LENGTH
operator|+
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|longName
argument_list|,
name|IndexRootDirectory
operator|.
name|getIndexFolderBaseName
argument_list|(
name|longName2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcIndexDirs
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Create an old format directory
name|File
name|fa0
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|configureUniqueId
argument_list|()
expr_stmt|;
name|File
name|fa1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|configureUniqueId
argument_list|()
expr_stmt|;
name|File
name|fa2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|dirs
init|=
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|gcEmptyDirs
argument_list|(
name|fa2
argument_list|)
expr_stmt|;
comment|//No index dir should be removed. Even empty ones
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|configureUniqueId
argument_list|()
expr_stmt|;
name|File
name|fa3
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Dir size should still be 3 as non empty dir cannot be be collected
name|dir
operator|.
name|gcEmptyDirs
argument_list|(
name|fa3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Now get rid of 'default' for the first localDir dir i.e. fa1
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|fa1
argument_list|)
expr_stmt|;
name|dir
operator|.
name|gcEmptyDirs
argument_list|(
name|fa1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fa0
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|fa2
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|fa3
argument_list|)
expr_stmt|;
comment|//Note that we deleted both fa2 and fa3 but GC was done based on fa2 (fa2< fa3)
comment|//So only dirs which are of same time or older than fa2 would be removed. So in this
comment|//case fa3 would be left
name|dir
operator|.
name|gcEmptyDirs
argument_list|(
name|fa2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
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
name|gcIndexDirsOnStart
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|fa0
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|configureUniqueId
argument_list|()
expr_stmt|;
name|File
name|fa1
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|configureUniqueId
argument_list|()
expr_stmt|;
name|File
name|fa2
init|=
name|dir
operator|.
name|getIndexDir
argument_list|(
name|getDefn
argument_list|()
argument_list|,
literal|"/a"
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//Now reinitialize
name|dir
operator|=
operator|new
name|IndexRootDirectory
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fa0
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fa1
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fa2
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dir
operator|.
name|getLocalIndexes
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeBuilder
name|resetBuilder
parameter_list|()
block|{
name|builder
operator|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
name|void
name|configureUniqueId
parameter_list|()
block|{
name|LuceneIndexEditorContext
operator|.
name|configureUniqueId
argument_list|(
name|resetBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexDefinition
name|getDefn
parameter_list|()
block|{
return|return
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|LocalIndexDir
name|getDir
parameter_list|(
name|String
name|jcrPath
parameter_list|,
name|List
argument_list|<
name|LocalIndexDir
argument_list|>
name|dirs
parameter_list|)
block|{
for|for
control|(
name|LocalIndexDir
name|dir
range|:
name|dirs
control|)
block|{
if|if
condition|(
name|dir
operator|.
name|getJcrPath
argument_list|()
operator|.
name|equals
argument_list|(
name|jcrPath
argument_list|)
condition|)
block|{
return|return
name|dir
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

