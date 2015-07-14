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
name|cache
package|;
end_package

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
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|Weigher
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
name|jmx
operator|.
name|CacheStatsMBean
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
name|commons
operator|.
name|jmx
operator|.
name|AnnotatedStandardMBean
import|;
end_import

begin_comment
comment|/**  * Cache statistics.  */
end_comment

begin_class
specifier|public
class|class
name|CacheStats
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|CacheStatsMBean
block|{
specifier|private
specifier|final
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|cache
decl_stmt|;
specifier|private
specifier|final
name|Weigher
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|weigher
decl_stmt|;
specifier|private
specifier|final
name|long
name|maxWeight
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheStats
name|lastSnapshot
init|=
operator|new
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|CacheStats
parameter_list|(
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|cache
parameter_list|,
name|String
name|name
parameter_list|,
name|Weigher
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|weigher
parameter_list|,
name|long
name|maxWeight
parameter_list|)
block|{
name|super
argument_list|(
name|CacheStatsMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
operator|(
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|cache
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|weigher
operator|=
operator|(
name|Weigher
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|weigher
expr_stmt|;
name|this
operator|.
name|maxWeight
operator|=
name|maxWeight
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getRequestCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|requestCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getHitCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|hitCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getHitRate
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|hitRate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMissCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|missCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getMissRate
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|missRate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLoadCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|loadCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLoadSuccessCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|loadSuccessCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLoadExceptionCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|loadExceptionCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getLoadExceptionRate
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|loadExceptionRate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTotalLoadTime
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|totalLoadTime
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getAverageLoadPenalty
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|averageLoadPenalty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEvictionCount
parameter_list|()
block|{
return|return
name|stats
argument_list|()
operator|.
name|evictionCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getElementCount
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|estimateCurrentWeight
parameter_list|()
block|{
if|if
condition|(
name|weigher
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|e
range|:
name|cache
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|k
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|v
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|size
operator|+=
name|weigher
operator|.
name|weigh
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxTotalWeight
parameter_list|()
block|{
return|return
name|maxWeight
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|resetStats
parameter_list|()
block|{
comment|//Cache stats cannot be rest at Guava level. Instead we
comment|//take a snapshot and then subtract it from future stats calls
name|lastSnapshot
operator|=
name|cache
operator|.
name|stats
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|cacheInfoAsString
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|toStringHelper
argument_list|(
literal|"CacheStats"
argument_list|)
operator|.
name|add
argument_list|(
literal|"hitCount"
argument_list|,
name|getHitCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"hitRate"
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%1.2f"
argument_list|,
name|getHitRate
argument_list|()
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"missCount"
argument_list|,
name|getMissCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"missRate"
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%1.2f"
argument_list|,
name|getMissRate
argument_list|()
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"requestCount"
argument_list|,
name|getRequestCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"loadCount"
argument_list|,
name|getLoadCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"loadSuccessCount"
argument_list|,
name|getLoadSuccessCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"loadExceptionCount"
argument_list|,
name|getLoadExceptionCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"totalLoadTime"
argument_list|,
name|timeInWords
argument_list|(
name|getTotalLoadTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"averageLoadPenalty (nanos)"
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%1.2f"
argument_list|,
name|getAverageLoadPenalty
argument_list|()
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"evictionCount"
argument_list|,
name|getEvictionCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"elementCount"
argument_list|,
name|getElementCount
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"totalWeight"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|estimateCurrentWeight
argument_list|()
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"maxWeight"
argument_list|,
name|humanReadableByteCount
argument_list|(
name|getMaxTotalWeight
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|private
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheStats
name|stats
parameter_list|()
block|{
return|return
name|cache
operator|.
name|stats
argument_list|()
operator|.
name|minus
argument_list|(
name|lastSnapshot
argument_list|)
return|;
block|}
specifier|static
name|String
name|timeInWords
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|long
name|millis
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|nanos
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%d min, %d sec"
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMinutes
argument_list|(
name|millis
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|millis
argument_list|)
operator|-
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toSeconds
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMinutes
argument_list|(
name|millis
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

