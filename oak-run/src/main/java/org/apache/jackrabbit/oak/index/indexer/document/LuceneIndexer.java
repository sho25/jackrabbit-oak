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
operator|.
name|indexer
operator|.
name|document
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
name|plugins
operator|.
name|document
operator|.
name|NodeDocument
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
name|PathFilter
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
name|IndexDefinition
operator|.
name|IndexingRule
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
name|LuceneDocumentMaker
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
name|binary
operator|.
name|BinaryTextExtractor
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
name|plugins
operator|.
name|index
operator|.
name|progress
operator|.
name|IndexingProgressReporter
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
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexer
implements|implements
name|NodeStateIndexer
block|{
specifier|private
specifier|final
name|IndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|BinaryTextExtractor
name|binaryTextExtractor
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definitionBuilder
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexWriter
name|indexWriter
decl_stmt|;
specifier|private
specifier|final
name|IndexingProgressReporter
name|progressReporter
decl_stmt|;
specifier|public
name|LuceneIndexer
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|LuceneIndexWriter
name|indexWriter
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|BinaryTextExtractor
name|binaryTextExtractor
parameter_list|,
name|IndexingProgressReporter
name|progressReporter
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
name|binaryTextExtractor
operator|=
name|binaryTextExtractor
expr_stmt|;
name|this
operator|.
name|indexWriter
operator|=
name|indexWriter
expr_stmt|;
name|this
operator|.
name|definitionBuilder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|progressReporter
operator|=
name|progressReporter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldInclude
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|definition
operator|.
name|getPathFilter
argument_list|()
operator|.
name|filter
argument_list|(
name|path
argument_list|)
operator|!=
name|PathFilter
operator|.
name|Result
operator|.
name|EXCLUDE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldInclude
parameter_list|(
name|NodeDocument
name|doc
parameter_list|)
block|{
comment|//TODO possible optimization for NodeType based filtering
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|(
name|NodeStateEntry
name|entry
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|IndexingRule
name|indexingRule
init|=
name|definition
operator|.
name|getApplicableIndexingRule
argument_list|(
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexingRule
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|LuceneDocumentMaker
name|maker
init|=
name|newDocumentMaker
argument_list|(
name|indexingRule
argument_list|,
name|entry
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|maker
operator|.
name|makeDocument
argument_list|(
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|writeToIndex
argument_list|(
name|doc
argument_list|,
name|entry
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|progressReporter
operator|.
name|indexUpdate
argument_list|(
name|definition
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|indexWriter
operator|.
name|close
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeToIndex
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|updateDocument
argument_list|(
name|path
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|private
name|LuceneDocumentMaker
name|newDocumentMaker
parameter_list|(
name|IndexingRule
name|indexingRule
parameter_list|,
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|LuceneDocumentMaker
argument_list|(
name|binaryTextExtractor
argument_list|,
parameter_list|()
lambda|->
name|FacetHelper
operator|.
name|getFacetsConfig
argument_list|(
name|definitionBuilder
argument_list|)
argument_list|,
comment|//TODO FacetsConfig handling
literal|null
argument_list|,
comment|//TODO augmentorFactory
name|definition
argument_list|,
name|indexingRule
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

