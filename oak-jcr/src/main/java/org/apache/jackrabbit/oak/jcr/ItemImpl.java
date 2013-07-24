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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|Type
operator|.
name|NAME
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
name|Type
operator|.
name|NAMES
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
name|Type
operator|.
name|PATH
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
name|Type
operator|.
name|PATHS
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
name|Type
operator|.
name|STRING
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
name|Type
operator|.
name|UNDEFINED
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
name|Type
operator|.
name|UNDEFINEDS
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
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
name|annotation
operator|.
name|CheckForNull
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
name|AccessDeniedException
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
name|ItemNotFoundException
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
name|PathNotFoundException
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
name|Value
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
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
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
name|VersionManager
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
name|commons
operator|.
name|PathUtils
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
name|delegate
operator|.
name|ItemDelegate
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
name|delegate
operator|.
name|NodeDelegate
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
name|delegate
operator|.
name|SessionDelegate
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
name|delegate
operator|.
name|SessionOperation
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
name|memory
operator|.
name|MemoryPropertyBuilder
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
name|DefinitionProvider
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
name|EffectiveNodeTypeProvider
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
implements|implements
name|Item
block|{
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
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|protected
specifier|final
name|T
name|dlg
decl_stmt|;
specifier|protected
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|protected
name|ItemImpl
parameter_list|(
name|T
name|itemDelegate
parameter_list|,
name|SessionContext
name|sessionContext
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|dlg
operator|=
name|itemDelegate
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
class|class
name|ItemReadOperation
parameter_list|<
name|U
parameter_list|>
extends|extends
name|SessionOperation
argument_list|<
name|U
argument_list|>
block|{
annotation|@
name|Override
specifier|protected
name|void
name|checkPreconditions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dlg
operator|.
name|checkAlive
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
class|class
name|ItemWriteOperation
parameter_list|<
name|U
parameter_list|>
extends|extends
name|SessionOperation
argument_list|<
name|U
argument_list|>
block|{
specifier|protected
name|ItemWriteOperation
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|checkPreconditions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|dlg
operator|.
name|checkAlive
argument_list|()
expr_stmt|;
name|checkProtected
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Perform the passed {@link org.apache.jackrabbit.oak.jcr.ItemImpl.ItemReadOperation}.      * @param op  operation to perform      * @param<U>  return type of the operation      * @return  the result of {@code op.perform()}      * @throws RepositoryException as thrown by {@code op.perform()}.      */
annotation|@
name|CheckForNull
specifier|protected
specifier|final
parameter_list|<
name|U
parameter_list|>
name|U
name|perform
parameter_list|(
annotation|@
name|Nonnull
name|SessionOperation
argument_list|<
name|U
argument_list|>
name|op
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
name|op
argument_list|)
return|;
block|}
comment|/**      * Perform the passed {@link org.apache.jackrabbit.oak.jcr.ItemImpl.ItemReadOperation} assuming it does not throw an      * {@code RepositoryException}. If it does, wrap it into and throw it as an      * {@code IllegalArgumentException}.      * @param op  operation to perform      * @param<U>  return type of the operation      * @return  the result of {@code op.perform()}      */
annotation|@
name|CheckForNull
specifier|protected
specifier|final
parameter_list|<
name|U
parameter_list|>
name|U
name|safePerform
parameter_list|(
annotation|@
name|Nonnull
name|SessionOperation
argument_list|<
name|U
argument_list|>
name|op
parameter_list|)
block|{
try|try
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
name|op
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Unexpected exception thrown by operation "
operator|+
name|op
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|perform
argument_list|(
operator|new
name|ItemReadOperation
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
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
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
name|sessionContext
operator|.
name|getSession
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Item
name|getAncestor
parameter_list|(
specifier|final
name|int
name|depth
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Item
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Item
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|depth
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|this
operator|+
literal|": Invalid ancestor depth ("
operator|+
name|depth
operator|+
literal|')'
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|depth
operator|==
literal|0
condition|)
block|{
name|NodeDelegate
name|nd
init|=
name|sessionDelegate
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|nd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|(
literal|"Root node is not accessible."
argument_list|)
throw|;
block|}
return|return
name|sessionContext
operator|.
name|createNodeOrNull
argument_list|(
name|nd
argument_list|)
return|;
block|}
name|String
name|path
init|=
name|dlg
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|slash
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|depth
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|slash
operator|=
name|PathUtils
operator|.
name|getNextSlash
argument_list|(
name|path
argument_list|,
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|this
operator|+
literal|": Invalid ancestor depth ("
operator|+
name|depth
operator|+
literal|')'
argument_list|)
throw|;
block|}
block|}
name|slash
operator|=
name|PathUtils
operator|.
name|getNextSlash
argument_list|(
name|path
argument_list|,
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|ItemImpl
operator|.
name|this
return|;
block|}
name|NodeDelegate
name|nd
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|nd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessDeniedException
argument_list|(
name|this
operator|+
literal|": Ancestor access denied ("
operator|+
name|depth
operator|+
literal|')'
argument_list|)
throw|;
block|}
return|return
name|sessionContext
operator|.
name|createNodeOrNull
argument_list|(
name|nd
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDepth
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|perform
argument_list|(
operator|new
name|ItemReadOperation
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|PathUtils
operator|.
name|getDepth
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
name|void
name|checkProtected
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|dlg
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
name|void
name|checkProtected
parameter_list|(
name|ItemDefinition
name|definition
parameter_list|)
throws|throws
name|ConstraintViolationException
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
annotation|@
name|Nonnull
name|String
name|getOakName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionContext
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|String
name|getOakPathOrThrow
parameter_list|(
name|String
name|jcrPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionContext
operator|.
name|getOakPathOrThrow
argument_list|(
name|jcrPath
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|String
name|getOakPathOrThrowNotFound
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|PathNotFoundException
block|{
return|return
name|sessionContext
operator|.
name|getOakPathOrThrowNotFound
argument_list|(
name|relPath
argument_list|)
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
name|sessionContext
operator|.
name|getJcrPath
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
comment|/**      * Returns the value factory associated with the editing session.      *      * @return the value factory      */
annotation|@
name|Nonnull
name|ValueFactory
name|getValueFactory
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getValueFactory
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
name|NodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getNodeTypeManager
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
name|DefinitionProvider
name|getDefinitionProvider
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getDefinitionProvider
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
name|EffectiveNodeTypeProvider
name|getEffectiveNodeTypeProvider
parameter_list|()
block|{
return|return
name|sessionContext
operator|.
name|getEffectiveNodeTypeProvider
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
name|VersionManager
name|getVersionManager
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionContext
operator|.
name|getVersionManager
argument_list|()
return|;
block|}
specifier|protected
name|PropertyState
name|createSingleState
parameter_list|(
name|String
name|oakName
parameter_list|,
name|Value
name|value
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|type
operator|==
name|UNDEFINED
condition|)
block|{
name|type
operator|=
name|Type
operator|.
name|fromTag
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|NAME
operator|||
name|type
operator|==
name|PATH
condition|)
block|{
return|return
name|createProperty
argument_list|(
name|oakName
argument_list|,
name|getOakValue
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createProperty
argument_list|(
name|oakName
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
specifier|protected
name|PropertyState
name|createMultiState
parameter_list|(
name|String
name|oakName
parameter_list|,
name|List
argument_list|<
name|Value
argument_list|>
name|values
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|base
init|=
name|type
operator|.
name|getBaseType
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|==
name|UNDEFINED
condition|)
block|{
name|base
operator|=
name|STRING
expr_stmt|;
block|}
return|return
name|MemoryPropertyBuilder
operator|.
name|array
argument_list|(
name|base
argument_list|)
operator|.
name|setName
argument_list|(
name|oakName
argument_list|)
operator|.
name|getPropertyState
argument_list|()
return|;
block|}
if|if
condition|(
name|type
operator|==
name|UNDEFINEDS
condition|)
block|{
name|type
operator|=
name|Type
operator|.
name|fromTag
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|NAMES
operator|||
name|type
operator|==
name|PATHS
condition|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|base
init|=
name|type
operator|.
name|getBaseType
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|newArrayListWithCapacity
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|getOakValue
argument_list|(
name|value
argument_list|,
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|createProperty
argument_list|(
name|oakName
argument_list|,
name|strings
argument_list|,
name|type
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createProperty
argument_list|(
name|oakName
argument_list|,
name|values
argument_list|,
name|type
operator|.
name|tag
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
name|String
name|getOakValue
parameter_list|(
name|Value
name|value
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|type
operator|==
name|NAME
condition|)
block|{
return|return
name|getOakName
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PATH
condition|)
block|{
name|String
name|path
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
comment|// leave identifiers unmodified
name|path
operator|=
name|getOakPathOrThrow
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

