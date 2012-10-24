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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|CommitFailedException
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|AbstractLoginModule
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
name|util
operator|.
name|Text
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
comment|/**  * ExternalLoginModule... TODO  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ExternalLoginModule
extends|extends
name|AbstractLoginModule
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
name|ExternalLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_SYNC_MODE
init|=
literal|"syncMode"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SyncMode
name|DEFAULT_SYNC_MODE
init|=
name|SyncMode
operator|.
name|DEFAULT_SYNC
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_SYNC_HANDLER
init|=
literal|"syncHandler"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SYNC_HANDLER
init|=
name|DefaultSyncHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|//------------------------------------------------< ExternalLoginModule>---
comment|/**      * TODO      *      * @return      */
specifier|protected
specifier|abstract
name|boolean
name|loginSucceeded
parameter_list|()
function_decl|;
comment|/**      * TODO      *      * @return      */
specifier|protected
specifier|abstract
name|ExternalUser
name|getExternalUser
parameter_list|()
function_decl|;
comment|/**      * TODO      *      * @return      * @throws SyncException      */
specifier|protected
name|SyncHandler
name|getSyncHandler
parameter_list|()
throws|throws
name|SyncException
block|{
name|String
name|shClass
init|=
name|options
operator|.
name|getConfigValue
argument_list|(
name|PARAM_SYNC_HANDLER
argument_list|,
name|DEFAULT_SYNC_HANDLER
argument_list|)
decl_stmt|;
name|Object
name|syncHandler
decl_stmt|;
try|try
block|{
comment|// FIXME this will create problems within OSGi environment
name|syncHandler
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|shClass
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SyncException
argument_list|(
literal|"Error while getting SyncHandler:"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|syncHandler
operator|instanceof
name|SyncHandler
condition|)
block|{
return|return
operator|(
name|SyncHandler
operator|)
name|syncHandler
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SyncException
argument_list|(
literal|"Invalid SyncHandler class configured: "
operator|+
name|syncHandler
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|//------------------------------------------------< AbstractLoginModule>---
comment|/**      * Default implementation of the {@link #getSupportedCredentials()} method      * that only lists {@link SimpleCredentials} as supported. Subclasses that      * wish to support other or additional credential implementations should      * override this method.      *      * @return An immutable set containing only the {@link SimpleCredentials} class.      */
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|Class
argument_list|>
name|getSupportedCredentials
parameter_list|()
block|{
name|Class
name|scClass
init|=
name|SimpleCredentials
operator|.
name|class
decl_stmt|;
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|scClass
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------< LoginModule>---
comment|/**      * TODO      *      * @return      * @throws LoginException      */
annotation|@
name|Override
specifier|public
name|boolean
name|commit
parameter_list|()
throws|throws
name|LoginException
block|{
if|if
condition|(
operator|!
name|loginSucceeded
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|SyncHandler
name|handler
init|=
name|getSyncHandler
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|getRoot
argument_list|()
decl_stmt|;
name|String
name|smValue
init|=
name|options
operator|.
name|getConfigValue
argument_list|(
name|PARAM_SYNC_MODE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SyncMode
name|syncMode
decl_stmt|;
if|if
condition|(
name|smValue
operator|==
literal|null
condition|)
block|{
name|syncMode
operator|=
name|DEFAULT_SYNC_MODE
expr_stmt|;
block|}
else|else
block|{
name|syncMode
operator|=
name|SyncMode
operator|.
name|fromStrings
argument_list|(
name|Text
operator|.
name|explode
argument_list|(
name|smValue
argument_list|,
literal|','
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|handler
operator|.
name|initialize
argument_list|(
name|getUserManager
argument_list|()
argument_list|,
name|root
argument_list|,
name|syncMode
argument_list|,
name|options
argument_list|)
condition|)
block|{
name|handler
operator|.
name|sync
argument_list|(
name|getExternalUser
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to initialize sync handler."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SyncException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"User synchronization failed: "
operator|+
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|LoginException
argument_list|(
literal|"User synchronization failed: "
operator|+
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

