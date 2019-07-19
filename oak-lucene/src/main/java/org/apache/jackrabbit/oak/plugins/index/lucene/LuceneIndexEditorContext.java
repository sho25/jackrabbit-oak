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
name|IndexingContext
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
name|FacetsConfigProvider
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
name|editor
operator|.
name|FulltextIndexEditorContext
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
name|editor
operator|.
name|FulltextIndexWriterFactory
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexEditorContext
extends|extends
name|FulltextIndexEditorContext
implements|implements
name|FacetsConfigProvider
block|{
specifier|private
name|FacetsConfig
name|facetsConfig
decl_stmt|;
specifier|private
specifier|final
name|IndexAugmentorFactory
name|augmentorFactory
decl_stmt|;
name|LuceneIndexEditorContext
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|Nullable
name|IndexDefinition
name|indexDefinition
parameter_list|,
name|IndexUpdateCallback
name|updateCallback
parameter_list|,
name|FulltextIndexWriterFactory
name|indexWriterFactory
parameter_list|,
name|ExtractedTextCache
name|extractedTextCache
parameter_list|,
name|IndexAugmentorFactory
name|augmentorFactory
parameter_list|,
name|IndexingContext
name|indexingContext
parameter_list|,
name|boolean
name|asyncIndexing
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|definition
argument_list|,
name|indexDefinition
argument_list|,
name|updateCallback
argument_list|,
name|indexWriterFactory
argument_list|,
name|extractedTextCache
argument_list|,
name|indexingContext
argument_list|,
name|asyncIndexing
argument_list|)
expr_stmt|;
name|this
operator|.
name|augmentorFactory
operator|=
name|augmentorFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexDefinition
operator|.
name|Builder
name|newDefinitionBuilder
parameter_list|()
block|{
return|return
operator|new
name|LuceneIndexDefinition
operator|.
name|Builder
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|LuceneDocumentMaker
name|newDocumentMaker
parameter_list|(
name|IndexDefinition
operator|.
name|IndexingRule
name|rule
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|//Faceting is only enabled for async mode
name|FacetsConfigProvider
name|facetsConfigProvider
init|=
name|isAsyncIndexing
argument_list|()
condition|?
name|this
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|LuceneDocumentMaker
argument_list|(
name|getTextExtractor
argument_list|()
argument_list|,
name|facetsConfigProvider
argument_list|,
name|augmentorFactory
argument_list|,
name|definition
argument_list|,
name|rule
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|LuceneIndexWriter
name|getWriter
parameter_list|()
block|{
return|return
operator|(
name|LuceneIndexWriter
operator|)
name|super
operator|.
name|getWriter
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|FacetsConfig
name|getFacetsConfig
parameter_list|()
block|{
if|if
condition|(
name|facetsConfig
operator|==
literal|null
condition|)
block|{
name|facetsConfig
operator|=
name|FacetHelper
operator|.
name|getFacetsConfig
argument_list|(
name|definitionBuilder
argument_list|)
expr_stmt|;
block|}
return|return
name|facetsConfig
return|;
block|}
comment|/** Only set for testing      * @param c clock      * */
specifier|public
specifier|static
name|void
name|setClock
parameter_list|(
name|Clock
name|c
parameter_list|)
block|{
name|FulltextIndexEditorContext
operator|.
name|setClock
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

