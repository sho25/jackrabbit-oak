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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|IOUtils
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
name|PropertyState
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|SuggestHelper
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
name|oak
operator|.
name|util
operator|.
name|PerfLogger
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
name|DirectoryReader
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
name|config
operator|.
name|TikaConfig
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
name|mime
operator|.
name|MediaType
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
specifier|final
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneIndexEditorContext
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
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
argument_list|,
literal|false
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
name|defaultParser
init|=
name|createDefaultParser
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
specifier|private
name|Parser
name|parser
decl_stmt|;
specifier|private
name|Directory
name|directory
decl_stmt|;
specifier|private
specifier|final
name|TextExtractionStats
name|textExtractionStats
init|=
operator|new
name|TextExtractionStats
argument_list|()
decl_stmt|;
comment|/**      * The media types supported by the parser used.      */
specifier|private
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedMediaTypes
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
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|parser
operator|=
name|initializeTikaParser
argument_list|(
name|definition
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|directory
operator|=
name|newIndexDirectory
argument_list|(
name|definition
argument_list|,
name|definitionBuilder
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Created IndexWriter for directory {}"
argument_list|,
name|definition
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
specifier|final
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
name|updateSuggester
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
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
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Closed IndexWriter for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
name|textExtractionStats
operator|.
name|log
argument_list|(
name|reindex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * eventually update suggest dictionary      * @throws IOException if suggest dictionary update fails      */
specifier|private
name|void
name|updateSuggester
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|definition
operator|.
name|isSuggestEnabled
argument_list|()
condition|)
block|{
name|boolean
name|updateSuggester
init|=
literal|false
decl_stmt|;
name|NodeBuilder
name|suggesterStatus
init|=
name|definitionBuilder
operator|.
name|child
argument_list|(
literal|":suggesterStatus"
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggesterStatus
operator|.
name|hasProperty
argument_list|(
literal|"lastUpdated"
argument_list|)
condition|)
block|{
name|PropertyState
name|suggesterLastUpdatedValue
init|=
name|suggesterStatus
operator|.
name|getProperty
argument_list|(
literal|"lastUpdated"
argument_list|)
decl_stmt|;
name|Calendar
name|suggesterLastUpdatedTime
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|suggesterLastUpdatedValue
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|updateFrequency
init|=
name|definition
operator|.
name|getSuggesterUpdateFrequencyMinutes
argument_list|()
decl_stmt|;
name|suggesterLastUpdatedTime
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
name|updateFrequency
argument_list|)
expr_stmt|;
if|if
condition|(
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|after
argument_list|(
name|suggesterLastUpdatedTime
argument_list|)
condition|)
block|{
name|updateSuggester
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|updateSuggester
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|updateSuggester
condition|)
block|{
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|SuggestHelper
operator|.
name|updateSuggester
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|suggesterStatus
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
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"could not update suggester"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
specifier|public
name|boolean
name|isSupportedMediaType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|supportedMediaTypes
operator|==
literal|null
condition|)
block|{
name|supportedMediaTypes
operator|=
name|getParser
argument_list|()
operator|.
name|getSupportedTypes
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|supportedMediaTypes
operator|.
name|contains
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
name|type
argument_list|)
argument_list|)
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
specifier|public
name|void
name|recordTextExtractionStats
parameter_list|(
name|long
name|timeInMillis
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|textExtractionStats
operator|.
name|addStats
argument_list|(
name|timeInMillis
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Parser
name|initializeTikaParser
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|)
block|{
name|ClassLoader
name|current
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|definition
operator|.
name|hasCustomTikaConfig
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|LuceneIndexEditorContext
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|definition
operator|.
name|getTikaConfig
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|AutoDetectParser
argument_list|(
name|getTikaConfig
argument_list|(
name|is
argument_list|,
name|definition
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
return|return
name|defaultParser
return|;
block|}
specifier|private
specifier|static
name|AutoDetectParser
name|createDefaultParser
parameter_list|()
block|{
name|ClassLoader
name|current
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|URL
name|configUrl
init|=
name|LuceneIndexEditorContext
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"tika-config.xml"
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|configUrl
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|LuceneIndexEditorContext
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|=
name|configUrl
operator|.
name|openStream
argument_list|()
expr_stmt|;
name|TikaConfig
name|config
init|=
operator|new
name|TikaConfig
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Loaded default Tika Config from classpath {}"
argument_list|,
name|configUrl
argument_list|)
expr_stmt|;
return|return
operator|new
name|AutoDetectParser
argument_list|(
name|config
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Tika configuration not available : "
operator|+
name|configUrl
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Default Tika configuration not found from {}"
argument_list|,
name|configUrl
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AutoDetectParser
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|TikaConfig
name|getTikaConfig
parameter_list|(
name|InputStream
name|configStream
parameter_list|,
name|Object
name|source
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|TikaConfig
argument_list|(
name|configStream
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Tika configuration not available : "
operator|+
name|source
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
return|;
block|}
specifier|static
class|class
name|TextExtractionStats
block|{
comment|/**          * Log stats only if time spent is more than 2 min          */
specifier|private
specifier|static
specifier|final
name|long
name|LOGGING_THRESHOLD
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|long
name|totalSize
decl_stmt|;
specifier|private
name|long
name|totalTime
decl_stmt|;
specifier|public
name|void
name|addStats
parameter_list|(
name|long
name|timeInMillis
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|totalSize
operator|+=
name|size
expr_stmt|;
name|totalTime
operator|+=
name|timeInMillis
expr_stmt|;
block|}
specifier|public
name|void
name|log
parameter_list|(
name|boolean
name|reindex
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Text extraction stats {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|anyParsingDone
argument_list|()
operator|&&
operator|(
name|reindex
operator|||
name|isTakingLotsOfTime
argument_list|()
operator|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Text extraction stats {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isTakingLotsOfTime
parameter_list|()
block|{
return|return
name|totalTime
operator|>
name|LOGGING_THRESHOLD
return|;
block|}
specifier|private
name|boolean
name|anyParsingDone
parameter_list|()
block|{
return|return
name|count
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|" %d (%s, %s)"
argument_list|,
name|count
argument_list|,
name|timeInWords
argument_list|(
name|totalTime
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalSize
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|timeInWords
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%d min, %d sec"
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMinutes
argument_list|(
name|millis
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|millis
argument_list|)
operator|-
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toSeconds
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMinutes
argument_list|(
name|millis
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

