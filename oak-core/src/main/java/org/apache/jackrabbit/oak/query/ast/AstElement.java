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
name|ast
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
name|api
operator|.
name|Tree
import|;
end_import

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
name|query
operator|.
name|Query
import|;
end_import

begin_comment
comment|/**  * The base class for all abstract syntax tree nodes.  */
end_comment

begin_class
specifier|abstract
class|class
name|AstElement
block|{
specifier|protected
name|Query
name|query
decl_stmt|;
specifier|abstract
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
function_decl|;
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
else|else
block|{
return|return
name|str
return|;
block|}
block|}
specifier|protected
name|String
name|quote
parameter_list|(
name|String
name|pathOrName
parameter_list|)
block|{
return|return
literal|'['
operator|+
name|pathOrName
operator|+
literal|']'
return|;
block|}
specifier|public
name|void
name|setQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/**      * Calculate the absolute path (the path including the workspace name).      *      * @param path the session local path      * @return the absolute path      */
specifier|protected
name|String
name|getAbsolutePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|path
return|;
block|}
comment|/**      * Calculate the session local path (the path excluding the workspace name)      * if possible.      *      * @param path the absolute path      * @return the session local path, or null if not within this workspace      */
specifier|protected
name|String
name|getLocalPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|path
return|;
block|}
specifier|protected
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|query
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

