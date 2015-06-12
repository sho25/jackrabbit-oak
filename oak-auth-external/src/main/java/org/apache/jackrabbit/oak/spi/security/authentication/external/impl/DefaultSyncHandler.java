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
package|;
end_package

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
name|ValueFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|UserManager
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
name|AbstractLazyIterator
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
name|ConfigurationParameters
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
name|SyncContext
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
name|SyncException
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
name|SyncedIdentity
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
name|basic
operator|.
name|DefaultSyncConfig
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
name|basic
operator|.
name|DefaultSyncContext
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
comment|/**  * {@code DefaultSyncHandler} implements an sync handler that synchronizes users and groups from an external identity  * provider with the repository users.  *<p>  * Please refer to {@link DefaultSyncConfigImpl} for configuration options.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
comment|// note that the metatype information is generated from DefaultSyncConfig
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|Service
specifier|public
class|class
name|DefaultSyncHandler
implements|implements
name|SyncHandler
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
name|DefaultSyncHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * internal configuration      */
specifier|private
name|DefaultSyncConfig
name|config
decl_stmt|;
comment|/**      * Default constructor for OSGi      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
name|DefaultSyncHandler
parameter_list|()
block|{     }
comment|/**      * Constructor for non-OSGi cases.      *      * @param config the configuration      */
specifier|public
name|DefaultSyncHandler
parameter_list|(
name|DefaultSyncConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|ConfigurationParameters
name|cfg
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|config
operator|=
name|DefaultSyncConfigImpl
operator|.
name|of
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|config
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|SyncContext
name|createContext
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentityProvider
name|idp
parameter_list|,
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|,
annotation|@
name|Nonnull
name|ValueFactory
name|valueFactory
parameter_list|)
throws|throws
name|SyncException
block|{
return|return
operator|new
name|DefaultSyncContext
argument_list|(
name|config
argument_list|,
name|idp
argument_list|,
name|userManager
argument_list|,
name|valueFactory
argument_list|)
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
specifier|public
name|SyncedIdentity
name|findIdentity
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|,
annotation|@
name|Nonnull
name|String
name|id
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|DefaultSyncContext
operator|.
name|createSyncedIdentity
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|SyncedIdentity
argument_list|>
name|listIdentities
parameter_list|(
annotation|@
name|Nonnull
name|UserManager
name|userManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
specifier|final
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|iter
init|=
name|userManager
operator|.
name|findAuthorizables
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractLazyIterator
argument_list|<
name|SyncedIdentity
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|SyncedIdentity
name|getNext
parameter_list|()
block|{
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|SyncedIdentity
name|id
init|=
name|DefaultSyncContext
operator|.
name|createSyncedIdentity
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
return|return
name|id
return|;
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
literal|"Error while fetching authorizables"
argument_list|,
name|e
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

