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
name|authorization
operator|.
name|cug
operator|.
name|impl
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
name|JcrConstants
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|nodetype
operator|.
name|TypePredicate
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
name|commit
operator|.
name|VisibleValidator
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|ACCESS_CONTROL
import|;
end_import

begin_class
class|class
name|CugValidatorProvider
extends|extends
name|ValidatorProvider
implements|implements
name|CugConstants
block|{
specifier|private
name|TypePredicate
name|isMixCug
decl_stmt|;
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
name|this
operator|.
name|isMixCug
operator|=
operator|new
name|TypePredicate
argument_list|(
name|after
argument_list|,
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
return|return
operator|new
name|CugValidator
argument_list|(
literal|""
argument_list|,
name|after
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|CommitFailedException
name|accessViolation
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|CommitFailedException
argument_list|(
name|ACCESS_CONTROL
argument_list|,
name|code
argument_list|,
name|message
argument_list|)
return|;
block|}
specifier|private
name|void
name|validateCugNode
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|parent
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|nodeState
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|NT_REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|nodeState
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|21
argument_list|,
literal|"Reserved name 'rep:cugPolicy' must only be used for nodes of type 'rep:CugPolicy'."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|isMixCug
operator|.
name|test
argument_list|(
name|parent
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|22
argument_list|,
literal|"Parent node not of mixin type 'rep:CugMixin'."
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isNodetypeTree
parameter_list|(
name|CugValidator
name|parentValidator
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|parentValidator
operator|.
name|isNodetypeTree
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|NodeTypeConstants
operator|.
name|JCR_SYSTEM
operator|.
name|equals
argument_list|(
name|parentValidator
operator|.
name|parentName
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|CugValidator
extends|extends
name|DefaultValidator
block|{
specifier|private
specifier|final
name|NodeState
name|parentAfter
decl_stmt|;
specifier|private
specifier|final
name|String
name|parentName
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isNodetypeTree
decl_stmt|;
specifier|private
name|CugValidator
parameter_list|(
annotation|@
name|NotNull
name|String
name|parentName
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|parentAfter
parameter_list|,
name|boolean
name|isNodetypeTree
parameter_list|)
block|{
name|this
operator|.
name|parentAfter
operator|=
name|parentAfter
expr_stmt|;
name|this
operator|.
name|parentName
operator|=
name|parentName
expr_stmt|;
name|this
operator|.
name|isNodetypeTree
operator|=
name|isNodetypeTree
expr_stmt|;
block|}
comment|//------------------------------------------------------< Validator>---
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
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|NT_REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
operator|&&
operator|!
name|REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|parentName
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|23
argument_list|,
literal|"Attempt create Cug node with different name than 'rep:cugPolicy'."
argument_list|)
throw|;
block|}
block|}
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
name|String
name|name
init|=
name|after
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|NT_REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|before
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
operator|||
name|NT_REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
name|accessViolation
argument_list|(
literal|20
argument_list|,
literal|"Attempt to change primary type of/to CUG policy."
argument_list|)
throw|;
block|}
block|}
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
operator|!
name|isNodetypeTree
operator|&&
name|REP_CUG_POLICY
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|validateCugNode
argument_list|(
name|parentAfter
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|VisibleValidator
argument_list|(
operator|new
name|CugValidator
argument_list|(
name|name
argument_list|,
name|after
argument_list|,
name|isNodetypeTree
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
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
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|isNodetypeTree
operator|&&
name|after
operator|.
name|hasChildNode
argument_list|(
name|REP_CUG_POLICY
argument_list|)
condition|)
block|{
name|validateCugNode
argument_list|(
name|after
argument_list|,
name|after
operator|.
name|getChildNode
argument_list|(
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|VisibleValidator
argument_list|(
operator|new
name|CugValidator
argument_list|(
name|name
argument_list|,
name|after
argument_list|,
name|isNodetypeTree
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

