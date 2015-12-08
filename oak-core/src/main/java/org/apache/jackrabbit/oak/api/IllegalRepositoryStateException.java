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
name|api
package|;
end_package

begin_comment
comment|/**  * This exception can be thrown by implementers of this API to signal an error  * condition caused by an invalid state of the repository.  *<p>  * It's up to the implementation to distinguish between recoverable and  * unrecoverable error conditions. In case of recoverable error conditions, it's  * appropriate for an implementation to create a subclass of this exception and  * expose that subclass as part of its public API. This way, clients of the  * Content Repository API can catch specific failures, provided that they also  * want to introduce a dependency to the implementation's API.  */
end_comment

begin_class
specifier|public
class|class
name|IllegalRepositoryStateException
extends|extends
name|RuntimeException
block|{
specifier|public
name|IllegalRepositoryStateException
parameter_list|()
block|{     }
specifier|public
name|IllegalRepositoryStateException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IllegalRepositoryStateException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IllegalRepositoryStateException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

