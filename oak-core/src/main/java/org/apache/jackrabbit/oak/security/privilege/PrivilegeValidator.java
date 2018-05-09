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
name|HashSet
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
name|PropertyState
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
name|plugins
operator|.
name|tree
operator|.
name|TreeProvider
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
name|commit
operator|.
name|DefaultValidator
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
name|commit
operator|.
name|Validator
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
name|namespace
operator|.
name|NamespaceConstants
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeUtil
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
name|state
operator|.
name|NodeState
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
name|state
operator|.
name|NodeStateUtils
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
name|util
operator|.
name|Text
import|;
end_import

begin_import
import|import static
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
operator|.
name|CONSTRAINT
import|;
end_import

begin_comment
comment|/**  * Validator implementation that is responsible for validating any modifications  * made to privileges stored in the repository.  */
end_comment

begin_class
class|class
name|PrivilegeValidator
extends|extends
name|DefaultValidator
implements|implements
name|PrivilegeConstants
block|{
specifier|private
specifier|final
name|Root
name|rootBefore
decl_stmt|;
specifier|private
specifier|final
name|Root
name|rootAfter
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|private
specifier|final
name|TreeProvider
name|treeProvider
decl_stmt|;
name|PrivilegeValidator
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|before
parameter_list|,
annotation|@
name|Nonnull
name|Root
name|after
parameter_list|,
annotation|@
name|Nonnull
name|TreeProvider
name|treeProvider
parameter_list|)
block|{
name|rootBefore
operator|=
name|before
expr_stmt|;
name|rootAfter
operator|=
name|after
expr_stmt|;
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|rootBefore
argument_list|)
expr_stmt|;
name|this
operator|.
name|treeProvider
operator|=
name|treeProvider
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Validator>---
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|REP_NEXT
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|validateNext
argument_list|(
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|getPrivilegesTree
argument_list|(
name|rootBefore
argument_list|)
operator|.
name|getProperty
argument_list|(
name|REP_NEXT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|45
argument_list|,
literal|"Attempt to modify existing privilege definition."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|46
argument_list|,
literal|"Attempt to modify existing privilege definition."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|isPrivilegeDefinition
argument_list|(
name|after
argument_list|)
condition|)
block|{
comment|// make sure privileges have been initialized before
name|Tree
name|parent
init|=
name|getPrivilegesTree
argument_list|(
name|rootBefore
argument_list|)
decl_stmt|;
comment|// the following characteristics are expected to be validated elsewhere:
comment|// - permission to allow privilege registration -> permission validator.
comment|// - name collisions (-> delegated to NodeTypeValidator since sms are not allowed)
comment|// - name must be valid (-> delegated to NameValidator)
comment|// name may not contain reserved namespace prefix
if|if
condition|(
name|NamespaceConstants
operator|.
name|RESERVED_PREFIXES
operator|.
name|contains
argument_list|(
name|Text
operator|.
name|getNamespacePrefix
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Failed to register custom privilege: Definition uses reserved namespace: "
operator|+
name|name
decl_stmt|;
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Privilege"
argument_list|,
literal|1
argument_list|,
name|msg
argument_list|)
throw|;
block|}
comment|// validate the definition
name|Tree
name|tree
init|=
name|treeProvider
operator|.
name|createReadOnlyTree
argument_list|(
name|parent
argument_list|,
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|validateDefinition
argument_list|(
name|tree
argument_list|)
expr_stmt|;
block|}
comment|// privilege definitions may not have child nodes (or another type of nodes
comment|// that is not handled by this validator anyway).
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|isPrivilegeDefinition
argument_list|(
name|before
argument_list|)
operator|&&
operator|!
name|before
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|41
argument_list|,
literal|"Attempt to modify existing privilege definition "
operator|+
name|name
argument_list|)
throw|;
block|}
else|else
block|{
comment|// not handled by this validator
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|isPrivilegeDefinition
argument_list|(
name|before
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|42
argument_list|,
literal|"Attempt to un-register privilege "
operator|+
name|name
argument_list|)
throw|;
block|}
else|else
block|{
comment|// not handled by this validator
return|return
literal|null
return|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|validateNext
parameter_list|(
name|PrivilegeBits
name|bits
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|PrivilegeBits
name|next
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|getPrivilegesTree
argument_list|(
name|rootAfter
argument_list|)
operator|.
name|getProperty
argument_list|(
name|REP_NEXT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|next
operator|.
name|equals
argument_list|(
name|bits
operator|.
name|nextBits
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|43
argument_list|,
literal|"Next bits not updated"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Tree
name|getPrivilegesTree
parameter_list|(
name|Root
name|root
parameter_list|)
throws|throws
name|CommitFailedException
block|{
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
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|44
argument_list|,
literal|"Privilege store not initialized."
argument_list|)
throw|;
block|}
return|return
name|privilegesTree
return|;
block|}
comment|/**      * Validation of the privilege definition including the following steps:      *<p>      * - privilege bits must not collide with an existing privilege      * - next bits must have been adjusted in case of a non-aggregate privilege      * - all aggregates must have been registered before      * - no existing privilege defines the same aggregation      * - no cyclic aggregation      *      * @param definitionTree The new privilege definition tree to validate.      * @throws org.apache.jackrabbit.oak.api.CommitFailedException      *          If any of      *          the checks listed above fails.      */
specifier|private
name|void
name|validateDefinition
parameter_list|(
name|Tree
name|definitionTree
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|PrivilegeBits
name|newBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|definitionTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|newBits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|48
argument_list|,
literal|"PrivilegeBits are missing."
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|privNames
init|=
name|bitsProvider
operator|.
name|getPrivilegeNames
argument_list|(
name|newBits
argument_list|)
decl_stmt|;
name|PrivilegeDefinition
name|definition
init|=
name|PrivilegeUtil
operator|.
name|readDefinition
argument_list|(
name|definitionTree
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|declaredNames
init|=
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
decl_stmt|;
comment|// non-aggregate privilege
if|if
condition|(
name|declaredNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|privNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|49
argument_list|,
literal|"PrivilegeBits already in used."
argument_list|)
throw|;
block|}
name|validateNext
argument_list|(
name|newBits
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// aggregation of a single privilege
if|if
condition|(
name|declaredNames
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|50
argument_list|,
literal|"Singular aggregation is equivalent to existing privilege."
argument_list|)
throw|;
block|}
comment|// aggregation of>1 privileges
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|PrivilegeDefinitionReader
argument_list|(
name|rootBefore
argument_list|)
operator|.
name|readDefinitions
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|aggrName
range|:
name|declaredNames
control|)
block|{
comment|// aggregated privilege not registered
if|if
condition|(
operator|!
name|definitions
operator|.
name|containsKey
argument_list|(
name|aggrName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|51
argument_list|,
literal|"Declared aggregate '"
operator|+
name|aggrName
operator|+
literal|"' is not a registered privilege."
argument_list|)
throw|;
block|}
comment|// check for circular aggregation
if|if
condition|(
name|isCircularAggregation
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
argument_list|,
name|aggrName
argument_list|,
name|definitions
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Detected circular aggregation within custom privilege caused by "
operator|+
name|aggrName
decl_stmt|;
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|52
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|aggregateNames
init|=
name|resolveAggregates
argument_list|(
name|declaredNames
argument_list|,
name|definitions
argument_list|)
decl_stmt|;
for|for
control|(
name|PrivilegeDefinition
name|existing
range|:
name|definitions
operator|.
name|values
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|existingDeclared
init|=
name|existing
operator|.
name|getDeclaredAggregateNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingDeclared
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// test for exact same aggregation or aggregation with the same net effect
if|if
condition|(
name|declaredNames
operator|.
name|equals
argument_list|(
name|existingDeclared
argument_list|)
operator|||
name|aggregateNames
operator|.
name|equals
argument_list|(
name|resolveAggregates
argument_list|(
name|existingDeclared
argument_list|,
name|definitions
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Custom aggregate privilege '"
operator|+
name|definition
operator|.
name|getName
argument_list|()
operator|+
literal|"' is already covered by '"
operator|+
name|existing
operator|.
name|getName
argument_list|()
operator|+
literal|'\''
decl_stmt|;
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|53
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
name|PrivilegeBits
name|aggrBits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|declaredNames
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|declaredNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|newBits
operator|.
name|equals
argument_list|(
name|aggrBits
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|53
argument_list|,
literal|"Invalid privilege bits for aggregated privilege definition."
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isCircularAggregation
parameter_list|(
name|String
name|privilegeName
parameter_list|,
name|String
name|aggregateName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
parameter_list|)
block|{
if|if
condition|(
name|privilegeName
operator|.
name|equals
argument_list|(
name|aggregateName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|PrivilegeDefinition
name|aggrPriv
init|=
name|definitions
operator|.
name|get
argument_list|(
name|aggregateName
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggrPriv
operator|.
name|getDeclaredAggregateNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|boolean
name|isCircular
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|aggrPriv
operator|.
name|getDeclaredAggregateNames
argument_list|()
control|)
block|{
if|if
condition|(
name|privilegeName
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|definitions
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|isCircular
operator|=
name|isCircularAggregation
argument_list|(
name|privilegeName
argument_list|,
name|name
argument_list|,
name|definitions
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|isCircular
return|;
block|}
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|resolveAggregates
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|declared
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|aggregateNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|declared
control|)
block|{
name|PrivilegeDefinition
name|d
init|=
name|definitions
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CONSTRAINT
argument_list|,
literal|47
argument_list|,
literal|"Invalid declared aggregate name "
operator|+
name|name
operator|+
literal|": Unknown privilege."
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|d
operator|.
name|getDeclaredAggregateNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|aggregateNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|aggregateNames
operator|.
name|addAll
argument_list|(
name|resolveAggregates
argument_list|(
name|names
argument_list|,
name|definitions
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|aggregateNames
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isPrivilegeDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
block|{
return|return
name|NT_REP_PRIVILEGE
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

