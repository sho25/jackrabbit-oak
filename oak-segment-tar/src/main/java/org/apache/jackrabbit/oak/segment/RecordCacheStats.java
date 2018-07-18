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
name|cache
operator|.
name|CacheStats
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
name|AbstractCacheStats
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
comment|/**  * Statistics for {@link RecordCache}.  */
end_comment

begin_class
specifier|public
class|class
name|RecordCacheStats
extends|extends
name|AbstractCacheStats
block|{
annotation|@
name|NotNull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|CacheStats
argument_list|>
name|stats
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Long
argument_list|>
name|elementCount
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Long
argument_list|>
name|weight
decl_stmt|;
specifier|public
name|RecordCacheStats
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|CacheStats
argument_list|>
name|stats
parameter_list|,
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|Long
argument_list|>
name|elementCount
parameter_list|,
annotation|@
name|NotNull
name|Supplier
argument_list|<
name|Long
argument_list|>
name|weight
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|checkNotNull
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|this
operator|.
name|elementCount
operator|=
name|checkNotNull
argument_list|(
name|elementCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|checkNotNull
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|CacheStats
name|getCurrentStats
parameter_list|()
block|{
return|return
name|stats
operator|.
name|get
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
name|elementCount
operator|.
name|get
argument_list|()
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
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|estimateCurrentWeight
parameter_list|()
block|{
return|return
name|weight
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

