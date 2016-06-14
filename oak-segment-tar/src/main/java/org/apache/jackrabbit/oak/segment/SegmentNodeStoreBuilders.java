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
name|SegmentNodeStore
operator|.
name|SegmentNodeStoreBuilder
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
name|http
operator|.
name|HttpStore
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

begin_comment
comment|/**  * Static factories for creating {@link SegmentNodeBuilder} instances  * pertaining to specific {@link SegmentStore} instances.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SegmentNodeStoreBuilders
block|{
specifier|private
name|SegmentNodeStoreBuilders
parameter_list|()
block|{}
comment|/**      * Create a {@code SegmentNodeStoreBuilder} based on a {@code FileStore}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|SegmentNodeStoreBuilder
name|builder
parameter_list|(
annotation|@
name|Nonnull
name|FileStore
name|store
parameter_list|)
block|{
return|return
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|store
operator|.
name|getRevisions
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getWriter
argument_list|()
argument_list|,
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Create a {@code SegmentNodeStoreBuilder} based on a {@code MemoryStore}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|SegmentNodeStoreBuilder
name|builder
parameter_list|(
annotation|@
name|Nonnull
name|MemoryStore
name|store
parameter_list|)
block|{
return|return
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|store
operator|.
name|getRevisions
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getWriter
argument_list|()
argument_list|,
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Create a {@code SegmentNodeStoreBuilder} based on a {@code HttpStore}.      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|SegmentNodeStoreBuilder
name|builder
parameter_list|(
annotation|@
name|Nonnull
name|HttpStore
name|store
parameter_list|)
block|{
return|return
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|store
operator|.
name|getRevisions
argument_list|()
argument_list|,
name|store
operator|.
name|getReader
argument_list|()
argument_list|,
name|store
operator|.
name|getWriter
argument_list|()
argument_list|,
name|store
operator|.
name|getBlobStore
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

