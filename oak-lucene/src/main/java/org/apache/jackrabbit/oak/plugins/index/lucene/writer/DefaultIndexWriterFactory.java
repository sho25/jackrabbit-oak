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
name|LuceneIndexConstants
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
name|DefaultDirectoryFactory
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
name|blob
operator|.
name|GarbageCollectableBlobStore
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

begin_class
specifier|public
class|class
name|DefaultIndexWriterFactory
implements|implements
name|LuceneIndexWriterFactory
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
specifier|public
name|DefaultIndexWriterFactory
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
annotation|@
name|Nullable
name|IndexCopier
name|indexCopier
parameter_list|,
annotation|@
name|Nullable
name|GarbageCollectableBlobStore
name|blobStore
parameter_list|)
block|{
name|this
argument_list|(
name|mountInfoProvider
argument_list|,
operator|new
name|DefaultDirectoryFactory
argument_list|(
name|indexCopier
argument_list|,
name|blobStore
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultIndexWriterFactory
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|,
name|DirectoryFactory
name|directoryFactory
parameter_list|)
block|{
name|this
operator|.
name|mountInfoProvider
operator|=
name|checkNotNull
argument_list|(
name|mountInfoProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|directoryFactory
operator|=
name|checkNotNull
argument_list|(
name|directoryFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|LuceneIndexWriter
name|newInstance
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|NodeBuilder
name|definitionBuilder
parameter_list|,
name|boolean
name|reindex
parameter_list|)
block|{
if|if
condition|(
name|mountInfoProvider
operator|.
name|hasNonDefaultMounts
argument_list|()
condition|)
block|{
return|return
operator|new
name|MultiplexingIndexWriter
argument_list|(
name|directoryFactory
argument_list|,
name|mountInfoProvider
argument_list|,
name|definition
argument_list|,
name|definitionBuilder
argument_list|,
name|reindex
argument_list|)
return|;
block|}
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
name|LuceneIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|SUGGEST_DATA_CHILD_NAME
argument_list|,
name|reindex
argument_list|)
return|;
block|}
block|}
end_class

end_unit

