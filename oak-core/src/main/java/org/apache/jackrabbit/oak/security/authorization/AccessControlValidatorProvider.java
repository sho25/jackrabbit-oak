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
package|;
end_package

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
name|collect
operator|.
name|ImmutableMap
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
name|api
operator|.
name|Root
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
name|core
operator|.
name|ImmutableRoot
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
name|core
operator|.
name|ImmutableTree
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|commit
operator|.
name|Validator
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
name|commit
operator|.
name|ValidatorProvider
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
name|SecurityProvider
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
name|AccessControlConfiguration
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
name|PrivilegeConfiguration
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
name|state
operator|.
name|NodeState
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
comment|/**  * {@code AccessControlValidatorProvider} aimed to provide a root validator  * that makes sure access control related content modifications (adding, modifying  * and removing access control policies) are valid according to the  * constraints defined by this access control implementation.  */
end_comment

begin_class
class|class
name|AccessControlValidatorProvider
extends|extends
name|ValidatorProvider
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
name|AccessControlValidatorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
name|AccessControlValidatorProvider
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
block|}
comment|//--------------------------------------------------< ValidatorProvider>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|Tree
name|rootBefore
init|=
operator|new
name|ImmutableTree
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|Tree
name|rootAfter
init|=
operator|new
name|ImmutableTree
argument_list|(
name|after
argument_list|)
decl_stmt|;
name|AccessControlConfiguration
name|acConfig
init|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
decl_stmt|;
name|RestrictionProvider
name|restrictionProvider
init|=
name|acConfig
operator|.
name|getRestrictionProvider
argument_list|(
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Privilege
argument_list|>
name|privileges
init|=
name|getPrivileges
argument_list|(
name|before
argument_list|,
name|securityProvider
operator|.
name|getPrivilegeConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|ReadOnlyNodeTypeManager
name|ntMgr
init|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|before
argument_list|)
decl_stmt|;
return|return
operator|new
name|AccessControlValidator
argument_list|(
name|rootBefore
argument_list|,
name|rootAfter
argument_list|,
name|privileges
argument_list|,
name|restrictionProvider
argument_list|,
name|ntMgr
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Privilege
argument_list|>
name|getPrivileges
parameter_list|(
name|NodeState
name|beforeRoot
parameter_list|,
name|PrivilegeConfiguration
name|config
parameter_list|)
block|{
name|Root
name|root
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|beforeRoot
argument_list|)
decl_stmt|;
name|PrivilegeManager
name|pMgr
init|=
name|config
operator|.
name|getPrivilegeManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
name|privileges
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Privilege
name|privilege
range|:
name|pMgr
operator|.
name|getRegisteredPrivileges
argument_list|()
control|)
block|{
name|privileges
operator|.
name|put
argument_list|(
name|privilege
operator|.
name|getName
argument_list|()
argument_list|,
name|privilege
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected error: failed to read privileges."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|privileges
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

