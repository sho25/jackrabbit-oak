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
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|HashMap
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
name|FacetHelper
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
name|stats
operator|.
name|Clock
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|miscellaneous
operator|.
name|PerFieldAnalyzerWrapper
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
name|analysis
operator|.
name|shingle
operator|.
name|ShingleAnalyzerWrapper
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
name|facet
operator|.
name|FacetsConfig
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
name|ParseContext
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
specifier|private
specifier|final
name|FacetsConfig
name|facetsConfig
decl_stmt|;
specifier|static
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|boolean
name|remoteDir
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
name|Analyzer
name|definitionAnalyzer
init|=
name|definition
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|()
decl_stmt|;
name|analyzers
operator|.
name|put
argument_list|(
name|FieldNames
operator|.
name|SPELLCHECK
argument_list|,
operator|new
name|ShingleAnalyzerWrapper
argument_list|(
name|LuceneIndexConstants
operator|.
name|ANALYZER
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|definition
operator|.
name|isSuggestAnalyzed
argument_list|()
condition|)
block|{
name|analyzers
operator|.
name|put
argument_list|(
name|FieldNames
operator|.
name|SUGGEST
argument_list|,
name|SuggestHelper
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Analyzer
name|analyzer
init|=
operator|new
name|PerFieldAnalyzerWrapper
argument_list|(
name|definitionAnalyzer
argument_list|,
name|analyzers
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|VERSION
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteDir
condition|)
block|{
name|config
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|static
specifier|final
name|Parser
name|defaultParser
init|=
name|createDefaultParser
argument_list|()
decl_stmt|;
specifier|private
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
annotation|@
name|Nullable
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
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
specifier|private
specifier|final
name|ExtractedTextCache
name|extractedTextCache
decl_stmt|;
specifier|private
specifier|final
name|IndexAugmentorFactory
name|augmentorFactory
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
comment|/**      * The media types supported by the parser used.      */
specifier|private
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedMediaTypes
decl_stmt|;
comment|//Intentionally static, so that it can be set without passing around clock objects
comment|//Set for testing ONLY
specifier|private
specifier|static
name|Clock
name|clock
init|=
name|Clock
operator|.
name|SIMPLE
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
parameter_list|,
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|,
name|ExtractedTextCache
name|extractedTextCache
parameter_list|,
name|IndexAugmentorFactory
name|augmentorFactory
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|definitionBuilder
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|indexCopier
operator|=
name|indexCopier
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
name|this
operator|.
name|extractedTextCache
operator|=
name|extractedTextCache
expr_stmt|;
name|this
operator|.
name|augmentorFactory
operator|=
name|augmentorFactory
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
name|this
operator|.
name|facetsConfig
operator|=
name|FacetHelper
operator|.
name|getFacetsConfig
argument_list|(
name|definition
argument_list|)
expr_stmt|;
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
name|IndexWriterConfig
name|config
decl_stmt|;
if|if
condition|(
name|indexCopier
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
name|indexCopier
operator|.
name|wrapForWrite
argument_list|(
name|definition
argument_list|,
name|directory
argument_list|,
name|reindex
argument_list|)
expr_stmt|;
name|config
operator|=
name|getIndexWriterConfig
argument_list|(
name|definition
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|config
operator|=
name|getIndexWriterConfig
argument_list|(
name|definition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
specifier|private
specifier|static
name|void
name|trackIndexSizeInfo
parameter_list|(
annotation|@
name|Nonnull
name|IndexWriter
name|writer
parameter_list|,
annotation|@
name|Nonnull
name|IndexDefinition
name|definition
parameter_list|,
annotation|@
name|Nonnull
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotNull
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|definition
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|int
name|docs
init|=
name|writer
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|int
name|ram
init|=
name|writer
operator|.
name|numRamDocs
argument_list|()
decl_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"Writer for direcory {} - docs: {}, ramDocs: {}"
argument_list|,
name|definition
argument_list|,
name|docs
argument_list|,
name|ram
argument_list|)
expr_stmt|;
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|long
name|overallSize
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|files
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|f
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|long
name|size
init|=
name|directory
operator|.
name|fileLength
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|overallSize
operator|+=
name|size
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"--"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|trace
argument_list|(
literal|"Directory overall size: {}, files: {}"
argument_list|,
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
argument_list|(
name|overallSize
argument_list|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|boolean
name|updateSuggestions
init|=
name|shouldUpdateSuggestions
argument_list|()
decl_stmt|;
name|boolean
name|forcedUpdateSuggester
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
operator|&&
name|updateSuggestions
condition|)
block|{
name|forcedUpdateSuggester
operator|=
literal|true
expr_stmt|;
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
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|trackIndexSizeInfo
argument_list|(
name|writer
argument_list|,
name|definition
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|start
init|=
name|PERF_LOGGER
operator|.
name|start
argument_list|()
decl_stmt|;
if|if
condition|(
name|updateSuggestions
condition|)
block|{
name|updateSuggester
argument_list|(
name|writer
operator|.
name|getAnalyzer
argument_list|()
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
literal|"Completed suggester for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
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
literal|"Closed writer for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
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
literal|"Closed directory for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|forcedUpdateSuggester
condition|)
block|{
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
name|getCalendar
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
name|PERF_LOGGER
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Overall Closed IndexWriter for directory {}"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|forcedUpdateSuggester
condition|)
block|{
name|textExtractionStats
operator|.
name|log
argument_list|(
name|reindex
argument_list|)
expr_stmt|;
name|textExtractionStats
operator|.
name|collectStats
argument_list|(
name|extractedTextCache
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * eventually update suggest dictionary      * @throws IOException if suggest dictionary update fails      * @param analyzer the analyzer used to update the suggester      */
specifier|private
name|void
name|updateSuggester
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
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
specifier|final
name|OakDirectory
name|suggestDirectory
init|=
operator|new
name|OakDirectory
argument_list|(
name|definitionBuilder
argument_list|,
literal|":suggest-data"
argument_list|,
name|definition
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
name|suggestDirectory
argument_list|,
name|analyzer
argument_list|,
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
name|getCalendar
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
name|suggestDirectory
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|shouldUpdateSuggestions
parameter_list|()
block|{
name|boolean
name|updateSuggestions
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isSuggestEnabled
argument_list|()
condition|)
block|{
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
name|getCalendar
argument_list|()
operator|.
name|after
argument_list|(
name|suggesterLastUpdatedTime
argument_list|)
condition|)
block|{
name|updateSuggestions
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|updateSuggestions
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|updateSuggestions
return|;
block|}
comment|/** Only set for testing */
specifier|static
name|void
name|setClock
parameter_list|(
name|Clock
name|c
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|clock
operator|=
name|c
expr_stmt|;
block|}
specifier|static
specifier|private
name|Calendar
name|getCalendar
parameter_list|()
block|{
name|Calendar
name|ret
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|ret
operator|.
name|setTime
argument_list|(
name|clock
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
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
comment|//Refresh the index definition based on update builder state
name|definition
operator|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|definitionBuilder
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
operator|new
name|ParseContext
argument_list|()
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
name|FacetsConfig
name|getFacetsConfig
parameter_list|()
block|{
return|return
name|facetsConfig
return|;
block|}
annotation|@
name|Deprecated
specifier|public
name|void
name|recordTextExtractionStats
parameter_list|(
name|long
name|timeInMillis
parameter_list|,
name|long
name|bytesRead
parameter_list|)
block|{
comment|//Keeping deprecated method to avoid major version change
name|recordTextExtractionStats
argument_list|(
name|timeInMillis
argument_list|,
name|bytesRead
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recordTextExtractionStats
parameter_list|(
name|long
name|timeInMillis
parameter_list|,
name|long
name|bytesRead
parameter_list|,
name|int
name|textLength
parameter_list|)
block|{
name|textExtractionStats
operator|.
name|addStats
argument_list|(
name|timeInMillis
argument_list|,
name|bytesRead
argument_list|,
name|textLength
argument_list|)
expr_stmt|;
block|}
name|ExtractedTextCache
name|getExtractedTextCache
parameter_list|()
block|{
return|return
name|extractedTextCache
return|;
block|}
name|IndexAugmentorFactory
name|getAugmentorFactory
parameter_list|()
block|{
return|return
name|augmentorFactory
return|;
block|}
specifier|public
name|boolean
name|isReindex
parameter_list|()
block|{
return|return
name|reindex
return|;
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
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|long
name|totalBytesRead
decl_stmt|;
specifier|private
name|long
name|totalTime
decl_stmt|;
specifier|private
name|long
name|totalTextLength
decl_stmt|;
specifier|public
name|void
name|addStats
parameter_list|(
name|long
name|timeInMillis
parameter_list|,
name|long
name|bytesRead
parameter_list|,
name|int
name|textLength
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|totalBytesRead
operator|+=
name|bytesRead
expr_stmt|;
name|totalTime
operator|+=
name|timeInMillis
expr_stmt|;
name|totalTextLength
operator|+=
name|textLength
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
specifier|public
name|void
name|collectStats
parameter_list|(
name|ExtractedTextCache
name|cache
parameter_list|)
block|{
name|cache
operator|.
name|addStats
argument_list|(
name|count
argument_list|,
name|totalTime
argument_list|,
name|totalBytesRead
argument_list|,
name|totalTextLength
argument_list|)
expr_stmt|;
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
literal|" %d (Time Taken %s, Bytes Read %s, Extracted text size %s)"
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
name|totalBytesRead
argument_list|)
argument_list|,
name|humanReadableByteCount
argument_list|(
name|totalTextLength
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

