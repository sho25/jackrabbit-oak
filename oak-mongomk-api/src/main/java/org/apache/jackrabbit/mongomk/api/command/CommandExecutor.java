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
name|api
operator|.
name|command
package|;
end_package

begin_comment
comment|/**  * The executor part of the<a href="http://en.wikipedia.org/wiki/Command_pattern">Command Pattern</a>.  *  *<p>  * The implementation of this class contains the business logic to execute a command.  *</p>  *  * @see<a href="http://en.wikipedia.org/wiki/Command_pattern">Command Pattern</a>  * @see Command  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|CommandExecutor
block|{
comment|/**      * Executes the given {@link Command} and returns the result.      *      *<p>      * If an retry behavior is specified this will be taken care of by the implementation as well.      *</p>      *      * @param command      * @return The result of the execution.      * @throws Exception If an error occurred while executing.      */
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Command
argument_list|<
name|T
argument_list|>
name|command
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

