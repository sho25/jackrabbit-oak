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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|jmx
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
name|oak
operator|.
name|api
operator|.
name|ContentRepository
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
name|authentication
operator|.
name|external
operator|.
name|ExternalIdentityProvider
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
name|authentication
operator|.
name|external
operator|.
name|ExternalIdentityProviderManager
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
name|authentication
operator|.
name|external
operator|.
name|SyncHandler
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
name|authentication
operator|.
name|external
operator|.
name|SyncManager
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
comment|/**  * Implementation of the {@link SynchronizationMBean} interface.  */
end_comment

begin_class
specifier|public
class|class
name|SyncMBeanImpl
implements|implements
name|SynchronizationMBean
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
name|SyncMBeanImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ContentRepository
name|repository
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|SyncManager
name|syncManager
decl_stmt|;
specifier|private
specifier|final
name|String
name|syncName
decl_stmt|;
specifier|private
specifier|final
name|ExternalIdentityProviderManager
name|idpManager
decl_stmt|;
specifier|private
specifier|final
name|String
name|idpName
decl_stmt|;
specifier|public
name|SyncMBeanImpl
parameter_list|(
annotation|@
name|NotNull
name|ContentRepository
name|repository
parameter_list|,
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|,
annotation|@
name|NotNull
name|SyncManager
name|syncManager
parameter_list|,
annotation|@
name|NotNull
name|String
name|syncName
parameter_list|,
annotation|@
name|NotNull
name|ExternalIdentityProviderManager
name|idpManager
parameter_list|,
annotation|@
name|NotNull
name|String
name|idpName
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
name|this
operator|.
name|syncManager
operator|=
name|syncManager
expr_stmt|;
name|this
operator|.
name|syncName
operator|=
name|syncName
expr_stmt|;
name|this
operator|.
name|idpManager
operator|=
name|idpManager
expr_stmt|;
name|this
operator|.
name|idpName
operator|=
name|idpName
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|private
name|Delegatee
name|getDelegatee
parameter_list|()
block|{
name|SyncHandler
name|handler
init|=
name|syncManager
operator|.
name|getSyncHandler
argument_list|(
name|syncName
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No sync manager available for name {}."
argument_list|,
name|syncName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No sync manager available for name "
operator|+
name|syncName
argument_list|)
throw|;
block|}
name|ExternalIdentityProvider
name|idp
init|=
name|idpManager
operator|.
name|getProvider
argument_list|(
name|idpName
argument_list|)
decl_stmt|;
if|if
condition|(
name|idp
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No idp available for name"
argument_list|,
name|idpName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No idp manager available for name "
operator|+
name|idpName
argument_list|)
throw|;
block|}
return|return
name|Delegatee
operator|.
name|createInstance
argument_list|(
name|repository
argument_list|,
name|securityProvider
argument_list|,
name|handler
argument_list|,
name|idp
argument_list|)
return|;
block|}
comment|//-----------------------------------------------< SynchronizationMBean>---
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getSyncHandlerName
parameter_list|()
block|{
return|return
name|syncName
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|getIDPName
parameter_list|()
block|{
return|return
name|idpName
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|syncUsers
parameter_list|(
annotation|@
name|NotNull
name|String
index|[]
name|userIds
parameter_list|,
name|boolean
name|purge
parameter_list|)
block|{
name|Delegatee
name|delegatee
init|=
name|getDelegatee
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|delegatee
operator|.
name|syncUsers
argument_list|(
name|userIds
argument_list|,
name|purge
argument_list|)
return|;
block|}
finally|finally
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|syncAllUsers
parameter_list|(
name|boolean
name|purge
parameter_list|)
block|{
name|Delegatee
name|delegatee
init|=
name|getDelegatee
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|delegatee
operator|.
name|syncAllUsers
argument_list|(
name|purge
argument_list|)
return|;
block|}
finally|finally
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|syncExternalUsers
parameter_list|(
annotation|@
name|NotNull
name|String
index|[]
name|externalIds
parameter_list|)
block|{
name|Delegatee
name|delegatee
init|=
name|getDelegatee
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|delegatee
operator|.
name|syncExternalUsers
argument_list|(
name|externalIds
argument_list|)
return|;
block|}
finally|finally
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|syncAllExternalUsers
parameter_list|()
block|{
name|Delegatee
name|delegatee
init|=
name|getDelegatee
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|delegatee
operator|.
name|syncAllExternalUsers
argument_list|()
return|;
block|}
finally|finally
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listOrphanedUsers
parameter_list|()
block|{
name|Delegatee
name|delegatee
init|=
name|getDelegatee
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|delegatee
operator|.
name|listOrphanedUsers
argument_list|()
return|;
block|}
finally|finally
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|purgeOrphanedUsers
parameter_list|()
block|{
name|Delegatee
name|delegatee
init|=
name|getDelegatee
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|delegatee
operator|.
name|purgeOrphanedUsers
argument_list|()
return|;
block|}
finally|finally
block|{
name|delegatee
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

