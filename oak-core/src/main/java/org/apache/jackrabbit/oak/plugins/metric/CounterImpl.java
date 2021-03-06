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
name|metric
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Counter
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
name|stats
operator|.
name|CounterStats
import|;
end_import

begin_class
specifier|final
class|class
name|CounterImpl
implements|implements
name|CounterStats
block|{
specifier|private
specifier|final
name|Counter
name|counter
decl_stmt|;
name|CounterImpl
parameter_list|(
name|Counter
name|counter
parameter_list|)
block|{
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|inc
parameter_list|()
block|{
name|counter
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dec
parameter_list|()
block|{
name|counter
operator|.
name|dec
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|inc
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|counter
operator|.
name|inc
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dec
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|counter
operator|.
name|dec
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|counter
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

