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
name|Collection
import|;
end_import

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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|annotation
operator|.
name|Nonnull
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
name|api
operator|.
name|Type
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
name|ImmutablePrivilegeDefinition
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
name|PrivilegeBits
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
name|PrivilegeBitsProvider
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
name|TreeUtil
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|bitsMgr
decl_stmt|;
specifier|private
name|PrivilegeBits
name|next
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
name|this
operator|.
name|bitsMgr
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|Tree
name|privilegesTree
init|=
name|bitsMgr
operator|.
name|getPrivilegesTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|privilegesTree
operator|.
name|exists
argument_list|()
operator|&&
name|privilegesTree
operator|.
name|hasProperty
argument_list|(
name|REP_NEXT
argument_list|)
condition|)
block|{
name|next
operator|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|privilegesTree
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|next
operator|=
name|PrivilegeBits
operator|.
name|NEXT_AFTER_BUILT_INS
expr_stmt|;
block|}
block|}
comment|/**      * Write the given privilege definition to the repository content.      *      * @param definition The new privilege definition.      * @throws RepositoryException If the definition can't be written.      */
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
comment|/**      * Create the built-in privilege definitions during repository setup.      *      * @throws RepositoryException If an error occurs.      */
name|void
name|writeBuiltInDefinitions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|writeDefinitions
argument_list|(
name|getBuiltInDefinitions
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
annotation|@
name|Nonnull
specifier|private
name|PrivilegeBits
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrivilegeBits
name|next
parameter_list|()
block|{
name|PrivilegeBits
name|bits
init|=
name|next
decl_stmt|;
name|next
operator|=
name|bits
operator|.
name|nextBits
argument_list|()
expr_stmt|;
return|return
name|bits
return|;
block|}
comment|/**      * @param definitions      * @throws RepositoryException      */
specifier|private
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
operator|!
name|privilegesTree
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Privilege store does not exist."
argument_list|)
throw|;
block|}
for|for
control|(
name|PrivilegeDefinition
name|definition
range|:
name|definitions
control|)
block|{
if|if
condition|(
name|privilegesTree
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
name|definition
operator|.
name|getName
argument_list|()
operator|+
literal|"' already exists."
argument_list|)
throw|;
block|}
name|writePrivilegeNode
argument_list|(
name|privilegesTree
argument_list|,
name|definition
argument_list|)
expr_stmt|;
block|}
comment|/*             update the property storing the next privilege bits with the             privileges root tree. this is a cheap way to detect collisions that             may arise from concurrent registration of custom privileges.             */
name|getNext
argument_list|()
operator|.
name|writeTo
argument_list|(
name|privilegesTree
argument_list|)
expr_stmt|;
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
throw|throw
name|e
operator|.
name|asRepositoryException
argument_list|()
throw|;
block|}
block|}
specifier|private
name|void
name|writePrivilegeNode
parameter_list|(
name|Tree
name|privilegesTree
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
name|Tree
name|privNode
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|privilegesTree
argument_list|,
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
name|setProperty
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
name|boolean
name|isAggregate
init|=
name|declAggrNames
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|isAggregate
condition|)
block|{
name|privNode
operator|.
name|setProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|declAggrNames
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
name|PrivilegeBits
name|bits
decl_stmt|;
if|if
condition|(
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|bits
operator|=
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isAggregate
condition|)
block|{
name|bits
operator|=
name|bitsMgr
operator|.
name|getBits
argument_list|(
name|declAggrNames
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bits
operator|=
name|next
argument_list|()
expr_stmt|;
block|}
name|bits
operator|.
name|writeTo
argument_list|(
name|privNode
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Collection
argument_list|<
name|PrivilegeDefinition
argument_list|>
name|getBuiltInDefinitions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|privilegeName
range|:
name|NON_AGGREGATE_PRIVILEGES
control|)
block|{
name|PrivilegeDefinition
name|def
init|=
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|privilegeName
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|privilegeName
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|privilegeName
range|:
name|AGGREGATE_PRIVILEGES
operator|.
name|keySet
argument_list|()
control|)
block|{
name|PrivilegeDefinition
name|def
init|=
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|privilegeName
argument_list|,
literal|false
argument_list|,
name|asList
argument_list|(
name|AGGREGATE_PRIVILEGES
operator|.
name|get
argument_list|(
name|privilegeName
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|privilegeName
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
name|PrivilegeDefinition
name|all
init|=
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|JCR_ALL
argument_list|,
literal|false
argument_list|,
name|definitions
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|JCR_ALL
argument_list|,
name|all
argument_list|)
expr_stmt|;
return|return
name|definitions
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class

end_unit

