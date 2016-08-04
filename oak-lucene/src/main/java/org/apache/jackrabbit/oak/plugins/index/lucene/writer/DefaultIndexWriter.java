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
name|writer
package|;
end_package

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
name|IndexableField
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
name|search
operator|.
name|PrefixQuery
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|SUGGEST_DATA_CHILD_NAME
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
name|TermFactory
operator|.
name|newPathTerm
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
name|IndexWriterUtils
operator|.
name|getIndexWriterConfig
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
name|IndexWriterUtils
operator|.
name|newIndexDirectory
import|;
end_import

begin_class
class|class
name|DefaultIndexWriter
implements|implements
name|LuceneIndexWriter
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
name|DefaultIndexWriter
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
name|LuceneIndexWriter
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
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definitionBuilder
decl_stmt|;
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
specifier|final
name|String
name|dirName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|reindex
decl_stmt|;
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
specifier|private
name|Directory
name|directory
decl_stmt|;
specifier|public
name|DefaultIndexWriter
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|NodeBuilder
name|definitionBuilder
parameter_list|,
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|,
name|String
name|dirName
parameter_list|,
name|boolean
name|reindex
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|definitionBuilder
operator|=
name|definitionBuilder
expr_stmt|;
name|this
operator|.
name|indexCopier
operator|=
name|indexCopier
expr_stmt|;
name|this
operator|.
name|dirName
operator|=
name|dirName
expr_stmt|;
name|this
operator|.
name|reindex
operator|=
name|reindex
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|getWriter
argument_list|()
operator|.
name|updateDocument
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|getWriter
argument_list|()
operator|.
name|deleteDocuments
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|getWriter
argument_list|()
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|getWriter
argument_list|()
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|close
parameter_list|(
name|long
name|timestamp
parameter_list|)
throws|throws
name|IOException
block|{
comment|//If reindex or fresh index and write is null on close
comment|//it indicates that the index is empty. In such a case trigger
comment|//creation of write such that an empty Lucene index state is persisted
comment|//in directory
name|boolean
name|indexUpdated
init|=
literal|false
decl_stmt|;
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
name|Calendar
name|currentTime
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|currentTime
operator|.
name|setTimeInMillis
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|boolean
name|updateSuggestions
init|=
name|shouldUpdateSuggestions
argument_list|(
name|currentTime
argument_list|)
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
name|log
operator|.
name|debug
argument_list|(
literal|"Would update suggester dictionary although no index changes were detected in current cycle"
argument_list|)
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
name|indexUpdated
operator|=
literal|true
expr_stmt|;
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
argument_list|,
name|currentTime
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
block|}
return|return
name|indexUpdated
return|;
block|}
comment|//~----------------------------------------< internal>
specifier|private
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
argument_list|,
name|dirName
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
argument_list|,
name|dirName
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
comment|/**      * eventually update suggest dictionary      * @throws IOException if suggest dictionary update fails      * @param analyzer the analyzer used to update the suggester      */
specifier|private
name|void
name|updateSuggester
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Calendar
name|currentTime
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
name|SUGGEST_DATA_CHILD_NAME
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
name|SUGGEST_DATA_CHILD_NAME
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
name|currentTime
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
comment|/**      * Checks if last suggestion build time was done sufficiently in the past AND that there were non-zero indexedNodes      * stored in the last run. Note, if index is updated only to rebuild suggestions, even then we update indexedNodes,      * which would be zero in case it was a forced update of suggestions.      * @return is suggest dict should be updated      */
specifier|private
name|boolean
name|shouldUpdateSuggestions
parameter_list|(
name|Calendar
name|currentTime
parameter_list|)
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
name|SUGGEST_DATA_CHILD_NAME
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|suggesterLastUpdatedValue
operator|!=
literal|null
condition|)
block|{
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
name|Calendar
name|nextSuggestUpdateTime
init|=
operator|(
name|Calendar
operator|)
name|suggesterLastUpdatedTime
operator|.
name|clone
argument_list|()
decl_stmt|;
name|nextSuggestUpdateTime
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
name|currentTime
operator|.
name|after
argument_list|(
name|nextSuggestUpdateTime
argument_list|)
condition|)
block|{
name|updateSuggestions
operator|=
operator|(
name|writer
operator|!=
literal|null
operator|||
name|isIndexUpdatedAfter
argument_list|(
name|suggesterLastUpdatedTime
argument_list|)
operator|)
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
comment|/**      * @return {@code false} if persisted lastUpdated time for index is after {@code calendar}. {@code true} otherwise      */
specifier|private
name|boolean
name|isIndexUpdatedAfter
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
block|{
name|NodeBuilder
name|indexStats
init|=
name|definitionBuilder
operator|.
name|child
argument_list|(
literal|":status"
argument_list|)
decl_stmt|;
name|PropertyState
name|indexLastUpdatedValue
init|=
name|indexStats
operator|.
name|getProperty
argument_list|(
literal|"lastUpdated"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexLastUpdatedValue
operator|!=
literal|null
condition|)
block|{
name|Calendar
name|indexLastUpdatedTime
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|indexLastUpdatedValue
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|indexLastUpdatedTime
operator|.
name|after
argument_list|(
name|calendar
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
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
literal|"Writer for directory {} - docs: {}, ramDocs: {}"
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
block|}
end_class

end_unit

