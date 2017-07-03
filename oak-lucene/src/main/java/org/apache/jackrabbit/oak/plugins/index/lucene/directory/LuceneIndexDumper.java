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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|OakDirectory
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
name|NodeStateUtils
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
name|ReadOnlyBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
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
name|index
operator|.
name|lucene
operator|.
name|writer
operator|.
name|MultiplexersLucene
operator|.
name|isIndexDirName
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
name|index
operator|.
name|lucene
operator|.
name|writer
operator|.
name|MultiplexersLucene
operator|.
name|isSuggestIndexDirName
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexDumper
block|{
specifier|private
specifier|final
name|NodeState
name|rootState
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|File
name|baseDir
decl_stmt|;
specifier|private
name|long
name|size
decl_stmt|;
specifier|private
name|File
name|indexDir
decl_stmt|;
comment|/**      * Constructs the dumper for copying Lucene index contents      *      * @param rootState rootState of repository      * @param indexPath path of index      * @param baseDir directory under which index contents would be copied to. Dumper      *                would create a sub directory based on index path and then copy      *                the index content under that directory      */
specifier|public
name|LuceneIndexDumper
parameter_list|(
name|NodeState
name|rootState
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|File
name|baseDir
parameter_list|)
block|{
name|this
operator|.
name|rootState
operator|=
name|rootState
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|baseDir
operator|=
name|baseDir
expr_stmt|;
block|}
specifier|public
name|void
name|dump
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
init|)
block|{
name|NodeState
name|idx
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|rootState
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|IndexDefinition
name|defn
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|rootState
argument_list|,
name|idx
argument_list|,
name|indexPath
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|indexDir
operator|=
name|DirectoryUtils
operator|.
name|createIndexDir
argument_list|(
name|baseDir
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|IndexMeta
name|meta
init|=
operator|new
name|IndexMeta
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|dirName
range|:
name|idx
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|dirName
argument_list|)
operator|&&
operator|(
name|isIndexDirName
argument_list|(
name|dirName
argument_list|)
operator|||
name|isSuggestIndexDirName
argument_list|(
name|dirName
argument_list|)
operator|)
condition|)
block|{
name|copyContent
argument_list|(
name|idx
argument_list|,
name|defn
argument_list|,
name|meta
argument_list|,
name|indexDir
argument_list|,
name|dirName
argument_list|,
name|closer
argument_list|)
expr_stmt|;
block|}
block|}
name|DirectoryUtils
operator|.
name|writeMeta
argument_list|(
name|indexDir
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|File
name|getIndexDir
parameter_list|()
block|{
return|return
name|indexDir
return|;
block|}
specifier|private
name|void
name|copyContent
parameter_list|(
name|NodeState
name|idx
parameter_list|,
name|IndexDefinition
name|defn
parameter_list|,
name|IndexMeta
name|meta
parameter_list|,
name|File
name|dir
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|idxDir
init|=
name|DirectoryUtils
operator|.
name|createSubDir
argument_list|(
name|dir
argument_list|,
name|dirName
argument_list|)
decl_stmt|;
name|meta
operator|.
name|addDirectoryMapping
argument_list|(
name|dirName
argument_list|,
name|idxDir
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|sourceDir
init|=
operator|new
name|OakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|idx
argument_list|)
argument_list|,
name|dirName
argument_list|,
name|defn
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Directory
name|targetDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|idxDir
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|sourceDir
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|targetDir
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|sourceDir
operator|.
name|listAll
argument_list|()
control|)
block|{
name|sourceDir
operator|.
name|copy
argument_list|(
name|targetDir
argument_list|,
name|file
argument_list|,
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|size
operator|+=
name|sourceDir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

