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
name|index
operator|.
name|IndexEditor
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
name|IndexEditorProvider
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
name|spi
operator|.
name|commit
operator|.
name|Editor
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

begin_comment
comment|/**  * Service that provides Lucene based {@link IndexEditor}s  *   * @see LuceneIndexEditor  * @see IndexEditorProvider  *   */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndexEditorProvider
implements|implements
name|IndexEditorProvider
block|{
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
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
specifier|public
name|LuceneIndexEditorProvider
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneIndexEditorProvider
parameter_list|(
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|)
block|{
comment|//Disable the cache by default in ExtractedTextCache
name|this
argument_list|(
name|indexCopier
argument_list|,
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneIndexEditorProvider
parameter_list|(
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|,
name|ExtractedTextCache
name|extractedTextCache
parameter_list|)
block|{
name|this
argument_list|(
name|indexCopier
argument_list|,
name|extractedTextCache
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneIndexEditorProvider
parameter_list|(
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
name|indexCopier
operator|=
name|indexCopier
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
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
annotation|@
name|Nonnull
name|String
name|type
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|IndexUpdateCallback
name|callback
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
operator|new
name|LuceneIndexEditor
argument_list|(
name|root
argument_list|,
name|definition
argument_list|,
name|callback
argument_list|,
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|indexCopier
argument_list|)
argument_list|,
name|extractedTextCache
argument_list|,
name|augmentorFactory
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
name|IndexCopier
name|getIndexCopier
parameter_list|()
block|{
return|return
name|indexCopier
return|;
block|}
name|ExtractedTextCache
name|getExtractedTextCache
parameter_list|()
block|{
return|return
name|extractedTextCache
return|;
block|}
block|}
end_class

end_unit

