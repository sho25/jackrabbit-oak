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
name|query
operator|.
name|qom
package|;
end_package

begin_comment
comment|/**  * The base class for all QOM nodes.  */
end_comment

begin_class
specifier|abstract
class|class
name|QOMNode
block|{
specifier|protected
name|String
name|protect
parameter_list|(
name|Object
name|expression
parameter_list|)
block|{
name|String
name|str
init|=
name|expression
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|str
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
operator|>=
literal|0
condition|)
block|{
return|return
literal|'('
operator|+
name|str
operator|+
literal|')'
return|;
block|}
return|return
name|str
return|;
block|}
specifier|protected
name|String
name|quotePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|'['
operator|+
name|path
operator|+
literal|']'
return|;
block|}
block|}
end_class

end_unit

