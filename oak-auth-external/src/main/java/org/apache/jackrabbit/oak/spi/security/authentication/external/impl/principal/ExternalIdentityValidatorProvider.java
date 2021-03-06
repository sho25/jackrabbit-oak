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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|ValidatorProvider
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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
name|principal
operator|.
name|SystemPrincipal
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * {@code ValidatorProvider} used to assure that the system maintained properties  * associated with external identities are only written by system sessions and  * are consistent.  *  * @since Oak 1.5.3  */
end_comment

begin_class
class|class
name|ExternalIdentityValidatorProvider
extends|extends
name|ValidatorProvider
implements|implements
name|ExternalIdentityConstants
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|ERROR_MSGS
init|=
name|ImmutableMap
operator|.
expr|<
name|Integer
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|70
argument_list|,
literal|"Attempt to create, modify or remove the system property 'rep:externalPrincipalNames'"
argument_list|)
decl|.
name|put
argument_list|(
literal|71
argument_list|,
literal|"Property 'rep:externalPrincipalNames' must be multi-valued of type STRING."
argument_list|)
decl|.
name|put
argument_list|(
literal|72
argument_list|,
literal|"Property 'rep:externalPrincipalNames' requires 'rep:externalId' to be present on the Node."
argument_list|)
decl|.
name|put
argument_list|(
literal|73
argument_list|,
literal|"Property 'rep:externalId' cannot be removed as long as 'rep:externalPrincipalNames' is present."
argument_list|)
decl|.
name|put
argument_list|(
literal|74
argument_list|,
literal|"Attempt to add, modify or remove the system maintained property 'rep:externalId'."
argument_list|)
decl|.
name|put
argument_list|(
literal|75
argument_list|,
literal|"Property 'rep:externalId' may only have a single value of type STRING."
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isSystem
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|protectedExternalIds
decl_stmt|;
name|ExternalIdentityValidatorProvider
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
name|boolean
name|protectExternalIds
parameter_list|)
block|{
name|isSystem
operator|=
name|principals
operator|.
name|contains
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|this
operator|.
name|protectedExternalIds
operator|=
name|protectExternalIds
expr_stmt|;
block|}
specifier|private
name|void
name|checkAddModifyProperties
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|PropertyState
name|propertyState
parameter_list|,
name|boolean
name|isModify
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|RESERVED_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|propertyState
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isSystem
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|70
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|70
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|STRINGS
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
operator|!
name|propertyState
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|71
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|71
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|parent
operator|.
name|hasProperty
argument_list|(
name|REP_EXTERNAL_ID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|72
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|72
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|REP_EXTERNAL_ID
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|protectedExternalIds
condition|)
block|{
if|if
condition|(
name|isModify
operator|&&
operator|!
name|isSystem
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|74
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|74
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|STRING
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
name|propertyState
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|75
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|75
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|checkRemoveProperties
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|parent
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|RESERVED_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|REP_EXTERNAL_ID
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|parent
operator|.
name|hasProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|73
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|73
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|protectedExternalIds
operator|&&
operator|!
name|isSystem
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|74
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|74
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|isSystem
condition|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|CommitFailedException
operator|.
name|CONSTRAINT
argument_list|,
literal|70
argument_list|,
name|ERROR_MSGS
operator|.
name|get
argument_list|(
literal|70
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
return|return
operator|new
name|ExternalIdentityValidator
argument_list|(
name|after
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
specifier|final
class|class
name|ExternalIdentityValidator
extends|extends
name|DefaultValidator
block|{
specifier|private
specifier|final
name|NodeState
name|parent
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|modifiedParent
decl_stmt|;
specifier|private
name|ExternalIdentityValidator
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|parent
parameter_list|,
name|boolean
name|modifiedParent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|modifiedParent
operator|=
name|modifiedParent
expr_stmt|;
block|}
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
name|checkAddModifyProperties
argument_list|(
name|parent
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|,
name|after
argument_list|,
name|modifiedParent
argument_list|)
expr_stmt|;
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
name|checkAddModifyProperties
argument_list|(
name|parent
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|,
name|after
argument_list|,
name|modifiedParent
argument_list|)
expr_stmt|;
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
name|checkRemoveProperties
argument_list|(
name|parent
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
block|{
return|return
operator|new
name|ExternalIdentityValidator
argument_list|(
name|after
argument_list|,
literal|false
argument_list|)
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
block|{
return|return
operator|new
name|ExternalIdentityValidator
argument_list|(
name|after
argument_list|,
literal|true
argument_list|)
return|;
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
block|{
comment|// removal of the parent node containing a reserved property must be possible
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

