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
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|IndexCopier
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
name|LuceneIndexEditorProvider
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
name|directory
operator|.
name|ActiveDeletedBlobCollectorFactory
operator|.
name|BlobDeletionCallback
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
name|directory
operator|.
name|DirectoryFactory
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
name|writer
operator|.
name|LuceneIndexWriterConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexHelper
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PROP_BUFFER_SIZE
init|=
literal|"oak.index.ramBufferSizeMB"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE_DEFAULT
init|=
literal|32
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexHelper
name|indexHelper
decl_stmt|;
specifier|private
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
name|LuceneIndexHelper
parameter_list|(
name|IndexHelper
name|indexHelper
parameter_list|)
block|{
name|this
operator|.
name|indexHelper
operator|=
name|indexHelper
expr_stmt|;
block|}
specifier|public
name|LuceneIndexEditorProvider
name|createEditorProvider
parameter_list|()
throws|throws
name|IOException
block|{
name|LuceneIndexEditorProvider
name|editor
decl_stmt|;
if|if
condition|(
name|directoryFactory
operator|!=
literal|null
condition|)
block|{
name|editor
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|getIndexCopier
argument_list|()
argument_list|,
name|indexHelper
operator|.
name|getExtractedTextCache
argument_list|()
argument_list|,
literal|null
argument_list|,
name|indexHelper
operator|.
name|getMountInfoProvider
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|DirectoryFactory
name|newDirectoryFactory
parameter_list|(
name|BlobDeletionCallback
name|blobDeletionCallback
parameter_list|)
block|{
return|return
name|directoryFactory
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|editor
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|getIndexCopier
argument_list|()
argument_list|,
name|indexHelper
operator|.
name|getExtractedTextCache
argument_list|()
argument_list|,
literal|null
argument_list|,
name|indexHelper
operator|.
name|getMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|editor
operator|.
name|setBlobStore
argument_list|(
name|indexHelper
operator|.
name|getGCBlobStore
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|editor
return|;
block|}
specifier|public
name|LuceneIndexWriterConfig
name|getWriterConfigForReindex
parameter_list|()
block|{
name|int
name|buffSize
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
name|PROP_BUFFER_SIZE
argument_list|,
name|BUFFER_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Setting RAMBufferSize for LuceneIndexWriter (configurable via "
operator|+
literal|"system property '{}') to {} MB"
argument_list|,
name|PROP_BUFFER_SIZE
argument_list|,
name|buffSize
argument_list|)
expr_stmt|;
return|return
operator|new
name|LuceneIndexWriterConfig
argument_list|(
name|buffSize
argument_list|)
return|;
block|}
specifier|public
name|void
name|setDirectoryFactory
parameter_list|(
name|DirectoryFactory
name|directoryFactory
parameter_list|)
block|{
name|this
operator|.
name|directoryFactory
operator|=
name|directoryFactory
expr_stmt|;
block|}
specifier|private
name|IndexCopier
name|getIndexCopier
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexCopier
operator|==
literal|null
condition|)
block|{
name|File
name|indexWorkDir
init|=
operator|new
name|File
argument_list|(
name|indexHelper
operator|.
name|getWorkDir
argument_list|()
argument_list|,
literal|"indexWorkDir"
argument_list|)
decl_stmt|;
name|indexCopier
operator|=
operator|new
name|IndexCopier
argument_list|(
name|indexHelper
operator|.
name|getExecutor
argument_list|()
argument_list|,
name|indexWorkDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|indexCopier
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexCopier
operator|!=
literal|null
condition|)
block|{
name|indexCopier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

