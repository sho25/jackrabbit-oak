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
operator|.
name|file
package|;
end_package

begin_class
class|class
name|FallbackCompactionStrategy
implements|implements
name|CompactionStrategy
block|{
specifier|private
specifier|final
name|CompactionStrategy
name|primary
decl_stmt|;
specifier|private
specifier|final
name|CompactionStrategy
name|fallback
decl_stmt|;
name|FallbackCompactionStrategy
parameter_list|(
name|CompactionStrategy
name|primary
parameter_list|,
name|CompactionStrategy
name|fallback
parameter_list|)
block|{
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
name|this
operator|.
name|fallback
operator|=
name|fallback
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CompactionResult
name|compact
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|CompactionResult
name|result
init|=
name|primary
operator|.
name|compact
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|isNotApplicable
argument_list|()
condition|)
block|{
return|return
name|fallback
operator|.
name|compact
argument_list|(
name|context
argument_list|)
return|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

