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
name|ContextAwareCallback
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
name|hybrid
operator|.
name|LocalIndexWriterFactory
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
name|spi
operator|.
name|commit
operator|.
name|CommitContext
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
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
specifier|private
specifier|final
name|LuceneIndexWriterFactory
name|indexWriterFactory
decl_stmt|;
specifier|private
specifier|final
name|IndexTracker
name|indexTracker
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
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
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
annotation|@
name|Nullable
name|IndexAugmentorFactory
name|augmentorFactory
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
argument_list|(
name|indexCopier
argument_list|,
literal|null
argument_list|,
name|extractedTextCache
argument_list|,
name|augmentorFactory
argument_list|,
name|mountInfoProvider
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
annotation|@
name|Nullable
name|IndexTracker
name|indexTracker
parameter_list|,
name|ExtractedTextCache
name|extractedTextCache
parameter_list|,
annotation|@
name|Nullable
name|IndexAugmentorFactory
name|augmentorFactory
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
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
name|indexTracker
operator|=
name|indexTracker
expr_stmt|;
name|this
operator|.
name|extractedTextCache
operator|=
name|extractedTextCache
operator|!=
literal|null
condition|?
name|extractedTextCache
else|:
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|augmentorFactory
operator|=
name|augmentorFactory
expr_stmt|;
name|this
operator|.
name|indexWriterFactory
operator|=
operator|new
name|DefaultIndexWriterFactory
argument_list|(
name|checkNotNull
argument_list|(
name|mountInfoProvider
argument_list|)
argument_list|,
name|indexCopier
argument_list|)
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
name|checkArgument
argument_list|(
name|callback
operator|instanceof
name|ContextAwareCallback
argument_list|,
literal|"callback instance not of type "
operator|+
literal|"ContextAwareCallback [%s]"
argument_list|,
name|callback
argument_list|)
expr_stmt|;
name|IndexingContext
name|indexingContext
init|=
operator|(
operator|(
name|ContextAwareCallback
operator|)
name|callback
operator|)
operator|.
name|getIndexingContext
argument_list|()
decl_stmt|;
name|LuceneIndexWriterFactory
name|writerFactory
init|=
name|indexWriterFactory
decl_stmt|;
name|IndexDefinition
name|indexDefinition
init|=
literal|null
decl_stmt|;
name|boolean
name|asyncIndexing
init|=
literal|true
decl_stmt|;
if|if
condition|(
operator|!
name|indexingContext
operator|.
name|isAsync
argument_list|()
operator|&&
name|IndexDefinition
operator|.
name|supportsSyncOrNRTIndexing
argument_list|(
name|definition
argument_list|)
condition|)
block|{
comment|//Would not participate in reindexing. Only interested in
comment|//incremental indexing
if|if
condition|(
name|indexingContext
operator|.
name|isReindexing
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|indexingContext
operator|.
name|getCommitInfo
argument_list|()
operator|.
name|getInfo
argument_list|()
operator|.
name|containsKey
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
condition|)
block|{
comment|//Logically there should not be any commit without commit context. But
comment|//some initializer code does the commit with out it. So ignore such calls with
comment|//warning now
comment|//TODO Revisit use of warn level once all such cases are analyzed
name|log
operator|.
name|warn
argument_list|(
literal|"No CommitContext found for commit"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|//TODO Also check if index has been done once
name|writerFactory
operator|=
operator|new
name|LocalIndexWriterFactory
argument_list|(
name|indexingContext
argument_list|)
expr_stmt|;
comment|//IndexDefinition from tracker might differ from one passed here for reindexing
comment|//case which should be fine. However reusing existing definition would avoid
comment|//creating definition instance for each commit as this gets executed for each commit
if|if
condition|(
name|indexTracker
operator|!=
literal|null
condition|)
block|{
name|indexDefinition
operator|=
name|indexTracker
operator|.
name|getIndexDefinition
argument_list|(
name|indexingContext
operator|.
name|getIndexPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Pass on a read only builder to ensure that nothing gets written
comment|//at all to NodeStore for local indexing.
comment|//TODO [hybrid] This would cause issue with Facets as for faceted fields
comment|//some stuff gets written to NodeBuilder. That logic should be refactored
comment|//to be moved to LuceneIndexWriter
name|definition
operator|=
operator|new
name|ReadOnlyBuilder
argument_list|(
name|definition
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|asyncIndexing
operator|=
literal|false
expr_stmt|;
block|}
name|LuceneIndexEditorContext
name|context
init|=
operator|new
name|LuceneIndexEditorContext
argument_list|(
name|root
argument_list|,
name|definition
argument_list|,
name|indexDefinition
argument_list|,
name|callback
argument_list|,
name|writerFactory
argument_list|,
name|extractedTextCache
argument_list|,
name|augmentorFactory
argument_list|,
name|asyncIndexing
argument_list|)
decl_stmt|;
return|return
operator|new
name|LuceneIndexEditor
argument_list|(
name|context
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

