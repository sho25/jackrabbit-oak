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
name|importer
operator|.
name|IndexImporterProvider
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
name|LuceneIndexDefinition
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
name|search
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
name|search
operator|.
name|ReindexOperations
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
name|GarbageCollectableBlobStore
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
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexImporter
implements|implements
name|IndexImporterProvider
block|{
specifier|private
name|GarbageCollectableBlobStore
name|blobStore
decl_stmt|;
specifier|public
name|LuceneIndexImporter
parameter_list|()
block|{      }
specifier|public
name|LuceneIndexImporter
parameter_list|(
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|importIndex
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|definitionBuilder
parameter_list|,
name|File
name|indexDir
parameter_list|)
throws|throws
name|IOException
block|{
name|LocalIndexDir
name|localIndex
init|=
operator|new
name|LocalIndexDir
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
comment|//TODO The indexFormatVersion would be considered latest. Need to be revisited
comment|//if off line indexing uses older Lucene
name|definitionBuilder
operator|.
name|getChildNode
argument_list|(
name|IndexDefinition
operator|.
name|STATUS_NODE
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|ReindexOperations
name|reindexOps
init|=
operator|new
name|ReindexOperations
argument_list|(
name|root
argument_list|,
name|definitionBuilder
argument_list|,
name|localIndex
operator|.
name|getJcrPath
argument_list|()
argument_list|,
operator|new
name|LuceneIndexDefinition
operator|.
name|Builder
argument_list|()
argument_list|)
decl_stmt|;
name|LuceneIndexDefinition
name|definition
init|=
operator|(
name|LuceneIndexDefinition
operator|)
name|reindexOps
operator|.
name|apply
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|dir
range|:
name|localIndex
operator|.
name|dir
operator|.
name|listFiles
argument_list|(
name|File
operator|::
name|isDirectory
argument_list|)
control|)
block|{
name|String
name|jcrName
init|=
name|localIndex
operator|.
name|indexMeta
operator|.
name|getJcrNameFromFSName
argument_list|(
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|jcrName
operator|!=
literal|null
condition|)
block|{
name|copyDirectory
argument_list|(
name|definition
argument_list|,
name|definitionBuilder
argument_list|,
name|jcrName
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE_LUCENE
return|;
block|}
specifier|public
name|void
name|setBlobStore
parameter_list|(
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
block|{
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
block|}
specifier|private
name|void
name|copyDirectory
parameter_list|(
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|NodeBuilder
name|definitionBuilder
parameter_list|,
name|String
name|jcrName
parameter_list|,
name|File
name|dir
parameter_list|)
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
name|Directory
name|sourceDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|sourceDir
argument_list|)
expr_stmt|;
comment|//Remove any existing directory as in import case
comment|//the builder can have existing hidden node structures
comment|//So remove the ones which are being imported and leave
comment|// //others as is
name|definitionBuilder
operator|.
name|getChildNode
argument_list|(
name|jcrName
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Directory
name|targetDir
init|=
operator|new
name|OakDirectory
argument_list|(
name|definitionBuilder
argument_list|,
name|jcrName
argument_list|,
name|definition
argument_list|,
literal|false
argument_list|,
name|blobStore
argument_list|)
decl_stmt|;
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
block|}
block|}
block|}
block|}
end_class

end_unit

