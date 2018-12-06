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
name|Collections
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
name|ValueFormatException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Objects
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
name|Collections2
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
name|JackrabbitAccessControlEntry
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
name|plugins
operator|.
name|value
operator|.
name|jcr
operator|.
name|PartialValueFactory
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

begin_comment
comment|/**  * Default implementation of the {@code JackrabbitAccessControlEntry} interface.  * It asserts that the basic contract is fulfilled but does perform any additional  * validation on the principal, the privileges or the specified restrictions.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ACE
implements|implements
name|JackrabbitAccessControlEntry
block|{
specifier|private
specifier|final
name|Principal
name|principal
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBits
name|privilegeBits
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAllow
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|PartialValueFactory
name|valueFactory
decl_stmt|;
specifier|private
name|int
name|hashCode
decl_stmt|;
specifier|public
name|ACE
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
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|principal
operator|==
literal|null
operator|||
name|privilegeBits
operator|==
literal|null
operator|||
name|privilegeBits
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|()
throw|;
block|}
name|this
operator|.
name|principal
operator|=
name|principal
expr_stmt|;
name|this
operator|.
name|privilegeBits
operator|=
name|privilegeBits
expr_stmt|;
name|this
operator|.
name|isAllow
operator|=
name|isAllow
expr_stmt|;
name|this
operator|.
name|restrictions
operator|=
operator|(
name|restrictions
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
expr|<
name|Restriction
operator|>
name|emptySet
argument_list|()
else|:
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|restrictions
argument_list|)
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
operator|new
name|PartialValueFactory
argument_list|(
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
annotation|@
name|NotNull
specifier|public
name|PrivilegeBits
name|getPrivilegeBits
parameter_list|()
block|{
return|return
name|privilegeBits
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Set
argument_list|<
name|Restriction
argument_list|>
name|getRestrictions
parameter_list|()
block|{
return|return
name|restrictions
return|;
block|}
comment|//-------------------------------------------------< AccessControlEntry>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
comment|//---------------------------------------< JackrabbitAccessControlEntry>---
annotation|@
name|Override
specifier|public
name|boolean
name|isAllow
parameter_list|()
block|{
return|return
name|isAllow
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getRestrictionNames
parameter_list|()
block|{
return|return
name|Collections2
operator|.
name|transform
argument_list|(
name|restrictions
argument_list|,
operator|new
name|Function
argument_list|<
name|Restriction
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Restriction
name|restriction
parameter_list|)
block|{
return|return
name|getJcrName
argument_list|(
name|restriction
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|restrictions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Value
name|getRestriction
parameter_list|(
name|String
name|restrictionName
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|Restriction
name|restriction
range|:
name|restrictions
control|)
block|{
name|String
name|jcrName
init|=
name|getJcrName
argument_list|(
name|restriction
argument_list|)
decl_stmt|;
if|if
condition|(
name|jcrName
operator|.
name|equals
argument_list|(
name|restrictionName
argument_list|)
condition|)
block|{
if|if
condition|(
name|restriction
operator|.
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|valueFactory
operator|.
name|createValues
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|values
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
return|return
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Attempt to retrieve single value from multivalued property"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|valueFactory
operator|.
name|createValue
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getRestrictions
parameter_list|(
name|String
name|restrictionName
parameter_list|)
block|{
for|for
control|(
name|Restriction
name|restriction
range|:
name|restrictions
control|)
block|{
name|String
name|jcrName
init|=
name|getJcrName
argument_list|(
name|restriction
argument_list|)
decl_stmt|;
if|if
condition|(
name|jcrName
operator|.
name|equals
argument_list|(
name|restrictionName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|valueFactory
operator|.
name|createValues
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
return|return
literal|null
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
name|hashCode
operator|=
name|Objects
operator|.
name|hashCode
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|,
name|privilegeBits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
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
name|ACE
condition|)
block|{
name|ACE
name|other
init|=
operator|(
name|ACE
operator|)
name|obj
decl_stmt|;
return|return
name|principal
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|isAllow
operator|==
name|other
operator|.
name|isAllow
operator|&&
name|privilegeBits
operator|.
name|equals
argument_list|(
name|other
operator|.
name|privilegeBits
argument_list|)
operator|&&
name|restrictions
operator|.
name|equals
argument_list|(
name|other
operator|.
name|restrictions
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|String
name|getJcrName
parameter_list|(
name|Restriction
name|restriction
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|restriction
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

