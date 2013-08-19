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
name|jcr
operator|.
name|delegate
package|;
end_package

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
name|jcr
operator|.
name|operation
operator|.
name|SessionOperation
import|;
end_import

begin_comment
comment|/**  * Interceptor for operations being performed in a session.  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|SessionOperationInterceptor
block|{
name|SessionOperationInterceptor
name|NOOP
init|=
operator|new
name|SessionOperationInterceptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|(
name|SessionDelegate
name|delegate
parameter_list|,
name|SessionOperation
name|operation
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|(
name|SessionDelegate
name|delegate
parameter_list|,
name|SessionOperation
name|operation
parameter_list|)
block|{         }
block|}
decl_stmt|;
comment|/**      * Invoked before the sessionOperation is performed.      *      *<p>      * An implementation of this method should not perform content access or any other      * repository operation. The {@link SessionDelegate} and {@link SessionOperation} must only be used      * to extract information e.g. session Id, type of operation etc.      *      * Further {@link SessionOperation#perform} is invoked      * by the caller and implementers MUST not invoke it      *</p>      *      * @param delegate sessionDelegate performing the operation      * @param operation operation to perform      */
name|void
name|before
parameter_list|(
name|SessionDelegate
name|delegate
parameter_list|,
name|SessionOperation
name|operation
parameter_list|)
function_decl|;
comment|/**      * Invoked after the sessionOperation is performed.      *      *<p>      * An implementation of this method should not perform content access or any other      * repository operation. The {@link SessionDelegate} and {@link SessionOperation} must only be used      * to extract information e.g. session Id, type of operation etc.      *      * Further {@link SessionOperation#perform} is invoked      * by the caller and implementers MUST not invoke it      *</p>      *      * @param delegate sessionDelegate performing the operation      * @param operation operation to perform      */
name|void
name|after
parameter_list|(
name|SessionDelegate
name|delegate
parameter_list|,
name|SessionOperation
name|operation
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

