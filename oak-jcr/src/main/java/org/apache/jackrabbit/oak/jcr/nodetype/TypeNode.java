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
name|nodetype
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_comment
comment|/**  * Base class for node type, property and node definitions based on  * in-content definitions.  */
end_comment

begin_class
class|class
name|TypeNode
block|{
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
specifier|protected
name|TypeNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
specifier|protected
name|IllegalStateException
name|illegalState
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to access node type information from "
operator|+
name|node
argument_list|,
name|e
argument_list|)
return|;
block|}
specifier|protected
name|NodeType
name|getType
parameter_list|(
name|NodeTypeManager
name|manager
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|manager
operator|.
name|getNodeType
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
name|getString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
name|getString
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|ifNotFound
parameter_list|)
block|{
try|try
block|{
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
return|return
name|ifNotFound
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
index|[]
name|getStrings
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|getStrings
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getValues
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
index|[]
name|getStrings
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|ifNotFound
parameter_list|)
block|{
try|try
block|{
return|return
name|getStrings
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getValues
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
return|return
name|ifNotFound
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|Value
index|[]
name|getValues
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
index|[]
name|ifNotFound
parameter_list|)
block|{
try|try
block|{
return|return
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
operator|.
name|getValues
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
return|return
name|ifNotFound
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
index|[]
name|getStrings
parameter_list|(
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
index|[]
name|strings
init|=
operator|new
name|String
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|strings
index|[
name|i
index|]
operator|=
name|values
index|[
name|i
index|]
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
specifier|protected
name|NodeIterator
name|getNodes
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|node
operator|.
name|getNodes
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
name|illegalState
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

