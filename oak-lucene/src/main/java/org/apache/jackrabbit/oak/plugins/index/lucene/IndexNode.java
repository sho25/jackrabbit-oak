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
name|Collections
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|Iterables
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
name|hybrid
operator|.
name|NRTIndex
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
name|hybrid
operator|.
name|NRTIndexFactory
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
name|hybrid
operator|.
name|ReaderRefreshPolicy
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
name|reader
operator|.
name|LuceneIndexReaderFactory
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
name|MultiReader
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
name|IndexSearcher
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
name|IndexNode
block|{
specifier|static
name|IndexNode
name|open
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|defnNodeState
parameter_list|,
name|LuceneIndexReaderFactory
name|readerFactory
parameter_list|,
annotation|@
name|Nullable
name|NRTIndexFactory
name|nrtFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexDefinition
name|definition
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defnNodeState
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
init|=
name|readerFactory
operator|.
name|createReaders
argument_list|(
name|definition
argument_list|,
name|defnNodeState
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|NRTIndex
name|nrtIndex
init|=
name|nrtFactory
operator|!=
literal|null
condition|?
name|nrtFactory
operator|.
name|createIndex
argument_list|(
name|definition
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|readers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|IndexNode
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|indexPath
argument_list|)
argument_list|,
name|definition
argument_list|,
name|readers
argument_list|,
name|nrtIndex
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
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
name|IndexNode
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|volatile
name|IndexSearcher
name|indexSearcher
decl_stmt|;
specifier|private
specifier|final
name|NRTIndex
name|nrtIndex
decl_stmt|;
specifier|private
specifier|final
name|ReaderRefreshPolicy
name|refreshPolicy
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|refreshCallback
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|refreshReaders
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
name|IndexNode
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexDefinition
name|definition
parameter_list|,
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|readers
parameter_list|,
annotation|@
name|Nullable
name|NRTIndex
name|nrtIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|checkArgument
argument_list|(
operator|!
name|readers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|readers
operator|=
name|readers
expr_stmt|;
name|this
operator|.
name|nrtIndex
operator|=
name|nrtIndex
expr_stmt|;
name|this
operator|.
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|createReader
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|refreshPolicy
operator|=
name|nrtIndex
operator|!=
literal|null
condition|?
name|nrtIndex
operator|.
name|getRefreshPolicy
argument_list|()
else|:
name|ReaderRefreshPolicy
operator|.
name|NEVER
expr_stmt|;
block|}
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
name|IndexDefinition
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
specifier|public
name|IndexSearcher
name|getSearcher
parameter_list|()
block|{
return|return
name|indexSearcher
return|;
block|}
name|Directory
name|getSuggestDirectory
parameter_list|()
block|{
return|return
name|getDefaultReader
argument_list|()
operator|.
name|getSuggestDirectory
argument_list|()
return|;
block|}
name|AnalyzingInfixSuggester
name|getLookup
parameter_list|()
block|{
return|return
name|getDefaultReader
argument_list|()
operator|.
name|getLookup
argument_list|()
return|;
block|}
name|boolean
name|acquire
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|closed
condition|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|refreshPolicy
operator|.
name|refreshOnReadIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
specifier|public
name|void
name|release
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|//Do not close the NRTIndex here as it might be in use
comment|//by newer IndexNode. Just close the readers obtained from
comment|//them
for|for
control|(
name|LuceneIndexReader
name|reader
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|readers
argument_list|,
name|getNRTReaders
argument_list|()
argument_list|)
control|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
name|LuceneIndexWriter
name|getLocalWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nrtIndex
operator|!=
literal|null
condition|?
name|nrtIndex
operator|.
name|getWriter
argument_list|()
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|refreshReadersOnWriteIfRequired
parameter_list|()
block|{
name|refreshPolicy
operator|.
name|refreshOnWriteIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|refreshReaders
parameter_list|()
block|{
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|createReader
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Refreshed reader for index [{}]"
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
specifier|private
name|LuceneIndexReader
name|getDefaultReader
parameter_list|()
block|{
comment|//TODO This is still required to support Suggester, Spellcheck etc OAK-4643
return|return
name|readers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
name|IndexReader
name|createReader
parameter_list|()
block|{
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|nrtReaders
init|=
name|getNRTReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|readers
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|nrtReaders
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|readers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getReader
argument_list|()
return|;
block|}
name|IndexReader
index|[]
name|readerArr
init|=
operator|new
name|IndexReader
index|[
name|readers
operator|.
name|size
argument_list|()
operator|+
name|nrtReaders
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LuceneIndexReader
name|r
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|readers
argument_list|,
name|nrtReaders
argument_list|)
control|)
block|{
name|readerArr
index|[
name|i
operator|++
index|]
operator|=
name|r
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|MultiReader
argument_list|(
name|readerArr
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|getNRTReaders
parameter_list|()
block|{
return|return
name|nrtIndex
operator|!=
literal|null
condition|?
name|nrtIndex
operator|.
name|getReaders
argument_list|()
else|:
name|Collections
operator|.
expr|<
name|LuceneIndexReader
operator|>
name|emptyList
argument_list|()
return|;
block|}
block|}
end_class

end_unit

