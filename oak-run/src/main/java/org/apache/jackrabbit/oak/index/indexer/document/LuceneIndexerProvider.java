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
name|index
operator|.
name|IndexHelper
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
name|index
operator|.
name|IndexerSupport
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
name|LuceneIndexWriterFactory
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
name|directory
operator|.
name|FSDirectoryFactory
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
name|DefaultIndexWriterFactory
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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|ExtractedTextCache
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
name|search
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
name|search
operator|.
name|spi
operator|.
name|binary
operator|.
name|FulltextBinaryTextExtractor
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|TYPE_LUCENE
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexerProvider
implements|implements
name|NodeStateIndexerProvider
block|{
specifier|private
specifier|final
name|ExtractedTextCache
name|textCache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
name|FileUtils
operator|.
name|ONE_MB
operator|*
literal|5
argument_list|,
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toSeconds
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexHelper
name|indexHelper
decl_stmt|;
specifier|private
specifier|final
name|DirectoryFactory
name|dirFactory
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexWriterFactory
name|indexWriterFactory
decl_stmt|;
specifier|public
name|LuceneIndexerProvider
parameter_list|(
name|IndexHelper
name|indexHelper
parameter_list|,
name|IndexerSupport
name|indexerSupport
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexHelper
operator|=
name|indexHelper
expr_stmt|;
name|this
operator|.
name|dirFactory
operator|=
operator|new
name|FSDirectoryFactory
argument_list|(
name|indexerSupport
operator|.
name|getLocalIndexDir
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexWriterFactory
operator|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|indexHelper
operator|.
name|getMountInfoProvider
argument_list|()
argument_list|,
name|dirFactory
argument_list|,
name|indexHelper
operator|.
name|getLuceneIndexHelper
argument_list|()
operator|.
name|getWriterConfigForReindex
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStateIndexer
name|getIndexer
parameter_list|(
annotation|@
name|NotNull
name|String
name|type
parameter_list|,
annotation|@
name|NotNull
name|String
name|indexPath
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
name|IndexingProgressReporter
name|progressReporter
parameter_list|)
block|{
if|if
condition|(
operator|!
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|definition
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|IndexDefinition
name|idxDefinition
init|=
name|IndexDefinition
operator|.
name|newBuilder
argument_list|(
name|root
argument_list|,
name|definition
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|indexPath
argument_list|)
operator|.
name|reindex
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|LuceneIndexWriter
name|indexWriter
init|=
name|indexWriterFactory
operator|.
name|newInstance
argument_list|(
name|idxDefinition
argument_list|,
name|definition
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FulltextBinaryTextExtractor
name|textExtractor
init|=
operator|new
name|FulltextBinaryTextExtractor
argument_list|(
name|textCache
argument_list|,
name|idxDefinition
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|LuceneIndexer
argument_list|(
name|idxDefinition
argument_list|,
name|indexWriter
argument_list|,
name|definition
argument_list|,
name|textExtractor
argument_list|,
name|progressReporter
argument_list|)
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
block|{      }
block|}
end_class

end_unit

