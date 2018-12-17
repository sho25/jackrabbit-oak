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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|Lists
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
name|ConfigurationParameters
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
name|PasswordUtil
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
name|TreeUtil
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
comment|/**  * Helper class for the password history feature.  */
end_comment

begin_class
specifier|final
class|class
name|PasswordHistory
implements|implements
name|UserConstants
block|{
specifier|private
specifier|static
specifier|final
name|int
name|HISTORY_MAX_SIZE
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isEnabled
decl_stmt|;
name|PasswordHistory
parameter_list|(
annotation|@
name|NotNull
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|maxSize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|HISTORY_MAX_SIZE
argument_list|,
name|config
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HISTORY_SIZE
argument_list|,
name|UserConstants
operator|.
name|PASSWORD_HISTORY_DISABLED_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|isEnabled
operator|=
name|maxSize
operator|>
name|UserConstants
operator|.
name|PASSWORD_HISTORY_DISABLED_SIZE
expr_stmt|;
block|}
comment|/**      * If password history is enabled this method validates the new password and      * updated the history; otherwise it returns {@code false}.      *      * @param userTree The user tree.      * @param password The new password to be validated.      * @return {@code true} if the history is enabled, the new password is not      * included in the history and the history was successfully updated;      * {@code false} otherwise.      * @throws javax.jcr.nodetype.ConstraintViolationException If the feature      * is enabled and the new password is found in the history.      * @throws javax.jcr.AccessDeniedException If the rep:pwd tree cannot be      * accessed.      */
name|boolean
name|updatePasswordHistory
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|userTree
parameter_list|,
annotation|@
name|NotNull
name|String
name|password
parameter_list|)
throws|throws
name|ConstraintViolationException
throws|,
name|AccessDeniedException
block|{
name|boolean
name|updated
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|isEnabled
condition|)
block|{
name|checkPasswordInHistory
argument_list|(
name|userTree
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|shiftPasswordHistory
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
name|updated
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|updated
return|;
block|}
comment|/**      * Update the history property with the current pw-hash stored in rep:password      * and trim the list of hashes in the list according to the configured maxSize.      *      * @param userTree The user tree.      * @throws AccessDeniedException If the editing session cannot access or      * create the rep:pwd node.      */
specifier|private
name|void
name|shiftPasswordHistory
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|userTree
parameter_list|)
throws|throws
name|AccessDeniedException
block|{
name|String
name|currentPasswordHash
init|=
name|TreeUtil
operator|.
name|getString
argument_list|(
name|userTree
argument_list|,
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentPasswordHash
operator|!=
literal|null
condition|)
block|{
name|Tree
name|passwordTree
init|=
name|getPasswordTree
argument_list|(
name|userTree
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PropertyState
name|historyProp
init|=
name|passwordTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PWD_HISTORY
argument_list|)
decl_stmt|;
comment|// insert the current (old) password at the beginning of the password history
name|List
argument_list|<
name|String
argument_list|>
name|historyEntries
init|=
operator|(
name|historyProp
operator|==
literal|null
operator|)
condition|?
operator|new
name|ArrayList
argument_list|<>
argument_list|()
else|:
name|Lists
operator|.
name|newArrayList
argument_list|(
name|historyProp
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
decl_stmt|;
name|historyEntries
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|currentPasswordHash
argument_list|)
expr_stmt|;
comment|/* remove oldest history entries exceeding configured history max size (e.g. after              * a configuration change)              */
if|if
condition|(
name|historyEntries
operator|.
name|size
argument_list|()
operator|>
name|maxSize
condition|)
block|{
name|historyEntries
operator|=
name|historyEntries
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
block|}
name|passwordTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PWD_HISTORY
argument_list|,
name|historyEntries
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Verify that the specified new password is not contained in the history.      *      * @param userTree The user tree.      * @param newPassword The new password      * @throws ConstraintViolationException If the passsword is found in the history      * @throws AccessDeniedException If the editing session cannot access the rep:pwd node.      */
specifier|private
name|void
name|checkPasswordInHistory
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|userTree
parameter_list|,
annotation|@
name|NotNull
name|String
name|newPassword
parameter_list|)
throws|throws
name|ConstraintViolationException
throws|,
name|AccessDeniedException
block|{
if|if
condition|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|TreeUtil
operator|.
name|getString
argument_list|(
name|userTree
argument_list|,
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|,
name|newPassword
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PasswordHistoryException
argument_list|(
literal|"New password is identical to the current password."
argument_list|)
throw|;
block|}
name|Tree
name|pwTree
init|=
name|getPasswordTree
argument_list|(
name|userTree
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|pwTree
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|pwHistoryProperty
init|=
name|pwTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PWD_HISTORY
argument_list|)
decl_stmt|;
if|if
condition|(
name|pwHistoryProperty
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|historyPwHash
range|:
name|Iterables
operator|.
name|limit
argument_list|(
name|pwHistoryProperty
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
name|maxSize
argument_list|)
control|)
block|{
if|if
condition|(
name|PasswordUtil
operator|.
name|isSame
argument_list|(
name|historyPwHash
argument_list|,
name|newPassword
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PasswordHistoryException
argument_list|(
literal|"New password was found in password history."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|Tree
name|getPasswordTree
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|userTree
parameter_list|,
name|boolean
name|doCreate
parameter_list|)
throws|throws
name|AccessDeniedException
block|{
if|if
condition|(
name|doCreate
condition|)
block|{
return|return
name|TreeUtil
operator|.
name|getOrAddChild
argument_list|(
name|userTree
argument_list|,
name|UserConstants
operator|.
name|REP_PWD
argument_list|,
name|UserConstants
operator|.
name|NT_REP_PASSWORD
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|userTree
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_PWD
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

