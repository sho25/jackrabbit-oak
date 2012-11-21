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
name|Arrays
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
name|collect
operator|.
name|Collections2
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
name|ValueFactoryImpl
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

begin_comment
comment|/**  * ACE... TODO  */
end_comment

begin_class
specifier|public
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
name|Privilege
index|[]
name|privileges
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
name|int
name|hashCode
decl_stmt|;
specifier|public
name|ACE
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
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|principal
operator|=
name|principal
expr_stmt|;
name|this
operator|.
name|privileges
operator|=
name|privileges
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
name|restrictions
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
comment|//-------------------------------------------------< AccessControlEntry>---
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
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getPrivileges
parameter_list|()
block|{
return|return
name|privileges
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
name|Override
specifier|public
name|String
index|[]
name|getRestrictionNames
parameter_list|()
throws|throws
name|RepositoryException
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
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|restriction
operator|.
name|getName
argument_list|()
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
name|String
name|oakName
init|=
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|restrictionName
argument_list|)
decl_stmt|;
for|for
control|(
name|Restriction
name|restriction
range|:
name|restrictions
control|)
block|{
if|if
condition|(
name|restriction
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|oakName
argument_list|)
condition|)
block|{
return|return
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
block|}
return|return
literal|null
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
operator|-
literal|1
condition|)
block|{
name|hashCode
operator|=
name|buildHashCode
argument_list|()
expr_stmt|;
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
name|equals
argument_list|(
name|other
operator|.
name|principal
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|privileges
argument_list|,
name|other
operator|.
name|privileges
argument_list|)
operator|&&
name|isAllow
operator|==
name|other
operator|.
name|isAllow
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
comment|/**      * @see Object#toString()      */
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
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|isAllow
argument_list|)
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|privileges
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|restrictions
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Build the hash code.      *      * @return the hash code.      */
specifier|private
name|int
name|buildHashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|17
decl_stmt|;
name|h
operator|=
literal|37
operator|*
name|h
operator|+
name|principal
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|37
operator|*
name|h
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|privileges
argument_list|)
expr_stmt|;
name|h
operator|=
literal|37
operator|*
name|h
operator|+
name|Boolean
operator|.
name|valueOf
argument_list|(
name|isAllow
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|37
operator|*
name|h
operator|+
name|restrictions
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

