begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|segment
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|getInteger
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Integer
operator|.
name|rotateLeft
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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

begin_comment
comment|/**  * Segment identifier. There are two types of segments: data segments, and bulk  * segments. Data segments have a header and may reference other segments; bulk  * segments do not.  */
end_comment

begin_class
specifier|public
class|class
name|SegmentId
implements|implements
name|Comparable
argument_list|<
name|SegmentId
argument_list|>
block|{
comment|/** Logger instance */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentId
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Sample rate bit mask of {@link SegmentTracker#segmentCache}. Lower values      * will cause more frequent accesses to that cache instead of the short      * circuit through {@link SegmentId#segment}. Access to that cache is slower      * but allows tracking access statistics. Should be 2^x - 1 (for example      * 1023, 255, 15,...).      */
specifier|private
specifier|static
specifier|final
name|int
name|SEGMENT_CACHE_SAMPLE_MASK
init|=
name|getInteger
argument_list|(
literal|"SegmentCacheSampleRate"
argument_list|,
literal|1023
argument_list|)
decl_stmt|;
comment|/**      * The initial random value for the pseudo random number generator. Initial      * values of 0 - 0xffff will ensure a long period, but other values don't.      */
specifier|private
specifier|static
specifier|volatile
name|int
name|random
init|=
call|(
name|int
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|&
literal|0xffff
argument_list|)
decl_stmt|;
comment|/**      * Checks whether this is a data segment identifier.      *      * @return {@code true} for a data segment, {@code false} otherwise      */
specifier|public
specifier|static
name|boolean
name|isDataSegmentId
parameter_list|(
name|long
name|lsb
parameter_list|)
block|{
return|return
operator|(
name|lsb
operator|>>>
literal|60
operator|)
operator|==
literal|0xAL
return|;
block|}
specifier|private
specifier|final
name|SegmentTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|long
name|msb
decl_stmt|;
specifier|private
specifier|final
name|long
name|lsb
decl_stmt|;
specifier|private
name|long
name|creationTime
decl_stmt|;
comment|/**      * A reference to the segment object, if it is available in memory. It is      * used for fast lookup. The segment tracker will set or reset this field.      *<p>      * Needs to be volatile so {@link #setSegment(Segment)} doesn't need to      * be synchronized as this would lead to deadlocks.      */
specifier|private
specifier|volatile
name|Segment
name|segment
decl_stmt|;
specifier|private
name|SegmentId
parameter_list|(
name|SegmentTracker
name|tracker
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|,
name|Segment
name|segment
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
name|this
operator|.
name|msb
operator|=
name|msb
expr_stmt|;
name|this
operator|.
name|lsb
operator|=
name|lsb
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
block|}
specifier|public
name|SegmentId
parameter_list|(
name|SegmentTracker
name|tracker
parameter_list|,
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
name|this
argument_list|(
name|tracker
argument_list|,
name|msb
argument_list|,
name|lsb
argument_list|,
literal|null
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks whether this is a data segment identifier.      *      * @return {@code true} for a data segment, {@code false} otherwise      */
specifier|public
name|boolean
name|isDataSegmentId
parameter_list|()
block|{
return|return
name|isDataSegmentId
argument_list|(
name|lsb
argument_list|)
return|;
block|}
comment|/**      * Checks whether this is a bulk segment identifier.      *      * @return {@code true} for a bulk segment, {@code false} otherwise      */
specifier|public
name|boolean
name|isBulkSegmentId
parameter_list|()
block|{
return|return
operator|(
name|lsb
operator|>>>
literal|60
operator|)
operator|==
literal|0xBL
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|long
name|msb
parameter_list|,
name|long
name|lsb
parameter_list|)
block|{
return|return
name|this
operator|.
name|msb
operator|==
name|msb
operator|&&
name|this
operator|.
name|lsb
operator|==
name|lsb
return|;
block|}
specifier|public
name|long
name|getMostSignificantBits
parameter_list|()
block|{
return|return
name|msb
return|;
block|}
specifier|public
name|long
name|getLeastSignificantBits
parameter_list|()
block|{
return|return
name|lsb
return|;
block|}
comment|/**      * Get a random integer. A fast, but lower quality pseudo random number      * generator is used.      *       * @return a random value.      */
specifier|private
specifier|static
name|int
name|randomInt
parameter_list|()
block|{
comment|// There is a race here on concurrent access. However, given the usage the resulting
comment|// bias seems preferable to the performance penalty of synchronization
return|return
name|random
operator|=
literal|0xc3e157c1
operator|-
name|rotateLeft
argument_list|(
name|random
argument_list|,
literal|19
argument_list|)
return|;
block|}
specifier|public
name|Segment
name|getSegment
parameter_list|()
block|{
comment|// Sample the segment cache once in a while to get some cache hit/miss statistics
if|if
condition|(
operator|(
name|randomInt
argument_list|()
operator|&
name|SEGMENT_CACHE_SAMPLE_MASK
operator|)
operator|==
literal|0
condition|)
block|{
name|Segment
name|segment
init|=
name|tracker
operator|.
name|getCachedSegment
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
return|return
name|segment
return|;
block|}
block|}
comment|// Fall back to short circuit via this.segment if not in the cache
name|Segment
name|segment
init|=
name|this
operator|.
name|segment
decl_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|segment
operator|=
name|this
operator|.
name|segment
expr_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Loading segment {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|segment
operator|=
name|tracker
operator|.
name|readSegment
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|segment
return|;
block|}
name|void
name|setSegment
parameter_list|(
name|Segment
name|segment
parameter_list|)
block|{
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
block|}
specifier|public
name|SegmentTracker
name|getTracker
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
comment|/**      * Pins this segment so it won't be cleaned by the {@code CLEAN_OLD} strategy.      */
name|void
name|pin
parameter_list|()
block|{
name|creationTime
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
comment|/**      * @return  this segment id as UUID      */
specifier|public
name|UUID
name|asUUID
parameter_list|()
block|{
return|return
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
return|;
block|}
comment|// --------------------------------------------------------< Comparable>--
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|SegmentId
name|that
parameter_list|)
block|{
name|int
name|d
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|msb
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|that
operator|.
name|msb
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|0
condition|)
block|{
name|d
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|lsb
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|that
operator|.
name|lsb
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|d
return|;
block|}
comment|// ------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|UUID
argument_list|(
name|msb
argument_list|,
name|lsb
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|SegmentId
condition|)
block|{
name|SegmentId
name|that
init|=
operator|(
name|SegmentId
operator|)
name|object
decl_stmt|;
return|return
name|msb
operator|==
name|that
operator|.
name|msb
operator|&&
name|lsb
operator|==
name|that
operator|.
name|lsb
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|lsb
return|;
block|}
block|}
end_class

end_unit

