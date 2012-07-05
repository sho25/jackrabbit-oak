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
name|jcr
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Abstract base class for a lazy value of type {@code T}. The initialisation of  * the actual value is deferred until first accessed.  *  * @param<T> type of the value  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|LazyValue
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
name|T
name|value
decl_stmt|;
comment|/**      * Create the value.      * @return  the value      */
specifier|protected
specifier|abstract
name|T
name|create
parameter_list|()
function_decl|;
comment|/**      * Retrieve the value. The first call of this method results in a call      * to {@link #create()}. Subsequent calls return the same instance as the      * first call.      *      * @return the underlying value      */
specifier|public
specifier|synchronized
name|T
name|get
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
name|create
argument_list|()
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

