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
name|Collection
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlList
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
comment|/**  * Abstract base implementation of the {@code JackrabbitAccessControlList}  * interface.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAccessControlList
implements|implements
name|JackrabbitAccessControlList
block|{
specifier|private
specifier|final
name|String
name|oakPath
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|public
name|AbstractAccessControlList
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|oakPath
operator|=
name|oakPath
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
comment|//------------------------------------------< AbstractAccessControlList>---
annotation|@
name|Nullable
specifier|public
name|String
name|getOakPath
parameter_list|()
block|{
return|return
name|oakPath
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
return|return
name|namePathMapper
return|;
block|}
annotation|@
name|NotNull
specifier|public
specifier|abstract
name|List
argument_list|<
name|?
extends|extends
name|JackrabbitAccessControlEntry
argument_list|>
name|getEntries
parameter_list|()
function_decl|;
annotation|@
name|NotNull
specifier|public
specifier|abstract
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
function_decl|;
comment|//--------------------------------------< JackrabbitAccessControlPolicy>---
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
operator|(
name|oakPath
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
comment|//--------------------------------------------------< AccessControlList>---
annotation|@
name|Override
specifier|public
name|AccessControlEntry
index|[]
name|getAccessControlEntries
parameter_list|()
block|{
name|List
argument_list|<
name|?
extends|extends
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
init|=
name|getEntries
argument_list|()
decl_stmt|;
return|return
name|entries
operator|.
name|toArray
argument_list|(
operator|new
name|JackrabbitAccessControlEntry
index|[
literal|0
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addAccessControlEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privileges
argument_list|,
literal|true
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
comment|//----------------------------------------< JackrabbitAccessControlList>---
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|getEntries
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
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
name|Collection
argument_list|<
name|RestrictionDefinition
argument_list|>
name|supported
init|=
name|getRestrictionProvider
argument_list|()
operator|.
name|getSupportedRestrictions
argument_list|(
name|getOakPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Collections2
operator|.
name|transform
argument_list|(
name|supported
argument_list|,
operator|new
name|Function
argument_list|<
name|RestrictionDefinition
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
name|RestrictionDefinition
name|definition
parameter_list|)
block|{
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|definition
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
name|supported
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
name|int
name|getRestrictionType
parameter_list|(
annotation|@
name|NotNull
name|String
name|restrictionName
parameter_list|)
block|{
for|for
control|(
name|RestrictionDefinition
name|definition
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
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
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
return|return
name|definition
operator|.
name|getRequiredType
argument_list|()
operator|.
name|tag
argument_list|()
return|;
block|}
block|}
comment|// for backwards compatibility with JR2 return undefined type for an
comment|// unknown restriction name.
return|return
name|PropertyType
operator|.
name|UNDEFINED
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiValueRestriction
parameter_list|(
annotation|@
name|NotNull
name|String
name|restrictionName
parameter_list|)
block|{
for|for
control|(
name|RestrictionDefinition
name|definition
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
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
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
return|return
name|definition
operator|.
name|getRequiredType
argument_list|()
operator|.
name|isArray
argument_list|()
return|;
block|}
block|}
comment|// not a supported restriction => return false.
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addEntry
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privileges
argument_list|,
name|isAllow
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addEntry
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privileges
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

