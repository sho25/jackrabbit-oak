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
name|token
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|AccessDeniedException
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
name|JcrConstants
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
name|AbstractSecurityTest
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
name|plugins
operator|.
name|identifier
operator|.
name|IdentifierManager
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
name|token
operator|.
name|TokenInfo
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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * AbstractTokenTest...  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTokenTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|TokenConstants
block|{
name|TokenProviderImpl
name|tokenProvider
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|root
operator|=
name|adminSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|tokenProvider
operator|=
operator|new
name|TokenProviderImpl
argument_list|(
name|root
argument_list|,
name|getTokenConfig
argument_list|()
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
name|ConfigurationParameters
name|getTokenConfig
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|CheckForNull
name|Tree
name|getTokenTree
parameter_list|(
annotation|@
name|Nonnull
name|TokenInfo
name|info
parameter_list|)
block|{
name|String
name|token
init|=
name|info
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
name|token
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
decl_stmt|;
name|String
name|nodeId
init|=
operator|(
name|pos
operator|==
operator|-
literal|1
operator|)
condition|?
name|token
else|:
name|token
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
operator|new
name|IdentifierManager
argument_list|(
name|root
argument_list|)
operator|.
name|getTree
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
name|Tree
name|createTokenTree
parameter_list|(
annotation|@
name|Nonnull
name|TokenInfo
name|base
parameter_list|,
annotation|@
name|Nonnull
name|NodeUtil
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|ntName
parameter_list|)
throws|throws
name|AccessDeniedException
block|{
name|Tree
name|tokenTree
init|=
name|getTokenTree
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|parent
operator|.
name|addChild
argument_list|(
literal|"token"
argument_list|,
name|ntName
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|tokenTree
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|tokenTree
operator|.
name|getProperty
argument_list|(
name|TOKEN_ATTRIBUTE_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|tokenTree
operator|.
name|getProperty
argument_list|(
name|TOKEN_ATTRIBUTE_EXPIRY
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|tree
return|;
block|}
block|}
end_class

end_unit

