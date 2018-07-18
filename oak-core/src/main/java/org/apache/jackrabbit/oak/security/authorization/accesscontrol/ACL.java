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
name|authorization
operator|.
name|accesscontrol
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
name|HashSet
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
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|base
operator|.
name|Predicate
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
name|api
operator|.
name|security
operator|.
name|authorization
operator|.
name|PrivilegeManager
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|ACE
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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AbstractAccessControlList
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
name|authorization
operator|.
name|restriction
operator|.
name|Restriction
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
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionDefinition
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
name|privilege
operator|.
name|PrivilegeBits
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
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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

begin_class
specifier|abstract
class|class
name|ACL
extends|extends
name|AbstractAccessControlList
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
name|ACL
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|ACE
argument_list|>
argument_list|()
decl_stmt|;
name|ACL
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|super
argument_list|(
name|oakPath
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|entries
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|entries
operator|.
name|addAll
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
specifier|abstract
name|ACE
name|createACE
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|PrivilegeBits
name|privilegeBits
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
specifier|abstract
name|boolean
name|checkValidPrincipal
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
specifier|abstract
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
function_decl|;
specifier|abstract
name|PrivilegeBits
name|getPrivilegeBits
parameter_list|(
name|Privilege
index|[]
name|privileges
parameter_list|)
function_decl|;
comment|//------------------------------------------< AbstractAccessControlList>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ACE
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|entries
return|;
block|}
comment|//--------------------------------------------------< AccessControlList>---
annotation|@
name|Override
specifier|public
name|void
name|removeAccessControlEntry
parameter_list|(
name|AccessControlEntry
name|ace
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ACE
name|entry
init|=
name|checkACE
argument_list|(
name|ace
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entries
operator|.
name|remove
argument_list|(
name|entry
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Cannot remove AccessControlEntry "
operator|+
name|ace
argument_list|)
throw|;
block|}
block|}
comment|//----------------------------------------< JackrabbitAccessControlList>---
annotation|@
name|Override
specifier|public
name|boolean
name|addEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
index|[]
argument_list|>
name|mvRestrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|privileges
operator|==
literal|null
operator|||
name|privileges
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Privileges may not be null nor an empty array"
argument_list|)
throw|;
block|}
for|for
control|(
name|Privilege
name|p
range|:
name|privileges
control|)
block|{
name|Privilege
name|pv
init|=
name|getPrivilegeManager
argument_list|()
operator|.
name|getPrivilege
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pv
operator|.
name|isAbstract
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Privilege "
operator|+
name|p
operator|+
literal|" is abstract."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|checkValidPrincipal
argument_list|(
name|principal
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|RestrictionDefinition
name|def
range|:
name|getRestrictionProvider
argument_list|()
operator|.
name|getSupportedRestrictions
argument_list|(
name|getOakPath
argument_list|()
argument_list|)
control|)
block|{
name|String
name|jcrName
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|.
name|isMandatory
argument_list|()
operator|&&
operator|(
name|restrictions
operator|==
literal|null
operator|||
operator|!
name|restrictions
operator|.
name|containsKey
argument_list|(
name|jcrName
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Mandatory restriction "
operator|+
name|jcrName
operator|+
literal|" is missing."
argument_list|)
throw|;
block|}
block|}
name|Set
argument_list|<
name|Restriction
argument_list|>
name|rs
decl_stmt|;
if|if
condition|(
name|restrictions
operator|==
literal|null
operator|&&
name|mvRestrictions
operator|==
literal|null
condition|)
block|{
name|rs
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rs
operator|=
operator|new
name|HashSet
argument_list|<
name|Restriction
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|restrictions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|jcrName
range|:
name|restrictions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|oakName
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
name|rs
operator|.
name|add
argument_list|(
name|getRestrictionProvider
argument_list|()
operator|.
name|createRestriction
argument_list|(
name|getOakPath
argument_list|()
argument_list|,
name|oakName
argument_list|,
name|restrictions
operator|.
name|get
argument_list|(
name|oakName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mvRestrictions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|jcrName
range|:
name|mvRestrictions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|oakName
init|=
name|getNamePathMapper
argument_list|()
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
name|rs
operator|.
name|add
argument_list|(
name|getRestrictionProvider
argument_list|()
operator|.
name|createRestriction
argument_list|(
name|getOakPath
argument_list|()
argument_list|,
name|oakName
argument_list|,
name|mvRestrictions
operator|.
name|get
argument_list|(
name|oakName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ACE
name|entry
init|=
name|createACE
argument_list|(
name|principal
argument_list|,
name|getPrivilegeBits
argument_list|(
name|privileges
argument_list|)
argument_list|,
name|isAllow
argument_list|,
name|rs
argument_list|)
decl_stmt|;
if|if
condition|(
name|entries
operator|.
name|contains
argument_list|(
name|entry
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Entry is already contained in policy -> no modification."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|internalAddEntry
argument_list|(
name|entry
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|orderBefore
parameter_list|(
name|AccessControlEntry
name|srcEntry
parameter_list|,
name|AccessControlEntry
name|destEntry
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ACE
name|src
init|=
name|checkACE
argument_list|(
name|srcEntry
argument_list|)
decl_stmt|;
name|ACE
name|dest
init|=
operator|(
name|destEntry
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|checkACE
argument_list|(
name|destEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|.
name|equals
argument_list|(
name|dest
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"'srcEntry' equals 'destEntry' -> no reordering required."
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|index
init|=
operator|(
name|dest
operator|==
literal|null
operator|)
condition|?
name|entries
operator|.
name|size
argument_list|()
operator|-
literal|1
else|:
name|entries
operator|.
name|indexOf
argument_list|(
name|dest
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"'destEntry' not contained in this AccessControlList."
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|entries
operator|.
name|remove
argument_list|(
name|src
argument_list|)
condition|)
block|{
comment|// re-insert the srcEntry at the new position.
name|entries
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// src entry not contained in this list.
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"srcEntry not contained in this AccessControlList"
argument_list|)
throw|;
block|}
block|}
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
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
literal|"ACL: "
argument_list|)
operator|.
name|append
argument_list|(
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; ACEs: "
argument_list|)
expr_stmt|;
for|for
control|(
name|AccessControlEntry
name|ace
range|:
name|entries
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ace
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Check validity of the specified access control entry.      *      * @param entry The access control entry to test.      * @return The validated {@code ACE}.      * @throws AccessControlException If the specified entry is invalid.      */
specifier|private
specifier|static
name|ACE
name|checkACE
parameter_list|(
name|AccessControlEntry
name|entry
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
operator|(
name|entry
operator|instanceof
name|ACE
operator|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Invalid access control entry."
argument_list|)
throw|;
block|}
return|return
operator|(
name|ACE
operator|)
name|entry
return|;
block|}
specifier|private
name|boolean
name|internalAddEntry
parameter_list|(
annotation|@
name|NotNull
name|ACE
name|entry
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|Principal
name|principal
init|=
name|entry
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ACE
argument_list|>
name|subList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|entries
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ACE
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|ACE
name|ace
parameter_list|)
block|{
return|return
operator|(
name|ace
operator|!=
literal|null
operator|)
operator|&&
name|ace
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|addEntry
init|=
literal|true
decl_stmt|;
for|for
control|(
name|ACE
name|existing
range|:
name|subList
control|)
block|{
name|PrivilegeBits
name|existingBits
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|existing
operator|.
name|getPrivilegeBits
argument_list|()
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|entryBits
init|=
name|entry
operator|.
name|getPrivilegeBits
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getRestrictions
argument_list|()
operator|.
name|equals
argument_list|(
name|existing
operator|.
name|getRestrictions
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|isAllow
argument_list|()
operator|==
name|existing
operator|.
name|isAllow
argument_list|()
condition|)
block|{
if|if
condition|(
name|existingBits
operator|.
name|includes
argument_list|(
name|entryBits
argument_list|)
condition|)
block|{
comment|// no changes
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// merge existing and new ace
name|existingBits
operator|.
name|add
argument_list|(
name|entryBits
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|entries
operator|.
name|indexOf
argument_list|(
name|existing
argument_list|)
decl_stmt|;
name|entries
operator|.
name|remove
argument_list|(
name|existing
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|createACE
argument_list|(
name|existing
argument_list|,
name|existingBits
argument_list|)
argument_list|)
expr_stmt|;
name|addEntry
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// existing is complementary entry -> clean up redundant
comment|// privileges defined by the existing entry
name|PrivilegeBits
name|updated
init|=
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|existingBits
argument_list|)
operator|.
name|diff
argument_list|(
name|entryBits
argument_list|)
decl_stmt|;
if|if
condition|(
name|updated
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// remove the existing entry as the new entry covers all privileges
name|entries
operator|.
name|remove
argument_list|(
name|existing
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|updated
operator|.
name|includes
argument_list|(
name|existingBits
argument_list|)
condition|)
block|{
comment|// replace the existing entry having it's privileges adjusted
name|int
name|index
init|=
name|entries
operator|.
name|indexOf
argument_list|(
name|existing
argument_list|)
decl_stmt|;
name|entries
operator|.
name|remove
argument_list|(
name|existing
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|createACE
argument_list|(
name|existing
argument_list|,
name|updated
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* else: no collision that requires adjusting the existing entry.*/
block|}
block|}
block|}
comment|// finally add the new entry at the end of the list
if|if
condition|(
name|addEntry
condition|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|ACE
name|createACE
parameter_list|(
annotation|@
name|NotNull
name|ACE
name|existing
parameter_list|,
annotation|@
name|NotNull
name|PrivilegeBits
name|newPrivilegeBits
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createACE
argument_list|(
name|existing
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|newPrivilegeBits
argument_list|,
name|existing
operator|.
name|isAllow
argument_list|()
argument_list|,
name|existing
operator|.
name|getRestrictions
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

