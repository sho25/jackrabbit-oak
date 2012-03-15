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
name|jcr
operator|.
name|SessionImpl
operator|.
name|Context
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
name|jcr
operator|.
name|state
operator|.
name|TransientNodeState
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
name|jcr
operator|.
name|util
operator|.
name|Path
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
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemExistsException
import|;
end_import

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
name|ReferentialIntegrityException
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockException
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
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NoSuchNodeTypeException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionException
import|;
end_import

begin_comment
comment|/**  *<code>ItemImpl</code>...  */
end_comment

begin_class
specifier|abstract
class|class
name|ItemImpl
implements|implements
name|Item
block|{
specifier|protected
specifier|final
name|Context
name|sessionContext
decl_stmt|;
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
name|ItemImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ItemImpl
parameter_list|(
name|Context
name|sessionContext
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
block|}
comment|//---------------------------------------------------------------< Item>---
annotation|@
name|Override
specifier|public
name|Session
name|getSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionContext
operator|.
name|getSession
argument_list|()
return|;
block|}
comment|/**      * @see Item#isSame(javax.jcr.Item)      */
annotation|@
name|Override
specifier|public
name|boolean
name|isSame
parameter_list|(
name|Item
name|otherItem
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|this
operator|==
name|otherItem
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// The objects are either both Node objects or both Property objects.
if|if
condition|(
name|isNode
argument_list|()
operator|!=
name|otherItem
operator|.
name|isNode
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Test if both items belong to the same repository
comment|// created by the same Repository object
if|if
condition|(
operator|!
name|getSession
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|equals
argument_list|(
name|otherItem
operator|.
name|getSession
argument_list|()
operator|.
name|getRepository
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Both objects were acquired through Session objects bound to the same
comment|// repository workspace.
if|if
condition|(
operator|!
name|getSession
argument_list|()
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|otherItem
operator|.
name|getSession
argument_list|()
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|isNode
argument_list|()
condition|)
block|{
return|return
operator|(
operator|(
name|Node
operator|)
name|this
operator|)
operator|.
name|getIdentifier
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Node
operator|)
name|otherItem
operator|)
operator|.
name|getIdentifier
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|otherItem
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|getParent
argument_list|()
operator|.
name|isSame
argument_list|(
name|otherItem
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * @see javax.jcr.Item#save()      */
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|AccessDeniedException
throws|,
name|ItemExistsException
throws|,
name|ConstraintViolationException
throws|,
name|InvalidItemStateException
throws|,
name|ReferentialIntegrityException
throws|,
name|VersionException
throws|,
name|LockException
throws|,
name|NoSuchNodeTypeException
throws|,
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Use Session#save"
argument_list|)
throw|;
block|}
comment|/**      * @see Item#refresh(boolean)      */
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|(
name|boolean
name|keepChanges
parameter_list|)
throws|throws
name|InvalidItemStateException
throws|,
name|RepositoryException
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Use Session#refresh"
argument_list|)
throw|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Performs a sanity check on this item and the associated session.      *      * @throws RepositoryException if this item has been rendered invalid for some reason      */
name|void
name|checkStatus
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// check session status
name|sessionContext
operator|.
name|getSession
argument_list|()
operator|.
name|checkIsAlive
argument_list|()
expr_stmt|;
comment|// TODO: validate item state.
block|}
comment|/**      * Checks if the associated session has pending changes.      *      * @throws InvalidItemStateException if this nodes session has pending changes      * @throws RepositoryException      */
name|void
name|checkSessionHasPendingChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|sessionContext
operator|.
name|getSession
argument_list|()
operator|.
name|checkHasPendingChanges
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the value factory associated with the editing session.      *      * @return the value factory      * @throws RepositoryException      */
name|ValueFactory
name|getValueFactory
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|TransientNodeState
name|getNodeState
parameter_list|(
name|Context
name|sessionContext
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
return|return
name|sessionContext
operator|.
name|getNodeStateProvider
argument_list|()
operator|.
name|getNodeState
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

