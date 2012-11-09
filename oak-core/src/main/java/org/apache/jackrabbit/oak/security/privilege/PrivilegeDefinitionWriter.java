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
name|security
operator|.
name|privilege
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CommitFailedException
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
name|api
operator|.
name|Root
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeDefinition
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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_comment
comment|/**  * PrivilegeDefinitionWriter is responsible for writing privilege definitions  * to the repository without applying any validation checks.  */
end_comment

begin_class
class|class
name|PrivilegeDefinitionWriter
implements|implements
name|PrivilegeConstants
block|{
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
name|PrivilegeDefinitionWriter
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
name|void
name|writeDefinition
parameter_list|(
name|PrivilegeDefinition
name|definition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|writeDefinitions
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|definition
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|writeDefinitions
parameter_list|(
name|Iterable
argument_list|<
name|PrivilegeDefinition
argument_list|>
name|definitions
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
comment|// make sure the privileges path is defined
name|Tree
name|privilegesTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|privilegesTree
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Repository doesn't contain node "
operator|+
name|PRIVILEGES_PATH
argument_list|)
throw|;
block|}
name|NodeUtil
name|privilegesNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|privilegesTree
argument_list|)
decl_stmt|;
for|for
control|(
name|PrivilegeDefinition
name|definition
range|:
name|definitions
control|)
block|{
name|writePrivilegeNode
argument_list|(
name|privilegesNode
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
comment|// delegate validation to the commit validation (see above)
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|RepositoryException
condition|)
block|{
throw|throw
operator|(
name|RepositoryException
operator|)
name|t
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|writePrivilegeNode
parameter_list|(
name|NodeUtil
name|privilegesNode
parameter_list|,
name|PrivilegeDefinition
name|definition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|definition
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|privilegesNode
operator|.
name|hasChild
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Privilege definition with name '"
operator|+
name|name
operator|+
literal|"' already exists."
argument_list|)
throw|;
block|}
name|NodeUtil
name|privNode
init|=
name|privilegesNode
operator|.
name|addChild
argument_list|(
name|name
argument_list|,
name|NT_REP_PRIVILEGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isAbstract
argument_list|()
condition|)
block|{
name|privNode
operator|.
name|setBoolean
argument_list|(
name|REP_IS_ABSTRACT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|declAggrNames
init|=
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|declAggrNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|names
init|=
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|declAggrNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|privNode
operator|.
name|setNames
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

