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
name|ImmutableList
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProvider
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
comment|/**  * An implementation of the {@code JackrabbitAccessControlList} interface that only  * allows for reading. The write methods throw an {@code AccessControlException}.  */
end_comment

begin_class
specifier|public
class|class
name|ImmutableACL
extends|extends
name|AbstractAccessControlList
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
decl_stmt|;
specifier|private
specifier|final
name|RestrictionProvider
name|restrictionProvider
decl_stmt|;
specifier|private
name|int
name|hashCode
decl_stmt|;
comment|/**      * Construct a new {@code UnmodifiableAccessControlList}      *      * @param oakPath             The Oak path of this policy or {@code null}.      * @param entries             The access control entries contained in this policy.      * @param restrictionProvider The restriction provider.      * @param namePathMapper      The {@link NamePathMapper} used for conversion.      */
specifier|public
name|ImmutableACL
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|List
argument_list|<
name|?
extends|extends
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
parameter_list|,
annotation|@
name|NotNull
name|RestrictionProvider
name|restrictionProvider
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
name|this
operator|.
name|entries
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|this
operator|.
name|restrictionProvider
operator|=
name|restrictionProvider
expr_stmt|;
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
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Immutable ACL. Use AccessControlManager#getApplicablePolicies in order to obtain an modifiable ACL."
argument_list|)
throw|;
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
parameter_list|)
throws|throws
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Immutable ACL. Use AccessControlManager#getPolicy or #getApplicablePolicies in order to obtain an modifiable ACL."
argument_list|)
throw|;
block|}
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
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Immutable ACL. Use AccessControlManager#getPolicy or #getApplicablePolicies in order to obtain an modifiable ACL."
argument_list|)
throw|;
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
name|AccessControlException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Immutable ACL. Use AccessControlManager#getPolicy or #getApplicablePolicy in order to obtain a modifiable ACL."
argument_list|)
throw|;
block|}
comment|//------------------------------------------< AbstractAccessControlList>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|getEntries
parameter_list|()
block|{
return|return
name|entries
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|restrictionProvider
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
name|getOakPath
argument_list|()
argument_list|,
name|entries
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
name|ImmutableACL
condition|)
block|{
name|ImmutableACL
name|other
init|=
operator|(
name|ImmutableACL
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|getOakPath
argument_list|()
argument_list|,
name|other
operator|.
name|getOakPath
argument_list|()
argument_list|)
operator|&&
name|entries
operator|.
name|equals
argument_list|(
name|other
operator|.
name|entries
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

