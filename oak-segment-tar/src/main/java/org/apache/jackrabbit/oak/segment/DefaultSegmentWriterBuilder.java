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
name|segment
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
name|checkNotNull
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
name|base
operator|.
name|Supplier
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
name|base
operator|.
name|Suppliers
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
name|segment
operator|.
name|WriterCacheManager
operator|.
name|Empty
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|ReadOnlyFileStore
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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

begin_comment
comment|/**  * Builder for building {@link DefaultSegmentWriter} instances.  * The returned instances are thread safe if {@link #withWriterPool()}  * was specified and<em>not</em> thread sage if {@link #withoutWriterPool()}  * was specified (default).  *<p>  *<em>Default:</em> calling one of the {@code build()} methods without previously  * calling one of the {@code with...()} methods returns a {@code SegmentWriter}  * as would the following chain of calls:  *<pre>      segmentWriterBuilder("name")         .with(LATEST_VERSION)         .withGeneration(0)         .withoutWriterPool()         .with(new WriterCacheManager.Default())         .build(store);  *</pre>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|DefaultSegmentWriterBuilder
block|{
annotation|@
name|NotNull
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|NotNull
specifier|private
name|Supplier
argument_list|<
name|GCGeneration
argument_list|>
name|generation
init|=
name|Suppliers
operator|.
name|ofInstance
argument_list|(
name|GCGeneration
operator|.
name|NULL
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|pooled
init|=
literal|false
decl_stmt|;
annotation|@
name|NotNull
specifier|private
name|WriterCacheManager
name|cacheManager
init|=
operator|new
name|WriterCacheManager
operator|.
name|Default
argument_list|()
decl_stmt|;
specifier|private
name|DefaultSegmentWriterBuilder
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the {@code name} of this builder. This name will appear in the segment's      * meta data.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|DefaultSegmentWriterBuilder
name|defaultSegmentWriterBuilder
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|DefaultSegmentWriterBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Specify the {@code generation} for the segment written by the returned      * segment writer.      *<p>      * If {@link #withoutWriterPool()} was specified all segments will be written      * at the generation that {@code generation.get()} returned at the time      * any of the {@code build()} methods is called.      * If {@link #withWriterPool()} was specified a segments will be written      * at the generation that {@code generation.get()} returns when a new segment      * is created by the returned writer.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriterBuilder
name|withGeneration
parameter_list|(
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|GCGeneration
argument_list|>
name|generation
parameter_list|)
block|{
name|this
operator|.
name|generation
operator|=
name|checkNotNull
argument_list|(
name|generation
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specify the {@code generation} for the segment written by the returned      * segment writer.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriterBuilder
name|withGeneration
parameter_list|(
annotation|@
name|NotNull
name|GCGeneration
name|generation
parameter_list|)
block|{
name|this
operator|.
name|generation
operator|=
name|Suppliers
operator|.
name|ofInstance
argument_list|(
name|checkNotNull
argument_list|(
name|generation
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Create a {@code SegmentWriter} backed by a {@link SegmentBufferWriterPool}.      * The returned instance is thread safe.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriterBuilder
name|withWriterPool
parameter_list|()
block|{
name|this
operator|.
name|pooled
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Create a {@code SegmentWriter} backed by a {@link SegmentBufferWriter}.      * The returned instance is<em>not</em> thread safe.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriterBuilder
name|withoutWriterPool
parameter_list|()
block|{
name|this
operator|.
name|pooled
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specify the {@code cacheManager} used by the returned writer.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriterBuilder
name|with
parameter_list|(
name|WriterCacheManager
name|cacheManager
parameter_list|)
block|{
name|this
operator|.
name|cacheManager
operator|=
name|checkNotNull
argument_list|(
name|cacheManager
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specify that the returned writer should not use a cache.      * @see #with(WriterCacheManager)      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriterBuilder
name|withoutCache
parameter_list|()
block|{
name|this
operator|.
name|cacheManager
operator|=
name|Empty
operator|.
name|INSTANCE
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Build a {@code SegmentWriter} for a {@code FileStore}.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriter
name|build
parameter_list|(
annotation|@
name|NotNull
name|FileStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|DefaultSegmentWriter
argument_list|(
name|checkNotNull
argument_list|(
name|store
argument_list|)
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|cacheManager
argument_list|,
name|createWriter
argument_list|(
name|store
argument_list|,
name|pooled
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Build a {@code SegmentWriter} for a {@code ReadOnlyFileStore}.      * Attempting to write to the returned writer will cause a      * {@code UnsupportedOperationException} to be thrown.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriter
name|build
parameter_list|(
annotation|@
name|NotNull
name|ReadOnlyFileStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|DefaultSegmentWriter
argument_list|(
name|checkNotNull
argument_list|(
name|store
argument_list|)
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|cacheManager
argument_list|,
operator|new
name|WriteOperationHandler
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|GCGeneration
name|getGCGeneration
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot write to read-only store"
argument_list|)
throw|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RecordId
name|execute
parameter_list|(
annotation|@
name|NotNull
name|GCGeneration
name|gcGeneration
parameter_list|,
annotation|@
name|NotNull
name|WriteOperation
name|writeOperation
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot write to read-only store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|(
annotation|@
name|NotNull
name|SegmentStore
name|store
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot write to read-only store"
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Build a {@code SegmentWriter} for a {@code MemoryStore}.      */
annotation|@
name|NotNull
specifier|public
name|DefaultSegmentWriter
name|build
parameter_list|(
annotation|@
name|NotNull
name|MemoryStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|DefaultSegmentWriter
argument_list|(
name|checkNotNull
argument_list|(
name|store
argument_list|)
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|cacheManager
argument_list|,
name|createWriter
argument_list|(
name|store
argument_list|,
name|pooled
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|WriteOperationHandler
name|createWriter
parameter_list|(
annotation|@
name|NotNull
name|FileStore
name|store
parameter_list|,
name|boolean
name|pooled
parameter_list|)
block|{
if|if
condition|(
name|pooled
condition|)
block|{
return|return
operator|new
name|SegmentBufferWriterPool
argument_list|(
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|name
argument_list|,
name|generation
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SegmentBufferWriter
argument_list|(
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|name
argument_list|,
name|generation
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
name|WriteOperationHandler
name|createWriter
parameter_list|(
annotation|@
name|NotNull
name|MemoryStore
name|store
parameter_list|,
name|boolean
name|pooled
parameter_list|)
block|{
if|if
condition|(
name|pooled
condition|)
block|{
return|return
operator|new
name|SegmentBufferWriterPool
argument_list|(
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|name
argument_list|,
name|generation
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SegmentBufferWriter
argument_list|(
name|store
operator|.
name|getSegmentIdProvider
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|name
argument_list|,
name|generation
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

