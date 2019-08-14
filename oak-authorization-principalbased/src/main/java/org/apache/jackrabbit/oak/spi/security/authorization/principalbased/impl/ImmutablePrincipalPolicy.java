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
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|PrincipalAccessControlList
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
name|ImmutableACL
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

begin_class
class|class
name|ImmutablePrincipalPolicy
extends|extends
name|ImmutableACL
implements|implements
name|PrincipalAccessControlList
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
name|ImmutablePrincipalPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Principal
name|principal
decl_stmt|;
specifier|private
name|int
name|hashCode
decl_stmt|;
specifier|public
name|ImmutablePrincipalPolicy
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|List
argument_list|<
name|?
extends|extends
name|PrincipalAccessControlList
operator|.
name|Entry
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
name|entries
argument_list|,
name|restrictionProvider
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|this
operator|.
name|principal
operator|=
name|principal
expr_stmt|;
block|}
specifier|public
name|ImmutablePrincipalPolicy
parameter_list|(
annotation|@
name|NotNull
name|PrincipalPolicyImpl
name|accessControlList
parameter_list|)
block|{
name|super
argument_list|(
name|accessControlList
argument_list|)
expr_stmt|;
name|this
operator|.
name|principal
operator|=
name|accessControlList
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
block|}
comment|//-----------------------------------------< PrincipalAccessControlList>---
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
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
name|boolean
name|addEntry
parameter_list|(
annotation|@
name|Nullable
name|String
name|effectivePath
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Immutable PrincipalAccessControlList."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addEntry
parameter_list|(
annotation|@
name|Nullable
name|String
name|effectivePath
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|,
annotation|@
name|NotNull
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
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Immutable PrincipalAccessControlList."
argument_list|)
throw|;
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
argument_list|,
name|getOakPath
argument_list|()
argument_list|,
name|getEntries
argument_list|()
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
name|ImmutablePrincipalPolicy
condition|)
block|{
name|ImmutablePrincipalPolicy
name|other
init|=
operator|(
name|ImmutablePrincipalPolicy
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
name|principal
operator|.
name|equals
argument_list|(
name|other
operator|.
name|principal
argument_list|)
operator|&&
name|getEntries
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getEntries
argument_list|()
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

