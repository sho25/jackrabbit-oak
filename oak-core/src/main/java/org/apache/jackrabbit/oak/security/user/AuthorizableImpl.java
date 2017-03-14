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
name|RepositoryException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|Iterators
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
name|commons
operator|.
name|iterator
operator|.
name|RangeIteratorAdapter
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
name|spi
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
name|UserConstants
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

begin_comment
comment|/**  * Base class for {@code User} and {@code Group} implementations.  */
end_comment

begin_class
specifier|abstract
class|class
name|AuthorizableImpl
implements|implements
name|Authorizable
implements|,
name|UserConstants
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
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|tree
decl_stmt|;
specifier|private
specifier|final
name|UserManagerImpl
name|userManager
decl_stmt|;
specifier|private
name|String
name|principalName
decl_stmt|;
specifier|private
name|AuthorizableProperties
name|properties
decl_stmt|;
specifier|private
name|int
name|hashCode
decl_stmt|;
name|AuthorizableImpl
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nonnull
name|UserManagerImpl
name|userManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkValidTree
argument_list|(
name|tree
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|tree
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
name|checkValidTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
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
annotation|@
name|Override
specifier|public
name|String
name|getID
parameter_list|()
block|{
return|return
name|id
return|;
block|}
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
name|userManager
operator|.
name|onRemove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
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
return|return
name|getAuthorizableProperties
argument_list|()
operator|.
name|getNames
argument_list|(
name|relPath
argument_list|)
return|;
block|}
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
name|getAuthorizableProperties
argument_list|()
operator|.
name|hasProperty
argument_list|(
name|relPath
argument_list|)
return|;
block|}
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
return|return
name|getAuthorizableProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
name|relPath
argument_list|)
return|;
block|}
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
name|getAuthorizableProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
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
name|getAuthorizableProperties
argument_list|()
operator|.
name|setProperty
argument_list|(
name|relPath
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
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
return|return
name|getAuthorizableProperties
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|relPath
argument_list|)
return|;
block|}
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
name|userManager
operator|.
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
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
literal|':'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|userManager
operator|.
name|hashCode
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
return|return
name|hashCode
return|;
block|}
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
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
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
return|return
name|isGroup
argument_list|()
operator|==
name|otherAuth
operator|.
name|isGroup
argument_list|()
operator|&&
name|id
operator|.
name|equals
argument_list|(
name|otherAuth
operator|.
name|id
argument_list|)
operator|&&
name|userManager
operator|.
name|equals
argument_list|(
name|otherAuth
operator|.
name|userManager
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
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
name|id
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
comment|//--------------------------------------------------------------------------
annotation|@
name|Nonnull
name|Tree
name|getTree
parameter_list|()
block|{
if|if
condition|(
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|tree
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Authorizable "
operator|+
name|id
operator|+
literal|": underlying tree has been disconnected."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
name|String
name|getPrincipalName
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|principalName
operator|==
literal|null
condition|)
block|{
name|PropertyState
name|pNameProp
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|pNameProp
operator|!=
literal|null
condition|)
block|{
name|principalName
operator|=
name|pNameProp
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"Authorizable without principal name "
operator|+
name|id
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
return|return
name|principalName
return|;
block|}
comment|/**      * @return The user manager associated with this authorizable.      */
annotation|@
name|Nonnull
name|UserManagerImpl
name|getUserManager
parameter_list|()
block|{
return|return
name|userManager
return|;
block|}
comment|/**      * @return The membership provider associated with this authorizable      */
annotation|@
name|Nonnull
name|MembershipProvider
name|getMembershipProvider
parameter_list|()
block|{
return|return
name|userManager
operator|.
name|getMembershipProvider
argument_list|()
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
comment|/**      * Retrieve authorizable properties for property related operations.      *      * @return The authorizable properties for this user/group.      */
specifier|private
name|AuthorizableProperties
name|getAuthorizableProperties
parameter_list|()
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|AuthorizablePropertiesImpl
argument_list|(
name|this
argument_list|,
name|userManager
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
comment|/**      * Retrieve the group membership of this authorizable.      *      * @param includeInherited Flag indicating whether the resulting iterator only      * contains groups this authorizable is declared member of or if inherited      * group membership is respected.      *      * @return Iterator of groups this authorizable is (declared) member of.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|Nonnull
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
name|MembershipProvider
name|mMgr
init|=
name|getMembershipProvider
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|oakPaths
init|=
name|mMgr
operator|.
name|getMembership
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|includeInherited
argument_list|)
decl_stmt|;
name|Authorizable
name|everyoneGroup
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|everyoneGroup
operator|instanceof
name|GroupImpl
condition|)
block|{
name|String
name|everyonePath
init|=
operator|(
operator|(
name|GroupImpl
operator|)
name|everyoneGroup
operator|)
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|oakPaths
operator|=
name|Iterators
operator|.
name|concat
argument_list|(
name|oakPaths
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|everyonePath
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|oakPaths
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AuthorizableIterator
name|groups
init|=
name|AuthorizableIterator
operator|.
name|create
argument_list|(
name|oakPaths
argument_list|,
name|userManager
argument_list|,
name|AuthorizableType
operator|.
name|GROUP
argument_list|)
decl_stmt|;
return|return
operator|new
name|RangeIteratorAdapter
argument_list|(
name|groups
argument_list|,
name|groups
operator|.
name|getSize
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RangeIteratorAdapter
operator|.
name|EMPTY
return|;
block|}
block|}
block|}
end_class

end_unit

