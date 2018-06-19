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
operator|.
name|cancel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * The result of a check for a pending cancellation request.  */
end_comment

begin_class
specifier|public
class|class
name|Cancellation
block|{
specifier|private
specifier|final
name|boolean
name|cancelled
decl_stmt|;
specifier|private
specifier|final
name|String
name|reason
decl_stmt|;
name|Cancellation
parameter_list|(
name|boolean
name|cancelled
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|cancelled
operator|=
name|cancelled
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
block|}
comment|/**      * Returns {@code true} if cancellation has been requested, {@code false}      * otherwise.      */
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|cancelled
return|;
block|}
comment|/**      * If cancellation has been requested (i.e. if {@link #isCancelled()} is      * {@code true}), returns the reason of the cancellation as provided by the      * user. Otherwise, an empty {@link Optional} is returned.      */
specifier|public
name|Optional
argument_list|<
name|String
argument_list|>
name|getReason
parameter_list|()
block|{
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|reason
argument_list|)
return|;
block|}
block|}
end_class

end_unit

