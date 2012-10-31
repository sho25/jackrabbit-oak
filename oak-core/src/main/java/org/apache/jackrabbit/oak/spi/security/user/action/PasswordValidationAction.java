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
name|user
operator|.
name|action
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|namepath
operator|.
name|NamePathMapper
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * {@code PasswordValidationAction} provides a simple password validation  * mechanism with the following configurable option:  *  *<ul>  *<li><strong>constraint</strong>: a regular expression that can be compiled  *     to a {@link java.util.regex.Pattern} defining validation rules for a password.</li>  *</ul>  *  *<p>The password validation is executed on user creation and upon password  * change. It throws a {@code ConstraintViolationException} if the password  * validation fails.</p>  *  * @see org.apache.jackrabbit.api.security.user.UserManager#createUser(String, String)  * @see org.apache.jackrabbit.api.security.user.User#changePassword(String)  * @see org.apache.jackrabbit.api.security.user.User#changePassword(String, String)  */
end_comment

begin_class
specifier|public
class|class
name|PasswordValidationAction
extends|extends
name|AbstractAuthorizableAction
block|{
comment|/**      * logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PasswordValidationAction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Pattern
name|pattern
decl_stmt|;
comment|//-------------------------------------------------< AuthorizableAction>---
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|validatePassword
argument_list|(
name|password
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onPasswordChange
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|newPassword
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|validatePassword
argument_list|(
name|newPassword
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------< Configuration>---
comment|/**      * Set the password constraint.      *      * @param constraint A regular expression that can be used to validate a new password.      */
specifier|public
name|void
name|setConstraint
parameter_list|(
name|String
name|constraint
parameter_list|)
block|{
try|try
block|{
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|constraint
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid password constraint: "
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Validate the specified password.      *      * @param password The password to be validated      * @param forceMatch If true the specified password is always validated;      * otherwise only if it is a plain text password.      * @throws RepositoryException If the specified password is too short or      * doesn't match the specified password pattern.      */
specifier|private
name|void
name|validatePassword
parameter_list|(
name|String
name|password
parameter_list|,
name|boolean
name|forceMatch
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|password
operator|!=
literal|null
operator|&&
operator|(
name|forceMatch
operator|||
name|PasswordUtility
operator|.
name|isPlainTextPassword
argument_list|(
name|password
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
name|pattern
operator|!=
literal|null
operator|&&
operator|!
name|pattern
operator|.
name|matcher
argument_list|(
name|password
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Password violates password constraint ("
operator|+
name|pattern
operator|.
name|pattern
argument_list|()
operator|+
literal|")."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

