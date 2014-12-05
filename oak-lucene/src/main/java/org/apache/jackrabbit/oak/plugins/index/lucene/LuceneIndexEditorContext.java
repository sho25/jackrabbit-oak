begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|INDEX_DATA_CHILD_NAME
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
name|PERSISTENCE_PATH
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
name|VERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NoLockFactory
operator|.
name|getNoLockFactory
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
name|Calendar
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
name|api
operator|.
name|CommitFailedException
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
name|api
operator|.
name|Type
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
name|IndexUpdateCallback
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
name|jackrabbit
operator|.
name|util
operator|.
name|ISO8601
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|SerialMergeScheduler
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
name|tika
operator|.
name|parser
operator|.
name|AutoDetectParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|Parser
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
name|LuceneIndexEditorContext
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneIndexEditorContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|)
block|{
comment|// FIXME: Hack needed to make Lucene work in an OSGi environment
name|Thread
name|thread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|thread
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|IndexWriterConfig
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|VERSION
argument_list|,
name|definition
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|config
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|definition
operator|.
name|getCodec
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|setCodec
argument_list|(
name|definition
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|config
return|;
block|}
finally|finally
block|{
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Directory
name|newIndexDirectory
parameter_list|(
name|IndexDefinition
name|indexDefinition
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|definition
operator|.
name|getString
argument_list|(
name|PERSISTENCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|OakDirectory
argument_list|(
name|definition
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
argument_list|,
name|indexDefinition
argument_list|)
return|;
block|}
else|else
block|{
comment|// try {
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// TODO: close() is never called
comment|// TODO: no locking used
comment|// --> using the FS backend for the index is in any case
comment|// troublesome in clustering scenarios and for backup
comment|// etc. so instead of fixing these issues we'd better
comment|// work on making the in-content index work without
comment|// problems (or look at the Solr indexer as alternative)
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
name|file
argument_list|,
name|getNoLockFactory
argument_list|()
argument_list|)
return|;
comment|// } catch (IOException e) {
comment|// throw new CommitFailedException("Lucene", 1,
comment|// "Failed to open the index in " + path, e);
comment|// }
block|}
block|}
specifier|private
specifier|final
name|IndexWriterConfig
name|config
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definitionBuilder
decl_stmt|;
specifier|private
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|indexedNodes
decl_stmt|;
specifier|private
specifier|final
name|IndexUpdateCallback
name|updateCallback
decl_stmt|;
specifier|private
name|boolean
name|reindex
decl_stmt|;
name|LuceneIndexEditorContext
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|IndexUpdateCallback
name|updateCallback
parameter_list|)
block|{
name|this
operator|.
name|definitionBuilder
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|definition
operator|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|definition
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|getIndexWriterConfig
argument_list|(
name|this
operator|.
name|definition
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexedNodes
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|updateCallback
operator|=
name|updateCallback
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|definition
operator|.
name|isOfOldFormat
argument_list|()
condition|)
block|{
name|IndexDefinition
operator|.
name|updateDefinition
argument_list|(
name|definition
argument_list|)
expr_stmt|;
block|}
block|}
name|Parser
name|getParser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
name|IndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|newIndexDirectory
argument_list|(
name|definition
argument_list|,
name|definitionBuilder
argument_list|)
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
comment|/**      * close writer if it's not null      */
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
comment|//If reindex or fresh index and write is null on close
comment|//it indicates that the index is empty. In such a case trigger
comment|//creation of write such that an empty Lucene index state is persisted
comment|//in directory
if|if
condition|(
name|reindex
operator|&&
name|writer
operator|==
literal|null
condition|)
block|{
name|getWriter
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//OAK-2029 Record the last updated status so
comment|//as to make IndexTracker detect changes when index
comment|//is stored in file system
name|NodeBuilder
name|status
init|=
name|definitionBuilder
operator|.
name|child
argument_list|(
literal|":status"
argument_list|)
decl_stmt|;
name|status
operator|.
name|setProperty
argument_list|(
literal|"lastUpdated"
argument_list|,
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|status
operator|.
name|setProperty
argument_list|(
literal|"indexedNodes"
argument_list|,
name|indexedNodes
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|enableReindexMode
parameter_list|()
block|{
name|reindex
operator|=
literal|true
expr_stmt|;
name|IndexFormatVersion
name|version
init|=
name|IndexDefinition
operator|.
name|determineVersionForFreshIndex
argument_list|(
name|definitionBuilder
argument_list|)
decl_stmt|;
name|definitionBuilder
operator|.
name|setProperty
argument_list|(
name|IndexDefinition
operator|.
name|INDEX_VERSION
argument_list|,
name|version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|incIndexedNodes
parameter_list|()
block|{
name|indexedNodes
operator|++
expr_stmt|;
return|return
name|indexedNodes
return|;
block|}
specifier|public
name|long
name|getIndexedNodes
parameter_list|()
block|{
return|return
name|indexedNodes
return|;
block|}
name|void
name|indexUpdate
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|updateCallback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|IndexDefinition
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
block|}
end_class

end_unit

