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
name|checkArgument
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
name|IndexUtils
operator|.
name|getString
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
name|FieldFactory
operator|.
name|newFulltextField
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
name|FieldFactory
operator|.
name|newPathField
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
name|FieldFactory
operator|.
name|newPropertyField
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
name|ANALYZER
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
name|INCLUDE_PROPERTY_TYPES
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
name|INDEX_PATH
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
name|PERSISTENCE_FILE
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
name|PERSISTENCE_NAME
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
name|PERSISTENCE_OAK
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
name|TO_WRITE_LOCK_MS
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
name|TO_MAX_RETRIES
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
name|TO_SLEEP_MS
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
name|util
operator|.
name|HashSet
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
name|TreeSet
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
name|jackrabbit
operator|.
name|JcrConstants
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
name|Blob
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
name|commons
operator|.
name|PathUtils
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
name|aggregation
operator|.
name|AggregatedState
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
name|aggregation
operator|.
name|NodeAggregator
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
name|document
operator|.
name|Document
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
name|LockObtainFailedException
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
name|metadata
operator|.
name|Metadata
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|WriteOutContentHandler
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
class|class
name|LuceneIndexUpdate
implements|implements
name|Closeable
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
name|LuceneIndexUpdate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|()
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
name|ANALYZER
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
name|config
operator|.
name|setWriteLockTimeout
argument_list|(
name|TO_WRITE_LOCK_MS
argument_list|)
expr_stmt|;
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
specifier|final
name|IndexWriterConfig
name|config
init|=
name|getIndexWriterConfig
argument_list|()
decl_stmt|;
comment|/**      * Parser used for extracting text content from binary properties for full      * text indexing.      */
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
comment|/**      * The media types supported by the parser used.      */
specifier|private
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedMediaTypes
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|updates
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|propertyTypes
decl_stmt|;
specifier|private
specifier|final
name|NodeAggregator
name|aggregator
decl_stmt|;
specifier|public
name|LuceneIndexUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|index
parameter_list|,
name|Parser
name|parser
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|propertyTypes
operator|=
name|buildPropertyTypes
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|newIndexWriter
argument_list|(
name|index
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|aggregator
operator|=
operator|new
name|NodeAggregator
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|IndexWriter
name|newIndexWriter
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|type
init|=
name|getString
argument_list|(
name|index
argument_list|,
name|PERSISTENCE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|PERSISTENCE_OAK
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
try|try
block|{
return|return
operator|new
name|IndexWriter
argument_list|(
operator|new
name|ReadWriteOakDirectory
argument_list|(
name|index
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|PERSISTENCE_FILE
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|File
name|f
init|=
name|getIndexChildFolder
argument_list|(
name|getString
argument_list|(
name|index
argument_list|,
name|PERSISTENCE_PATH
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|f
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|INDEX_PATH
argument_list|,
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|d
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|f
argument_list|)
decl_stmt|;
return|return
name|newIndexWriterTO
argument_list|(
name|d
argument_list|,
literal|0
argument_list|,
name|TO_MAX_RETRIES
argument_list|,
name|TO_SLEEP_MS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Unknown lucene persistence setting"
argument_list|)
throw|;
block|}
specifier|private
specifier|static
name|IndexWriter
name|newIndexWriterTO
parameter_list|(
name|Directory
name|d
parameter_list|,
name|int
name|retry
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|sleep
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|config
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|lofe
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to create a new index writer ({}/{}): {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|retry
block|,
name|max
block|,
name|lofe
operator|.
name|getMessage
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|retry
operator|++
expr_stmt|;
if|if
condition|(
name|retry
operator|>
name|max
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to create a new index writer, giving up."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//
block|}
return|return
name|newIndexWriterTO
argument_list|(
name|d
argument_list|,
name|retry
argument_list|,
name|max
argument_list|,
name|sleep
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|File
name|getIndexChildFolder
parameter_list|(
name|String
name|root
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|File
name|rootf
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|root
operator|.
name|startsWith
argument_list|(
literal|".."
argument_list|)
operator|||
name|root
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Index config path should be a descendant of the repository directory."
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|p
range|:
name|root
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
control|)
block|{
name|rootf
operator|=
operator|new
name|File
argument_list|(
name|rootf
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO factor in the 'path' argument to not have overlapping lucene
comment|// index defs
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|String
name|elements
init|=
name|path
decl_stmt|;
if|if
condition|(
name|elements
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|elements
operator|=
name|elements
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|elements
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
control|)
block|{
name|rootf
operator|=
operator|new
name|File
argument_list|(
name|rootf
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|rootf
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|)
decl_stmt|;
name|f
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
specifier|private
name|Set
argument_list|<
name|Integer
argument_list|>
name|buildPropertyTypes
parameter_list|(
name|NodeBuilder
name|index
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|index
operator|.
name|getProperty
argument_list|(
name|INCLUDE_PROPERTY_TYPES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|Integer
argument_list|>
name|includes
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|inc
range|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
comment|// TODO add more types as needed
if|if
condition|(
name|Type
operator|.
name|STRING
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|inc
argument_list|)
condition|)
block|{
name|includes
operator|.
name|add
argument_list|(
name|Type
operator|.
name|STRING
operator|.
name|tag
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|BINARY
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|inc
argument_list|)
condition|)
block|{
name|includes
operator|.
name|add
argument_list|(
name|Type
operator|.
name|STRING
operator|.
name|tag
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|includes
return|;
block|}
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|value
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
comment|// noop
return|return;
block|}
comment|// null value can come from a deleted node, followed by a deleted
comment|// property event which would trigger an update on the previously
comment|// deleted node
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|key
operator|=
literal|"/"
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|key
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|key
operator|=
literal|"/"
operator|+
name|key
expr_stmt|;
block|}
if|if
condition|(
name|updates
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return;
block|}
name|updates
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|newPathTerm
argument_list|(
name|key
argument_list|)
argument_list|,
name|makeDocument
argument_list|(
name|key
argument_list|,
name|value
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
comment|// noop
return|return;
block|}
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|deleteSubtreeWriter
argument_list|(
name|writer
argument_list|,
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|apply
parameter_list|()
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Lucene"
argument_list|,
literal|1
argument_list|,
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|deleteSubtreeWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO verify the removal of the entire sub-hierarchy
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/"
operator|+
name|path
expr_stmt|;
block|}
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|+=
literal|"/"
expr_stmt|;
block|}
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Document
name|makeDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|pname
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isVisible
argument_list|(
name|pname
argument_list|)
operator|&&
name|propertyTypes
operator|.
name|isEmpty
argument_list|()
operator|||
name|propertyTypes
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|BINARY
operator|.
name|tag
argument_list|()
operator|==
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
name|addBinaryValue
argument_list|(
name|document
argument_list|,
name|property
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|document
operator|.
name|add
argument_list|(
name|newPropertyField
argument_list|(
name|pname
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
for|for
control|(
name|AggregatedState
name|agg
range|:
name|aggregator
operator|.
name|getAggregates
argument_list|(
name|state
argument_list|)
control|)
block|{
for|for
control|(
name|PropertyState
name|property
range|:
name|agg
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|pname
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isVisible
argument_list|(
name|pname
argument_list|)
operator|&&
name|propertyTypes
operator|.
name|isEmpty
argument_list|()
operator|||
name|propertyTypes
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|BINARY
operator|.
name|tag
argument_list|()
operator|==
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
name|addBinaryValue
argument_list|(
name|document
argument_list|,
name|property
argument_list|,
name|agg
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|document
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|document
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isVisible
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|':'
return|;
block|}
specifier|private
name|void
name|addBinaryValue
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|String
name|type
init|=
name|getString
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
operator|!
name|isSupportedMediaType
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return;
block|}
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
comment|// jcr:encoding is not mandatory
name|String
name|encoding
init|=
name|getString
argument_list|(
name|state
argument_list|,
name|JcrConstants
operator|.
name|JCR_ENCODING
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Blob
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|parseStringValue
argument_list|(
name|v
argument_list|,
name|metadata
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns<code>true</code> if the provided type is among the types      * supported by the Tika parser we are using.      *       * @param type      *            the type to check.      * @return whether the type is supported by the Tika parser we are using.      */
specifier|private
name|boolean
name|isSupportedMediaType
parameter_list|(
specifier|final
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
name|parser
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
specifier|private
name|String
name|parseStringValue
parameter_list|(
name|Blob
name|v
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|WriteOutContentHandler
name|handler
init|=
operator|new
name|WriteOutContentHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|v
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LinkageError
name|e
parameter_list|)
block|{
comment|// Capture and ignore errors caused by extraction libraries
comment|// not being present. This is equivalent to disabling
comment|// selected media types in configuration, so we can simply
comment|// ignore these errors.
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Capture and report any other full text extraction problems.
comment|// The special STOP exception is used for normal termination.
if|if
condition|(
operator|!
name|handler
operator|.
name|isWriteLimitReached
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Failed to extract text from a binary property."
operator|+
literal|" This is a fairly common case, and nothing to"
operator|+
literal|" worry about. The stack trace is included to"
operator|+
literal|" help improve the text extraction feature."
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return
literal|"TextExtractionError"
return|;
block|}
block|}
return|return
name|handler
operator|.
name|toString
argument_list|()
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
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
block|}
end_class

end_unit

