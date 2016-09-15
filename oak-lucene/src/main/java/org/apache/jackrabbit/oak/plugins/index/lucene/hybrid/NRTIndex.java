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
name|hybrid
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
name|java
operator|.
name|util
operator|.
name|List
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|reader
operator|.
name|LuceneIndexReader
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
name|IndexWriterUtils
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
name|LuceneIndexWriter
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
name|IndexReader
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
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
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
name|NRTCachingDirectory
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
name|checkState
import|;
end_import

begin_class
specifier|public
class|class
name|NRTIndex
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|COUNTER
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
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
name|NRTIndex
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Prefix used for naming the directory created for NRT indexes      */
specifier|public
specifier|static
specifier|final
name|String
name|NRT_DIR_PREFIX
init|=
literal|"nrt-"
decl_stmt|;
specifier|private
specifier|final
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexReader
name|previousReader
decl_stmt|;
specifier|private
specifier|final
name|ReaderRefreshPolicy
name|refreshPolicy
decl_stmt|;
specifier|private
name|IndexWriter
name|indexWriter
decl_stmt|;
specifier|private
name|NRTIndexWriter
name|nrtIndexWriter
decl_stmt|;
specifier|private
name|File
name|indexDir
decl_stmt|;
specifier|private
name|Directory
name|directory
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|public
name|NRTIndex
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|IndexCopier
name|indexCopier
parameter_list|,
name|ReaderRefreshPolicy
name|refreshPolicy
parameter_list|,
annotation|@
name|Nullable
name|NRTIndex
name|previous
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
name|indexCopier
operator|=
name|indexCopier
expr_stmt|;
name|this
operator|.
name|refreshPolicy
operator|=
name|refreshPolicy
expr_stmt|;
name|this
operator|.
name|previousReader
operator|=
name|previous
operator|!=
literal|null
condition|?
name|previous
operator|.
name|getPrimaryReader
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
annotation|@
name|CheckForNull
name|LuceneIndexReader
name|getPrimaryReader
parameter_list|()
block|{
return|return
name|createReader
argument_list|()
return|;
block|}
specifier|public
name|LuceneIndexWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
if|if
condition|(
name|nrtIndexWriter
operator|==
literal|null
condition|)
block|{
name|nrtIndexWriter
operator|=
name|createWriter
argument_list|()
expr_stmt|;
block|}
return|return
name|nrtIndexWriter
return|;
block|}
specifier|public
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|getReaders
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|LuceneIndexReader
name|latestReader
init|=
name|createReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|latestReader
operator|!=
literal|null
condition|)
block|{
name|readers
operator|.
name|add
argument_list|(
name|latestReader
argument_list|)
expr_stmt|;
block|}
comment|//Old reader should be added later
if|if
condition|(
name|previousReader
operator|!=
literal|null
condition|)
block|{
name|readers
operator|.
name|add
argument_list|(
name|previousReader
argument_list|)
expr_stmt|;
block|}
return|return
name|readers
return|;
block|}
specifier|public
name|ReaderRefreshPolicy
name|getRefreshPolicy
parameter_list|()
block|{
return|return
name|refreshPolicy
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
comment|//TODO Close call can possibly be speeded up by
comment|//avoiding merge and dropping stuff in memory. To be explored
comment|//indexWrite.close(waitForMerges)
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"[{}] Removed directory [{}]"
argument_list|,
name|this
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
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
name|definition
operator|.
name|getIndexPathFromConfig
argument_list|()
return|;
block|}
comment|//For test
name|File
name|getIndexDir
parameter_list|()
block|{
return|return
name|indexDir
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|LuceneIndexReader
name|createReader
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
comment|//Its possible that readers are obtained
comment|//before anything gets indexed
if|if
condition|(
name|indexWriter
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
comment|//applyDeletes is false as layers above would take care of
comment|//stale result
return|return
operator|new
name|NRTReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error opening index [{}]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|synchronized
name|NRTIndexWriter
name|createWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|dirName
init|=
name|generateDirName
argument_list|()
decl_stmt|;
name|indexDir
operator|=
name|indexCopier
operator|.
name|getIndexDir
argument_list|(
name|definition
argument_list|,
name|definition
operator|.
name|getIndexPathFromConfig
argument_list|()
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
name|Directory
name|fsdir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
comment|//TODO make these configurable
name|directory
operator|=
operator|new
name|NRTCachingDirectory
argument_list|(
name|fsdir
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
name|IndexWriterUtils
operator|.
name|getIndexWriterConfig
argument_list|(
name|definition
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
operator|new
name|NRTIndexWriter
argument_list|(
name|indexWriter
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|generateDirName
parameter_list|()
block|{
name|long
name|uniqueCount
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|COUNTER
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
return|return
name|NRT_DIR_PREFIX
operator|+
name|uniqueCount
return|;
block|}
specifier|private
specifier|static
class|class
name|NRTReader
implements|implements
name|LuceneIndexReader
block|{
specifier|private
specifier|final
name|IndexReader
name|indexReader
decl_stmt|;
specifier|public
name|NRTReader
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
block|{
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexReader
name|getReader
parameter_list|()
block|{
return|return
name|indexReader
return|;
block|}
annotation|@
name|Override
specifier|public
name|AnalyzingInfixSuggester
name|getLookup
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Directory
name|getSuggestDirectory
parameter_list|()
block|{
return|return
literal|null
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
block|{          }
block|}
specifier|private
class|class
name|NRTIndexWriter
implements|implements
name|LuceneIndexWriter
block|{
specifier|private
specifier|final
name|IndexWriter
name|indexWriter
decl_stmt|;
specifier|public
name|NRTIndexWriter
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|)
block|{
name|this
operator|.
name|indexWriter
operator|=
name|indexWriter
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
comment|//For NRT case documents are never updated
comment|//instead they are just added. This would cause duplicates
comment|//That should be taken care at query side via unique cursor
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|refreshPolicy
operator|.
name|updated
argument_list|()
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
comment|//Do not delete documents. Query side would handle it
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Close should not be called"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

