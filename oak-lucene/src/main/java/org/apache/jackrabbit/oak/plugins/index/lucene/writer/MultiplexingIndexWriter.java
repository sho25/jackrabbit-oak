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
name|Map
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
name|Maps
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
name|LuceneIndexDefinition
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
name|spi
operator|.
name|mount
operator|.
name|Mount
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
name|index
operator|.
name|IndexableField
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
name|collect
operator|.
name|Iterables
operator|.
name|concat
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
operator|.
name|stream
import|;
end_import

begin_class
class|class
name|MultiplexingIndexWriter
implements|implements
name|LuceneIndexWriter
block|{
specifier|private
specifier|final
name|MountInfoProvider
name|mountInfoProvider
decl_stmt|;
specifier|private
specifier|final
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexDefinition
name|definition
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|definitionBuilder
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|reindex
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexWriterConfig
name|writerConfig
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Mount
argument_list|,
name|DefaultIndexWriter
argument_list|>
name|writers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|public
name|MultiplexingIndexWriter
parameter_list|(
name|DirectoryFactory
name|directoryFactory
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
name|LuceneIndexDefinition
name|definition
parameter_list|,
name|NodeBuilder
name|definitionBuilder
parameter_list|,
name|boolean
name|reindex
parameter_list|,
name|LuceneIndexWriterConfig
name|writerConfig
parameter_list|)
block|{
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
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
name|reindex
operator|=
name|reindex
expr_stmt|;
name|this
operator|.
name|directoryFactory
operator|=
name|directoryFactory
expr_stmt|;
name|this
operator|.
name|writerConfig
operator|=
name|writerConfig
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
argument_list|(
name|path
argument_list|)
operator|.
name|updateDocument
argument_list|(
name|path
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
name|Mount
name|mount
init|=
name|mountInfoProvider
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|getWriter
argument_list|(
name|mount
argument_list|)
operator|.
name|deleteDocuments
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|//In case of default mount look for other mounts with roots under this path
comment|//Note that one mount cannot be part of another mount
if|if
condition|(
name|mount
operator|.
name|isDefault
argument_list|()
condition|)
block|{
comment|//If any mount falls under given path then delete all documents in that
for|for
control|(
name|Mount
name|m
range|:
name|mountInfoProvider
operator|.
name|getMountsPlacedUnder
argument_list|(
name|path
argument_list|)
control|)
block|{
name|getWriter
argument_list|(
name|m
argument_list|)
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
block|}
block|}
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
comment|// explicitly get writers for mounts which haven't got writers even at close.
comment|// This essentially ensures we respect DefaultIndexWriters#close's intent to
comment|// create empty index even if nothing has been written during re-index.
name|stream
argument_list|(
name|concat
argument_list|(
name|singleton
argument_list|(
name|mountInfoProvider
operator|.
name|getDefaultMount
argument_list|()
argument_list|)
argument_list|,
name|mountInfoProvider
operator|.
name|getNonDefaultMounts
argument_list|()
argument_list|)
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|filter
argument_list|(
name|m
lambda|->
name|reindex
operator|&&
operator|!
name|m
operator|.
name|isReadOnly
argument_list|()
argument_list|)
comment|// only needed when re-indexing for read-write mounts.
comment|// reindex for ro-mount doesn't make sense in this case anyway.
operator|.
name|forEach
argument_list|(
name|m
lambda|->
name|getWriter
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
comment|// open default writers for mounts that passed all our tests
name|boolean
name|indexUpdated
init|=
literal|false
decl_stmt|;
for|for
control|(
name|LuceneIndexWriter
name|w
range|:
name|writers
operator|.
name|values
argument_list|()
control|)
block|{
name|indexUpdated
operator||=
name|w
operator|.
name|close
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
block|}
return|return
name|indexUpdated
return|;
block|}
specifier|private
name|LuceneIndexWriter
name|getWriter
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Mount
name|mount
init|=
name|mountInfoProvider
operator|.
name|getMountByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|getWriter
argument_list|(
name|mount
argument_list|)
return|;
block|}
specifier|private
name|DefaultIndexWriter
name|getWriter
parameter_list|(
name|Mount
name|mount
parameter_list|)
block|{
name|DefaultIndexWriter
name|writer
init|=
name|writers
operator|.
name|get
argument_list|(
name|mount
argument_list|)
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
name|createWriter
argument_list|(
name|mount
argument_list|)
expr_stmt|;
name|writers
operator|.
name|put
argument_list|(
name|mount
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
specifier|private
name|DefaultIndexWriter
name|createWriter
parameter_list|(
name|Mount
name|m
parameter_list|)
block|{
name|String
name|dirName
init|=
name|MultiplexersLucene
operator|.
name|getIndexDirName
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|String
name|suggestDirName
init|=
name|MultiplexersLucene
operator|.
name|getSuggestDirName
argument_list|(
name|m
argument_list|)
decl_stmt|;
return|return
operator|new
name|DefaultIndexWriter
argument_list|(
name|definition
argument_list|,
name|definitionBuilder
argument_list|,
name|directoryFactory
argument_list|,
name|dirName
argument_list|,
name|suggestDirName
argument_list|,
name|reindex
argument_list|,
name|writerConfig
argument_list|)
return|;
block|}
block|}
end_class

end_unit

