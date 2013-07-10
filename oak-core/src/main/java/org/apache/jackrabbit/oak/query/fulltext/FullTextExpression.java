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
name|query
operator|.
name|fulltext
package|;
end_package

begin_comment
comment|/**  * The base class for fulltext condition expression.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|FullTextExpression
block|{
specifier|public
specifier|static
specifier|final
name|int
name|PRECEDENCE_OR
init|=
literal|1
decl_stmt|,
name|PRECEDENCE_AND
init|=
literal|2
decl_stmt|,
name|PRECEDENCE_TERM
init|=
literal|3
decl_stmt|;
specifier|public
specifier|abstract
name|int
name|getPrecedence
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|evaluate
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
specifier|abstract
name|FullTextExpression
name|simplify
parameter_list|()
function_decl|;
block|}
end_class

end_unit

