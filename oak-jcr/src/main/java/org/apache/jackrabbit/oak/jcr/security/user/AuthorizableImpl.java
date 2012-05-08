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
operator|.
name|security
operator|.
name|user
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|Group
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
name|jcr
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
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
name|PropertyIterator
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
name|nodetype
operator|.
name|NodeType
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
name|PropertyDefinition
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * AuthorizableImpl...  */
end_comment

begin_class
specifier|abstract
class|class
name|AuthorizableImpl
implements|implements
name|Authorizable
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
name|AuthorizableImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|NT_REP_AUTHORIZABLE
init|=
literal|"rep:Authorizable"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NT_REP_USER
init|=
literal|"rep:User"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NT_REP_GROUP
init|=
literal|"rep:Group"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NT_REP_MEMBERS
init|=
literal|"rep:Members"
decl_stmt|;
specifier|static
specifier|final
name|String
name|REP_PRINCIPAL_NAME
init|=
literal|"rep:principalName"
decl_stmt|;
specifier|static
specifier|final
name|String
name|REP_PASSWORD
init|=
literal|"rep:password"
decl_stmt|;
specifier|static
specifier|final
name|String
name|REP_DISABLED
init|=
literal|"rep:disabled"
decl_stmt|;
specifier|static
specifier|final
name|String
name|REP_MEMBERS
init|=
literal|"rep:members"
decl_stmt|;
specifier|static
specifier|final
name|String
name|REP_IMPERSONATORS
init|=
literal|"rep:impersonators"
decl_stmt|;
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
specifier|private
specifier|final
name|UserManagerImpl
name|userManager
decl_stmt|;
specifier|private
name|int
name|hashCode
decl_stmt|;
name|AuthorizableImpl
parameter_list|(
name|Node
name|node
parameter_list|,
name|UserManagerImpl
name|userManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkValidNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|userManager
operator|=
name|userManager
expr_stmt|;
block|}
specifier|abstract
name|void
name|checkValidNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
specifier|static
name|boolean
name|isValidAuthorizableImpl
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
block|{
return|return
name|authorizable
operator|instanceof
name|AuthorizableImpl
return|;
block|}
comment|//-------------------------------------------------------< Authorizable>---
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#getID()      */
annotation|@
name|Override
specifier|public
name|String
name|getID
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|Text
operator|.
name|unescapeIllegalJcrChars
argument_list|(
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @see Authorizable#declaredMemberOf()      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|declaredMemberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getMembership
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**      * @see Authorizable#memberOf()      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|memberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getMembership
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#remove()      */
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// don't allow for removal of the administrator even if the executing
comment|// session has all permissions.
if|if
condition|(
operator|!
name|isGroup
argument_list|()
operator|&&
operator|(
operator|(
name|User
operator|)
name|this
operator|)
operator|.
name|isAdmin
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"The administrator cannot be removed."
argument_list|)
throw|;
block|}
name|Session
name|s
init|=
name|node
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|userManager
operator|.
name|onRemove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|node
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#getPropertyNames()      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getPropertyNames
argument_list|(
literal|"."
argument_list|)
return|;
block|}
comment|/**      * @see Authorizable#getPropertyNames(String)      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|n
init|=
name|node
operator|.
name|getNode
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyIterator
name|it
init|=
name|n
operator|.
name|getProperties
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Property
name|prop
init|=
name|it
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|prop
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|l
operator|.
name|add
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Relative path "
operator|+
name|relPath
operator|+
literal|" refers to items outside of scope of authorizable "
operator|+
name|getID
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#hasProperty(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|node
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
operator|&&
name|isAuthorizableProperty
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#getProperty(String)      */
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Value
index|[]
name|values
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
name|Property
name|prop
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|prop
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|prop
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|values
operator|=
name|prop
operator|.
name|getValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
operator|new
name|Value
index|[]
block|{
name|prop
operator|.
name|getValue
argument_list|()
block|}
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#setProperty(String, javax.jcr.Value)      */
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|relPath
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|String
name|intermediate
init|=
operator|(
name|relPath
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|?
literal|null
else|:
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|relPath
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|getOrCreateTargetNode
argument_list|(
name|intermediate
argument_list|)
decl_stmt|;
comment|// check if the property has already been created as multi valued
comment|// property before -> in this case remove in order to avoid
comment|// ValueFormatException.
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|p
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|n
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#setProperty(String, javax.jcr.Value[])      */
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|relPath
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|String
name|intermediate
init|=
operator|(
name|relPath
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
condition|?
literal|null
else|:
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|relPath
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|getOrCreateTargetNode
argument_list|(
name|intermediate
argument_list|)
decl_stmt|;
comment|// check if the property has already been created as single valued
comment|// property before -> in this case remove in order to avoid
comment|// ValueFormatException.
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|p
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|n
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#removeProperty(String)      */
annotation|@
name|Override
specifier|public
name|boolean
name|removeProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAuthorizableProperty
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|p
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|// no such property or wasn't a property of this authorizable.
return|return
literal|false
return|;
block|}
comment|/**      * @see org.apache.jackrabbit.api.security.user.Authorizable#getPath()      */
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|node
operator|.
name|getPath
argument_list|()
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
comment|/**      * @see Object#hashCode()      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|isGroup
argument_list|()
condition|?
literal|"group:"
else|:
literal|"user:"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|node
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
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|node
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|hashCode
operator|=
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{             }
block|}
return|return
name|hashCode
return|;
block|}
comment|/**      * @see Object#equals(Object)      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|AuthorizableImpl
condition|)
block|{
name|AuthorizableImpl
name|otherAuth
init|=
operator|(
name|AuthorizableImpl
operator|)
name|obj
decl_stmt|;
try|try
block|{
return|return
name|isGroup
argument_list|()
operator|==
name|otherAuth
operator|.
name|isGroup
argument_list|()
operator|&&
name|node
operator|.
name|isSame
argument_list|(
name|otherAuth
operator|.
name|node
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// should not occur -> return false in this case.
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * @see Object#toString()      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|String
name|typeStr
init|=
operator|(
name|isGroup
argument_list|()
operator|)
condition|?
literal|"Group '"
else|:
literal|"User '"
decl_stmt|;
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|typeStr
argument_list|)
operator|.
name|append
argument_list|(
name|getID
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * @return The node associated with this authorizable instance.      */
name|Node
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
comment|/**      * @return The user manager associated with this authorizable.      */
name|UserManagerImpl
name|getUserManager
parameter_list|()
block|{
return|return
name|userManager
return|;
block|}
comment|/**      * @return The principal name of this authorizable.      * @throws RepositoryException If no principal name can be retrieved.      */
name|String
name|getPrincipalName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|principalName
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
name|principalName
operator|=
name|node
operator|.
name|getProperty
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Authorizable without principal name -> using ID as fallback."
argument_list|)
expr_stmt|;
name|principalName
operator|=
name|getID
argument_list|()
expr_stmt|;
block|}
return|return
name|principalName
return|;
block|}
comment|/**      * Returns {@code true} if this authorizable represents the 'everyone' group.      *      * @return {@code true} if this authorizable represents the group everyone      * is member of; {@code false} otherwise.      * @throws RepositoryException If an error occurs.      */
name|boolean
name|isEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|isGroup
argument_list|()
operator|&&
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|getPrincipalName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns true if the given property of the authorizable node is one of the      * non-protected properties defined by the rep:Authorizable node type or a      * some other descendant of the authorizable node.      *      * @param prop Property to be tested.      * @param verifyAncestor If true the property is tested to be a descendant      * of the node of this authorizable; otherwise it is expected that this      * test has been executed by the caller.      * @return {@code true} if the given property is defined      * by the rep:authorizable node type or one of it's sub-node types;      * {@code false} otherwise.      * @throws RepositoryException If the property definition cannot be retrieved.      */
specifier|private
name|boolean
name|isAuthorizableProperty
parameter_list|(
name|Property
name|prop
parameter_list|,
name|boolean
name|verifyAncestor
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|verifyAncestor
operator|&&
operator|!
name|Text
operator|.
name|isDescendant
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|,
name|prop
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Attempt to access property outside of authorizable scope."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|PropertyDefinition
name|def
init|=
name|prop
operator|.
name|getDefinition
argument_list|()
decl_stmt|;
if|if
condition|(
name|def
operator|.
name|isProtected
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isSame
argument_list|(
name|prop
operator|.
name|getParent
argument_list|()
argument_list|)
condition|)
block|{
name|NodeType
name|declaringNt
init|=
name|prop
operator|.
name|getDefinition
argument_list|()
operator|.
name|getDeclaringNodeType
argument_list|()
decl_stmt|;
return|return
name|declaringNt
operator|.
name|isNodeType
argument_list|(
name|NT_REP_AUTHORIZABLE
argument_list|)
return|;
block|}
else|else
block|{
comment|// another non-protected property somewhere in the subtree of this
comment|// authorizable node -> is a property that can be set using #setProperty.
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Retrieves the node at {@code relPath} relative to node associated with      * this authorizable. If no such node exist it and any missing intermediate      * nodes are created.      *      * @param relPath A relative path.      * @return The corresponding node.      * @throws RepositoryException If an error occurs or if {@code relPath} refers      * to a node that is outside of the scope of this authorizable.      */
specifier|private
name|Node
name|getOrCreateTargetNode
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|n
decl_stmt|;
if|if
condition|(
name|relPath
operator|!=
literal|null
condition|)
block|{
name|String
name|userPath
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
name|n
operator|=
name|node
operator|.
name|getNode
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|userPath
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Relative path "
operator|+
name|relPath
operator|+
literal|" outside of scope of "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|n
operator|=
name|node
expr_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|Text
operator|.
name|explode
argument_list|(
name|relPath
argument_list|,
literal|'/'
argument_list|)
control|)
block|{
if|if
condition|(
name|n
operator|.
name|hasNode
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|getNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|userPath
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|addNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Relative path "
operator|+
name|relPath
operator|+
literal|" outside of scope of "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
else|else
block|{
name|n
operator|=
name|node
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
comment|/**      * Retrieve the group membership of this authorizable.      *      * @param includeInherited Flag indicating whether the resulting iterator only      * contains groups this authorizable is declared member of or if inherited      * group membership is respected.      *      * @return Iterator of groups this authorizable is (declared) member of.      * @throws RepositoryException If an error occurs.      */
specifier|private
name|Iterator
argument_list|<
name|Group
argument_list|>
name|getMembership
parameter_list|(
name|boolean
name|includeInherited
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|isEveryone
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
expr|<
name|Group
operator|>
name|emptySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
name|MembershipManager
name|membershipManager
init|=
name|userManager
operator|.
name|getMembershipManager
argument_list|()
decl_stmt|;
return|return
name|membershipManager
operator|.
name|getMembership
argument_list|(
name|this
argument_list|,
name|includeInherited
argument_list|)
return|;
block|}
block|}
end_class

end_unit

