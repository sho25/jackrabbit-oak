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
name|user
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
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
name|security
operator|.
name|user
operator|.
name|AuthorizableType
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
name|user
operator|.
name|util
operator|.
name|PasswordUtility
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
name|user
operator|.
name|UserConstants
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
name|user
operator|.
name|util
operator|.
name|UserUtility
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
name|util
operator|.
name|NodeUtil
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

begin_comment
comment|/**  * UserValidator... TODO  */
end_comment

begin_class
class|class
name|UserValidator
extends|extends
name|DefaultValidator
implements|implements
name|UserConstants
block|{
specifier|private
specifier|final
name|UserValidatorProvider
name|provider
decl_stmt|;
specifier|private
specifier|final
name|NodeUtil
name|parentBefore
decl_stmt|;
specifier|private
specifier|final
name|NodeUtil
name|parentAfter
decl_stmt|;
name|UserValidator
parameter_list|(
name|NodeUtil
name|parentBefore
parameter_list|,
name|NodeUtil
name|parentAfter
parameter_list|,
name|UserValidatorProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|parentBefore
operator|=
name|parentBefore
expr_stmt|;
name|this
operator|.
name|parentAfter
operator|=
name|parentAfter
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
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
name|REP_DISABLED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|isAdminUser
argument_list|(
name|parentAfter
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Admin user cannot be disabled."
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
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
name|before
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|UserUtility
operator|.
name|isAuthorizableTree
argument_list|(
name|parentBefore
operator|.
name|getTree
argument_list|()
argument_list|)
operator|&&
operator|(
name|REP_PRINCIPAL_NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|REP_AUTHORIZABLE_ID
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Authorizable property "
operator|+
name|name
operator|+
literal|" may not be altered after user/group creation."
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|UserUtility
operator|.
name|isAuthorizableTree
argument_list|(
name|parentBefore
operator|.
name|getTree
argument_list|()
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
operator|&&
name|REP_PASSWORD
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|PasswordUtility
operator|.
name|isPlainTextPassword
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
name|String
name|msg
init|=
literal|"Password may not be plain text."
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
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
name|String
name|name
init|=
name|before
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|UserUtility
operator|.
name|isAuthorizableTree
argument_list|(
name|parentBefore
operator|.
name|getTree
argument_list|()
argument_list|)
operator|&&
operator|(
name|REP_PASSWORD
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|REP_PRINCIPAL_NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|REP_AUTHORIZABLE_ID
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Authorizable property "
operator|+
name|name
operator|+
literal|" may not be removed."
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
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
name|NodeUtil
name|node
init|=
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|authRoot
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasPrimaryNodeTypeName
argument_list|(
name|NT_REP_USER
argument_list|)
condition|)
block|{
name|authRoot
operator|=
name|provider
operator|.
name|getConfig
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_PATH
argument_list|,
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|hasPrimaryNodeTypeName
argument_list|(
name|UserConstants
operator|.
name|NT_REP_GROUP
argument_list|)
condition|)
block|{
name|authRoot
operator|=
name|provider
operator|.
name|getConfig
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_GROUP_PATH
argument_list|,
name|DEFAULT_GROUP_PATH
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|authRoot
operator|!=
literal|null
condition|)
block|{
name|assertHierarchy
argument_list|(
name|node
argument_list|,
name|authRoot
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UserValidator
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
name|provider
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
comment|// TODO: anything to do here?
return|return
operator|new
name|UserValidator
argument_list|(
name|parentBefore
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|,
name|parentAfter
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|,
name|provider
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
throws|throws
name|CommitFailedException
block|{
name|NodeUtil
name|node
init|=
name|parentBefore
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAdminUser
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"The admin user cannot be removed."
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Make sure user and group nodes are located underneath the configured path      * and that path consists of rep:authorizableFolder nodes.      *      * @param userNode      * @param pathConstraint      * @throws CommitFailedException      */
specifier|private
name|void
name|assertHierarchy
parameter_list|(
name|NodeUtil
name|userNode
parameter_list|,
name|String
name|pathConstraint
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|Text
operator|.
name|isDescendant
argument_list|(
name|pathConstraint
argument_list|,
name|userNode
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Attempt to create user/group outside of configured scope "
operator|+
name|pathConstraint
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|NodeUtil
name|parent
init|=
name|userNode
operator|.
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|parent
operator|.
name|getTree
argument_list|()
operator|.
name|isRoot
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|parent
operator|.
name|hasPrimaryNodeTypeName
argument_list|(
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Cannot create user/group: Intermediate folders must be of type rep:AuthorizableFolder."
decl_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|parent
operator|=
name|parent
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
block|}
comment|// FIXME: copied from UserProvider#isAdminUser
specifier|private
name|boolean
name|isAdminUser
parameter_list|(
name|NodeUtil
name|userNode
parameter_list|)
block|{
name|String
name|id
init|=
operator|(
name|userNode
operator|.
name|getString
argument_list|(
name|REP_AUTHORIZABLE_ID
argument_list|,
name|Text
operator|.
name|unescapeIllegalJcrChars
argument_list|(
name|userNode
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|)
decl_stmt|;
return|return
name|UserUtility
operator|.
name|isAuthorizableTree
argument_list|(
name|userNode
operator|.
name|getTree
argument_list|()
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
operator|&&
name|UserUtility
operator|.
name|getAdminId
argument_list|(
name|provider
operator|.
name|getConfig
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|fail
parameter_list|(
name|String
name|msg
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Exception
name|e
init|=
operator|new
name|ConstraintViolationException
argument_list|(
name|msg
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|CommitFailedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

