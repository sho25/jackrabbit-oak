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
name|authentication
operator|.
name|ldap
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
name|HashMap
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
name|Credentials
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
name|Subject
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
name|callback
operator|.
name|CallbackHandler
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
name|AuthInfo
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
name|AuthInfoImpl
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
name|ExternalLoginModule
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
specifier|public
specifier|final
class|class
name|LdapLoginModule
extends|extends
name|ExternalLoginModule
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
name|LdapLoginModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Credentials
name|credentials
decl_stmt|;
specifier|private
name|LdapUser
name|ldapUser
decl_stmt|;
specifier|private
name|boolean
name|success
decl_stmt|;
specifier|private
name|LdapSearch
name|search
decl_stmt|;
comment|//--------------------------------------------------------< LoginModule>---
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|sharedState
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|options
parameter_list|)
block|{
name|super
operator|.
name|initialize
argument_list|(
name|subject
argument_list|,
name|callbackHandler
argument_list|,
name|sharedState
argument_list|,
name|options
argument_list|)
expr_stmt|;
comment|//TODO
name|search
operator|=
operator|new
name|JndiLdapSearch
argument_list|(
operator|new
name|LdapSettings
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|success
operator|&&
name|super
operator|.
name|commit
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|subject
operator|.
name|isReadOnly
argument_list|()
condition|)
block|{
name|String
name|userId
init|=
name|ldapUser
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
init|=
name|getPrincipals
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|subject
operator|.
name|getPrincipals
argument_list|()
operator|.
name|addAll
argument_list|(
name|principals
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
name|subject
operator|.
name|getPublicCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|createAuthInfo
argument_list|(
name|userId
argument_list|,
name|principals
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Could not add information to read only subject {}"
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|login
parameter_list|()
throws|throws
name|LoginException
block|{
name|ldapUser
operator|=
name|getExternalUser
argument_list|()
expr_stmt|;
if|if
condition|(
name|ldapUser
operator|!=
literal|null
operator|&&
name|search
operator|.
name|findUser
argument_list|(
name|ldapUser
argument_list|)
condition|)
block|{
name|search
operator|.
name|authenticate
argument_list|(
name|ldapUser
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
comment|//------------------------------------------------< AbstractLoginModule>---
annotation|@
name|Override
specifier|protected
name|void
name|clearState
parameter_list|()
block|{
name|super
operator|.
name|clearState
argument_list|()
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
name|credentials
operator|=
literal|null
expr_stmt|;
name|ldapUser
operator|=
literal|null
expr_stmt|;
name|search
operator|=
literal|null
expr_stmt|;
block|}
comment|//------------------------------------------------< ExternalLoginModule>---
annotation|@
name|Override
specifier|protected
name|boolean
name|loginSucceeded
parameter_list|()
block|{
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|protected
name|LdapUser
name|getExternalUser
parameter_list|()
block|{
if|if
condition|(
name|ldapUser
operator|==
literal|null
condition|)
block|{
name|credentials
operator|=
name|getCredentials
argument_list|()
expr_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|String
name|uid
init|=
operator|(
operator|(
name|SimpleCredentials
operator|)
name|credentials
operator|)
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|char
index|[]
name|pwd
init|=
operator|(
operator|(
name|SimpleCredentials
operator|)
name|credentials
operator|)
operator|.
name|getPassword
argument_list|()
decl_stmt|;
return|return
operator|new
name|LdapUser
argument_list|(
name|uid
argument_list|,
operator|new
name|String
argument_list|(
name|pwd
argument_list|)
argument_list|,
name|search
argument_list|)
return|;
block|}
block|}
return|return
name|ldapUser
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|AuthInfo
name|createAuthInfo
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|credentials
decl_stmt|;
for|for
control|(
name|String
name|attrName
range|:
name|sc
operator|.
name|getAttributeNames
argument_list|()
control|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|attrName
argument_list|,
name|sc
operator|.
name|getAttribute
argument_list|(
name|attrName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|AuthInfoImpl
argument_list|(
name|userId
argument_list|,
name|attributes
argument_list|,
name|principals
argument_list|)
return|;
block|}
block|}
end_class

end_unit

