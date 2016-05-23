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
name|java
operator|.
name|lang
operator|.
name|Long
operator|.
name|getLong
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|cache
operator|.
name|CacheStats
import|;
end_import

begin_comment
comment|/*  * FIXME OAK-4373 implement invalidation through GCMonitor listener  * FIXME OAK-4373 implement monitoring, management, logging, tests  */
end_comment

begin_class
specifier|public
class|class
name|SegmentReaderImpl
implements|implements
name|SegmentReader
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_STRING_CACHE_MB
init|=
literal|256
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STRING_CACHE_MB
init|=
literal|"oak.segment.stringCacheMB"
decl_stmt|;
comment|/**      * Cache for string records      */
specifier|private
specifier|final
name|StringCache
name|stringCache
decl_stmt|;
specifier|public
name|SegmentReaderImpl
parameter_list|(
name|long
name|stringCacheMB
parameter_list|)
block|{
name|stringCache
operator|=
operator|new
name|StringCache
argument_list|(
name|getLong
argument_list|(
name|STRING_CACHE_MB
argument_list|,
name|stringCacheMB
argument_list|)
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SegmentReaderImpl
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_STRING_CACHE_MB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|readString
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|)
block|{
specifier|final
name|SegmentId
name|segmentId
init|=
name|id
operator|.
name|getSegmentId
argument_list|()
decl_stmt|;
name|long
name|msb
init|=
name|segmentId
operator|.
name|getMostSignificantBits
argument_list|()
decl_stmt|;
name|long
name|lsb
init|=
name|segmentId
operator|.
name|getLeastSignificantBits
argument_list|()
decl_stmt|;
return|return
name|stringCache
operator|.
name|getString
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|,
name|id
operator|.
name|getOffset
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Integer
name|offset
parameter_list|)
block|{
return|return
name|segmentId
operator|.
name|getSegment
argument_list|()
operator|.
name|readString
argument_list|(
name|offset
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|MapRecord
name|readMap
parameter_list|(
annotation|@
name|Nonnull
name|SegmentStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|)
block|{
return|return
operator|new
name|MapRecord
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Template
name|readTemplate
parameter_list|(
annotation|@
name|Nonnull
name|SegmentStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|)
block|{
name|int
name|offset
init|=
name|id
operator|.
name|getOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|templates
operator|==
literal|null
condition|)
block|{
return|return
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|readTemplate
argument_list|(
name|offset
argument_list|)
return|;
block|}
name|Template
name|template
init|=
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|templates
operator|.
name|get
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|template
operator|==
literal|null
condition|)
block|{
name|template
operator|=
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|readTemplate
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|id
operator|.
name|getSegment
argument_list|()
operator|.
name|templates
operator|.
name|putIfAbsent
argument_list|(
name|offset
argument_list|,
name|template
argument_list|)
expr_stmt|;
comment|// only keep the first copy
block|}
return|return
name|template
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CacheStats
name|getStringCacheStats
parameter_list|()
block|{
return|return
name|stringCache
operator|.
name|getStats
argument_list|()
return|;
block|}
block|}
end_class

end_unit

