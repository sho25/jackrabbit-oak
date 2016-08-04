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
name|reader
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
name|ImmutableList
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
name|SUGGEST_DATA_CHILD_NAME
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultIndexReaderFactory
implements|implements
name|LuceneIndexReaderFactory
block|{
specifier|private
specifier|final
name|IndexCopier
name|cloner
decl_stmt|;
specifier|public
name|DefaultIndexReaderFactory
parameter_list|(
annotation|@
name|Nullable
name|IndexCopier
name|cloner
parameter_list|)
block|{
name|this
operator|.
name|cloner
operator|=
name|cloner
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|LuceneIndexReader
argument_list|>
name|createReaders
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|NodeState
name|defnState
parameter_list|,
name|String
name|indexPath
parameter_list|)
throws|throws
name|IOException
block|{
name|LuceneIndexReader
name|reader
init|=
name|createReader
argument_list|(
name|definition
argument_list|,
name|defnState
argument_list|,
name|indexPath
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|SUGGEST_DATA_CHILD_NAME
argument_list|)
decl_stmt|;
return|return
name|reader
operator|!=
literal|null
condition|?
name|ImmutableList
operator|.
name|of
argument_list|(
name|reader
argument_list|)
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
annotation|@
name|CheckForNull
specifier|private
name|LuceneIndexReader
name|createReader
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|NodeState
name|defnNodeState
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|String
name|indexDataNodeName
parameter_list|,
name|String
name|suggestDataNodeName
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
name|NodeState
name|data
init|=
name|defnNodeState
operator|.
name|getChildNode
argument_list|(
name|indexDataNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|exists
argument_list|()
condition|)
block|{
name|directory
operator|=
operator|new
name|OakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|defnNodeState
argument_list|)
argument_list|,
name|indexDataNodeName
argument_list|,
name|definition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|cloner
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
name|cloner
operator|.
name|wrapForRead
argument_list|(
name|indexPath
argument_list|,
name|definition
argument_list|,
name|directory
argument_list|,
name|indexDataNodeName
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|PERSISTENCE_FILE
operator|.
name|equalsIgnoreCase
argument_list|(
name|defnNodeState
operator|.
name|getString
argument_list|(
name|PERSISTENCE_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|path
init|=
name|defnNodeState
operator|.
name|getString
argument_list|(
name|PERSISTENCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|directory
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|OakDirectory
name|suggestDirectory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isSuggestEnabled
argument_list|()
condition|)
block|{
name|suggestDirectory
operator|=
operator|new
name|OakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|defnNodeState
argument_list|)
argument_list|,
name|suggestDataNodeName
argument_list|,
name|definition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|LuceneIndexReader
name|reader
init|=
operator|new
name|DefaultIndexReader
argument_list|(
name|directory
argument_list|,
name|suggestDirectory
argument_list|,
name|definition
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
comment|// closed in LuceneIndexReader.close()
return|return
name|reader
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

