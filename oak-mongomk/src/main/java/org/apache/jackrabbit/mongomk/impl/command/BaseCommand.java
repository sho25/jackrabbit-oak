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
name|mongomk
operator|.
name|impl
operator|.
name|command
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
name|mongomk
operator|.
name|api
operator|.
name|command
operator|.
name|Command
import|;
end_import

begin_comment
comment|/**  * Base {@code Command} implementation.  *  * @param<T> The result type of the {@code Command}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseCommand
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Command
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Constructs a base command.      */
specifier|public
name|BaseCommand
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|int
name|getNumOfRetries
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsRetry
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsRetry
parameter_list|(
name|T
name|result
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

