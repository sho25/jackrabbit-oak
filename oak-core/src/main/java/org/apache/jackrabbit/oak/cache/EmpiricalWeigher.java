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

begin_comment
comment|/**  * Determines the weight of object based on the memory taken by them. The memory estimates  * are based on empirical data and not exact  */
end_comment

begin_class
specifier|public
class|class
name|EmpiricalWeigher
implements|implements
name|Weigher
argument_list|<
name|String
argument_list|,
name|CacheValue
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|key
parameter_list|,
name|CacheValue
name|value
parameter_list|)
block|{
return|return
name|key
operator|.
name|length
argument_list|()
operator|*
literal|2
operator|+
name|value
operator|.
name|getMemory
argument_list|()
return|;
block|}
block|}
end_class

end_unit

