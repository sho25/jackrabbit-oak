begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|util
package|;
end_package

begin_comment
comment|/**  * Type safe counter part of {@link org.apache.commons.collections.Predicate}.  *  * @param<T> type of values this predicate is defined on  */
end_comment

begin_interface
specifier|public
interface|interface
name|Predicate
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Use the specified parameter to perform a test that returns true or false.      *      * @param arg  the predicate to evaluate, should not be changed      * @return true or false      */
name|boolean
name|evaluate
parameter_list|(
name|T
name|arg
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

