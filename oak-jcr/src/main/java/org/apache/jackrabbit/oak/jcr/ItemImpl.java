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
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
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
name|ValueFactory
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
name|ItemDefinition
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
name|commons
operator|.
name|AbstractItem
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
comment|/**  * TODO document  */
end_comment

begin_class
specifier|abstract
class|class
name|ItemImpl
parameter_list|<
name|T
extends|extends
name|ItemDelegate
parameter_list|>
extends|extends
name|AbstractItem
block|{
comment|/**      * Flag to disable expensive transient item definition checks.      * FIXME: This flag should be removed once OAK-652 gets resolved.      */
specifier|protected
specifier|static
specifier|final
name|boolean
name|DISABLE_TRANSIENT_DEFINITION_CHECKS
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"OAK-652"
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|protected
specifier|final
name|T
name|dlg
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
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|T
name|itemDelegate
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|dlg
operator|=
name|itemDelegate
expr_stmt|;
block|}
comment|//---------------------------------------------------------------< Item>---
comment|/**      * @see javax.jcr.Item#getName()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|oakName
init|=
name|dlg
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// special case name of root node
return|return
name|oakName
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
name|toJcrPath
argument_list|(
name|dlg
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Property#getPath()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|toJcrPath
argument_list|(
name|dlg
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Session
name|getSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
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
name|RepositoryException
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Item#save is no longer supported. Please use Session#save instead."
argument_list|)
expr_stmt|;
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Item.save() not allowed on new item"
argument_list|)
throw|;
block|}
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
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
name|RepositoryException
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Item#refresh is no longer supported. Please use Session#refresh"
argument_list|)
expr_stmt|;
name|getSession
argument_list|()
operator|.
name|refresh
argument_list|(
name|keepChanges
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|isNode
argument_list|()
condition|?
literal|"Node["
else|:
literal|"Property["
operator|)
operator|+
name|dlg
operator|+
literal|']'
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
comment|/**      * Performs a sanity check on this item and the associated session.      *      * @throws RepositoryException if this item has been rendered invalid for some reason      */
name|void
name|checkStatus
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|dlg
operator|.
name|isStale
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"stale"
argument_list|)
throw|;
block|}
comment|// check session status
if|if
condition|(
operator|!
name|sessionDelegate
operator|.
name|isAlive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"This session has been closed."
argument_list|)
throw|;
block|}
comment|// TODO: validate item state.
block|}
name|void
name|checkProtected
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|DISABLE_TRANSIENT_DEFINITION_CHECKS
condition|)
block|{
return|return;
block|}
name|ItemDefinition
name|definition
decl_stmt|;
try|try
block|{
name|definition
operator|=
operator|(
name|isNode
argument_list|()
operator|)
condition|?
operator|(
operator|(
name|Node
operator|)
name|this
operator|)
operator|.
name|getDefinition
argument_list|()
else|:
operator|(
operator|(
name|Property
operator|)
name|this
operator|)
operator|.
name|getDefinition
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|ignore
parameter_list|)
block|{
comment|// No definition -> not protected but a different error which should be handled else where
return|return;
block|}
name|checkProtected
argument_list|(
name|definition
argument_list|)
expr_stmt|;
block|}
name|void
name|checkProtected
parameter_list|(
name|ItemDefinition
name|definition
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|definition
operator|.
name|isProtected
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Item is protected."
argument_list|)
throw|;
block|}
block|}
comment|/**      * Ensure that the associated session has no pending changes and throw an      * exception otherwise.      *      * @throws InvalidItemStateException if this nodes session has pending changes      * @throws RepositoryException      */
name|void
name|ensureNoPendingSessionChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// check for pending changes
if|if
condition|(
name|sessionDelegate
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Unable to perform operation. Session has pending changes."
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the value factory associated with the editing session.      *      * @return the value factory      */
annotation|@
name|Nonnull
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
name|String
name|toJcrPath
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

